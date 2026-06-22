import os
import re

DOMAINS = {
    "bank": {"noun": "账户", "v1": "张三的账户", "v2": "李四的账户", "v3": "王五的账户"},
    "ecommerce": {"noun": "商品", "v1": "笔记本电脑", "v2": "智能手机", "v3": "蓝牙耳机"},
    "hospital": {"noun": "病历", "v1": "张三的病历", "v2": "李四的病历", "v3": "王五的病历"},
    "hotel": {"noun": "客房", "v1": "总统套房", "v2": "豪华大床房", "v3": "标准间"},
    "library": {"noun": "图书", "v1": "Java编程思想", "v2": "算法导论", "v3": "计算机网络"},
    "logistics": {"noun": "包裹", "v1": "电子产品包裹", "v2": "书籍包裹", "v3": "衣物包裹"},
    "rental": {"noun": "车辆", "v1": "丰田卡罗拉", "v2": "本田雅阁", "v3": "宝马X5"},
    "restaurant": {"noun": "菜品", "v1": "宫保鸡丁", "v2": "鱼香肉丝", "v3": "红烧肉"},
    "school": {"noun": "课程", "v1": "高等数学", "v2": "大学物理", "v3": "计算机科学"},
    "smarthome": {"noun": "设备", "v1": "智能灯", "v2": "智能空调", "v3": "智能门锁"},
    "generic": {"noun": "对象", "v1": "对象A", "v2": "对象B", "v3": "对象C"},
    "exception": {"noun": "数据", "v1": "数据1", "v2": "数据2", "v3": "数据3"},
    "file": {"noun": "记录", "v1": "记录1", "v2": "记录2", "v3": "记录3"},
    "lambda": {"noun": "元素", "v1": "元素1", "v2": "元素2", "v3": "元素3"},
    "payment": {"noun": "订单", "v1": "订单1", "v2": "订单2", "v3": "订单3"},
    "shape": {"noun": "图形", "v1": "圆形", "v2": "矩形", "v3": "三角形"},
    "student": {"noun": "学生", "v1": "小明", "v2": "小红", "v3": "小刚"},
    "ticket": {"noun": "车票", "v1": "车票1", "v2": "车票2", "v3": "车票3"},
    "vehicle": {"noun": "交通工具", "v1": "汽车", "v2": "卡车", "v3": "摩托车"},
    "weather": {"noun": "气象数据", "v1": "温度数据", "v2": "湿度数据", "v3": "气压数据"}
}

TOPICS = {
    "hashmap": """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 添加{noun}：
  - `101`，`{v1}`
  - `102`，`{v2}`
  - `103`，`{v3}`
- 查询{noun}：`102`
- 删除{noun}：`102`
- 删除后再次查询：`102`

### 预期输出示例
```text
添加后{noun}数量：3
查询 id=102 的{noun}：{v2}
删除后再次查询 id=102：null
```
""",
    "collections": """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下{noun}：
  - `103`，`{v3}`
  - `101`，`{v1}`
  - `102`，`{v2}`
  - `102`，`{v2}` (重复)

### 预期输出示例
```text
添加后去重的{noun}数量：3
排序后输出：
id=101: {v1}
id=102: {v2}
id=103: {v3}
```
""",
    "encapsulation": """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`{v1}`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

### 预期输出示例
```text
正常设置成功：id=101, 名称={v1}
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```
""",
    "exceptions": """### 测试数据示例
请在 `Main` 类中使用以下包含错误的数据进行解析演示：
- 数据1：正常格式
- 数据2：错误格式 (触发异常)
- 数据3：正常格式

### 预期输出示例
```text
解析成功：数据1
捕获异常：格式错误，跳过该条数据
解析成功：数据3
全部数据处理完毕
```
""",
    "fileio": """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 写入{noun}操作记录：`101`，`{v1}`操作成功
- 写入{noun}操作记录：`102`，`{v2}`操作成功

### 预期输出示例
```text
记录已追加写入文件
文件内容：
101: {v1}操作成功
102: {v2}操作成功
```
""",
    "multithreading": """### 测试数据示例
请在 `Main` 类中启动三个线程，同时对同一个{noun}进行操作演示：
- 初始数量：`100`
- 线程A：扣减 `10`
- 线程B：扣减 `20`
- 线程C：扣减 `30`

### 预期输出示例
```text
初始数量：100
线程A 扣减成功，剩余：90
线程B 扣减成功，剩余：70
线程C 扣减成功，剩余：40
多线程操作结束，最终数量：40，数据一致
```
""",
    "polymorphism": """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础{noun}对象：`{v1}`
- 创建高级{noun}对象：`{v2}`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础{noun}：{v1}
统一处理高级{noun}：{v2}
```
""",
    "strategy": """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`{v1}`应用策略A
- 对`{v2}`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 {v1}
切换到策略B
执行策略B：处理 {v2}
```
"""
}

# Generic mapping for odd questions
DEFAULT_TEMPLATE = """### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 输入：`{v1}`、`{v2}`、`{v3}`

### 预期输出示例
```text
处理成功：{v1}
处理成功：{v2}
处理成功：{v3}
```
"""

base_dir = "."
for d in os.listdir(base_dir):
    dir_path = os.path.join(base_dir, d)
    if os.path.isdir(dir_path) and d.endswith("-question"):
        readme_path = os.path.join(dir_path, "README.md")
        if not os.path.exists(readme_path):
            continue
            
        with open(readme_path, "r", encoding="utf-8") as f:
            content = f.read()
            
        if "### 测试数据示例" in content:
            continue
            
        parts = d.split("-")
        domain_key = parts[0]
        
        # We need a proper topic key mapping
        # Let's map explicitly based on the middle part
        middle = parts[1] if len(parts) > 1 else ""
        
        topic_key = None
        if middle in TOPICS:
            topic_key = middle
        else:
            # handle odd cases manually
            if "thread" in d:
                topic_key = "multithreading"
            elif "parser" in d:
                topic_key = "exceptions"
            elif "io" in d and "file" in d: # handles file-io
                topic_key = "fileio"
            elif "box" in d or "generic" in d or "roster" in d or "lambda" in d:
                topic_key = "collections"
            elif "factory" in d or "shape" in d or "payment" in d or "weather" in d:
                topic_key = "polymorphism"
            
        if not topic_key:
            template = DEFAULT_TEMPLATE
        else:
            template = TOPICS[topic_key]
            
        if domain_key not in DOMAINS:
            domain_info = DOMAINS["generic"]
        else:
            domain_info = DOMAINS[domain_key]
            
        append_str = "\n" + template.format(**domain_info)
        
        with open(readme_path, "a", encoding="utf-8") as f:
            f.write(append_str)
        print(f"Updated {d} with {topic_key} template")
