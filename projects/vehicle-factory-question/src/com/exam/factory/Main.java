package com.exam.factory;

public class Main {
    public static void main(String[] args) {
        // FIXME: 客户端代码直接依赖具体产品类，违反依赖倒置原则
        Car car = new Car();
        car.drive();
        
        Truck truck = new Truck();
        truck.drive();
    }
}
