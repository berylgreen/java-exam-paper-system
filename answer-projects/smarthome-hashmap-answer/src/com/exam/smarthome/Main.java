import java.util.HashMap;
import java.util.Map;

class Device {
    private String id;
    private String name;

    public Device(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Device{id='" + id + "', name='" + name + "'}";
    }
}

class DeviceManager {
    private Map<String, Device> deviceMap = new HashMap<>();

    // 添加设备
    public void addDevice(Device device) {
        deviceMap.put(device.getId(), device);
    }

    // 根据 ID 获取设备
    public Device getDevice(String id) {
        return deviceMap.get(id);
    }

    // 根据 ID 删除设备
    public void removeDevice(String id) {
        deviceMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        DeviceManager manager = new DeviceManager();

        // 添加设备
        manager.addDevice(new Device("101", "智能灯"));
        manager.addDevice(new Device("102", "智能空调"));
        manager.addDevice(new Device("103", "智能门锁"));

        // 根据 ID 查询设备
        System.out.println("查询 ID=102 的设备：" + manager.getDevice("102"));

        // 删除设备
        manager.removeDevice("102");
        System.out.println("删除后再次查询 ID=102：" + manager.getDevice("102"));
    }
}