package com.exam.logistics;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 logistics 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Package[] array = new Package[3];
        array[0] = new Package("103", "衣物包裹");
        array[1] = new Package("101", "电子产品包裹");
        array[2] = new Package("102", "书籍包裹");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Package("102", "书籍包裹"); // ArrayIndexOutOfBoundsException
        
        for (Package item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
