import json
import os
import re
import glob

path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"
base_dir = "/home/cc/server/java-exam-paper-system"

with open(path, "r", encoding="utf-8") as f:
    questions = json.load(f)

for q in questions:
    if q.get("type") == "PROGRAMMING" and q.get("answerProjectPath") == "project-answer/payment-system-answer":
        ans_proj = os.path.join(base_dir, q["answerProjectPath"])
        src_dir = os.path.join(ans_proj, "src")
        
        java_files = glob.glob(os.path.join(src_dir, "**", "*.java"), recursive=True)
        pkg_name = ""
        for line in open(java_files[0], "r", encoding="utf-8"):
            m = re.match(r"^\s*package\s+([^;]+);", line)
            if m:
                pkg_name = m.group(1).strip()
                break
                
        ans = q.get("answer", "")
        ans = re.sub(r"^```java\s*", "", ans)
        ans = re.sub(r"```\s*$", "", ans)
        
        imports = re.findall(r"^\s*import\s+[^;]+;", ans, re.MULTILINE)
        ans_no_imports = re.sub(r"^\s*import\s+[^;]+;", "", ans, flags=re.MULTILINE).strip()
        
        pattern = re.compile(r"^ *public\s+(?:abstract\s+)?(?:final\s+)?(?:class|interface|enum)\s+(\w+)", re.MULTILINE)
        matches = list(pattern.finditer(ans_no_imports))
        
        chunks = []
        if not matches:
            chunks.append(("Main", ans_no_imports))
        else:
            first_pos = matches[0].start()
            prelude = ans_no_imports[:first_pos]
            
            for i in range(len(matches)):
                start = matches[i].start()
                end = matches[i+1].start() if i + 1 < len(matches) else len(ans_no_imports)
                name = matches[i].group(1)
                chunk_code = ans_no_imports[start:end]
                if i == 0:
                    chunk_code = prelude + "\n" + chunk_code
                chunks.append((name, chunk_code))
                
        print(f"Project: {q['answerProjectPath']}")
        for name, code in chunks:
            print(f"--- File: {name}.java ---")
            if pkg_name: print(f"package {pkg_name};\n")
            if imports: print("\n".join(imports) + "\n")
            print(code.strip()[:100] + "...\n")
