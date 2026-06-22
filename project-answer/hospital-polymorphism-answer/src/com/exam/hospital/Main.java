package com.exam.hospital;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        PatientProcessor[] processors = { new Outpatient("张三的病历"), new Emergency("李四的病历") };
        for (PatientProcessor p : processors) { p.process(); }
    }
}
