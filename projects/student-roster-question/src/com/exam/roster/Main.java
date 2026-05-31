package com.exam.roster;

public class Main {
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student("1003", "Alice"));
        manager.addStudent(new Student("1001", "Bob"));
        manager.addStudent(new Student("1002", "Charlie"));
        // 下面添加重复学号及超出数组容量的元素将导致问题
        // manager.addStudent(new Student("1001", "Bob Duplicate"));
        
        manager.printAll();
    }
}
