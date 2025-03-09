package com.example;

import java.io.IOException;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Program extends Application {

    private static Stage primaryStage;
    private EmployeeManager employeeManager;
    private PayrollManager payrollManager;
    private ObservableList<Employee> employeeList;
    public User loggedInUser;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        try {
            employeeManager = EmployeeManager.loadData("employees.txt");
            payrollManager = PayrollManager.loadData("payrolls.txt");
        } catch (IOException | ClassNotFoundException e) {
            employeeManager = new EmployeeManager();
            payrollManager = new PayrollManager();
        }
        employeeList = FXCollections.observableArrayList(employeeManager.getAllEmployees());
        showLoginView();
    }

    @Override
    public void stop() {
        try {
            employeeManager.saveData("employees.txt");
            payrollManager.saveData("payrolls.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // login view
    private void showLoginView() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Employee ID or Admin Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if ("admin".equals(username) && "password".equals(password)) {
                loggedInUser = new User(username, password, "Admin");
                showMainView(); // to admin view
            } else {
                Employee employee = employeeManager.getEmployeeById(username);
                if (employee != null && password.equals(employee.getPassword())) {
                    loggedInUser = new User(username, password, "Employee");
                    showEmployeeView(employee); // to employee specific view
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid credentials!");
                    alert.show();
                }
            }
        });

        layout.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);

        Scene loginScene = new Scene(layout, 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    // admin view
    private void showMainView() {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("HR Management System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-alignment: center;");

        Button employeeButton = new Button("Employee Management"); // to employee management view
        employeeButton.setOnAction(e -> showEmployeeManagementView());
        employeeButton.setMinWidth(200);

        Button payrollButton = new Button("Payroll Processing"); // to payroll processing view
        payrollButton.setOnAction(e -> showPayrollProcessingView());
        payrollButton.setMinWidth(200);

        Button reportingButton = new Button("Reporting"); // to reporting view
        reportingButton.setOnAction(e -> showReportingView());
        reportingButton.setMinWidth(200);

        Button chartButton = new Button("Payroll Visualization"); // to payroll chart view
        chartButton.setOnAction(e -> showPayrollChart());
        chartButton.setMinWidth(200);

        Button logoutButton = new Button("Logout"); // to login view
        logoutButton.setOnAction(e -> showLoginView());
        logoutButton.setMinWidth(200);

        mainLayout.getChildren().addAll(titleLabel, employeeButton, payrollButton, reportingButton, chartButton, logoutButton);

        Scene mainScene = new Scene(mainLayout, 600, 500);
        primaryStage.setTitle("HR Management System");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // employee specific view
    private void showEmployeeView(Employee loggedInEmployee) {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Welcome, " + loggedInEmployee.getFullName()); // welcome message
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.setTop(titleLabel);

        VBox actions = new VBox(10);
        Button clockInButton = new Button("Clock In");
        Button clockOutButton = new Button("Clock Out");
        Button viewSalaryButton = new Button("View Salary");

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(150);

        // handle clock in action
        clockInButton.setOnAction(e -> {
            if (loggedInEmployee.isClockedIn()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You are already clocked in. Please clock out first.");
                alert.show();
            } else {
                loggedInEmployee.clockIn();
                logArea.appendText("Clocked In at: " + loggedInEmployee.getClockInTime() + "\n\n");
            }
        });

        // handle clock out action and pay calculation
        clockOutButton.setOnAction(e -> {
            if (!loggedInEmployee.isClockedIn()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "You are not clocked in. Please clock in first.");
                alert.show();
            } else {
                loggedInEmployee.clockOut();
                double hoursWorked = loggedInEmployee.getHoursWorked();
                long totalMinutes = Math.round(hoursWorked * 60);
                long workedHours = totalMinutes / 60;
                long workedMinutes = totalMinutes % 60;
                double regularHours = loggedInEmployee.calculateRegularHours();
                double overtimeHours = loggedInEmployee.calculateOvertimeHours();
                double hourlyRate = loggedInEmployee.getHourlyRate();
                double regularPay = regularHours * hourlyRate;
                double overtimePay = overtimeHours * hourlyRate * 1.5;
                double totalPay = regularPay + overtimePay;
                loggedInEmployee.addToGrossPay(totalPay);
                // log clock out and pay calculation
                logArea.appendText("Clocked Out at: " + loggedInEmployee.getClockOutTime() + "\n\n");
                logArea.appendText("Time Worked: " + workedHours + " hours and " + workedMinutes + " minutes\n");
                logArea.appendText("Regular Hours: " + String.format("%.2f", regularHours) + " hours\n");
                logArea.appendText("Overtime Hours: " + String.format("%.2f", overtimeHours) + " hours\n");
                logArea.appendText("Regular Pay: $" + String.format("%.2f", regularPay) + "\n");
                logArea.appendText("Overtime Pay: $" + String.format("%.2f", overtimePay) + "\n");
                logArea.appendText("Total Pay Added: $" + String.format("%.2f", totalPay) + "\n\n");
            }
        });

        // handle view salary action
        viewSalaryButton.setOnAction(e -> {
            if (loggedInEmployee.getGrossPay() > 0) {
                logArea.appendText("Salary Details:\n");
                logArea.appendText("Gross Pay: $" + String.format("%.2f", loggedInEmployee.getGrossPay()) + "\n");
                logArea.appendText("Tax Deducted: $" + String.format("%.2f", loggedInEmployee.getTaxDeducted()) + "\n");
                logArea.appendText("Net Pay: $" + String.format("%.2f", loggedInEmployee.getNetPay()) + "\n\n");
            } else {
                logArea.appendText("Payroll data not available. Please contact your admin.\n\n");
            }
        });

        Button logoutButton = new Button("Logout"); // to login view
        logoutButton.setOnAction(e -> showLoginView());

        actions.getChildren().addAll(clockInButton, clockOutButton, viewSalaryButton, logoutButton);
        layout.setCenter(actions);
        layout.setBottom(logArea);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Employee Dashboard");
    }

    // employee management view
    private void showEmployeeManagementView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Employee Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.setTop(titleLabel);

        // table to display employee details
        TableView<Employee> employeeTable = new TableView<>(employeeList);
        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().fullNameProperty());
        TableColumn<Employee, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(data -> data.getValue().departmentProperty());
        TableColumn<Employee, Double> grossPayColumn = new TableColumn<>("Gross Pay");
        grossPayColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getGrossPay()).asObject());

        employeeTable.getColumns().addAll(idColumn, nameColumn, departmentColumn, grossPayColumn);
        layout.setCenter(employeeTable);

        // input fields for employee management
        VBox controls = new VBox(10);
        HBox inputFields = new HBox(10);
        TextField idField = new TextField();
        idField.setPromptText("ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField departmentField = new TextField();
        departmentField.setPromptText("Department");
        TextField hourlyRateField = new TextField();
        hourlyRateField.setPromptText("Hourly Rate");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        inputFields.getChildren().addAll(idField, nameField, departmentField, hourlyRateField, passwordField);

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(150);
        logArea.setPromptText("Employee Activity Logs");

        HBox buttons = new HBox(10);

        Button clockInButton = new Button("Clock In");
        clockInButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.isClockedIn()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, selected.getFullName() + " is already clocked in. Please clock out first.");
                    alert.show();
                } else {
                    selected.clockIn();
                    selected.addTimeRecord(new ClockInClockOut(selected.getId(), "Clocked In"));
                    employeeTable.refresh();
                    logArea.appendText("Clocked In: " + selected.getFullName() + "\n");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an employee to Clock In.");
                alert.show();
            }
        });

        Button clockOutButton = new Button("Clock Out");
        clockOutButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (!selected.isClockedIn()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, selected.getFullName() + " is not clocked in. Please clock in first.");
                    alert.show();
                } else {
                    try {
                        selected.clockOut();
                        selected.addTimeRecord(new ClockInClockOut(selected.getId(), "Clocked Out"));
                        employeeTable.refresh();
                        logArea.appendText("Clocked Out: " + selected.getFullName() + "\n");
                    } catch (IllegalStateException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                        alert.show();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an employee to Clock Out.");
                alert.show();
            }
        });

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            if (idField.getText().isEmpty() || nameField.getText().isEmpty() ||
                    departmentField.getText().isEmpty() || hourlyRateField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled out!");
                alert.show();
            } else {
                boolean exists = employeeList.stream()
                        .anyMatch(emp -> emp.getId().equals(idField.getText()));
                if (exists) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "An employee with this ID already exists!");
                    alert.show();
                } else {
                    try {
                        Employee newEmployee = new Employee(
                                idField.getText(),
                                nameField.getText(),
                                departmentField.getText(),
                                Double.parseDouble(hourlyRateField.getText()),
                                0.0 // initial grossPay is 0.0
                        );
                        newEmployee.setPassword(passwordField.getText());
                        employeeManager.addEmployee(newEmployee);
                        employeeList.add(newEmployee);

                        logArea.appendText("Added employee: " + newEmployee.getFullName() + "\n");

                        idField.clear();
                        nameField.clear();
                        departmentField.clear();
                        hourlyRateField.clear();
                        passwordField.clear();
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input for hourly rate!");
                        alert.show();
                    }
                }
            }
        });

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (idField.getText().isEmpty() || nameField.getText().isEmpty() ||
                        departmentField.getText().isEmpty() || hourlyRateField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled to update an employee!");
                    alert.show();
                } else {
                    try {
                        selected.setId(idField.getText());
                        selected.setFullName(nameField.getText());
                        selected.setDepartment(departmentField.getText());
                        selected.setHourlyRate(Double.parseDouble(hourlyRateField.getText()));
                        selected.setPassword(passwordField.getText());

                        idField.clear();
                        nameField.clear();
                        departmentField.clear();
                        hourlyRateField.clear();
                        passwordField.clear();

                        employeeTable.refresh();
                        logArea.appendText("Updated employee: " + selected.getFullName() + "\n");
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Enter valid numbers for Hourly Rate and Leave Hours.");
                        alert.show();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an employee to update.");
                alert.show();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Employee selected = employeeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                employeeManager.deleteEmployee(selected.getId());
                employeeList.remove(selected);
                logArea.appendText("Deleted employee: " + selected.getFullName() + "\n");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Select an employee to delete.");
                alert.show();
            }
        });

        buttons.getChildren().addAll(clockInButton, clockOutButton, addButton, updateButton, deleteButton);
        controls.getChildren().addAll(inputFields, buttons, logArea);
        layout.setBottom(controls);

        Button backButton = new Button("Back"); // to main view
        backButton.setOnAction(e -> showMainView());
        controls.getChildren().add(backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Employee Management");
        primaryStage.setScene(scene);
    }

    // payroll processing view
    private void showPayrollProcessingView() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Payroll Processing");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.setTop(titleLabel);

        // table to display employee psyroll details
        TableView<Employee> employeeTable = new TableView<>(employeeList);
        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().fullNameProperty());
        TableColumn<Employee, Double> grossPayColumn = new TableColumn<>("Gross Pay");
        grossPayColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getGrossPay()).asObject());

        employeeTable.getColumns().addAll(idColumn, nameColumn, grossPayColumn);

        // input fields for payroll processing
        VBox controls = new VBox(10);
        TextField taxRateField = new TextField();
        taxRateField.setPromptText("Tax Rate (e.g., 0.2 for 20%)");
        Button processAllButton = new Button("Process Payroll for All");

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(150);

        // process payroll for all employees
        processAllButton.setOnAction(e -> {
            try {
                double taxRate = Double.parseDouble(taxRateField.getText());
                logArea.clear();
                payrollManager.clearPayrolls(); // clear previous payrolls

                // process payroll for each employee and log the results
                for (Employee employee : employeeList) {
                    Payroll payroll = payrollManager.processPayroll(employee, taxRate);
                    logArea.appendText("Processed payroll for " + employee.getFullName() + "\n");
                    logArea.appendText("Gross Pay: $" + String.format("%.2f", payroll.getGrossPay()) + "\n");
                    logArea.appendText("Tax Deducted: $" + String.format("%.2f", payroll.getTaxDeducted()) + "\n");
                    logArea.appendText("Net Pay: $" + String.format("%.2f", payroll.getNetPay()) + "\n\n");
                }
                employeeTable.refresh(); // refresh the table to show updated data
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Enter a valid tax rate!");
                alert.show();
            }
        });

        controls.getChildren().addAll(taxRateField, processAllButton, logArea);

        Button backButton = new Button("Back"); // to main view
        backButton.setOnAction(e -> showMainView());

        VBox vbox = new VBox(20, employeeTable, controls, backButton);
        layout.setCenter(vbox);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Payroll Processing");
        primaryStage.setScene(scene);
    }

    // reporting view
    private void showReportingView() {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Reporting");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // display payroll report
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefHeight(300);

        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setOnAction(e -> {
            StringBuilder report = new StringBuilder();
            report.append("Total Employees: ").append(employeeList.size()).append("\n");
            double totalPayroll = employeeList.stream().mapToDouble(emp -> emp.getGrossPay()).sum();
            report.append("Total Payroll: $").append(totalPayroll).append("\n");
            reportArea.setText(report.toString());
        });

        Button backButton = new Button("Back"); // to main view
        backButton.setOnAction(e -> showMainView());

        layout.getChildren().addAll(titleLabel, generateReportButton, reportArea, backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Reporting");
        primaryStage.setScene(scene);
    }

    // payroll chart view
    private void showPayrollChart() {
        // x-axis is department
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Department");

        // y-axis is total payroll
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Payroll");

        // create bar chart
        BarChart<String, Number> payrollChart = new BarChart<>(xAxis, yAxis);
        payrollChart.setTitle("Departmental Payroll Visualization");

        // create a data series to represent payroll data
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Total Payroll by Department");

        // populate the data series with payroll data for each department
        for (Department department : employeeManager.getAllDepartments()) {
            double totalPayroll = department.calculateTotalPayroll();
            dataSeries.getData().add(new XYChart.Data<>(department.getName(), totalPayroll));
        }

        // add the data series to the chart
        payrollChart.getData().add(dataSeries);

        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().add(payrollChart);

        Button backButton = new Button("Back"); // to main view
        backButton.setOnAction(e -> showMainView());

        layout.getChildren().add(backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setTitle("Departmental Payroll Visualization");
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}