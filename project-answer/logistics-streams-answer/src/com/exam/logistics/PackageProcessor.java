package com.exam.logistics;

import java.util.List;
import java.util.stream.Collectors;

class PackageItem {
    private boolean valid;
    private String name;

    public PackageItem(boolean valid, String name) {
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

public class PackageProcessor {
    public List<String> processList(List<PackageItem> list) {
        return list.stream()
                   .filter(item -> item.isValid())
                   .map(item -> item.getName())
                   .collect(Collectors.toList());
    }
}