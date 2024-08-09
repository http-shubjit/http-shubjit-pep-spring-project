package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> register(Account account) {
        if (account.getUsername() == null || account.getUsername().isEmpty() || account.getPassword().length() < 4) {
            return Optional.empty();
        }
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists");
        }
        Account savedAccount = accountRepository.save(account);
        return Optional.of(savedAccount);
    }

    public Optional<Account> login(String username, String password) {
        return accountRepository.findByUsernameAndPassword(username, password);
    }

    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }
}
