import os

def check():
    projects_dir = "/home/cc/server/java-exam-paper-system/projects"
    for proj in os.listdir(projects_dir):
        proj_path = os.path.join(projects_dir, proj)
        if not os.path.isdir(proj_path): continue
        
        src_path = os.path.join(proj_path, "src")
        if not os.path.exists(src_path): continue
        
        # Check if there's any file in src that seems to be a solution
        java_files = []
        for root, dirs, files in os.walk(src_path):
            for f in files:
                if f.endswith(".java"):
                    java_files.append(os.path.join(root, f))
                    
        for jf in java_files:
            with open(jf, 'r') as f:
                content = f.read()
                
            # If it's a polymorphism question, solution usually has 'implements Shape' or 'abstract class'
            # but wait, the question description will have the keywords.
            
            # Print files that have unusual solution-like keywords but no TODO/FIXME
            if "TODO" not in content and "FIXME" not in content and "原始设计" not in content:
                # Could be normal (like ShapeCalculator, IntegerBox), but let's check further
                print(jf)

if __name__ == "__main__":
    check()
