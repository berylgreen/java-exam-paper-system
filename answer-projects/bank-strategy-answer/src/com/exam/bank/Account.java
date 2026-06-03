package com.exam.bank;

public class Account {
    public String id;
    public String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Account(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
