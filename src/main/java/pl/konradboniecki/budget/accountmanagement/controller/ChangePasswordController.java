package pl.konradboniecki.budget.accountmanagement.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.accountmanagement.model.Account;
import pl.konradboniecki.budget.accountmanagement.service.AccountService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/account")
public class ChangePasswordController {

    private AccountService accountService;

    @Autowired
    public ChangePasswordController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("/change-password")
    public ResponseEntity changePassword(@RequestBody String json) throws IOException {
        Map<String, Object> map = createMapFromJson(json);
        Long id = Long.valueOf(map.get("AccountId").toString());
        String newPassword = map.get("NewPassword").toString();

        Optional<Account> accountOpt = accountService.findById(id);
        if (accountOpt.isPresent()) {
            accountService.changePassword(newPassword, id);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    private Map<String, Object> createMapFromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }
}