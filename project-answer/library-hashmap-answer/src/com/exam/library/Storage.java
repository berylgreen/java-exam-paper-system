package com.exam.library;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Book> items = new HashMap<>();
    public void add(Book item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Book get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
