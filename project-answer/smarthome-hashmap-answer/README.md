# 映射字典 (HashMap) (智能家居)

智能家居系统原先使用一个 `List<Device>` 保存所有设备信息。由于设备 `id` 是唯一标识，若需要根据 `id` 查询设备，只能从头到尾遍历列表逐个比较，查询效率较低。

请使用 `HashMap` 对该存储方案进行优化，要求如下：
1. 使用 `HashMap<String, Device>` 保存设备信息，其中键为设备 `id`，值为对应的 `Device` 对象。
2. 实现以下功能：
   - 添加设备信息
   - 根据 `id` 查询设备信息
   - 根据 `id` 删除设备信息
3. 编写 `Main` 测试类，演示 `HashMap` 的存储、查询和删除过程。

### 测试数据示例
请在 `Main` 类中使用以下测试数据进行演示：
- 添加设备：
  - `101`，`智能灯`
  - `102`，`智能空调`
  - `103`，`智能门锁`
- 查询设备：`102`
- 删除设备：`102`
- 删除后再次查询：`102`

### 预期输出示例
```text
添加后设备数量：3
查询 id=102 的设备：Device{id='102', name='智能空调'}
删除后再次查询 id=102：null
```


---

## 解决方案

本题考查使用 `HashMap` 对设备信息进行高效管理。

### 1. 为什么使用 `HashMap`
原先使用 `List<Device>` 存储设备时，如果要根据设备 `id` 查询，需要遍历整个列表逐个比较，效率较低。

`HashMap` 采用键值对存储方式，可以直接通过设备 `id` 快速定位对应设备：
- `key`：设备 `id`，类型为 `String`
- `value`：设备对象，类型为 `Device`

定义如下：
```java
Map<String, Device> deviceMap = new HashMap<>();
```

### 2. 核心操作
#### 添加设备
```java
deviceMap.put(device.getId(), device);
```
将设备 `id` 作为键，设备对象作为值存入 `HashMap`。

#### 查询设备
```java
deviceMap.get(id);
```
根据设备 `id` 直接获取对应对象。如果不存在，返回 `null`。

#### 删除设备
```java
deviceMap.remove(id);
```
根据设备 `id` 删除对应的键值对。

### 3. 测试数据说明
本题在 `Main` 类中使用以下测试数据：
- `101` -> `智能灯`
- `102` -> `智能空调`
- `103` -> `智能门锁`

程序先添加 3 个设备，此时设备数量为 `3`；随后查询 `id=102` 的设备，输出对应对象；最后删除该设备，再次查询时返回 `null`。

### 4. 代码说明
- `Device` 类封装设备的 `id` 和 `name`，并重写了 `toString()`，方便输出对象内容。
- `DeviceManager` 类负责管理设备，内部使用 `HashMap<String, Device>` 存储数据。
- `Main` 类中通过测试数据演示了添加、查询和删除的完整过程。

### 5. 结果说明
程序输出能够体现：
- 设备成功存入 `HashMap`
- 可以根据 `id` 快速查询设备
- 删除后再次查询结果为 `null`

这说明 `HashMap` 非常适合“根据唯一标识快速查找对象”的场景。

### 参考代码

```java
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
    private final Map<String, Device> deviceMap = new HashMap<>();

    // 添加设备
    public void addDevice(Device device) {
        deviceMap.put(device.getId(), device);
    }

    // 根据 id 查询设备
    public Device getDevice(String id) {
        return deviceMap.get(id);
    }

    // 根据 id 删除设备
    public void removeDevice(String id) {
        deviceMap.remove(id);
    }

    // 获取设备数量（用于测试输出）
    public int getDeviceCount() {
        return deviceMap.size();
    }
}

public class Main {
    public static void main(String[] args) {
        DeviceManager manager = new DeviceManager();

        // 添加测试数据
        manager.addDevice(new Device("101", "智能灯"));
        manager.addDevice(new Device("102", "智能空调"));
        manager.addDevice(new Device("103", "智能门锁"));

        System.out.println("添加后设备数量：" + manager.getDeviceCount());

        // 查询设备
        System.out.println("查询 id=102 的设备：" + manager.getDevice("102"));

        // 删除设备
        manager.removeDevice("102");

        // 删除后再次查询
        System.out.println("删除后再次查询 id=102：" + manager.getDevice("102"));
    }
}
```
