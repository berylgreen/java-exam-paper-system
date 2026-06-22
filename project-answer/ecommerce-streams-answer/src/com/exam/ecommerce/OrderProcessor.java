package com.exam.ecommerce;

import java.util.List;
import java.util.stream.Collectors;

class Order {
    private String name = "";
    private int value;
    private String id;
    public Order() {}
    public Order(boolean valid, String name) { this.valid = valid; this.name = name; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    

    private boolean valid;
    private String orderId;

    public boolean isValid() {
        return valid;
    }

    public String getOrderId() {
        return orderId;
    }
}

public class OrderProcessor {
    public List<String> processList(List<Order> orderList) {
        return orderList.stream()
                .filter(order -> order.isValid())
                .map(order -> order.getOrderId())
                .collect(Collectors.toList());
    }
}