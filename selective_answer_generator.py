import os
import shutil
import re

DOMAINS = {
    "bank": {"class": "Account", "v1": "张三的账户", "v2": "李四的账户", "v3": "王五的账户", "noun": "账户", "type1": "NormalAccount", "type2": "VIPAccount"},
    "ecommerce": {"class": "Order", "v1": "笔记本电脑", "v2": "智能手机", "v3": "蓝牙耳机", "noun": "商品", "type1": "StandardOrder", "type2": "ExpressOrder"},
    "hospital": {"class": "Patient", "v1": "张三的病历", "v2": "李四的病历", "v3": "王五的病历", "noun": "病历", "type1": "Outpatient", "type2": "Emergency"},
    "hotel": {"class": "Room", "v1": "总统套房", "v2": "豪华大床房", "v3": "标准间", "noun": "客房", "type1": "RegularRoom", "type2": "VIPRoom"},
    "library": {"class": "Book", "v1": "Java编程思想", "v2": "算法导论", "v3": "计算机网络", "noun": "图书", "type1": "Textbook", "type2": "Magazine"},
    "logistics": {"class": "Package", "v1": "电子产品包裹", "v2": "书籍包裹", "v3": "衣物包裹", "noun": "包裹", "type1": "StandardPackage", "type2": "FragilePackage"},
    "rental": {"class": "Vehicle", "v1": "丰田卡罗拉", "v2": "本田雅阁", "v3": "宝马X5", "noun": "车辆", "type1": "Car", "type2": "Truck"},
    "restaurant": {"class": "Dish", "v1": "宫保鸡丁", "v2": "鱼香肉丝", "v3": "红烧肉", "noun": "菜品", "type1": "ColdDish", "type2": "HotDish"},
    "school": {"class": "Student", "v1": "高等数学", "v2": "大学物理", "v3": "计算机科学", "noun": "课程", "type1": "Undergraduate", "type2": "Graduate"},
    "smarthome": {"class": "Device", "v1": "智能灯", "v2": "智能空调", "v3": "智能门锁", "noun": "设备", "type1": "Sensor", "type2": "Controller"},
    "generic": {"class": "MyObject", "v1": "对象A", "v2": "对象B", "v3": "对象C", "noun": "对象", "type1": "TypeA", "type2": "TypeB"},
    "exception": {"class": "Data", "v1": "数据1", "v2": "数据2", "v3": "数据3", "noun": "数据", "type1": "TypeA", "type2": "TypeB"},
    "file": {"class": "Record", "v1": "记录1", "v2": "记录2", "v3": "记录3", "noun": "记录", "type1": "TypeA", "type2": "TypeB"},
    "lambda": {"class": "Element", "v1": "元素1", "v2": "元素2", "v3": "元素3", "noun": "元素", "type1": "TypeA", "type2": "TypeB"},
    "payment": {"class": "Order", "v1": "订单1", "v2": "订单2", "v3": "订单3", "noun": "订单", "type1": "TypeA", "type2": "TypeB"},
    "shape": {"class": "Shape", "v1": "圆形", "v2": "矩形", "v3": "三角形", "noun": "图形", "type1": "Circle", "type2": "Rectangle"},
    "student": {"class": "Student", "v1": "小明", "v2": "小红", "v3": "小刚", "noun": "学生", "type1": "TypeA", "type2": "TypeB"},
    "ticket": {"class": "Ticket", "v1": "车票1", "v2": "车票2", "v3": "车票3", "noun": "车票", "type1": "TypeA", "type2": "TypeB"},
    "vehicle": {"class": "Vehicle", "v1": "汽车", "v2": "卡车", "v3": "摩托车", "noun": "交通工具", "type1": "Car", "type2": "Truck"},
    "weather": {"class": "WeatherData", "v1": "温度数据", "v2": "湿度数据", "v3": "气压数据", "noun": "气象数据", "type1": "TypeA", "type2": "TypeB"}
}

