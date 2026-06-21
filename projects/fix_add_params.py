import os
import glob

base_dir = "/home/cc/server/java-exam-paper-system/projects"

# Projects and their entity names
domains = {
    'bank': 'Account',
    'ecommerce': 'Order',
    'hospital': 'Patient',
    'hotel': 'Room',
    'library': 'Book',
    'logistics': 'Package',
    'rental': 'Vehicle',
    'restaurant': 'Dish',
    'school': 'Student',
    'smarthome': 'Device'
}

for domain, entity in domains.items():
    proj_dir = os.path.join(base_dir, f"{domain}-hashmap-question", "src", "com", "exam", domain)
    if not os.path.exists(proj_dir):
        continue

    # 1. Update Storage.java / OrderManager.java
    # We will just look for any java file that is NOT Main.java or Entity.java
    java_files = glob.glob(os.path.join(proj_dir, "*.java"))
    for jf in java_files:
        basename = os.path.basename(jf)
        if basename not in ["Main.java", f"{entity}.java"]:
            # It's the Storage / Manager file
            with open(jf, "r") as f:
                content = f.read()
            
            # Replace `public void add(String id, {entity} item)` with `public void add({entity} item)`
            old_add_sig = f"public void add(String id, {entity} item)"
            new_add_sig = f"public void add({entity} item)"
            
            if old_add_sig in content:
                content = content.replace(old_add_sig, new_add_sig)
                with open(jf, "w") as f:
                    f.write(content)
                print(f"Updated {basename} in {domain}")

    # 2. Update Main.java
    main_path = os.path.join(proj_dir, "Main.java")
    if os.path.exists(main_path):
        with open(main_path, "r") as f:
            content = f.read()
            
        # The add calls in Main.java look like: storage.add("001", new Device("001", "Info 1"));
        # We want to change them to: storage.add(new Device("001", "Info 1"));
        # Or manager.add("001", new Order("001", "Info 1"));
        import re
        # Find all `.add("xxx", new Entity(`
        # Replace with `.add(new Entity(`
        # re.sub(r'\.add\(\s*"[^"]+"\s*,\s*(new\s+[A-Za-z0-9_]+\()', r'.add(\1', content)
        new_content = re.sub(r'\.add\(\s*"[^"]+"\s*,\s*(new\s+[A-Za-z0-9_]+\()', r'.add(\1', content)
        
        if content != new_content:
            with open(main_path, "w") as f:
                f.write(new_content)
            print(f"Updated Main.java in {domain}")

