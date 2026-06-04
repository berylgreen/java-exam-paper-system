package com.exam.hospital;

// 1. 定义策略接口
interface PatientStrategy {
    void process();
}

// 2. 具体策略：普通病患处理
class NormalPatientStrategy implements PatientStrategy {
    @Override
    public void process() {
        System.out.println("普通病患：按常规流程处理");
    }
}

// 3. 具体策略：紧急病患处理
class UrgentPatientStrategy implements PatientStrategy {
    @Override
    public void process() {
        System.out.println("紧急病患：优先安排抢救与检查");
    }
}

// 4. 上下文类
class PatientProcessor {
    private PatientStrategy strategy;

    public void setStrategy(PatientStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeStrategy() {
        if (strategy == null) {
            throw new IllegalStateException("未设置病患处理策略");
        }
        strategy.process();
    }
}

// 5. 测试示例
public class Main {
    public static void main(String[] args) {
        PatientProcessor processor = new PatientProcessor();

        // 处理普通病患
        processor.setStrategy(new NormalPatientStrategy());
        processor.executeStrategy();

        // 处理紧急病患
        processor.setStrategy(new UrgentPatientStrategy());
        processor.executeStrategy();
    }
}