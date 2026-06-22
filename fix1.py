import os

path = "/home/cc/server/java-exam-paper-system/project-answer/smarthome-hashmap-answer/src/com/exam/smarthome/Main.java"
with open(path, "r") as f:
    code = f.read()

code = code.replace('item != null ? item.getName() : "null"', 'item != null ? item.toString() : "null"')

with open(path, "w") as f:
    f.write(code)

readme_path = "/home/cc/server/java-exam-paper-system/project-answer/smarthome-hashmap-answer/README.md"
with open(readme_path, "r") as f:
    r = f.read()
r = r.replace('item != null ? item.getName() : "null"', 'item != null ? item.toString() : "null"')
with open(readme_path, "w") as f:
    f.write(r)

print("Fixed smarthome-hashmap-answer")
