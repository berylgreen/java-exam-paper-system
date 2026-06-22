import os
import re
import glob

projects_dir = "/home/cc/server/java-exam-paper-system/projects"
matches = []

for project in os.listdir(projects_dir):
    readme_path = os.path.join(projects_dir, project, "README.md")
    if os.path.isfile(readme_path):
        with open(readme_path, "r", encoding="utf-8") as f:
            content = f.read()
            # Look for indicators that the student needs to write the test class
            if re.search(r'编写.*测试', content) or re.search(r'编写.*Main', content):
                matches.append(project)

print(f"Found {len(matches)} projects requiring student to write tests:")
for m in matches:
    print(m)
