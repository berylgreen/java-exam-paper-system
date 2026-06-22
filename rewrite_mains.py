import os
import re
import glob

projects_dir = "/home/cc/server/java-exam-paper-system/projects"

# Regular expressions to detect if the question requires writing a test class
# e.g., "编写 Main 测试类", "编写一个多线程测试程序", "编写测试类"
regexes = [
    r'编写.*测试类',
    r'编写.*测试程序',
    r'编写.*Main'
]

modified_count = 0

for project in os.listdir(projects_dir):
    project_path = os.path.join(projects_dir, project)
    readme_path = os.path.join(project_path, "README.md")
    
    if os.path.isdir(project_path) and os.path.isfile(readme_path):
        with open(readme_path, "r", encoding="utf-8") as f:
            content = f.read()
            
        requires_test_class = any(re.search(regex, content) for regex in regexes)
        
        if requires_test_class:
            # Find the Main.java in this project
            main_files = glob.glob(os.path.join(project_path, "src", "**", "Main.java"), recursive=True)
            for main_file in main_files:
                # Read the package declaration
                with open(main_file, "r", encoding="utf-8") as f:
                    main_content = f.read()
                
                pkg_match = re.search(r'package\s+([^;]+);', main_content)
                pkg_line = f"package {pkg_match.group(1)};\n\n" if pkg_match else ""
                
                new_main_content = pkg_line + "public class Main {\n    public static void main(String[] args) {\n        // TODO: 请在此编写测试代码\n    }\n}\n"
                
                with open(main_file, "w", encoding="utf-8") as f:
                    f.write(new_main_content)
                print(f"Rewrote: {main_file}")
                modified_count += 1

print(f"Total modified Main.java files: {modified_count}")
