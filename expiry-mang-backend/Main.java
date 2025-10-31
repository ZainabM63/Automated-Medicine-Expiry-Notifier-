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

public class Main {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:ORCL"; // Adjust as necessary
    private static final String USER = "system"; // Replace with your DB username
    private static final String PASSWORD = "Uit54321"; // Replace with your DB password

    public static void main(String[] args) {
        List<Product> productList = loadProductsFromDatabase();

        // Check for expired products
        Set<Product> expiredProducts = new HashSet<>();
        for (Product product : productList) {
            if (product.isExpired()) {
                expiredProducts.add(product);
            }
        }

        // Output expired products
        if (expiredProducts.isEmpty()) {
            System.out.println("No expired products found.");
        } else {
            System.out.println("Expired products:");
            for (Product product : expiredProducts) {
                System.out.println(product.getName() + " (Batch: " + product.getBatchNumber() + ", Expiry: " + product.getExpiryDate() + ")");
            }
        }
    }

    public static List<Product> loadProductsFromDatabase() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT name, batch_number, expiry_date FROM products";

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (!resultSet.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No products found in the database.");
                return productList; // Early return if no data
            }

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String batchNumber = resultSet.getString("batch_number");
                LocalDate expiryDate = resultSet.getDate("expiry_date").toLocalDate();
                productList.add(new Product(name, batchNumber, expiryDate)); // Adding products from the database
            }

            // Print the loaded products
            System.out.println("Loaded products from database:");
            for (Product product : productList) {
                System.out.println(product.getName() + " (Batch: " + product.getBatchNumber() + ", Expiry: " + product.getExpiryDate() + ")");
            }

        } catch (SQLException e) {
            System.out.println("Error loading products from database: " + e.getMessage());
            e.printStackTrace();
        }

        return productList; // Return the list fetched from the database
    }
}

