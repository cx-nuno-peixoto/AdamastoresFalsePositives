package com.checkmarx.falsepositive.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Command-Line Application - Demonstrates false positives in CLI context
 * Processes user input from console with various sanitization patterns
 */
public class DataProcessor {
    
    // Mock entity
    public static class Record {
        private Long id;
        private Integer count;
        
        public Record(Long id, Integer count) {
            this.id = id;
            this.count = count;
        }
        
        public Long getId() { return id; }
        public Integer getCount() { return count; }
    }
    
    public static void main(String[] args) {
        DataProcessor processor = new DataProcessor();
        processor.run();
    }
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        // FALSE POSITIVE: Type conversion (Reflected XSS equivalent)
        System.out.print("Enter record ID: ");
        String idInput = scanner.nextLine();
        long recordId = Long.parseLong(idInput); // SAFE: Numeric conversion
        System.out.println("Processing record: " + recordId); // SAFE: recordId is long
        
        // FALSE POSITIVE: Loop with type conversion (Loop Condition)
        System.out.print("Enter number of iterations: ");
        String iterInput = scanner.nextLine();
        int iterations = Integer.parseInt(iterInput); // SAFE: Numeric conversion
        int bounded = Math.min(iterations, 100); // SAFE: Bounded
        
        for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated int
            System.out.println("Iteration: " + i);
        }
        
        // FALSE POSITIVE: Numeric getter from collection (Stored XSS equivalent)
        List<Record> records = getRecordsFromFile();
        if (!records.isEmpty()) {
            Long firstId = records.get(0).getId(); // SAFE: getId() returns Long
            System.out.println("First record ID: " + firstId); // SAFE: Numeric
        }
        
        // FALSE POSITIVE: Metadata (Privacy Violation)
        String passwordMinLength = "8"; // SAFE: Configuration, not actual password
        System.out.println("Password minimum length: " + passwordMinLength); // FLAGGED but SAFE
        
        scanner.close();
    }
    
    private List<Record> getRecordsFromFile() {
        // Mock file reading
        List<Record> records = new ArrayList<>();
        records.add(new Record(1001L, 50));
        records.add(new Record(1002L, 75));
        return records;
    }
}

