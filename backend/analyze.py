import os
import json
import re

base_dir = "/home/cc/server/java-exam-paper-system/projects"
json_path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"

with open(json_path, 'r', encoding='utf-8') as f:
    questions = json.load(f)

projects = {}
for q in questions:
    if q.get('projectPath'):
        projects[q['projectPath']] = q['content']

domain_entities = {
    'hospital': 'Patient',
    'restaurant': 'Dish',
    'school': 'Student',
    'logistics': 'Package',
    'rental': 'Vehicle',
    'smarthome': 'Device',
    'hotel': 'Room',
    'ecommerce': 'Order',
    'library': 'Book',
    'bank': 'Account'
}

for proj, content in projects.items():
    proj_dir = os.path.join("/home/cc/server/java-exam-paper-system", proj)
    if not os.path.exists(proj_dir):
        continue
        
    domain = proj.split('/')[1].split('-')[0]
    entity_name = domain_entities.get(domain, "Unknown")
    
    # find entity file
    entity_file = None
    for root, dirs, files in os.walk(proj_dir):
        for file in files:
            if file == f"{entity_name}.java":
                entity_file = os.path.join(root, file)
                break
                
    if not entity_file:
        continue
        
    with open(entity_file, 'r', encoding='utf-8') as f:
        code = f.read()
        
    has_getter = "get" in code
    has_setter = "set" in code
    has_info = "String info" in code
    has_value = "double value" in code
    
    issues = []
    if "-encapsulation-" not in proj:
        if not has_getter or not has_setter:
            issues.append("Missing getter/setter")
    
    if "-hashmap-" in proj and "编号和" in content and "名称属性" in content:
        if has_info and not "String name" in code:
            issues.append("Has 'info' but description asks for '名称' (name)")

    if "-encapsulation-" in proj:
        if has_getter or has_setter:
            issues.append("Encapsulation project has getter/setter (should not have)")
            
    if issues:
        print(f"Project: {proj}")
        print(f"Entity: {entity_name}")
        print(f"Issues: {', '.join(issues)}")
        print("---")
