package com.example.demo_vaccine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.*;

public class RegistrationPage {
    private VaccineManagementSystem app;
    private GridPane pane;

    //UI components
    private Label userLabel;
    private TextField userField;
    private Label passLabel;
    private PasswordField passField;
    private Button registerButton;
    private Button backButton;
    private HBox buttonBox;


    //constructor
    public RegistrationPage(VaccineManagementSystem app) {
        this.app = app;
        pane = new GridPane();
        pane.setPadding(new Insets(20)); // Padding around the grid
        pane.setVgap(15); // Vertical gap between rows
        pane.setHgap(10); // Horizontal gap between columns
        pane.setAlignment(Pos.CENTER); // Center the grid

        // Create UI components
        userLabel = new Label("Username:");
        userField = new TextField();
        passLabel = new Label("Password:");
        passField = new PasswordField();

        //Create Button for register and back previous page
        registerButton = new Button("Register");
        backButton = new Button("Back");

        // Add components to the GridPane
        pane.add(userLabel, 0, 0);
        pane.add(userField, 1, 0);
        pane.add(passLabel, 0, 1);
        pane.add(passField, 1, 1);

        // Create an HBox for buttons to keep them aligned horizontally
        buttonBox = new HBox(10, registerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        pane.add(buttonBox, 1, 3);

        // Handle registration button action
        registerButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            String role = "user"; //set 'user' as role default
            try (Connection connection = DatabaseUtil.getConnection()) {
                String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, username);
                    statement.setString(2, password);
                    statement.setString(3, role);
                    statement.executeUpdate();

                    // Get the generated user ID
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        app.setUser(userId, role); // Set the user in the application

                        // Create a patient profile if the role is 'user'
                        if (role.equals("user")) {
                            createPatientProfile(userId, username);
                        }


                        app.showLoginPage();

                        // Show success alert
                        showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "An error occurred during registration.");
            }
        });

        // Handle back button action
        backButton.setOnAction(e -> app.showLoginPage());
    }

    /**
     * @param userId the user id in the database
     * @param name the patient name in the patients table
     */
    private void createPatientProfile(int userId, String name) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Define the SQL statement with default values for other fields
            String sql = "INSERT INTO patients (name, dob, gender, contact_info, medical_history, user_id) " +
                    "VALUES (?, '1990-01-01', 'other', NULL, NULL, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the values for name and user_id
                statement.setString(1, name);
                statement.setInt(2, userId);

                // Execute the update
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param alertType the type of alert message  such as information, error...
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
