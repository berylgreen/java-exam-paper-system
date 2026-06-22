package com.exam.rental;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 rental 模块测试...");
        Vehicle obj = new Vehicle();
        
        // 原始设计：直接修改 public 属性，导致可以赋非法值
        obj.id = "";
        obj.value = -100.0; 
        
        System.out.println("对象状态: " + obj.id + ", " + obj.value);
        
        // TODO: 将属性改为 private，提供 Getter/Setter，并在 Setter 中加入数据合法性校验
    }
}
