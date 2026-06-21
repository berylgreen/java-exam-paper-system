import os

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

base_dir = "/home/cc/server/java-exam-paper-system/projects"

storage_template = """package com.exam.{domain};

import java.util.ArrayList;
import java.util.List;

public class Storage {{
    // 原始设计：使用一个 List 存储对象，查询时需要遍历整个列表，效率低
    private List<{entity}> items = new ArrayList<>();
    
    public void add(String id, {entity} item) {{
        items.add(item);
    }}
    
    public {entity} get(String id) {{
        for ({entity} item : items) {{
            if (item.getId().equals(id)) {{
                return item;
            }}
        }}
        return null;
    }}
    
    public void remove(String id) {{
        {entity} target = null;
        for ({entity} item : items) {{
            if (item.getId().equals(id)) {{
                target = item;
                break;
            }}
        }}
        if (target != null) {{
            items.remove(target);
        }}
    }}
}}
"""

for domain, entity in domains.items():
    proj_dir = os.path.join(base_dir, f"{domain}-hashmap-question")
    storage_path = os.path.join(proj_dir, "src", "com", "exam", domain, "Storage.java")
    if os.path.exists(storage_path):
        with open(storage_path, 'w') as f:
            f.write(storage_template.format(domain=domain, entity=entity))
        print(f"Updated {storage_path}")
    else:
        print(f"Not found: {storage_path}")
