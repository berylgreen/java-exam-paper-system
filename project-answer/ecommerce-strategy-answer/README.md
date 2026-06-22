# 接口与策略模式 (电商系统)

某电商系统在处理订单的不同业务类型（如普通订单、加急订单等）时，原先在核心处理类中使用了冗长的 `switch-case` 分支判断。这样会导致：当新增一种订单处理方式时，必须修改核心类代码，不利于扩展与维护。

请使用**策略模式（Strategy Pattern）**对该设计进行重构，要求如下：
1. 定义一个 `OrderStrategy` 策略接口，表示订单处理行为；
2. 针对不同的业务类型，分别创建实现该接口的具体策略类；
3. 定义一个订单处理上下文类，在运行时动态注入具体策略对象；
4. 通过策略模式替换原有的 `switch-case` 逻辑，体现“对扩展开放、对修改关闭”的设计思想。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`笔记本电脑`应用策略A
- 对`智能手机`应用策略B

**预期输出示例：**
```text
切换到策略A
执行策略A：处理 笔记本电脑
切换到策略B
执行策略B：处理 智能手机
```

---

## 解决方案

策略模式的核心思想是：**将可变的业务逻辑封装为独立的策略类，并通过统一接口对外提供服务**。

### 1. 原问题分析
如果在订单处理类中直接使用大量 `switch-case` 或 `if-else` 判断不同订单类型，例如：

```java
switch(orderType) {
    case "NORMAL":
        // 普通订单处理
        break;
    case "URGENT":
        // 加急订单处理
        break;
}
```

这种写法虽然直观，但存在明显缺点：
- 新增一种订单类型时，需要修改原有核心类；
- 业务逻辑集中在一个类中，职责不清晰；
- 分支过多时，可读性和可维护性较差。

### 2. 使用策略模式后的改进
通过策略模式：
- `OrderStrategy` 作为统一策略接口，定义订单处理的规范；
- `NormalOrderStrategy`、`UrgentOrderStrategy` 等类分别封装不同业务逻辑；
- `OrderProcessor` 作为上下文类，负责持有并调用具体策略；
- 客户端可以在运行时自由切换策略，而不需要修改处理器内部代码。

### 3. 设计优势
这种设计具有以下优点：
- **消除冗长条件分支**：避免大量 `switch-case`；
- **符合开闭原则**：新增策略时只需增加新类，不必修改原有核心处理代码；
- **便于维护与扩展**：每种订单处理逻辑独立封装，结构更清晰；
- **支持运行时切换行为**：可以根据实际业务动态注入不同策略。

### 4. 总结
本题通过策略模式将“订单处理方式”抽象为可替换的算法族，使系统在面对业务变化时更加灵活，是面向对象设计中替代复杂条件分支的典型做法。

### 参考代码

```java
// Main.java
package com.exam.ecommerce;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("笔记本电脑");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("智能手机");
    }
}

```

```java
// BusinessHandler.java
package com.exam.ecommerce;

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
// Order.java
package com.exam.ecommerce;

public class Order {
    public String id;
    public String name;
    public double value;
    
    public Order() {}
    
    public Order(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Order{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// StrategyContext.java
package com.exam.ecommerce;
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
package com.exam.ecommerce;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

