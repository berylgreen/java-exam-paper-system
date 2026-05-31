package com.exam.weather;

public class WeatherStation {
    // FIXME: 强耦合了具体的设备类
    private PhoneApp phoneApp;
    private DisplayPanel displayPanel;

    public WeatherStation(PhoneApp p, DisplayPanel d) {
        this.phoneApp = p;
        this.displayPanel = d;
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        // 数据更新时，硬编码通知对应设备
        phoneApp.showWeather(temperature, humidity, pressure);
        displayPanel.renderData(temperature, humidity, pressure);
    }
}
