import java.util.HashMap;
import java.util.Map;

class Order {
    private String id;
    private String name;

    public Order(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Order{id='" + id + "', name='" + name + "'}";
    }
}

class OrderManager {
    private Map<String, Order> orderMap = new HashMap<>();

    // 添加订单
    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    // 根据订单编号查询订单
    public Order getOrderById(String id) {
        return orderMap.get(id);
    }

    // 根据订单编号删除订单
    public void removeOrderById(String id) {
        orderMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        OrderManager manager = new OrderManager();

        // 添加订单
        manager.addOrder(new Order("101", "手机"));
        manager.addOrder(new Order("102", "耳机"));
        manager.addOrder(new Order("103", "键盘"));

        // 查询订单
        System.out.println("查询订单 102：" + manager.getOrderById("102"));

        // 删除订单
        manager.removeOrderById("102");
        System.out.println("删除后再次查询订单 102：" + manager.getOrderById("102"));
    }
}