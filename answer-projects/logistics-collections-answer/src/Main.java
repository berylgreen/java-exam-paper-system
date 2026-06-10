import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Package {
    private String id;

    public Package(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Package other = (Package) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Package{id='" + id + "'}";
    }
}

public class Main {
    public static void main(String[] args) {
        Set<Package> packageSet = new HashSet<Package>();

        packageSet.add(new Package("A002"));
        packageSet.add(new Package("A001"));
        packageSet.add(new Package("A003"));
        packageSet.add(new Package("A002"));

        List<Package> packageList = new ArrayList<Package>(packageSet);

        packageList.sort(new Comparator<Package>() {
            @Override
            public int compare(Package p1, Package p2) {
                return p1.getId().compareTo(p2.getId());
            }
        });

        for (Package pkg : packageList) {
            System.out.println(pkg);
        }
    }
}