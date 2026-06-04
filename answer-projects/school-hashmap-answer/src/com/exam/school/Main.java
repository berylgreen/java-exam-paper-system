package com.exam.school;

import java.util.HashMap;
import java.util.Map;

class Student {
    private String id;
    private String name;
    private int age;

    public Student(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "', age=" + age + "}";
    }
}

class StudentManager {
    private Map<String, Student> studentMap = new HashMap<>();

    // 添加学生
    public void addStudent(Student student) {
        studentMap.put(student.getId(), student);
    }

    // 根据学号查询学生
    public Student getStudentById(String id) {
        return studentMap.get(id);
    }

    // 根据学号删除学生
    public void removeStudentById(String id) {
        studentMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();

        manager.addStudent(new Student("1001", "张三", 18));
        manager.addStudent(new Student("1002", "李四", 19));
        manager.addStudent(new Student("1003", "王五", 20));

        System.out.println("查询学号1002：" + manager.getStudentById("1002"));

        manager.removeStudentById("1002");
        System.out.println("删除后再次查询学号1002：" + manager.getStudentById("1002"));
    }
}