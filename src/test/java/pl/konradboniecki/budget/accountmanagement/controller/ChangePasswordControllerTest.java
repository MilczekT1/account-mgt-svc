package pl.konradboniecki.budget.accountmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.service.AccountService;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
public class ChangePasswordControllerTest {

    @MockBean
    private AccountService accountService;
    @Autowired
    private TestRestTemplate rest;
    @LocalServerPort
    private int port;
    private String baseUrl;
    private String changePasswdUrl;
    private HashMap<String, String> urlVariables = new HashMap<>();
    private Long validId;
    private HttpEntity httpEntity;
    private HttpHeaders httpHeaders;

    @BeforeAll
    public void healthcheck() {
        baseUrl = "http://localhost:" + port + "/api/account";
        changePasswdUrl = baseUrl + "/change-password";
        validId = 1L;

        httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        httpEntity = new HttpEntity(httpHeaders);

        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = rest.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    public void givenChangePassword_WhenAccountFound_ThenResponseCodeIs200() {
        // Given
        Mockito.when(accountService.findById(validId))
                .thenReturn(Optional.of(new Account()));
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("AccountId", validId);
        json.put("NewPassword", "abracadabra");
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), httpHeaders);
        // When
        ResponseEntity<String> responseEntity = rest.exchange(changePasswdUrl, HttpMethod.PUT, entity, String.class, urlVariables);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenChangePassword_WhenAccountNotFound_ThenResponseCodeIs404() {
        // Given
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("AccountId", -1L);
        json.put("NewPassword", "abracadabra");
        HttpEntity<String> entity = new HttpEntity<>(json.toString(), httpHeaders);
        // When
        ResponseEntity<String> responseEntity = rest.exchange(changePasswdUrl, HttpMethod.PUT, entity, String.class, urlVariables);
        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createActivationCode_ifBAHeaderIsMissing_returnUnauthorized() {
        // When:
        ResponseEntity<String> responseEntity = rest.exchange(changePasswdUrl, HttpMethod.PUT, new HttpEntity<>(new HttpHeaders()), String.class);
        // Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
