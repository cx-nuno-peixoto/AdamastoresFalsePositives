package com.app.controller;

import com.app.service.AccountService;
import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class DataController extends HttpServlet {
    
    private AccountService accountService;
    
    // #P01 - Masked SSN through service layer (privacy safe)
    public void displayMaskedSsn(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String masked = accountService.getMaskedSsn(id);
        System.out.println("SSN: " + masked);
    }
    
    // #P02 - Masked account number through service layer
    public void displayMaskedAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String masked = accountService.getMaskedAccountNumber(id);
        System.out.println("Account: " + masked);
    }
    
    // #P03 - SSN checksum (int) through service layer
    public void displaySsnChecksum(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        int checksum = accountService.getSsnChecksum(id);
        System.out.println("Checksum: " + checksum);
    }
    
    // #P04 - Account ID (long) through service layer
    public void displayAccountId(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        long accountId = accountService.getAccountId(id);
        System.out.println("Account ID: " + accountId);
    }
    
    // #P05 - Account tier (int) through service layer
    public void displayAccountTier(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        int tier = accountService.getAccountTier(id);
        System.out.println("Tier: " + tier);
    }
    
    // #P06 - Account verified (boolean) through service layer
    public void displayAccountVerified(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        boolean verified = accountService.isAccountVerified(id);
        System.out.println("Verified: " + verified);
    }
    
    // #P07 - All account IDs (List<Long>) through service layer
    public void displayAllAccountIds(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Long> ids = accountService.getAllAccountIds();
        for (Long accountId : ids) {
            System.out.println("ID: " + accountId);
        }
    }
    
    // #P08 - All account tiers (List<Integer>) through service layer
    public void displayAllAccountTiers(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<Integer> tiers = accountService.getAllAccountTiers();
        for (Integer tier : tiers) {
            System.out.println("Tier: " + tier);
        }
    }
    
    // #P09 - All masked SSNs through service layer
    public void displayAllMaskedSsns(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        List<String> maskedSsns = accountService.getAllMaskedSsns();
        for (String masked : maskedSsns) {
            System.out.println("SSN: " + masked);
        }
    }
    
    // #P10 - Constant field name with PII variable name
    public void displayFieldName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String password = "password";
        String ssn = "ssn";
        System.out.println("Required fields: " + password + ", " + ssn);
    }
    
    // #P11 - Format requirements (not actual PII)
    public void displayFormatRequirements(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ssnFormat = "XXX-XX-XXXX";
        String passwordFormat = "8+ chars, 1 uppercase, 1 number";
        System.out.println("SSN format: " + ssnFormat);
        System.out.println("Password requirements: " + passwordFormat);
    }
    
    // #P12 - Validation result message
    public void displayValidationResult(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String password = req.getParameter("password");
        boolean valid = password != null && password.length() >= 8;
        System.out.println("Password validation: " + (valid ? "passed" : "failed"));
    }
}

