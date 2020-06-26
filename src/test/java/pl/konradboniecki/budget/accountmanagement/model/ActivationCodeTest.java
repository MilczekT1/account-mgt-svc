package pl.konradboniecki.budget.accountmanagement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActivationCodeTest {

    @Test
    void testUserActivationCodeInit() {
        Long accountId = 3L;
        String activationCode = "234regdf";
        ActivationCode userActivationCode = new ActivationCode(accountId, activationCode);

        assertAll(
                () -> assertNull(userActivationCode.getId()),
                () -> assertEquals(accountId, userActivationCode.getAccountId()),
                () -> assertEquals(activationCode, userActivationCode.getActivationCode()),
                () -> assertNotNull(userActivationCode.getApplyTime())
        );
    }
}
