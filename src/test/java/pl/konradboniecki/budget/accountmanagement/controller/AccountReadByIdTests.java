package pl.konradboniecki.budget.accountmanagement.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.service.AccountRepository;
import pl.konradboniecki.budget.accountmanagement.service.AccountService;
import pl.konradboniecki.budget.accountmanagement.service.ProjectAccessRepository;
import pl.konradboniecki.budget.accountmanagement.service.ProjectAccessService;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
public class AccountReadByIdTests {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TestRestTemplate rest;
    @MockBean
    protected ProjectAccessService projectAccessService;
    @MockBean
    protected ProjectAccessRepository projectAccessRepository;
    @MockBean
    private AccountRepository accountRepository;
    @LocalServerPort
    private int port;
    private String baseUrl;
    private HttpEntity httpEntity;

    @BeforeAll
    public void healthcheck() {
        baseUrl = "http://localhost:" + port + "/api/account";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        httpEntity = new HttpEntity(httpHeaders);

        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = rest.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");
    }

    private void mockAccountWithIdEqual1() {
        Account account = new Account()
                .setId(1L)
                .setEmail("test@email.com");
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(projectAccessService.addProjectAccessDataToAccount(any(Account.class)))
                .thenReturn(account);
    }

    @Test
    public void givenFindByIdWithDefaultParam_WhenFound_ThenAccountIsReturned() {
        // Given
        String url = baseUrl + "/1";
        mockAccountWithIdEqual1();
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getBody().getId()).isEqualTo(1L);
    }

    @Test
    public void givenFindByIdWithIdParam_WhenFound_ThenAccountIsReturned() {
        // Given
        String url = baseUrl + "/1?findBy=id";
        mockAccountWithIdEqual1();
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);

        // Then
        assertThat(responseEntity.getBody().getId()).isEqualTo(1L);
    }

    @Test
    public void givenFindByIdWithDefaultParam_WhenFound_ThenResponseCodeIs200() {
        // Given
        String url = baseUrl + "/1";
        mockAccountWithIdEqual1();
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenFindByIdWithIdParam_WhenFound_ThenResponseCodeIs200() {
        // Given
        String url = baseUrl + "/1?findBy=id";
        mockAccountWithIdEqual1();
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenFindById_WhenNotFound_ThenResponseCodeIs404() {
        // Given
        String url = baseUrl + "/300";
        when(accountRepository.findById(300L))
                .thenReturn(Optional.empty());
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenFindById_WhenInvalidId_ThenResponseCodeIs400() {
        // Given
        String url = baseUrl + "/345trdsfv";
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenFindById_WhenUnknownParam_ThenResponseCodeIs400() {
        // Given
        String url = baseUrl + "/1?findBy=dupa";
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
//TODO:
//    public void givenFindById_WhenUnknownParam_ThenResponseMessageIsValid(){}
}
