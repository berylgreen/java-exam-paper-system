package com.exam.shape;

public class Main {
    public static void main(String[] args) {
        Object[] shapes = {
            new Circle(5),
            new Rectangle(4, 6),
            new Triangle(3, 8)
        };
        ShapeCalculator calc = new ShapeCalculator();
        System.out.println("Total Area: " + calc.calculateTotalArea(shapes));
    }
}