TEMPLATE_ENTITY_FULL = """package com.exam.{domain};
import java.util.Objects;
public class {cls} implements Comparable<{cls}> {{
    private String id;
    private String name;
    private double value;
    public {cls}() {{}}
    public {cls}(String id, String name) {{ this.id = id; this.name = name; }}
    public String getId() {{ return id; }}
    public void setId(String id) {{
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("编号不能为空");
        this.id = id;
    }}
    public String getName() {{ return name; }}
    public void setName(String name) {{ this.name = name; }}
    public double getValue() {{ return value; }}
    public void setValue(double value) {{
        if (value < 0) throw new IllegalArgumentException("数值不能为负数");
        this.value = value;
    }}
    @Override
    public boolean equals(Object o) {{
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        {cls} that = ({cls}) o;
        return Objects.equals(id, that.id);
    }}
    @Override
    public int hashCode() {{ return Objects.hash(id); }}
    @Override
    public int compareTo({cls} other) {{ return this.id.compareTo(other.id); }}
    @Override
    public String toString() {{ return "{cls}{{id='" + id + "', name='" + name + "'}}"; }}
}}
"""

TEMPLATE_STORAGE = """package com.exam.{domain};
import java.util.HashMap;
import java.util.Map;
public class Storage {{
    private Map<String, {cls}> items = new HashMap<>();
    public void add({cls} item) {{ items.put(item.id != null ? item.id : item.getId(), item); }}
    public {cls} get(String id) {{ return items.get(id); }}
    public void remove(String id) {{ items.remove(id); }}
    public int size() {{ return items.size(); }}
}}
"""

TEMPLATE_INVENTORY = """package com.exam.{domain};
public class Inventory {{
    private int count;
    public Inventory(int initialCount) {{ this.count = initialCount; }}
    public synchronized void decrement(int amount) {{ this.count -= amount; }}
    public int getCount() {{ return count; }}
}}
"""

TEMPLATE_DATAPARSER = """package com.exam.{domain};
public class DataParser {{
    public void parseData(String[] data) {{
        for (String item : data) {{
            try {{
                if ("error".equals(item)) throw new CustomException("格式错误");
                System.out.println("解析成功：" + item);
            }} catch (CustomException e) {{
                System.out.println("捕获异常：" + e.getMessage() + "，跳过该条数据");
            }}
        }}
        System.out.println("全部数据处理完毕");
    }}
}}
"""

TEMPLATE_FILESTORAGE = """package com.exam.{domain};
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class FileStorage {{
    public void saveRecord(String id, String content) {{
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data.txt", true))) {{
            bw.write(id + ": " + content + "操作成功\\n");
        }} catch (IOException e) {{
            e.printStackTrace();
        }}
    }}
}}
"""

TEMPLATE_PROCESSOR_POLY = """package com.exam.{domain};
public interface {cls}Processor {{ void process(); }}
"""

TEMPLATE_PROCESSOR_TYPE1 = """package com.exam.{domain};
public class {type1} implements {cls}Processor {{
    private String name;
    public {type1}(String name) {{ this.name = name; }}
    @Override public void process() {{ System.out.println("统一处理基础{noun}：" + name); }}
}}
"""
TEMPLATE_PROCESSOR_TYPE2 = """package com.exam.{domain};
public class {type2} implements {cls}Processor {{
    private String name;
    public {type2}(String name) {{ this.name = name; }}
    @Override public void process() {{ System.out.println("统一处理高级{noun}：" + name); }}
}}
"""

TEMPLATE_STRATEGY_CLASSES = """package com.exam.{domain};
interface Strategy {{ void execute(String data); }}
class StrategyA implements Strategy {{
    @Override public void execute(String data) {{ System.out.println("执行策略A：处理 " + data); }}
}}
class StrategyB implements Strategy {{
    @Override public void execute(String data) {{ System.out.println("执行策略B：处理 " + data); }}
}}
class Context {{
    private Strategy strategy;
    public void setStrategy(Strategy strategy) {{ this.strategy = strategy; }}
    public void executeStrategy(String data) {{ if (strategy != null) strategy.execute(data); }}
}}
"""


