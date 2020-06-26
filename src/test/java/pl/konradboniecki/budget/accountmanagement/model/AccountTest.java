package pl.konradboniecki.budget.accountmanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class AccountTest {

    @Test
    void testIfHasFamily() {
        Account acc = new Account();
        assertFalse(acc.hasFamily());
        acc.setFamilyId(1L);
        assertTrue(acc.hasFamily());
    }

    @Test
    public void testEmailValidation() {
        // TODO: edge cases
        assertTrue(Account.isEmailValid("konrad_boniecki@hotmail.com"));
        assertFalse(Account.isEmailValid(""));
        assertFalse(Account.isEmailValid(null));
    }
}
