import java.util.List;
import java.util.stream.Collectors;

class Car {
    private boolean valid;
    private String name;

    public Car(boolean valid, String name) {
        this.valid = valid;
        this.name = name;
    }

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class CarProcessor {
    public List<String> processList(List<Car> list) {
        return list.stream()
                .filter(car -> car.isValid())
                .map(car -> car.getName())
                .collect(Collectors.toList());
    }
}