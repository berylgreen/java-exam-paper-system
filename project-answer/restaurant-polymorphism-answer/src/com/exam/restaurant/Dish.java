package com.exam.restaurant;

// 抽象父类：定义所有菜品的统一行为

public abstract class Dish {
    // 统计类只关心“如何处理菜品”，不关心具体是什么类型
    public abstract void process();
}

// 普通菜品
class RegularDish extends Dish {
    @Override
    public void process() {
        System.out.println("处理普通菜品的统计逻辑");
    }
}

// VIP 菜品
class VIPDish extends Dish {
    @Override
    public void process() {
        System.out.println("处理 VIP 菜品的统计逻辑");
    }
}

// 特价菜品
class DiscountDish extends Dish {
    @Override
    public void process() {
        System.out.println("处理特价菜品的统计逻辑");
    }
}

// 重构后的统计类：不再使用 instanceof
