import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Order implements Comparable<Order> {
    private String id;

    public Order(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Order other) {
        return this.id.compareTo(other.id);
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Order> orderSet = new HashSet<>();
        orderSet.add(new Order("A002"));
        orderSet.add(new Order("A001"));
        orderSet.add(new Order("A002")); // 重复订单，不会重复加入

        List<Order> orderList = new ArrayList<>(orderSet);
        Collections.sort(orderList);

        for (Order order : orderList) {
            System.out.println(order.getId());
        }
    }
}