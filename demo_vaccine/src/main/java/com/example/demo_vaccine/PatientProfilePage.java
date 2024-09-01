package com.example.demo_vaccine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.*;
import java.time.LocalDate;

public class PatientProfilePage {
    private VaccineManagementSystem app;
    private GridPane pane;

    // UI components
    private Label nameLabel;
    private TextField nameField;
    private Label dobLabel;
    private DatePicker dobDatePicker;
    private Label genderLabel;
    private ComboBox<String> genderComboBox;
    private Label contactLabel;
    private TextField contactField;
    private Label historyLabel;
    private TextField historyField;
    private Button saveButton;
    private Button backButton;
    private HBox buttonBox;

    public PatientProfilePage(VaccineManagementSystem app) {
        this.app = app;
        pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setVgap(15);
        pane.setHgap(10);
        pane.setAlignment(Pos.CENTER);

        // Create UI components
        nameLabel = new Label("Name:");
        nameField = new TextField();
        dobLabel = new Label("Date of Birth:");
        dobDatePicker = new DatePicker();
        genderLabel = new Label("Gender:");
        genderComboBox = new ComboBox<>();
        contactLabel = new Label("Contact Info:");
        contactField = new TextField();
        historyLabel = new Label("Medical History:");
        historyField = new TextField();

        saveButton = new Button("Save");
        backButton = new Button("Back");

        // Configure the Gender ComboBox
        genderComboBox.getItems().addAll("Male", "Female");

        // Add components to the GridPane
        pane.add(nameLabel, 0, 0);
        pane.add(nameField, 1, 0);
        pane.add(dobLabel, 0, 1);
        pane.add(dobDatePicker, 1, 1);
        pane.add(genderLabel, 0, 2);
        pane.add(genderComboBox, 1, 2);
        pane.add(contactLabel, 0, 3);
        pane.add(contactField, 1, 3);
        pane.add(historyLabel, 0, 4);
        pane.add(historyField, 1, 4);

        // Create an HBox for buttons and center them
        buttonBox = new HBox(10, saveButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        pane.add(buttonBox, 1, 5);

        // Load patient profile based on the logged-in user's ID
        loadPatientProfile(app.getUserId(), nameField, dobDatePicker, genderComboBox, contactField, historyField);


        // Handle save button action
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            LocalDate dob = dobDatePicker.getValue();
            String gender = genderComboBox.getValue();
            String contactInfo = contactField.getText();
            String medicalHistory = historyField.getText();
            updatePatientProfile(app.getUserId(), name, dob, gender, contactInfo, medicalHistory);
            app.setName(name);
            app.setGender(gender);
        });

        //Handle back button action
        backButton.setOnAction(e -> {
            app.showDashboardPage("user");
        });

    }

    /**
     * @param userId the user id of the logged in account
     * @param nameField the name to store in the patients table in the database
     * @param dobDatePicker to select the DOB of the patient
     * @param genderComboBox to save the gender of the patients
     * @param contactField to store the contact number of the patient
     * @param historyField to store the medical history of the patient
     */
    private void loadPatientProfile(int userId, TextField nameField, DatePicker dobDatePicker, ComboBox<String> genderComboBox, TextField contactField, TextField historyField) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM patients WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String name =resultSet.getString("name");
                        app.setName(name);
                        nameField.setText(name);
                        Date dob = resultSet.getDate("dob");
                        if (dob != null && !dob.toString().equals("0000-00-00")) {
                            dobDatePicker.setValue(dob.toLocalDate());
                        } else {
                            dobDatePicker.setValue(null);
                        }

                        String gender = resultSet.getString("gender");
                        app.setGender(gender);
                        genderComboBox.setValue(gender);
                        contactField.setText(resultSet.getString("contact_info"));
                        historyField.setText(resultSet.getString("medical_history"));
                    } else {
                        nameField.setText("Patient profile not found.");
                        dobDatePicker.setValue(null);
                        genderComboBox.setValue("");
                        contactField.setText("");
                        historyField.setText("");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * When the save button is selected, the new information of the patients will be update based on the user's input
     */
    private void updatePatientProfile(int userId, String name, LocalDate dob, String gender, String contactInfo, String medicalHistory) {
        String sql = "UPDATE patients SET name = ?, dob = ?, gender = ?, contact_info = ?, medical_history = ? WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            if (dob != null) {
                statement.setDate(2, Date.valueOf(dob));
            } else {
                statement.setNull(2, java.sql.Types.DATE);
            }
            statement.setString(3, gender);
            statement.setString(4, contactInfo);
            statement.setString(5, medicalHistory);
            statement.setInt(6, userId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Update Successful", "Patient profile updated successfully!");
            } else {
                System.out.println("No records updated. Please check the user ID.");
                showAlert(Alert.AlertType.ERROR, "Error Message!", "No records updated. Please check the user ID.");
            }
        } catch (SQLException ex) {
            System.err.println("Error updating patient profile: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * @param alertType the type of the alert such as information, error...
     * @param title the title of the message box
     * @param message the information to be delivered to the user
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public GridPane getPane() {
        return pane;
    }
}
