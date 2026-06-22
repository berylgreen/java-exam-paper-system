# 异常处理 (数据解析系统)

系统提供的一个数据解析工具 `DataParser` 目前很脆弱，解析外部传入的字符串数组时，遇到非数字格式或 null 值会导致整个程序崩溃终止。

**任务要求**：
1. 自定义一个受检异常 `InvalidDataException`。
2. 在 `DataParser.parseScores` 方法中使用 `try-catch` 捕获 `NumberFormatException` 和 `NullPointerException`。
3. 当遇到非法数据时，将引发 `InvalidDataException` 或在控制台记录错误并继续解析下一个数据。
4. 使用 `finally` 块打印“解析过程结束”的日志。