import os
import glob
import re

projects_dir = "/home/cc/server/java-exam-paper-system/projects"
modified_count = 0

main_files = glob.glob(os.path.join(projects_dir, "*", "src", "**", "Main.java"), recursive=True)

for main_file in main_files:
    with open(main_file, "r", encoding="utf-8") as f:
        content = f.read()
    
    # Check if it has the TODO
    if "// TODO: 请在此编写测试代码" in content and "System.out.println" not in content:
        # Extract module from package name
        pkg_match = re.search(r'package\s+com\.exam\.([^;]+);', content)
        module_name = pkg_match.group(1) if pkg_match else "模块"
        
        sysout_line = f'        System.out.println("启动 {module_name} 模块测试...");\n'
        
        # Insert sysout before the TODO
        new_content = content.replace('        // TODO: 请在此编写测试代码', sysout_line + '        // TODO: 请在此编写测试代码')
        
        with open(main_file, "w", encoding="utf-8") as f:
            f.write(new_content)
        
        print(f"Added sysout to {main_file}")
        modified_count += 1

print(f"Total files modified: {modified_count}")
