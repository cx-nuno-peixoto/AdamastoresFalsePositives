package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Whitelist Validation Patterns
 * 
 * Pattern: User input validated against a fixed whitelist of allowed values
 * 
 * Whitelist validation ensures only known-safe values are used.
 * CxQL doesn't recognize whitelist checks as sanitization.
 * 
 * All scenarios are FALSE POSITIVES - SAFE code that should NOT be flagged.
 */
public class ReflectedXSS_FP_WhitelistValidation {
    
    private static final Set<String> ALLOWED_CATEGORIES = new HashSet<>(
        Arrays.asList("electronics", "books", "clothing", "food", "toys")
    );
    
    private static final List<String> ALLOWED_COLORS = Arrays.asList(
        "red", "green", "blue", "yellow", "orange", "purple"
    );
    
    private static final String[] ALLOWED_SIZES = {"XS", "S", "M", "L", "XL", "XXL"};
    
    /**
     * FALSE POSITIVE: Set.contains() whitelist check
     * Only values in the HashSet are allowed
     */
    public void showSetWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String category = request.getParameter("category");
        
        PrintWriter out = response.getWriter();
        if (ALLOWED_CATEGORIES.contains(category)) {
            out.write("Category: " + category); // FALSE POSITIVE - Whitelisted value
        } else {
            out.write("Invalid category");
        }
    }
    
    /**
     * FALSE POSITIVE: List.contains() whitelist check
     * Only values in the List are allowed
     */
    public void showListWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String color = request.getParameter("color");
        
        PrintWriter out = response.getWriter();
        if (ALLOWED_COLORS.contains(color)) {
            out.write("Color: " + color); // FALSE POSITIVE - Whitelisted value
        } else {
            out.write("Invalid color");
        }
    }
    
    /**
     * FALSE POSITIVE: Array contains check
     * Only values in the array are allowed
     */
    public void showArrayWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String size = request.getParameter("size");
        
        boolean isValid = Arrays.asList(ALLOWED_SIZES).contains(size);
        
        PrintWriter out = response.getWriter();
        if (isValid) {
            out.write("Size: " + size); // FALSE POSITIVE - Whitelisted value
        } else {
            out.write("Invalid size");
        }
    }
    
    /**
     * FALSE POSITIVE: Switch statement whitelist
     * Only specific case values are processed
     */
    public void showSwitchWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String status = request.getParameter("status");
        
        PrintWriter out = response.getWriter();
        switch (status) {
            case "pending":
            case "approved":
            case "rejected":
            case "completed":
                out.write("Status: " + status); // FALSE POSITIVE - Whitelisted by switch
                break;
            default:
                out.write("Invalid status");
        }
    }
    
    /**
     * FALSE POSITIVE: Enum.valueOf() validation
     * Only valid enum values pass
     */
    public void showEnumWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String priority = request.getParameter("priority");
        
        PrintWriter out = response.getWriter();
        try {
            Priority p = Priority.valueOf(priority.toUpperCase());
            out.write("Priority: " + p.name()); // FALSE POSITIVE - Valid enum value
        } catch (IllegalArgumentException e) {
            out.write("Invalid priority");
        }
    }
    
    /**
     * FALSE POSITIVE: Stream filter whitelist
     * Filtered through stream predicate
     */
    public void showStreamWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        
        boolean isValid = ALLOWED_CATEGORIES.stream()
            .anyMatch(allowed -> allowed.equals(type));
        
        PrintWriter out = response.getWriter();
        if (isValid) {
            out.write("Type: " + type); // FALSE POSITIVE - Stream filtered
        } else {
            out.write("Invalid type");
        }
    }
    
    /**
     * FALSE POSITIVE: equalsIgnoreCase whitelist
     * Explicit string comparison
     */
    public void showExplicitWhitelist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        
        PrintWriter out = response.getWriter();
        if ("view".equalsIgnoreCase(action) || 
            "edit".equalsIgnoreCase(action) || 
            "delete".equalsIgnoreCase(action)) {
            out.write("Action: " + action); // FALSE POSITIVE - Explicit whitelist
        } else {
            out.write("Invalid action");
        }
    }
    
    // Enum for whitelist validation
    private enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}

