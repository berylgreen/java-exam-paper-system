package com.exam.ecommerce;

// 1. 策略接口
interface OrderStrategy {
    void processOrder();
}

// 2. 具体策略：普通订单处理
class NormalOrderStrategy implements OrderStrategy {
    @Override
    public void processOrder() {
        System.out.println("普通订单处理流程");
    }
}

// 3. 具体策略：加急订单处理
class UrgentOrderStrategy implements OrderStrategy {
    @Override
    public void processOrder() {
        System.out.println("加急订单优先处理流程");
    }
}

// 4. 上下文类
class OrderProcessor {
    private OrderStrategy strategy;

    public void setStrategy(OrderStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("未设置订单处理策略");
        }
        strategy.processOrder();
    }
}

// 5. 测试示例
public class Main {
    public static void main(String[] args) {
        OrderProcessor processor = new OrderProcessor();

        processor.setStrategy(new NormalOrderStrategy());
        processor.process();

        processor.setStrategy(new UrgentOrderStrategy());
        processor.process();
    }
}