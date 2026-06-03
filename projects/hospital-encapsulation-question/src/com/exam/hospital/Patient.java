package com.exam.hospital;

public class Patient {
    // 原始设计：属性直接暴露，破坏了封装性，也无法保证数据合法性
    public String id;
    public double value;
    
    // TODO: 1. 将属性改为 private
    // TODO: 2. 提供 Getter 和 Setter 方法
    // TODO: 3. 在 Setter 方法中加入数据有效性校验
}
