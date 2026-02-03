package com.checkmarx.handlers.console;

import java.io.*;
import java.util.Scanner;

public class CommandProcessor {
    
    // LC-MR:01
    public static void scenario01(String[] args) {
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]);
            int bounded = Math.min(count, 100);
            for (int i = 0; i < bounded; i++) {
                System.out.println("Item " + i);
            }
        }
    }
    
    // LC-MR:02
    public static void scenario02() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter count: ");
        int count = scanner.nextInt();
        int bounded = (count > 50) ? 50 : count;
        for (int i = 0; i < bounded; i++) {
            System.out.println("Item " + i);
        }
        scanner.close();
    }
    
    // LC-MR:03
    public static void scenario03() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter count (max 100): ");
            int count = Integer.parseInt(input);
            if (count > 0 && count <= 100) {
                for (int i = 0; i < count; i++) {
                    console.printf("Item %d%n", i);
                }
            }
        }
    }
    
    // LC-MR:04
    public static void scenario04(String[] args) {
        final int MAX_ITERATIONS = 100;
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]);
            for (int i = 0; i < count && i < MAX_ITERATIONS; i++) {
                System.out.println("Item " + i);
            }
        }
    }
    
    // LC-BR:05
    public static void scenario05(String[] args) {
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]);
            for (int i = 0; i < count; i++) {
                System.out.println("Item " + i);
            }
        }
    }
    
    // LC-BR:06
    public static void scenario06() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter count: ");
        int count = scanner.nextInt();
        for (int i = 0; i < count; i++) {
            System.out.println("Item " + i);
        }
        scanner.close();
    }
    
    // LC-BR:07
    public static void scenario07() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter count: ");
            int count = Integer.parseInt(input);
            for (int i = 0; i < count; i++) {
                console.printf("Item %d%n", i);
            }
        }
    }
    
    // LC-BR:08
    public static void scenario08() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter count: ");
        String input = reader.readLine();
        int count = Integer.parseInt(input);
        for (int i = 0; i < count; i++) {
            System.out.println("Item " + i);
        }
    }
    
    // LC-BR:09
    public static void scenario09(String[] args) {
        if (args.length > 0) {
            int base = Integer.parseInt(args[0]);
            int count = base * 2;
            for (int i = 0; i < count; i++) {
                System.out.println("Item " + i);
            }
        }
    }
    
    // LC-BR:10
    public static void scenario10(String[] args) {
        if (args.length >= 2) {
            int outer = Integer.parseInt(args[0]);
            int inner = Integer.parseInt(args[1]);
            for (int i = 0; i < outer; i++) {
                for (int j = 0; j < inner; j++) {
                    System.out.println("Item " + i + "," + j);
                }
            }
        }
    }
    
    // LC-BR:11
    public static void scenario11(String[] args) {
        if (args.length >= 2) {
            int value1 = Integer.parseInt(args[0]);
            int bounded = Math.min(value1, 50);
            for (int i = 0; i < bounded; i++) {
                System.out.println("Bounded " + i);
            }
            int value2 = Integer.parseInt(args[1]);
            for (int i = 0; i < value2; i++) {
                System.out.println("Unbounded " + i);
            }
        }
    }
}

