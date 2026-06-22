package com.exam.bank;

// 策略接口：定义统一的业务处理方法
interface AccountStrategy {
    void execute();
}

// 具体策略1：普通账户业务处理
class NormalStrategy implements AccountStrategy {
    @Override
    public void execute() {
        System.out.println("普通账户业务处理");
    }
}

// 具体策略2：紧急账户业务处理
class UrgentStrategy implements AccountStrategy {
    @Override
    public void execute() {
        System.out.println("紧急账户业务处理");
    }
}

// 上下文类：持有策略对象，并在运行时调用具体策略
class AccountProcessor {
    private AccountStrategy strategy;

    public void setStrategy(AccountStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            System.out.println("未设置账户处理策略");
            return;
        }
        strategy.execute();
    }
}

// 测试类

public class Main {
    public static void main(String[] args) {
        AccountProcessor processor = new AccountProcessor();

        // 使用普通账户策略
        processor.setStrategy(new NormalStrategy());
        processor.process();

        // 切换为紧急账户策略
        processor.setStrategy(new UrgentStrategy());
        processor.process();
    }
}
