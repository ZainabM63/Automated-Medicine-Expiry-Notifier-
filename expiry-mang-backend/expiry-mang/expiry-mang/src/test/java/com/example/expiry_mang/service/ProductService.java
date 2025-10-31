package com.example.expiry_mang.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.expiry_mang.model.Product;

@Service
public class ProductService {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl"; // Adjust as necessary
    private static final String USER = "sys as sysdba";
    private static final String PASSWORD = "gjhgkh"; // Replace with your DB password

    // Check for expired products
    public Set<Product> checkForExpiredProducts(String tableName) {
        System.out.println("Loading products from the database for expiry check...");
        List<Product> productList = loadProductsFromDatabase(tableName);

        Set<Product> expiredProducts = new HashSet<>();
        for (Product product : productList) {
            if (product.isExpired()) {
                expiredProducts.add(product);
            }
        }

        return expiredProducts;
    }

    // Check for products near expiry (within 2 months)
    public Set<Product> checkForNearExpiryProducts(String tableName) {
        System.out.println("Loading products from the database for near-expiry check...");
        List<Product> productList = loadProductsFromDatabase(tableName);

        Set<Product> nearExpiryProducts = new HashSet<>();
        LocalDate twoMonthsFromNow = LocalDate.now().plusMonths(2);

        for (Product product : productList) {
            if (product.getExpiryDate().isAfter(LocalDate.now()) && product.getExpiryDate().isBefore(twoMonthsFromNow)) {
                nearExpiryProducts.add(product);
            }
        }

        return nearExpiryProducts;
    }

    // Load products from the database with timeout
    public static List<Product> loadProductsFromDatabase(String tableName) {
        List<Product> productList = new ArrayList<>();
       String query = "SELECT name, batch_number, expiry_date FROM " + tableName;


        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Database connection successful. Executing query: " + query);

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String batchNumber = resultSet.getString("batch_number");
                LocalDate expiryDate = resultSet.getDate("expiry_date").toLocalDate();

                productList.add(new Product(name, batchNumber, expiryDate));
            }

        } catch (SQLException e) {
            System.out.println("Error loading products from database: " + e.getMessage());
            e.printStackTrace();
        }

        return productList;
    }

    // Delete product by batch number
    public boolean deleteProductByBatchNumber(String batchNumber) {
        String query = "DELETE FROM PRODUCT WHERE batch_number = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, batchNumber);
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0; // Returns true if a product was deleted
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }}
