package pl.konradboniecki.budget.accountmanagement.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import pl.konradboniecki.budget.accountmanagement.service.ProjectAccessRepository;
import pl.konradboniecki.budget.accountmanagement.service.ProjectAccessService;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
public class AccountReadByEmailTests {

    @Autowired
    private TestRestTemplate rest;
    @LocalServerPort
    private int port;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    protected ProjectAccessService projectAccessService;
    @MockBean
    protected ProjectAccessRepository projectAccessRepository;
    private String baseUrl;
    private HttpEntity httpEntity;

    @BeforeAll
    public void healthcheck() {
        baseUrl = "http://localhost:" + port + "/api/account";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        httpEntity = new HttpEntity(httpHeaders);

        String healthCheckUrl = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = rest.getForEntity(healthCheckUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    public void givenFindByEmail_WhenFound_ThenAccountIsReturned() {
        // Given
        String email = "test@mail.com";
        Account acc = new Account().setEmail(email);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(acc));
        when(projectAccessService.addProjectAccessDataToAccount(any(Account.class))).thenReturn(acc);
        String url = baseUrl + "/test@mail.com?findBy=email";
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        // TODO: adjust validation for email
        assertThat(responseEntity.getBody().getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    public void givenFindByEmail_WhenFound_ThenResponseCodeIs200() {
        // Given
        String email = "test@mail.com";
        Account acc = new Account().setEmail(email);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(acc));
        String url = baseUrl + "/test@mail.com?findBy=email";
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenFindByEmail_WhenNotFound_ThenResponseCodeIs404() {
        // Given
        String url = baseUrl + "/79esriguyfxhjvTEST@MAIL.tv?findBy=email";
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(url, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void givenFindByEmail_WhenInvalidEmail_ThenResponseCodeIs400() {
        // Given
        String urlWithoutEmail = baseUrl + "/3?findBy=email";
        // When
        ResponseEntity<Account> responseEntity = rest.exchange(urlWithoutEmail, HttpMethod.GET, httpEntity, Account.class);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void return401FromControllerWhenBAHeaderIsMissing() {
        // Given:
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        // When:
        ResponseEntity<String> responseEntity1 = rest.exchange(baseUrl, HttpMethod.POST, httpEntity, String.class);
        ResponseEntity<String> responseEntity2 = rest.exchange(baseUrl + "/10/family/10", HttpMethod.PUT, httpEntity, String.class);
        ResponseEntity<String> responseEntity3 = rest.exchange(baseUrl + "/credentials", HttpMethod.GET, httpEntity, String.class);
        ResponseEntity<String> responseEntity4 = rest.exchange(baseUrl + "/4", HttpMethod.GET, httpEntity, String.class);
        // Then:
        Assertions.assertAll(
                () -> assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity3.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED),
                () -> assertThat(responseEntity4.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
        );
    }
}
