# 设计模式 (气象站通知系统)

某气象站系统 `WeatherStation` 在气象数据发生变化时，原先通过硬编码方式直接调用具体显示设备（如 `PhoneApp`、`DisplayPanel`）的方法。这种实现方式使 `WeatherStation` 与具体设备强耦合，导致系统难以扩展：每增加一种新设备，都需要修改气象站代码。

请使用**观察者模式（Observer Pattern）**对该系统进行重构，要求如下：

1. 定义观察者接口 `Observer`，包含方法：`void update(float temp, float humidity, float pressure)`。
2. 定义主题接口 `Subject`，至少包含以下方法：
   - `void registerObserver(Observer o)`
   - `void removeObserver(Observer o)`
   - `void notifyObservers()`
3. 重构 `WeatherStation` 类，使其实现 `Subject` 接口，并在类中维护一个 `List<Observer>` 类型的订阅者列表。
4. 让不同的显示设备（如 `PhoneApp`、`DisplayPanel`）实现 `Observer` 接口。
5. 编写测试代码，演示：
   - 观察者的动态注册；
   - 气象数据更新后自动通知所有已订阅设备；
   - 移除某个观察者后，该设备不再接收更新。

请给出完整实现代码。

**测试数据示例：**
请在 `Main` 类中使用以下测试数据进行演示：
- 创建基础气象数据对象：`温度数据`
- 创建高级气象数据对象：`湿度数据`
- 使用统一方法遍历调用处理逻辑

**预期输出示例：**
```text
统一处理基础气象数据：温度数据
统一处理高级气象数据：湿度数据
```