package com.exam.restaurant;
interface DishProcessor { void process(); }
class ColdDish implements DishProcessor {
    private String name;
    public ColdDish(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础菜品：" + name); }
}
class HotDish implements DishProcessor {
    private String name;
    public HotDish(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级菜品：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DishProcessor[] processors = { new ColdDish("宫保鸡丁"), new HotDish("鱼香肉丝") };
        for (DishProcessor p : processors) { p.process(); }
    }
}
