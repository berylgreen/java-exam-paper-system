import java.util.List;
import java.util.stream.Collectors;

class Order {
    private boolean valid;
    private String orderId;

    public boolean isValid() {
        return valid;
    }

    public String getOrderId() {
        return orderId;
    }
}

public class OrderProcessor {
    public List<String> processList(List<Order> orderList) {
        return orderList.stream()
                .filter(order -> order.isValid())
                .map(order -> order.getOrderId())
                .collect(Collectors.toList());
    }
}