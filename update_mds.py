import os
import re

base_path = "/home/cc/server/java-exam-paper-system"
directories = ["projects", "project-answer"]
changes = 0

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
                
                # Replace numbering like "1. ", "2. " with "(1) ", "(2) "
                new_text = re.sub(r'(?m)^(\s*)(\d+)\.\s*', r'\1(\2) ', text)
                
                if new_text != text:
                    with open(path, "w", encoding="utf-8") as file:
                        file.write(new_text)
                    changes += 1

print(f"Successfully updated {changes} markdown files.")
