# 多态与继承 (物流系统)

在某物流系统中，原有的“包裹统计/处理”模块写成了一个庞大的类，内部通过大量 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分不同类型的包裹对象，并执行对应的处理逻辑。这样的实现可读性差、扩展性弱，后续新增包裹类型时还需要频繁修改原有代码。

请使用**面向对象中的多态思想**对该设计进行重构，完成以下任务：

1. 定义一个通用的 `Package` 抽象类或接口，声明统一的处理方法。
2. 创建若干具体包裹类型（如普通包裹、加急包裹、冷链包裹等），分别实现各自的处理逻辑。
3. 编写一个统计/处理类，接收一组 `Package` 对象并统一调用其方法处理，**不得再使用 `instanceof` 或类型分支判断**。

要求：
- 体现“统一抽象 + 子类重写 + 父类引用调用子类方法”的多态机制；
- 代码结构清晰，能够说明如何通过多态消除原有的类型判断；
- 可自行设计方法名，但语义应明确。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础包裹对象：`电子产品包裹`
- 创建高级包裹对象：`书籍包裹`
- 使用统一方法遍历调用处理逻辑

**预期输出示例：**
```text
统一处理基础包裹：电子产品包裹
统一处理高级包裹：书籍包裹
```

---

## 解决方案

本题的核心是用**多态**替代大量 `instanceof` 判断。

### 1. 原有问题
如果在统计类中这样写：

```java
if (obj instanceof RegularPackage) {
    // ...
} else if (obj instanceof ExpressPackage) {
    // ...
} else if (obj instanceof ColdChainPackage) {
    // ...
}
```

会带来以下问题：
- 代码冗长，可读性差；
- 每新增一种包裹类型，都要修改原有统计类；
- 不符合“开闭原则”，即**对扩展开放、对修改关闭**。

### 2. 多态重构思路
重构时先提取父类（或接口）`Package`，在其中声明统一的抽象方法 `process()`。然后让不同包裹子类分别重写该方法，实现各自的业务逻辑。

这样，处理类只需要面向父类 `Package` 编程：

```java
for (Package p : packages) {
    p.process();
}
```

虽然循环中变量类型是父类 `Package`，但在运行时会根据对象的真实类型，自动调用对应子类的 `process()` 方法，这就是多态。

### 3. 这种设计的优点
- **消除了类型判断**：不再需要 `instanceof`；
- **便于扩展**：如果以后新增 `FragilePackage`（易碎包裹），只需新增一个子类并重写 `process()`，不必修改 `PackageStatistics`；
- **结构更清晰**：每种包裹的处理逻辑放在自己的类中，职责明确。

### 4. 总结
本题体现了面向对象设计中的典型做法：
- 抽象公共行为；
- 子类各自实现差异化逻辑；
- 调用方依赖抽象类型，通过多态完成统一调用。

这正是使用多态优化条件分支、提升系统可维护性的常见方式。

### 参考代码

```java
// StandardPackage.java
package com.exam.logistics;
public class StandardPackage implements PackageProcessor {
    private String name;
    public StandardPackage(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础包裹：" + name); }
}

```

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
        PackageProcessor[] processors = { new StandardPackage("电子产品包裹"), new FragilePackage("书籍包裹") };
        for (PackageProcessor p : processors) { p.process(); }
    }
}

```

```java
// FragilePackage.java
package com.exam.logistics;
public class FragilePackage implements PackageProcessor {
    private String name;
    public FragilePackage(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级包裹：" + name); }
}

```

```java
// PackageProcessor.java
package com.exam.logistics;
public interface PackageProcessor { void process(); }

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

