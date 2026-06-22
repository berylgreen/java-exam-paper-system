# 集合框架 (List/Set) (电商系统)

电商系统早期使用定长对象数组保存订单列表。随着订单数量增长，这种方式逐渐暴露出两个问题：一是数组容量固定，扩容和管理不方便；二是难以按照业务规则高效去除重复订单。

请使用 Java 集合框架完成以下优化：  
(1) 使用 `ArrayList` 或 `HashSet` 替代原有定长数组来存储订单。  
(2) 定义 `Order` 类，并根据业务主键（如订单编号 `id`）重写 `equals()` 和 `hashCode()`，实现订单去重。  
(3) 为 `Order` 类提供排序能力，可通过实现 `Comparable<Order>` 接口或创建 `Comparator` 按订单编号升序排序。  
(4) 编写示例代码，演示：添加订单、自动去重、转换为可排序集合并完成排序输出。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下商品：
  - `103`，`蓝牙耳机`
  - `101`，`笔记本电脑`
  - `102`，`智能手机`
  - `102`，`智能手机` (重复)

**预期输出示例：**
```text
添加后去重的商品数量：3
排序后输出：
id=101: 笔记本电脑
id=102: 智能手机
id=103: 蓝牙耳机
```

---

## 解决方案

```java
// 运行结果：
A001
A002
```

本题的核心是使用集合框架替代定长数组，从而提升订单管理的灵活性与正确性。  

(1) **使用集合替代数组**  
   - `ArrayList` 适合按顺序保存元素，支持动态扩容。  
   - `HashSet` 适合去重存储，因为它不允许保存重复元素。  
   在本题中，为了突出“去重”需求，使用 `HashSet<Order>` 更合适。  

(2) **重写 `equals()` 和 `hashCode()`**  
   `HashSet` 判断两个对象是否重复，不是只看对象地址，而是依赖：
   - `hashCode()`：先确定对象的哈希位置
   - `equals()`：再判断两个对象在业务上是否相等

   如果两个订单的 `id` 相同，就应视为同一订单，因此在 `Order` 类中基于 `id` 重写这两个方法。  

(3) **实现排序功能**  
   `HashSet` 本身是无序的。如果需要排序，可以先将其转换为 `List<Order>`，再通过：
   - 让 `Order` 实现 `Comparable<Order>` 接口或创建 `Comparator`
   - 在 `compareTo()` 中定义排序规则

   本题中按订单编号 `id` 的字典序升序排序。  

(4) **程序执行过程**  
   - 向 `HashSet` 中添加 `A002`、`A001`、`A002`
   - 因为第二个 `A002` 与第一个业务上相同，所以不会重复保存
   - 再将 `Set` 转换为 `List`
   - 使用 `Collections.sort()` 排序后输出结果  

(5) **原答案中的问题**  
   原代码把自定义类命名为 `ArrayList`，会与 Java 标准库中的 `ArrayList` 类重名，造成语义混乱，甚至导致代码无法正确编译或使用。因此应将业务实体类命名为 `Order`。

### 参考代码

```java
// Main.java
package com.exam.ecommerce;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Order> set = new HashSet<>();
        set.add(new Order("103", "蓝牙耳机"));
        set.add(new Order("101", "笔记本电脑"));
        set.add(new Order("102", "智能手机"));
        set.add(new Order("102", "智能手机")); // 重复对象
        System.out.println("添加后去重的商品数量：" + set.size());
        List<Order> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Order item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```

```java
// Order.java
package com.exam.ecommerce;
import java.util.Objects;
public class Order implements Comparable<Order> {
    private String id;
    private String name;
    private double value;
    public Order() {}
    public Order(String id, String name) { this.id = id; this.name = name; }
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
        Order that = (Order) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Order other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Order{id='" + id + "', name='" + name + "'}"; }
}

```

