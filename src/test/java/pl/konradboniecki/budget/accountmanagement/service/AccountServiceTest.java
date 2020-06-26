package pl.konradboniecki.budget.accountmanagement.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import pl.konradboniecki.budget.accountmanagement.exceptions.FamilyNotFoundException;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    protected ProjectAccessRepository projectAccessRepository;
    @MockBean
    private FamilyClient familyClient;

    @Test
    @DisplayName("findby invalid id results in exception with 404 status")
    public void givenInvalidId_whenFindById_thenExceptionIsThrownWith400Status() {
        // Given:
        String invalidId = "blabla";
        // When:
        BadRequestException throwable = catchThrowableOfType(() -> accountService.findByIdAsStringFromParam(invalidId),
                BadRequestException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(BadRequestException.class);
    }

    @Test
    public void whenActivateAccountWithId_thenRepoMethodIsInvoked() {
        // Given:
        doNothing().when(accountRepository).setEnabled(anyLong());
        // When:
        accountService.activateAccountWith(1L);
        // Then:
        verify(accountRepository, times(1)).setEnabled(1L);
    }

    @Test
    public void givenAssignmentFamilyToAccount_whenFamilyFound_thenSetFamilyId() {
        // Given:
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(familyClient.isPresentById(eq(1L))).thenReturn(true);
        doNothing().when(projectAccessRepository).setFamilyId(anyLong(), anyLong());
        // When:
        accountService.assignAccountToFamily(1L, 1L);
        // Then:
        verify(projectAccessRepository, times(1)).setFamilyId(anyLong(), anyLong());
    }

    @Test
    public void givenAssignmentFamilyToAccount_whenFamilyNotFoundWithClientError_thenThrow() {
        // Given:
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));
        when(familyClient.isPresentById(5L)).thenReturn(false);
        // When:
        Exception throwable = catchThrowableOfType(() ->
                        accountService.assignAccountToFamily(1L, 5L),
                FamilyNotFoundException.class);
        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable).isInstanceOf(FamilyNotFoundException.class);
    }

    @Test
    void given_accountCreation_whenConflict_thenResponseWithConflict() {
        // Given:
        String presentEmail = "presentEmail@mail.com";
        Account mockedAccount = new Account().setEmail(presentEmail);
        when(accountRepository.findByEmail(presentEmail))
                .thenReturn(Optional.of(mockedAccount));
        // When:
        ResponseStatusException throwable = catchThrowableOfType(() ->
                        accountService.createAccountAndProjectAccess(mockedAccount),
                ResponseStatusException.class);

        // Then:
        assertThat(throwable).isNotNull();
        assertThat(throwable.getStatus()).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    void given_accountCreation_whenSuccess_thenResponseWithAccount() {
        // Given:
        String notPresentEmail = "notPresentEmail@mail.com";
        Account mockedAccount = new Account()
                .setEmail(notPresentEmail)
                .setPassword("thisShouldBeErased");
        when(accountRepository.findByEmail(notPresentEmail))
                .thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class)))
                .thenReturn(mockedAccount);
        // When:
        Account newAccount = accountService.createAccountAndProjectAccess(new Account());
        // Then:
        assertThat(newAccount).isNotNull();
        assertThat(newAccount.getEmail()).isEqualTo(notPresentEmail);
        assertThat(newAccount.getPassword()).isNull();

    }
}
