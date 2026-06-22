# 接口与策略模式 (租车系统)

某租车系统在处理车辆业务时，原先通过冗长的 `switch-case` 或 `if-else` 来区分不同业务类型，导致代码难以维护：每新增一种业务类型，都需要修改核心处理类。

请使用**策略模式**对该场景进行重构，要求如下：
1. 定义一个策略接口 `VehicleStrategy`，用于抽象不同车辆业务的处理行为。
2. 针对不同业务类型，分别编写具体策略类并实现该接口。
3. 编写一个上下文类，在运行时动态注入具体策略对象，并调用对应策略完成业务处理。
4. 通过示例代码说明如何替换原有的 `switch-case` 逻辑。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`丰田卡罗拉`应用策略A
- 对`本田雅阁`应用策略B

### 预期输出示例
```text
切换到策略A
执行策略A：处理 丰田卡罗拉
切换到策略B
执行策略B：处理 本田雅阁
```

---

## 解决方案

```java
// 传统写法（示意）
/*
public void process(String type) {
    switch (type) {
        case "normal":
            System.out.println("普通车辆业务处理");
            break;
        case "urgent":
            System.out.println("加急车辆业务处理");
            break;
        default:
            throw new IllegalArgumentException("未知业务类型");
    }
}
*/
```

使用策略模式后，不同业务逻辑被封装到各自独立的策略类中，上下文类只负责持有和调用策略对象，不再依赖大量分支判断。

其优点包括：
1. **符合开闭原则**：新增业务类型时，只需增加新的策略类，一般不需要修改原有核心处理代码。
2. **降低耦合度**：将“选择哪种业务逻辑”和“具体如何处理”分离。
3. **便于维护与扩展**：各个策略职责单一，代码结构更清晰。
4. **便于测试**：每个具体策略类都可以单独测试。

本题中：
- `VehicleStrategy` 是策略接口，定义统一行为；
- `NormalStrategy`、`UrgentStrategy` 是具体策略，实现不同业务处理方式；
- `VehicleProcessor` 是上下文类，负责在运行时使用具体策略；
- 客户端通过 `setStrategy()` 动态切换策略，从而替代原来的 `switch-case`。

### 参考代码

```java
// 策略接口：定义统一的业务处理方法
interface VehicleStrategy {
    void execute();
}

// 具体策略1：普通业务处理
class NormalStrategy implements VehicleStrategy {
    @Override
    public void execute() {
        System.out.println("普通车辆业务处理");
    }
}

// 具体策略2：加急业务处理
class UrgentStrategy implements VehicleStrategy {
    @Override
    public void execute() {
        System.out.println("加急车辆业务处理");
    }
}

// 上下文类：持有策略对象，并在运行时调用具体策略
class VehicleProcessor {
    private VehicleStrategy strategy;

    public void setStrategy(VehicleStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("未设置车辆业务处理策略");
        }
        strategy.execute();
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        VehicleProcessor processor = new VehicleProcessor();

        // 处理普通业务
        processor.setStrategy(new NormalStrategy());
        processor.process();

        // 处理加急业务
        processor.setStrategy(new UrgentStrategy());
        processor.process();
    }
}
```
