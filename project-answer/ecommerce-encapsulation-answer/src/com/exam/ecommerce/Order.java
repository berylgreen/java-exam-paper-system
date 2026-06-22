package com.exam.ecommerce;

public class Order {
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

    private String id;
    private double amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        this.setId(id);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.setAmount(amount);
    }
}