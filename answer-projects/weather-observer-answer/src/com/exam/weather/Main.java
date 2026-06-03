import java.util.ArrayList;
import java.util.List;

// 观察者接口
interface Observer {
    void update(float temp, float humidity, float pressure);
}

// 主题接口
interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

// 具体主题：气象站
class WeatherStation implements Subject {
    private List<Observer> observers;
    private float temp;
    private float humidity;
    private float pressure;

    public WeatherStation() {
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(temp, humidity, pressure);
        }
    }

    public void setMeasurements(float temp, float humidity, float pressure) {
        this.temp = temp;
        this.humidity = humidity;
        this.pressure = pressure;
        notifyObservers();
    }
}

// 具体观察者：手机应用
class PhoneApp implements Observer {
    @Override
    public void update(float temp, float humidity, float pressure) {
        System.out.println("PhoneApp 接收到数据：温度=" + temp
                + ", 湿度=" + humidity + ", 气压=" + pressure);
    }
}

// 具体观察者：显示面板
class DisplayPanel implements Observer {
    @Override
    public void update(float temp, float humidity, float pressure) {
        System.out.println("DisplayPanel 显示数据：温度=" + temp
                + ", 湿度=" + humidity + ", 气压=" + pressure);
    }
}

// 测试类
public class Main {
    public static void main(String[] args) {
        WeatherStation station = new WeatherStation();

        Observer phoneApp = new PhoneApp();
        Observer displayPanel = new DisplayPanel();

        // 动态注册观察者
        station.registerObserver(phoneApp);
        station.registerObserver(displayPanel);

        System.out.println("第一次数据更新：");
        station.setMeasurements(26.5f, 65.0f, 1012.3f);

        // 移除一个观察者
        station.removeObserver(phoneApp);

        System.out.println("\n第二次数据更新（移除 PhoneApp 后）：");
        station.setMeasurements(28.0f, 70.0f, 1010.5f);
    }
}