package com.exam.generic;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 generic 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Object[] array = new Object[3];
        array[0] = new Object("103", "对象C");
        array[1] = new Object("101", "对象A");
        array[2] = new Object("102", "对象B");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Object("102", "对象B"); // ArrayIndexOutOfBoundsException
        
        for (Object item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
