package com.exam.rental;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 rental 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Vehicle[] array = new Vehicle[3];
        array[0] = new Vehicle("103", "宝马X5");
        array[1] = new Vehicle("101", "丰田卡罗拉");
        array[2] = new Vehicle("102", "本田雅阁");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Vehicle("102", "本田雅阁"); // ArrayIndexOutOfBoundsException
        
        for (Vehicle item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
