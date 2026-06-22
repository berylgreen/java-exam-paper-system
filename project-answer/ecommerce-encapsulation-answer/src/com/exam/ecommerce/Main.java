package com.exam.ecommerce;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Order obj = new Order();
        try {
            obj.setId("101");
            obj.setName("笔记本电脑");
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
