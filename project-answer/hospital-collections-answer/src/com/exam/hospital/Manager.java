package com.exam.hospital;

public class Manager {
    // 原始设计：使用定长数组，容量固定且难以去重
    private Patient[] items = new Patient[10];
    private int count = 0;
    
    public void add(Patient item) {
        if (count < items.length) {
            items[count++] = item;
        } else {
            System.out.println("数组已满，无法添加！");
        }
    }
    
    public void printAll() {
        for (int i = 0; i < count; i++) {
            System.out.println("Item ID: " + items[i].getId());
        }
    }
}
