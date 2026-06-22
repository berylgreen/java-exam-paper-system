# 集合框架 (List/Set) (图书馆系统)

某图书馆系统原先使用**定长对象数组**保存图书信息。随着图书数量增加，这种方式逐渐暴露出以下问题：

- 数组容量固定，元素增多时容易发生越界或扩容不便；
- 难以高效判断并过滤重复图书；
- 不便于按照业务规则对图书列表进行排序。

**请使用 Java 集合框架对该功能进行改进，完成以下要求：**
(1) 使用 `ArrayList` 或 `HashSet` 替代原有的底层数组来管理图书数据；
(2) 定义 `Book` 类，并根据图书唯一标识（如 `id`）重写 `equals()` 和 `hashCode()` 方法，以实现业务上的去重；
(3) 为 `Book` 类提供排序能力，可通过实现 `Comparable<Book>`接口或创建 `Comparator`，按照某一业务规则（例如按图书编号升序）进行排序；
(4) 编写测试代码，演示图书的添加、去重与排序过程。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下图书：
  - `103`，`计算机网络`
  - `101`，`Java编程思想`
  - `102`，`算法导论`
  - `102`，`算法导论` (重复)

**预期输出示例：**
```text
添加后去重的图书数量：3
排序后输出：
id=101: Java编程思想
id=102: 算法导论
id=103: 计算机网络
```

---

## 解决方案

本题的核心是使用**集合框架**替代定长数组，并结合 `equals()`、`hashCode()` 和排序机制完成图书管理。

### 1. 为什么要使用集合替代数组
- 数组长度固定，新增元素不方便，容易出现越界问题；
- `ArrayList` 可以动态扩容，适合存储数量不断变化的数据；
- `HashSet` 不允许存放重复元素，适合做去重处理。

因此，本题可以先使用 `HashSet<Book>` 保存图书，实现自动去重；再转换为 `ArrayList<Book>` 进行排序。

### 2. 为什么要重写 `equals()` 和 `hashCode()`
在 `HashSet` 中，判断对象是否重复时，不仅会用到 `hashCode()`，还会在必要时调用 `equals()`。

如果不重写这两个方法，即使两个 `Book` 对象的 `id` 相同，系统也会认为它们是不同对象，无法实现业务上的“同一本书去重”。

本题中用图书编号 `id` 作为唯一标识，因此：
- `equals()`：判断两个图书对象的 `id` 是否相同；
- `hashCode()`：根据 `id` 生成哈希值，保证相等对象具有相同哈希值。

### 3. 为什么实现 `Comparable<Book>`
为了让图书对象可以直接排序，可以让 `Book` 类实现 `Comparable<Book>`接口或创建 `Comparator`，并重写 `compareTo()` 方法。

本答案中按照图书编号 `id` 升序排序：

```java
@Override
public int compareTo(Book other) {
    return this.id.compareTo(other.id);
}
```

之后即可使用：

```java
Collections.sort(bookList);
```

### 4. 程序执行过程说明
(1) 创建 `HashSet<Book>` 存放图书；
(2) 向集合中加入 3 本图书，其中两本图书 `id` 相同；
(3) 由于重写了 `equals()` 和 `hashCode()`，重复的图书不会被重复保存；
(4) 将 `HashSet` 转为 `ArrayList`；
(5) 使用 `Collections.sort()` 按 `id` 排序；
(6) 输出排序后的图书信息。

### 5. 结果特点
最终输出中：
- 不会出现 `id` 相同的重复图书；
- 图书会按照编号升序排列。

这说明程序同时完成了**动态存储、去重和排序**三个要求，符合题目要求。

### 参考代码

```java
// Main.java
package com.exam.library;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Set<Book> set = new HashSet<>();
        set.add(new Book("103", "计算机网络"));
        set.add(new Book("101", "Java编程思想"));
        set.add(new Book("102", "算法导论"));
        set.add(new Book("102", "算法导论")); // 重复对象
        System.out.println("添加后去重的图书数量：" + set.size());
        List<Book> list = new ArrayList<>(set);
        Collections.sort(list);
        System.out.println("排序后输出：");
        for (Book item : list) {
            System.out.println("id=" + item.getId() + ": " + item.getName());
        }
    }
}

```

```java
// Book.java
package com.exam.library;
import java.util.Objects;
public class Book implements Comparable<Book> {
    private String id;
    private String name;
    private double value;
    public Book() {}
    public Book(String id, String name) { this.id = id; this.name = name; }
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
        Book that = (Book) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Book other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Book{id='" + id + "', name='" + name + "'}"; }
}

```

