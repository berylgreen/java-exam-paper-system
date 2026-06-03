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