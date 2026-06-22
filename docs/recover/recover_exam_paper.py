import json
import urllib.request
import re
import sys
import zipfile
import xml.etree.ElementTree as ET
from difflib import SequenceMatcher

def similar(a, b):
    return SequenceMatcher(None, a, b).ratio()

def clean_text(text):
    return re.sub(r'\W+', '', text)

def extract_text_from_docx(docx_path):
    lines = []
    try:
        with zipfile.ZipFile(docx_path) as z:
            xml_content = z.read('word/document.xml')
        tree = ET.fromstring(xml_content)
        ns = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
        for p in tree.findall('.//w:p', ns):
            texts = [node.text for node in p.findall('.//w:t', ns) if node.text]
            if texts:
                lines.append(''.join(texts))
    except Exception as e:
        print(f"Error reading docx: {e}")
    return lines

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 recover_exam_paper.py <path_to_docx>")
        sys.exit(1)

    docx_path = sys.argv[1]
    lines = extract_text_from_docx(docx_path)
    if not lines:
        print("No text found in docx.")
        sys.exit(1)

    title = lines[0].strip()
    if "试卷" not in title:
        title = "恢复的试卷"

    questions_extracted = []
    current_q = None
    for line in lines:
        line = line.strip()
        if not line: continue
        m = re.match(r'^(\d+)\.\s*\(([\d\.]+)分\)\s*(.*)', line)
        if m:
            if current_q:
                questions_extracted.append(current_q)
            current_q = {
                'score': float(m.group(2)),
                'content': m.group(3),
                'full_text': line
            }
        elif current_q:
            current_q['full_text'] += "\n" + line
            if not current_q['content']:
                current_q['content'] = line
            else:
                current_q['content'] += line

    if current_q:
        questions_extracted.append(current_q)

    print(f"Extracted {len(questions_extracted)} questions from {docx_path}.")

    try:
        req = urllib.request.Request('http://localhost:18080/api/questions?size=2000')
        with urllib.request.urlopen(req) as response:
            db_data = json.loads(response.read().decode())
        db_questions = db_data['content']
    except Exception as e:
        print(f"Failed to fetch questions from database: {e}")
        sys.exit(1)

    print(f"Loaded {len(db_questions)} questions from DB.")

    matched_questions = []
    order = 1

    for q_ext in questions_extracted:
        best_match = None
        best_score = 0
        ext_text = q_ext['content']
        ext_cleaned = clean_text(ext_text)
        
        for db_q in db_questions:
            db_content = db_q['content']
            db_cleaned = clean_text(db_content)
            
            if db_cleaned and db_cleaned in ext_cleaned:
                score = 1.0
            else:
                score = similar(ext_cleaned, db_cleaned)
                truncated_ext = ext_cleaned[:len(db_cleaned)+5]
                score_trunc = similar(truncated_ext, db_cleaned)
                if score_trunc > score:
                    score = score_trunc
                
            if score > best_score:
                best_score = score
                best_match = db_q
                
        if best_score >= 0.5:
            matched_questions.append({
                'questionId': best_match['id'],
                'questionOrder': order,
                'score': int(q_ext['score'])
            })
            order += 1
            print(f"[MATCH {best_score:.2f}] DOC: {ext_text[:30]}... -> DB: {best_match['content'][:30]}...")
        else:
            print(f"[FAIL {best_score:.2f}] DOC: {ext_text[:30]}...")

    payload = {
        "title": title,
        "durationMinutes": 120,
        "description": "基于历史导出的word版本恢复",
        "questions": matched_questions
    }

    try:
        req = urllib.request.Request('http://localhost:18080/api/papers/save-generated', 
                                     data=json.dumps(payload).encode('utf-8'),
                                     headers={'Content-Type': 'application/json'},
                                     method='POST')
        with urllib.request.urlopen(req) as response:
            res = json.loads(response.read().decode())
            print(f"\nSuccess! Saved paper ID: {res.get('id')} with {len(matched_questions)} questions.")
    except Exception as e:
        print(f"\nFailed to save paper: {e}")

if __name__ == '__main__':
    main()
