# 面向对象与封装 (电商系统)

在电商系统中，`Order` 类中的属性如果直接声明为 `public`，外部代码就可以绕过业务规则随意修改订单状态，容易造成数据不一致。

请根据封装思想，对 `Order` 类进行重构，要求如下：
1. 将原来对外公开的属性改为 `private`，隐藏内部数据。
2. 为每个属性提供规范的 Getter 和 Setter 方法。
3. 在 Setter 方法中加入基本的数据合法性校验：
   - 订单编号 `id` 不能为空（不能为 `null`，去除首尾空格后也不能为空串）。
   - 订单金额 `amount` 不能为负数。
4. 当数据不合法时，抛出合适的异常提示错误。

请写出重构后的 `Order` 类代码。


---

## 解决方案

```java
public class Order {
    private String id;
    private double amount;
    ...
}
```
以上将属性改为 `private`，体现了**封装**思想。对象的内部数据不能被外部直接访问，只能通过方法进行受控操作。

```java
public String getId() {
    return id;
}

public double getAmount() {
    return amount;
}
```
Getter 方法用于读取属性值，是对外提供访问入口的标准写法。

```java
public void setId(String id) {
    if (id == null || id.trim().isEmpty()) {
        throw new IllegalArgumentException("id cannot be null or empty");
    }
    this.id = id;
}
```
`setId` 中对订单编号进行了校验：
- `id == null`：防止传入空引用；
- `id.trim().isEmpty()`：防止传入仅包含空格的无效字符串。

```java
public void setAmount(double amount) {
    if (amount < 0) {
        throw new IllegalArgumentException("amount cannot be negative");
    }
    this.amount = amount;
}
```
`setAmount` 中限制订单金额不能小于 0，保证业务数据合理。

当参数不合法时，抛出 `IllegalArgumentException`，表示“调用方法时传入了非法参数”，这是 Java 中非常常见且合适的做法。

本题的核心在于：
1. 使用 `private` 隐藏属性；
2. 使用 Getter/Setter 提供受控访问；
3. 在 Setter 中加入校验逻辑，保证对象始终处于合法状态。

这样设计后，`Order` 类的安全性、可维护性和健壮性都会更好。

### 参考代码

```java
public class Order {
    private String id;
    private double amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.amount = amount;
    }
}
```
