package com.app.repository;

import com.app.model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {
    
    private Connection connection;
    
    public AccountRepository(Connection connection) {
        this.connection = connection;
    }
    
    public Optional<Account> findById(long id) throws SQLException {
        String sql = "SELECT account_id, account_number, ssn, password, tier, verified FROM accounts WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Account a = new Account();
                a.setAccountId(rs.getLong("account_id"));
                a.setAccountNumber(rs.getString("account_number"));
                a.setSsn(rs.getString("ssn"));
                a.setPassword(rs.getString("password"));
                a.setTier(rs.getInt("tier"));
                a.setVerified(rs.getBoolean("verified"));
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }
    
    public List<Account> findAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, account_number, ssn, password, tier, verified FROM accounts";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Account a = new Account();
                a.setAccountId(rs.getLong("account_id"));
                a.setAccountNumber(rs.getString("account_number"));
                a.setSsn(rs.getString("ssn"));
                a.setPassword(rs.getString("password"));
                a.setTier(rs.getInt("tier"));
                a.setVerified(rs.getBoolean("verified"));
                accounts.add(a);
            }
        }
        return accounts;
    }
}

