package com.exam.bank;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 bank 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Account[] array = new Account[3];
        array[0] = new Account("103", "王五的账户");
        array[1] = new Account("101", "张三的账户");
        array[2] = new Account("102", "李四的账户");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Account("102", "李四的账户"); // ArrayIndexOutOfBoundsException
        
        for (Account item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
