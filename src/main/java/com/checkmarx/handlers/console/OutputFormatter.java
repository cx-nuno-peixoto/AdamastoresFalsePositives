package com.checkmarx.handlers.console;

import java.io.*;
import java.util.Scanner;

public class OutputFormatter {
    
    private static final String FIELD_KEY = "Password";
    private static final String ID_KEY = "SSN";
    
    // PV-MR:01
    public static void scenario01(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            System.out.println("Field: " + FIELD_KEY);
        }
    }
    
    // PV-MR:02
    public static void scenario02() {
        String passwordFormat = "Min 8 characters, 1 uppercase, 1 number";
        System.out.println("Password requirements: " + passwordFormat);
    }
    
    // PV-MR:03
    public static void scenario03(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            StringBuilder sb = new StringBuilder();
            sb.append("Password: ");
            sb.append(password);
        }
    }
    
    // PV-MR:04
    public static void scenario04(String[] args) {
        if (args.length > 0) {
            String ssn = args[0];
            String masked = "XXX-XX-" + ssn.substring(ssn.length() - 4);
            System.out.println("SSN: " + masked);
        }
    }
    
    // PV-MR:05
    public static void scenario05(String[] args) {
        if (args.length > 0) {
            long accountId = Long.parseLong(args[0]);
            System.out.println("Account ID: " + accountId);
        }
    }
    
    // PV-BR:06
    public static void scenario06(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            System.out.println("Your password: " + password);
        }
    }
    
    // PV-BR:07
    public static void scenario07() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter SSN: ");
        String ssn = scanner.nextLine();
        System.out.println("SSN: " + ssn);
        scanner.close();
    }
    
    // PV-BR:08
    public static void scenario08() {
        Console console = System.console();
        if (console != null) {
            String creditCard = console.readLine("Enter credit card: ");
            console.printf("Credit Card: %s%n", creditCard);
        }
    }
    
    // PV-BR:09
    public static void scenario09(String[] args) throws IOException {
        if (args.length > 0) {
            String password = args[0];
            FileWriter writer = new FileWriter("credentials.txt");
            writer.write("Password: " + password);
            writer.close();
        }
    }
    
    // PV-BR:10
    public static void scenario10(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            System.err.println("Password: " + password);
        }
    }
    
    // PV-BR:11
    public static void scenario11(String[] args) {
        if (args.length >= 3) {
            String name = args[0];
            String ssn = args[1];
            String dob = args[2];
            System.out.println("User: " + name + ", SSN: " + ssn + ", DOB: " + dob);
        }
    }
    
    // PV-BR:12
    public static void scenario12(String[] args) {
        if (args.length > 0) {
            String email = args[0];
            try {
                throw new RuntimeException("Failed for user: " + email);
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    // PV-BR:13
    public static void scenario13(String[] args) {
        if (args.length > 0) {
            String password = args[0];
            StringBuilder sb = new StringBuilder();
            sb.append("Password: ");
            sb.append(password);
            System.out.println(sb.toString());
        }
    }
    
    // PV-BR:14
    public static void scenario14(String[] args) {
        String passwordFormat = "Min 8 characters";
        System.out.println("Requirements: " + passwordFormat);
        if (args.length > 0) {
            String password = args[0];
            System.out.println("Your password: " + password);
        }
    }
}

