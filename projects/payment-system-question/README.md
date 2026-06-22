# 订单支付模块重构（接口与多态应用）

当前订单支付模块中，`PaymentProcessor` 直接依赖具体支付方式，扩展新支付手段时需要频繁修改原有代码，耦合度较高。请使用面向对象中的接口、多态思想对该模块进行重构，使系统能够灵活支持多种支付方式。

具体要求如下：

（1）定义支付策略接口 `PaymentStrategy`，其中包含抽象方法 `void pay(Order order)`。

（2）编写 `WechatPay` 和 `Alipay` 两个实现类，分别实现 `PaymentStrategy` 接口，并按如下格式输出支付结果：
`订单 [orderId] 使用微信支付了 [amount] 元`
`订单 [orderId] 使用支付宝支付了 [amount] 元`

（3）重构 `PaymentProcessor` 类：
- 增加 `PaymentStrategy` 类型的成员变量，用于保存当前支付策略；
- 提供 `setPaymentStrategy(PaymentStrategy strategy)` 方法，用于动态设置支付方式；
- 将 `processPayment(Order order)` 方法改为通过策略对象完成支付；
- 如果尚未设置支付策略，应输出提示信息，例如：`未设置支付方式！`

（4）在 `Main` 类中编写测试代码，演示同一个 `PaymentProcessor` 对象如何针对不同订单动态切换为微信支付和支付宝支付。

### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础订单对象：`订单1`
- 创建高级订单对象：`订单2`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础订单：订单1
统一处理高级订单：订单2
```
