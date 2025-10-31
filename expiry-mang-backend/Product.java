import java.time.LocalDate;
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
