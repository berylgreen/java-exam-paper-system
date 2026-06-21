import os
import json

base_dir = "/home/cc/server/java-exam-paper-system/projects"
json_path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"

with open(json_path, 'r', encoding='utf-8') as f:
    questions = json.load(f)

for q in questions:
    proj = q.get('projectPath')
    if proj and "-multithreading-" in proj:
        proj_dir = os.path.join("/home/cc/server/java-exam-paper-system", proj)
        content = q['content']
        # Find shared variable name in content: "共享数据为xxx yyy"
        print(f"Project: {proj}")
        print(f"Content contains '共享数据': {content[content.find('共享数据'):content.find('。', content.find('共享数据'))]}")
