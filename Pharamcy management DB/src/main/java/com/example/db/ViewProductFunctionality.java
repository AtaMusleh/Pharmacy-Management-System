package com.example.db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.Optional;

public class ViewProductFunctionality extends VBox {

    private TableView<Product> productsTable;

    public ViewProductFunctionality(Menu menu) {
        initializeUI(menu);
    }

    private void initializeUI(Menu menu) {

        Label productsLabel = new Label("Products Functionality");
        productsTable = createProductsTableView();

        HBox buttonsLayout = new HBox(20);
        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> handleAddProduct());
        Button updateButton = new Button("Update Product");
        updateButton.setOnAction(e -> handleUpdateProduct());
        Button deleteButton = new Button("Delete Product");
        deleteButton.setOnAction(e -> handleDeleteProduct());
        Button searchButton = new Button("Search Product");
        searchButton.setOnAction(e -> {
            Product foundProduct = handleSearchProduct();

            if (foundProduct != null) {
                ObservableList<Product> foundProductList = FXCollections.observableArrayList();
                foundProductList.add(foundProduct);
                productsTable.setItems(foundProductList);
            }
        });

        buttonsLayout.getChildren().addAll(addButton, updateButton, deleteButton, searchButton);
        buttonsLayout.setAlignment(Pos.CENTER);

        this.getChildren().addAll(productsLabel, productsTable, buttonsLayout);
        fetchAndDisplayProductsData();
        // Set action for the "View Products" button
    }

    private TableView<Product> createProductsTableView() {
        TableView<Product> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Adjust these columns based on your Product class properties
        TableColumn<Product, String> productIdCol = new TableColumn<>("Product ID");
        TableColumn<Product, String> productNameCol = new TableColumn<>("Product Name");
        TableColumn<Product, Integer> priceCol = new TableColumn<>("Price");
        TableColumn<Product, Integer> quantityCol = new TableColumn<>("Quantity");
        TableColumn<Product, String> categoryCol = new TableColumn<>("Category");

        productIdCol.setCellValueFactory(new PropertyValueFactory<>("productID"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        tableView.getColumns().addAll(productIdCol, productNameCol, priceCol, quantityCol, categoryCol);

        return tableView;
    }

    private void fetchAndDisplayProductsData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Product");

            ObservableList<Product> productsData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getString("Category"),
                        resultSet.getInt("Quantity"),
                        resultSet.getString("productID"),
                        resultSet.getString("productName"),
                        resultSet.getInt("price")
                );

                productsData.add(product);
            }

            productsTable.setItems(productsData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleAddProduct() {
        // Create a dialog
        Alert addProductDialog = new Alert(Alert.AlertType.CONFIRMATION);
        addProductDialog.setTitle("Add Product");
        addProductDialog.setHeaderText(null);

        // Create a GridPane for labels and text fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Add labels and text fields
        TextField productIdField = new TextField();
        TextField productNameField = new TextField();
        TextField priceField = new TextField();
        TextField quantityField = new TextField();
        TextField categoryField = new TextField(); // New field for category

        grid.add(new Label("Product ID:"), 0, 0);
        grid.add(productIdField, 1, 0);
        grid.add(new Label("Product Name:"), 0, 1);
        grid.add(productNameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);

        addProductDialog.getDialogPane().setContent(grid);

        // Set the button types
        addProductDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and wait for user input
        addProductDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Retrieve values from text fields
                String productId = productIdField.getText();
                String productName = productNameField.getText();
                int price = Integer.parseInt(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String category = categoryField.getText(); // Get category value

                // Implement logic to add the product with the provided information to the database
                addProductToDatabase(productId, productName, price, quantity, category);

                // Refresh the products table after adding
                fetchAndDisplayProductsData();
            }
        });
    }

