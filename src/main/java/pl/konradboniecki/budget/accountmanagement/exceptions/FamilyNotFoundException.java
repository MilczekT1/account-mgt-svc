package pl.konradboniecki.budget.accountmanagement.exceptions;

import pl.konradboniecki.chassis.exceptions.ResourceNotFoundException;

public class FamilyNotFoundException extends ResourceNotFoundException {

    public FamilyNotFoundException(String message) {
        super(message);
    }

    public FamilyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
