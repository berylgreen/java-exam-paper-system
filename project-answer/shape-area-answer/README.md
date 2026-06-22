# 多态与继承 (形状面积计算器)

现有一个用于计算图形面积的程序，最初在 `ShapeCalculator` 中通过一组冗长的 `if-else if` 语句分别判断 `Circle`、`Rectangle` 和 `Triangle` 的类型，再执行对应的面积计算逻辑。这种做法在新增图形类型时需要频繁修改原有代码，不利于程序扩展，违反了开闭原则。

请按照面向对象设计思想对程序进行重构，完成以下任务：

1. 定义一个抽象类 `Shape`，其中声明抽象方法 `double calculateArea()`，用于计算图形面积。
2. 编写 `Circle`、`Rectangle` 和 `Triangle` 三个具体图形类，使其继承 `Shape` 并分别实现 `calculateArea()` 方法。
3. 重构 `ShapeCalculator` 类，使其不再依赖 `if-else if` 判断图形类型，而是接收 `Shape[]` 数组，并通过多态统一调用各图形对象的 `calculateArea()` 方法来计算总面积。
4. 在 `Main` 类中创建若干图形对象，测试各图形面积及总面积的计算结果。

要求：程序结构清晰，能够体现“面向抽象编程”和“使用多态替代条件分支”的设计思想。

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础图形对象：`圆形`
- 创建高级图形对象：`矩形`
- 使用统一方法遍历调用处理逻辑

**预期输出示例：**
```text
统一处理基础图形：圆形
统一处理高级图形：矩形
```

---

## 解决方案

本题核心是使用**继承 + 抽象类 + 多态**来替代原先基于类型判断的 `if-else if` 结构。

### 1. 为什么原写法不合理
如果 `ShapeCalculator` 中直接写成如下思路：

```java
if (shape instanceof Circle) {
    // 计算圆面积
} else if (shape instanceof Rectangle) {
    // 计算矩形面积
} else if (shape instanceof Triangle) {
    // 计算三角形面积
}
```

那么每增加一种新图形（如 `Ellipse`、`Square`），都必须修改 `ShapeCalculator` 的代码。这说明系统**对扩展不开放、对修改不关闭**，违反了开闭原则。

### 2. 重构后的设计思路
- 抽象类 `Shape` 统一规定所有图形都必须具备 `calculateArea()` 方法。
- `Circle`、`Rectangle`、`Triangle` 分别给出各自的面积实现。
- `ShapeCalculator` 不关心传入对象到底是哪一种图形，只需调用：

```java
shape.calculateArea()
```

这就是多态的体现：**同一方法调用，因对象实际类型不同而执行不同实现**。

### 3. 各类功能说明
- `Shape`：定义统一的面积计算规范。
- `Circle`：面积公式为 `πr²`。
- `Rectangle`：面积公式为 `width * height`。
- `Triangle`：面积公式为 `0.5 * base * height`。
- `ShapeCalculator`：遍历 `Shape[]` 数组，累加每个图形的面积。
- `Main`：负责创建对象并验证结果。

### 4. 这样设计的优点
1. **符合开闭原则**：新增图形时，只需新增一个继承 `Shape` 的类并实现 `calculateArea()`，无需修改 `ShapeCalculator`。
2. **降低耦合**：计算器依赖抽象 `Shape`，而不是具体图形类。
3. **代码更清晰**：每个类只负责自己的面积计算逻辑，职责单一。
4. **便于维护和扩展**：程序结构更符合面向对象设计思想。

### 5. 总结
本题通过将“面积计算逻辑”分散到各具体图形类中，实现了从“条件分支判断类型”到“多态调用行为”的重构。这是面向对象中非常典型的优化方式。

### 参考代码

```java
// Main.java
package com.exam.shape;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        ShapeProcessor[] processors = { new Circle("圆形"), new Rectangle("矩形") };
        for (ShapeProcessor p : processors) { p.process(); }
    }
}

```

```java
// Circle.java
package com.exam.shape;
public class Circle implements ShapeProcessor {
    private String name;
    public Circle(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础图形：" + name); }
}

```

```java
// ShapeProcessor.java
package com.exam.shape;
public interface ShapeProcessor { void process(); }

```

```java
// FileStorage.java
package com.exam.shape;

public class FileStorage {
    public void saveRecord(String id, String content) {
        // TODO: 完善此方法，使用 try-with-resources 写入文件
    }
}

```

```java
// Rectangle.java
package com.exam.shape;
public class Rectangle implements ShapeProcessor {
    private String name;
    public Rectangle(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级图形：" + name); }
}

```

```java
// Shape.java
package com.exam.shape;

public class Shape {
    public String id;
    public String name;
    public double value;
    
    public Shape() {}
    
    public Shape(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Shape{id='" + id + "', name='" + name + "'}";
    }
}

```

