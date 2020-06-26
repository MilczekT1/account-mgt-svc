package pl.konradboniecki.budget.accountmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.konradboniecki.budget.accountmanagement.exceptions.AccountNotFoundException;
import pl.konradboniecki.budget.accountmanagement.exceptions.FamilyNotFoundException;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.CredentialCheck;
import pl.konradboniecki.budget.accountmanagement.model.ProjectAccess;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.Optional;

@Slf4j
@Service
public class AccountService {

    private AccountRepository accountRepository;
    private ProjectAccessService projectAccessService;
    private FamilyClient familyClient;

    @Autowired
    public AccountService(AccountRepository accountRepository, ProjectAccessService projectAccessService, FamilyClient familyClient) {
        this.accountRepository = accountRepository;
        this.projectAccessService = projectAccessService;
        this.familyClient = familyClient;
    }

    public void changePassword(String newPassword, Long accountId) {
        accountRepository.changePassword(newPassword, accountId);
    }

    public void activateAccountWith(Long id) {
        accountRepository.setEnabled(id);
    }

    public Account createAccountAndProjectAccess(Account newAccount) {
        Optional<Account> account = accountRepository.findByEmail(newAccount.getEmail());
        if (account.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            Account acc = accountRepository.save(newAccount);
            projectAccessService.saveProjectAccess(
                    new ProjectAccess().setAccountId(acc.getId())
            );
            return acc.setPassword(null);//TODO:projectAccessService.addProjectAccessDataToAccount(account.get());  to include access data just after creation?
        }
    }

    public Account findAccount(String idOrEmail, String findBy) {
        switch (findBy) {
            case "id":
                return findByIdAsStringFromParam(idOrEmail);
            case "email":
                return findByEmailFromParam(idOrEmail);
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid argument findBy=" + findBy + ", it should be \"id\" or \"email\"");
        }
    }

    public Account findByEmailFromParam(String email) {
        if (Account.isEmailValid(email.toLowerCase())) {
            Optional<Account> account = findByEmail(email);
            if (account.isPresent()) {
                return projectAccessService.addProjectAccessDataToAccount(account.get());
            } else {
                throw new AccountNotFoundException("Account with email: " + email + " not found.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");
        }
    }

    public Account findByIdAsStringFromParam(String id) {
        Optional<Long> idOpt = Optional.of(convertStringToLongOrThrow(id));
        Optional<Account> account = findById(idOpt.get());
        if (account.isPresent()) {
            return projectAccessService.addProjectAccessDataToAccount(account.get());
        } else {
            throw new AccountNotFoundException("Account with id: " + id + " not found.");
        }
    }

    public Boolean checkCredentialsForAccountWithId(CredentialCheck credentialCheck) {
        Optional<Account> accountOptional = accountRepository.findById(credentialCheck.getAccountId());
        if (accountOptional.isPresent()) {
            if (accountOptional.get().getPassword().equals(credentialCheck.getPassword())) {
                return true;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new AccountNotFoundException("Account with id: " + credentialCheck.getAccountId() + " not found during credentials check.");
        }
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email.toLowerCase());
    }

    private Long convertStringToLongOrThrow(String stringId) {
        try {
            return Long.valueOf(stringId);
        } catch (Exception e) {
            throw new BadRequestException("Failed conversion from String to Long.", e);
        }
    }

    public void assignAccountToFamily(Long accountId, Long familyId) {
        if (!findById(accountId).isPresent()) {
            throw new AccountNotFoundException("Account with id: " + accountId + " not found");
        }
        if (familyClient.isPresentById(familyId)) {
            projectAccessService.setFamilyId(familyId, accountId);
        } else {
            throw new FamilyNotFoundException("Family with id: " + familyId + " not found.");
        }
    }
}
