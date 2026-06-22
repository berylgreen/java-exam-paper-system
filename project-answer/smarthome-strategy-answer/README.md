# 接口与策略模式 (智能家居)

智能家居系统在处理设备的不同状态或不同业务类型时，原先通过冗长的 `switch-case` 语句进行分支判断。这样的实现方式会导致：当新增一种业务类型时，必须修改核心处理类，系统扩展性较差。

请使用**策略模式（Strategy Pattern）**对该逻辑进行重构，要求如下：
1. 定义一个策略接口 `DeviceStrategy`，用于抽象设备处理行为；
2. 针对不同的业务类型，分别创建实现该接口的具体策略类；
3. 定义上下文类，通过动态注入策略对象来完成设备处理；
4. 用策略模式替代原有的 `switch-case` 分支逻辑，体现“开闭原则”。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`智能灯`应用策略A
- 对`智能空调`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 智能灯
切换到策略B
执行策略B：处理 智能空调
```

---

## 解决方案

策略模式的核心思想是：**将不同的处理算法封装为独立的策略类，并通过统一接口进行调用**，从而避免在核心业务类中编写大量的条件分支。

### 本题实现说明
1. **定义策略接口 `DeviceStrategy`**  
   该接口声明统一的处理方法 `execute()`，表示所有设备处理策略都必须具备相同的调用方式。

2. **定义具体策略类**  
   例如 `NormalStrategy` 和 `UrgentStrategy`，分别表示不同业务类型下的处理逻辑。以后如果新增策略，只需新增实现类即可。

3. **定义上下文类 `DeviceProcessor`**  
   上下文类中维护一个 `DeviceStrategy` 类型的成员变量，并通过 `setStrategy()` 方法动态注入具体策略，再通过 `process()` 方法执行对应逻辑。

4. **替代 `switch-case`**  
   原先可能需要根据业务类型写成：
   ```java
   switch(type) {
       case "normal":
           // 普通处理
           break;
       case "urgent":
           // 紧急处理
           break;
       // 更多分支...
   }
   ```
   使用策略模式后，不再需要频繁修改核心处理类，而是通过切换不同策略对象来完成不同业务逻辑。

### 优点分析
- **符合开闭原则**：新增业务类型时，只需扩展新的策略类，无需修改原有核心代码；
- **降低耦合度**：具体业务逻辑与调用方解耦；
- **提高可维护性**：每种业务逻辑独立封装，结构清晰；
- **便于测试与扩展**：每个策略类都可以单独测试。

因此，本题中使用策略模式可以很好地解决智能家居系统中因业务分支不断增加而导致的 `switch-case` 臃肿问题。

### 参考代码

```java
// 策略接口：定义统一的设备处理行为
interface DeviceStrategy {
    void execute();
}

// 具体策略：普通处理策略
class NormalStrategy implements DeviceStrategy {
    @Override
    public void execute() {
        System.out.println("Normal processing");
    }
}

// 具体策略：紧急处理策略
class UrgentStrategy implements DeviceStrategy {
    @Override
    public void execute() {
        System.out.println("Urgent processing");
    }
}

// 上下文类：负责持有并调用具体策略
class DeviceProcessor {
    private DeviceStrategy strategy;

    public void setStrategy(DeviceStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy has not been set.");
        }
        strategy.execute();
    }
}

// 测试示例
public class Main {
    public static void main(String[] args) {
        DeviceProcessor processor = new DeviceProcessor();

        processor.setStrategy(new NormalStrategy());
        processor.process();

        processor.setStrategy(new UrgentStrategy());
        processor.process();
    }
}
```
