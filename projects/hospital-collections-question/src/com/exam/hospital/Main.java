package com.exam.hospital;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 hospital 模块测试...");
        
        // 原始设计：使用定长数组，无法动态扩容，也难以去重
        Patient[] array = new Patient[3];
        array[0] = new Patient("103", "王五的病历");
        array[1] = new Patient("101", "张三的病历");
        array[2] = new Patient("102", "李四的病历");
        
        // 尝试添加重复元素会抛出异常或覆盖
        // array[3] = new Patient("102", "李四的病历"); // ArrayIndexOutOfBoundsException
        
        for (Patient item : array) {
            System.out.println(item);
        }
        
        // TODO: 使用 ArrayList 或 HashSet 替代原有数组来管理对象，实现去重和排序
    }
}
