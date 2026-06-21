# 映射字典 (HashMap) (智能家居)

智能家居系统原先使用一个 `List<Device>` 保存所有设备信息（`Device` 的数据结构详见 `smarthome-hashmap-question` 工程代码）。i当需要根据设备 `id` 查询时，只能从头到尾遍历列表逐个比较，查询效率较低。

请使用 `HashMap` 对该存储方案进行优化，要求如下：
1. 使用 `HashMap<String, Device>` 保存设备信息，其中键为设备 `id`，值为对应的 `Device` 对象。
2. 实现以下功能：
   - 添加设备信息
   - 根据 `id` 查询设备信息
   - 根据 `id` 删除设备信息
3. 编写 `Main` 测试类，演示 `HashMap` 的存储、查询和删除过程。