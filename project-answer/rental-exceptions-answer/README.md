# 异常处理 (租车系统)

租车系统在批量解析外部车辆数据时，若某条数据格式不合法，不能因为抛出未处理异常而导致整个系统中断。

**任务要求：**
1. 定义一个受检异常 `VehicleException`，用于表示车辆数据解析错误。
2. 在批量解析方法中，使用 `try-catch` 捕获单条数据解析过程中可能出现的异常。
3. 当某条数据解析失败时，输出错误日志并跳过该条数据，继续处理后续数据。
4. 请给出核心代码示例，体现“单条出错不影响整体流程”的异常处理思想。


---

## 解决方案

```java
// 关键思路：
// 1. 自定义受检异常 VehicleException，表示“可预期的业务解析错误”。
// 2. 将单条数据的校验与解析封装到 parseVehicle() 方法中，并通过 throws 抛出异常。
// 3. 在批量处理 parseList() 中，对每条数据分别使用 try-catch。
// 4. 这样即使某一条数据异常，也只会影响当前这条记录，不会中断整个循环。
```

本题的重点是体现异常处理的健壮性：

1. **为什么使用受检异常 `VehicleException`**  
   受检异常继承自 `Exception`，调用者必须显式处理，能够提醒开发者注意数据解析过程中的异常情况，更适合这类可预期的业务错误。

2. **为什么不能直接抛出未处理的 `RuntimeException`**  
   如果在批量解析过程中直接抛出且不捕获运行时异常，程序可能在遇到第一条错误数据时就终止，无法继续处理后续合法数据。

3. **为什么要把 `try-catch` 放在循环内部**  
   若将 `try-catch` 放在整个循环外部，一旦某条数据出错，循环可能提前结束；放在循环内部则可以保证“当前条目出错，后续条目继续执行”。

4. **日志记录的意义**  
   捕获异常后输出错误信息，有助于排查是哪条数据出现问题。在真实项目中，通常会将日志写入文件、数据库或监控系统，而不仅仅是打印到控制台。

因此，该实现满足题目要求：定义了自定义受检异常、使用 `try-catch` 捕获解析错误、记录错误信息，并保证批量处理流程不中断。

### 参考代码

```java
class VehicleException extends Exception {
    public VehicleException(String message) {
        super(message);
    }
}

public class VehicleParser {

    public void parseList(String[] data) {
        for (String item : data) {
            try {
                parseVehicle(item);
                System.out.println("Parsed: " + item);
            } catch (VehicleException e) {
                System.err.println("车辆数据解析失败：" + e.getMessage());
                // 此处可进一步写入日志文件或监控系统
            }
        }
    }

    private void parseVehicle(String item) throws VehicleException {
        if (item == null || item.trim().isEmpty()) {
            throw new VehicleException("数据为空或格式非法");
        }

        // 这里可以补充更具体的解析逻辑
        // 例如按约定格式拆分字段、校验车辆编号、品牌、租金等
    }
}
```
