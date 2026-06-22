package com.exam.school;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 教务系统 模块测试...");
        Student obj = new Student();
        
        // 外部直接修改属性，可能导致非法数据
        obj.setId(""); 
        obj.setValue(-100);
        
        System.out.println("ID: " + obj.getId() + ", Value: " + obj.getValue());
    }
}
