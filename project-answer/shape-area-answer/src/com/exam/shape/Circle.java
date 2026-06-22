package com.exam.shape;
public class Circle implements ShapeProcessor {
    private String name;
    public Circle(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础图形：" + name); }
}
