package com.exam.shape;

public class ShapeCalculator {
    public double calculateTotalArea(Object[] shapes) {
        double total = 0;
        for (Object shape : shapes) {
            if (shape instanceof Circle) {
                Circle c = (Circle) shape;
                total += Math.PI * c.radius * c.radius;
            } else if (shape instanceof Rectangle) {
                Rectangle r = (Rectangle) shape;
                total += r.width * r.height;
            } else if (shape instanceof Triangle) {
                Triangle t = (Triangle) shape;
                total += 0.5 * t.base * t.height;
            }
        }
        return total;
    }
}
