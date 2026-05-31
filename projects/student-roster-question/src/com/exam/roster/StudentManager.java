package com.exam.roster;

public class StudentManager {
    private Student[] students = new Student[3];
    private int count = 0;

    public void addStudent(Student student) {
        // FIXME: 如果人数超过3会抛出异常，且没有进行去重检查
        students[count++] = student;
    }

    public void printAll() {
        for (int i = 0; i < count; i++) {
            System.out.println(students[i]);
        }
    }
}
