package com.exam.hospital;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Patient obj = new Patient();
        try {
            obj.setId("101");
            obj.setName("张三的病历");
            System.out.println("正常设置成功：id=" + obj.getId() + ", 名称=" + obj.getName());
        } catch (Exception e) {
            System.out.println("正常设置失败：" + e.getMessage());
        }
        try {
            obj.setId("");
        } catch (IllegalArgumentException e) {
            System.out.println("设置编号失败：" + e.getMessage());
        }
        try {
            obj.setValue(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("设置数值失败：" + e.getMessage());
        }
    }
}
