package com.checkmarx.validation.cli;

import java.io.*;
import java.util.Scanner;

/**
 * VALIDATION FILE: CLI Loop Condition Patterns - Good vs Bad Findings
 * 
 * This file contains command-line application scenarios for Unchecked Input for Loop Condition.
 * 
 * - FALSE POSITIVES (BAD) - SAFE because they have EXPLICIT BOUNDS
 * - TRUE POSITIVES (GOOD) - VULNERABLE because they have NO BOUNDS
 */
public class CLI_LoopCondition_Validation {
    
    // ========== FALSE POSITIVES (BAD) - Should NOT be flagged after fix ==========
    
    // BAD: Args with Math.min bound - SAFE because max 100
    public static void badArgsBounded(String[] args) {
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]);
            int bounded = Math.min(count, 100); // SAFE: Max 100 iterations
            
            for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Bounded
                System.out.println("Item " + i);
            }
        }
    }
    
    // BAD: Scanner with ternary bound - SAFE because max 50
    public static void badScannerBounded() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter count: ");
        int count = scanner.nextInt();
        int bounded = (count > 50) ? 50 : count; // SAFE: Max 50 iterations
        
        for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD) - Bounded
            System.out.println("Item " + i);
        }
        scanner.close();
    }
    
    // BAD: Console with if-guard - SAFE because explicit check
    public static void badConsoleIfGuard() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter count (max 100): ");
            int count = Integer.parseInt(input);
            
            if (count > 0 && count <= 100) { // SAFE: Explicit bound check
                for (int i = 0; i < count; i++) { // FALSE POSITIVE (BAD) - Bounded
                    console.printf("Item %d%n", i);
                }
            }
        }
    }
    
    // BAD: Args with constant bound - SAFE because hardcoded limit
    public static void badArgsConstantBound(String[] args) {
        final int MAX_ITERATIONS = 100;
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]);
            
            for (int i = 0; i < count && i < MAX_ITERATIONS; i++) { // FALSE POSITIVE (BAD)
                System.out.println("Item " + i);
            }
        }
    }
    
    // ========== TRUE POSITIVES (GOOD) - SHOULD be flagged after fix ==========
    
    // GOOD: Args without bounds - VULNERABLE (can be 2 billion iterations)
    public static void goodArgsUnbounded(String[] args) {
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]); // NOT SAFE: No upper bound!
            
            for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Unbounded DoS
                System.out.println("Item " + i);
            }
        }
    }
    
    // GOOD: Scanner without bounds - VULNERABLE
    public static void goodScannerUnbounded() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter count: ");
        int count = scanner.nextInt(); // NOT SAFE: No upper bound!
        
        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Unbounded
            System.out.println("Item " + i);
        }
        scanner.close();
    }
    
    // GOOD: Console without bounds - VULNERABLE
    public static void goodConsoleUnbounded() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter count: ");
            int count = Integer.parseInt(input); // NOT SAFE: No upper bound!
            
            for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Unbounded
                console.printf("Item %d%n", i);
            }
        }
    }
    
    // GOOD: BufferedReader without bounds - VULNERABLE
    public static void goodBufferedReaderUnbounded() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter count: ");
        String input = reader.readLine();
        int count = Integer.parseInt(input); // NOT SAFE: No upper bound!
        
        for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Unbounded
            System.out.println("Item " + i);
        }
    }
    
    // GOOD: Args with multiplication - VULNERABLE (can overflow)
    public static void goodArgsMultiplication(String[] args) {
        if (args.length > 0) {
            int base = Integer.parseInt(args[0]);
            int count = base * 2; // VULNERABLE: Can be huge or overflow!
            
            for (int i = 0; i < count; i++) { // TRUE POSITIVE (GOOD) - Multiplication
                System.out.println("Item " + i);
            }
        }
    }
    
    // GOOD: Nested loops from args - EXPONENTIAL vulnerability
    public static void goodArgsNestedLoops(String[] args) {
        if (args.length >= 2) {
            int outer = Integer.parseInt(args[0]);
            int inner = Integer.parseInt(args[1]);
            
            // If outer=1000 and inner=1000, this is 1,000,000 iterations!
            for (int i = 0; i < outer; i++) { // TRUE POSITIVE (GOOD)
                for (int j = 0; j < inner; j++) { // TRUE POSITIVE (GOOD)
                    System.out.println("Item " + i + "," + j);
                }
            }
        }
    }
    
    // MIXED: Bounded (BAD/FP) + Unbounded (GOOD/TP)
    public static void mixedBoundedAndUnbounded(String[] args) {
        if (args.length >= 2) {
            // BAD/FP: Bounded
            int safe = Integer.parseInt(args[0]);
            int bounded = Math.min(safe, 50);
            for (int i = 0; i < bounded; i++) { // FALSE POSITIVE (BAD)
                System.out.println("Safe " + i);
            }
            
            // GOOD/TP: Unbounded
            int unsafe = Integer.parseInt(args[1]);
            for (int i = 0; i < unsafe; i++) { // TRUE POSITIVE (GOOD)
                System.out.println("Unsafe " + i);
            }
        }
    }
}

