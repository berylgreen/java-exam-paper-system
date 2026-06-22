package com.exam.bank;
import java.util.HashMap;
import java.util.Map;
public class Storage {
    private Map<String, Account> items = new HashMap<>();
    public void add(Account item) { items.put(item.id != null ? item.id : item.getId(), item); }
    public Account get(String id) { return items.get(id); }
    public void remove(String id) { items.remove(id); }
    public int size() { return items.size(); }
}
