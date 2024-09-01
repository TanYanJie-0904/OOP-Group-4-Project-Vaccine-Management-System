package com.example.demo_vaccine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UserInjectionSchedulePage {
    private VaccineManagementSystem app;
    private GridPane pane;
    private String userName;
    private String userGender;
    private Label headerLabel;
    private Button backButton;

    //Table structure
    private TableView<UserInjectionRecord> table;
    private TableColumn<UserInjectionRecord, String> vaccineNameColumn;
    private TableColumn<UserInjectionRecord, String> nextInjectionDateColumn;
    private TableColumn<UserInjectionRecord, Integer> dosageColumn;


    public UserInjectionSchedulePage(VaccineManagementSystem app) {
        this.app = app;
        pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);
        pane.setVgap(10);

        // Retrieve user information
        userName = app.getName(); // Adjust based on how you retrieve user information
        userGender = app.getGender(); // Adjust based on how you retrieve user information

        Label greetingLabel;

        // Determine salutation
        if(userName == null && userGender == null) {
            String printId = "Hello! User " + String.valueOf(app.getUserId());
            greetingLabel = new Label(printId);
        } else {
            String salutation = userGender != null && userGender.equalsIgnoreCase("female") ? "Ms." : "Mr.";
            String greetingText = salutation + " " + userName;
            greetingLabel = new Label(greetingText);
        }

        pane.add(greetingLabel, 0, 0, 3, 1);

        headerLabel = new Label("Your Injection Schedule:");
        pane.add(headerLabel, 0, 1, 3, 1); // Move headerLabel to row 1

        // Initialize TableView and Columns
        table = new TableView<>();

        // Create columns
        vaccineNameColumn = new TableColumn<>("Vaccine Name");
        nextInjectionDateColumn = new TableColumn<>("Next Injection Date");
        dosageColumn = new TableColumn<>("Dosage");

        // Set cell value factories
        vaccineNameColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        nextInjectionDateColumn.setCellValueFactory(new PropertyValueFactory<>("nextInjectionDate"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));

        // Set column widths if needed (optional)
        vaccineNameColumn.setPrefWidth(300);
        nextInjectionDateColumn.setPrefWidth(300);
        dosageColumn.setPrefWidth(200);

        // Add columns to the table
        table.getColumns().addAll(vaccineNameColumn, nextInjectionDateColumn, dosageColumn);

        // Adjust table size
        table.setPrefSize(800, 400);

        // Add TableView to the GridPane
        pane.add(table, 0, 2, 3, 1); // Move TableView to row 2

        // Add Back to User Dashboard Button
        backButton = new Button("Back to User Dashboard");
        pane.add(backButton, 0, 3, 3, 1); // Move button to row 3

        // Set up button action
        backButton.setOnAction(e -> app.showDashboardPage("user")); // Assuming "user" is the identifier for the user dashboard

        // Load data into the table
        loadUserInjectionSchedule();
    }

    /**
     * Get the information of the scheduled vaccination from the user_injections table
     */
    private void loadUserInjectionSchedule() {
        int userId = app.getUserId(); // Get user ID

        ObservableList<UserInjectionRecord> records = FXCollections.observableArrayList();

        try (Connection connection = DatabaseUtil.getConnection()) {
            // SQL query to get the data from user_injections and join with injection_schedules
            String sql = "SELECT s.vaccine_name, ui.next_injection_date, s.dosage " +
                    "FROM user_injections ui " +
                    "JOIN injection_schedules s ON ui.schedule_id = s.id " +
                    "WHERE ui.user_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String vaccineName = resultSet.getString("vaccine_name");
                        LocalDate nextInjectionDate = resultSet.getDate("next_injection_date") != null ?
                                resultSet.getDate("next_injection_date").toLocalDate() : null;
                        int dosage = resultSet.getInt("dosage");

                        // Convert dates to strings
                        String nextInjectionDateStr = nextInjectionDate != null ? nextInjectionDate.toString() : "N/A";

                        // Add to list
                        records.add(new UserInjectionRecord(vaccineName, nextInjectionDateStr, dosage));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Update TableView with records
        table.setItems(records);
    }

    public GridPane getPane() {
        return pane;
    }
}

