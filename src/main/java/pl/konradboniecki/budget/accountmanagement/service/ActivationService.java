package pl.konradboniecki.budget.accountmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import pl.konradboniecki.budget.accountmanagement.exceptions.AccountNotFoundException;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ActivationCode;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ActivationService {

    private AccountService accountService;
    private ActivationCodeService activationCodeService;
    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;

    @Autowired
    public ActivationService(AccountService accountService, ActivationCodeService activationCodeService) {
        this.accountService = accountService;
        this.activationCodeService = activationCodeService;
    }

    public RedirectView activateAccount(Long id, String activationCodeFromUrl) {
        Optional<Account> acc = accountService.findById(id);
        if (acc.isEmpty()) {
            return new RedirectView(BASE_URL + "/register");
        }
        if (acc.get().isEnabled()) {
            return new RedirectView(BASE_URL + "/login");
        }

        Optional<ActivationCode> activationCode =
                activationCodeService.findByAccountId(acc.get().getId());

        if (activationCodeIsPresentAndMatchesWithUrl(activationCode, activationCodeFromUrl)) {
            accountService.activateAccountWith(id);
            log.info("User with ID: " + acc.get().getId() + " has been activated");
            activationCodeService.deleteById(activationCode.get().getId());
            return new RedirectView(BASE_URL + "/login");
        }
        //TODO: redirect to error service in future
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "invalid activation link");
    }

    public ActivationCode createActivationCodeForAccountWithId(Long accountId) {
        if (accountService.findById(accountId).isEmpty()) {
            throw new AccountNotFoundException("Account not found. id: " + accountId);
        }
        //TODO: override existing activationCode
        // and remove cleanup service
        return activationCodeService.save(new ActivationCode()
                .setAccountId(accountId)
                .setActivationCode(UUID.randomUUID().toString()));
    }

    private boolean activationCodeIsPresentAndMatchesWithUrl(Optional<ActivationCode> activationCode, String activationCodeFromUrl) {
        return activationCode.isPresent()
                && activationCode.get().getActivationCode().equals(activationCodeFromUrl);
    }
}
