package com.exam.payment;

public class Order {
    public void setId(String id) { }
    public String getId() { return ""; }

    private boolean valid;
    private String name = "";
    private int value;
    
    public Order() {}
    public Order(boolean valid, String name) { this.valid = valid; this.name = name; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    private String orderId;
    private double amount;

    public Order(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
    
    public String getOrderId() {
        return orderId;
    }
}
