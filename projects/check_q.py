import json

with open("/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json") as f:
    data = json.load(f)

count = 0
for q in data:
    if q.get("projectPath") and "projects/" in q["projectPath"]:
        print(f"Path: {q['projectPath']}")
        print(f"Content: {q['content'][:100]}...")
        count += 1
        if count >= 3:
            break
