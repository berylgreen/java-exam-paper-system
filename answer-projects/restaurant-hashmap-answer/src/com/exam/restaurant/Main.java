import java.util.HashMap;
import java.util.Map;

class Dish {
    private String id;
    private String name;

    public Dish(String id, String name) {
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
        return "Dish{id='" + id + "', name='" + name + "'}";
    }
}

class DishManager {
    private Map<String, Dish> dishMap = new HashMap<>();

    // 添加菜品
    public void addDish(Dish dish) {
        dishMap.put(dish.getId(), dish);
    }

    // 根据 ID 查询菜品
    public Dish getDishById(String id) {
        return dishMap.get(id);
    }

    // 根据 ID 删除菜品
    public void removeDishById(String id) {
        dishMap.remove(id);
    }
}

public class Main {
    public static void main(String[] args) {
        DishManager manager = new DishManager();

        // 添加菜品
        manager.addDish(new Dish("101", "宫保鸡丁"));
        manager.addDish(new Dish("102", "鱼香肉丝"));
        manager.addDish(new Dish("103", "麻婆豆腐"));

        // 查询菜品
        System.out.println("查询 102：" + manager.getDishById("102"));

        // 删除菜品
        manager.removeDishById("102");
        System.out.println("删除后再次查询 102：" + manager.getDishById("102"));
    }
}