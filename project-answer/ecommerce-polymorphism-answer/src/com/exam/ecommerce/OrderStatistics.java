package com.exam.ecommerce;

abstract class Order {
    /**
     * 订单处理的统一入口
     */
    public abstract void process();
}

class RegularOrder extends Order {
    @Override
    public void process() {
        System.out.println("处理普通订单");
    }
}

class VIPOrder extends Order {
    @Override
    public void process() {
        System.out.println("处理 VIP 订单");
    }
}

class GroupOrder extends Order {
    @Override
    public void process() {
        System.out.println("处理团购订单");
    }
}

public class OrderStatistics {
    public void processAll(Order[] orders) {
        for (Order order : orders) {
            order.process(); // 利用多态调用具体子类的实现
        }
    }

    public static void main(String[] args) {
        Order[] orders = {
            new RegularOrder(),
            new VIPOrder(),
            new GroupOrder()
        };

        OrderStatistics statistics = new OrderStatistics();
        statistics.processAll(orders);
    }
}
