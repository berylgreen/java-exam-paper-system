# 设计模式 (气象站通知系统)\n\n当前气象站 (`WeatherStation`) 的气象数据更新时，代码直接硬编码去调用特定设备的显示方法（如 `PhoneApp` 和 `DisplayPanel`）。这种紧耦合导致系统很难增加新设备。

**任务要求**：
1. 提取观察者接口 `Observer`，包含方法 `void update(float temp, float humidity, float pressure)`。
2. 定义主题接口 `Subject`（包含 register, remove, notify 方法）。
3. 重构 `WeatherStation` 实现 `Subject`，并维护一个订阅者列表 `List<Observer>`。
4. 让各个显示设备实现 `Observer` 接口。测试动态注册和数据通知。