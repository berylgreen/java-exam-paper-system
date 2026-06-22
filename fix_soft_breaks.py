import os
import json
import re

base_path = "/home/cc/server/java-exam-paper-system"
json_path = os.path.join(base_path, "backend/src/main/resources/questions.json")

# 1. Update questions.json
with open(json_path, "r", encoding="utf-8") as f:
    data = json.loads(f.read())

json_changes = 0
for q in data:
    if q.get("type") == "PROGRAMMING":
        content = q.get("content", "")
        # Replace any remaining 1. 2. 3.
        new_content = re.sub(r'(?m)^(\s*)(\d+)\.\s*', r'\1(\2) ', content)
        # Add two spaces before single newlines preceding (1), (2), etc.
        new_content = re.sub(r'([^\n \t])[ \t]*\n(\s*\(\d+\)[ \t]*)', r'\1  \n\2', new_content)
        
        if new_content != content:
            q["content"] = new_content
            json_changes += 1

if json_changes > 0:
    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2, separators=(',', ' : '))
print(f"Updated {json_changes} questions in questions.json")

# 2. Update .md files
directories = ["projects", "project-answer"]
md_changes = 0

for d in directories:
    dir_path = os.path.join(base_path, d)
    if not os.path.exists(dir_path):
        continue
    for root, dirs, files in os.walk(dir_path):
        for f in files:
            if f.endswith(".md"):
                path = os.path.join(root, f)
                with open(path, "r", encoding="utf-8") as file:
                    text = file.read()
                
                # Replace numbering
                new_text = re.sub(r'(?m)^(\s*)(\d+)\.\s*', r'\1(\2) ', text)
                # Add two spaces before single newlines
                new_text = re.sub(r'([^\n \t])[ \t]*\n(\s*\(\d+\)[ \t]*)', r'\1  \n\2', new_text)
                
                if new_text != text:
                    with open(path, "w", encoding="utf-8") as file:
                        file.write(new_text)
                    md_changes += 1

print(f"Updated {md_changes} markdown files.")
