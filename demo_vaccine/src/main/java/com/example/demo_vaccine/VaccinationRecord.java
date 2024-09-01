package com.example.demo_vaccine;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class VaccinationRecord {
    private SimpleStringProperty vaccineName;
    private SimpleStringProperty date;
    private SimpleIntegerProperty doseNumber;

    //constructor
    public VaccinationRecord(String vaccineName, String date, int doseNumber) {
        this.vaccineName = new SimpleStringProperty(vaccineName);
        this.date = new SimpleStringProperty(date);
        this.doseNumber = new SimpleIntegerProperty(doseNumber);
    }

    public String getVaccineName() {
        return vaccineName.get();
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName.set(vaccineName);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public int getDoseNumber() {
        return doseNumber.get();
    }

    public void setDoseNumber(int doseNumber) {
        this.doseNumber.set(doseNumber);
    }
}