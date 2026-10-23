package com.bank.atm_monolithic.account;

import com.bank.atm_monolithic.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account sampleAccount;
    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);

        sampleAccount = new Account();
        sampleAccount.setId(1L);
        sampleAccount.setAccountNumber("AZ123456789012345678");
        sampleAccount.setBalance(new BigDecimal("500.00"));
        sampleAccount.setDailyLimit(new BigDecimal("1000.00"));
        sampleAccount.setAccountType(Account.AccountType.CHECKING);
        sampleAccount.setStatus(Account.AccountStatus.ACTIVE);
        sampleAccount.setCustomer(sampleCustomer);
    }

    @Test
    void getAccounts_ShouldReturnListOfAccountDtos() {
        given(accountRepository.findAll()).willReturn(List.of(sampleAccount));

        List<AccountDto> result = accountService.getAccounts();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().accountNumber()).isEqualTo("AZ123456789012345678");
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccountDto() {
        given(accountRepository.findById(1L)).willReturn(Optional.of(sampleAccount));

        Optional<AccountDto> result = accountService.getAccountById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountById_WhenAccountDoesNotExist_ShouldReturnEmpty() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        Optional<AccountDto> result = accountService.getAccountById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void addAccount_ShouldSaveAndReturnAccountDto() {
        given(accountRepository.save(any(Account.class))).willReturn(sampleAccount);

        AccountDto result = accountService.addAccount(sampleAccount);

        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("AZ123456789012345678");
        verify(accountRepository, times(1)).save(sampleAccount);
    }

    @Test
    void replaceAccount_WhenAccountExists_ShouldUpdateAndReturnAccountDto() {
        Account updatedInfo = new Account();
        updatedInfo.setAccountNumber("AZ999999999999999999");
        updatedInfo.setBalance(new BigDecimal("750.00"));
        updatedInfo.setAccountType(Account.AccountType.SAVINGS);
        updatedInfo.setCustomer(sampleCustomer);

        given(accountRepository.findById(1L)).willReturn(Optional.of(sampleAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));

        AccountDto result = accountService.replaceAccount(1L, updatedInfo);

        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("AZ999999999999999999");
        assertThat(result.balance()).isEqualByComparingTo("750.00");
        assertThat(result.accountType()).isEqualTo(Account.AccountType.SAVINGS);
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void replaceAccount_WhenAccountDoesNotExist_ShouldCreateNew() {
        Account newAccount = new Account();
        newAccount.setAccountNumber("AZ111111111111111111");

        given(accountRepository.findById(99L)).willReturn(Optional.empty());
        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(2L);
            return acc;
        });

        AccountDto result = accountService.replaceAccount(99L, newAccount);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.accountNumber()).isEqualTo("AZ111111111111111111");
        verify(accountRepository, times(1)).findById(99L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void deleteAccount_ShouldCallRepositoryDelete() {
        accountService.deleteAccount(1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void manualUnblockAccount_WhenAccountExists_ShouldSetActiveStatus() {
        sampleAccount.setStatus(Account.AccountStatus.BLOCKED);
        given(accountRepository.findById(1L)).willReturn(Optional.of(sampleAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));

        accountService.manualUnblockAccount(1L);

        assertThat(sampleAccount.getStatus()).isEqualTo(Account.AccountStatus.ACTIVE);
        verify(accountRepository, times(1)).save(sampleAccount);
    }

    @Test
    void manualUnblockAccount_WhenAccountNotFound_ShouldThrowException() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.manualUnblockAccount(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account not found");
    }

    @Test
    void manualBlockAccount_WhenAccountExists_ShouldSetBlockedStatus() {
        given(accountRepository.findById(1L)).willReturn(Optional.of(sampleAccount));
        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));

        accountService.manualBlockAccount(1L);

        assertThat(sampleAccount.getStatus()).isEqualTo(Account.AccountStatus.BLOCKED);
        verify(accountRepository, times(1)).save(sampleAccount);
    }

    @Test
    void manualBlockAccount_WhenAccountNotFound_ShouldThrowException() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.manualBlockAccount(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account not found");
    }
}