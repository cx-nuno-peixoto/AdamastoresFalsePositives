package com.checkmarx.falsepositive.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * COMPLEX CLI APPLICATION - Report Generator
 * 
 * This command-line application demonstrates multiple false positive patterns
 * in a realistic report generation context, combining all vulnerability types.
 */
public class ComplexReportGenerator {
    
    public enum ReportType {
        DAILY, WEEKLY, MONTHLY, YEARLY
    }
    
    public enum ReportFormat {
        PDF, CSV, EXCEL, HTML
    }
    
    public static class Transaction {
        private Long id;
        private Double amount;
        private Integer count;
        private boolean processed;
        private ReportType type;
        
        public Transaction(Long id, Double amount, Integer count, boolean processed, ReportType type) {
            this.id = id;
            this.amount = amount;
            this.count = count;
            this.processed = processed;
            this.type = type;
        }
        
        public Long getId() { return id; }
        public Double getAmount() { return amount; }
        public Integer getCount() { return count; }
        public boolean isProcessed() { return processed; }
        public ReportType getType() { return type; }
    }
    
    public static void main(String[] args) {
        ComplexReportGenerator generator = new ComplexReportGenerator();
        generator.run();
    }
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Complex Report Generator ===\n");
        
        // === REFLECTED XSS FALSE POSITIVES (CLI equivalent) ===
        
        // FP: Type conversion
        System.out.print("Enter report ID: ");
        String reportIdInput = scanner.nextLine();
        long reportId = Long.parseLong(reportIdInput); // SAFE: Numeric conversion
        System.out.println("Generating report ID: " + reportId); // SAFE: reportId is long
        
        // FP: Number formatting
        System.out.print("Enter total amount: ");
        String amountInput = scanner.nextLine();
        double amount = Double.parseDouble(amountInput); // SAFE: Numeric conversion
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        String formattedAmount = df.format(amount); // SAFE: DecimalFormat sanitizes
        System.out.println("Total: " + formattedAmount); // SAFE: Formatted numeric
        
        // FP: Regex validation
        System.out.print("Enter report code (REP-XXXXXX): ");
        String codeInput = scanner.nextLine();
        Pattern pattern = Pattern.compile("^REP-[0-9]{6}$");
        Matcher matcher = pattern.matcher(codeInput);
        if (matcher.matches()) { // SAFE: Only REP-XXXXXX format passes
            System.out.println("Report Code: " + codeInput); // SAFE: Validated
        } else {
            System.out.println("Invalid report code format");
        }
        
        // FP: Enum validation
        System.out.print("Enter report type (DAILY/WEEKLY/MONTHLY/YEARLY): ");
        String typeInput = scanner.nextLine();
        try {
            ReportType type = ReportType.valueOf(typeInput.toUpperCase()); // SAFE: Enum validation
            System.out.println("Report Type: " + type.name()); // SAFE: Enum name
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid report type");
        }
        
        // FP: Ternary with math operators
        System.out.print("Enter page count: ");
        String pageInput = scanner.nextLine();
        int pageCount = Integer.parseInt(pageInput); // SAFE: Numeric conversion
        int finalPages = (pageCount > 100) ? 100 : pageCount; // SAFE: Ternary bounds pages
        System.out.println("Pages (max 100): " + finalPages); // SAFE: Bounded
        
        // === STORED XSS FALSE POSITIVES (File/Database equivalent) ===
        
        List<Transaction> transactions = loadTransactionsFromFile();
        
        if (!transactions.isEmpty()) {
            // FP: Numeric getter from collection
            Long firstId = transactions.get(0).getId(); // SAFE: getId() returns Long
            System.out.println("First Transaction ID: " + firstId); // SAFE: Numeric
            
            // FP: Boolean field
            boolean processed = transactions.get(0).isProcessed(); // SAFE: Boolean
            System.out.println("Processed: " + processed); // SAFE: Boolean outputs true/false
            
            // FP: Enum from data
            ReportType reportType = transactions.get(0).getType(); // SAFE: Enum
            System.out.println("Transaction Type: " + reportType.name()); // SAFE: Enum name
        }
        
        // === LOOP CONDITION FALSE POSITIVES ===
        
        System.out.print("Enter number of records to process: ");
        String recordInput = scanner.nextLine();
        int recordCount = Integer.parseInt(recordInput); // SAFE: Numeric conversion
        int bounded = Math.min(recordCount, 50); // SAFE: Bounded to max 50
        
        System.out.println("\nProcessing records:");
        for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated int
            System.out.println("  Record " + (i + 1) + " processed");
        }
        
        // FP: Ternary in loop
        System.out.print("\nEnter batch size: ");
        String batchInput = scanner.nextLine();
        int batchSize = Integer.parseInt(batchInput); // SAFE: Numeric conversion
        int batchLimit = (batchSize > 20) ? 20 : batchSize; // SAFE: Ternary bounds
        
        for (int i = 0; i < batchLimit; i++) { // SAFE: batchLimit is bounded
            System.out.println("  Batch " + (i + 1));
        }
        
        // === PRIVACY VIOLATION FALSE POSITIVES ===
        
        // FP: Metadata variables
        String accountNumberFormat = "ACC-XXXXXX"; // SAFE: Format pattern
        System.out.println("\nAccount Format: " + accountNumberFormat); // FLAGGED but SAFE
        
        String passwordMinLength = "8"; // SAFE: Configuration
        System.out.println("Password Min Length: " + passwordMinLength); // FLAGGED but SAFE
        
        String phoneFormat = "XXX-XXX-XXXX"; // SAFE: Format pattern
        System.out.println("Phone Format: " + phoneFormat); // FLAGGED but SAFE
        
        // FP: Constants as keys
        final String CREDIT_CARD = "CreditCard";
        System.out.println("Field Name: " + CREDIT_CARD); // SAFE: Outputs "CreditCard"
        
        System.out.println("\n=== Report Generation Complete ===");
        scanner.close();
    }
    
    private List<Transaction> loadTransactionsFromFile() {
        // Mock file loading
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(10001L, 1250.50, 5, true, ReportType.DAILY));
        transactions.add(new Transaction(10002L, 3500.75, 12, true, ReportType.WEEKLY));
        transactions.add(new Transaction(10003L, 890.25, 3, false, ReportType.MONTHLY));
        return transactions;
    }
}

