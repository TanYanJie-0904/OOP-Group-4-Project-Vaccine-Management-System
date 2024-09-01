package com.example.demo_vaccine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VaccineManagementSystem extends Application {
    private Stage primaryStage;
    private int userId; // Store the logged-in user's ID
    private String userRole; // To track the logged-in user's role (user/admin)
    private String gender;
    private String name;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Vaccine Management System");

        showLoginPage();
    }


    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        Scene loginScene = new Scene(loginPage.getPane(), 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public void showRegistrationPage() {
        RegistrationPage registrationPage = new RegistrationPage(this);
        Scene registrationScene = new Scene(registrationPage.getPane(), 300, 250);
        primaryStage.setScene(registrationScene);
    }

    public void setUser(int userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        showDashboardPage(userRole); // Automatically show the dashboard after setting the user
    }

    public int getUserId() {
        return userId;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getName(){
        return name;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setGender(String gender) {
        this.gender=gender;
    }

    public String getGender(){
        return gender;
    }

    public void showDashboardPage(String role) {
        this.userRole = role;
        DashboardPage dashboard;
        Scene scene; // Declare scene outside of the if-else blocks

        if (role.equals("admin")) {
            dashboard = new AdminDashboardPage(this);
            scene = new Scene(new BorderPane(dashboard.getPane()), 300, 500); // Initialize scene for admin
        } else {
            dashboard = new UserDashboardPage(this);
            scene = new Scene(new BorderPane(dashboard.getPane()), 650, 500); // Initialize scene for user
        }

        primaryStage.setScene(scene); // Now, 'scene' can be accessed here
        primaryStage.setTitle(role + " Dashboard");
        primaryStage.show();
    }

    public void showUserInjectionSchedulePage() {
        UserInjectionSchedulePage userInjectionSchedulePage = new UserInjectionSchedulePage(this);
        Scene userInjectionScheduleScene = new Scene(userInjectionSchedulePage.getPane(), 830, 400);
        primaryStage.setScene(userInjectionScheduleScene);
    }

    public void showPatientProfilePage() {
        PatientProfilePage patientProfilePage = new PatientProfilePage(this);
        Scene patientProfileScene = new Scene(patientProfilePage.getPane(), 500, 300);
        primaryStage.setScene(patientProfileScene);
    }

    public void showVaccinationHistoryPage() {
        VaccinationHistoryPage vaccinationHistoryPage = new VaccinationHistoryPage(this);
        Scene vaccinationHistoryScene = new Scene(vaccinationHistoryPage.getPane(), 830, 400);
        primaryStage.setScene(vaccinationHistoryScene);
    }


    public void showAdminInjectionSchedulePage() {
        AdminInjectionSchedulePage adminInjectionSchedulePage = new AdminInjectionSchedulePage(this);
        Scene adminInjectionScheduleScene = new Scene(adminInjectionSchedulePage.getPane(), 600, 400);
        primaryStage.setScene(adminInjectionScheduleScene);
    }


    public void showAdminVaccinationEditPage() {
        // Ensure that the AdminInjectionSchedulePage is properly initialized
        AdminInjectionSchedulePage adminInjectionSchedulePage = new AdminInjectionSchedulePage(this);

        // Fetch the list of available vaccine names
        List<String> vaccineNames = adminInjectionSchedulePage.getAvailableVaccineNames();

        // Create the AdminVaccinationEditPage with the list of vaccine names
        AdminVaccinationEditPage adminVaccinationEditPage = new AdminVaccinationEditPage(this, vaccineNames);

        // Set the new scene
        Scene scene = new Scene(adminVaccinationEditPage.getPane(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Vaccination Edit Page");
        primaryStage.show();
    }


    /**
     * Retrieves patient names from the database.
     *
     * @return List of patient names
     */
    public List<String> getPatientNamesFromDatabase() {
        List<String> patientNames = new ArrayList<>();
        String query = "SELECT name FROM patients"; // Adjust based on your actual database schema

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                patientNames.add(name);
            }
        } catch (SQLException e) {
            System.err.println("Error querying database: " + e.getMessage());
        }

        return patientNames;
    }

    /**
     * Writes the list of patient names to a CSV file.
     *
     * @param patientNames List of patient names to be written to the CSV file
     * @param filePath Path of the CSV file to be created
     */
    public void writePatientNamesToCSV(List<String> patientNames, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Name"); // Header of the CSV
            writer.newLine();

            for (String name : patientNames) {
                writer.write(name);
                writer.newLine();
            }

            System.out.println("Patient names have been written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Fetches patient names from the database and writes them to a CSV file.
     *
     * @param filePath Path of the CSV file to be created
     */
    public void exportPatientNamesToCSV(String filePath) {
        List<String> patientNames = getPatientNamesFromDatabase();
        writePatientNamesToCSV(patientNames, filePath);
    }

    public static void main(String[] args) {
        launch(args);
    }
}