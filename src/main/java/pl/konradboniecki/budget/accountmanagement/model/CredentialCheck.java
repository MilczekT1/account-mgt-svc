package pl.konradboniecki.budget.accountmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CredentialCheck {

    private Long accountId;
    private String password;

    //TODO properly test credential check (headers may have invalid values)
}
