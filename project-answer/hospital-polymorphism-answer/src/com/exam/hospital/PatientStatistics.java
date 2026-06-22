package com.exam.hospital;

abstract class Patient {
    // 抽象方法：不同类型的病患有不同的处理逻辑
    public abstract void process();
}

class RegularPatient extends Patient {
    @Override
    public void process() {
        System.out.println("处理普通病患的统计逻辑");
    }
}

class VIPPatient extends Patient {
    @Override
    public void process() {
        System.out.println("处理 VIP 病患的统计逻辑");
    }
}

class EmergencyPatient extends Patient {
    @Override
    public void process() {
        System.out.println("处理急诊病患的统计逻辑");
    }
}

public class PatientStatistics {
    // 统一处理所有病患对象，只面向抽象类型编程
    public void processAll(Patient[] patients) {
        for (Patient patient : patients) {
            patient.process();   // 多态调用
        }
    }

    public static void main(String[] args) {
        Patient[] patients = {
            new RegularPatient(),
            new VIPPatient(),
            new EmergencyPatient()
        };

        PatientStatistics statistics = new PatientStatistics();
        statistics.processAll(patients);
    }
}
