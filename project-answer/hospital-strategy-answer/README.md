# 接口与策略模式 (医疗系统)

某医疗系统在处理病患业务时，原先通过冗长的 `switch-case` 来区分不同的处理类型，导致核心代码难以维护，并且每新增一种业务类型都需要修改原有逻辑。

请使用**策略模式**对该场景进行重构，要求如下：
1. 定义一个统一的策略接口 `PatientStrategy`，用于描述病患业务的处理行为；
2. 针对不同业务类型，分别编写实现该接口的具体策略类；
3. 编写上下文类，在运行时动态注入不同策略对象，从而替代原有的 `switch-case` 分支判断；
4. 给出示例代码，演示普通病患与紧急病患两种业务的处理方式。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`张三的病历`应用策略A
- 对`李四的病历`应用策略B

**预期输出示例：**
```text
切换到策略A
执行策略A：处理 张三的病历
切换到策略B
执行策略B：处理 李四的病历
```

---

## 解决方案

```java
// 策略模式的核心结构：
// 1. Strategy（策略接口）：定义统一的业务处理方法
// 2. ConcreteStrategy（具体策略类）：封装不同的业务实现
// 3. Context（上下文类）：持有策略对象，并在运行时调用对应策略
```

本题中，原有系统使用 `switch-case` 根据病患业务类型执行不同逻辑，这样会带来以下问题：
1. 核心类职责过重，既负责调度又负责实现具体业务；
2. 扩展性差，每增加一种病患处理方式都要修改原有代码；
3. 不符合“开闭原则”，即对扩展开放、对修改关闭。

使用策略模式后：
- `PatientStrategy` 作为统一接口，约定所有病患处理方式都必须实现 `process()` 方法；
- `NormalPatientStrategy` 和 `UrgentPatientStrategy` 分别封装不同业务逻辑；
- `PatientProcessor` 只负责持有并调用策略，不关心具体实现细节。

这样做的优点是：
1. 去除了冗长的条件分支；
2. 新增业务类型时，只需增加新的策略类即可；
3. 提高了代码的可维护性、可扩展性和可读性。

例如，若后续需要增加“VIP病患处理策略”，只需新增一个实现 `PatientStrategy` 的类，而不必修改 `PatientProcessor` 的核心代码。

### 参考代码

```java
// Main.java
package com.exam.hospital;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("张三的病历");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("李四的病历");
    }
}

```

```java
// BusinessHandler.java
package com.exam.hospital;

public class BusinessHandler {
    // 原始设计：随着业务增加，switch-case 越来越长，难以维护
    public void handle(String type, String data) {
        switch (type) {
            case "TYPE_A":
                System.out.println("执行策略A: 处理 " + data);
                break;
            case "TYPE_B":
                System.out.println("执行策略B: 处理 " + data);
                break;
            default:
                System.out.println("未知策略");
        }
    }
}

```

```java
// Patient.java
package com.exam.hospital;

public class Patient {
    public String id;
    public String name;
    public double value;
    
    public Patient() {}
    
    public Patient(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Patient{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// StrategyContext.java
package com.exam.hospital;
interface Strategy { void execute(String data); }
class StrategyA implements Strategy {
    @Override public void execute(String data) { System.out.println("执行策略A：处理 " + data); }
}
class StrategyB implements Strategy {
    @Override public void execute(String data) { System.out.println("执行策略B：处理 " + data); }
}
class Context {
    private Strategy strategy;
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }
    public void executeStrategy(String data) { if (strategy != null) strategy.execute(data); }
}

```

```java
// FileStorage.java
package com.exam.hospital;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

