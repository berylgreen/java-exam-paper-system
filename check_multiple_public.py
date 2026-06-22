import json
import re

path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"
with open(path, "r", encoding="utf-8") as f:
    questions = json.load(f)

for q in questions:
    if q.get("type") == "PROGRAMMING" and q.get("answerProjectPath"):
        ans = q.get("answer", "")
        # Remove markdown java codeblock markers if any
        ans = re.sub(r"^```java\s*", "", ans)
        ans = re.sub(r"```\s*$", "", ans)
        
        matches = re.findall(r"public\s+(?:abstract\s+)?(?:final\s+)?(?:class|interface)\s+(\w+)", ans)
        if len(matches) > 1:
            print(f"{q['answerProjectPath']}: MULTIPLE PUBLIC: {matches}")
