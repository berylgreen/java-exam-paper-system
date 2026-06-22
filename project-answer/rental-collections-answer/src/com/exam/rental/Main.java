package com.exam.rental;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Vehicle implements Comparable<Vehicle> {
    private String id;
    private String brand;

    public Vehicle(String id, String brand) {
        this.id = id;
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Vehicle other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Vehicle{id='" + id + "', brand='" + brand + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Vehicle> vehicleSet = new HashSet<>();

        vehicleSet.add(new Vehicle("A002", "Toyota"));
        vehicleSet.add(new Vehicle("A001", "Honda"));
        vehicleSet.add(new Vehicle("A002", "Toyota")); // 重复车辆，无法再次加入
        vehicleSet.add(new Vehicle("A003", "BMW"));

        List<Vehicle> vehicleList = new ArrayList<>(vehicleSet);
        Collections.sort(vehicleList);

        for (Vehicle vehicle : vehicleList) {
            System.out.println(vehicle);
        }
    }
}
