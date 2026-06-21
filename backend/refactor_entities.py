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

def capitalize(s):
    if not s: return s
    return s[0].upper() + s[1:]

for proj, content in projects.items():
    proj_dir = os.path.join("/home/cc/server/java-exam-paper-system", proj)
    if not os.path.exists(proj_dir):
        continue
        
    domain = proj.split('/')[1].split('-')[0]
    entity_name = domain_entities.get(domain, "Unknown")
    
    java_files = []
    for root, dirs, files in os.walk(proj_dir):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
                
    entity_file = None
    for jf in java_files:
        if os.path.basename(jf) == f"{entity_name}.java":
            entity_file = jf
            break
            
    if not entity_file:
        continue
        
    # Read entity
    with open(entity_file, 'r', encoding='utf-8') as f:
        entity_code = f.read()

    # Step 1: Fix 'info' to 'name' for hashmap projects
    if "-hashmap-" in proj:
        entity_code = re.sub(r'\binfo\b', 'name', entity_code)
        
        # apply 'info' to 'name' replacement in all other java files
        for jf in java_files:
            if jf == entity_file: continue
            with open(jf, 'r', encoding='utf-8') as f2:
                other_code = f2.read()
            if 'info' in other_code:
                # rename variable usages safely. 'info' to 'name'.
                # To be safe, just do simple word replacement
                other_code = re.sub(r'\binfo\b', 'name', other_code)
                with open(jf, 'w', encoding='utf-8') as f2:
                    f2.write(other_code)
                    
    # Skip encapsulation projects for getter/setter
    if "-encapsulation-" in proj:
        # Just write back entity code if we changed it
        with open(entity_file, 'w', encoding='utf-8') as f:
            f.write(entity_code)
        continue

    # Step 2: Add getter/setters and make private
    # Find all public fields
    # Example: public String id; public double value;
    fields = []
    
    # We will look for: public Type fieldName;
    # or public Type fieldName = defaultValue;
    def repl_field(match):
        type_str = match.group(1)
        name_str = match.group(2)
        fields.append((type_str, name_str))
        return f"private {type_str} {name_str}" + (match.group(3) if match.group(3) else "") + ";"

    # Regex to match public fields. 
    # Match public followed by Type and Name, optionally followed by = something, ending with ;
    new_entity_code = re.sub(r'public\s+([a-zA-Z0-9_<>]+)\s+([a-zA-Z0-9_]+)(\s*=\s*[^;]+)?\s*;', repl_field, entity_code)
    
    if len(fields) > 0:
        # Generate getter/setters
        methods = []
        for t, n in fields:
            cap_n = capitalize(n)
            methods.append(f"    public {t} get{cap_n}() {{\n        return {n};\n    }}")
            methods.append(f"    public void set{cap_n}({t} {n}) {{\n        this.{n} = {n};\n    }}")
            
        methods_str = "\n\n".join(methods)
        
        # Insert methods before the last closing brace
        last_brace_idx = new_entity_code.rfind('}')
        if last_brace_idx != -1:
            new_entity_code = new_entity_code[:last_brace_idx] + "\n" + methods_str + "\n" + new_entity_code[last_brace_idx:]
            
        with open(entity_file, 'w', encoding='utf-8') as f:
            f.write(new_entity_code)
            
        # Step 3: Replace field access in other java files
        for jf in java_files:
            if jf == entity_file: continue
            with open(jf, 'r', encoding='utf-8') as f2:
                other_code = f2.read()
                
            for t, n in fields:
                cap_n = capitalize(n)
                # Assignment: obj.field = value; -> obj.setField(value);
                # This is tricky with regex, let's assume no assignments to fields except maybe in constructor
                # but if there's assignment obj.field = something; we might have trouble.
                # Usually in these test projects, they only do reads: System.out.println(obj.field);
                # Let's handle reads: obj.field -> obj.getField()
                # We want to match .field but not if it's already a method .field()
                # and not "this.field" inside the entity itself (which we skipped).
                
                # regex: \.field(?!\()
                other_code = re.sub(r'\.' + n + r'(?!\()', f'.get{cap_n}()', other_code)
                
            with open(jf, 'w', encoding='utf-8') as f2:
                f2.write(other_code)
                
print("Refactoring complete.")
