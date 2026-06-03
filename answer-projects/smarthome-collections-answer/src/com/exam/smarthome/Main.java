import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Device implements Comparable<Device> {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Device other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Device{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        // 使用 HashSet 去重
        Set<Device> deviceSet = new HashSet<>();
        deviceSet.add(new Device("A002", "智能门锁"));
        deviceSet.add(new Device("A001", "智能灯"));
        deviceSet.add(new Device("A003", "智能空调"));
        deviceSet.add(new Device("A002", "重复的智能门锁")); // id 相同，视为重复

        // 转为 List 后排序
        List<Device> deviceList = new ArrayList<>(deviceSet);
        Collections.sort(deviceList);

        System.out.println("去重并排序后的设备列表：");
        for (Device device : deviceList) {
            System.out.println(device);
        }
    }
}