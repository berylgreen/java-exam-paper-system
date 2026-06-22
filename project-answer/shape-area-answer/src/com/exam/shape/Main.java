package com.exam.shape;
interface ShapeProcessor { void process(); }
class Circle implements ShapeProcessor {
    private String name;
    public Circle(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础图形：" + name); }
}
class Rectangle implements ShapeProcessor {
    private String name;
    public Rectangle(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级图形：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        ShapeProcessor[] processors = { new Circle("圆形"), new Rectangle("矩形") };
        for (ShapeProcessor p : processors) { p.process(); }
    }
}
