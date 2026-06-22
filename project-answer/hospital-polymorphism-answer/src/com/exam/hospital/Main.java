package com.exam.hospital;
interface PatientProcessor { void process(); }
class Outpatient implements PatientProcessor {
    private String name;
    public Outpatient(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础病历：" + name); }
}
class Emergency implements PatientProcessor {
    private String name;
    public Emergency(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级病历：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        PatientProcessor[] processors = { new Outpatient("张三的病历"), new Emergency("李四的病历") };
        for (PatientProcessor p : processors) { p.process(); }
    }
}
