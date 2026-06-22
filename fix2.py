import os
import glob

proj = "/home/cc/server/java-exam-paper-system/project-answer/generic-box-answer"
os.rename(os.path.join(proj, "src/com/exam/generic/Object.java"), os.path.join(proj, "src/com/exam/generic/MyObject.java"))

for path in glob.glob(proj + "/**/*.java", recursive=True):
    with open(path, "r") as f:
        code = f.read()
    code = code.replace("Object", "MyObject")
    with open(path, "w") as f:
        f.write(code)

readme_path = os.path.join(proj, "README.md")
with open(readme_path, "r") as f:
    r = f.read()
r = r.replace("Object.java", "MyObject.java")
r = r.replace("class Object", "class MyObject")
r = r.replace("Object(", "MyObject(")
r = r.replace("Comparable<Object>", "Comparable<MyObject>")
r = r.replace("(Object)", "(MyObject)")
r = r.replace("Object that", "MyObject that")
r = r.replace("Object other", "MyObject other")
r = r.replace("Set<Object>", "Set<MyObject>")
r = r.replace("List<Object>", "List<MyObject>")
r = r.replace("new Object", "new MyObject")
r = r.replace("Object item", "MyObject item")
with open(readme_path, "w") as f:
    f.write(r)

print("Fixed generic-box-answer")
