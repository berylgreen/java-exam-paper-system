package com.exam.factory;

interface Vehicle {
    void drive();
}

class Car implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Driving a car");
    }
}

class Truck implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Driving a truck");
    }
}

class VehicleFactory {
    public static Vehicle createVehicle(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }

        if ("car".equalsIgnoreCase(type)) {
            return new Car();
        } else if ("truck".equalsIgnoreCase(type)) {
            return new Truck();
        }

        throw new IllegalArgumentException("Unknown vehicle type: " + type);
    }
}

public class Main {
    public static void main(String[] args) {
        Vehicle vehicle1 = VehicleFactory.createVehicle("car");
        vehicle1.drive();

        Vehicle vehicle2 = VehicleFactory.createVehicle("truck");
        vehicle2.drive();
    }
}
