package com.app.model;

public class Account {
    private long accountId;
    private String accountNumber;
    private String ssn;
    private String password;
    private int tier;
    private boolean verified;
    private String creditCardNumber;
    private String email;
    private String phone;
    private String fullName;
    private String accountName;

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getTier() { return tier; }
    public void setTier(int tier) { this.tier = tier; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public String getCreditCardNumber() { return creditCardNumber; }
    public void setCreditCardNumber(String creditCardNumber) { this.creditCardNumber = creditCardNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getMaskedSsn() {
        if (ssn == null || ssn.length() < 4) return "***-**-****";
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }

    public String getMaskedAccount() {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    public int getSsnChecksum() {
        if (ssn == null) return 0;
        int sum = 0;
        for (char c : ssn.toCharArray()) {
            if (Character.isDigit(c)) sum += Character.getNumericValue(c);
        }
        return sum % 10;
    }
}

