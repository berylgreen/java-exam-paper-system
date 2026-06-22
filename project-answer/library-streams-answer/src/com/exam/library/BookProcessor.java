package com.exam.library;

import java.util.List;
import java.util.stream.Collectors;

class Book {
    private int value;
    private String id;
    public Book() {}
    public Book(boolean valid, String name) { this.valid = valid; this.name = name; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    

    private boolean valid;
    private String name;

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class BookProcessor {
    public List<String> processList(List<Book> list) {
        return list.stream()
                   .filter(book -> book.isValid())
                   .map(book -> book.getName())
                   .collect(Collectors.toList());
    }
}