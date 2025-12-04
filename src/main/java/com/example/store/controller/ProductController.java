package com.example.store.controller;

import com.example.store.model.Product;
import com.example.store.model.User;
import com.example.store.repository.ProductRepository;
import com.example.store.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Public - anyone can view products
    @GetMapping
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // SELLER ONLY - Create product
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody Product product, Authentication auth) {
        String username = auth.getName();
        User seller = userRepository.findByUsername(username);

        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return ResponseEntity.status(403).body("Only sellers can create products");
        }

        product.setSeller(seller);
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    // SELLER ONLY - Update their own product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, Authentication auth) {
        String username = auth.getName();
        User seller = userRepository.findByUsername(username);

        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return ResponseEntity.status(403).body("Only sellers can update products");
        }

        Product existing = productRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if this seller owns the product
        if (!existing.getSeller().getId().equals(seller.getId())) {
            return ResponseEntity.status(403).body("You can only update your own products");
        }

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        Product updated = productRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    // SELLER ONLY - Delete their own product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication auth) {
        // DEBUG: Check if auth is null
        if (auth == null) {
            System.out.println("❌ Authentication is NULL!");
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        String username = auth.getName();
        System.out.println("🔍 DELETE REQUEST - Username from token: " + username);

        User seller = userRepository.findByUsername(username);

        if (seller == null) {
            System.out.println("❌ User not found: " + username);
            return ResponseEntity.status(403).body(Map.of("error", "User not found"));
        }

        System.out.println("👤 Logged in user ID: " + seller.getId() + ", Role: " + seller.getRole());

        if (!"SELLER".equals(seller.getRole())) {
            System.out.println("❌ User is not a SELLER");
            return ResponseEntity.status(403).body(Map.of("error", "Only sellers can delete products"));
        }

        Product existing = productRepository.findById(id).orElse(null);
        if (existing == null) {
            System.out.println("❌ Product not found: " + id);
            return ResponseEntity.notFound().build();
        }

        System.out.println("📦 Product ID: " + existing.getId() + ", Owner ID: " + existing.getSeller().getId());
        System.out.println("🔐 Ownership check: " + seller.getId() + " vs " + existing.getSeller().getId());
        System.out.println("✅ IDs match? " + existing.getSeller().getId().equals(seller.getId()));

        if (!existing.getSeller().getId().equals(seller.getId())) {
            System.out.println("❌ BLOCKED: User " + seller.getId() + " tried to delete product owned by " + existing.getSeller().getId());
            return ResponseEntity.status(403).body(Map.of("error", "You can only delete your own products"));
        }

        System.out.println("✅ ALLOWED: Deleting product " + id);
        productRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    // Get products for logged-in seller
    @GetMapping("/my-products")
    public ResponseEntity<?> getMyProducts(Authentication auth) {
        String username = auth.getName();
        User seller = userRepository.findByUsername(username);

        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return ResponseEntity.status(403).body("Only sellers can access this endpoint");
        }

        List<Product> products = productRepository.findBySellerId(seller.getId());
        return ResponseEntity.ok(products);
    }
}