// 策略接口：定义统一的业务处理方法
interface VehicleStrategy {
    void execute();
}

// 具体策略1：普通业务处理
class NormalStrategy implements VehicleStrategy {
    @Override
    public void execute() {
        System.out.println("普通车辆业务处理");
    }
}

// 具体策略2：加急业务处理
class UrgentStrategy implements VehicleStrategy {
    @Override
    public void execute() {
        System.out.println("加急车辆业务处理");
    }
}

// 上下文类：持有策略对象，并在运行时调用具体策略
class VehicleProcessor {
    private VehicleStrategy strategy;

    public void setStrategy(VehicleStrategy strategy) {
        this.strategy = strategy;
    }

    public void process() {
        if (strategy == null) {
            throw new IllegalStateException("未设置车辆业务处理策略");
        }
        strategy.execute();
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        VehicleProcessor processor = new VehicleProcessor();

        // 处理普通业务
        processor.setStrategy(new NormalStrategy());
        processor.process();

        // 处理加急业务
        processor.setStrategy(new UrgentStrategy());
        processor.process();
    }
}