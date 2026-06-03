import java.util.HashMap;
import java.util.Map;

class Package {
    private String id;
    private String receiver;

    public Package(String id, String receiver) {
        this.id = id;
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "Package{id='" + id + "', receiver='" + receiver + "'}";
    }
}

class PackageManager {
    private Map<String, Package> packageMap = new HashMap<>();

    // 存入包裹
    public void addPackage(Package pkg) {
        packageMap.put(pkg.getId(), pkg);
    }

    // 根据 ID 获取包裹
    public Package getPackage(String id) {
        return packageMap.get(id);
    }

    // 根据 ID 删除包裹
    public void removePackage(String id) {
        packageMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        PackageManager manager = new PackageManager();

        // 添加包裹
        manager.addPackage(new Package("101", "Alice"));
        manager.addPackage(new Package("102", "Bob"));
        manager.addPackage(new Package("103", "Charlie"));

        // 查询包裹
        System.out.println("查询 ID 为 102 的包裹：");
        System.out.println(manager.getPackage("102"));

        // 删除包裹
        manager.removePackage("102");
        System.out.println("删除后再次查询 ID 为 102 的包裹：");
        System.out.println(manager.getPackage("102"));
    }
}