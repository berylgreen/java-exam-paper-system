import java.util.List;
import java.util.stream.Collectors;

class Device {
    private boolean valid;
    private String name;

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class DeviceProcessor {
    public List<String> processList(List<Device> list) {
        return list.stream()
                .filter(device -> device.isValid())
                .map(Device::getName)
                .collect(Collectors.toList());
    }
}