# 面向对象与封装 (图书馆系统)

在图书馆系统中，核心实体类 `Book` 目前将属性声明为 `public`，这会使外部代码能够直接修改对象状态，从而影响数据的安全性与完整性。

请按照面向对象中“封装”的思想，对 `Book` 类进行改造，要求如下：
1. 将原来对外公开的属性改为 `private`。
2. 为每个属性提供规范的 Getter/Setter 方法。
3. 在 Setter 方法中加入基本的数据合法性校验，例如：
   - 字符串属性不能为 `null`，也不能为空字符串；
   - 数值属性不能为负数。

请给出改造后的 `Book` 类代码。


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
public class Book {
    private String id;
    private double amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount 不能为负数");
        }
        this.amount = amount;
    }
}
```
