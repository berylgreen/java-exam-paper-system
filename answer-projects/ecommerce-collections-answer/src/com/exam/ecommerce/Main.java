package com.exam.ecommerce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Order implements Comparable<Order> {
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
    public void setId(String id) { this.id = id; }


    private String id;

    public Order(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Order other) {
        return this.getId().compareTo(other.getId());
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Order> orderSet = new HashSet<>();
        orderSet.add(new Order("A002"));
        orderSet.add(new Order("A001"));
        orderSet.add(new Order("A002")); // 重复订单，不会重复加入

        List<Order> orderList = new ArrayList<>(orderSet);
        Collections.sort(orderList);

        for (Order order : orderList) {
            System.out.println(order.getId());
        }
    }
}