package pl.konradboniecki.budget.accountmanagement.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Table(name = "project_access")
public class ProjectAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "thisNameSuxInHibernate5")
    @GenericGenerator(name = "thisNameSuxInHibernate5", strategy = "increment")
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "family_id")
    private Long familyId;
    @Column(name = "budget_granted")
    private boolean budgetGranted;
    @Column(name = "horsee_granted")
    private boolean horseeGranted;
}
