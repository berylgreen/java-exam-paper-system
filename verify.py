import os
import glob

projects_dir = "/home/cc/server/java-exam-paper-system/projects"

# Let's find projects where Main.java is NOT rewritten
# i.e., it doesn't have "TODO: 请在此编写测试代码"

for project in os.listdir(projects_dir):
    project_path = os.path.join(projects_dir, project)
    main_files = glob.glob(os.path.join(project_path, "src", "**", "Main.java"), recursive=True)
    
    for main_file in main_files:
        with open(main_file, "r", encoding="utf-8") as f:
            content = f.read()
        
        if "TODO: 请在此编写测试代码" not in content:
            # We didn't rewrite this one. Let's see if its README has any test writing requirement
            readme_path = os.path.join(project_path, "README.md")
            if os.path.isfile(readme_path):
                with open(readme_path, "r", encoding="utf-8") as f:
                    readme_content = f.read()
                
                # print a snippet if it contains '测试'
                import re
                matches = re.findall(r'[^.\n]*测试[^.\n]*', readme_content)
                if matches:
                    print(f"--- {project} ---")
                    for match in set(matches):
                        print(match.strip())
