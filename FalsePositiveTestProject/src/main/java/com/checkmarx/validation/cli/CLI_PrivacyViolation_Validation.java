package com.checkmarx.validation.cli;

import java.io.*;
import java.util.Scanner;

/**
 * VALIDATION FILE: CLI Privacy Violation Patterns - Good vs Bad Findings
 * 
 * This file contains command-line application scenarios for Privacy Violation.
 * 
 * - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
 * - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
 */
public class CLI_PrivacyViolation_Validation {
    
    // Constants
    private static final String PASSWORD = "Password";
    private static final String SSN = "SSN";
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
    
    // BAD: Constant as label - SAFE because outputs constant, not value
    public static void badConstantLabel(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            System.out.println("Field: " + PASSWORD); // FALSE POSITIVE (BAD) - Outputs "Password"
        }
    }
    
    // BAD: Metadata format pattern - SAFE because format description
    public static void badMetadataFormat() {
        String passwordFormat = "Min 8 characters, 1 uppercase, 1 number";
        System.out.println("Password requirements: " + passwordFormat); // FALSE POSITIVE (BAD)
    }
    
    // BAD: StringBuilder without output - SAFE because no output
    public static void badStringBuilderNoOutput(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            StringBuilder sb = new StringBuilder();
            sb.append("Password: ");
            sb.append(password); // FALSE POSITIVE (BAD) - Just building, no output
            // No System.out or file output
        }
    }
    
    // BAD: Masked value - SAFE because properly redacted
    public static void badMaskedValue(String[] args) {
        if (args.length > 0) {
            String ssn = args[0];
            String masked = "XXX-XX-" + ssn.substring(ssn.length() - 4);
            System.out.println("SSN: " + masked); // FALSE POSITIVE (BAD) - Only last 4
        }
    }
    
    // BAD: Numeric account ID - SAFE because numeric
    public static void badNumericAccountId(String[] args) {
        if (args.length > 0) {
            long accountId = Long.parseLong(args[0]);
            System.out.println("Account ID: " + accountId); // FALSE POSITIVE (BAD) - Numeric
        }
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
    
    // GOOD: Password from args to console - VULNERABLE
    public static void goodPasswordOutput(String[] args) {
        if (args.length > 0) {
            String password = args[0]; // VULNERABLE: Actual password
            System.out.println("Your password: " + password); // TRUE POSITIVE (GOOD)
        }
    }
    
    // GOOD: SSN from Scanner to console - VULNERABLE
    public static void goodSSNOutput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter SSN: ");
        String ssn = scanner.nextLine(); // VULNERABLE: Actual SSN
        System.out.println("SSN: " + ssn); // TRUE POSITIVE (GOOD)
        scanner.close();
    }
    
    // GOOD: Credit card from Console - VULNERABLE
    public static void goodCreditCardOutput() {
        Console console = System.console();
        if (console != null) {
            String creditCard = console.readLine("Enter credit card: ");
            console.printf("Credit Card: %s%n", creditCard); // TRUE POSITIVE (GOOD)
        }
    }
    
    // GOOD: Password to file - VULNERABLE
    public static void goodPasswordToFile(String[] args) throws IOException {
        if (args.length > 0) {
            String password = args[0]; // VULNERABLE: Actual password
            FileWriter writer = new FileWriter("credentials.txt");
            writer.write("Password: " + password); // TRUE POSITIVE (GOOD) - PII to file
            writer.close();
        }
    }
    
    // GOOD: Password to stderr - VULNERABLE
    public static void goodPasswordToStderr(String[] args) {
        if (args.length > 0) {
            String password = args[0]; // VULNERABLE: Actual password
            System.err.println("Password: " + password); // TRUE POSITIVE (GOOD) - PII to stderr
        }
    }
    
    // GOOD: Multiple PII fields - VULNERABLE
    public static void goodMultiplePII(String[] args) {
        if (args.length >= 3) {
            String name = args[0];
            String ssn = args[1];
            String dob = args[2];
            System.out.println("User: " + name + ", SSN: " + ssn + ", DOB: " + dob); // TRUE POSITIVE
        }
    }
    
    // GOOD: PII in exception message - VULNERABLE
    public static void goodExceptionWithPII(String[] args) {
        if (args.length > 0) {
            String email = args[0];
            try {
                throw new RuntimeException("Failed for user: " + email);
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage()); // TRUE POSITIVE (GOOD)
            }
        }
    }
    
    // GOOD: StringBuilder WITH output - VULNERABLE
    public static void goodStringBuilderWithOutput(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            StringBuilder sb = new StringBuilder();
            sb.append("Password: ");
            sb.append(password);
            System.out.println(sb.toString()); // TRUE POSITIVE (GOOD) - Password exposed
        }
    }
    
    // MIXED: Metadata (BAD/FP) + Actual PII (GOOD/TP)
    public static void mixedMetadataAndPII(String[] args) {
        // BAD/FP: Metadata
        String passwordFormat = "Min 8 characters";
        System.out.println("Requirements: " + passwordFormat); // FALSE POSITIVE (BAD)
        
        // GOOD/TP: Actual password
        if (args.length > 0) {
            String password = args[0];
            System.out.println("Your password: " + password); // TRUE POSITIVE (GOOD)
        }
    }
}

