# 多态与继承 (形状面积计算器)

现有的形状面积计算器类 `ShapeCalculator` 使用了冗长的 `if-else if` 语句来计算 `Circle`, `Rectangle` 和 `Triangle` 的面积。这违反了开闭原则。

**任务要求**：
1. 定义一个抽象类 `Shape`，包含抽象方法 `double calculateArea()`。
2. 让 `Circle`, `Rectangle`, `Triangle` 继承 `Shape` 并实现该方法。
3. 重构 `ShapeCalculator`，使其接收 `Shape` 数组并通过多态统一调用 `calculateArea()`。
4. 在 `Main` 中测试计算结果。