# 多态与继承 (图书馆系统)

某图书馆系统中有一个体积庞大的统计类，它通过大量 `if (obj instanceof X) ... else if (obj instanceof Y) ...` 语句，分别处理不同类型的图书对象。这种写法会导致代码难以维护，也不利于后续扩展新的图书类型。

**请使用面向对象中的多态思想对其进行重构，要求如下：**
1. 定义一个通用的 `Book` 抽象类或接口，并声明统一的处理方法。
2. 创建若干具体图书子类，在子类中分别实现各自的处理逻辑。
3. 编写统计类，遍历图书对象时直接调用统一方法，彻底消除 `instanceof` 和分支类型判断。

**要求说明：**
- 需要体现“将变化的行为分散到各个子类中”的设计思想。
- 统计类只面向 `Book` 父类型编程，不依赖任何具体子类类型。


---

## 解决方案

本题的核心是用**多态**替代大量的 `instanceof` 类型判断。

### 设计思路
1. 将不同图书共有的“可被统计处理”行为提取到父类 `Book` 中，定义统一的抽象方法 `process()`。
2. 各个具体图书类型（如 `RegularBook`、`VIPBook`、`ReferenceBook`）分别重写 `process()`，实现各自特有的处理逻辑。
3. 在统计类 `BookStatistics` 中，只需要面向 `Book` 类型编程，遍历时直接调用 `book.process()`，由 Java 的动态绑定机制决定实际执行哪个子类的方法。

### 这样重构的优点
- **消除分支判断**：不再需要 `if...else if...` 或 `instanceof`。
- **便于扩展**：如果将来新增一种图书类型，只需新增一个子类并实现 `process()`，无需修改统计类。
- **符合开闭原则**：对扩展开放，对修改关闭。
- **提高可维护性**：各类图书的逻辑放在各自子类中，职责更加清晰。

### 与原有写法的本质区别
原有写法通常类似：

```java
if (obj instanceof RegularBook) {
    // 处理普通图书
} else if (obj instanceof VIPBook) {
    // 处理 VIP 图书
} else if (obj instanceof ReferenceBook) {
    // 处理参考图书
}
```

这种方式每增加一种新图书类型，都要修改原有统计类。

重构后变为：

```java
for (Book book : books) {
    book.process();
}
```

调用代码保持不变，扩展新类型时只需增加新的子类实现即可。这正是面向对象多态在实际开发中的典型应用。

### 参考代码

```java
abstract class Book {
    // 统一的处理方法，由各子类分别实现
    public abstract void process();
}

class RegularBook extends Book {
    @Override
    public void process() {
        System.out.println("处理普通图书的统计逻辑");
    }
}

class VIPBook extends Book {
    @Override
    public void process() {
        System.out.println("处理 VIP 图书的统计逻辑");
    }
}

class ReferenceBook extends Book {
    @Override
    public void process() {
        System.out.println("处理参考图书的统计逻辑");
    }
}

public class BookStatistics {
    public void processAll(Book[] books) {
        for (Book book : books) {
            book.process(); // 利用多态调用实际子类的方法
        }
    }

    public static void main(String[] args) {
        Book[] books = {
            new RegularBook(),
            new VIPBook(),
            new ReferenceBook()
        };

        BookStatistics statistics = new BookStatistics();
        statistics.processAll(books);
    }
}
```
