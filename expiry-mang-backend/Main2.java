import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main2 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:ORCL"; // Adjust as necessary
    private static final String USER = "system"; // Replace with your DB username
    private static final String PASSWORD = "Uit54321"; // Replace with your DB password

    public static void main(String[] args) {
        // Test database connection first to ensure it's working
        testDatabaseConnection();

        // Schedule the check for expired products twice daily
        scheduleDailyExpiryCheck("PRODUCT");
    }

    public static void scheduleDailyExpiryCheck(String tableName) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);  // Increase thread pool size

        // Calculate initial delays for morning and night (8 AM and 8 PM)
        long initialDelayMorning = calculateInitialDelay(LocalTime.of(8, 0));
        long initialDelayNight = calculateInitialDelay(LocalTime.of(20, 0));

        // Debug log: Checking the delays
        System.out.println("Initial delay for morning: " + initialDelayMorning + " minutes");
        System.out.println("Initial delay for night: " + initialDelayNight + " minutes");

        // Schedule tasks to run at 8 AM and 8 PM every day
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Checking for expired products at 8 AM...");
            checkForExpiredProducts(tableName);
        }, initialDelayMorning, 24, TimeUnit.HOURS);

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Checking for expired products at 8 PM...");
            checkForExpiredProducts(tableName);
        }, initialDelayNight, 24, TimeUnit.HOURS);
    }

    // Method to calculate initial delay based on current time
    private static long calculateInitialDelay(LocalTime targetTime) {
        LocalTime now = LocalTime.now(ZoneId.systemDefault());
        long delay = TimeUnit.MINUTES.convert(
            targetTime.toSecondOfDay() - now.toSecondOfDay(),
            TimeUnit.SECONDS
        );
        if (delay < 0) { // If target time has already passed today, schedule for the next day
            delay += TimeUnit.MINUTES.convert(24, TimeUnit.HOURS);
        }
        return delay;
    }

    // Method to check for expired products
    public static void checkForExpiredProducts(String tableName) {
        System.out.println("Loading products from the database for expiry check...");
        List<Product> productList = loadProductsFromDatabase(tableName);

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

    // Load products from the database with timeout
    public static List<Product> loadProductsFromDatabase(String tableName) {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT name, batch_number, expiry_date FROM " + tableName; // Use the specified table name

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // Debug log: Checking if the connection and query execution is successful
            System.out.println("Database connection successful. Executing query: " + query);

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setQueryTimeout(30);  // Set query timeout to 30 seconds
            ResultSet resultSet = statement.executeQuery();

            // Iterate through the ResultSet
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String batchNumber = resultSet.getString("batch_number");
                LocalDate expiryDate = resultSet.getDate("expiry_date").toLocalDate();
                
                // Create a new Product object and add it to the list
                productList.add(new Product(name, batchNumber, expiryDate));
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

    // Test database connection
    public static void testDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class Product {
    private String name;
    private String batchNumber;
    private LocalDate expiryDate;

    // Constructor
    public Product(String name, String batchNumber, LocalDate expiryDate) {
        this.name = name;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    // Check if the product is expired
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}