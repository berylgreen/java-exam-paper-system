package com.exam.library;

import java.util.HashMap;
import java.util.Map;

class Book {
    private String id;
    private String name;
    private String author;

    public Book(String id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Book{id='" + id + "', name='" + name + "', author='" + author + "'}";
    }
}

class LibraryManager {
    private Map<String, Book> bookMap = new HashMap<>();

    // 添加图书
    public void addBook(Book book) {
        bookMap.put(book.getId(), book);
    }

    // 根据 ID 查询图书
    public Book getBookById(String id) {
        return bookMap.get(id);
    }

    // 根据 ID 删除图书
    public void removeBookById(String id) {
        bookMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        LibraryManager manager = new LibraryManager();

        manager.addBook(new Book("101", "Java 程序设计", "张三"));
        manager.addBook(new Book("102", "数据结构", "李四"));
        manager.addBook(new Book("103", "操作系统", "王五"));

        // 根据 ID 查询
        System.out.println("查询 ID=102 的图书：");
        Book book = manager.getBookById("102");
        System.out.println(book);

        // 根据 ID 删除
        System.out.println("删除 ID=102 的图书");
        manager.removeBookById("102");

        // 再次查询验证是否删除成功
        System.out.println("再次查询 ID=102 的图书：");
        System.out.println(manager.getBookById("102"));
    }
}