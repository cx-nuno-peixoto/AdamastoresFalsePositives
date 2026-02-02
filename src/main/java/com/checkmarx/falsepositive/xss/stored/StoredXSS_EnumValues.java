package com.checkmarx.falsepositive.xss.stored;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * FALSE POSITIVE SCENARIOS: Stored XSS - Enum Values from Database
 * 
 * Pattern: Enum values retrieved from database
 * 
 * These scenarios demonstrate that enum values from database are safe.
 * Enums can only contain predefined constant values, not XSS payloads.
 */
public class StoredXSS_EnumValues {
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
    
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    // Mock entity with enum
    public static class User {
        private Long id;
        private String name;
        private UserStatus status;
        
        public User(Long id, String name, UserStatus status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public UserStatus getStatus() { return status; }
    }
    
    public static class Order {
        private Long id;
        private OrderStatus status;
        
        public Order(Long id, OrderStatus status) {
            this.id = id;
            this.status = status;
        }
        
        public Long getId() { return id; }
        public OrderStatus getStatus() { return status; }
    }
    
    // Scenario 1: Enum from database entity
    public void showUserStatus(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        UserStatus status = users.get(0).getStatus(); // SAFE: Enum from database
        out.write("Status: " + status); // SAFE: Enum cannot contain XSS
    }
    
    // Scenario 2: Enum.name() from database
    public void showStatusName(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        String statusName = users.get(0).getStatus().name(); // SAFE: Enum.name() returns safe string
        out.write("Status: " + statusName);
    }
    
    // Scenario 3: Enum.ordinal() from database
    public void showStatusOrdinal(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        int ordinal = users.get(0).getStatus().ordinal(); // SAFE: ordinal() returns int
        out.write("Status ordinal: " + ordinal);
    }
    
    // Scenario 4: Enum in switch from database
    public void showOrderStatusMessage(HttpServletRequest request, HttpServletResponse response, List<Order> orders) throws IOException {
        PrintWriter out = response.getWriter();
        OrderStatus status = orders.get(0).getStatus(); // SAFE: Enum from database
        
        String message;
        switch (status) { // SAFE: Switch on enum
            case PENDING:
                message = "Order is pending";
                break;
            case CONFIRMED:
                message = "Order confirmed";
                break;
            case SHIPPED:
                message = "Order shipped";
                break;
            case DELIVERED:
                message = "Order delivered";
                break;
            case CANCELLED:
                message = "Order cancelled";
                break;
            default:
                message = "Unknown status";
        }
        
        out.write(message); // SAFE: message is from switch on enum
    }
    
    // Scenario 5: Multiple enum values from collection
    public void showAllStatuses(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("<ul>");
        for (User user : users) {
            UserStatus status = user.getStatus(); // SAFE: Enum from database
            out.write("<li>" + status.name() + "</li>"); // SAFE: Enum.name()
        }
        out.write("</ul>");
    }
}

