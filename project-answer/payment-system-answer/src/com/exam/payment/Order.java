package com.exam.payment;

public class Order {
    public String id;
    public String name;
    public double value;
    
    public Order() {}
    
    public Order(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Order{id='" + id + "', name='" + name + "'}";
    }
}
