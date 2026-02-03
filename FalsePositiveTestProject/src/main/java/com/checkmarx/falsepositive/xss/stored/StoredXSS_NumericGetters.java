package com.checkmarx.falsepositive.xss.stored;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Stored XSS - Entity Numeric Getters
 * 
 * Pattern: Database entity numeric getters (getId(), getAccountId(), etc.)
 * Issue: CxQL loses type information on collection.get(0).getId() patterns
 * 
 * These scenarios demonstrate that numeric getters return primitive types (int, long)
 * which cannot contain XSS payloads.
 */
public class StoredXSS_NumericGetters {
    
    // Mock entity class
    public static class User {
        private Long id;
        private Integer accountId;
        private Double balance;
        
        public Long getId() { return id; }
        public Integer getAccountId() { return accountId; }
        public Double getBalance() { return balance; }
    }
    
    // Scenario 1: getId() from collection.get(0)
    public void showUserId(HttpServletRequest request, HttpServletResponse response, java.util.List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Long userId = users.get(0).getId(); // SAFE: getId() returns Long (numeric)
        out.write("User ID: " + userId); // SAFE: Cannot contain XSS
    }
    
    // Scenario 2: getAccountId() from collection
    public void showAccountId(HttpServletRequest request, HttpServletResponse response, java.util.List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Integer accountId = users.get(0).getAccountId(); // SAFE: Returns Integer
        out.write("Account: " + accountId);
    }
    
    // Scenario 3: getBalance() from collection
    public void showBalance(HttpServletRequest request, HttpServletResponse response, java.util.List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Double balance = users.get(0).getBalance(); // SAFE: Returns Double
        out.write("Balance: " + balance);
    }
    
    // Scenario 4: Chained numeric getter
    public void showNestedId(HttpServletRequest request, HttpServletResponse response, java.util.List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("ID: " + users.get(0).getId()); // SAFE: Numeric getter
    }
    
    // Scenario 5: Multiple numeric getters
    public void showMultipleIds(HttpServletRequest request, HttpServletResponse response, java.util.List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("User: " + users.get(0).getId() + ", Account: " + users.get(0).getAccountId()); // SAFE: All numeric
    }
}

