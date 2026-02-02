package com.checkmarx.falsepositive.privacy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FALSE POSITIVE SCENARIOS: Privacy Violation - StringBuilder.append()
 * 
 * Based on Bug 300145
 * Pattern: INPUT → PII variable → StringBuilder.append() → (no output to unauthorized actors)
 * 
 * CxQL will flag these because:
 * - Input source exists (HttpServletRequest)
 * - PII variable name matches pattern
 * - StringBuilder.append() is treated as a sink
 * 
 * But these are FALSE POSITIVES because:
 * - StringBuilder.append() is an intermediate operation (builds string in memory)
 * - NOT exposing data to unauthorized actors
 * - The actual vulnerability would be when sb.toString() is sent to response/log
 */
public class PrivacyViolation_StringBuilder {
    
    // Scenario 1: Password appended to StringBuilder (no output)
    public void passwordStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        StringBuilder sb = new StringBuilder();
        sb.append("User password: ");
        sb.append(password); // FLAGGED but SAFE: append() is not a sink, just building string in memory
        // No output - the password is not exposed to unauthorized actors
    }
    
    // Scenario 2: SSN appended to StringBuilder (no output)
    public void ssnStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ssn = request.getParameter("ssn");
        StringBuilder sb = new StringBuilder();
        sb.append("SSN: ");
        sb.append(ssn); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 3: Credit card appended to StringBuilder (no output)
    public void creditCardStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String creditCard = request.getParameter("creditCard");
        StringBuilder sb = new StringBuilder();
        sb.append("Credit Card: ");
        sb.append(creditCard); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 4: Credentials appended to StringBuilder (no output)
    public void credentialsStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String credentials = request.getParameter("credentials");
        StringBuilder sb = new StringBuilder();
        sb.append("Credentials: ");
        sb.append(credentials); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 5: Secret appended to StringBuilder (no output)
    public void secretStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String secret = request.getParameter("secret");
        StringBuilder sb = new StringBuilder();
        sb.append("Secret: ");
        sb.append(secret); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 6: Account number appended to StringBuilder (no output)
    public void accountNumberStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accountNumber = request.getParameter("accountNumber");
        StringBuilder sb = new StringBuilder();
        sb.append("Account: ");
        sb.append(accountNumber); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 7: Auth token appended to StringBuilder (no output)
    public void authTokenStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getParameter("authToken");
        StringBuilder sb = new StringBuilder();
        sb.append("Token: ");
        sb.append(authToken); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 8: DOB appended to StringBuilder (no output)
    public void dobStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String DOB = request.getParameter("dob");
        StringBuilder sb = new StringBuilder();
        sb.append("Date of Birth: ");
        sb.append(DOB); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 9: Passport appended to StringBuilder (no output)
    public void passportStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String passport = request.getParameter("passport");
        StringBuilder sb = new StringBuilder();
        sb.append("Passport: ");
        sb.append(passport); // FLAGGED but SAFE: just building string in memory
    }
    
    // Scenario 10: Social Security appended to StringBuilder (no output)
    public void socialSecurityStringBuilder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String socialSecurity = request.getParameter("socialSecurity");
        StringBuilder sb = new StringBuilder();
        sb.append("Social Security: ");
        sb.append(socialSecurity); // FLAGGED but SAFE: just building string in memory
    }
}

