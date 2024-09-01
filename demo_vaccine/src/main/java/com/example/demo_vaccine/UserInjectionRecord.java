package com.example.demo_vaccine;

public class UserInjectionRecord {
    private String vaccineName;
    private String nextInjectionDate;
    private int dosage;

    // Constructor
    public UserInjectionRecord(String vaccineName, String nextInjectionDate, int dosage) {
        this.vaccineName = vaccineName != null ? vaccineName : "N/A";
        this.nextInjectionDate = nextInjectionDate != null ? nextInjectionDate : "N/A";
        this.dosage = dosage;
    }

    /**
     * @return vaccine name
     * Getters method
     */
    public String getVaccineName() {
        return vaccineName;
    }

    public String getNextInjectionDate() {
        return nextInjectionDate;
    }

    public int getDosage() {
        return dosage;
    }

    /**
     * etters (optional, in case you need to modify fields after creation)
     */
    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName != null ? vaccineName : "N/A";
    }


    public void setNextInjectionDate(String nextInjectionDate) {
        this.nextInjectionDate = nextInjectionDate != null ? nextInjectionDate : "N/A";
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }
}
