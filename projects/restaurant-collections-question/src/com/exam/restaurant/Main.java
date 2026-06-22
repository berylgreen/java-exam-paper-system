package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 restaurant 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Dish[] array = new Dish[3];
        array[0] = new Dish("103", "红烧肉");
        array[1] = new Dish("101", "宫保鸡丁");
        array[2] = new Dish("102", "鱼香肉丝");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Dish("102", "鱼香肉丝"); // ArrayIndexOutOfBoundsException
        
        for (Dish item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
