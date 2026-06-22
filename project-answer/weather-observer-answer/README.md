# 设计模式 (气象站通知系统)

某气象站系统 `WeatherStation` 在气象数据发生变化时，原先通过硬编码方式直接调用具体显示设备（如 `PhoneApp`、`DisplayPanel`）的方法。这种实现方式使 `WeatherStation` 与具体设备强耦合，导致系统难以扩展：每增加一种新设备，都需要修改气象站代码。

请使用**观察者模式（Observer Pattern）**对该系统进行重构，要求如下：

1. 定义观察者接口 `Observer`，包含方法：`void update(float temp, float humidity, float pressure)`。
2. 定义主题接口 `Subject`，至少包含以下方法：
   - `void registerObserver(Observer o)`
   - `void removeObserver(Observer o)`
   - `void notifyObservers()`
3. 重构 `WeatherStation` 类，使其实现 `Subject` 接口，并在类中维护一个 `List<Observer>` 类型的订阅者列表。
4. 让不同的显示设备（如 `PhoneApp`、`DisplayPanel`）实现 `Observer` 接口。
5. 编写测试代码，演示：
   - 观察者的动态注册；
   - 气象数据更新后自动通知所有已订阅设备；
   - 移除某个观察者后，该设备不再接收更新。

请给出完整实现代码。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础气象数据对象：`温度数据`
- 创建高级气象数据对象：`湿度数据`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础气象数据：温度数据
统一处理高级气象数据：湿度数据
```

---

## 解决方案

观察者模式的核心思想是：**让主题（Subject）主动通知所有依赖它的观察者（Observer）**，从而实现对象之间的松耦合。

### 1. 为什么原实现有问题
原来的 `WeatherStation` 直接调用 `PhoneApp`、`DisplayPanel` 等具体类的方法，说明它依赖的是**具体实现**而不是**抽象接口**。这样会带来两个问题：
- 新增一种显示设备时，必须修改 `WeatherStation` 的代码；
- `WeatherStation` 与各设备类耦合过紧，不利于维护和扩展。

### 2. 重构后的关键点
#### （1）定义 `Observer` 接口
所有显示设备都实现统一的 `update(...)` 方法，这样 `WeatherStation` 无需关心设备的具体类型。

#### （2）定义 `Subject` 接口
主题负责：
- 注册观察者；
- 移除观察者；
- 在状态变化时通知所有观察者。

#### （3）`WeatherStation` 维护观察者列表
通过 `List<Observer>` 保存所有订阅者，当调用 `setMeasurements(...)` 更新数据后，再统一执行 `notifyObservers()`。

### 3. 运行机制说明
以如下代码为例：

```java
station.registerObserver(phoneApp);
station.registerObserver(displayPanel);
station.setMeasurements(26.5f, 65.0f, 1012.3f);
```

执行流程为：
1. `phoneApp` 和 `displayPanel` 注册到 `station`；
2. `station` 更新内部气象数据；
3. `station.notifyObservers()` 遍历观察者列表；
4. 每个观察者都会收到 `update(temp, humidity, pressure)` 调用。

当执行：

```java
station.removeObserver(phoneApp);
```

之后，`phoneApp` 就不再出现在观察者列表中，因此后续数据更新时不会再收到通知。

### 4. 该设计的优点
- **降低耦合**：`WeatherStation` 只依赖 `Observer` 接口，不依赖具体设备类；
- **易于扩展**：新增设备时，只需实现 `Observer` 接口并注册即可；
- **符合开闭原则**：对扩展开放，对修改关闭。

### 5. 总结
本题通过观察者模式，将“气象数据发布者”和“数据显示设备”解耦。`WeatherStation` 只负责维护状态和通知观察者，而各观察者独立决定如何处理数据。这种设计是事件通知、消息订阅等场景中的经典做法。

### 参考代码

```java
// Main.java
package com.exam.weather;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        WeatherDataProcessor[] processors = { new TypeA("温度数据"), new TypeB("湿度数据") };
        for (WeatherDataProcessor p : processors) { p.process(); }
    }
}

```

```java
// TypeA.java
package com.exam.weather;
public class TypeA implements WeatherDataProcessor {
    private String name;
    public TypeA(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础气象数据：" + name); }
}

```

```java
// WeatherData.java
package com.exam.weather;

public class WeatherData {
    public String id;
    public String name;
    public double value;
    
    public WeatherData() {}
    
    public WeatherData(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "WeatherData{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// FileStorage.java
package com.exam.weather;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// WeatherDataProcessor.java
package com.exam.weather;
public interface WeatherDataProcessor { void process(); }

```

```java
// TypeB.java
package com.exam.weather;
public class TypeB implements WeatherDataProcessor {
    private String name;
    public TypeB(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级气象数据：" + name); }
}

```

