package com.example.demo_vaccine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminInjectionSchedulePage {
    private VaccineManagementSystem app;
    private GridPane pane;
    private CheckBox[] patientCheckBoxes;

    //UI Components
    private Label headerLabel;
    private Button backButton;
    private Label vaccineNameLabel;
    private TextField vaccineNameField;
    private Label injectionDateLabel;
    private DatePicker injectionDatePicker;
    private Label dosageLabel;
    private TextField dosageField;
    private Button scheduleButton;
    private Label patientsLabel;

    public AdminInjectionSchedulePage(VaccineManagementSystem app) {
        this.app = app;
        pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);

        //Add label
        headerLabel = new Label("Schedule Injections for Multiple Users");
        pane.add(headerLabel, 0, 0, 2, 1);

        //Add button for back to previous page
        backButton = new Button("Back");
        pane.add(backButton, 0, 1, 2, 1);

        //Add label before the text field box for name insertion
        vaccineNameLabel = new Label("Vaccine Name:");
        vaccineNameField = new TextField();
        pane.add(vaccineNameLabel, 0, 2);
        pane.add(vaccineNameField, 1, 2);

        //Add label before the date selection
        injectionDateLabel = new Label("Injection Date:");
        injectionDatePicker = new DatePicker();
        pane.add(injectionDateLabel, 0, 3);
        pane.add(injectionDatePicker, 1, 3);

        ///Add label before the text field box for dosage insertion
        dosageLabel = new Label("Dosage:");
        dosageField = new TextField();
        pane.add(dosageLabel, 0, 4);
        pane.add(dosageField, 1, 4);

        // Place scheduleButton and backButton after checkboxes
        scheduleButton = new Button("Schedule Injections");
        pane.add(scheduleButton, 0, 18, 2, 1);

        // Load patients and add checkboxes
        patientsLabel = new Label("Patient List:");
        pane.add(patientsLabel, 0, 5);
        loadPatientList();




        //set button actions
        scheduleButton.setOnAction(e -> {
            String vaccineName = vaccineNameField.getText();
            LocalDate injectionDate = injectionDatePicker.getValue();
            int dosage = Integer.parseInt(dosageField.getText());

            scheduleInjections(vaccineName, injectionDate, dosage);

            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null); // No header text
            alert.setContentText("Injections scheduled successfully.");
            alert.showAndWait();
        });

        //set button actions
        backButton.setOnAction(e -> app.showDashboardPage("admin")); // Method to return to Admin Dashboard
    }

    /**
     * This method is to load the patient's id and name from database
     */
    private void loadPatientList() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT p.id, p.name FROM patients p"; // Adjust SQL to match your schema
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                int rowIndex = 5; // Adjust starting row index to place checkboxes above buttons
                ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    int patientId = resultSet.getInt("id");
                    String patientName = resultSet.getString("name");

                    CheckBox patientCheckBox = new CheckBox(patientName);
                    patientCheckBox.setId(String.valueOf(patientId)); // Store patient ID as ID

                    checkBoxes.add(patientCheckBox);
                    pane.add(patientCheckBox, 1, rowIndex++);
                }

                patientCheckBoxes = checkBoxes.toArray(new CheckBox[0]);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param vaccineName is the name of the vaccine to be scheduled
     * @param injectionDate is the date of the vaccination to be scheduled
     * @param dosage is the injection dose for the scheduled injection
     */
    private void scheduleInjections(String vaccineName, LocalDate injectionDate, int dosage) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Insert the schedule
            String sql = "INSERT INTO injection_schedules (vaccine_name, injection_date, dosage) VALUES (?, ?, ?)";
            int scheduleId;
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, vaccineName);
                statement.setDate(2, java.sql.Date.valueOf(injectionDate));
                statement.setInt(3, dosage);
                statement.executeUpdate();

                // Retrieve generated schedule ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        scheduleId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve schedule ID.");
                    }
                }
            }

            // Update user injections
            sql = "INSERT INTO user_injections (patient_id, user_id, schedule_id, next_injection_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (CheckBox checkBox : patientCheckBoxes) {
                    if (checkBox.isSelected()) {
                        int patientId = Integer.parseInt(checkBox.getId());
                        int userId = getUserIdFromPatientId(patientId); // Get user ID associated with patient
                        statement.setInt(1, patientId);
                        statement.setInt(2, userId);
                        statement.setInt(3, scheduleId);
                        statement.setDate(4, java.sql.Date.valueOf(injectionDate)); // Set last injection date
                        statement.addBatch(); // Batch update for selected patients
                    }
                }
                statement.executeBatch(); // Execute batch update
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param patientId is the patient id which to find the user id
     * @return the result(user id)
     */
    private int getUserIdFromPatientId(int patientId) throws SQLException {
        String sql = "SELECT user_id FROM patients WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                } else {
                    throw new SQLException("User ID not found for patient ID: " + patientId);
                }
            }
        }
    }


    /**
     * @return vaccine name
     */
    public List<String> getAvailableVaccineNames() {
        List<String> vaccineNames = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT DISTINCT vaccine_name FROM injection_schedules";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vaccineNames.add(resultSet.getString("vaccine_name"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return vaccineNames;
    }

    public GridPane getPane() {
        return pane;
    }
}
