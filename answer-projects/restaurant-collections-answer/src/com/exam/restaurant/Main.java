package com.exam.restaurant;

import java.util.*;

class Dish implements Comparable<Dish> {
    private boolean valid;
    private int value;
    public Dish() {}
    public Dish(boolean valid, String name) { this.valid = valid; this.name = name; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public void setId(String id) { this.id = id; }


    private String id;
    private String name;

    public Dish(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Dish other) {
        return this.getId().compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Dish{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Dish> dishSet = new HashSet<>();

        dishSet.add(new Dish("D002", "宫保鸡丁"));
        dishSet.add(new Dish("D001", "鱼香肉丝"));
        dishSet.add(new Dish("D003", "麻婆豆腐"));
        dishSet.add(new Dish("D002", "宫保鸡丁（重复）"));

        List<Dish> dishList = new ArrayList<>(dishSet);
        Collections.sort(dishList);

        for (Dish dish : dishList) {
            System.out.println(dish);
        }
    }
}