# 多态与继承 (租车系统)

在当前租车系统中，统计模块存在一个庞大的处理类，内部使用了大量 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分不同类型的车辆对象并执行对应逻辑。这种实现方式可维护性较差，也不利于后续功能扩展。

**请使用面向对象中的多态思想对其进行重构，要求如下：**
1. 定义一个通用的 `Vehicle` 抽象类或接口，声明统一的业务处理方法。
2. 创建若干具体车辆子类，并在各自类中实现对应的处理逻辑。
3. 编写统计类，对一组车辆对象进行统一处理，不能再使用 `instanceof` 或类型强制转换来区分车辆类型。
4. 给出完整示例代码，体现“调用方只面向父类，具体行为由子类决定”的多态特性。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础车辆对象：`丰田卡罗拉`
- 创建高级车辆对象：`本田雅阁`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础车辆：丰田卡罗拉
统一处理高级车辆：本田雅阁
```

---

## 解决方案

```java
// 传统写法示意
if (obj instanceof RegularVehicle) {
    // 处理普通车辆
} else if (obj instanceof VIPVehicle) {
    // 处理VIP车辆
} else if (obj instanceof TruckVehicle) {
    // 处理货运车辆
}
```

上述写法的问题在于：
1. **耦合度高**：统计类必须知道所有具体车辆类型。
2. **扩展性差**：每新增一种车辆类型，都要修改原有的判断代码。
3. **不符合开闭原则**：对扩展开放、对修改关闭这一原则没有得到体现。

重构后的核心思想是把“不同车辆的不同处理逻辑”下放到各自子类中：
- 父类 `Vehicle` 定义统一的抽象方法 `process()`；
- 各子类分别重写 `process()`，实现自己的业务逻辑；
- 统计类 `VehicleStatistics` 只需要面向 `Vehicle` 编程，循环调用 `vehicle.process()` 即可。

这样做的优点是：
1. **消除了 `instanceof` 判断**，代码更简洁；
2. **体现了多态性**：同一个方法调用，根据对象实际类型表现出不同的行为；
3. **便于扩展**：如果新增 `BusVehicle` 类，只需继承 `Vehicle` 并实现 `process()`，统计类代码无需修改。

因此，本题的关键不只是“定义继承结构”，更重要的是通过**抽象 + 方法重写 + 父类引用指向子类对象**来实现真正的多态重构。

### 参考代码

```java
package com.exam.rental;
interface VehicleProcessor { void process(); }
class Car implements VehicleProcessor {
    private String name;
    public Car(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础车辆：" + name); }
}
class Truck implements VehicleProcessor {
    private String name;
    public Truck(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级车辆：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        VehicleProcessor[] processors = { new Car("丰田卡罗拉"), new Truck("本田雅阁") };
        for (VehicleProcessor p : processors) { p.process(); }
    }
}

```