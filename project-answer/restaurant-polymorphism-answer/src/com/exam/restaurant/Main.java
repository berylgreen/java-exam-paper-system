package com.exam.restaurant;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DishProcessor[] processors = { new ColdDish("宫保鸡丁"), new HotDish("鱼香肉丝") };
        for (DishProcessor p : processors) { p.process(); }
    }
}
