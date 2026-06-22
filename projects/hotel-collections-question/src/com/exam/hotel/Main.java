package com.exam.hotel;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 hotel 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Room[] array = new Room[3];
        array[0] = new Room("103", "标准间");
        array[1] = new Room("101", "总统套房");
        array[2] = new Room("102", "豪华大床房");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Room("102", "豪华大床房"); // ArrayIndexOutOfBoundsException
        
        for (Room item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
