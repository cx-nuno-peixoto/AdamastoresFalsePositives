package com.app.service;

import com.app.model.Account;
import com.app.repository.AccountRepository;
import com.app.util.Sanitizer;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountService {
    
    private AccountRepository repository;
    
    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }
    
    public long getAccountId(long id) throws SQLException {
        Optional<Account> account = repository.findById(id);
        return account.map(Account::getAccountId).orElse(0L);
    }
    
    public int getAccountTier(long id) throws SQLException {
        Optional<Account> account = repository.findById(id);
        return account.map(Account::getTier).orElse(0);
    }
    
    public boolean isAccountVerified(long id) throws SQLException {
        Optional<Account> account = repository.findById(id);
        return account.map(Account::isVerified).orElse(false);
    }
    
    public String getMaskedSsn(long id) throws SQLException {
        Optional<Account> account = repository.findById(id);
        return account.map(Account::getMaskedSsn).orElse("***-**-****");
    }
    
    public String getMaskedAccountNumber(long id) throws SQLException {
        Optional<Account> account = repository.findById(id);
        return account.map(Account::getMaskedAccount).orElse("****");
    }
    
    public int getSsnChecksum(long id) throws SQLException {
        Optional<Account> account = repository.findById(id);
        return account.map(Account::getSsnChecksum).orElse(0);
    }
    
    public List<Long> getAllAccountIds() throws SQLException {
        return repository.findAll().stream()
            .map(Account::getAccountId)
            .collect(Collectors.toList());
    }
    
    public List<Integer> getAllAccountTiers() throws SQLException {
        return repository.findAll().stream()
            .map(Account::getTier)
            .collect(Collectors.toList());
    }
    
    public List<String> getAllMaskedSsns() throws SQLException {
        return repository.findAll().stream()
            .map(Account::getMaskedSsn)
            .collect(Collectors.toList());
    }
}

