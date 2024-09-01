package com.example.demo_vaccine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class VaccinationHistoryPage {
    private VaccineManagementSystem app;
    private GridPane pane;

    //Table Structure
    private TableView<VaccinationRecord> table;
    private TableColumn<VaccinationRecord, String> vaccineNameColumn;
    private TableColumn<VaccinationRecord, String> dateColumn;
    private TableColumn<VaccinationRecord, Integer> doseColumn;

    //UI elements
    private Label headerLabel;
    private Button backButton;

    public VaccinationHistoryPage(VaccineManagementSystem app) {
        this.app = app;
        pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);

        headerLabel = new Label("Your Vaccination History:");
        pane.add(headerLabel, 0, 0, 3, 1); // Place header in row 0

        // Initialize TableView and Columns
        table = new TableView<>();
        vaccineNameColumn = new TableColumn<>("Vaccine Name");
        dateColumn = new TableColumn<>("Date");
        doseColumn = new TableColumn<>("Dose Number");

        vaccineNameColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        doseColumn.setCellValueFactory(new PropertyValueFactory<>("doseNumber"));

        // Set column widths if needed (optional)
        vaccineNameColumn.setPrefWidth(300);
        dateColumn.setPrefWidth(300);
        doseColumn.setPrefWidth(200);

        // Add columns to the table
        table.getColumns().addAll(vaccineNameColumn, dateColumn, doseColumn);

        // Adjust table size
        table.setPrefSize(800, 400);

        // Set table styling if needed
        table.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");

        // Add TableView to the GridPane
        pane.add(table, 0, 1, 3, 1); // Place table in row 1

        // Add Back button for back user dashboard
        backButton = new Button("Back");
        pane.add(backButton, 0, 2, 3, 1); // Place button in row 2

        // Load data into the table
        loadVaccinationHistory();

        // Set up button action
        backButton.setOnAction(e -> app.showDashboardPage("User"));
    }

    /**
     * Find the specific patients vaccine history
     */
    private void loadVaccinationHistory() {
        int userId = app.getUserId();
        ObservableList<VaccinationRecord> records = FXCollections.observableArrayList();

        try (Connection connection = DatabaseUtil.getConnection()) {
            // First, get the patient_id from the patients table using the user_id
            String patientIdSql = "SELECT id FROM patients WHERE user_id = ?";
            int patientId = -1; // Default invalid patient_id

            try (PreparedStatement patientIdStatement = connection.prepareStatement(patientIdSql)) {
                patientIdStatement.setInt(1, userId);
                try (ResultSet patientIdResultSet = patientIdStatement.executeQuery()) {
                    if (patientIdResultSet.next()) {
                        patientId = patientIdResultSet.getInt("id");
                    }
                }
            }

            if (patientId == -1) {
                System.out.println("No patient record found for user ID: " + userId);
                return; // Exit if no patient record is found
            }

            // Then, get the vaccination records for the found patient_id
            String vaccinationSql = "SELECT vaccine_name, date_of_vaccination, dose_number " +
                    "FROM vaccination_history WHERE patient_id = ?";
            try (PreparedStatement vaccinationStatement = connection.prepareStatement(vaccinationSql)) {
                vaccinationStatement.setInt(1, patientId);
                try (ResultSet vaccinationResultSet = vaccinationStatement.executeQuery()) {
                    while (vaccinationResultSet.next()) {
                        String vaccineName = vaccinationResultSet.getString("vaccine_name");
                        String date = vaccinationResultSet.getDate("date_of_vaccination").toLocalDate().toString();
                        int dose = vaccinationResultSet.getInt("dose_number");

                        records.add(new VaccinationRecord(vaccineName, date, dose));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        table.setItems(records);
    }

    public GridPane getPane() {
        return pane;
    }
}
