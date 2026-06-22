# 集合框架 (List/Set) (物流系统)

某物流系统早期使用定长对象数组保存“包裹”信息。随着业务量增加，这种方式逐渐暴露出两个问题：一是数组长度固定，扩容不方便；二是难以按照包裹的业务唯一标识去除重复数据。

请编写程序完成下列任务：
1. 使用 `ArrayList` 或 `HashSet` 替代原有定长数组来管理包裹集合；
2. 定义 `Package` 类，并根据包裹唯一标识 `id` 重写 `equals()` 和 `hashCode()` 方法，使集合能够正确识别重复包裹；
3. 使用 `Comparator<Package>` 指定排序规则，要求按包裹编号 `id` 的升序排序；
4. 编写测试代码，演示以下过程：
   - 向集合中添加多个包裹对象；
   - 自动去除重复包裹；
   - 按指定规则排序后输出结果。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下包裹：
  - `103`，`衣物包裹`
  - `101`，`电子产品包裹`
  - `102`，`书籍包裹`
  - `102`，`书籍包裹` (重复)

### 预期输出示例
```text
添加后去重的包裹数量：3
排序后输出：
id=101: 电子产品包裹
id=102: 书籍包裹
id=103: 衣物包裹
```

---

## 解决方案

```java
// 程序输出示例：
// Package{id='A001'}
// Package{id='A002'}
// Package{id='A003'}
```

本题的关键是使用集合替代定长数组，并结合对象判等与排序规则完成“去重 + 排序”功能。

1. `HashSet` 用于去重
   - 数组长度固定，不适合数量动态变化的包裹管理。
   - `HashSet` 可以自动去除重复元素，适合先完成包裹去重。
   - 当向 `HashSet` 中重复加入 `id` 相同的包裹对象时，集合中最终只会保留一份。

2. `equals()` 和 `hashCode()` 必须同时重写
   - `HashSet` 判断两个对象是否重复时，会同时依赖 `hashCode()` 和 `equals()`。
   - 如果不重写这两个方法，即使两个包裹的 `id` 相同，也可能被当作不同对象处理。
   - 题目中 `id` 是业务唯一标识，因此应基于 `id` 实现对象相等判断。

3. 使用 `Comparator<Package>` 指定排序规则
   - 题目要求按 `id` 升序排序，因此先把 `HashSet` 中的数据转为 `ArrayList`。
   - 然后使用 `Comparator<Package>` 对列表排序。
   - 这里使用的是匿名内部类写法，没有使用 lambda 表达式，也符合题目要求。

4. 程序执行流程
   - 创建 `HashSet<Package>` 保存包裹对象；
   - 添加多个包裹，其中包含重复编号 `A002`；
   - `HashSet` 自动去重；
   - 将去重后的结果放入 `ArrayList`；
   - 调用 `sort()` 并传入 `Comparator<Package>` 按编号升序排列；
   - 最后遍历输出排序结果。

因此，本题体现了集合框架中 `Set` 的去重能力、`List` 的有序处理能力，以及 `Comparator` 自定义排序规则的应用。

### 参考代码

```java
// Object.java 等实体类
package com.exam.logistics;
import java.util.Objects;
public class Package implements Comparable<Package> {
    private String id;
    private String name;
    private double value;
    public Package() {}
    public Package(String id, String name) { this.id = id; this.name = name; }
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
        Package that = (Package) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Package other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Package{id='" + id + "', name='" + name + "'}"; }
}

```

```java
// Main.java 等核心逻辑
package com.exam.logistics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Package> set = new HashSet<>();
        set.add(new Package("103", "衣物包裹"));
        set.add(new Package("101", "电子产品包裹"));
        set.add(new Package("102", "书籍包裹"));
        set.add(new Package("102", "书籍包裹")); // 重复对象
        System.out.println("添加后去重的包裹数量：" + set.size());
        List<Package> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Package item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```