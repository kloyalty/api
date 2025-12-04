package com.example.store.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;  // ADD THIS
    private Double price;

    @ManyToOne
    @JoinColumn(name = "seller_id")  // ADD THIS - creates proper foreign key column
    private User seller;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }  // ADD THIS
    public void setDescription(String description) { this.description = description; }  // ADD THIS

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
}