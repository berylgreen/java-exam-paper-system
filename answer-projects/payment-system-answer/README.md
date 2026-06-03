# 订单支付模块重构（接口与多态应用）

## 题目背景
当前系统包含一个简单的订单支付模块，但 `PaymentProcessor`（支付处理器）内部硬编码写死了只支持现金支付。随着业务发展，系统需要支持微信和支付宝等多种支付方式。请通过面向对象的特性对代码进行解耦重构。

## 任务要求
1. **提取接口**：新建一个 `PaymentStrategy` 接口，包含抽象方法 `void pay(Order order)`。
2. **实现策略**：新建两个类 `WechatPay` 和 `Alipay`，均实现 `PaymentStrategy` 接口：
   - `WechatPay` 在控制台输出："订单 [orderId] 使用微信支付了 [amount] 元"
   - `Alipay` 在控制台输出："订单 [orderId] 使用支付宝支付了 [amount] 元"
3. **重构处理器**：修改 `PaymentProcessor` 类：
   - 删除原来的硬编码打印逻辑。
   - 增加一个 `PaymentStrategy` 类型的成员变量，并提供 `setPaymentStrategy(PaymentStrategy strategy)` 方法以允许动态切换。
   - 修改 `processPayment` 方法，使其调用当前策略的 `pay` 方法。如果策略未设置，需输出提示信息 "未设置支付方式！"。
4. **验证代码**：修改 `Main` 类的测试逻辑：
   - 实例化两个新订单：`ORD002` (金额 200.0) 和 `ORD003` (金额 300.0)。
   - 为 `PaymentProcessor` 注入 `WechatPay` 策略，然后支付 `ORD002`。
   - 将处理器的策略动态切换为 `Alipay`，然后支付 `ORD003`。

## 运行方式
可以直接在 IDE (IntelliJ IDEA / Eclipse / VS Code) 中运行 `Main.java`。
