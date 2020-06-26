package pl.konradboniecki.budget.accountmanagement.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class ActivationCodeServiceTest {

    @MockBean
    private ActivationCodeRepository activationCodeRepository;

    @Autowired
    private ActivationCodeService activationCodeService;

    @Test
    @DisplayName("Invoke repository method on service call for findByAccountID")
    public void givenFindByAccountId_whenServiceIsCalled_thenRepoMethodIsInvoked() {
        // Given:
        when(activationCodeRepository.findByAccountId(anyLong())).thenReturn(null);
        // When:
        activationCodeService.findByAccountId(1L);
        // Then:
        verify(activationCodeRepository, times(1)).findByAccountId(1L);
    }

    @Test
    @DisplayName("Invoke repository method on service call for deleteById")
    public void givenDeleteById_whenServiceIsCalled_thenRepoMethodIsInvoked() {
        // Given:
        doNothing().when(activationCodeRepository).deleteById(anyLong());
        // When:
        activationCodeService.deleteById(1L);
        // Then:
        verify(activationCodeRepository, times(1)).deleteById(1L);
    }
}
