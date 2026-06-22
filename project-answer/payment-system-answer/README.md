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

## 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础订单对象：`订单1`
- 创建高级订单对象：`订单2`
- 使用统一方法遍历调用处理逻辑

### 预期输出示例
```text
统一处理基础订单：订单1
统一处理高级订单：订单2
```

---

## 解决方案

```java
// 本题的核心是将“支付方式”从 PaymentProcessor 中抽离出来，
// 通过接口统一行为，再利用多态在运行时决定具体采用哪一种支付方式。
```

重构前，如果 `PaymentProcessor` 内部写死现金支付逻辑，那么每增加一种支付方式，都需要直接修改处理器代码。这种做法会导致类之间耦合严重，也不利于后续扩展。

重构后的设计思路如下：

1. `PaymentStrategy` 接口定义统一的支付行为 `pay(Order order)`，表示“任何支付方式都必须具备支付功能”。

2. `WechatPay` 和 `Alipay` 分别实现该接口，各自给出具体的支付实现。这样，不同支付方式之间遵循同一套调用规范。

3. `PaymentProcessor` 不再依赖具体类，而是依赖抽象接口 `PaymentStrategy`。这体现了“面向接口编程”的思想。通过 `setPaymentStrategy()` 方法，可以在程序运行过程中灵活切换支付方式。

4. `processPayment(Order order)` 中只负责调用 `paymentStrategy.pay(order)`。如果尚未设置策略，则进行必要的空值判断并输出提示信息，避免程序出错。

5. 在 `Main` 中，同一个 `PaymentProcessor` 对象先后设置为 `WechatPay` 和 `Alipay`，说明策略可以动态替换，这正是策略模式的典型应用。

这种重构的优点是：

- 降低 `PaymentProcessor` 与具体支付方式之间的耦合；
- 新增支付方式时，只需新增实现类，不必修改原有处理器逻辑；
- 代码更符合开闭原则，即“对扩展开放，对修改关闭”。

如果后续还要支持银行卡支付、云闪付支付等，只需继续实现 `PaymentStrategy` 接口即可。

### 参考代码

```java
// PaymentStrategy.java
public interface PaymentStrategy {
    void pay(Order order);
}

// WechatPay.java
public class WechatPay implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("订单 " + order.getOrderId() + " 使用微信支付了 " + order.getAmount() + " 元");
    }
}

// Alipay.java
public class Alipay implements PaymentStrategy {
    @Override
    public void pay(Order order) {
        System.out.println("订单 " + order.getOrderId() + " 使用支付宝支付了 " + order.getAmount() + " 元");
    }
}

// PaymentProcessor.java
public class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(Order order) {
        if (paymentStrategy == null) {
            System.out.println("未设置支付方式！");
            return;
        }
        paymentStrategy.pay(order);
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        PaymentProcessor processor = new PaymentProcessor();

        Order order1 = new Order("ORD002", 200.0);
        processor.setPaymentStrategy(new WechatPay());
        processor.processPayment(order1);

        Order order2 = new Order("ORD003", 300.0);
        processor.setPaymentStrategy(new Alipay());
        processor.processPayment(order2);
    }
}
```
