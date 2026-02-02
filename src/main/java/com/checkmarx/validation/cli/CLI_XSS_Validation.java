package com.checkmarx.validation.cli;

import java.io.*;
import java.util.Scanner;

/**
 * VALIDATION FILE: CLI XSS Patterns - Good vs Bad Findings
 * 
 * This file contains command-line application scenarios for XSS-like output injection.
 * 
 * - FALSE POSITIVES (BAD) - Should NOT be flagged after query fixes
 * - TRUE POSITIVES (GOOD) - SHOULD be flagged after query fixes
 */
public class CLI_XSS_Validation {
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
    
    // BAD: Type conversion from args - SAFE because numeric
    public static void badArgsNumeric(String[] args) {
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]); // SAFE: Converts to int
            System.out.println("Count: " + count); // FALSE POSITIVE (BAD) - Numeric output
        }
    }
    
    // BAD: Boolean from args - SAFE because boolean
    public static void badArgsBoolean(String[] args) {
        if (args.length > 0) {
            boolean flag = Boolean.parseBoolean(args[0]); // SAFE: Only true/false
            System.out.println("Flag: " + flag); // FALSE POSITIVE (BAD) - Boolean output
        }
    }
    
    // BAD: Scanner with numeric input - SAFE because numeric
    public static void badScannerNumeric() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter age: ");
        int age = scanner.nextInt(); // SAFE: Reads int directly
        System.out.println("Age: " + age); // FALSE POSITIVE (BAD) - Numeric output
        scanner.close();
    }
    
    // BAD: Console with numeric conversion - SAFE because numeric
    public static void badConsoleNumeric() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter ID: ");
            long id = Long.parseLong(input); // SAFE: Converts to long
            console.printf("ID: %d%n", id); // FALSE POSITIVE (BAD) - Numeric output
        }
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
    
    // GOOD: Direct args output - VULNERABLE
    public static void goodArgsDirectOutput(String[] args) {
        if (args.length > 0) {
            String input = args[0]; // VULNERABLE: Direct string from args
            System.out.println("Input: " + input); // TRUE POSITIVE (GOOD) - Direct output
        }
    }
    
    // GOOD: Scanner string input - VULNERABLE
    public static void goodScannerString() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine(); // VULNERABLE: String input
        System.out.println("Hello, " + name); // TRUE POSITIVE (GOOD) - String output
        scanner.close();
    }
    
    // GOOD: Console string input - VULNERABLE
    public static void goodConsoleString() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter command: ");
            console.printf("Executing: %s%n", input); // TRUE POSITIVE (GOOD) - String output
        }
    }
    
    // GOOD: BufferedReader input - VULNERABLE
    public static void goodBufferedReaderInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter message: ");
        String message = reader.readLine(); // VULNERABLE: String input
        System.out.println("Message: " + message); // TRUE POSITIVE (GOOD) - String output
    }
    
    // GOOD: Multiple args concatenation - VULNERABLE
    public static void goodArgsConcat(String[] args) {
        if (args.length >= 2) {
            String fullName = args[0] + " " + args[1]; // VULNERABLE: String concat
            System.out.println("Full name: " + fullName); // TRUE POSITIVE (GOOD)
        }
    }
    
    // GOOD: File output with user input - VULNERABLE
    public static void goodFileOutput(String[] args) throws IOException {
        if (args.length > 0) {
            String content = args[0]; // VULNERABLE: User input
            FileWriter writer = new FileWriter("output.html");
            writer.write("<html><body>" + content + "</body></html>"); // TRUE POSITIVE (GOOD)
            writer.close();
        }
    }
    
    // GOOD: PrintWriter output - VULNERABLE
    public static void goodPrintWriterOutput(String[] args) throws IOException {
        if (args.length > 0) {
            String data = args[0]; // VULNERABLE: User input
            PrintWriter pw = new PrintWriter(new FileWriter("log.txt"));
            pw.println("Data: " + data); // TRUE POSITIVE (GOOD) - String to file
            pw.close();
        }
    }
    
    // MIXED: Numeric (BAD/FP) + String (GOOD/TP)
    public static void mixedArgsOutput(String[] args) {
        if (args.length >= 2) {
            int id = Integer.parseInt(args[0]); // SAFE: Numeric
            System.out.println("ID: " + id); // FALSE POSITIVE (BAD)
            
            String name = args[1]; // VULNERABLE: String
            System.out.println("Name: " + name); // TRUE POSITIVE (GOOD)
        }
    }
}

