package com.exam.weather;

public class Main {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation(new PhoneApp(), new DisplayPanel());
        station.setMeasurements(25.5f, 65f, 1013.1f);
    }
}
