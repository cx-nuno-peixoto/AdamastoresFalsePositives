package com.checkmarx.falsepositive.xss.stored;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * FALSE POSITIVE SCENARIOS: Stored XSS - Type Inference Failures
 * 
 * Pattern: CxQL fails to infer that generic collection getters return numeric types
 * 
 * When we call products.get(0).getId(), CxQL sees:
 * 1. get(0) returns Product type (from List<Product>)
 * 2. getId() is called on Product
 * 3. But CxQL cannot infer getId() returns Long
 * 
 * All scenarios are FALSE POSITIVES - SAFE numeric output.
 */
public class StoredXSS_FP_TypeInference {
    
    private ProductRepository productRepository;
    
    /**
     * FALSE POSITIVE: List.get(index).getId() - Long getter
     * CxQL cannot infer that getId() returns Long through generic type
     */
    public void showListGetNumericId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();
        
        if (!products.isEmpty()) {
            // get(0) returns Product, getId() returns Long
            // CxQL cannot follow this type chain through generics
            Long id = products.get(0).getId();
            
            PrintWriter out = response.getWriter();
            out.write("Product ID: " + id); // FALSE POSITIVE - Long is numeric
        }
    }
    
    /**
     * FALSE POSITIVE: List.get(index).getPrice() - Double getter
     */
    public void showListGetNumericPrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();
        
        if (!products.isEmpty()) {
            Double price = products.get(0).getPrice();
            
            PrintWriter out = response.getWriter();
            out.write("Price: $" + price); // FALSE POSITIVE - Double is numeric
        }
    }
    
    /**
     * FALSE POSITIVE: List.get(index).getQuantity() - Integer getter
     */
    public void showListGetNumericQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();
        
        if (!products.isEmpty()) {
            Integer quantity = products.get(0).getQuantity();
            
            PrintWriter out = response.getWriter();
            out.write("Stock: " + quantity); // FALSE POSITIVE - Integer is numeric
        }
    }
    
    /**
     * FALSE POSITIVE: Optional.get().getId() - through Optional wrapper
     */
    public void showOptionalGetNumericId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<Product> optProduct = productRepository.findById(1L);
        
        if (optProduct.isPresent()) {
            Long id = optProduct.get().getId();
            
            PrintWriter out = response.getWriter();
            out.write("Product ID: " + id); // FALSE POSITIVE - Long is numeric
        }
    }
    
    /**
     * FALSE POSITIVE: Stream first().getId() - through Stream API
     */
    public void showStreamFirstNumericId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();
        
        products.stream()
            .findFirst()
            .ifPresent(product -> {
                Long id = product.getId();
                try {
                    PrintWriter out = response.getWriter();
                    out.write("First ID: " + id); // FALSE POSITIVE - Long is numeric
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
    
    /**
     * FALSE POSITIVE: Iterator.next().getId() - through Iterator
     */
    public void showIteratorNextNumericId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();
        
        if (!products.isEmpty()) {
            Product product = products.iterator().next();
            Long id = product.getId();
            
            PrintWriter out = response.getWriter();
            out.write("Product ID: " + id); // FALSE POSITIVE - Long is numeric
        }
    }
    
    /**
     * FALSE POSITIVE: Array element getter - products[0].getId()
     */
    public void showArrayElementNumericId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product[] products = productRepository.findAllAsArray();
        
        if (products.length > 0) {
            Long id = products[0].getId();
            
            PrintWriter out = response.getWriter();
            out.write("Product ID: " + id); // FALSE POSITIVE - Long is numeric
        }
    }
    
    // ========== MOCK CLASSES FOR COMPILATION ==========
    private interface ProductRepository {
        List<Product> findAll();
        Optional<Product> findById(Long id);
        Product[] findAllAsArray();
    }
    
    private static class Product {
        private Long id;
        private Double price;
        private Integer quantity;
        
        public Long getId() { return id; }
        public Double getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
    }
}

