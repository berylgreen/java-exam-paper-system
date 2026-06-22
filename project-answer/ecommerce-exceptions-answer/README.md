# 异常处理 (电商系统)

电商系统在批量解析外部订单数据时，如果某条数据格式错误，不应因为未捕获异常而导致整个解析流程中断。

请编写程序完成以下要求：
1. 自定义一个受检异常 `OrderException`，用于表示订单数据格式不合法。
2. 在订单解析过程中，使用 `try-catch` 结构处理可能出现的异常。
3. 当某条订单数据异常时，输出错误日志并跳过该条数据，继续处理后续订单。

说明：可将 `null`、空字符串等情况视为格式错误。

## 测试数据示例
请在 `Main` 类中使用以下包含错误的数据进行解析演示：
- 数据1：正常格式
- 数据2：错误格式 (触发异常)
- 数据3：正常格式

### 预期输出示例
```text
解析成功：数据1
捕获异常：格式错误，跳过该条数据
解析成功：数据3
全部数据处理完毕
```

---

## 解决方案

本题考查**自定义受检异常**与**异常处理流程控制**。

1. `OrderException` 继承 `Exception`，因此它属于**受检异常**，调用方必须显式处理或继续抛出。
2. 将单条订单的解析逻辑封装到 `parse()` 方法中，并在方法签名中使用 `throws OrderException` 声明可能抛出的异常。
3. 在批量处理 `parseList()` 中，通过 `try-catch` 捕获每一条数据解析时产生的异常。这样即使某一条订单有问题，也只会影响当前数据，不会终止整个循环。
4. 使用 `System.err.println()` 输出错误信息，体现“记录错误并继续执行”的处理思路。

这种写法比直接抛出未处理的 `RuntimeException` 更安全，能够提高系统的健壮性和容错能力，适合批量数据处理场景。

### 参考代码

```java
// Main.java
package com.exam.ecommerce;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DataParser parser = new DataParser();
        String[] data = {"数据1", "error", "数据3"}; 
        parser.parseData(data);
    }
}

```

```java
// DataParser.java
package com.exam.ecommerce;
public class DataParser {
    public void parseData(String[] data) {
        for (String item : data) {
            try {
                if ("error".equals(item)) throw new CustomException("格式错误");
                System.out.println("解析成功：" + item);
            } catch (CustomException e) {
                System.out.println("捕获异常：" + e.getMessage() + "，跳过该条数据");
            }
        }
        System.out.println("全部数据处理完毕");
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
// CustomException.java
package com.exam.ecommerce;
public class CustomException extends Exception { public CustomException(String m) { super(m); } }

```

