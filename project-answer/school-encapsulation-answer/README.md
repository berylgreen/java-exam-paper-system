# 面向对象与封装 (教务系统)

在教务系统中，`Student` 类原先将成员变量定义为 `public`，这会导致外部代码可以直接修改对象状态，不利于数据安全与封装性。

**请按要求重构 `Student` 类：**
1. 将原有的 `public` 成员变量修改为 `private`。
2. 为每个成员变量提供标准的 Getter 和 Setter 方法。
3. 在 Setter 方法中加入必要的数据合法性校验：
   - 字符串类型字段不能为空（不能为 `null`，也不能为空字符串）；
   - 数值类型字段不能为负数。
4. 当输入数据不合法时，应抛出 `IllegalArgumentException` 异常。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`高等数学`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

### 预期输出示例
```text
正常设置成功：id=101, 名称=高等数学
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

```java
public class Student {
    private String id;
    private double amount;
    // ... getter / setter
}
```

本题考查面向对象中的**封装性**。

1. **将属性声明为 `private`**  
   成员变量使用 `private` 修饰后，类的外部不能直接访问或随意修改属性值，从而保护对象内部状态。

2. **通过 Getter/Setter 提供受控访问**  
   - Getter 用于读取属性值；
   - Setter 用于修改属性值；
   这样可以在赋值时统一进行规则检查。

3. **在 Setter 中进行合法性校验**  
   - `id` 是字符串，不能为 `null`，也不能是只包含空白字符的空串；
   - `amount` 是数值，不能小于 `0`。

4. **非法数据抛出异常**  
   当传入不符合要求的数据时，使用 `IllegalArgumentException` 可以明确提示“参数不合法”，这是 Java 中常见的做法。

通过这样的改造，`Student` 类不再允许外部直接破坏数据，有助于提高程序的健壮性与可维护性。

### 参考代码

```java
public class Student {
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
