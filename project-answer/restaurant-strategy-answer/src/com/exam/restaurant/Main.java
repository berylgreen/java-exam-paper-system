package com.exam.restaurant;

// 策略接口
interface DishStrategy {
    void process(String dishName);
}

// 具体策略：堂食处理
class DineInStrategy implements DishStrategy {
    @Override
    public void process(String dishName) {
        System.out.println("堂食菜品处理：" + dishName);
    }
}

// 具体策略：打包处理
class TakeAwayStrategy implements DishStrategy {
    @Override
    public void process(String dishName) {
        System.out.println("打包菜品处理：" + dishName);
    }
}

// 具体策略：加急处理
class UrgentStrategy implements DishStrategy {
    @Override
    public void process(String dishName) {
        System.out.println("加急菜品处理：" + dishName);
    }
}

// 上下文类
class DishContext {
    private DishStrategy strategy;

    public void setStrategy(DishStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeStrategy(String dishName) {
        if (strategy == null) {
            throw new IllegalStateException("请先设置菜品处理策略");
        }
        strategy.process(dishName);
    }
}

// 测试类

public class Main {
    public static void main(String[] args) {
        DishContext context = new DishContext();

        context.setStrategy(new DineInStrategy());
        context.executeStrategy("宫保鸡丁");

        context.setStrategy(new TakeAwayStrategy());
        context.executeStrategy("鱼香肉丝");

        context.setStrategy(new UrgentStrategy());
        context.executeStrategy("红烧排骨");
    }
}
