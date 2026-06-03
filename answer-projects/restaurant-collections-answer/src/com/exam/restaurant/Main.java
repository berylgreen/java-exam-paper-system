import java.util.*;

class Dish implements Comparable<Dish> {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Dish other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Dish{id='" + id + "', name='" + name + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Dish> dishSet = new HashSet<>();

        dishSet.add(new Dish("D002", "宫保鸡丁"));
        dishSet.add(new Dish("D001", "鱼香肉丝"));
        dishSet.add(new Dish("D003", "麻婆豆腐"));
        dishSet.add(new Dish("D002", "宫保鸡丁（重复）"));

        List<Dish> dishList = new ArrayList<>(dishSet);
        Collections.sort(dishList);

        for (Dish dish : dishList) {
            System.out.println(dish);
        }
    }
}