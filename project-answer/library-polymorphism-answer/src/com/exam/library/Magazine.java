package com.exam.library;
public class Magazine implements BookProcessor {
    private String name;
    public Magazine(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级图书：" + name); }
}
