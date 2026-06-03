# Lambda 与接口 (数据过滤器)

程序中现有一个处理数字列表的 `DataFilter` 类，目前内部死板地提供了 `filterEvenNumbers` (过滤偶数) 和 `filterPositiveNumbers` (过滤正数) 方法，代码冗余且无法适应灵活变化的新过滤需求。

**任务要求**：
1. 声明一个带有一个方法的函数式接口 `Condition<T>`：`boolean test(T item)`。可以添加 `@FunctionalInterface` 注解。
2. 将 `DataFilter` 中的多个过滤方法统一重构为单个通用方法 `List<Integer> filter(List<Integer> list, Condition<Integer> cond)`。
3. 在 `Main` 方法中，使用 Lambda 表达式作为参数传入，实现“过滤大于5的数字”、“过滤偶数”的调用。