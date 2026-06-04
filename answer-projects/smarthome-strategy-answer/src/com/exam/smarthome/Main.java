package com.exam.smarthome;

// 策略接口：定义统一的设备处理行为
interface DeviceStrategy {
    void execute();
}

// 具体策略：普通处理策略
class NormalStrategy implements DeviceStrategy {
    @Override
    public void execute() {
        System.out.println("Normal processing");
    }
}

// 具体策略：紧急处理策略
class UrgentStrategy implements DeviceStrategy {
    @Override
    public void execute() {
        System.out.println("Urgent processing");
    }
}

// 上下文类：负责持有并调用具体策略
class DeviceProcessor {
    private DeviceStrategy strategy;

    public void setStrategy(DeviceStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy has not been set.");
        }
        strategy.execute();
    }
}

// 测试示例
public class Main {
    public static void main(String[] args) {
        DeviceProcessor processor = new DeviceProcessor();

        processor.setStrategy(new NormalStrategy());
        processor.process();

        processor.setStrategy(new UrgentStrategy());
        processor.process();
    }
}