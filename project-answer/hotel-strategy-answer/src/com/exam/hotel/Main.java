package com.exam.hotel;

// 策略接口
interface RoomStrategy {
    void execute();
}

// 具体策略：普通入住处理
class NormalCheckInStrategy implements RoomStrategy {
    @Override
    public void execute() {
        System.out.println("执行普通入住处理");
    }
}

// 具体策略：加急清洁处理
class UrgentCleaningStrategy implements RoomStrategy {
    @Override
    public void execute() {
        System.out.println("执行加急清洁处理");
    }
}

// 上下文类
class RoomProcessor {
    private RoomStrategy strategy;

    public void setStrategy(RoomStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("未设置房间处理策略");
        }
        strategy.execute();
    }
}

// 测试类

public class Main {
    public static void main(String[] args) {
        RoomProcessor processor = new RoomProcessor();

        // 使用普通入住策略
        processor.setStrategy(new NormalCheckInStrategy());
        processor.process();

        // 切换为加急清洁策略
        processor.setStrategy(new UrgentCleaningStrategy());
        processor.process();
    }
}
