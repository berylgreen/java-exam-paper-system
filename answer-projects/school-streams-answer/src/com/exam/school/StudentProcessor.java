import java.util.List;
import java.util.stream.Collectors;

class Student {
    private boolean valid;
    private String name;

    public Student(boolean valid, String name) {
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

public class StudentProcessor {
    public List<String> processList(List<Student> list) {
        return list.stream()
                .filter(student -> student.isValid())
                .map(student -> student.getName())
                .collect(Collectors.toList());
    }
}