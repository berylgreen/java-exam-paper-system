package com.exam.school;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Student implements Comparable<Student> {
    private boolean valid;
    private int value;
    public Student() {}
    public Student(boolean valid, String name) { this.valid = valid; this.name = name; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public void setId(String id) { this.id = id; }


    private String id;
    private String name;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Student other) {
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Student> studentSet = new HashSet<>();

        studentSet.add(new Student("A002", "张三"));
        studentSet.add(new Student("A001", "李四"));
        studentSet.add(new Student("A002", "王五")); // 学号重复，视为同一学生

        List<Student> studentList = new ArrayList<>(studentSet);
        Collections.sort(studentList);

        for (Student student : studentList) {
            System.out.println(student);
        }
    }
}