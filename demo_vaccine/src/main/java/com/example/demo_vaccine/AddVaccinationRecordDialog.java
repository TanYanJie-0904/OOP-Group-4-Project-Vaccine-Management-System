package com.example.demo_vaccine;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class AddVaccinationRecordDialog extends Stage {

    private Label vaccineNameLabel;
    private ComboBox<String> vaccineNameComboBox;
    private Label dateLabel;
    private DatePicker datePicker;
    private Label doseLabel;
    private TextField doseNumberField;
    private Button confirmButton;
    private Button cancelButton;
    private boolean confirmed = false;

    public AddVaccinationRecordDialog(Stage owner, List<String> vaccineNames) {
        setTitle("Add Vaccination Record");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);

        vaccineNameLabel = new Label("Vaccine Name:");
        vaccineNameComboBox = new ComboBox<>(FXCollections.observableArrayList(vaccineNames));
        dateLabel = new Label("Date:");
        datePicker = new DatePicker();
        doseLabel = new Label("Dose Number:");
        doseNumberField = new TextField();

        confirmButton = new Button("Confirm");
        cancelButton = new Button("Cancel");

        pane.add(vaccineNameLabel, 0, 0);
        pane.add(vaccineNameComboBox, 1, 0);
        pane.add(dateLabel, 0, 1);
        pane.add(datePicker, 1, 1);
        pane.add(doseLabel, 0, 2);
        pane.add(doseNumberField, 1, 2);
        pane.add(confirmButton, 0, 3);
        pane.add(cancelButton, 1, 3);

        Scene scene = new Scene(pane);
        setScene(scene);

        confirmButton.setOnAction(e -> {
            confirmed = true;
            close();
        });

        cancelButton.setOnAction(e -> close());
    }


    /**
     * @return vaccine names from the exist vaccine in the scheduled table
     */
    public String getVaccineName() {
        return vaccineNameComboBox.getValue();
    }

    /**
     * @return date of the selected vaccine
     */
    public String getDate() {
        return datePicker.getValue().toString();
    }

    /**
     * @return the dosage
     */
    public int getDoseNumber() {
        return Integer.parseInt(doseNumberField.getText());
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}