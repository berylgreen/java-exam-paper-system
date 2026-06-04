package com.exam.bank;

public class Account {
    private boolean valid;
    private String name = "";
    private int value;
    
    public Account() {}
    public Account(boolean valid, String name) { this.valid = valid; this.name = name; }
    
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
            throw new IllegalArgumentException("账户编号不能为空");
        }
        this.setId(id);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("账户余额不能为负数");
        }
        this.setAmount(amount);
    }
}