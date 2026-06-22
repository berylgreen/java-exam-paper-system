import json

with open("backend/src/main/resources/questions.json", "r", encoding="utf-8") as f:
    original = f.read()

data = json.loads(original)
new_json = json.dumps(data, ensure_ascii=False, indent=2, separators=(',', ' : '))

print("Length original:", len(original))
print("Length new:", len(new_json))

# Just print the first 200 chars of new to see if it matches
print(new_json[:200])
