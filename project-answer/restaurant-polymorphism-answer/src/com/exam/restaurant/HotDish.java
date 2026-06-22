package com.exam.restaurant;
public class HotDish implements DishProcessor {
    private String name;
    public HotDish(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级菜品：" + name); }
}
