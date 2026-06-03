// 策略接口：定义统一的处理方法
interface PackageStrategy {
    void process();
}

// 具体策略：普通包裹处理
class NormalPackageStrategy implements PackageStrategy {
    @Override
    public void process() {
        System.out.println("普通包裹处理流程");
    }
}

// 具体策略：加急包裹处理
class UrgentPackageStrategy implements PackageStrategy {
    @Override
    public void process() {
        System.out.println("加急包裹优先处理流程");
    }
}

// 上下文类：持有策略对象，并调用策略完成处理
class PackageProcessor {
    private PackageStrategy strategy;

    public void setStrategy(PackageStrategy strategy) {
        this.strategy = strategy;
    }

    public void processPackage() {
        if (strategy == null) {
            throw new IllegalStateException("未设置包裹处理策略");
        }
        strategy.process();
    }
}

// 测试示例
public class Main {
    public static void main(String[] args) {
        PackageProcessor processor = new PackageProcessor();

        // 处理普通包裹
        processor.setStrategy(new NormalPackageStrategy());
        processor.processPackage();

        // 处理加急包裹
        processor.setStrategy(new UrgentPackageStrategy());
        processor.processPackage();
    }
}