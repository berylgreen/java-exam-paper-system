package com.exam.smarthome;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 smarthome 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Device[] array = new Device[3];
        array[0] = new Device("103", "智能门锁");
        array[1] = new Device("101", "智能灯");
        array[2] = new Device("102", "智能空调");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Device("102", "智能空调"); // ArrayIndexOutOfBoundsException
        
        for (Device item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
