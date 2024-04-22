package com.example.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ViewEmployeesFunctionality extends VBox {

    private TableView<Employee> employeesTable;

    public ViewEmployeesFunctionality(Menu menu) {
        initializeUI(menu);
    }

    private void initializeUI(Menu menu) {

        Label employeesLabel = new Label("Employees Functionality");
        employeesTable = createEmployeesTableView();

        HBox buttonsLayout = new HBox(20);
        Button addButton = new Button("Add Employee");
        addButton.setOnAction(e -> handleAddEmployee());
        Button updateButton = new Button("Update Employee");
        updateButton.setOnAction(e -> handleUpdateEmployee());
        Button deleteButton = new Button("Delete Employee");
        deleteButton.setOnAction(e -> handleDeleteEmployee());
        Button searchButton = new Button("Search Employee");
        searchButton.setOnAction(e -> {
            Employee foundEmployee = handleSearchEmployee();

            if (foundEmployee != null) {
                ObservableList<Employee> foundEmployeeList = FXCollections.observableArrayList();
                foundEmployeeList.add(foundEmployee);
                employeesTable.setItems(foundEmployeeList);
            }
        });

        buttonsLayout.getChildren().addAll(addButton, updateButton, deleteButton, searchButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        this.getChildren().addAll(employeesLabel, employeesTable, buttonsLayout);
        fetchAndDisplayEmployeesData();
        // Set action for the "View Employees" button
    }

    private TableView<Employee> createEmployeesTableView() {
        TableView<Employee> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Employee, String> employeeIDCol = new TableColumn<>("Employee ID");
        TableColumn<Employee, String> firstNameCol = new TableColumn<>("First Name");
        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Last Name");
        TableColumn<Employee, String> positionCol = new TableColumn<>("Position");
        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Salary");
        TableColumn<Employee, LocalDate> hireDateCol = new TableColumn<>("Hire Date");
        TableColumn<Employee, LocalDate> birthDateCol = new TableColumn<>("Birth Date");
        TableColumn<Employee, String> storeIDCol = new TableColumn<>("Store ID");

        employeeIDCol.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));
        hireDateCol.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        birthDateCol.setCellValueFactory(new PropertyValueFactory<>("empBirthDate"));
        storeIDCol.setCellValueFactory(new PropertyValueFactory<>("storeID"));

        tableView.getColumns().addAll(employeeIDCol, firstNameCol, lastNameCol, positionCol,
                salaryCol, hireDateCol, birthDateCol, storeIDCol);

        return tableView;
    }

    private void fetchAndDisplayEmployeesData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Employee");

            ObservableList<Employee> employeesData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getString("position"),
                        resultSet.getDouble("salary"),
                        resultSet.getDate("hireDate").toLocalDate(),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("employeeID"),
                        resultSet.getDate("empBirthDate").toLocalDate(),
                        resultSet.getString("storeID")
                );

                employeesData.add(employee);
            }

            employeesTable.setItems(employeesData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddEmployee() {
        // Create a dialog
        Alert addEmployeeDialog = new Alert(Alert.AlertType.CONFIRMATION);
        addEmployeeDialog.setTitle("Add Employee");
        addEmployeeDialog.setHeaderText(null);

        // Create a GridPane for labels and text fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and text fields
        TextField employeeIDField = new TextField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        //TextField positionField = new TextField();
        ComboBox<String> positionField = new ComboBox<>();
        positionField.getItems().addAll("Manager","Employee");
        TextField salaryField = new TextField();
       // TextField hireDateField = new TextField(); // Use a DatePicker for dates
        TextField birthDateField = new TextField(); // Use a DatePicker for dates
        TextField storeIDField = new TextField();
        DatePicker hireDatePicker = new DatePicker();
        hireDatePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        DatePicker BirthDatePicker = new DatePicker();
        BirthDatePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        grid.add(new Label("Employee ID:"), 0, 0);
        grid.add(employeeIDField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Position:"), 0, 3);
        grid.add(positionField, 1, 3);
        grid.add(new Label("Salary:"), 0, 4);
        grid.add(salaryField, 1, 4);
        grid.add(new Label("Hire Date:"), 0, 5);
        grid.add(hireDatePicker, 1, 5);
        grid.add(new Label("Birth Date:"), 0, 6);
        grid.add(BirthDatePicker, 1, 6);
        grid.add(new Label("Store ID:"), 0, 7);
        grid.add(storeIDField, 1, 7);

        addEmployeeDialog.getDialogPane().setContent(grid);

        // Set the button types
        addEmployeeDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for user input
        addEmployeeDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Retrieve values from text fields
                String employeeID = employeeIDField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String position = positionField.getValue();
                double salary = Double.parseDouble(salaryField.getText());
                LocalDate hireDate = (hireDatePicker.getValue()); // Parse date
                LocalDate birthDate = (BirthDatePicker.getValue()); // Parse date
                String storeID = storeIDField.getText();

                // Implement logic to add the employee with the provided information to the database
                addEmployeeToDatabase(employeeID, firstName, lastName, position, salary, hireDate, birthDate, storeID);

                // Refresh the employees table after adding
                fetchAndDisplayEmployeesData();
            }
        });
    }

    private void addEmployeeToDatabase(String employeeID, String firstName, String lastName, String position,
                                       double salary, LocalDate hireDate, LocalDate birthDate, String storeID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Check if the employee with the same employeeID already exists
            String checkQuery = String.format("SELECT * FROM Employee WHERE employeeID = '%s'", employeeID);
            ResultSet resultSet = statement.executeQuery(checkQuery);

            if (resultSet.next()) {
                // Employee with the same employeeID already exists, show an alert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Add Employee");
                alert.setHeaderText(null);
                alert.setContentText("Employee with the same ID already exists.");
                alert.showAndWait();
            } else {
                // Insert the new employee into the Employee table
                String insertQuery = String.format("INSERT INTO Employee VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                        position, salary, Date.valueOf(hireDate), firstName, lastName, employeeID, Date.valueOf(birthDate), storeID);

                statement.executeUpdate(insertQuery);

                System.out.println("Employee added to the database.");

                // Alternatively, you can show a success message here
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    private void handleDeleteEmployee() {
//        // Get the selected employee from the table
//        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
//
//        if (selectedEmployee == null) {
//            // If no employee is selected, show an alert and return
//            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
//            noSelectionAlert.setTitle("No Employee Selected");
//            noSelectionAlert.setHeaderText(null);
//            noSelectionAlert.setContentText("Please select an employee to delete.");
//            noSelectionAlert.showAndWait();
//            return;
//        }
//
//        // Ask for confirmation before deleting
//        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmationDialog.setTitle("Delete Employee");
//        confirmationDialog.setHeaderText(null);
//        confirmationDialog.setContentText("Are you sure you want to delete the selected employee?");
//
//        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
//
//        confirmationDialog.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.OK) {
//                // If the user confirms, proceed with deletion
//                deleteEmployeeFromDatabase(selectedEmployee);
//
//                // Refresh the employees table after deletion
//                fetchAndDisplayEmployeesData();
//            }
//        });
//    }
//
//    private void deleteEmployeeFromDatabase(Employee employee) {
//        try (Connection connection = DatabaseConnection.getConnection();
//             Statement statement = connection.createStatement()) {
//            // Delete the selected employee from the Employee table
//            String deleteQuery = String.format("DELETE FROM Employee WHERE employeeID = '%s'", employee.getEmployeeID());
//            statement.executeUpdate(deleteQuery);
//
//            System.out.println("Employee deleted from the database.");
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
private void handleDeleteEmployee() {
    // Get the selected employee from the table
    Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

    if (selectedEmployee == null) {
        // If no employee is selected, show an alert and return
        Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
        noSelectionAlert.setTitle("No Employee Selected");
        noSelectionAlert.setHeaderText(null);
        noSelectionAlert.setContentText("Please select an employee to delete.");
        noSelectionAlert.showAndWait();
        return;
    }

    // Check if the employee is the last manager
    if (selectedEmployee.getPosition().equals("Manager") && countManagers() == 1) {
        // Show an alert that the last manager cannot be deleted
        Alert lastManagerAlert = new Alert(Alert.AlertType.WARNING);
        lastManagerAlert.setTitle("Delete Manager");
        lastManagerAlert.setHeaderText(null);
        lastManagerAlert.setContentText("Cannot delete the last manager.");
        lastManagerAlert.showAndWait();
        return;
    }

    // Ask for confirmation before deleting
    Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
    confirmationDialog.setTitle("Delete Employee");
    confirmationDialog.setHeaderText(null);
    confirmationDialog.setContentText("Are you sure you want to delete the selected employee?");

    confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

    confirmationDialog.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            // If the user confirms, proceed with deletion
            deleteEmployeeFromDatabase(selectedEmployee);

            // Refresh the employees table after deletion
            fetchAndDisplayEmployeesData();
        }
    });
}

    private int countManagers() {
        int managerCount = 0;
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM Employee WHERE position = 'Manager'");

            if (resultSet.next()) {
                managerCount = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managerCount;
    }

    private void deleteEmployeeFromDatabase(Employee employee) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Check if the employee to be deleted is the last manager
            if (employee.getPosition().equals("Manager") && countManagers() == 1) {
                // Show an alert that the last manager cannot be deleted
                Alert lastManagerAlert = new Alert(Alert.AlertType.WARNING);
                lastManagerAlert.setTitle("Delete Manager");
                lastManagerAlert.setHeaderText(null);
                lastManagerAlert.setContentText("Cannot delete the last manager.");
                lastManagerAlert.showAndWait();
                return;
            }

            // Delete the selected employee from the Employee table
            String deleteQuery = String.format("DELETE FROM Employee WHERE employeeID = '%s'", employee.getEmployeeID());
            statement.executeUpdate(deleteQuery);

            System.out.println("Employee deleted from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void handleUpdateEmployee() {
        // Get the selected employee from the table
        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            // If no employee is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Employee Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select an employee to update.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Create an alert for updating employee information
        Alert updateEmployeeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        updateEmployeeAlert.setTitle("Update Employee");
        updateEmployeeAlert.setHeaderText(null);
        updateEmployeeAlert.setContentText("Update the employee information:");

        // Create labels and text fields for employee information
        Label employeeIDLabel = new Label("Employee ID:");
        TextField employeeIDTextField = new TextField(selectedEmployee.getEmployeeID());
        //employeeIDTextField.setEditable(false);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameTextField = new TextField(selectedEmployee.getFirstName());

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameTextField = new TextField(selectedEmployee.getLastName());

        Label positionLabel = new Label("Position:");
        TextField positionTextField = new TextField(selectedEmployee.getPosition());

        Label salaryLabel = new Label("Salary:");
        TextField salaryTextField = new TextField(String.valueOf(selectedEmployee.getSalary()));

        Label hireDateLabel = new Label("Hire Date:");
        TextField hireDateTextField = new TextField(String.valueOf(selectedEmployee.getHireDate())); // Convert date to string

        Label birthDateLabel = new Label("Birth Date:");
        TextField birthDateTextField = new TextField(String.valueOf(selectedEmployee.getEmpBirthDate())); // Convert date to string

        Label storeIDLabel = new Label("Store ID:");
        TextField storeIDTextField = new TextField(selectedEmployee.getStoreID());

        VBox updateEmployeeLayout = new VBox(10,
                employeeIDLabel, employeeIDTextField,
                firstNameLabel, firstNameTextField,
                lastNameLabel, lastNameTextField,
                positionLabel, positionTextField,
                salaryLabel, salaryTextField,
                hireDateLabel, hireDateTextField,
                birthDateLabel, birthDateTextField,
                storeIDLabel, storeIDTextField);

        updateEmployeeLayout.setAlignment(Pos.CENTER);

        updateEmployeeAlert.getDialogPane().setContent(updateEmployeeLayout);

        // Set the button types for the alert
        updateEmployeeAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and handle the result
        updateEmployeeAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user clicks OK, update the employee information
                updateEmployeeInDatabase(selectedEmployee,
                        firstNameTextField.getText(),
                        lastNameTextField.getText(),
                        positionTextField.getText(),
                        Double.parseDouble(salaryTextField.getText()),
                        LocalDate.parse(hireDateTextField.getText()), // Parse date
                        LocalDate.parse(birthDateTextField.getText()), // Parse date
                        storeIDTextField.getText());

                // Refresh the employees table after updating
                fetchAndDisplayEmployeesData();
            }
        });
    }

    private void updateEmployeeInDatabase(Employee employee, String newFirstName, String newLastName,
                                          String newPosition, double newSalary, LocalDate newHireDate,
                                          LocalDate newBirthDate, String newStoreID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Update the selected employee in the Employee table
            String updateQuery = String.format("UPDATE Employee SET firstName = '%s', lastName = '%s', " +
                            "position = '%s', salary = %f, hireDate = '%s', empBirthDate = '%s', storeID = '%s' " +
                            "WHERE employeeID = '%s'",
                    newFirstName, newLastName, newPosition, newSalary,
                    Date.valueOf(newHireDate), Date.valueOf(newBirthDate), newStoreID, employee.getEmployeeID());
            statement.executeUpdate(updateQuery);

            System.out.println("Employee updated in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleSearchEmployee1() {
        Employee foundEmployee = handleSearchEmployee();

        if (foundEmployee != null) {
            ObservableList<Employee> foundEmployeeList = FXCollections.observableArrayList();
            foundEmployeeList.add(foundEmployee);
            employeesTable.setItems(foundEmployeeList);
        }
    }

    private Employee handleSearchEmployee() {
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Search Employee");
        searchDialog.setHeaderText(null);
        searchDialog.setContentText("Enter Employee ID:");

        Optional<String> result = searchDialog.showAndWait();

        return result.map(this::searchEmployeeInDatabase).orElse(null);
    }

    private Employee searchEmployeeInDatabase(String employeeID) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM Employee WHERE employeeID = ?")) {

            preparedStatement.setString(1, employeeID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Employee(
                        resultSet.getString("position"),
                        resultSet.getDouble("salary"),
                        resultSet.getDate("hireDate").toLocalDate(),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("employeeID"),
                        resultSet.getDate("empBirthDate").toLocalDate(),
                        resultSet.getString("storeID")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return null if employee is not found
        return null;
    }
}