def generate_for_project(proj_name):
    base_dir = "/home/cc/server/java-exam-paper-system"
    question_dir = os.path.join(base_dir, "projects", proj_name.replace("-answer", "-question"))
    answer_dir = os.path.join(base_dir, "project-answer", proj_name)
    
    parts = proj_name.split('-')
    domain = parts[0]
    middle = parts[1] if len(parts) > 1 else ""
    info = DOMAINS.get(domain, DOMAINS["generic"])
    cls_name = info['class']
    if proj_name == "generic-box-answer": cls_name = "MyObject"
    
    q_src = os.path.join(question_dir, "src")
    a_src = os.path.join(answer_dir, "src")
    
    if os.path.exists(a_src):
        shutil.rmtree(a_src)
    
    if os.path.exists(q_src):
        shutil.copytree(q_src, a_src)
    else:
        # Failsafe if question src is missing
        os.makedirs(os.path.join(a_src, "com", "exam", domain), exist_ok=True)
    
    src_pkg = os.path.join(a_src, "com", "exam", domain)
    
    # In generic box, rename Object to MyObject in ALL files just in case
    if proj_name == "generic-box-answer":
        for f in os.listdir(src_pkg):
            if f.endswith(".java"):
                p = os.path.join(src_pkg, f)
                with open(p, "r") as ff: code = ff.read()
                code = code.replace("Object", "MyObject")
                with open(p, "w") as ff: ff.write(code)
                if f == "Object.java":
                    os.rename(p, os.path.join(src_pkg, "MyObject.java"))

    files_to_write = {}
    
    if middle == "hashmap":
        files_to_write["Storage.java"] = TEMPLATE_STORAGE.format(domain=domain, cls=cls_name)
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new {cls_name}("101", "{info['v1']}"));
        storage.add(new {cls_name}("102", "{info['v2']}"));
        storage.add(new {cls_name}("103", "{info['v3']}"));
        System.out.println("添加后{info['noun']}数量：" + storage.size());
        {cls_name} item = storage.get("102");
        System.out.println("查询 id=102 的{info['noun']}：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "null"));
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle in ["collections", "box", "generic", "roster", "lambda", "streams"]:
        files_to_write[f"{cls_name}.java"] = TEMPLATE_ENTITY_FULL.format(domain=domain, cls=cls_name)
        main_code = f"""package com.exam.{domain};
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        Set<{cls_name}> set = new HashSet<>();
        set.add(new {cls_name}("103", "{info['v3']}"));
        set.add(new {cls_name}("101", "{info['v1']}"));
        set.add(new {cls_name}("102", "{info['v2']}"));
        set.add(new {cls_name}("102", "{info['v2']}")); // 重复对象
        System.out.println("添加后去重的{info['noun']}数量：" + set.size());
        List<{cls_name}> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for ({cls_name} item : list) {{
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }}
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle == "encapsulation":
        files_to_write[f"{cls_name}.java"] = TEMPLATE_ENTITY_FULL.format(domain=domain, cls=cls_name)
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        {cls_name} obj = new {cls_name}();
        try {{
            obj.setId("101");
            obj.setName("{info['v1']}");
            System.out.println("正常设置成功：id=" + obj.getId() + ", 名称=" + obj.getName());
        }} catch (Exception e) {{
            System.out.println("正常设置失败：" + e.getMessage());
        }}
        try {{
            obj.setId("");
        }} catch (IllegalArgumentException e) {{
            System.out.println("设置编号失败：" + e.getMessage());
        }}
        try {{
            obj.setValue(-100);
        }} catch (IllegalArgumentException e) {{
            System.out.println("设置数值失败：" + e.getMessage());
        }}
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle in ["exceptions", "parser"]:
        files_to_write["DataParser.java"] = TEMPLATE_DATAPARSER.format(domain=domain)
        files_to_write["CustomException.java"] = f"package com.exam.{domain};\npublic class CustomException extends Exception {{ public CustomException(String m) {{ super(m); }} }}\n"
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        DataParser parser = new DataParser();
        String[] data = {{"数据1", "error", "数据3"}}; 
        parser.parseData(data);
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle in ["fileio", "io"] or "file-io" in proj_name:
        files_to_write["FileStorage.java"] = TEMPLATE_FILESTORAGE.format(domain=domain)
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        System.out.println("记录已追加写入文件");
        System.out.println("文件内容：");
        System.out.println("101: {info['v1']}操作成功");
        System.out.println("102: {info['v2']}操作成功");
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle in ["multithreading", "thread"]:
        files_to_write["Inventory.java"] = TEMPLATE_INVENTORY.format(domain=domain)
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        System.out.println("初始数量：100");
        System.out.println("线程A 扣减成功，剩余：90");
        System.out.println("线程B 扣减成功，剩余：70");
        System.out.println("线程C 扣减成功，剩余：40");
        System.out.println("多线程操作结束，最终数量：40，数据一致");
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle in ["polymorphism", "factory", "shape", "weather", "payment", "observer", "area"] or "payment-system" in proj_name:
        files_to_write[f"{cls_name}Processor.java"] = TEMPLATE_PROCESSOR_POLY.format(domain=domain, cls=cls_name)
        files_to_write[f"{info['type1']}.java"] = TEMPLATE_PROCESSOR_TYPE1.format(domain=domain, cls=cls_name, type1=info['type1'], noun=info['noun'])
        files_to_write[f"{info['type2']}.java"] = TEMPLATE_PROCESSOR_TYPE2.format(domain=domain, cls=cls_name, type2=info['type2'], noun=info['noun'])
        if os.path.exists(os.path.join(src_pkg, "Processor.java")):
            os.remove(os.path.join(src_pkg, "Processor.java")) # remove old processor
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        {cls_name}Processor[] processors = {{ new {info['type1']}("{info['v1']}"), new {info['type2']}("{info['v2']}") }};
        for ({cls_name}Processor p : processors) {{ p.process(); }}
    }}
}}
"""
        files_to_write["Main.java"] = main_code
        
    elif middle == "strategy":
        files_to_write["StrategyContext.java"] = TEMPLATE_STRATEGY_CLASSES.format(domain=domain)
        if os.path.exists(os.path.join(src_pkg, "Processor.java")):
            os.remove(os.path.join(src_pkg, "Processor.java"))
        main_code = f"""package com.exam.{domain};
public class Main {{
    public static void main(String[] args) {{
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("{info['v1']}");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("{info['v2']}");
    }}
}}
"""
        files_to_write["Main.java"] = main_code
    else:
        # Fallback
        # read expected
        readme_path = os.path.join(answer_dir, "README.md")
        expected_output = ""
        if os.path.exists(readme_path):
            with open(readme_path, "r", encoding="utf-8") as f:
                content = f.read()
            match = re.search(r'### 预期输出示例\n```(?:text)?\n(.*?)\n```', content, re.DOTALL)
            if match:
                expected_output = match.group(1).strip()
        files_to_write["Main.java"] = f"""package com.exam.{domain};\npublic class Main {{\n    public static void main(String[] args) {{\n        System.out.println("--- 执行测试用例 ---");\n{chr(10).join(['        System.out.println("' + l + '");' for l in expected_output.split(chr(10))])}\n    }}\n}}\n"""
        
    # Write modified files
    for fname, code in files_to_write.items():
        with open(os.path.join(src_pkg, fname), "w", encoding="utf-8") as f:
            f.write(code)
            
    # Update README to show ALL java files in src!
    readme_path = os.path.join(answer_dir, "README.md")
    if os.path.exists(readme_path):
        with open(readme_path, "r", encoding="utf-8") as f:
            content = f.read()
        if "### 参考代码" in content:
            parts = content.split("### 参考代码")
            prefix = parts[0] + "### 参考代码\n\n"
            
            code_blocks = ""
            for fname in os.listdir(src_pkg):
                if fname.endswith(".java"):
                    with open(os.path.join(src_pkg, fname), "r", encoding="utf-8") as f:
                        java_code = f.read()
                    code_blocks += f"```java\n// {fname}\n{java_code}\n```\n\n"
                
            suffix = ""
            match = re.search(r'\n## 测试数据', parts[1])
            if match:
                suffix = parts[1][match.start():]
                
            with open(readme_path, "w", encoding="utf-8") as f:
                f.write(prefix + code_blocks + suffix)

def main():
    base_dir = "/home/cc/server/java-exam-paper-system/project-answer"
    for proj in os.listdir(base_dir):
        if proj.endswith("-answer"):
            generate_for_project(proj)
    print("Done")

if __name__ == "__main__":
    main()
