# 面向对象与封装 (餐饮系统)

在餐饮系统中，`Dish` 类原先将属性直接声明为 `public`，这会使外部代码可以任意读取和修改对象状态，不利于保证数据的合法性与一致性。

请根据面向对象的封装思想，对 `Dish` 类进行改造，要求如下：
1. 将原有的 `public` 属性改为 `private`。
2. 为属性提供标准的 Getter 和 Setter 方法。
3. 在 Setter 方法中加入必要的数据有效性校验：
   - 字符串类型属性不能为 `null`，且不能为空字符串；
   - 数值类型属性不能为负数。
4. 当输入数据不合法时，抛出 `IllegalArgumentException` 异常。

请写出改造后的 `Dish` 类代码。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`宫保鸡丁`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

### 预期输出示例
```text
正常设置成功：id=101, 名称=宫保鸡丁
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

```java
public class Dish {
    private String id;
    private double amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id 不能为 null 或空字符串");
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

本题考查面向对象中的**封装性**。

1. **将属性设为 `private`**  
   使用 `private` 修饰属性后，类的外部不能直接访问或修改成员变量，从而避免对象状态被随意篡改。

2. **提供 Getter/Setter 方法**  
   通过公开方法访问和修改属性，是 Java 中实现封装的标准方式。这样既能满足数据访问需求，又能在赋值时加入控制逻辑。

3. **在 Setter 中进行数据校验**  
   - `id` 作为字符串标识，不能为 `null`，也不能是空字符串或只包含空白字符；
   - `amount` 表示数值信息，不能小于 `0`。  
   若数据不合法，抛出 `IllegalArgumentException`，可以及时发现并阻止错误数据进入对象。

4. **这样设计的好处**  
   通过封装和校验机制，可以提高类的安全性、可维护性和健壮性，保证 `Dish` 对象始终处于合法状态。

如果原类中还有其他属性，也应按照相同思路：改为 `private`，并在对应的 Setter 中补充合理的校验规则。

### 参考代码

```java
public class Dish {
    private String id;
    private double amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id 不能为 null 或空字符串");
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
