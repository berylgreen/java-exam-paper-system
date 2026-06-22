package com.exam.library;

// 1. 定义策略接口
interface BookStrategy {
    void handle(String bookName);
}

// 2. 具体策略：普通借阅处理
class NormalBorrowStrategy implements BookStrategy {
    @Override
    public void handle(String bookName) {
        System.out.println("普通借阅处理：" + bookName);
    }
}

// 3. 具体策略：加急处理
class UrgentBorrowStrategy implements BookStrategy {
    @Override
    public void handle(String bookName) {
        System.out.println("加急借阅处理：" + bookName);
    }
}

// 4. 具体策略：馆内阅览处理
class ReadingRoomStrategy implements BookStrategy {
    @Override
    public void handle(String bookName) {
        System.out.println("馆内阅览处理：" + bookName);
    }
}

// 5. 上下文类
class BookContext {
    private BookStrategy strategy;

    public void setStrategy(BookStrategy strategy) {
        this.strategy = strategy;
    }

    public void process(String bookName) {
        if (strategy == null) {
            throw new IllegalStateException("请先设置图书处理策略");
        }
        strategy.handle(bookName);
    }
}

// 6. 测试类

public class Main {
    public static void main(String[] args) {
        BookContext context = new BookContext();

        context.setStrategy(new NormalBorrowStrategy());
        context.process("Java 程序设计");

        context.setStrategy(new UrgentBorrowStrategy());
        context.process("数据结构");

        context.setStrategy(new ReadingRoomStrategy());
        context.process("操作系统");
    }
}
