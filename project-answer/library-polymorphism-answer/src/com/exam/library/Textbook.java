package com.exam.library;
public class Textbook implements BookProcessor {
    private String name;
    public Textbook(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础图书：" + name); }
}
