# 面向对象与封装 (医疗系统)

在医疗系统中，核心实体类 `Patient` 原先将属性声明为 `public`，这会使外部代码能够直接修改对象状态，从而影响数据的安全性与一致性。

请根据封装思想对该类进行改造，完成以下要求：
1. 将原有对外公开的属性改为 `private`。
2. 为每个属性提供规范的 Getter 和 Setter 方法。
3. 在 Setter 方法中加入必要的数据合法性校验：
   - 字符串属性不能为空、不能为 `null`；
   - 数值属性不能为负数。
4. 当传入数据不合法时，抛出合适的异常提示错误。

请给出改造后的 `Patient` 类代码。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`张三的病历`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

### 预期输出示例
```text
正常设置成功：id=101, 名称=张三的病历
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

本题考查面向对象中的**封装**思想。

```java
public class Patient {
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

解析如下：
1. 将属性改为 `private`：
   - `private` 表示属性只能在当前类内部直接访问；
   - 可以防止外部代码随意修改对象成员，提升安全性。

2. 提供 Getter/Setter：
   - Getter 用于读取属性值；
   - Setter 用于在受控条件下修改属性值；
   - 这样可以把“赋值”行为集中管理。

3. 在 Setter 中进行合法性校验：
   - `id` 不能为 `null`，也不能是空字符串或只包含空白字符；
   - `amount` 不能小于 0；
   - 若数据不合法，使用 `IllegalArgumentException` 抛出异常，能够及时发现错误输入。

4. 封装的意义：
   - 隐藏对象内部实现细节；
   - 保证对象状态始终处于合理范围；
   - 提高代码的可维护性和健壮性。

因此，本题的关键不是单纯把 `public` 改成 `private`，而是通过 Getter/Setter 和参数校验，实现对对象状态的有效保护。

### 参考代码

```java
public class Patient {
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
