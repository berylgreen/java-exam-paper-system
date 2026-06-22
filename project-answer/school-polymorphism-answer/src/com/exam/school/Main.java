package com.exam.school;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        StudentProcessor[] processors = { new Undergraduate("高等数学"), new Graduate("大学物理") };
        for (StudentProcessor p : processors) { p.process(); }
    }
}
