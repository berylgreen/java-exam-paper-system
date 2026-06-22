package com.exam.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Book implements Comparable<Book> {
    private boolean valid;
    private int value;
    public Book() {}
    public Book(boolean valid, String name) { this.valid = valid; this.name = name; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public void setId(String id) { this.id = id; }


    private String id;
    private String name;

    public Book(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Book other) {
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Book{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Book> bookSet = new HashSet<>();

        bookSet.add(new Book("B002", "Java 程序设计"));
        bookSet.add(new Book("B001", "数据结构"));
        bookSet.add(new Book("B002", "Java 编程思想")); // id 相同，视为重复图书

        List<Book> bookList = new ArrayList<>(bookSet);
        Collections.sort(bookList);

        for (Book book : bookList) {
            System.out.println(book);
        }
    }
}