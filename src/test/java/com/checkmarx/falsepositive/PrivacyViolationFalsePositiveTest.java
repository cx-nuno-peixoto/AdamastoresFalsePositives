package com.checkmarx.falsepositive;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * FALSE POSITIVE SCENARIOS: Test Files - Privacy Violation Tests
 * 
 * Pattern: Test files with @Test annotations and mock PII data
 * 
 * These scenarios should NOT be flagged because they are test files with mock data,
 * not production code exposing real PII to unauthorized actors.
 */
public class PrivacyViolationFalsePositiveTest {
    
    // Mock PII test data
    private static final String MOCK_PASSWORD = "TestPassword123";
    private static final String MOCK_SSN = "123-45-6789";
    private static final String MOCK_CREDIT_CARD = "4111-1111-1111-1111";
    private static final String MOCK_ACCOUNT_NUMBER = "ACC123456789";
    private static final String MOCK_PHONE = "555-123-4567";
    private static final String MOCK_EMAIL = "test@example.com";
    
    // Scenario 1: Test with password mock data
    @Test
    public void testPasswordValidation() {
        String password = MOCK_PASSWORD; // SAFE: Mock test data
        System.out.println("Testing with password: " + password); // SAFE: Test output
        assertTrue(password.length() > 8);
    }
    
    // Scenario 2: Test with SSN mock data
    @Test
    public void testSSNFormat() {
        String ssn = MOCK_SSN; // SAFE: Mock test data
        System.out.println("Testing SSN format: " + ssn); // SAFE: Test output
        assertTrue(ssn.matches("\\d{3}-\\d{2}-\\d{4}"));
    }
    
    // Scenario 3: Test with credit card mock data
    @Test
    public void testCreditCardValidation() {
        String creditCard = MOCK_CREDIT_CARD; // SAFE: Mock test data
        System.out.println("Testing credit card: " + creditCard); // SAFE: Test output
        assertTrue(creditCard.contains("4111"));
    }
    
    // Scenario 4: Test with account number mock data
    @Test
    public void testAccountNumber() {
        String accountNumber = MOCK_ACCOUNT_NUMBER; // SAFE: Mock test data
        System.out.println("Testing account: " + accountNumber); // SAFE: Test output
        assertTrue(accountNumber.startsWith("ACC"));
    }
    
    // Scenario 5: Test with phone mock data
    @Test
    public void testPhoneFormat() {
        String phone = MOCK_PHONE; // SAFE: Mock test data
        System.out.println("Testing phone: " + phone); // SAFE: Test output
        assertTrue(phone.matches("\\d{3}-\\d{3}-\\d{4}"));
    }
    
    // Scenario 6: Test with email mock data
    @Test
    public void testEmailValidation() {
        String email = MOCK_EMAIL; // SAFE: Mock test data
        System.out.println("Testing email: " + email); // SAFE: Test output
        assertTrue(email.contains("@"));
    }
    
    // Scenario 7: Test StringBuilder with PII
    @Test
    public void testStringBuilderWithPII() {
        String password = MOCK_PASSWORD; // SAFE: Mock test data
        StringBuilder sb = new StringBuilder();
        sb.append("Password: ");
        sb.append(password); // SAFE: Test StringBuilder operation
        
        System.out.println("StringBuilder result: " + sb.toString()); // SAFE: Test output
        assertTrue(sb.toString().contains("Password"));
    }
    
    // Scenario 8: Test with credentials mock data
    @Test
    public void testCredentials() {
        String credentials = "user:password"; // SAFE: Mock test data
        System.out.println("Testing credentials format: " + credentials); // SAFE: Test output
        assertTrue(credentials.contains(":"));
    }
    
    // Scenario 9: Test with secret mock data
    @Test
    public void testSecretKey() {
        String secret = "test-secret-key-12345"; // SAFE: Mock test data
        System.out.println("Testing secret: " + secret); // SAFE: Test output
        assertTrue(secret.length() > 10);
    }
    
    // Scenario 10: Test with auth token mock data
    @Test
    public void testAuthToken() {
        String authToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"; // SAFE: Mock test data
        System.out.println("Testing auth token: " + authToken); // SAFE: Test output
        assertTrue(authToken.startsWith("Bearer"));
    }
    
    // Scenario 11: Test with DOB mock data
    @Test
    public void testDOBFormat() {
        String DOB = "01/01/1990"; // SAFE: Mock test data
        System.out.println("Testing DOB: " + DOB); // SAFE: Test output
        assertTrue(DOB.matches("\\d{2}/\\d{2}/\\d{4}"));
    }
    
    // Scenario 12: Test with passport mock data
    @Test
    public void testPassportFormat() {
        String passport = "AB1234567"; // SAFE: Mock test data
        System.out.println("Testing passport: " + passport); // SAFE: Test output
        assertTrue(passport.matches("[A-Z]{2}\\d{7}"));
    }
    
    // Scenario 13: Test with social security mock data
    @Test
    public void testSocialSecurity() {
        String socialSecurity = "123-45-6789"; // SAFE: Mock test data
        System.out.println("Testing social security: " + socialSecurity); // SAFE: Test output
        assertEquals(11, socialSecurity.length());
    }
    
    // Scenario 14: Test with multiple PII fields
    @Test
    public void testMultiplePIIFields() {
        String password = MOCK_PASSWORD; // SAFE: Mock test data
        String ssn = MOCK_SSN; // SAFE: Mock test data
        String phone = MOCK_PHONE; // SAFE: Mock test data
        
        System.out.println("Password: " + password); // SAFE: Test output
        System.out.println("SSN: " + ssn); // SAFE: Test output
        System.out.println("Phone: " + phone); // SAFE: Test output
        
        assertNotNull(password);
        assertNotNull(ssn);
        assertNotNull(phone);
    }
    
    // Scenario 15: Test PII encryption
    @Test
    public void testPIIEncryption() {
        String password = MOCK_PASSWORD; // SAFE: Mock test data
        String encrypted = "encrypted_" + password; // SAFE: Mock encryption
        
        System.out.println("Original: " + password); // SAFE: Test output
        System.out.println("Encrypted: " + encrypted); // SAFE: Test output
        
        assertTrue(encrypted.startsWith("encrypted_"));
    }
}

