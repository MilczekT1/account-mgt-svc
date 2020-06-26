package pl.konradboniecki.budget.accountmanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "thisNameSuxInHibernate5")
    @GenericGenerator(name = "thisNameSuxInHibernate5", strategy = "increment")
    @Column(name = "account_id")
    private Long id;

    @JsonInclude()
    @Transient
    private Long familyId;
    @JsonInclude()
    @Transient
    private boolean budgetGranted;
    @JsonInclude()
    @Transient
    private boolean horseeGranted;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "date_of_registration")
    private ZonedDateTime registerDate;

    @Column(name = "role")
    private String role;
    @Column(name = "enabled")
    private boolean enabled;

    public boolean hasFamily() {
        return familyId != null;
    }

    public static boolean isEmailValid(String email) {
        if (StringUtils.isEmpty(email))
            return false;
        if (!Pattern.matches("(\\w||\\.)+@\\w+.[a-zA-Z]+", email))
            return false;
        else
            return true;
    }
}
