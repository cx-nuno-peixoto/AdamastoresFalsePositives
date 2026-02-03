package com.checkmarx.handlers.console;

import java.io.*;
import java.util.Scanner;

public class InputHandler {
    
    // RX-MR:01
    public static void scenario01(String[] args) {
        if (args.length > 0) {
            int count = Integer.parseInt(args[0]);
            System.out.println("Count: " + count);
        }
    }
    
    // RX-MR:02
    public static void scenario02(String[] args) {
        if (args.length > 0) {
            boolean flag = Boolean.parseBoolean(args[0]);
            System.out.println("Flag: " + flag);
        }
    }
    
    // RX-MR:03
    public static void scenario03() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        System.out.println("Age: " + age);
        scanner.close();
    }
    
    // RX-MR:04
    public static void scenario04() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter ID: ");
            long id = Long.parseLong(input);
            console.printf("ID: %d%n", id);
        }
    }
    
    // RX-BR:05
    public static void scenario05(String[] args) {
        if (args.length > 0) {
            String input = args[0];
            System.out.println("Input: " + input);
        }
    }
    
    // RX-BR:06
    public static void scenario06() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.println("Hello, " + name);
        scanner.close();
    }
    
    // RX-BR:07
    public static void scenario07() {
        Console console = System.console();
        if (console != null) {
            String input = console.readLine("Enter command: ");
            console.printf("Executing: %s%n", input);
        }
    }
    
    // RX-BR:08
    public static void scenario08() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter message: ");
        String message = reader.readLine();
        System.out.println("Message: " + message);
    }
    
    // RX-BR:09
    public static void scenario09(String[] args) {
        if (args.length >= 2) {
            String fullName = args[0] + " " + args[1];
            System.out.println("Full name: " + fullName);
        }
    }
    
    // RX-BR:10
    public static void scenario10(String[] args) throws IOException {
        if (args.length > 0) {
            String content = args[0];
            FileWriter writer = new FileWriter("output.html");
            writer.write("<html><body>" + content + "</body></html>");
            writer.close();
        }
    }
    
    // RX-BR:11
    public static void scenario11(String[] args) throws IOException {
        if (args.length > 0) {
            String data = args[0];
            PrintWriter pw = new PrintWriter(new FileWriter("log.txt"));
            pw.println("Data: " + data);
            pw.close();
        }
    }
    
    // RX-BR:12
    public static void scenario12(String[] args) {
        if (args.length >= 2) {
            int id = Integer.parseInt(args[0]);
            System.out.println("ID: " + id);
            String name = args[1];
            System.out.println("Name: " + name);
        }
    }
}

