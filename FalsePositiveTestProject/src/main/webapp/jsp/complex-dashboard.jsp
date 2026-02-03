<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>

<%!
    // Enums
    public enum UserRole {
        ADMIN, MANAGER, USER, GUEST
    }
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, LOCKED
    }
    
    // Mock entity
    public static class Account {
        private Long id;
        private Integer loginCount;
        private Double balance;
        private boolean verified;
        private UserRole role;
        private AccountStatus status;
        
        public Account(Long id, Integer loginCount, Double balance, boolean verified, UserRole role, AccountStatus status) {
            this.id = id;
            this.loginCount = loginCount;
            this.balance = balance;
            this.verified = verified;
            this.role = role;
            this.status = status;
        }
        
        public Long getId() { return id; }
        public Integer getLoginCount() { return loginCount; }
        public Double getBalance() { return balance; }
        public boolean isVerified() { return verified; }
        public UserRole getRole() { return role; }
        public AccountStatus getStatus() { return status; }
    }
    
    // Mock database
    private List<Account> getAccountsFromDatabase() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(2001L, 150, 5000.50, true, UserRole.ADMIN, AccountStatus.ACTIVE));
        accounts.add(new Account(2002L, 75, 1250.75, true, UserRole.USER, AccountStatus.ACTIVE));
        accounts.add(new Account(2003L, 5, 0.0, false, UserRole.GUEST, AccountStatus.INACTIVE));
        return accounts;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Complex Dashboard - False Positive Scenarios</title>
