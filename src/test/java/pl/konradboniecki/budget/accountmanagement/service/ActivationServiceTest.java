package pl.konradboniecki.budget.accountmanagement.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import pl.konradboniecki.budget.accountmanagement.exceptions.AccountNotFoundException;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ActivationCode;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class ActivationServiceTest {

    @Autowired
    private ActivationService activationService;
    @MockBean
    private ActivationCodeService activationCodeService;
    @MockBean
    private AccountService accountService;

    private Long notExistingId;
    private Long idOfExistingAndEnabledAccount;
    private Long idOfExistingAndNotEnabledAccount;
    private String invalidActivationCode;
    private String validActivationCode;
    @Value("${budget.baseUrl.gateway}")
    private String siteBaseUrl;

    @BeforeAll
    private void setUp() {
        notExistingId = 1L;
        idOfExistingAndEnabledAccount = 2L;
        idOfExistingAndNotEnabledAccount = 3L;
        invalidActivationCode = "randomString";
        validActivationCode = UUID.randomUUID().toString();
    }

    @Test
    public void redirectToRegistrationPageWhenAccountNotFound() {
        // Given:
        when(accountService.findById(notExistingId)).thenReturn(Optional.empty());
        // When:
        RedirectView redirectView = activationService.activateAccount(notExistingId, invalidActivationCode);
        // Then:
        assertThat(redirectView.getUrl()).isEqualTo(siteBaseUrl + "/register");
    }

    @Test
    public void redirectToLoginPageWhenAccountIsNotEnabledYet() {
        // Given:
        mockReturningPresentAndEnabledAccount();
        // When:
        RedirectView redirectView = activationService.activateAccount(idOfExistingAndEnabledAccount, invalidActivationCode);
        // Then:
        assertThat(redirectView.getUrl()).isEqualTo(siteBaseUrl + "/login");
    }

    @Test
    public void throwExceptionWhenActivationCodeIsNotPresent() {
        // Given:
        mockReturningPresentAndNotEnabledAccount();
        when(activationCodeService.findByAccountId(idOfExistingAndNotEnabledAccount))
                .thenReturn(Optional.empty());
        // When:
        ResponseStatusException throwable = catchThrowableOfType(() -> activationService.activateAccount(idOfExistingAndNotEnabledAccount, invalidActivationCode),
                ResponseStatusException.class);
        // Then:
        Assertions.assertAll(
                () -> assertThat(throwable).isNotNull(),
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @Test
    public void throwExceptionWhenActivationCodeDoesNotMatch() {
        // Given:
        mockReturningPresentAndNotEnabledAccount();
        mockReturningActivationCode();
        // When:
        ResponseStatusException throwable = catchThrowableOfType(() -> activationService.activateAccount(idOfExistingAndNotEnabledAccount, invalidActivationCode),
                ResponseStatusException.class);
        // Then:
        Assertions.assertAll(
                () -> assertThat(throwable).isNotNull(),
                () -> assertThat(throwable).isInstanceOf(ResponseStatusException.class),
                () -> assertThat(throwable.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @Test
    public void activateUserAndRedirectToLoginPage() {
        // Given:
        mockReturningPresentAndNotEnabledAccount();
        mockReturningActivationCode();
        // When:
        RedirectView redirectView = activationService.activateAccount(idOfExistingAndNotEnabledAccount, validActivationCode);
        // Then:
        assertThat(redirectView.getUrl()).isEqualTo(siteBaseUrl + "/login");
    }

    @Test
    public void throwAccountNotFoundExceptionIfNotFound() {
        // Given:
        when(accountService.findById(1000L))
                .thenReturn(Optional.empty());
        // When:
        Throwable throwable = catchThrowableOfType(() -> activationService.createActivationCodeForAccountWithId(1000L), RuntimeException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(AccountNotFoundException.class);
    }


    private void mockReturningPresentAndEnabledAccount() {
        Account account = new Account()
                .setId(idOfExistingAndEnabledAccount)
                .setEnabled(true);
        when(accountService.findById(idOfExistingAndEnabledAccount)).thenReturn(Optional.of(account));
    }

    private void mockReturningPresentAndNotEnabledAccount() {
        Account account = new Account()
                .setId(idOfExistingAndNotEnabledAccount)
                .setEnabled(false);
        when(accountService.findById(idOfExistingAndNotEnabledAccount)).thenReturn(Optional.of(account));
    }

    private void mockReturningActivationCode() {
        ActivationCode activationCode = new ActivationCode()
                .setAccountId(idOfExistingAndNotEnabledAccount)
                .setActivationCode(validActivationCode);
        when(activationCodeService.findByAccountId(idOfExistingAndNotEnabledAccount))
                .thenReturn(Optional.of(activationCode));
    }
}
