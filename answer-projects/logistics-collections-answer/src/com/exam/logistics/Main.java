import java.util.*;

class Package implements Comparable<Package> {
    private String id;

    public Package(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Package)) return false;
        Package pkg = (Package) o;
        return Objects.equals(id, pkg.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Package other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return "Package{id='" + id + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Package> packageSet = new HashSet<>();

        packageSet.add(new Package("A002"));
        packageSet.add(new Package("A001"));
        packageSet.add(new Package("A003"));
        packageSet.add(new Package("A002")); // 重复元素

        List<Package> packageList = new ArrayList<>(packageSet);
        Collections.sort(packageList);

        for (Package pkg : packageList) {
            System.out.println(pkg);
        }
    }
}