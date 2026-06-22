package com.exam.ecommerce;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ecommerce 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Order[] array = new Order[3];
        array[0] = new Order("103", "蓝牙耳机");
        array[1] = new Order("101", "笔记本电脑");
        array[2] = new Order("102", "智能手机");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Order("102", "智能手机"); // ArrayIndexOutOfBoundsException
        
        for (Order item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
