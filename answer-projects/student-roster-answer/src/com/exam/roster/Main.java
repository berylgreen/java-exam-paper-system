import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class Student implements Comparable<Student> {
    private String studentId;

    public Student(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public int compareTo(Student other) {
        return this.studentId.compareTo(other.studentId);
    }

    @Override
    public String toString() {
        return "Student{studentId='" + studentId + "'}";
    }
}

class StudentManager {
    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getSortedStudents() {
        List<Student> list = new ArrayList<>(students);
        Collections.sort(list);
        return list;
    }
}

public class Main {
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();

        manager.addStudent(new Student("1002"));
        manager.addStudent(new Student("1001"));
        manager.addStudent(new Student("1003"));
        manager.addStudent(new Student("1002"));

        List<Student> result = manager.getSortedStudents();
        for (Student student : result) {
            System.out.println(student);
        }
    }
}