//    private void addProductToDatabase(String productId, String productName, int price, int quantity, String category) {
//        try (Connection connection = DatabaseConnection.getConnection();
//             Statement statement = connection.createStatement()) {
//
//            // Check if the product with the same productID already exists
//            String checkQuery = String.format("SELECT * FROM Product WHERE productID = '%s'", productId);
//            ResultSet resultSet = statement.executeQuery(checkQuery);
//
//            if (resultSet.next()) {
//                // Product with the same productID already exists, show an alert
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("Add Product");
//                alert.setHeaderText(null);
//                alert.setContentText("Product with the same ID already exists.");
//                alert.showAndWait();
//            } else {
//                // Insert the new product into the Product table
////                String insertQuery = String.format("INSERT INTO Product VALUES ('%s', '%s', %d, %d, '%s')",
////                        productId, productName, price, quantity, category);
//                String insertQuery = String.format("INSERT INTO Product VALUES ('%s', '%d', %s, %s, '%d')",
//                        category,quantity,productId,productName,price);
//                statement.executeUpdate(insertQuery);
//
//                System.out.println("Product added to the database.");
//
//                // Alternatively, you can show a success message here
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
private void addProductToDatabase(String productId, String productName, int price, int quantity, String category) {
    try (Connection connection = DatabaseConnection.getConnection();
         Statement statement = connection.createStatement()) {

        // Check if the product with the same productID already exists
        String checkQuery = String.format("SELECT * FROM Product WHERE productID = '%s'", productId);
        ResultSet resultSet = statement.executeQuery(checkQuery);

        if (resultSet.next()) {
            // Product with the same productID already exists, show an alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Add Product");
            alert.setHeaderText(null);
            alert.setContentText("Product with the same ID already exists.");
            alert.showAndWait();
        } else {
            // Insert the new product into the Product table
            String insertQuery = "INSERT INTO Product (Category, Quantity, productID, productName, price) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, category);
                preparedStatement.setInt(2, quantity);
                preparedStatement.setString(3, productId);
                preparedStatement.setString(4, productName);
                preparedStatement.setInt(5, price);

                preparedStatement.executeUpdate();
            }

            System.out.println("Product added to the database.");
            // Alternatively, you can show a success message here
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private void handleDeleteProduct() {
        // Get the selected product from the table
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            // If no product is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Product Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a product to delete.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Ask for confirmation before deleting
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete Product");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Are you sure you want to delete the selected product?");

        confirmationDialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user confirms, proceed with deletion
                deleteProductFromDatabase(selectedProduct);

                // Refresh the products table after deletion
                fetchAndDisplayProductsData();
            }
        });
    }

    private void deleteProductFromDatabase(Product product) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Delete the selected product from the Product table
            String deleteQuery = String.format("DELETE FROM Product WHERE productID = '%s'", product.getProductID());
            statement.executeUpdate(deleteQuery);

            System.out.println("Product deleted from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateProduct() {
        // Get the selected product from the table
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            // If no product is selected, show an alert and return
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Product Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select a product to update.");
            noSelectionAlert.showAndWait();
            return;
        }

        // Create an alert for updating product information
        Alert updateProductAlert = new Alert(Alert.AlertType.CONFIRMATION);
        updateProductAlert.setTitle("Update Product");
        updateProductAlert.setHeaderText(null);
        updateProductAlert.setContentText("Update the product information:");

        // Create labels and text fields for product information
        Label productIdLabel = new Label("Product ID:");
        TextField productIdTextField = new TextField(selectedProduct.getProductID());
        productIdTextField.setEditable(false);

        Label productNameLabel = new Label("Product Name:");
        TextField productNameTextField = new TextField(selectedProduct.getProductName());

        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField(String.valueOf(selectedProduct.getPrice()));

        Label quantityLabel = new Label("Quantity:");
        TextField quantityTextField = new TextField(String.valueOf(selectedProduct.getQuantity()));

        Label categoryLabel = new Label("Category:");
        TextField categoryTextField = new TextField(selectedProduct.getCategory());

        VBox updateProductLayout = new VBox(10,
                productIdLabel, productIdTextField,
                productNameLabel, productNameTextField,
                priceLabel, priceTextField,
                quantityLabel, quantityTextField,
                categoryLabel, categoryTextField);

        updateProductLayout.setAlignment(Pos.CENTER);

        updateProductAlert.getDialogPane().setContent(updateProductLayout);

        // Set the button types for the alert
        updateProductAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the alert and handle the result
        updateProductAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user clicks OK, update the product information
                updateProductInDatabase(selectedProduct,
                        productNameTextField.getText(),
                        Integer.parseInt(priceTextField.getText()),
                        Integer.parseInt(quantityTextField.getText()),
                        categoryTextField.getText());

                // Refresh the products table after updating
                fetchAndDisplayProductsData();
            }
        });
    }

    private void updateProductInDatabase(Product product, String newProductName, int newPrice, int newQuantity, String newCategory) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Update the selected product in the Product table
            String updateQuery = String.format("UPDATE Product SET productName = '%s', price = %d, quantity = %d, category = '%s' " +
                            "WHERE productID = '%s'",
                    newProductName, newPrice, newQuantity, newCategory, product.getProductID());
            statement.executeUpdate(updateQuery);

            System.out.println("Product updated in the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Product handleSearchProduct() {
        // Create a dialog for searching a product
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Search Product");
        searchDialog.setHeaderText(null);
        searchDialog.setContentText("Enter Product ID:");

        Optional<String> result = searchDialog.showAndWait();
        if (result.isPresent()) {
            String productId = result.get();
            return searchProductInDatabase(productId);
        }
        return null;
    }

    private Product searchProductInDatabase(String productId) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            // Search for the product in the Product table
            String searchQuery = String.format("SELECT * FROM Product WHERE productID = '%s'", productId);
            ResultSet resultSet = statement.executeQuery(searchQuery);

            if (resultSet.next()) {
                // If the product is found, return a Product object
                return new Product(
                        resultSet.getString("Category"),
                        resultSet.getInt("Quantity"),
                        resultSet.getString("productID"),
                        resultSet.getString("productName"),
                        resultSet.getInt("price")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
