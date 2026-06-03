package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 餐饮系统 模块测试...");
        Dish obj = new Dish();
        
        // 外部直接修改属性，可能导致非法数据
        obj.id = ""; 
        obj.value = -100;
        
        System.out.println("ID: " + obj.id + ", Value: " + obj.value);
    }
}
