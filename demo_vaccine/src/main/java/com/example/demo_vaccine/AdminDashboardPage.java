package com.example.demo_vaccine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.awt.*;

public class AdminDashboardPage extends DashboardPage {

    private Button editVaccinationButton;
    private Button scheduleInjectionsButton; // New button for scheduling injections
    private Button exportPatientNamesButton;

    public AdminDashboardPage(VaccineManagementSystem app) {
        super(app, "Admin");
    }

    /**
     * show the buttons on the admin dashboard page
     */
    @Override
    protected void addRoleSpecificButtons() {
        // Initialize buttons
        editVaccinationButton = new Button("Edit Vaccination Records");
        scheduleInjectionsButton = new Button("Schedule Injections");
        exportPatientNamesButton = new Button("Export Patient Names"); // Initialize new button


        // Configure VBox layout
        pane.setSpacing(10); // Vertical spacing between buttons
        pane.setPadding(new Insets(10)); // Padding around VBox
        pane.setAlignment(Pos.CENTER); // Align buttons to the center

        // Set a maximum width for the VBox
        pane.setMaxWidth(300);
        pane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #bdfdf1;");

        // Add buttons to the VBox
        pane.getChildren().addAll(
                editVaccinationButton,
                scheduleInjectionsButton,
                exportPatientNamesButton
        );

        // Set button actions
        editVaccinationButton.setOnAction(e -> app.showAdminVaccinationEditPage());
        scheduleInjectionsButton.setOnAction(e -> app.showAdminInjectionSchedulePage());
        exportPatientNamesButton.setOnAction(e -> exportPatientNames());// Action for new button
    }

    /**
     * export patient name list
     */
    private void exportPatientNames() {
        // Define the file path where you want to save the CSV file
        String filePath = "patients.csv"; // Or specify the full path if needed

        try {
            app.exportPatientNamesToCSV(filePath);
            showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Patient names have been exported to " + filePath);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Export Error", "An error occurred while exporting patient names: " + ex.getMessage());
        }
    }

    /**
     * Show an alert dialog.
     *
     * @param type The type of alert (information, error, etc.)
     * @param title The title of the alert
     * @param message The message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}