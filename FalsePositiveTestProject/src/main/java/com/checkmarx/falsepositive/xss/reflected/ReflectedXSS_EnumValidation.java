package com.checkmarx.falsepositive.xss.reflected;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Reflected XSS - Enum Validation
 * 
 * Pattern: User input validated against enum values
 * 
 * These scenarios demonstrate that enum validation sanitizes input.
 * Only valid enum values can be output, XSS payloads cause exceptions.
 */
public class ReflectedXSS_EnumValidation {
    
    // Enum definitions
    public enum UserRole {
        ADMIN, USER, GUEST, MODERATOR
    }
    
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER
    }
    
    // Scenario 1: Enum.valueOf() validation
    public void showUserRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roleParam = request.getParameter("role");
        PrintWriter out = response.getWriter();
        
        try {
            UserRole role = UserRole.valueOf(roleParam.toUpperCase()); // SAFE: Enum validation
            out.write("Role: " + role); // SAFE: Only valid enum values
        } catch (IllegalArgumentException e) {
            out.write("Invalid role");
        }
    }
    
    // Scenario 2: Enum with switch statement
    public void showOrderStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String statusParam = request.getParameter("status");
        PrintWriter out = response.getWriter();
        
        try {
            OrderStatus status = OrderStatus.valueOf(statusParam.toUpperCase()); // SAFE: Enum validation
            
            switch (status) { // SAFE: Only enum values in switch
                case PENDING:
                    out.write("Status: Pending");
                    break;
                case PROCESSING:
                    out.write("Status: Processing");
                    break;
                case SHIPPED:
                    out.write("Status: Shipped");
                    break;
                case DELIVERED:
                    out.write("Status: Delivered");
                    break;
                case CANCELLED:
                    out.write("Status: Cancelled");
                    break;
            }
        } catch (IllegalArgumentException e) {
            out.write("Invalid status");
        }
    }
    
    // Scenario 3: Enum.name() output
    public void showPaymentMethod(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String methodParam = request.getParameter("method");
        PrintWriter out = response.getWriter();
        
        try {
            PaymentMethod method = PaymentMethod.valueOf(methodParam.toUpperCase()); // SAFE: Enum validation
            out.write("Payment: " + method.name()); // SAFE: Enum.name() returns safe string
        } catch (IllegalArgumentException e) {
            out.write("Invalid payment method");
        }
    }
    
    // Scenario 4: Enum with ordinal()
    public void showRoleOrdinal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roleParam = request.getParameter("role");
        PrintWriter out = response.getWriter();
        
        try {
            UserRole role = UserRole.valueOf(roleParam.toUpperCase()); // SAFE: Enum validation
            out.write("Role ordinal: " + role.ordinal()); // SAFE: ordinal() returns int
        } catch (IllegalArgumentException e) {
            out.write("Invalid role");
        }
    }
    
    // Scenario 5: Enum comparison
    public void checkAdminRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roleParam = request.getParameter("role");
        PrintWriter out = response.getWriter();
        
        try {
            UserRole role = UserRole.valueOf(roleParam.toUpperCase()); // SAFE: Enum validation
            if (role == UserRole.ADMIN) { // SAFE: Enum comparison
                out.write("Admin role: " + role); // SAFE: Validated enum
            } else {
                out.write("Non-admin role: " + role);
            }
        } catch (IllegalArgumentException e) {
            out.write("Invalid role");
        }
    }
    
    // Scenario 6: Enum.values() iteration
    public void showAllRoles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String selectedParam = request.getParameter("selected");
        PrintWriter out = response.getWriter();
        
        try {
            UserRole selected = UserRole.valueOf(selectedParam.toUpperCase()); // SAFE: Enum validation
            
            out.write("<select>");
            for (UserRole role : UserRole.values()) { // SAFE: Iterating enum values
                String selectedAttr = (role == selected) ? " selected" : "";
                out.write("<option" + selectedAttr + ">" + role.name() + "</option>"); // SAFE: Enum names
            }
            out.write("</select>");
        } catch (IllegalArgumentException e) {
            out.write("Invalid role");
        }
    }
}

