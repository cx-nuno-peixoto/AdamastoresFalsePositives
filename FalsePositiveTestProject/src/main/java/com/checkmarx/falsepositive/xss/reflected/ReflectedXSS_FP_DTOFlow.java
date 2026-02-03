package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Data Flow Through DTOs/Value Objects
 * 
 * Pattern: User input flows through DTOs with type-safe getters that return numeric types
 * 
 * When user input is set to a DTO and later retrieved via type-safe getters,
 * the getter's return type guarantees the output format. CxQL doesn't track
 * that the DTO getter returns a different type than what was set.
 * 
 * All scenarios are FALSE POSITIVES - SAFE type-safe output.
 */
public class ReflectedXSS_FP_DTOFlow {
    
    /**
     * FALSE POSITIVE: DTO with Long field
     * User input parsed to Long, stored in DTO, retrieved as Long
     */
    public void showDtoLongField(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        
        // User input -> parsed to Long -> stored in DTO
        UserDTO dto = new UserDTO();
        dto.setId(Long.parseLong(idParam));
        
        PrintWriter out = response.getWriter();
        out.write("User ID: " + dto.getId()); // FALSE POSITIVE - getId() returns Long
    }
    
    /**
     * FALSE POSITIVE: DTO with Integer field
     * User input parsed to Integer, stored in DTO, retrieved as Integer
     */
    public void showDtoIntegerField(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ageParam = request.getParameter("age");
        
        UserDTO dto = new UserDTO();
        dto.setAge(Integer.parseInt(ageParam));
        
        PrintWriter out = response.getWriter();
        out.write("Age: " + dto.getAge()); // FALSE POSITIVE - getAge() returns Integer
    }
    
    /**
     * FALSE POSITIVE: DTO with Double field
     */
    public void showDtoDoubleField(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priceParam = request.getParameter("price");
        
        ProductDTO dto = new ProductDTO();
        dto.setPrice(Double.parseDouble(priceParam));
        
        PrintWriter out = response.getWriter();
        out.write("Price: $" + dto.getPrice()); // FALSE POSITIVE - getPrice() returns Double
    }
    
    /**
     * FALSE POSITIVE: Immutable Value Object
     * Value object created with validated data, all getters return safe types
     */
    public void showImmutableValueObject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String quantityParam = request.getParameter("quantity");
        
        // Immutable value object with type-safe fields
        OrderItem item = new OrderItem(
            Long.parseLong(idParam),
            Integer.parseInt(quantityParam)
        );
        
        PrintWriter out = response.getWriter();
        out.write("Order Item: " + item.getProductId() + " x " + item.getQuantity()); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: Builder pattern with type-safe setters
     */
    public void showBuilderPattern(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String amountParam = request.getParameter("amount");
        
        Transaction tx = Transaction.builder()
            .id(Long.parseLong(idParam))
            .amount(Double.parseDouble(amountParam))
            .build();
        
        PrintWriter out = response.getWriter();
        out.write("Transaction " + tx.getId() + ": $" + tx.getAmount()); // FALSE POSITIVE
    }
    
    /**
     * FALSE POSITIVE: Record class (Java 14+)
     * Records have immutable fields with type-safe accessors
     */
    public void showRecordClass(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        String scoreParam = request.getParameter("score");
        
        // In Java 14+: record ScoreRecord(Long id, Integer score) {}
        ScoreRecord record = new ScoreRecord(
            Long.parseLong(idParam),
            Integer.parseInt(scoreParam)
        );
        
        PrintWriter out = response.getWriter();
        out.write("Score for " + record.id() + ": " + record.score()); // FALSE POSITIVE
    }
    
    // ========== MOCK CLASSES FOR COMPILATION ==========
    
    private static class UserDTO {
        private Long id;
        private Integer age;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }
    
    private static class ProductDTO {
        private Double price;
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
    
    // Immutable value object
    private static class OrderItem {
        private final Long productId;
        private final Integer quantity;
        public OrderItem(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        public Long getProductId() { return productId; }
        public Integer getQuantity() { return quantity; }
    }
    
    // Builder pattern
    private static class Transaction {
        private Long id;
        private Double amount;
        public Long getId() { return id; }
        public Double getAmount() { return amount; }
        public static Builder builder() { return new Builder(); }
        
        static class Builder {
            private Transaction tx = new Transaction();
            public Builder id(Long id) { tx.id = id; return this; }
            public Builder amount(Double amount) { tx.amount = amount; return this; }
            public Transaction build() { return tx; }
        }
    }
    
    // Record-like class (for Java < 14)
    private static class ScoreRecord {
        private final Long id;
        private final Integer score;
        public ScoreRecord(Long id, Integer score) { this.id = id; this.score = score; }
        public Long id() { return id; }
        public Integer score() { return score; }
    }
}

