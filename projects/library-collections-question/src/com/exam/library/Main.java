package com.exam.library;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 library 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Book[] array = new Book[3];
        array[0] = new Book("103", "计算机网络");
        array[1] = new Book("101", "Java编程思想");
        array[2] = new Book("102", "算法导论");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Book("102", "算法导论"); // ArrayIndexOutOfBoundsException
        
        for (Book item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
