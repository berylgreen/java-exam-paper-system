package com.exam.library;

abstract class Book {
    // 统一的处理方法，由各子类分别实现
    public abstract void process();
}

class RegularBook extends Book {
    @Override
    public void process() {
        System.out.println("处理普通图书的统计逻辑");
    }
}

class VIPBook extends Book {
    @Override
    public void process() {
        System.out.println("处理 VIP 图书的统计逻辑");
    }
}

class ReferenceBook extends Book {
    @Override
    public void process() {
        System.out.println("处理参考图书的统计逻辑");
    }
}

public class BookStatistics {
    public void processAll(Book[] books) {
        for (Book book : books) {
            book.process(); // 利用多态调用实际子类的方法
        }
    }

    public static void main(String[] args) {
        Book[] books = {
            new RegularBook(),
            new VIPBook(),
            new ReferenceBook()
        };

        BookStatistics statistics = new BookStatistics();
        statistics.processAll(books);
    }
}