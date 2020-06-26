package pl.konradboniecki.budget.accountmanagement.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.model.ProjectAccess;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class ProjectAccessServiceTest {

    @Autowired
    private ProjectAccessService projectAccessService;
    @MockBean
    private ProjectAccessRepository projectAccessRepository;

    @BeforeEach
    void setUp() {
        ProjectAccess pj = new ProjectAccess()
                .setAccountId(1L)
                .setFamilyId(2L)
                .setBudgetGranted(true)
                .setHorseeGranted(true);
        when(projectAccessRepository.findByAccountId(eq(1L))).thenReturn(Optional.of(pj));
    }

    @Test
    public void accessDataInjectionOverridesProperties() {
        // Given:
        Account mockedAccount = new Account()
                .setId(1L)
                .setFamilyId(3L)
                .setBudgetGranted(false)
                .setHorseeGranted(false);
        // When:
        Account newAccount = projectAccessService.addProjectAccessDataToAccount(mockedAccount);
        Assertions.assertAll(
                () -> assertThat(newAccount.getFamilyId()).isEqualTo(mockedAccount.getFamilyId()),
                () -> assertThat(newAccount.isBudgetGranted()).isEqualTo(mockedAccount.isBudgetGranted()),
                () -> assertThat(newAccount.isHorseeGranted()).isEqualTo(mockedAccount.isHorseeGranted())
        );
    }
}
