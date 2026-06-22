package com.exam.rental;

abstract class Vehicle {
    /**
     * 车辆统计处理方法，不同车辆有不同实现
     */
    public abstract void process();
}

class RegularVehicle extends Vehicle {
    @Override
    public void process() {
        System.out.println("普通车辆：执行常规统计逻辑");
    }
}

class VIPVehicle extends Vehicle {
    @Override
    public void process() {
        System.out.println("VIP车辆：执行VIP统计逻辑");
    }
}

class TruckVehicle extends Vehicle {
    @Override
    public void process() {
        System.out.println("货运车辆：执行货运统计逻辑");
    }
}

class VehicleStatistics {
    /**
     * 统一处理所有车辆对象
     */
    public void processAll(Vehicle[] vehicles) {
        for (Vehicle vehicle : vehicles) {
            vehicle.process();
        }
    }
}

public class Test {
    public static void main(String[] args) {
        Vehicle[] vehicles = {
            new RegularVehicle(),
            new VIPVehicle(),
            new TruckVehicle()
        };

        VehicleStatistics statistics = new VehicleStatistics();
        statistics.processAll(vehicles);
    }
}
