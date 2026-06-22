# 设计模式 (交通工具制造厂)

为减少业务代码中频繁直接使用 `new Car()`、`new Truck()` 带来的耦合问题，请使用**简单工厂模式**对车辆对象的创建过程进行统一封装。

请根据以下要求完成程序设计：

1. 定义车辆接口 `Vehicle`，并在接口中声明 `drive()` 方法。
2. 编写 `Car` 和 `Truck` 两个实现类，分别实现 `Vehicle` 接口。
3. 创建一个静态简单工厂类 `VehicleFactory`，并提供方法 `Vehicle createVehicle(String type)`。
4. 在工厂方法中，根据传入的字符串类型（如 `"car"` 或 `"truck"`）创建并返回对应的车辆对象。
5. 修改主程序，不再直接使用 `new` 创建 `Car` 或 `Truck` 对象，而是通过 `VehicleFactory` 获取对象后调用其 `drive()` 方法。

要求：当传入类型不合法时，程序应给出合理的异常提示。
