package com.exam.student;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 student 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Student[] array = new Student[3];
        array[0] = new Student("103", "小刚");
        array[1] = new Student("101", "小明");
        array[2] = new Student("102", "小红");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Student("102", "小红"); // ArrayIndexOutOfBoundsException
        
        for (Student item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
