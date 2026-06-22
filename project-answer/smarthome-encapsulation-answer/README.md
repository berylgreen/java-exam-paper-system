# 面向对象与封装 (智能家居)

在智能家居系统中，核心实体类 `Device` 原先将属性声明为 `public`，这会导致外部代码可以直接修改对象状态，从而破坏数据的安全性与一致性。

请根据面向对象中“封装”的思想，对 `Device` 类进行改造，要求如下：
1. 将原本对外公开的属性改为 `private`；
2. 为这些属性提供规范的 Getter 和 Setter 方法；
3. 在 Setter 方法中加入必要的数据合法性校验，例如：
   - 字符串属性不能为 `null`，也不能为空字符串；
   - 数值属性不能为负数。

请给出改造后的 `Device` 类代码实现。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`智能灯`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

### 预期输出示例
```text
正常设置成功：id=101, 名称=智能灯
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

```java
public class Device {
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

本题考查面向对象中的**封装**思想，核心改造点如下：

1. **属性私有化**  
   将 `id` 和 `amount` 声明为 `private`，可以防止外部对象直接访问和随意修改成员变量。

2. **通过方法控制访问**  
   使用 Getter 方法读取属性值，使用 Setter 方法修改属性值，这样类可以统一控制数据的读写过程。

3. **在 Setter 中进行合法性校验**  
   - `id` 不能为 `null`，也不能是空字符串或只包含空格；
   - `amount` 不能小于 0。  
   如果传入非法数据，抛出 `IllegalArgumentException`，可以及时发现错误并阻止无效状态进入对象。

4. **封装的意义**  
   封装不仅是“把属性设为 private”，更重要的是通过公开的方法对数据进行约束，保证对象始终处于合理、可用的状态。

因此，该实现既满足了题目对访问权限的要求，也体现了封装与数据校验的基本设计思想。

### 参考代码

```java
// Main.java
package com.exam.smarthome;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Device obj = new Device();
        try {
            obj.setId("101");
            obj.setName("智能灯");
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
// Device.java
package com.exam.smarthome;
import java.util.Objects;
public class Device implements Comparable<Device> {
    private String id;
    private String name;
    private double value;
    public Device() {}
    public Device(String id, String name) { this.id = id; this.name = name; }
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
        Device that = (Device) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Device other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Device{id='" + id + "', name='" + name + "'}"; }
}

```

