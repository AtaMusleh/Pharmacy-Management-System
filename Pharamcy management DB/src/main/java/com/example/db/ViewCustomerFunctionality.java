package com.example.db;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class ViewCustomerFunctionality extends VBox {

    private TableView<Customer> customersTable;

    public ViewCustomerFunctionality(Menu menu) {
        initializeUI(menu);
    }

    private void initializeUI(Menu menu) {

        Label customersLabel = new Label("Customers Functionality");
        customersTable = createCustomersTableView();

        HBox buttonsLayout = new HBox(20);
        Button addButton = new Button("Add Customer");
        addButton.setOnAction(e -> handleAddCustomer());
        Button updateButton = new Button("Update Customer");
        updateButton.setOnAction(e -> handleUpdateCustomer());
        Button deleteButton = new Button("Delete Customer");
        deleteButton.setOnAction(e -> handleDeleteCustomer());
        Button searchButton = new Button("Search Customer");
        searchButton.setOnAction(e -> {
            Customer foundCustomer = handleSearchCustomer();

            if (foundCustomer != null) {
                ObservableList<Customer> foundCustomerList = FXCollections.observableArrayList();
                foundCustomerList.add(foundCustomer);
                customersTable.setItems(foundCustomerList);
            }
        });

        buttonsLayout.getChildren().addAll(addButton, updateButton, deleteButton, searchButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        this.getChildren().addAll(customersLabel, customersTable, buttonsLayout);
        fetchAndDisplayCustomersData();
        // Set action for the "View Customers" button
    }

    private TableView<Customer> createCustomersTableView() {
        TableView<Customer> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, String> phoneNumberCol = new TableColumn<>("Phone Number");
        TableColumn<Customer, String> firstNameCol = new TableColumn<>("First Name");
        TableColumn<Customer, String> lastNameCol = new TableColumn<>("Last Name");
        TableColumn<Customer, String> customerIDCol = new TableColumn<>("Customer ID");

        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        tableView.getColumns().addAll(phoneNumberCol, firstNameCol, lastNameCol, customerIDCol);

        return tableView;
    }

    private void fetchAndDisplayCustomersData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Customer");

            ObservableList<Customer> customersData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("customerID")
                );

                customersData.add(customer);
            }

            customersTable.setItems(customersData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddCustomer() {
        // Create a dialog
        Alert addCustomerDialog = new Alert(Alert.AlertType.CONFIRMATION);
        addCustomerDialog.setTitle("Add Customer");
        addCustomerDialog.setHeaderText(null);

        // Create a GridPane for labels and text fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and text fields
        TextField phoneNumberField = new TextField();
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField customerIDField = new TextField();

        grid.add(new Label("Phone Number:"), 0, 0);
        grid.add(phoneNumberField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Customer ID:"), 0, 3);
        grid.add(customerIDField, 1, 3);

        addCustomerDialog.getDialogPane().setContent(grid);

        // Set the button types
        addCustomerDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for user input
        addCustomerDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Retrieve values from text fields
                String phoneNumber = phoneNumberField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String customerID = customerIDField.getText();

                // Implement logic to add the customer with the provided information to the database
                addCustomerToDatabase(phoneNumber, firstName, lastName, customerID);

                // Refresh the customers table after adding
                fetchAndDisplayCustomersData();
            }
        });
    }

    private void addCustomerToDatabase(String phoneNumber, String firstName, String lastName, String customerID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Check if the customer with the same customerID already exists
            String checkQuery = String.format("SELECT * FROM Customer WHERE customerID = '%s'", customerID);
            ResultSet resultSet = statement.executeQuery(checkQuery);

            if (resultSet.next()) {
                // Customer with the same customerID already exists, show an alert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Add Customer");
                alert.setHeaderText(null);
                alert.setContentText("Customer with the same ID already exists.");
                alert.showAndWait();
            } else {
                // Insert the new customer into the Customer table
                String insertQuery = "INSERT INTO Customer VALUES (?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, phoneNumber);
                    preparedStatement.setString(2, firstName);
                    preparedStatement.setString(3, lastName);
                    preparedStatement.setString(4, customerID);

                    preparedStatement.executeUpdate();
                }

                System.out.println("Customer added to the database.");

                // Alternatively, you can show a success message here
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteCustomer() {
        // Get the selected customer from the table
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            // If no customer is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Customer Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a customer to delete.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete Customer");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to delete the selected customer?");

        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user confirms, proceed with deletion
                deleteCustomerFromDatabase(selectedCustomer);

                // Refresh the customers table after deletion
                fetchAndDisplayCustomersData();
            }
        });
    }

    private void deleteCustomerFromDatabase(Customer customer) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Delete the selected customer from the Customer table
            String deleteQuery = String.format("DELETE FROM Customer WHERE customerID = '%s'", customer.getCustomerID());
            statement.executeUpdate(deleteQuery);

            System.out.println("Customer deleted from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateCustomer() {
        // Get the selected customer from the table
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            // If no customer is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Customer Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a customer to update.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Create an alert for updating customer information
        Alert updateCustomerAlert = new Alert(Alert.AlertType.CONFIRMATION);
        updateCustomerAlert.setTitle("Update Customer");
        updateCustomerAlert.setHeaderText(null);
        updateCustomerAlert.setContentText("Update the customer information:");

        // Create labels and text fields for customer information
        Label phoneNumberLabel = new Label("Phone Number:");
        TextField phoneNumberTextField = new TextField(selectedCustomer.getPhoneNumber());

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameTextField = new TextField(selectedCustomer.getFirstName());

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameTextField = new TextField(selectedCustomer.getLastName());

        Label customerIDLabel = new Label("Customer ID:");
        TextField customerIDTextField = new TextField(selectedCustomer.getCustomerID());
        //customerIDTextField.setEditable(false);

        VBox updateCustomerLayout = new VBox(10,
                phoneNumberLabel, phoneNumberTextField,
                firstNameLabel, firstNameTextField,
                lastNameLabel, lastNameTextField,
                customerIDLabel, customerIDTextField);

        updateCustomerLayout.setAlignment(Pos.CENTER);

        updateCustomerAlert.getDialogPane().setContent(updateCustomerLayout);

        // Set the button types for the alert
        updateCustomerAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and handle the result
        updateCustomerAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user clicks OK, update the customer information
                updateCustomerInDatabase(selectedCustomer,
                        phoneNumberTextField.getText(),
                        firstNameTextField.getText(),
                        lastNameTextField.getText(),
                        customerIDTextField.getText());

                // Refresh the customers table after updating
                fetchAndDisplayCustomersData();
            }
        });
    }

    private void updateCustomerInDatabase(Customer customer, String newPhoneNumber, String newFirstName,
                                          String newLastName, String newCustomerID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Update the selected customer in the Customer table
            String updateQuery = "UPDATE Customer SET phoneNumber = ?, firstName = ?, " +
                    "lastName = ?, customerID = ? WHERE customerID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newPhoneNumber);
                preparedStatement.setString(2, newFirstName);
                preparedStatement.setString(3, newLastName);
                preparedStatement.setString(4, newCustomerID);
                preparedStatement.setString(5, customer.getCustomerID());

                preparedStatement.executeUpdate();
            }

            System.out.println("Customer updated in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Customer handleSearchCustomer() {
        // Create a dialog
        TextInputDialog searchCustomerDialog = new TextInputDialog();
        searchCustomerDialog.setTitle("Search Customer");
        searchCustomerDialog.setHeaderText(null);
        searchCustomerDialog.setContentText("Enter Customer ID:");

        // Show the dialog and wait for user input
        Optional<String> result = searchCustomerDialog.showAndWait();
        if (result.isPresent()) {
            String customerID = result.get();

            // Search for the customer in the database
            return searchCustomerInDatabase(customerID);
        }

        return null;
    }

    private Customer searchCustomerInDatabase(String customerID) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Search for the customer in the Customer table
            String searchQuery = String.format("SELECT * FROM Customer WHERE customerID = '%s'", customerID);
            ResultSet resultSet = statement.executeQuery(searchQuery);

            if (resultSet.next()) {
                // If the customer is found, create a Customer object and return it
                return new Customer(
                        resultSet.getString("phoneNumber"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("customerID")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no customer is found, return null
        return null;
    }
}

