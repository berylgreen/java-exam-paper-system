# 集合框架 (List/Set) (智能家居)

智能家居系统早期使用定长对象数组来存放和管理设备列表。随着设备数量增加，这种方式逐渐暴露出两个问题：一是容量固定，容易出现数组越界；二是难以高效判断和去除重复设备。

请使用 Java 集合框架对该功能进行重构，完成以下任务：
1. 使用 `ArrayList` 或 `HashSet` 替代原有定长数组来存储设备对象。
2. 定义 `Device` 类，并根据业务标识（如设备编号 `id`）重写 `equals()` 和 `hashCode()`，以保证集合中的重复设备能够被正确识别。
3. 提供设备排序功能，可让 `Device` 实现 `Comparable<Device>` 接口或创建 `Comparator`，并按照某一业务规则（如设备编号升序）排序。
4. 编写测试代码，演示：添加多个设备、自动去重、排序后输出结果。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下设备：
  - `103`，`智能门锁`
  - `101`，`智能灯`
  - `102`，`智能空调`
  - `102`，`智能空调` (重复)

### 预期输出示例
```text
添加后去重的设备数量：3
排序后输出：
id=101: 智能灯
id=102: 智能空调
id=103: 智能门锁
```

---

## 解决方案

```java
// 运行结果示例：
// 去重并排序后的设备列表：
// Device{id='A001', name='智能灯'}
// Device{id='A002', name='智能门锁'}
// Device{id='A003', name='智能空调'}
```

本题考查集合框架中 `List`、`Set`、对象去重规则以及排序机制的综合应用。

1. **为什么要用集合替代数组**
   - 数组长度固定，元素数量增长后容易出现容量不足的问题。
   - `ArrayList` 支持动态扩容，适合按顺序存储和遍历元素。
   - `HashSet` 不允许存放重复元素，适合做去重处理。

2. **为什么要重写 `equals()` 和 `hashCode()`**
   - `HashSet` 判断两个对象是否重复时，依赖 `hashCode()` 和 `equals()`。
   - 如果不重写，系统默认比较的是对象地址，即使两个设备的 `id` 相同，也会被当作不同对象。
   - 本题中以设备编号 `id` 作为业务上的唯一标识，因此应根据 `id` 来重写这两个方法。

3. **为什么实现 `Comparable<Device>`**
   - 实现该接口或创建 `Comparator`后，可以定义对象的“自然顺序”。
   - 本题中在 `compareTo()` 中按 `id` 升序比较，因此调用 `Collections.sort()` 后可完成排序。

4. **程序实现思路**
   - 先将 `Device` 对象加入 `HashSet`，自动过滤重复设备。
   - 再将 `Set` 转换为 `ArrayList`。
   - 使用 `Collections.sort()` 对列表排序。
   - 最后输出排序后的设备列表。

5. **原答案中的问题已修正**
   - 原代码错误地将自定义类命名为 `ArrayList`，与 Java 集合类重名，容易造成编译和理解问题。
   - 现在改为定义业务类 `Device`，更符合题意，也避免与标准库类冲突。
   - 同时补充了设备名称字段和 `toString()` 方法，使测试结果更清晰。

### 参考代码

```java
// Main.java
package com.exam.smarthome;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Device> set = new HashSet<>();
        set.add(new Device("103", "智能门锁"));
        set.add(new Device("101", "智能灯"));
        set.add(new Device("102", "智能空调"));
        set.add(new Device("102", "智能空调")); // 重复对象
        System.out.println("添加后去重的设备数量：" + set.size());
        List<Device> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Device item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```

```java
// Device.java
package com.exam.smarthome;
import java.util.Objects;
public class Device implements Comparable<Device> {
    private String id;
    private String name;
    private double value;
    public Device() {}
    public Device(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("编号不能为空");
        this.id = id;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getValue() { return value; }
    public void setValue(double value) {
        if (value < 0) throw new IllegalArgumentException("数值不能为负数");
        this.value = value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device that = (Device) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Device other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Device{id='" + id + "', name='" + name + "'}"; }
}

```

