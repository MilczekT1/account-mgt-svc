package pl.konradboniecki.budget.accountmanagement.contractbases;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import pl.konradboniecki.budget.accountmanagement.AccountServiceApp;
import pl.konradboniecki.budget.accountmanagement.controller.ChangePasswordController;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ActivationCode;
import pl.konradboniecki.budget.accountmanagement.model.ProjectAccess;
import pl.konradboniecki.budget.accountmanagement.service.*;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = AccountServiceApp.class,
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false"
)
public class MvcClientBase {

    @Autowired
    protected ChangePasswordController changePasswordController;
    @Autowired
    protected AccountService accountService;
    @Autowired
    protected ProjectAccessService projectAccessService;
    @MockBean
    protected AccountRepository accountRepository;
    @MockBean
    protected ActivationCodeService activationCodeService;
    @MockBean
    protected ProjectAccessRepository projectAccessRepository;
    @MockBean
    protected FamilyClient familyClient;

    private static String existingEmail;
    private String notExistingEmail;
    private static String existingEmailInFindByMailContracts;

    @LocalServerPort
    int port;

    @Before
    public void setUpMocks() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
        findByIdContracts_mockExistingAndNotExistingAccount();

        existingEmail = "existing_email@mail.com";
        existingEmailInFindByMailContracts = "existing_email@find_by_mail.com";
        notExistingEmail = "not_existing_email@mail.com";

        findAccountContracts_mockProjectAccessData();
        findByEmailContracts_mockExistingAndNotExistingAccount();

        saveAccountContracts_mockProjectAccessData();
        saveAccountContracts_mockSuccessfulCreationOfAnAccount();
        saveAccountContracts_mockConflictDuringCreationOfAnAccount();

        activateUserContracts__mockAccounts();
        activateUserContracts_mockActivationCodeToPersist();

        activateUserContracts_mockNotExistingAccount();
        credentialsContracts_mockCredentialChecks();

        familyAssignmentContracts_mockFamilyAssignments();
    }

    private void findAccountContracts_mockProjectAccessData() {
        ProjectAccess pj1 = new ProjectAccess()
                .setAccountId(1L)
                .setFamilyId(1L);
        when(projectAccessRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(pj1));
    }

    private void findByIdContracts_mockExistingAndNotExistingAccount() {
        Account account = new Account()
                .setId(1L)
                .setFirstName("testFirstName")
                .setLastName("testLastName")
                .setEmail(existingEmail)
                .setRole("USER")
                .setEnabled(true);
        when((accountRepository.findById(1L)))
                .thenReturn(Optional.of(account));
        when((accountRepository.findById(2L)))
                .thenReturn(Optional.empty());
    }

    private void findByEmailContracts_mockExistingAndNotExistingAccount() {
        Account account = new Account()
                .setId(1L)
                .setFirstName("testFirstName")
                .setLastName("testLastName")
                .setEmail(existingEmailInFindByMailContracts)
                .setRole("USER")
                .setEnabled(true);
        Account account13 = new Account()
                .setId(13L)
                .setFirstName("testFirstName")
                .setLastName("testLastName")
                .setEmail(existingEmail)
                .setRole("USER")
                .setEnabled(true);
        when(accountRepository.findByEmail(existingEmailInFindByMailContracts.toLowerCase()))
                .thenReturn(Optional.of(account));
        when(accountRepository.findById(13L))
                .thenReturn(Optional.of(account13));
        when(accountRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());
    }

    private void saveAccountContracts_mockProjectAccessData() {
        ProjectAccess pj2 = new ProjectAccess()
                .setAccountId(2L)
                .setBudgetGranted(true)
                .setHorseeGranted(true);
        when(projectAccessRepository.findByAccountId(eq(2L))).thenReturn(Optional.of(pj2));
    }

    private void saveAccountContracts_mockSuccessfulCreationOfAnAccount() {
        Account account = new Account()
                .setId(2L)
                .setFirstName("testFirstName")
                .setLastName("testLastName")
                .setEmail(notExistingEmail)
                .setRole("USER")
                .setEnabled(false);
        when(accountRepository.findByEmail(notExistingEmail))
                .thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);
    }

    private void saveAccountContracts_mockConflictDuringCreationOfAnAccount() {
        when(accountRepository.findByEmail(existingEmail))
                .thenReturn(Optional.of(new Account()));
    }

    private void activateUserContracts_mockActivationCodeToPersist() {
        ActivationCode activationCode = new ActivationCode()
                .setId(23L)
                .setAccountId(5L)
                .setActivationCode(UUID.randomUUID().toString());
        when(activationCodeService.save(any(ActivationCode.class)))
                .thenReturn(activationCode);
    }

    private void activateUserContracts__mockAccounts() {
        when(accountService.findById(5L)).thenReturn(Optional.of(new Account()));
        when(accountService.findById(1000L)).thenReturn(Optional.empty());
    }

    private void credentialsContracts_mockCredentialChecks() {
        Account acc = new Account()
                .setId(5L)
                .setPassword("correctHashValue");
        when(accountRepository.findById(5L)).thenReturn(Optional.of(acc));
    }

    private void activateUserContracts_mockNotExistingAccount() {
        when(accountRepository.findById(4L)).thenReturn(Optional.empty());
    }

    private void familyAssignmentContracts_mockFamilyAssignments() {
        when(accountRepository.findById(105L)).thenReturn(Optional.of(new Account()));
        when(familyClient.isPresentById(105L)).thenReturn(true);
        when(familyClient.isPresentById(106L)).thenReturn(false);
    }
}
