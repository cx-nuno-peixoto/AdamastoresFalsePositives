package com.checkmarx.falsepositive.xss.stored;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * FALSE POSITIVE SCENARIOS: Stored XSS - Boolean Fields from Database
 * 
 * Pattern: Boolean fields retrieved from database
 * 
 * These scenarios demonstrate that boolean values from database are safe.
 * Booleans can only be true/false, not XSS payloads.
 */
public class StoredXSS_BooleanFields {
    
    // Mock entity with boolean fields
    public static class User {
        private Long id;
        private String name;
        private boolean active;
        private Boolean verified;
        private boolean admin;
        
        public User(Long id, String name, boolean active, Boolean verified, boolean admin) {
            this.id = id;
            this.name = name;
            this.active = active;
            this.verified = verified;
            this.admin = admin;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public boolean isActive() { return active; }
        public Boolean getVerified() { return verified; }
        public boolean isAdmin() { return admin; }
    }
    
    // Scenario 1: Boolean getter from database
    public void showActiveStatus(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        boolean active = users.get(0).isActive(); // SAFE: Boolean from database
        out.write("Active: " + active); // SAFE: Boolean outputs "true" or "false"
    }
    
    // Scenario 2: Boolean wrapper from database
    public void showVerifiedStatus(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Boolean verified = users.get(0).getVerified(); // SAFE: Boolean wrapper
        out.write("Verified: " + verified); // SAFE: Boolean outputs "true" or "false"
    }
    
    // Scenario 3: Boolean in conditional
    public void showAdminMessage(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        boolean isAdmin = users.get(0).isAdmin(); // SAFE: Boolean from database
        
        if (isAdmin) { // SAFE: Boolean condition
            out.write("User is admin");
        } else {
            out.write("User is not admin");
        }
    }
    
    // Scenario 4: Boolean toString()
    public void showBooleanString(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        Boolean verified = users.get(0).getVerified(); // SAFE: Boolean wrapper
        String verifiedStr = verified.toString(); // SAFE: toString() returns "true" or "false"
        out.write("Verified: " + verifiedStr);
    }
    
    // Scenario 5: Boolean ternary expression
    public void showStatusMessage(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        boolean active = users.get(0).isActive(); // SAFE: Boolean from database
        String message = active ? "Active" : "Inactive"; // SAFE: Ternary with boolean
        out.write("Status: " + message);
    }
    
    // Scenario 6: Multiple boolean fields
    public void showUserFlags(HttpServletRequest request, HttpServletResponse response, List<User> users) throws IOException {
        PrintWriter out = response.getWriter();
        User user = users.get(0);
        
        out.write("Active: " + user.isActive()); // SAFE: Boolean
        out.write(", Verified: " + user.getVerified()); // SAFE: Boolean
        out.write(", Admin: " + user.isAdmin()); // SAFE: Boolean
    }
}

