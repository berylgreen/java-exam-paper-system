package com.exam.restaurant;
public class ColdDish implements DishProcessor {
    private String name;
    public ColdDish(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础菜品：" + name); }
}
