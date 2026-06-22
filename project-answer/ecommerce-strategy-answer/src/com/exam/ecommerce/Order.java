package com.exam.ecommerce;

public class Order {
    public String id;
    public String type; // e.g. "TYPE_A", "TYPE_B"
    
    public Order(String id, String type) {
        this.id = id;
        this.type = type;
    }
}
