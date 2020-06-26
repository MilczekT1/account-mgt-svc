package pl.konradboniecki.budget.accountmanagement.contractbases;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import pl.konradboniecki.budget.accountmanagement.AccountServiceApp;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ProjectAccess;
import pl.konradboniecki.budget.accountmanagement.service.AccountRepository;
import pl.konradboniecki.budget.accountmanagement.service.AccountService;
import pl.konradboniecki.budget.accountmanagement.service.ProjectAccessRepository;
import pl.konradboniecki.budget.accountmanagement.service.ProjectAccessService;

import java.util.Optional;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AccountServiceApp.class,
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false"
)
public class PasswordMgtClientBase {

    @Autowired
    protected AccountService accountService;
    @Autowired
    protected ProjectAccessService projectAccessService;
    @MockBean
    protected ProjectAccessRepository projectAccessRepository;
    @MockBean
    protected AccountRepository accountRepository;

    @LocalServerPort
    int port;

    private static String existingEmail;
    private String notExistingEmail;

    @Before
    public void setUpMocks() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));

        existingEmail = "existing_email@password_management.com";
        notExistingEmail = "not_existing_email@password_management.com";

        findAccountContracts_mockProjectAccessData();
        mockExistingAndNotExistingAccount();
    }

    private void findAccountContracts_mockProjectAccessData() {
        ProjectAccess pj1 = new ProjectAccess()
                .setAccountId(1L)
                .setFamilyId(1L);
        when(projectAccessRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(pj1));
    }

    private void mockExistingAndNotExistingAccount() {
        Account account = new Account()
                .setId(1L)
                .setFirstName("testFirstName")
                .setLastName("testLastName")
                .setEmail(existingEmail)
                .setRole("USER")
                .setEnabled(true);
        when(accountRepository.findByEmail(existingEmail))
                .thenReturn(Optional.of(account));
        when(accountRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());
    }
}
