package com.checkmarx.falsepositive.privacy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * FALSE POSITIVE SCENARIOS: Privacy Violation - Constants as Keys
 * 
 * Based on Bug 12908
 * Pattern: Constants used as parameter names/keys
 * 
 * These scenarios demonstrate that constants used as keys output the constant string,
 * not actual PII data.
 */
public class PrivacyViolation_Constants {
    
    // Constants that match PII patterns
    private static final String PASSWORD = "Password";
    private static final String SSN = "SSN";
    private static final String CREDIT_CARD = "CreditCard";
    private static final String ACCOUNT_NUMBER = "AccountNumber";
    private static final String CREDENTIALS = "Credentials";
    private static final String SECRET = "Secret";
    private static final String AUTH_TOKEN = "AuthToken";
    private static final String DOB = "DOB";
    private static final String PASSPORT = "Passport";
    private static final String PHONE = "Phone";
    
    // Scenario 1: PASSWORD constant as parameter name (Bug 12908 exact pattern)
    public void getPasswordField(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter(PASSWORD); // PASSWORD is "Password", not actual password
        PrintWriter out = response.getWriter();
        out.write("Field: " + PASSWORD); // SAFE: Outputs "Password", not actual password data
    }
    
    // Scenario 2: SSN constant as map key
    public void getSSNField(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fields = new HashMap<>();
        fields.put(SSN, "Social Security Number"); // SSN is "SSN", not actual SSN
        
        PrintWriter out = response.getWriter();
        out.write("Field name: " + SSN); // SAFE: Outputs "SSN"
    }
    
    // Scenario 3: CREDIT_CARD constant in validation
    public void validateCreditCard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fieldName = CREDIT_CARD; // CREDIT_CARD is "CreditCard"
        String value = request.getParameter(fieldName);
        
        PrintWriter out = response.getWriter();
        out.write("Validating field: " + fieldName); // SAFE: Outputs "CreditCard"
    }
    
    // Scenario 4: ACCOUNT_NUMBER constant as label
    public void showAccountLabel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String label = ACCOUNT_NUMBER; // ACCOUNT_NUMBER is "AccountNumber"
        
        PrintWriter out = response.getWriter();
        out.write("Label: " + label); // SAFE: Outputs "AccountNumber"
    }
    
    // Scenario 5: CREDENTIALS constant in error message
    public void showCredentialsError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("Missing field: " + CREDENTIALS); // SAFE: Outputs "Credentials"
    }
    
    // Scenario 6: SECRET constant as configuration key
    public void getSecretConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put(SECRET, "api_secret_key"); // SECRET is "Secret"
        
        PrintWriter out = response.getWriter();
        out.write("Config key: " + SECRET); // SAFE: Outputs "Secret"
    }
    
    // Scenario 7: AUTH_TOKEN constant in header name
    public void getAuthHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String headerName = AUTH_TOKEN; // AUTH_TOKEN is "AuthToken"
        
        PrintWriter out = response.getWriter();
        out.write("Header: " + headerName); // SAFE: Outputs "AuthToken"
    }
    
    // Scenario 8: DOB constant as form field
    public void getDOBField(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fieldName = DOB; // DOB is "DOB"
        
        PrintWriter out = response.getWriter();
        out.write("Field: " + fieldName); // SAFE: Outputs "DOB"
    }
    
    // Scenario 9: PASSPORT constant in validation message
    public void validatePassport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        out.write("Validating " + PASSPORT); // SAFE: Outputs "Passport"
    }
    
    // Scenario 10: PHONE constant as parameter key
    public void getPhoneParameter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String paramName = PHONE; // PHONE is "Phone"
        String value = request.getParameter(paramName);
        
        PrintWriter out = response.getWriter();
        out.write("Parameter: " + paramName); // SAFE: Outputs "Phone"
    }
}

