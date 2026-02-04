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

    /*
     * #P01 - FALSE POSITIVE: Masked SSN output
     * WHY SAFE: Sanitizer.mask(ssn, 4) replaces all but last 4 chars with asterisks.
     *           SSN "123-45-6789" becomes "***-**-6789". Original SSN not exposed.
     *           Last 4 digits alone are not PII - commonly used for verification.
     * WHY CXQL FAILS: CxQL detects SSN variable name and flags any output.
     *                 It cannot determine that masking destroys the sensitive portion.
     *                 Pattern matching on "ssn" triggers false positive.
     * CXQL LIMITATION: Masking/redaction not recognized as PII protection.
     */
    public void logMaskedSsn(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        String masked = Sanitizer.mask(ssn, 4);  // Only last 4 visible
        System.out.println("SSN: " + masked);
    }

    /*
     * #P02 - FALSE POSITIVE: Masked account number output
     * WHY SAFE: mask(accountNum, 4) shows only last 4 digits.
     *           Account "1234567890123456" becomes "************3456".
     *           This is industry-standard practice for displaying payment info.
     * WHY CXQL FAILS: CxQL flags "accountNum" or "account" variable patterns.
     *                 It cannot recognize that masking protects the sensitive data.
     * CXQL LIMITATION: Custom masking methods not recognized as sanitizers.
     */
    public void logMaskedAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String accountNum = accountService.getAccountNumber(id);
        String masked = Sanitizer.mask(accountNum, 4);
        System.out.println("Account: " + masked);
    }

    /*
     * #P03 - FALSE POSITIVE: SSN checksum only
     * WHY SAFE: Checksum is a single digit (0-9) derived from all digits.
     *           Original SSN cannot be reconstructed from checksum.
     *           This is a one-way mathematical derivation.
     * WHY CXQL FAILS: CxQL sees SSN flowing into calculation and output.
     *                 It cannot determine that checksum destroys original information.
     * CXQL LIMITATION: Derived/computed values not analyzed for information loss.
     */
    public void logSsnChecksum(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        int checksum = calculateLuhnChecksum(ssn.replaceAll("-", ""));
        System.out.println("Checksum: " + checksum);
    }

    /*
     * #P04 - FALSE POSITIVE: Hashed SSN output
     * WHY SAFE: SHA-256 is a cryptographic one-way hash function.
     *           Original SSN cannot be recovered from hash output.
     *           This is standard practice for storing/comparing sensitive data.
     * WHY CXQL FAILS: CxQL sees SSN variable flowing to output (System.out).
     *                 It cannot determine that hash destroys original value.
     * CXQL LIMITATION: Cryptographic hashing not recognized as PII sanitizer.
     */
    public void logHashedSsn(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        String hashed = hashSha256(ssn);
        System.out.println("SSN hash: " + hashed);
    }

    /*
     * #P05 - FALSE POSITIVE: SSN existence check
     * WHY SAFE: Output is boolean (true/false) - "SSN present: true".
     *           No part of the actual SSN value is exposed.
     *           Boolean existence check reveals no sensitive information.
     * WHY CXQL FAILS: CxQL tracks SSN variable through the null check to output.
     *                 It cannot determine that only boolean result is output.
     * CXQL LIMITATION: Boolean derivation from PII not recognized as safe.
     */
    public void logSsnExists(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        boolean exists = ssn != null && !ssn.isEmpty();
        System.out.println("SSN present: " + exists);
    }

    /*
     * #P06 - FALSE POSITIVE: SSN length output
     * WHY SAFE: Output is just the length (integer 11 for "XXX-XX-XXXX").
     *           Length does not reveal SSN content - all SSNs have same format.
     *           This is public information about SSN format.
     * WHY CXQL FAILS: CxQL tracks SSN through .length() to output.
     *                 It cannot determine that length reveals no sensitive data.
     * CXQL LIMITATION: .length() on PII not recognized as safe derivation.
     */
    public void logSsnLength(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        int length = ssn != null ? ssn.length() : 0;
        System.out.println("SSN length: " + length);
    }

    /*
     * #P07 - FALSE POSITIVE: SSN format validation result
     * WHY SAFE: Output is boolean - "SSN format valid: true/false".
     *           No SSN content exposed, only whether format matches pattern.
     * WHY CXQL FAILS: CxQL sees SSN in validation expression flowing to output.
     *                 It cannot determine that regex match produces only boolean.
     * CXQL LIMITATION: Regex validation boolean result not recognized as safe.
     */
    public void logSsnValid(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        boolean valid = ssn != null && ssn.matches("^\\d{3}-\\d{2}-\\d{4}$");
        System.out.println("SSN format valid: " + valid);
    }

    /*
     * #P08 - FALSE POSITIVE: Password strength score
     * WHY SAFE: Output is numeric score (0-4) based on password characteristics.
     *           Actual password content not exposed - only complexity rating.
     *           Strength score cannot be used to reconstruct password.
     * WHY CXQL FAILS: CxQL sees password variable flowing to calculation and output.
     *                 It cannot determine that strength calculation destroys content.
     * CXQL LIMITATION: Strength/quality metrics not recognized as safe derivation.
     */
    public void logPasswordStrength(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String password = accountService.getPassword(id);
        int strength = calculatePasswordStrength(password);
        System.out.println("Password strength: " + strength);
    }

    /*
     * #P09 - FALSE POSITIVE: Hashed password output
     * WHY SAFE: SHA-256 cryptographic hash is one-way - password cannot be recovered.
     *           Output is 64-character hex string, not the actual password.
     *           This is standard practice for password verification logging.
     * WHY CXQL FAILS: CxQL tracks password variable through hash function to output.
     *                 It cannot determine that hashing destroys the original value.
     * CXQL LIMITATION: SHA-256/cryptographic hashing not recognized as PII sanitizer.
     */
    public void logHashedPassword(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String password = accountService.getPassword(id);
        String hashed = hashSha256(password);
        System.out.println("Password hash: " + hashed);
    }

    /*
     * #P10 - FALSE POSITIVE: Credit card type only
     * WHY SAFE: detectCardType() returns only "visa", "mastercard", "amex", or "other".
     *           Card number is analyzed but only card network type is output.
     *           Card type is public information (first digit determines type).
     * WHY CXQL FAILS: CxQL sees credit card variable flowing to output.
     *                 It cannot determine that only a derived category is output.
     * CXQL LIMITATION: Card type detection not recognized as safe derivation.
     */
    public void logCardType(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String cardNumber = accountService.getCreditCardNumber(id);
        String type = detectCardType(cardNumber);
        System.out.println("Card type: " + type);
    }

    /*
     * #P11 - FALSE POSITIVE: Masked credit card output
     * WHY SAFE: mask(cardNumber, 4) replaces all but last 4 digits with asterisks.
     *           "4111111111111111" becomes "************1111".
     *           Industry-standard practice for displaying card info (PCI DSS compliant).
     * WHY CXQL FAILS: CxQL flags credit card variable pattern in output.
     *                 It cannot recognize that masking protects the sensitive data.
     * CXQL LIMITATION: Masking not recognized as PCI-compliant sanitization.
     */
    public void logMaskedCard(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String cardNumber = accountService.getCreditCardNumber(id);
        String masked = Sanitizer.mask(cardNumber, 4);
        System.out.println("Card: " + masked);
    }

    /*
     * #P12 - FALSE POSITIVE: SSN area code only
     * WHY SAFE: First 3 digits of SSN indicate geographic area (historical).
     *           Area codes are not unique - millions share same area code.
     *           Cannot identify individual from 3-digit area code alone.
     * WHY CXQL FAILS: CxQL sees SSN variable with substring flowing to output.
     *                 It cannot determine that partial data is not identifying.
     * CXQL LIMITATION: Partial PII extraction not analyzed for identification risk.
     */
    public void logSsnStateCode(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String ssn = accountService.getSsn(id);
        String areaCode = ssn != null && ssn.length() >= 3 ? ssn.substring(0, 3) : "000";
        System.out.println("Area code: " + areaCode);
    }

    /*
     * #P13 - FALSE POSITIVE: Email domain only
     * WHY SAFE: Only domain part after @ is output (e.g., "gmail.com").
     *           Username part containing PII is discarded.
     *           Domain is public information, not personally identifying.
     * WHY CXQL FAILS: CxQL sees email variable with split flowing to output.
     *                 It cannot determine that split()[1] isolates non-PII portion.
     * CXQL LIMITATION: Email domain extraction not recognized as safe.
     */
    public void logEmailDomain(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String email = accountService.getEmail(id);
        String domain = email != null && email.contains("@") ? email.split("@")[1] : "";
        System.out.println("Domain: " + domain);
    }

    /*
     * #P14 - FALSE POSITIVE: Phone country code only
     * WHY SAFE: First 2-3 characters after + are country code (e.g., "+1" for US).
     *           Country codes are public information, shared by millions.
     *           Cannot identify individual from country code alone.
     * WHY CXQL FAILS: CxQL sees phone variable with substring flowing to output.
     *                 It cannot determine that country code is non-identifying.
     * CXQL LIMITATION: Country code extraction not recognized as safe.
     */
    public void logPhoneCountry(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        long id = Sanitizer.toLong(req.getParameter("id"));
        String phone = accountService.getPhone(id);
        String countryCode = phone != null && phone.startsWith("+") ? phone.substring(0, 3) : "+1";
        System.out.println("Country: " + countryCode);
    }

    /*
     * #P15 - FALSE POSITIVE: Name initials only
     * WHY SAFE: Only first character of each name part is output (e.g., "JD" for "John Doe").
     *           Initials are not uniquely identifying - millions share same initials.
     *           Full name destroyed, only abbreviation remains.
     * WHY CXQL FAILS: CxQL tracks name variable through charAt loop to output.
     *                 It cannot determine that initials are non-identifying.
     * CXQL LIMITATION: Initials extraction not recognized as safe transformation.
     */
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

