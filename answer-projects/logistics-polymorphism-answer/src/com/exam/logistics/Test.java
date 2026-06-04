package com.exam.logistics;

abstract class Package {
    private boolean valid;
    private String name = "";
    private int value;
    private String id;
    
    public Package() {}
    public Package(boolean valid, String name) { this.valid = valid; this.name = name; }
    
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // 统一的抽象处理方法
    public abstract void process();
}

class RegularPackage extends Package {
    @Override
    public void process() {
        System.out.println("处理普通包裹：按标准流程入库、分拣、出库");
    }
}

class ExpressPackage extends Package {
    @Override
    public void process() {
        System.out.println("处理加急包裹：优先分拣并优先配送");
    }
}

class ColdChainPackage extends Package {
    @Override
    public void process() {
        System.out.println("处理冷链包裹：检查温控后进行冷链运输");
    }
}

class PackageStatistics {
    public void processAll(Package[] packages) {
        for (Package p : packages) {
            p.process();  // 多态调用：实际执行的是子类重写后的方法
        }
    }
}

public class Test {
    public static void main(String[] args) {
        Package[] packages = {
            new RegularPackage(),
            new ExpressPackage(),
            new ColdChainPackage()
        };

        PackageStatistics statistics = new PackageStatistics();
        statistics.processAll(packages);
    }
}