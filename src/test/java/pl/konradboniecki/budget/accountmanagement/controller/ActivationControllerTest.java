package pl.konradboniecki.budget.accountmanagement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ActivationCode;
import pl.konradboniecki.budget.accountmanagement.service.*;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = RANDOM_PORT
)
public class ActivationControllerTest {
    @Value("${budget.baseUrl.gateway}")
    private String BASE_URI;
    private String contextPath;
    @LocalServerPort

    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ActivationService activationService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private ActivationCodeService activationCodeService;
    @MockBean
    private ActivationCodeRepository activationCodeRepository;
    @MockBean
    private ProjectAccessRepository projectAccessRepository;
    @MockBean
    private AccountRepository accountRepository;

    private HttpEntity httpEntity;

    @BeforeEach
    void setUp() {
        contextPath = "http://localhost:" + port + "/api/account";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        httpEntity = new HttpEntity(httpHeaders);
    }

    @Test
    void activateUser_ifAccountDoesNotExist_redirectToRegisterPage() {
        // Given:
        String GET_URL = contextPath + "/activate/" + 4L + "/notImportantValueInThisCase";
        when(accountService.findById(4L)).thenReturn(Optional.empty());
        // When:
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(GET_URL, HttpMethod.GET, httpEntity, String.class);
        // Then:
        assertThat(responseEntity.getHeaders().get("Location").get(0)).isEqualTo(BASE_URI + "/register");
        assertThat(responseEntity.getStatusCode() == HttpStatus.FOUND);

    }

    @Test
    void activateUser_ifAccountIsAlreadyEnabled_redirectToRegisterPage() {
        // Given:
        String GET_URL = contextPath + "/activate/" + 3L + "/notImportantValueInThisCase";
        when(accountService.findById(3L)).thenReturn(Optional.of(new Account().setEnabled(true)));
        // When:
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(GET_URL, HttpMethod.GET, httpEntity, String.class);
        // Then:
        assertThat(responseEntity.getHeaders().get("Location").get(0)).isEqualTo(BASE_URI + "/login");
        assertThat(responseEntity.getStatusCode() == HttpStatus.FOUND);
    }

    @Test
    void activateUser_ifAccountIsNotEnabled_redirectToLoginPage() {
        // Given:
        Long accID = 3L;
        String activationCodeString = UUID.randomUUID().toString();
        String GET_URL = contextPath + "/activate/" + accID + "/" + activationCodeString;
        ActivationCode activationCode = new ActivationCode()
                .setActivationCode(activationCodeString)
                .setAccountId(accID);
        Account account = new Account()
                .setId(accID)
                .setEnabled(true);
        when(accountService.findById(accID)).thenReturn(Optional.of(account));
        when(activationCodeService.findByAccountId(accID)).thenReturn(Optional.of(activationCode));
        // When:
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(GET_URL, HttpMethod.GET, httpEntity, String.class);
        // Then:
        assertThat(responseEntity.getHeaders().get("Location").get(0)).isEqualTo(BASE_URI + "/login");
        assertThat(responseEntity.getStatusCode() == HttpStatus.FOUND);
    }

    @Test
    public void activateUser_BAHeaderNotRequired() {
        // When:
        String url = contextPath + "/activate/100/437865";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        // Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }
}

