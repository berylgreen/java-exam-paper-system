package com.exam.ecommerce;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 电商系统 模块测试...");
        Order obj = new Order();
        
        // 外部直接修改属性，可能导致非法数据
        obj.setId(""); 
        obj.setValue(-100);
        
        System.out.println("ID: " + obj.getId() + ", Value: " + obj.getValue());
    }
}
