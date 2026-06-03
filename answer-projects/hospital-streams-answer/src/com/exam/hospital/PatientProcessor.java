import java.util.List;
import java.util.stream.Collectors;

class Patient {
    private boolean valid;
    private String name;

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }
}

public class PatientProcessor {
    public List<String> processList(List<Patient> list) {
        return list.stream()
                .filter(patient -> patient.isValid())
                .map(Patient::getName)
                .collect(Collectors.toList());
    }
}