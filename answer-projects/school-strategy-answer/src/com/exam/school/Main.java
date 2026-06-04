package com.exam.school;

// 策略接口
interface StudentStrategy {
    void execute();
}

// 具体策略：普通业务处理
class NormalStudentStrategy implements StudentStrategy {
    @Override
    public void execute() {
        System.out.println("执行普通学生业务处理");
    }
}

// 具体策略：紧急业务处理
class UrgentStudentStrategy implements StudentStrategy {
    @Override
    public void execute() {
        System.out.println("执行紧急学生业务处理");
    }
}

// 上下文类
class StudentProcessor {
    private StudentStrategy strategy;

    public void setStrategy(StudentStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("未设置学生业务处理策略");
        }
        strategy.execute();
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        StudentProcessor processor = new StudentProcessor();

        processor.setStrategy(new NormalStudentStrategy());
        processor.process();

        processor.setStrategy(new UrgentStudentStrategy());
        processor.process();
    }
}