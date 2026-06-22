# 面向对象与封装 (物流系统)

在物流系统中，核心实体 `Package` 类如果将属性声明为 `public`，外部代码就可以直接修改对象状态，从而可能造成数据不合法，影响系统中数据的一致性与安全性。

**任务要求：**  
(1) 将类中的属性由 `public` 改为 `private`，体现封装思想。  
(2) 为每个属性提供标准的 Getter 和 Setter 方法。  
(3) 在 Setter 方法中加入必要的数据合法性校验，例如：
   - 字符串类型属性不能为空或空白字符串；
   - 数值类型属性不能为负数。  
(4) 请给出一个符合要求的 `Package` 类实现。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 正常设置：`101`，`电子产品包裹`
- 异常设置：编号为空字符串
- 异常设置：数值为负数

**预期输出示例：**
```text
正常设置成功：id=101, 名称=电子产品包裹
设置编号失败：编号不能为空
设置数值失败：数值不能为负数
```

---

## 解决方案

本题考查面向对象中的**封装**思想。

`Package` 类中的属性如果直接定义为 `public`，外部对象就可以绕过任何限制直接访问和修改属性值，例如将 `id` 设为 `null`，或将 `amount` 设为负数。这会导致对象处于不合理状态。

优化后的实现有以下几个关键点：  

(1) **属性私有化**  
   使用 `private` 修饰属性，禁止外部直接访问成员变量，只能通过方法间接操作。  

(2) **提供 Getter/Setter 方法**  
   - Getter 用于安全地读取属性值；
   - Setter 用于在赋值时统一进行检查。  

(3) **在 Setter 中进行数据校验**  
   - `id` 是字符串，不能为 `null`，也不能是只包含空格的空字符串；
   - `amount` 是数值，不能小于 0。  

(4) **非法数据通过异常提示**  
   当传入不合法的数据时，使用 `IllegalArgumentException` 抛出异常，能够及时发现错误并阻止对象进入非法状态。

例如：

```java
Package pkg = new Package();
pkg.setId("PKG001");
pkg.setAmount(99.5);
```

如果写成：

```java
pkg.setId(null);
pkg.setAmount(-10);
```

程序会抛出异常，因为这些值不符合业务要求。

因此，本题的核心是通过 **private + Getter/Setter + 数据校验** 来提高类的安全性和可维护性。

### 参考代码

```java
// Package.java
package com.exam.logistics;
import java.util.Objects;
public class Package implements Comparable<Package> {
    private String id;
    private String name;
    private double value;
    public Package() {}
    public Package(String id, String name) { this.id = id; this.name = name; }
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
        Package that = (Package) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
    @Override
    public int compareTo(Package other) { return this.id.compareTo(other.id); }
    @Override
    public String toString() { return "Package{id='" + id + "', name='" + name + "'}"; }
}

```

```java
// Main.java
package com.exam.logistics;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Package obj = new Package();
        try {
            obj.setId("101");
            obj.setName("电子产品包裹");
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

