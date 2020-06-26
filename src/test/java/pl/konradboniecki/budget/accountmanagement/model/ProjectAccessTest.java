package pl.konradboniecki.budget.accountmanagement.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProjectAccessTest {

    @Test
    void accessToBudgetIsNotGrantedByDefault() {
        ProjectAccess pj = new ProjectAccess();
        Assertions.assertThat(pj.isBudgetGranted()).isFalse();
    }

    @Test
    void accessToHorseeIsNotGrantedByDefault() {
        ProjectAccess pj = new ProjectAccess();
        Assertions.assertThat(pj.isHorseeGranted()).isFalse();
    }

}
