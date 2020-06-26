package pl.konradboniecki.budget.accountmanagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.konradboniecki.budget.accountmanagement.model.ActivationCode;
import pl.konradboniecki.budget.accountmanagement.service.ActivationService;

@Slf4j
@Controller
@RequestMapping("/api/account")
public class ActivationController {

    private ActivationService activationService;

    @Autowired
    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    @GetMapping(value = "/activate/{id}/{activationCode}")
    public RedirectView activateUser(
            @PathVariable(name = "id") Long accountId,
            @PathVariable(name = "activationCode") String activationCodeFromUrl) {

        return activationService.activateAccount(accountId, activationCodeFromUrl);
    }

    @PostMapping(value = "/activationCode")
    public ResponseEntity<ActivationCode> createActivationCode(
            @RequestHeader("id") Long accountId) {

        ActivationCode activationCode = activationService.createActivationCodeForAccountWithId(accountId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(activationCode, httpHeaders, HttpStatus.CREATED);
    }
}
