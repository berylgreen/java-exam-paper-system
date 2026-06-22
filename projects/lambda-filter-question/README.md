# Lambda 与接口 (数据过滤器)

程序中现有一个处理数字列表的 `DataFilter` 类，目前内部死板地提供了 `filterEvenNumbers` (过滤偶数) 和 `filterPositiveNumbers` (过滤正数) 方法，代码冗余且无法适应灵活变化的新过滤需求。

**任务要求**：
(1) 声明一个带有一个方法的函数式接口 `Condition<T>`：`boolean test(T item)`。可以添加 `@FunctionalInterface` 注解。
(2) 将 `DataFilter` 中的多个过滤方法统一重构为单个通用方法 `List<Integer> filter(List<Integer> list, Condition<Integer> cond)`。
(3) 在 `Main` 方法中，使用 Lambda 表达式作为参数传入，实现“过滤大于5的数字”、“过滤偶数”的调用。
**测试数据示例：**
请在 `Main` 类中使用以下测试数据进行演示：
- 依次添加以下元素：
  - `103`，`元素3`
  - `101`，`元素1`
  - `102`，`元素2`
  - `102`，`元素2` (重复)

**预期输出示例：**
```text
添加后去重的元素数量：3
排序后输出：
id=101: 元素1
id=102: 元素2
id=103: 元素3
```
