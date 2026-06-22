package com.exam.rental;

import java.util.HashMap;
import java.util.Map;

class Vehicle {
    private String id;
    private String brand;
    private double rentPerDay;

    public Vehicle(String id, String brand, double rentPerDay) {
        this.id = id;
        this.brand = brand;
        this.rentPerDay = rentPerDay;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public double getRentPerDay() {
        return rentPerDay;
    }

    @Override
    public String toString() {
        return "Vehicle{id='" + id + "', brand='" + brand + "', rentPerDay=" + rentPerDay + "}";
    }
}

class VehicleManager {
    private Map<String, Vehicle> vehicleMap = new HashMap<>();

    // 添加车辆
    public void addVehicle(Vehicle vehicle) {
        vehicleMap.put(vehicle.getId(), vehicle);
    }

    // 根据 ID 查询车辆
    public Vehicle getVehicle(String id) {
        return vehicleMap.get(id);
    }

    // 根据 ID 删除车辆
    public void removeVehicle(String id) {
        vehicleMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        VehicleManager manager = new VehicleManager();

        manager.addVehicle(new Vehicle("V101", "Toyota", 300));
        manager.addVehicle(new Vehicle("V102", "Honda", 280));
        manager.addVehicle(new Vehicle("V103", "BMW", 500));

        // 根据 ID 查询
        System.out.println("查询 V102: " + manager.getVehicle("V102"));

        // 删除车辆
        manager.removeVehicle("V102");
        System.out.println("删除后再查询 V102: " + manager.getVehicle("V102"));
    }
}