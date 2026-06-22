# 接口与策略模式 (图书馆系统)

某图书馆管理系统在处理图书业务时，原本通过冗长的 `switch-case` 判断不同业务类型（如普通借阅、加急处理、馆内阅览等）。这种做法会导致核心代码难以维护，并且每次新增一种业务类型时，都需要修改原有核心类，扩展性较差。

**任务要求：**  
(1) 使用策略模式对原有实现进行重构，定义统一的 `BookStrategy` 策略接口。  
(2) 针对不同的图书业务类型，分别创建实现该接口的具体策略类。  
(3) 定义上下文类，在运行时动态注入具体策略对象，替代原有的 `switch-case` 分支逻辑。  
(4) 给出一个简单示例，展示如何切换不同策略来处理图书业务。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`Java编程思想`应用策略A
- 对`算法导论`应用策略B

**预期输出示例：**
```text
切换到策略A
执行策略A：处理 Java编程思想
切换到策略B
执行策略B：处理 算法导论
```

---

## 解决方案

```java
// 策略模式的核心思想：
// 将“不同业务处理方式”封装为独立的策略类，
// 让上下文类在运行时选择并调用具体策略，
// 从而避免大量的 if-else 或 switch-case。
```

本题中：  

(1) **`BookStrategy` 接口**
   - 统一定义图书业务处理方法 `handle(String bookName)`。
   - 所有具体业务策略都必须实现该接口。  

(2) **具体策略类**
   - `NormalBorrowStrategy`：处理普通借阅。
   - `UrgentBorrowStrategy`：处理加急借阅。
   - `ReadingRoomStrategy`：处理馆内阅览。
   - 每种业务逻辑独立封装，便于维护和扩展。  

(3) **上下文类 `BookContext`**
   - 持有一个 `BookStrategy` 类型的引用。
   - 通过 `setStrategy()` 方法在运行时动态切换策略。
   - `process()` 方法不再关心具体是哪种业务，只负责调用当前策略。  

(4) **相对于 `switch-case` 的优势**
   - **符合开闭原则**：新增业务类型时，只需增加新的策略类，不需要修改原有核心处理代码。
   - **降低耦合度**：业务逻辑与上下文分离。
   - **提高可维护性**：每个策略职责单一，结构更清晰。

因此，使用策略模式后，系统能够更灵活地应对图书业务类型的变化，避免因频繁修改核心类而带来的维护问题。

### 参考代码

```java
// Main.java
package com.exam.library;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("Java编程思想");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("算法导论");
    }
}

```

```java
// BusinessHandler.java
package com.exam.library;

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
// Book.java
package com.exam.library;

public class Book {
    public String id;
    public String name;
    public double value;
    
    public Book() {}
    
    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Book{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// StrategyContext.java
package com.exam.library;
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
package com.exam.library;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

