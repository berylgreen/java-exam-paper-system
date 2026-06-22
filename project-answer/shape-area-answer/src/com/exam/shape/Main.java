package com.exam.shape;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        ShapeProcessor[] processors = { new Circle("圆形"), new Rectangle("矩形") };
        for (ShapeProcessor p : processors) { p.process(); }
    }
}
