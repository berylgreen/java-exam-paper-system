import json

path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"
with open(path, "r", encoding="utf-8") as f:
    questions = json.load(f)

for q in questions:
    if q.get("projectPath") == "projects/smarthome-hashmap-question":
        print("EXPLANATION:")
        print(q.get("explanation"))
        print("\nANSWER:")
        print(q.get("answer"))
        break
