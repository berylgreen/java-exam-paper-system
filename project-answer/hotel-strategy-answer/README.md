# 接口与策略模式 (酒店系统)

酒店系统在处理房间相关业务时，原先将不同业务类型的处理逻辑集中写在冗长的 `switch-case` 中，导致代码难以维护，并且每次新增业务类型都需要修改核心处理类。

**请使用策略模式对该设计进行重构，要求如下：**
1. 定义 `RoomStrategy` 接口，统一不同业务处理策略的行为。
2. 针对不同业务类型分别编写实现类，例如普通入住处理、加急清洁处理等。
3. 定义上下文类，在运行时动态注入具体策略对象，替代原有的 `switch-case` 分支判断。
4. 给出一个简单的测试示例，展示如何切换不同策略并执行对应业务。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`总统套房`应用策略A
- 对`豪华大床房`应用策略B

**预期输出示例：**
```text
切换到策略A
执行策略A：处理 总统套房
切换到策略B
执行策略B：处理 豪华大床房
```

---

## 解决方案

```java
// 传统写法示意
switch (type) {
    case "NORMAL":
        // 普通入住处理
        break;
    case "URGENT":
        // 加急清洁处理
        break;
    default:
        // 其他处理
}
```

上述做法的问题在于：
1. 业务分支过多时，`switch-case` 会变得冗长，降低可读性。
2. 每增加一种新业务类型，都要修改原有核心类，违反“开闭原则”。
3. 不同业务逻辑耦合在同一个类中，不利于维护和扩展。

使用策略模式后：
1. 将每种业务逻辑封装为独立的策略类，并统一实现 `RoomStrategy` 接口。
2. 上下文类 `RoomProcessor` 不再关心具体业务实现，只负责持有并调用策略对象。
3. 运行时可以通过 `setStrategy()` 动态切换策略，增强程序灵活性。
4. 如果后续新增“延迟退房处理”“VIP服务处理”等业务，只需新增对应策略类，无需修改上下文核心逻辑。

因此，策略模式能够有效替代复杂的条件分支，提高代码的可维护性、扩展性和面向对象设计质量。```

### 参考代码

```java
// Main.java
package com.exam.hotel;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("总统套房");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("豪华大床房");
    }
}

```

```java
// Room.java
package com.exam.hotel;

public class Room {
    public String id;
    public String name;
    public double value;
    
    public Room() {}
    
    public Room(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Room{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// BusinessHandler.java
package com.exam.hotel;

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
// StrategyContext.java
package com.exam.hotel;
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
package com.exam.hotel;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

