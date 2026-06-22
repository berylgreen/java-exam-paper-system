import json
import os

JSON_PATH = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"
BASE_DIR = "/home/cc/server/java-exam-paper-system"

with open(JSON_PATH, "r", encoding="utf-8") as f:
    data = json.load(f)

count = 0
for q in data:
    proj_path = q.get("projectPath")
    if proj_path:
        readme_path = os.path.join(BASE_DIR, proj_path, "README.md")
        if os.path.exists(readme_path):
            with open(readme_path, "r", encoding="utf-8") as rf:
                content = rf.read()
            
            # strip out the first line if it's a markdown title
            if content.startswith("# "):
                lines = content.split("\n", 1)
                if len(lines) > 1:
                    content = lines[1]
                else:
                    content = ""
            
            # remove leading/trailing whitespaces
            content = content.strip()
            
            if q["content"] != content:
                q["content"] = content
                count += 1

with open(JSON_PATH, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"Successfully synced {count} questions to questions.json")
