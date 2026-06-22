import json
import os
import re
import glob

path = "/home/cc/server/java-exam-paper-system/backend/src/main/resources/questions.json"
base_dir = "/home/cc/server/java-exam-paper-system"

with open(path, "r", encoding="utf-8") as f:
    questions = json.load(f)

count = 0
for q in questions:
    if q.get("type") == "PROGRAMMING" and q.get("answerProjectPath"):
        ans_proj = os.path.join(base_dir, q["answerProjectPath"])
        src_dir = os.path.join(ans_proj, "src")
        
        if not os.path.exists(src_dir):
            continue
            
        java_files = glob.glob(os.path.join(src_dir, "**", "*.java"), recursive=True)
        if not java_files:
            continue
            
        pkg_name = ""
        for line in open(java_files[0], "r", encoding="utf-8"):
            m = re.match(r"^\s*package\s+([^;]+);", line)
            if m:
                pkg_name = m.group(1).strip()
                break
                
        pkg_dir = os.path.dirname(java_files[0])
        
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
            prelude = ans_no_imports[:first_pos].strip()
            
            for i in range(len(matches)):
                start = matches[i].start()
                end = matches[i+1].start() if i + 1 < len(matches) else len(ans_no_imports)
                name = matches[i].group(1)
                chunk_code = ans_no_imports[start:end].strip()
                if i == 0 and prelude:
                    chunk_code = prelude + "\n\n" + chunk_code
                chunks.append((name, chunk_code))
                
        if chunks:
            for jf in java_files:
                os.remove(jf)
                
            for name, code in chunks:
                file_path = os.path.join(pkg_dir, f"{name}.java")
                with open(file_path, "w", encoding="utf-8") as out:
                    if pkg_name: out.write(f"package {pkg_name};\n\n")
                    if imports: out.write("\n".join(imports) + "\n\n")
                    out.write(code.strip() + "\n")
            count += 1

print(f"Synced {count} answer projects src directories.")
