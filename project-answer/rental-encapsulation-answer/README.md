# 面向对象与封装 (租车系统)

在租车系统中，`Vehicle` 类如果直接将属性声明为 `public`，外部代码就可以随意读取和修改对象状态，这会破坏封装性，也不利于保证数据合法性。

**任务要求：**
1. 将 `Vehicle` 类中原本对外公开的属性改为 `private`。
2. 为这些属性提供标准的 Getter 和 Setter 方法。
3. 在 Setter 方法中增加必要的数据有效性校验，例如：
   - 字符串类型属性不能为 `null`，也不能为空字符串；
   - 数值类型属性不能为负数。

请编写优化后的 `Vehicle` 类。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`丰田卡罗拉`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

### 预期输出示例
```text
正常设置成功：id=101, 名称=丰田卡罗拉
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

```java
public class Vehicle {
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

本题考查的是面向对象中的**封装**思想。

1. **属性设为 `private`**：
   将成员变量隐藏在类内部，避免外部代码直接修改对象状态。

2. **提供 Getter/Setter 方法**：
   通过公共方法访问和修改属性，是 Java 中常见的封装写法。

3. **在 Setter 中进行合法性校验**：
   - `id` 不能为 `null`，也不能是空字符串；
   - `amount` 不能小于 0。

这样设计后，`Vehicle` 对象的状态只能通过受控方式修改，从而提高了代码的安全性、可维护性和健壮性。

### 参考代码

```java
public class Vehicle {
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
