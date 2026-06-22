# 多态与继承 (教务系统)

某教务系统中有一个体量较大的统计类，当前通过大量 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句来区分并处理不同类型的学生对象。这种写法可读性较差，也不利于后续扩展。

请使用面向对象中的**多态**思想对其进行重构，要求如下：
1. 定义一个通用的 `Student` 抽象类或接口，声明统一的处理方法。
2. 为不同类型的学生创建具体子类，并在子类中实现各自的处理逻辑。
3. 重构统计类，使其只依赖 `Student` 抽象类型，通过多态完成调用，彻底去除所有 `instanceof` 类型判断。

要求给出核心代码实现，并体现“面向抽象编程”的设计思想。


---

## 解决方案

本题的核心是用**多态**替代大量的 `instanceof` 分支判断。

原来的问题通常类似下面这样：

```java
if (obj instanceof RegularStudent) {
    // ...
} else if (obj instanceof VIPStudent) {
    // ...
} else if (obj instanceof ExchangeStudent) {
    // ...
}
```

这种写法存在以下不足：
1. 代码分支过多，可读性和可维护性较差。
2. 每增加一种新的学生类型，都要修改原有统计类。
3. 违背了“对扩展开放、对修改关闭”的开闭原则。

重构后的思路如下：
1. 抽象出父类 `Student`，定义统一方法 `process()`。
2. 不同学生类型分别继承 `Student`，并重写 `process()` 实现自己的处理逻辑。
3. 统计类只面向 `Student` 编程，在遍历时直接调用 `student.process()`。

这样做的优点是：
- **消除类型判断**：不再需要 `instanceof`。
- **便于扩展**：如果新增一种学生类型，只需新增一个子类并实现 `process()`，原统计类代码无需修改。
- **体现多态特性**：同一方法调用，因对象实际类型不同而表现出不同的行为。

因此，本题的关键不在于写出很多判断语句，而在于通过“抽象 + 继承 + 重写 + 多态调用”实现更清晰、可扩展的面向对象设计。

### 参考代码

```java
abstract class Student {
    // 统一的处理行为，由子类分别实现
    public abstract void process();
}

class RegularStudent extends Student {
    @Override
    public void process() {
        System.out.println("处理普通学生的统计逻辑");
    }
}

class VIPStudent extends Student {
    @Override
    public void process() {
        System.out.println("处理 VIP 学生的统计逻辑");
    }
}

class ExchangeStudent extends Student {
    @Override
    public void process() {
        System.out.println("处理交换生的统计逻辑");
    }
}

public class StudentStatistics {
    public void processAll(Student[] students) {
        for (Student student : students) {
            student.process(); // 多态调用，无需 instanceof 判断
        }
    }

    public static void main(String[] args) {
        Student[] students = {
            new RegularStudent(),
            new VIPStudent(),
            new ExchangeStudent()
        };

        StudentStatistics statistics = new StudentStatistics();
        statistics.processAll(students);
    }
}
```
