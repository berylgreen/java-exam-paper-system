# 面向对象与封装 (银行系统)

在银行系统中，`Account` 类当前将成员属性直接声明为 `public`，这会导致外部代码可以随意读取和修改对象状态，从而影响数据的安全性与一致性。

请根据面向对象的封装思想，对 `Account` 类进行改造，要求如下：
(1) 将原本对外公开的属性改为 `private`。
(2) 为每个属性提供规范的 Getter 和 Setter 方法。
(3) 在 Setter 方法中加入必要的数据合法性校验：
   - 账户编号不能为 `null`，也不能为空字符串；
   - 账户余额不能为负数。

请编写改造后的 `Account` 类。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`张三的账户`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

**预期输出示例：**
```text
正常设置成功：id=101, 名称=张三的账户
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

本题考查面向对象中的**封装性**。

### 1. 为什么要将属性设为 `private`
如果属性直接声明为 `public`，类的外部就可以绕过任何限制，直接修改对象内部状态。例如可以把账户余额改成负数，或者把账户编号设为无效值，这会破坏对象数据的正确性。

将属性改为 `private` 后，外部不能直接访问成员变量，只能通过类提供的方法进行操作，从而实现对数据的统一管理。

### 2. Getter / Setter 的作用
- `Getter`：用于安全地读取属性值；
- `Setter`：用于修改属性值，并可在修改前加入校验逻辑。

这种写法既保留了对属性的访问能力，又增强了程序的健壮性。

### 3. 校验逻辑说明
在本题中：
- `id` 不能为 `null`，也不能是空字符串或只包含空格；
- `amount` 不能小于 0。

因此在 `setId()` 和 `setAmount()` 中，若传入非法数据，就通过抛出 `IllegalArgumentException` 的方式阻止错误赋值。

### 4. 体现的面向对象思想
该改造体现了“隐藏内部实现、控制外部访问”的封装思想，是设计安全、可靠类的重要方式。

例如，下面的调用是合法的：

```java
Account account = new Account();
account.setId("A1001");
account.setAmount(5000);
```

而下面的调用会抛出异常：

```java
account.setId("   ");
account.setAmount(-100);
```

这样可以有效保证 `Account` 对象始终处于合理状态。

### 参考代码

```java
// Main.java
package com.exam.bank;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Account obj = new Account();
        try {
            obj.setId("101");
            obj.setName("张三的账户");
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
// Account.java
package com.exam.bank;
import java.util.Objects;
public class Account implements Comparable<Account> {
    private String id;
    private String name;
    private double value;
    public Account() {}
    public Account(String id, String name) { this.id = id; this.name = name; }
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
        Account that = (Account) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Account other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Account{id='" + id + "', name='" + name + "'}"; }
}

```

