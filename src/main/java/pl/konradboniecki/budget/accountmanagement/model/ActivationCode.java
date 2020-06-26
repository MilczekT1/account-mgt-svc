package pl.konradboniecki.budget.accountmanagement.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "activation_code")
public class ActivationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "thisNameSuxInHibernate5")
    @GenericGenerator(name = "thisNameSuxInHibernate5", strategy = "increment")
    @Column(name = "activation_code_id")
    private Long id;
    @Column(name = "account_id")
    private Long accountId;
    @Column(name = "activation_code")
    private String activationCode;
    @Column(name = "creation_time")
    private ZonedDateTime applyTime;

    public ActivationCode() {
        setApplyTime(ZonedDateTime.now(ZoneId.of("Europe/Warsaw")));
    }

    public ActivationCode(Long accountId, String activationCode) {
        this();
        setAccountId(accountId);
        setActivationCode(activationCode);
    }
}
