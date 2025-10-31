package com.example.expiry_mang.controller;

import com.example.expiry_mang.model.Product;
import com.example.expiry_mang.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/expired")
public Set<Product> getExpiredProducts() {
    return productService.checkForExpiredProducts("PRODUCT");
}

@GetMapping("/near-expiry")
public Set<Product> getNearExpiryProducts() {
    return productService.checkForNearExpiryProducts("PRODUCT");
}

   
    @DeleteMapping("/{batchNumber}")
    public ResponseEntity<String> deleteProduct(@PathVariable String batchNumber) {
        boolean isDeleted = productService.deleteProductByBatchNumber(batchNumber);
        if (isDeleted) {
            return ResponseEntity.ok("Product with batch number " + batchNumber + " deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with batch number " + batchNumber + " not found.");
        }
    }
     
    @Scheduled(cron = "0 * * * * ?") // Every minute
    public void checkExpiryAndNotify() {
        Set<Product> expiredProducts = productService.checkForExpiredProducts("PRODUCT");
        notifyUserOfExpiredProducts(expiredProducts);

        Set<Product> nearExpiryProducts = productService.checkForNearExpiryProducts("PRODUCT");
        notifyUserOfNearExpiryProducts(nearExpiryProducts);
    }
    

    private void notifyUserOfExpiredProducts(Set<Product> expiredProducts) {
        if (!expiredProducts.isEmpty()) {
            System.out.println("Sending expired products via WebSocket: " + expiredProducts);
            messagingTemplate.convertAndSend("/topic/expired", expiredProducts);
        }
    }
    
    private void notifyUserOfNearExpiryProducts(Set<Product> nearExpiryProducts) {
        if (!nearExpiryProducts.isEmpty()) {
            System.out.println("Sending near-expiry products via WebSocket: " + nearExpiryProducts);
            messagingTemplate.convertAndSend("/topic/near-expiry", nearExpiryProducts);
        }
    }
    
}
