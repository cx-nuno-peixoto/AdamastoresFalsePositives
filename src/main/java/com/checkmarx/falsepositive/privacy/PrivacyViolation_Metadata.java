package com.checkmarx.falsepositive.privacy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * FALSE POSITIVE SCENARIOS: Privacy Violation - Metadata Variables
 * 
 * Pattern: Variable names match PII patterns but contain metadata/configuration
 * 
 * CxQL flags these because:
 * - Variable name matches PII pattern (*phone*, *password*, *ssn*, etc.)
 * - Method has HttpServletRequest parameter (input source)
 * - Variable flows to output
 * 
 * But these are FALSE POSITIVES because:
 * - Variables contain metadata (format patterns, labels, configuration)
 * - NOT actual PII data
 * - Assigned hardcoded constants, not from request.getParameter()
 */
public class PrivacyViolation_Metadata {
    
    // Scenario 1: Phone format pattern
    public void showPhoneFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phoneFormat = "XXX-XXX-XXXX"; // SAFE: Format pattern, not actual phone
        PrintWriter out = response.getWriter();
        out.write("Phone format: " + phoneFormat); // FLAGGED but SAFE: Metadata
    }
    
    // Scenario 2: Password requirements
    public void showPasswordRequirements(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String passwordMinLength = "8"; // SAFE: Configuration, not actual password
        PrintWriter out = response.getWriter();
        out.write("Password min length: " + passwordMinLength);
    }
    
    // Scenario 3: SSN format
    public void showSSNFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ssnFormat = "XXX-XX-XXXX"; // SAFE: Format pattern
        PrintWriter out = response.getWriter();
        out.write("SSN format: " + ssnFormat);
    }
    
    // Scenario 4: Credit card label
    public void showCreditCardLabel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String creditCardLabel = "Credit Card Number"; // SAFE: Label text
        PrintWriter out = response.getWriter();
        out.write("Label: " + creditCardLabel);
    }
    
    // Scenario 5: Account number prefix
    public void showAccountPrefix(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountNumberPrefix = "ACC-"; // SAFE: Prefix pattern
        PrintWriter out = response.getWriter();
        out.write("Account prefix: " + accountNumberPrefix);
    }
    
    // Scenario 6: Passport format
    public void showPassportFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String passportFormat = "AA1234567"; // SAFE: Format example
        PrintWriter out = response.getWriter();
        out.write("Passport format: " + passportFormat);
    }
    
    // Scenario 7: DOB format
    public void showDOBFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String DOBFormat = "MM/DD/YYYY"; // SAFE: Date format
        PrintWriter out = response.getWriter();
        out.write("DOB format: " + DOBFormat);
    }
    
    // Scenario 8: Credentials type
    public void showCredentialsType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String credentialsType = "OAuth2"; // SAFE: Authentication type
        PrintWriter out = response.getWriter();
        out.write("Auth type: " + credentialsType);
    }
}

