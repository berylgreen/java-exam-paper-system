import os

base_dir = "."
for d in os.listdir(base_dir):
    dir_path = os.path.join(base_dir, d)
    if os.path.isdir(dir_path) and d.endswith("-question"):
        readme_path = os.path.join(dir_path, "README.md")
        if not os.path.exists(readme_path):
            continue
            
        with open(readme_path, "r", encoding="utf-8") as f:
            content = f.read()
            
        # We know we appended "\n### 测试数据示例\n请在 `Main` 类中使用以下"
        # Let's find the index of "\n### 测试数据示例\n请在 `Main` 类中使用以下"
        # Wait, some had "### 测试数据示例" already (like smarthome-hashmap-question).
        # Wait, the script skipped if "### 测试数据示例" was already in content.
        # So we just need to remove the added text, which is exactly the last occurrence if there are multiple, or just the whole block.
        # Actually, let's just restore files to their previous state.
        # Wait, I didn't create a backup.
        # Let's just find the exact string we appended and remove it.
        # The append string always started with "\n### 测试数据示例\n"
        # Let's split by "\n### 测试数据示例" and keep only the first part, BUT ONLY IF we were the ones who added it.
        # Wait, smarthome-hashmap-question already had it! And my script skipped it!
        # "if '### 测试数据示例' in content: continue" -> So we didn't touch smarthome-hashmap-question.
        # Thus, ALL other files that have "### 测试数据示例" got it from my script!
        
        if "### 测试数据示例" in content and d != "smarthome-hashmap-question":
            idx = content.find("\n### 测试数据示例")
            if idx != -1:
                content = content[:idx]
                with open(readme_path, "w", encoding="utf-8") as f:
                    f.write(content)
                print(f"Fixed {d}")
