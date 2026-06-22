# 面向对象与封装 (酒店系统)

在酒店管理系统中，`Room` 类的属性如果直接声明为 `public`，外部代码就可以随意修改对象状态，容易造成数据不合法，影响系统的可靠性。

请根据面向对象中的封装思想，对 `Room` 类进行改造，要求如下：
1. 将原来可被外部直接访问的属性改为 `private`。
2. 为属性提供规范的 Getter 和 Setter 方法。
3. 在 Setter 方法中加入必要的数据合法性校验，例如：
   - 字符串类型属性不能为空或空白字符串；
   - 数值类型属性不能为负数。

请给出改造后的 `Room` 类代码实现。


---

## 解决方案

本题考查面向对象中的**封装性**。

封装的核心思想是：**隐藏对象内部实现细节，只对外暴露受控的访问方式**。在本题中，如果将属性声明为 `public`，外部代码可以直接修改 `Room` 对象中的数据，例如把房间编号设为 `null`，或把价格设为负数，这会破坏对象的有效状态。

改造后的实现体现了以下几点：

1. **属性私有化**  
   使用 `private` 修饰 `id` 和 `amount`，禁止外部直接访问。

2. **通过方法访问属性**  
   提供 Getter/Setter 方法，使属性的读取和修改都通过统一入口进行。

3. **在 Setter 中进行数据校验**  
   - `id` 不能为 `null`，也不能是空字符串或仅包含空白字符；
   - `amount` 不能小于 0。

这样可以保证对象在赋值时始终处于合理状态，提高程序的健壮性和可维护性。

例如，下面的写法会触发异常：

```java
Room room = new Room();
room.setId("   ");      // 非法，抛出异常
room.setAmount(-100);    // 非法，抛出异常
```

因此，本题的关键不只是把 `public` 改成 `private`，更重要的是通过 Getter/Setter 和参数校验真正实现对数据的保护。

### 参考代码

```java
public class Room {
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
