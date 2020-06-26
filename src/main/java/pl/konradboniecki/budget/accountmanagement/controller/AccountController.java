package pl.konradboniecki.budget.accountmanagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.CredentialCheck;
import pl.konradboniecki.budget.accountmanagement.service.AccountService;

@Slf4j
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{idOrEmail}")
    public ResponseEntity<Account> findAccount(
            @PathVariable("idOrEmail") String id,
            @RequestParam(name = "findBy", required = false, defaultValue = "id") String findBy) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountService.findAccount(id, findBy));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account accountToCreate) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(accountService.createAccountAndProjectAccess(accountToCreate));
    }

    @GetMapping("/credentials")
    public ResponseEntity<Void> checkCredentials(
            @RequestHeader("accountId") Long accountId,
            @RequestHeader("password") String password) {

        accountService.checkCredentialsForAccountWithId(new CredentialCheck(accountId, password));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}/family/{familyId}")
    public ResponseEntity<Account> assignFamily(
            @PathVariable("accountId") Long accountId,
            @PathVariable("familyId") Long familyId) {

        accountService.assignAccountToFamily(accountId, familyId);
        return ResponseEntity.ok().build();
    }
}
