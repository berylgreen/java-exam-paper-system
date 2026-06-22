# 多态与继承 (智能家居)

在智能家居系统中，原有统计模块通过大量 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分并处理不同类型的设备对象，这种写法不利于维护和扩展。

请使用面向对象中的**多态**思想完成重构，要求如下：
1. 定义一个通用的 `Device` 抽象类或接口，并声明统一的处理方法。
2. 编写若干具体设备类，分别实现各自的处理逻辑。
3. 编写统计类，在不使用 `instanceof` 或类型强制转换的前提下，统一处理所有设备对象。

请给出完整示例代码，并说明这种重构方式的优点。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础设备对象：`智能灯`
- 创建高级设备对象：`智能空调`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础设备：智能灯
统一处理高级设备：智能空调
```

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
// DeviceProcessor.java
package com.exam.smarthome;
public interface DeviceProcessor { void process(); }

```

```java
// Controller.java
package com.exam.smarthome;
public class Controller implements DeviceProcessor {
    private String name;
    public Controller(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级设备：" + name); }
}

```

```java
// Main.java
package com.exam.smarthome;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DeviceProcessor[] processors = { new Sensor("智能灯"), new Controller("智能空调") };
        for (DeviceProcessor p : processors) { p.process(); }
    }
}

```

```java
// Sensor.java
package com.exam.smarthome;
public class Sensor implements DeviceProcessor {
    private String name;
    public Sensor(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础设备：" + name); }
}

```

```java
// FileStorage.java
package com.exam.smarthome;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// Device.java
package com.exam.smarthome;

public class Device {
    public String id;
    public String name;
    public double value;
    
    public Device() {}
    
    public Device(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Device{id='" + id + "', name='" + name + "'}";
    }
}

```

