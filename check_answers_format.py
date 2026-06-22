import json
import re

path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"
with open(path, "r", encoding="utf-8") as f:
    questions = json.load(f)

for q in questions:
    if q.get("type") == "PROGRAMMING" and q.get("answerProjectPath"):
        ans = q.get("answer", "")
        m = re.search(r"public\s+class\s+(\w+)", ans)
        if m:
            print(f"{q['answerProjectPath']}: public class {m.group(1)}")
        else:
            print(f"{q['answerProjectPath']}: NO PUBLIC CLASS FOUND")
