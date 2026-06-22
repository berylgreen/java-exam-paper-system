package com.exam.restaurant;

public class DishStatistics {
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
