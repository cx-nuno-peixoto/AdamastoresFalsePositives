package com.checkmarx.validation.privacy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * VALIDATION FILE: Privacy Violation - Good vs Bad Findings
 *
 * This file contains BOTH:
 * - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
 * - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
 */
public class PrivacyViolation_Validation {
    
    // Constants for testing
    private static final String PASSWORD = "Password";
    private static final String SSN = "SSN";
    private static final String CREDIT_CARD = "CreditCard";
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========

    // BAD: Constant as parameter name (Bug 12908)
    public void badConstantAsKey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String password = request.getParameter(PASSWORD); // PASSWORD is "Password"
        out.write("Field: " + PASSWORD); // FALSE POSITIVE (BAD) - Outputs "Password", not actual password
    }

    // BAD: Metadata variable (format pattern)
    public void badMetadataFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String passwordFormat = "Min 8 characters"; // SAFE: Format description
        out.write("Password requirements: " + passwordFormat); // FALSE POSITIVE (BAD) - Should NOT be flagged
    }

    // BAD: Metadata variable (configuration)
    public void badMetadataConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String ssnFormat = "XXX-XX-XXXX"; // SAFE: Format pattern
        out.write("SSN Format: " + ssnFormat); // FALSE POSITIVE (BAD) - Should NOT be flagged
    }

    // BAD: StringBuilder.append() without output (Bug 300145)
    public void badStringBuilderNoOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password");
        StringBuilder sb = new StringBuilder();
        sb.append("Password: ");
        sb.append(password); // FALSE POSITIVE (BAD) - Just building string in memory, no output
        // No output to response - password not exposed to unauthorized actors
    }

    // BAD: Validation message with field name
    public void badValidationMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String fieldName = "password"; // SAFE: Field name, not actual password
        out.write("Missing field: " + fieldName); // FALSE POSITIVE (BAD) - Should NOT be flagged
    }

    // BAD: Account number as ID (numeric)
    public void badAccountId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String accountIdParam = request.getParameter("accountId");
        long accountId = Long.parseLong(accountIdParam); // SAFE: Numeric ID
        out.write("Account ID: " + accountId); // FALSE POSITIVE (BAD) - Should NOT be flagged
    }

    // BAD: Phone format pattern
    public void badPhoneFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String phoneFormat = "(XXX) XXX-XXXX"; // SAFE: Format pattern
        out.write("Phone format: " + phoneFormat); // FALSE POSITIVE (BAD) - Should NOT be flagged
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========

    // GOOD: Actual password sent to response
    public void goodPasswordOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String password = request.getParameter("password"); // VULNERABLE: Actual password
        out.write("Your password: " + password); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: SSN sent to response
    public void goodSSNOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String ssn = request.getParameter("ssn"); // VULNERABLE: Actual SSN
        out.write("SSN: " + ssn); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Credit card sent to response
    public void goodCreditCardOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String creditCard = request.getParameter("creditCard"); // VULNERABLE: Actual credit card
        out.write("Credit Card: " + creditCard); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Password logged to console
    public void goodPasswordLogging(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter("password"); // VULNERABLE: Actual password
        System.out.println("User password: " + password); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Credentials sent to response
    public void goodCredentialsOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String credentials = request.getParameter("credentials"); // VULNERABLE: Actual credentials
        out.write("Credentials: " + credentials); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Secret key sent to response
    public void goodSecretOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String secret = request.getParameter("secret"); // VULNERABLE: Actual secret
        out.write("Secret: " + secret); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Auth token sent to response
    public void goodAuthTokenOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String authToken = request.getParameter("authToken"); // VULNERABLE: Actual token
        out.write("Token: " + authToken); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: DOB sent to response
    public void goodDOBOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String dob = request.getParameter("dob"); // VULNERABLE: Actual DOB
        out.write("Date of Birth: " + dob); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Passport sent to response
    public void goodPassportOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String passport = request.getParameter("passport"); // VULNERABLE: Actual passport
        out.write("Passport: " + passport); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: Social Security sent to response
    public void goodSocialSecurityOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String socialSecurity = request.getParameter("socialSecurity"); // VULNERABLE: Actual SSN
        out.write("Social Security: " + socialSecurity); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // GOOD: StringBuilder with output to response
    public void goodStringBuilderWithOutput(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String password = request.getParameter("password");
        StringBuilder sb = new StringBuilder();
        sb.append("Password: ");
        sb.append(password);
        out.write(sb.toString()); // TRUE POSITIVE (GOOD) - SHOULD be flagged (password exposed to response)
    }

    // MIXED: Bad/FP metadata + Good/TP actual PII
    public void mixedGoodAndBad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        // BAD/FP: Metadata
        String passwordFormat = "Min 8 characters";
        out.write("Requirements: " + passwordFormat); // FALSE POSITIVE (BAD) - Should NOT be flagged

        // GOOD/TP: Actual password
        String password = request.getParameter("password");
        out.write(", Your password: " + password); // TRUE POSITIVE (GOOD) - SHOULD be flagged after fix
    }

    // ========== ADDITIONAL FALSE POSITIVES (BAD) ==========

    // BAD: Error message with field name (not value)
    public void badErrorFieldName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String errorField = "creditCard"; // SAFE: Field name, not actual credit card
        out.write("Validation error on field: " + errorField); // FALSE POSITIVE (BAD)
    }

    // BAD: Masked/redacted value
    public void badMaskedValue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String ssn = request.getParameter("ssn");
        String masked = "XXX-XX-" + ssn.substring(ssn.length() - 4); // SAFE: Only last 4 digits
        out.write("SSN: " + masked); // FALSE POSITIVE (BAD) - Properly masked
    }

    // ========== ADDITIONAL TRUE POSITIVES (GOOD) ==========

    // GOOD: PII in exception message
    public void goodExceptionWithPII(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String email = request.getParameter("email");
        try {
            // Some operation
            throw new RuntimeException("Failed for user: " + email);
        } catch (RuntimeException e) {
            out.write("Error: " + e.getMessage()); // TRUE POSITIVE (GOOD) - PII in exception
        }
    }

    // GOOD: PII in logging (System.err)
    public void goodLoggingSystemErr(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ssn = request.getParameter("ssn");
        System.err.println("Processing SSN: " + ssn); // TRUE POSITIVE (GOOD) - PII to stderr
    }

    // GOOD: Multiple PII fields in single output
    public void goodMultiplePII(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");
        String ssn = request.getParameter("ssn");
        String dob = request.getParameter("dob");
        out.write("User: " + name + ", SSN: " + ssn + ", DOB: " + dob); // TRUE POSITIVE (GOOD) - Multiple PII
    }

    // GOOD: Medical record number
    public void goodMedicalRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String mrn = request.getParameter("medicalRecordNumber");
        out.write("Medical Record: " + mrn); // TRUE POSITIVE (GOOD) - Medical PII
    }

    // GOOD: Driver's license
    public void goodDriversLicense(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String license = request.getParameter("driversLicense");
        out.write("License: " + license); // TRUE POSITIVE (GOOD) - Driver's license PII
    }
}

