package com.app.controller;

import com.app.service.AccountService;
import com.app.util.Sanitizer;
import javax.servlet.http.*;
import java.io.*;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Privacy Violation False Positive Scenarios
 * All scenarios are SAFE but CxQL incorrectly flags them
 * Pattern: PII data -> Transformation -> Output (not exposing actual PII)
 */
public class DataController extends HttpServlet {

    private AccountService accountService;

    // #P01 - Masked SSN (CxQL flags but data is masked)
    public void logMaskedSsn(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        String masked = Sanitizer.mask(ssn, 4);  // Only last 4 visible
        System.out.println("SSN: " + masked);
    }

    // #P02 - Masked account number (CxQL flags but data is masked)
    public void logMaskedAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String accountNum = accountService.getAccountNumber(id);
        String masked = Sanitizer.mask(accountNum, 4);
        System.out.println("Account: " + masked);
    }

    // #P03 - SSN checksum only (CxQL flags but only checksum exposed)
    public void logSsnChecksum(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        int checksum = calculateLuhnChecksum(ssn.replaceAll("-", ""));
        System.out.println("Checksum: " + checksum);
    }

    // #P04 - SSN hashed (CxQL flags but value is hashed, not original)
    public void logHashedSsn(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        String hashed = hashSha256(ssn);
        System.out.println("SSN hash: " + hashed);
    }

    // #P05 - SSN existence check (CxQL flags but only boolean exposed)
    public void logSsnExists(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        boolean exists = ssn != null && !ssn.isEmpty();
        System.out.println("SSN present: " + exists);
    }

    // #P06 - SSN length (CxQL flags but only length exposed)
    public void logSsnLength(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        int length = ssn != null ? ssn.length() : 0;
        System.out.println("SSN length: " + length);
    }

    // #P07 - SSN format validation (CxQL flags but only boolean exposed)
    public void logSsnValid(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        boolean valid = ssn != null && ssn.matches("^\\d{3}-\\d{2}-\\d{4}$");
        System.out.println("SSN format valid: " + valid);
    }

    // #P08 - Password strength (CxQL flags but only score exposed)
    public void logPasswordStrength(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String password = accountService.getPassword(id);
        int strength = calculatePasswordStrength(password);
        System.out.println("Password strength: " + strength);
    }

    // #P09 - Password hashed (CxQL flags but value is hashed)
    public void logHashedPassword(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String password = accountService.getPassword(id);
        String hashed = hashSha256(password);
        System.out.println("Password hash: " + hashed);
    }

    // #P10 - Credit card type (CxQL flags but only type exposed)
    public void logCardType(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String cardNumber = accountService.getCreditCardNumber(id);
        String type = detectCardType(cardNumber);
        System.out.println("Card type: " + type);
    }

    // #P11 - Credit card masked (CxQL flags but data is masked)
    public void logMaskedCard(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String cardNumber = accountService.getCreditCardNumber(id);
        String masked = Sanitizer.mask(cardNumber, 4);
        System.out.println("Card: " + masked);
    }

    // #P12 - SSN state code (CxQL flags but only first 3 digits exposed)
    public void logSsnStateCode(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        String areaCode = ssn != null && ssn.length() >= 3 ? ssn.substring(0, 3) : "000";
        System.out.println("Area code: " + areaCode);
    }

    // #P13 - Email domain only (CxQL flags but only domain exposed)
    public void logEmailDomain(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String email = accountService.getEmail(id);
        String domain = email != null && email.contains("@") ? email.split("@")[1] : "";
        System.out.println("Domain: " + domain);
    }

    // #P14 - Phone country code (CxQL flags but only prefix exposed)
    public void logPhoneCountry(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String phone = accountService.getPhone(id);
        String countryCode = phone != null && phone.startsWith("+") ? phone.substring(0, 3) : "+1";
        System.out.println("Country: " + countryCode);
    }

    // #P15 - Name initials only (CxQL flags but only initials exposed)
    public void logNameInitials(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String name = accountService.getFullName(id);
        String[] parts = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) initials.append(part.charAt(0));
        }
        System.out.println("Initials: " + initials.toString().toUpperCase());
    }

    // Helper methods
    private int calculateLuhnChecksum(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            sum += digit;
        }
        return sum % 10;
    }

    private String hashSha256(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    private int calculatePasswordStrength(String password) {
        if (password == null) return 0;
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*].*")) score++;
        return score;
    }

    private String detectCardType(String cardNumber) {
        if (cardNumber == null) return "unknown";
        if (cardNumber.startsWith("4")) return "visa";
        if (cardNumber.startsWith("5")) return "mastercard";
        if (cardNumber.startsWith("3")) return "amex";
        return "other";
    }
}

