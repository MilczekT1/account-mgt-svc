package pl.konradboniecki.budget.accountmanagement.exceptions;

import pl.konradboniecki.chassis.exceptions.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
