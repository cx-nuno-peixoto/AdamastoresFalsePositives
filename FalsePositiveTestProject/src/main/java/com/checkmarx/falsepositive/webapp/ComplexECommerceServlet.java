package com.checkmarx.falsepositive.webapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * COMPLEX WEB APPLICATION SCENARIO - E-Commerce Order Processing
 * 
 * This servlet demonstrates multiple false positive patterns in a realistic
 * e-commerce application context, combining:
 * - Stored XSS (numeric getters, enum values, boolean fields)
 * - Reflected XSS (type conversion, number formatting, regex validation, enum validation)
 * - Loop Condition (type conversion, ternary expressions, bounded loops)
 * - Privacy Violation (metadata variables, constants as keys)
 */
@WebServlet("/order")
public class ComplexECommerceServlet extends HttpServlet {
    
    // Enums
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    }
    
    // Constants for Privacy Violation testing
    private static final String CREDIT_CARD = "CreditCard";
    private static final String ACCOUNT_NUMBER = "AccountNumber";
    
    // Mock entities
    public static class Product {
        private Long id;
        private String name;
        private Double price;
        private Integer stock;
        private boolean available;
        
        public Product(Long id, String name, Double price, Integer stock, boolean available) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.available = available;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
        public Integer getStock() { return stock; }
        public boolean isAvailable() { return available; }
    }
    
    public static class Order {
        private Long id;
        private OrderStatus status;
        private PaymentMethod paymentMethod;
        
        public Order(Long id, OrderStatus status, PaymentMethod paymentMethod) {
            this.id = id;
            this.status = status;
            this.paymentMethod = paymentMethod;
        }
        
        public Long getId() { return id; }
        public OrderStatus getStatus() { return status; }
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.write("<html><body>");
        out.write("<h1>E-Commerce Order Processing</h1>");
        
        // === REFLECTED XSS FALSE POSITIVES ===
        
        // FP: Type conversion
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null) {
            long productId = Long.parseLong(productIdParam); // SAFE: Numeric conversion
            out.write("<p>Product ID: " + productId + "</p>"); // SAFE: productId is long
        }
        
        // FP: Number formatting
        String priceParam = request.getParameter("price");
        if (priceParam != null) {
            double price = Double.parseDouble(priceParam); // SAFE: Numeric conversion
            DecimalFormat df = new DecimalFormat("$#,##0.00");
            String formattedPrice = df.format(price); // SAFE: DecimalFormat sanitizes
            out.write("<p>Price: " + formattedPrice + "</p>"); // SAFE: Formatted numeric
        }
        
        // FP: Regex validation
        String orderCodeParam = request.getParameter("orderCode");
        if (orderCodeParam != null) {
            Pattern pattern = Pattern.compile("^ORD-[0-9]{6}$");
            Matcher matcher = pattern.matcher(orderCodeParam);
            if (matcher.matches()) { // SAFE: Only ORD-XXXXXX format passes
                out.write("<p>Order Code: " + orderCodeParam + "</p>"); // SAFE: Validated
            }
        }
        
        // FP: Enum validation
        String paymentParam = request.getParameter("payment");
        if (paymentParam != null) {
            try {
                PaymentMethod payment = PaymentMethod.valueOf(paymentParam.toUpperCase()); // SAFE: Enum validation
                out.write("<p>Payment: " + payment.name() + "</p>"); // SAFE: Enum name
            } catch (IllegalArgumentException e) {
                out.write("<p>Invalid payment method</p>");
            }
        }
        
        // FP: Ternary with math operators
        String quantityParam = request.getParameter("quantity");
        if (quantityParam != null) {
            int quantity = Integer.parseInt(quantityParam); // SAFE: Numeric conversion
            int finalQty = (quantity > 10) ? 10 : quantity; // SAFE: Ternary bounds quantity
            out.write("<p>Quantity (max 10): " + finalQty + "</p>"); // SAFE: Bounded
        }
        
        // === STORED XSS FALSE POSITIVES ===
        
        List<Product> products = getProductsFromDatabase();
        
        if (!products.isEmpty()) {
            // FP: Numeric getter from collection
            Long firstProductId = products.get(0).getId(); // SAFE: getId() returns Long
            out.write("<p>First Product ID: " + firstProductId + "</p>"); // SAFE: Numeric
            
            // FP: Boolean field from database
            boolean available = products.get(0).isAvailable(); // SAFE: Boolean from database
            out.write("<p>Available: " + available + "</p>"); // SAFE: Boolean outputs true/false
            
            // FP: Enum from database
            List<Order> orders = getOrdersFromDatabase();
            if (!orders.isEmpty()) {
                OrderStatus status = orders.get(0).getStatus(); // SAFE: Enum from database
                out.write("<p>Order Status: " + status.name() + "</p>"); // SAFE: Enum name
            }
        }
        
        // === LOOP CONDITION FALSE POSITIVES ===
        
        String itemCountParam = request.getParameter("itemCount");
        if (itemCountParam != null) {
            int itemCount = Integer.parseInt(itemCountParam); // SAFE: Numeric conversion
            int bounded = Math.min(itemCount, 20); // SAFE: Bounded to max 20
            
            out.write("<ul>");
            for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated int
                out.write("<li>Item " + (i + 1) + "</li>");
            }
            out.write("</ul>");
        }
        
        // === PRIVACY VIOLATION FALSE POSITIVES ===
        
        // FP: Metadata variables
        String creditCardFormat = "XXXX-XXXX-XXXX-XXXX"; // SAFE: Format pattern, not actual card
        out.write("<p>Credit Card Format: " + creditCardFormat + "</p>"); // FLAGGED but SAFE
        
        String accountNumberPrefix = "ACC-"; // SAFE: Prefix pattern
        out.write("<p>Account Prefix: " + accountNumberPrefix + "</p>"); // FLAGGED but SAFE
        
        // FP: Constants as keys
        String fieldName = CREDIT_CARD; // CREDIT_CARD is "CreditCard"
        out.write("<p>Field Name: " + fieldName + "</p>"); // SAFE: Outputs "CreditCard"
        
        out.write("</body></html>");
        out.close();
    }
    
    private List<Product> getProductsFromDatabase() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1001L, "Laptop", 999.99, 50, true));
        products.add(new Product(1002L, "Mouse", 29.99, 200, true));
        products.add(new Product(1003L, "Keyboard", 79.99, 100, false));
        return products;
    }
    
    private List<Order> getOrdersFromDatabase() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(5001L, OrderStatus.PROCESSING, PaymentMethod.CREDIT_CARD));
        orders.add(new Order(5002L, OrderStatus.SHIPPED, PaymentMethod.PAYPAL));
        return orders;
    }
}

