# 多态与继承 (智能家居)

在智能家居系统中，原有统计模块通过大量 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分并处理不同类型的设备对象，这种写法不利于维护和扩展。

请使用面向对象中的**多态**思想完成重构，要求如下：
1. 定义一个通用的 `Device` 抽象类或接口，并声明统一的处理方法。
2. 编写若干具体设备类，分别实现各自的处理逻辑。
3. 编写统计类，在不使用 `instanceof` 或类型强制转换的前提下，统一处理所有设备对象。

请给出完整示例代码，并说明这种重构方式的优点。


---

## 解决方案

本题的核心是用**多态**替代大量的类型判断。

### 设计思路
1. 定义抽象父类 `Device`，并声明统一的抽象方法 `process()`。
2. 不同设备类型继承 `Device`，分别重写 `process()`，实现各自特有的处理逻辑。
3. 在统计类 `DeviceStatistics` 中，只面向父类 `Device` 编程，遍历设备数组时直接调用 `device.process()`。

### 为什么不再需要 `instanceof`
当程序执行 `device.process()` 时，Java 会根据对象的实际类型动态绑定到对应子类的方法：
- `LightDevice` 调用自己的 `process()`
- `AirConditionerDevice` 调用自己的 `process()`
- `SecurityDevice` 调用自己的 `process()`

因此，调用方无需关心对象具体属于哪一种设备，也就不需要写：

```java
if (obj instanceof LightDevice) {
    ...
} else if (obj instanceof AirConditionerDevice) {
    ...
} else if (obj instanceof SecurityDevice) {
    ...
}
```

### 这种重构方式的优点
- **提高可维护性**：避免冗长的条件分支，代码结构更清晰。
- **便于扩展**：新增设备类型时，只需新增一个子类并重写 `process()`，无需修改统计类。
- **符合开闭原则**：对扩展开放、对修改关闭。
- **体现面向对象思想**：将“做什么”交给对象自身完成，而不是由外部统一判断类型后处理。

### 总结
本题通过“抽象父类 + 子类重写 + 父类引用调用”的方式，实现了利用多态统一处理不同设备对象，成功消除了 `instanceof` 带来的类型判断逻辑。

### 参考代码

```java
abstract class Device {
    public abstract void process();
}

class LightDevice extends Device {
    @Override
    public void process() {
        System.out.println("统计并处理照明设备数据");
    }
}

class AirConditionerDevice extends Device {
    @Override
    public void process() {
        System.out.println("统计并处理空调设备数据");
    }
}

class SecurityDevice extends Device {
    @Override
    public void process() {
        System.out.println("统计并处理安防设备数据");
    }
}

class DeviceStatistics {
    public void processAll(Device[] devices) {
        for (Device device : devices) {
            device.process();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Device[] devices = {
            new LightDevice(),
            new AirConditionerDevice(),
            new SecurityDevice()
        };

        DeviceStatistics statistics = new DeviceStatistics();
        statistics.processAll(devices);
    }
}
```
