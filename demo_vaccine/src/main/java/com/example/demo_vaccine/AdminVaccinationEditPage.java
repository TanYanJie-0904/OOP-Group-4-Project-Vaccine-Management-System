package com.example.demo_vaccine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminVaccinationEditPage {
    private VaccineManagementSystem app;
    private GridPane pane;

    //UI elements
    private Label idLabel;
    private Button loadButton;
    private Button addButton;
    private Button backButton;

    private ComboBox<String> patientComboBox; // ComboBox for patient ID and name

    //Table structure
    private TableView<VaccinationRecord> table;
    private TableColumn<VaccinationRecord, String> vaccineNameColumn;
    private TableColumn<VaccinationRecord, String> dateColumn;
    private TableColumn<VaccinationRecord, Integer> doseColumn;

    public AdminVaccinationEditPage(VaccineManagementSystem app, List<String> vaccineNames) {
        this.app = app;
        pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);

        idLabel = new Label("Select Patient:");
        patientComboBox = new ComboBox<>();
        loadPatientData(); // Load patient data into ComboBox

        loadButton = new Button("Load Vaccination History");
        addButton = new Button("Add Vaccination Record");
        backButton = new Button("Back");

        // Initialize TableView and Columns
        table = new TableView<>();
        vaccineNameColumn = new TableColumn<>("Vaccine Name");
        dateColumn = new TableColumn<>("Date");
        doseColumn = new TableColumn<>("Dose Number");

        vaccineNameColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        doseColumn.setCellValueFactory(new PropertyValueFactory<>("doseNumber"));

        // Adjust column widths
        vaccineNameColumn.setPrefWidth(200);
        dateColumn.setPrefWidth(150);
        doseColumn.setPrefWidth(100);

        // Add columns to the table
        table.getColumns().addAll(vaccineNameColumn, dateColumn, doseColumn);

        // Adjust table size
        table.setPrefSize(450, 300);

        // Set table styling if needed
        table.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");

        // Add components to the GridPane
        pane.add(idLabel, 0, 0);
        pane.add(patientComboBox, 1, 0); // Add ComboBox to the GridPane
        pane.add(loadButton, 1, 1);
        pane.add(addButton, 2, 1);
        pane.add(table, 0, 2, 3, 1);
        pane.add(backButton, 0, 3);

        // Set button actions
        loadButton.setOnAction(e -> loadVaccinationHistory());
        addButton.setOnAction(e -> openAddVaccinationRecordDialog());
        backButton.setOnAction(e -> app.showDashboardPage("admin"));
    }

    /**
     * Search the id and name for the patient who need to edit the history page after get a vaccine
     */
    private void loadPatientData() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT id, name FROM patients";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                ObservableList<String> patientOptions = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    patientOptions.add(id + "  " + name); // Combine ID and Name for display
                }

                patientComboBox.setItems(patientOptions); // Set items in ComboBox
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * After select the id and name of the patient and select the load vaccination history, will output this patient's vaccination history
     */
    private void loadVaccinationHistory() {
        String selectedItem = patientComboBox.getValue();
        if (selectedItem == null) {
            // Handle case where no patient is selected
            return;
        }

        // Extract patient ID from selected item
        int patientId;
        try {
            // Use regex to find the first sequence of digits
            String[] parts = selectedItem.split("\\s+", 2); // Split only on the first space sequence
            if (parts.length > 0) {
                patientId = Integer.parseInt(parts[0].trim());
            } else {
                // Handle case where splitting did not work as expected
                throw new NumberFormatException("No valid ID found");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle parsing error
            System.err.println("Error parsing patient ID: " + e.getMessage());
            return;
        }

        ObservableList<VaccinationRecord> records = FXCollections.observableArrayList();

        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT * FROM vaccination_history WHERE patient_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, patientId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    boolean hasRecords = false; // Flag to check if any records are found
                    while (resultSet.next()) {
                        hasRecords = true; // Set flag to true if at least one record is found
                        String vaccineName = resultSet.getString("vaccine_name");
                        String date = resultSet.getString("date_of_vaccination");
                        int dose = resultSet.getInt("dose_number");

                        records.add(new VaccinationRecord(vaccineName, date, dose));
                    }
                    if (!hasRecords) {
                        // Add a single record indicating no records found
                        records.add(new VaccinationRecord("No records", "", 0));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        table.setItems(records);
    }

    /**
     * Add new vaccination record from the dialog
     */
    private void openAddVaccinationRecordDialog() {
        // Create a new dialog window
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Vaccination Record");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);

        // Combined Vaccine Name and Date ComboBox
        Label recordLabel = new Label("Select Vaccine and Date:");
        ComboBox<String> recordComboBox = new ComboBox<>();
        recordComboBox.setPrefWidth(300);
        loadVaccineAndDateData(recordComboBox); // Load vaccine and date data into ComboBox

        // Dose Number ComboBox
        Label doseLabel = new Label("Dose Number:");
        ComboBox<Integer> doseComboBox = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3));
        doseComboBox.setPrefWidth(100);

        // Add the labels and inputs to the pane
        pane.add(recordLabel, 0, 0);
        pane.add(recordComboBox, 1, 0);
        pane.add(doseLabel, 0, 1);
        pane.add(doseComboBox, 1, 1);

        // Confirmation buttons
        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");

        pane.add(confirmButton, 1, 2);
        pane.add(cancelButton, 2, 2);

        // Handle button actions
        confirmButton.setOnAction(e -> {
            String selectedRecord = recordComboBox.getValue();
            Integer selectedDose = doseComboBox.getValue();

            if (selectedRecord != null && selectedDose != null) {
                String[] parts = selectedRecord.split(" - ");
                String vaccineName = parts[0];
                String date = parts[1];

                // Insert the record into the database
                addVaccinationRecordToDatabase(vaccineName, date, selectedDose);
                stage.close();
            }
        });

        cancelButton.setOnAction(e -> stage.close());

        // Set up the stage
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Method to load valid vaccine names and dates into the recordComboBox
     */
    private void loadVaccineAndDateData(ComboBox<String> recordComboBox) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT DISTINCT vaccine_name, injection_date FROM injection_schedules ORDER BY vaccine_name, injection_date ASC";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                ObservableList<String> records = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    String vaccineName = resultSet.getString("vaccine_name");
                    String date = resultSet.getString("injection_date");
                    records.add(vaccineName + " - " + date); // Combine Vaccine Name and Date for display
                }
                recordComboBox.setItems(records); // Populate ComboBox with vaccine and date records
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param vaccineName the name of vaccine to be scheduled
     * @param date the date of vaccination to be scheduled
     * @param doseNumber the dose number to be scheduled
     * Method to insert the new vaccination record into the database
     */
    private void addVaccinationRecordToDatabase(String vaccineName, String date, int doseNumber) {
        String selectedItem = patientComboBox.getValue();
        if (selectedItem == null) {
            // Handle case where no patient is selected
            return;
        }

        int patientId = Integer.parseInt(selectedItem.split("\\s+", 2)[0].trim());

        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "INSERT INTO vaccination_history (patient_id, vaccine_name, date_of_vaccination, dose_number) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, patientId);
                statement.setString(2, vaccineName);
                statement.setString(3, date);
                statement.setInt(4, doseNumber);
                statement.executeUpdate();

                // Refresh the table after adding the record
                loadVaccinationHistory();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public GridPane getPane() {
        return pane;
    }
}
