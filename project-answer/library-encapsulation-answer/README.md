# 面向对象与封装 (图书馆系统)

在图书馆系统中，核心实体类 `Book` 目前将属性声明为 `public`，这会使外部代码能够直接修改对象状态，从而影响数据的安全性与完整性。

请按照面向对象中“封装”的思想，对 `Book` 类进行改造，要求如下：
1. 将原来对外公开的属性改为 `private`。
2. 为每个属性提供规范的 Getter/Setter 方法。
3. 在 Setter 方法中加入基本的数据合法性校验，例如：
   - 字符串属性不能为 `null`，也不能为空字符串；
   - 数值属性不能为负数。

请给出改造后的 `Book` 类代码。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`Java编程思想`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

**预期输出示例：**
```text
正常设置成功：id=101, 名称=Java编程思想
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

本题考查面向对象中的**封装性**。

将属性声明为 `private` 后，外部不能直接访问或修改对象内部数据，只能通过公开的方法进行操作。这样做有两个主要好处：

1. **隐藏实现细节**：对象内部属性不再暴露给外部，降低耦合。
2. **保证数据有效性**：通过 Setter 方法统一进行校验，避免非法数据进入对象。

例如本题中：
- `id` 是字符串类型，因此在 `setId()` 中要判断是否为 `null` 或空字符串；
- `amount` 是数值类型，因此在 `setAmount()` 中要判断是否小于 0。

改造后的代码体现了“属性私有、方法公开”的基本封装规范，符合 Java 面向对象编程的设计思想。

### 参考代码

```java
// Main.java
package com.exam.library;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Book obj = new Book();
        try {
            obj.setId("101");
            obj.setName("Java编程思想");
            System.out.println("正常设置成功：id=" + obj.getId() + ", 名称=" + obj.getName());
        } catch (Exception e) {
            System.out.println("正常设置失败：" + e.getMessage());
        }
        try {
            obj.setId("");
        } catch (IllegalArgumentException e) {
            System.out.println("设置编号失败：" + e.getMessage());
        }
        try {
            obj.setValue(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("设置数值失败：" + e.getMessage());
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

