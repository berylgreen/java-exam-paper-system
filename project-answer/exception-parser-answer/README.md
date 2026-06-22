# 异常处理 (数据解析系统)

系统中提供了一个数据解析工具 `DataParser`，用于将外部传入的字符串数组解析为整数成绩。但目前该工具健壮性较差：当数组中出现 `null` 或非数字字符串时，程序可能因异常而中断。

请根据异常处理机制完善该程序，要求如下：

1. 自定义一个受检异常 `InvalidDataException`，用于表示非法数据。
2. 在 `DataParser` 的 `parseScores` 方法中，使用 `try-catch` 捕获解析过程中可能出现的 `NumberFormatException` 和 `NullPointerException`。
3. 当遇到非法数据时，可将底层异常转换为 `InvalidDataException`，并在外层进行处理；或者直接记录错误信息后继续解析后续数据，保证程序不会因单个错误数据而终止。
4. 无论本次解析是否成功，都必须在 `finally` 块中输出“解析过程结束”。

请给出一个符合上述要求的实现示例。
