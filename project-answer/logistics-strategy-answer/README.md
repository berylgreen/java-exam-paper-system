# 接口与策略模式 (物流系统)

物流系统在处理包裹业务时，原有代码使用了大量 `switch-case` 来区分不同处理类型，导致代码难以维护，并且每次新增业务类型都需要修改核心处理类。

请使用**策略模式**对该设计进行优化，要求如下：
1. 定义一个 `PackageStrategy` 接口，用于描述包裹处理策略的统一行为。
2. 针对不同的业务类型，分别编写具体策略类并实现该接口。
3. 编写一个上下文类 `PackageProcessor`，通过动态注入策略对象来完成包裹处理。
4. 给出示例代码，展示如何分别处理普通包裹和加急包裹。

要求体现：将原先依赖 `switch-case` 的分支处理方式，改为基于策略对象的可扩展设计。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 对`电子产品包裹`应用策略A
- 对`书籍包裹`应用策略B

**预期输出示例：**
```text
切换到策略A
执行策略A：处理 电子产品包裹
切换到策略B
执行策略B：处理 书籍包裹
```

---

## 解决方案

```java
// 原来的做法通常类似：
class OldPackageProcessor {
    public void process(String type) {
        switch (type) {
            case "normal":
                System.out.println("普通包裹处理流程");
                break;
            case "urgent":
                System.out.println("加急包裹优先处理流程");
                break;
            default:
                throw new IllegalArgumentException("未知包裹类型");
        }
    }
}
```

上述写法的问题是：
1. 处理逻辑集中在一个类中，分支过多时可读性差。
2. 每新增一种业务类型，都要修改原有核心类，违反**开闭原则**。
3. 不同业务逻辑难以独立维护、测试和复用。

使用策略模式后：
1. `PackageStrategy` 接口定义统一行为，体现“面向接口编程”。
2. `NormalPackageStrategy`、`UrgentPackageStrategy` 等具体策略类分别封装各自业务逻辑。
3. `PackageProcessor` 作为上下文类，不再关心具体是哪一种包裹，只负责调用当前注入的策略。
4. 若以后增加“冷链包裹”“国际包裹”等类型，只需新增对应策略类，而无需修改 `PackageProcessor` 的核心代码。

这种设计将“选择哪种处理方式”和“具体如何处理”分离开来，能够有效替代冗长的 `switch-case`，提高系统的可扩展性与可维护性。

### 参考代码

```java
// Package.java
package com.exam.logistics;

public class Package {
    public String id;
    public String name;
    public double value;
    
    public Package() {}
    
    public Package(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Package{id='" + id + "', name='" + name + "'}";
    }
}

```

```java
// Main.java
package com.exam.logistics;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Context context = new Context();
        System.out.println("切换到策略A");
        context.setStrategy(new StrategyA());
        context.executeStrategy("电子产品包裹");
        System.out.println("切换到策略B");
        context.setStrategy(new StrategyB());
        context.executeStrategy("书籍包裹");
    }
}

```

```java
// BusinessHandler.java
package com.exam.logistics;

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
package com.exam.logistics;
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
package com.exam.logistics;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

