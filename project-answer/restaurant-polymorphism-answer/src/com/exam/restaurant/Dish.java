package com.exam.restaurant;

// 抽象父类：定义所有菜品的统一行为
public abstract class Dish {
    public void setId(String id) { }
    public String getId() { return ""; }

    private boolean valid;
    private String name = "";
    private int value;
    
    public Dish() {}
    public Dish(boolean valid, String name) { this.valid = valid; this.name = name; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

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
class DishStatistics {
    public void processAll(Dish[] dishes) {
        for (Dish dish : dishes) {
            dish.process(); // 多态调用
        }
    }

    public static void main(String[] args) {
        Dish[] dishes = {
            new RegularDish(),
            new VIPDish(),
            new DiscountDish()
        };

        DishStatistics statistics = new DishStatistics();
        statistics.processAll(dishes);
    }
}