package com.exam.shape;
public class Rectangle implements ShapeProcessor {
    private String name;
    public Rectangle(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级图形：" + name); }
}
