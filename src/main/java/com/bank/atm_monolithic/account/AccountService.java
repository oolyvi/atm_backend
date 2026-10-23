package com.bank.atm_monolithic.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(AccountDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AccountDto> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(AccountDto::fromEntity);
    }

    @Transactional
    public AccountDto addAccount(Account account) {
        Account savedAccount = accountRepository.save(account);
        return AccountDto.fromEntity(savedAccount);
    }

    @Transactional
    public AccountDto replaceAccount(Long id, Account newAccount) {
        Account savedAccount = accountRepository.findById(id)
                .map(account -> {
                    account.setAccountNumber(newAccount.getAccountNumber());
                    account.setBalance(newAccount.getBalance());
                    account.setAccountType(newAccount.getAccountType());
                    account.setCustomer(newAccount.getCustomer());

                    return accountRepository.save(account);
                })
                .orElseGet(() -> {
                    newAccount.setId(null);
                    return accountRepository.save(newAccount);
                });

        return AccountDto.fromEntity(savedAccount);
    }

    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    @Transactional
    public void manualUnblockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void manualBlockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        account.setStatus(Account.AccountStatus.BLOCKED);
        accountRepository.save(account);
    }
}