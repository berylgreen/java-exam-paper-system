import os
import re
import subprocess

base_dir = "/home/cc/server/java-exam-paper-system/project-answer"
success_count = 0
fail_count = 0

for proj in os.listdir(base_dir):
    proj_dir = os.path.join(base_dir, proj)
    if not os.path.isdir(proj_dir) or not proj.endswith("-answer"):
        continue
        
    readme_path = os.path.join(proj_dir, "README.md")
    if not os.path.exists(readme_path):
        continue
        
    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()
        
    match = re.search(r'### 预期输出示例\n```(?:text)?\n(.*?)\n```', content, re.DOTALL)
    if not match:
        continue
        
    expected_output = match.group(1).strip()
    
    src_dir = os.path.join(proj_dir, "src")
    
    java_files = []
    for root, dirs, files in os.walk(src_dir):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
                
    if not java_files:
        continue
        
    try:
        compile_cmd = ["javac", "-d", "/tmp/classes_" + proj] + java_files
        os.makedirs("/tmp/classes_" + proj, exist_ok=True)
        res = subprocess.run(compile_cmd, capture_output=True, text=True)
        if res.returncode != 0:
            print(f"COMPILE ERROR in {proj}: {res.stderr}")
            fail_count += 1
            continue
            
        domain = proj.split('-')[0]
        run_cmd = ["java", "-cp", "/tmp/classes_" + proj, f"com.exam.{domain}.Main"]
        res = subprocess.run(run_cmd, capture_output=True, text=True)
        
        actual_output = res.stdout.strip()
        actual_output = actual_output.replace("--- 执行测试用例 ---\n", "").strip()
        
        if actual_output == expected_output:
            success_count += 1
        else:
            print(f"FAIL in {proj}:")
            print("EXPECTED:\n" + expected_output)
            print("ACTUAL:\n" + actual_output)
            print("-" * 40)
            fail_count += 1
    except Exception as e:
        print(f"Exception in {proj}: {e}")
        fail_count += 1

print(f"Verification complete: {success_count} passed, {fail_count} failed")
