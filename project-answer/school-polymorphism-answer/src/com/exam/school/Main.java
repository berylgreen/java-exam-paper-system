package com.exam.school;
interface StudentProcessor { void process(); }
class Undergraduate implements StudentProcessor {
    private String name;
    public Undergraduate(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础课程：" + name); }
}
class Graduate implements StudentProcessor {
    private String name;
    public Graduate(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级课程：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        StudentProcessor[] processors = { new Undergraduate("高等数学"), new Graduate("大学物理") };
        for (StudentProcessor p : processors) { p.process(); }
    }
}
