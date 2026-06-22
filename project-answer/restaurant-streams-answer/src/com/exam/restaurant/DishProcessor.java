package com.exam.restaurant;

import java.util.List;
import java.util.stream.Collectors;

class Dish {
    private boolean valid;
    private String name;

    public Dish(boolean valid, String name) {
        this.valid = valid;
        this.name = name;
    }

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class DishProcessor {
    public List<String> processList(List<Dish> list) {
        return list.stream()
                .filter(Dish::isValid)
                .map(Dish::getName)
                .collect(Collectors.toList());
    }
}