# 异常处理 (电商系统)

电商系统在批量解析外部订单数据时，如果某条数据格式错误，不应因为未捕获异常而导致整个解析流程中断。

请编写程序完成以下要求：
1. 自定义一个受检异常 `OrderException`，用于表示订单数据格式不合法。
2. 在订单解析过程中，使用 `try-catch` 结构处理可能出现的异常。
3. 当某条订单数据异常时，输出错误日志并跳过该条数据，继续处理后续订单。

说明：可将 `null`、空字符串等情况视为格式错误。


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
class OrderException extends Exception {
    public OrderException(String message) {
        super(message);
    }
}

public class OrderParser {

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parse(item);
            } catch (OrderException e) {
                System.err.println("订单解析失败：" + e.getMessage());
            }
        }
    }

    private void parse(String item) throws OrderException {
        if (item == null || item.trim().isEmpty()) {
            throw new OrderException("订单数据为空或格式不合法");
        }

        System.out.println("解析成功：" + item);
    }
}
```
