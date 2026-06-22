package com.exam.school;

abstract class Student {
    // 统一的处理行为，由子类分别实现
    public abstract void process();
}

class RegularStudent extends Student {
    @Override
    public void process() {
        System.out.println("处理普通学生的统计逻辑");
    }
}

class VIPStudent extends Student {
    @Override
    public void process() {
        System.out.println("处理 VIP 学生的统计逻辑");
    }
}

class ExchangeStudent extends Student {
    @Override
    public void process() {
        System.out.println("处理交换生的统计逻辑");
    }
}

public class StudentStatistics {
    public void processAll(Student[] students) {
        for (Student student : students) {
            student.process(); // 多态调用，无需 instanceof 判断
        }
    }

    public static void main(String[] args) {
        Student[] students = {
            new RegularStudent(),
            new VIPStudent(),
            new ExchangeStudent()
        };

        StudentStatistics statistics = new StudentStatistics();
        statistics.processAll(students);
    }
}