import json
import re

file_path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"

with open(file_path, "r", encoding="utf-8") as f:
    raw_text = f.read()

data = json.loads(raw_text)

changes = 0
for q in data:
    if q.get("type") == "PROGRAMMING":
        content = q.get("content", "")
        # Replace numbers like "1. ", "1.", " 1." at the beginning of lines
        new_content = re.sub(r'(?m)^(\s*)(\d+)\.\s*', r'\1(\2) ', content)
        if new_content != content:
            old_str = json.dumps(content, ensure_ascii=False)
            new_str = json.dumps(new_content, ensure_ascii=False)
            if old_str in raw_text:
                raw_text = raw_text.replace(old_str, new_str)
                changes += 1
            else:
                # Try with ensure_ascii=True just in case
                old_str2 = json.dumps(content, ensure_ascii=True)
                new_str2 = json.dumps(new_content, ensure_ascii=True)
                if old_str2 in raw_text:
                    raw_text = raw_text.replace(old_str2, new_str2)
                    changes += 1
                else:
                    print(f"Could not find exact match for: {old_str[:50]}...")

if changes > 0:
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(raw_text)
    print(f"Successfully updated {changes} programming questions.")
else:
    print("No changes were made.")