</head>
<body>
    <h1>Complex Dashboard - All False Positive Patterns</h1>
    
    <h2>Reflected XSS False Positives</h2>
    
    <%
        // FP: Type conversion
        String accountIdParam = request.getParameter("accountId");
        if (accountIdParam != null) {
            long accountId = Long.parseLong(accountIdParam); // SAFE: Numeric conversion
    %>
            <p>Account ID: <%= accountId %></p> <!-- SAFE: accountId is long -->
    <%
        }
        
        // FP: Number formatting
        String balanceParam = request.getParameter("balance");
        if (balanceParam != null) {
            double balance = Double.parseDouble(balanceParam); // SAFE: Numeric conversion
            DecimalFormat df = new DecimalFormat("$#,##0.00");
            String formattedBalance = df.format(balance); // SAFE: DecimalFormat sanitizes
    %>
            <p>Balance: <%= formattedBalance %></p> <!-- SAFE: Formatted numeric -->
    <%
        }
        
        // FP: Regex validation
        String accountCodeParam = request.getParameter("accountCode");
        if (accountCodeParam != null) {
            Pattern pattern = Pattern.compile("^ACC-[A-Z]{3}-[0-9]{4}$");
            Matcher matcher = pattern.matcher(accountCodeParam);
            if (matcher.matches()) { // SAFE: Only ACC-XXX-XXXX format passes
    %>
                <p>Account Code: <%= accountCodeParam %></p> <!-- SAFE: Validated -->
    <%
            }
        }
        
        // FP: Enum validation
        String roleParam = request.getParameter("role");
        if (roleParam != null) {
            try {
                UserRole role = UserRole.valueOf(roleParam.toUpperCase()); // SAFE: Enum validation
    %>
                <p>Role: <%= role.name() %></p> <!-- SAFE: Enum name -->
    <%
            } catch (IllegalArgumentException e) {
    %>
                <p>Invalid role</p>
    <%
            }
        }
        
        // FP: Ternary with math operators
        String limitParam = request.getParameter("limit");
        if (limitParam != null) {
            int limit = Integer.parseInt(limitParam); // SAFE: Numeric conversion
            int finalLimit = (limit > 50) ? 50 : limit; // SAFE: Ternary bounds limit
    %>
            <p>Display Limit (max 50): <%= finalLimit %></p> <!-- SAFE: Bounded -->
    <%
        }
    %>
    
    <h2>Stored XSS False Positives</h2>
    
    <%
        List<Account> accounts = getAccountsFromDatabase();
        
        if (!accounts.isEmpty()) {
            // FP: Numeric getter from collection
            Long firstAccountId = accounts.get(0).getId(); // SAFE: getId() returns Long
    %>
            <p>First Account ID: <%= firstAccountId %></p> <!-- SAFE: Numeric -->
    <%
            // FP: Boolean field from database
            boolean verified = accounts.get(0).isVerified(); // SAFE: Boolean from database
    %>
            <p>Verified: <%= verified %></p> <!-- SAFE: Boolean outputs true/false -->
    <%
            // FP: Enum from database
            UserRole userRole = accounts.get(0).getRole(); // SAFE: Enum from database
    %>
            <p>User Role: <%= userRole.name() %></p> <!-- SAFE: Enum name -->
    <%
            AccountStatus accountStatus = accounts.get(0).getStatus(); // SAFE: Enum from database
    %>
            <p>Account Status: <%= accountStatus.name() %></p> <!-- SAFE: Enum name -->
    <%
        }
    %>
    
    <h2>Loop Condition False Positives</h2>
    
    <%
        String rowCountParam = request.getParameter("rowCount");
        if (rowCountParam != null) {
            int rowCount = Integer.parseInt(rowCountParam); // SAFE: Numeric conversion
            int bounded = Math.min(rowCount, 25); // SAFE: Bounded to max 25
    %>
            <table border="1">
                <tr><th>Row</th><th>Data</th></tr>
    <%
            for (int i = 0; i < bounded; i++) { // SAFE: bounded is validated int
    %>
                <tr>
                    <td><%= (i + 1) %></td>
                    <td>Row <%= (i + 1) %> data</td>
                </tr>
    <%
            }
    %>
            </table>
    <%
        }
        
        // FP: Ternary in loop
        String batchParam = request.getParameter("batch");
        if (batchParam != null) {
            int batch = Integer.parseInt(batchParam); // SAFE: Numeric conversion
            int batchSize = (batch > 10) ? 10 : batch; // SAFE: Ternary bounds
    %>
            <ul>
    <%
            for (int i = 0; i < batchSize; i++) { // SAFE: batchSize is bounded
    %>
                <li>Batch Item <%= (i + 1) %></li>
    <%
            }
    %>
            </ul>
    <%
        }
    %>
    
    <h2>Privacy Violation False Positives</h2>
    
    <%
        // FP: Metadata variables
        String passwordMinLength = "8"; // SAFE: Configuration value
        String passwordMaxLength = "128"; // SAFE: Configuration value
        String accountNumberFormat = "ACC-XXX-XXXX"; // SAFE: Format pattern
        String phoneFormat = "(XXX) XXX-XXXX"; // SAFE: Format pattern
        String ssnFormat = "XXX-XX-XXXX"; // SAFE: Format pattern
    %>
    
    <p>Password Min Length: <%= passwordMinLength %></p> <!-- FLAGGED but SAFE -->
    <p>Password Max Length: <%= passwordMaxLength %></p> <!-- FLAGGED but SAFE -->
    <p>Account Number Format: <%= accountNumberFormat %></p> <!-- FLAGGED but SAFE -->
    <p>Phone Format: <%= phoneFormat %></p> <!-- FLAGGED but SAFE -->
    <p>SSN Format: <%= ssnFormat %></p> <!-- FLAGGED but SAFE -->
    
    <%
        // FP: Constants as keys
        final String PASSWORD = "Password";
        final String CREDIT_CARD = "CreditCard";
    %>
    
    <p>Field Name: <%= PASSWORD %></p> <!-- SAFE: Outputs "Password" -->
    <p>Field Name: <%= CREDIT_CARD %></p> <!-- SAFE: Outputs "CreditCard" -->
    
</body>
</html>

