package com.checkmarx.falsepositive.privacy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Privacy Violation - Safe Masking Patterns
 * 
 * Pattern: PII is properly masked before output, revealing only partial information
 * 
 * These patterns show PII that is redacted, masked, or hashed before output.
 * The actual sensitive data is not exposed. CxQL doesn't recognize these
 * masking patterns as safe.
 * 
 * All scenarios are FALSE POSITIVES - SAFE masked output.
 */
public class PrivacyViolation_FP_Masking {
    
    /**
     * FALSE POSITIVE: SSN masked to last 4 digits
     * Only "XXX-XX-1234" format shown - full SSN never exposed
     */
    public void showMaskedSSN(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ssn = request.getParameter("ssn");
        
        // Mask to show only last 4 digits
        String maskedSSN = "XXX-XX-" + ssn.substring(ssn.length() - 4);
        
        PrintWriter out = response.getWriter();
        out.write("SSN: " + maskedSSN); // FALSE POSITIVE - Properly masked
    }
    
    /**
     * FALSE POSITIVE: Credit card masked to last 4 digits
     * Only "**** **** **** 1234" format shown
     */
    public void showMaskedCreditCard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String creditCard = request.getParameter("creditCard");
        
        // Mask to show only last 4 digits
        String last4 = creditCard.substring(creditCard.length() - 4);
        String masked = "**** **** **** " + last4;
        
        PrintWriter out = response.getWriter();
        out.write("Card: " + masked); // FALSE POSITIVE - Properly masked
    }
    
    /**
     * FALSE POSITIVE: Phone number masked
     * Only "(***) ***-1234" format shown
     */
    public void showMaskedPhone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phone = request.getParameter("phone");
        
        // Mask to show only last 4 digits
        String last4 = phone.substring(phone.length() - 4);
        String masked = "(***) ***-" + last4;
        
        PrintWriter out = response.getWriter();
        out.write("Phone: " + masked); // FALSE POSITIVE - Properly masked
    }
    
    /**
     * FALSE POSITIVE: Email masked
     * Only "j***@example.com" format shown
     */
    public void showMaskedEmail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        
        int atIndex = email.indexOf('@');
        String masked = email.charAt(0) + "***" + email.substring(atIndex);
        
        PrintWriter out = response.getWriter();
        out.write("Email: " + masked); // FALSE POSITIVE - Properly masked
    }
    
    /**
     * FALSE POSITIVE: Password replaced with dots
     * Password value replaced with "••••••••"
     */
    public void showMaskedPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        
        // Replace with dots regardless of length
        String masked = "••••••••";
        
        PrintWriter out = response.getWriter();
        out.write("Password: " + masked); // FALSE POSITIVE - Password never shown
    }
    
    /**
     * FALSE POSITIVE: Account number hashed
     * SHA-256 hash shown instead of actual value
     */
    public void showHashedAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountNumber = request.getParameter("accountNumber");
        
        // Hash the account number
        String hashed = hashValue(accountNumber);
        
        PrintWriter out = response.getWriter();
        out.write("Account Hash: " + hashed); // FALSE POSITIVE - Irreversible hash
    }
    
    /**
     * FALSE POSITIVE: ID masked to checksum only
     * Only verification checksum shown
     */
    public void showChecksumOnly(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String driversLicense = request.getParameter("driversLicense");
        
        // Calculate checksum (just last 2 chars as example)
        String checksum = driversLicense.substring(driversLicense.length() - 2);
        
        PrintWriter out = response.getWriter();
        out.write("DL Checksum: " + checksum); // FALSE POSITIVE - Minimal info exposed
    }
    
    /**
     * FALSE POSITIVE: Length-only disclosure
     * Only the length of the value is shown
     */
    public void showLengthOnly(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String secretKey = request.getParameter("secretKey");
        
        int length = secretKey.length();
        
        PrintWriter out = response.getWriter();
        out.write("Secret key length: " + length); // FALSE POSITIVE - Only length shown
    }
    
    /**
     * FALSE POSITIVE: Existence check only
     * Only whether the value exists is shown
     */
    public void showExistenceOnly(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiKey = request.getParameter("apiKey");
        
        boolean hasKey = apiKey != null && !apiKey.isEmpty();
        
        PrintWriter out = response.getWriter();
        out.write("API Key configured: " + hasKey); // FALSE POSITIVE - Boolean only
    }
    
    // Helper method for hashing
    private String hashValue(String value) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(value.getBytes());
            StringBuilder hexHash = new StringBuilder();
            for (byte b : hash) {
                hexHash.append(String.format("%02x", b));
            }
            return hexHash.toString();
        } catch (Exception e) {
            return "HASH_ERROR";
        }
    }
}

