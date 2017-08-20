package ru.vershd.urlshortener.controller;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.vershd.urlshortener.dao.AccountDao;
import ru.vershd.urlshortener.model.account.AccountRegistrationRequest;
import ru.vershd.urlshortener.model.account.AccountRegistrationResult;
import ru.vershd.urlshortener.util.MessageService;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@RestController
public class AccountController {

    private static final int PASSWORD_LENGTH = 8;
    private static final String FAILED_TO_CREATE_DESCRIPTION = "account.failed.to.create";
    private static final String ACCOUNT_EXISTS_DESCRIPTION = "account.exists";
    private static final String ACCOUNT_CREATED_DESCRIPTION = "account.created";

    private final AccountDao accountDao;
    private final MessageService messageService;

    private RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(LETTERS, DIGITS)
            .build();

    @Autowired
    public AccountController(AccountDao accountDao, MessageService messageService) {
        this.accountDao = accountDao;
        this.messageService = messageService;
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountRegistrationResult> registerAccount(@RequestBody AccountRegistrationRequest accountRegistrationRequest) {
        String accountName = accountRegistrationRequest.getAccountId();
        if (accountName == null || accountName.isEmpty()) {
            return new ResponseEntity<>(new AccountRegistrationResult(false, messageService.getMessage(FAILED_TO_CREATE_DESCRIPTION)), HttpStatus.BAD_REQUEST);
        }

        if (accountDao.existsByName(accountName)) {
            return new ResponseEntity<>(new AccountRegistrationResult(false, messageService.getMessage(ACCOUNT_EXISTS_DESCRIPTION), null), HttpStatus.CONFLICT);
        }

        String accountPassword = generator.generate(PASSWORD_LENGTH);
        accountDao.create(accountName, accountPassword);

        return new ResponseEntity<>(new AccountRegistrationResult(true, messageService.getMessage(ACCOUNT_CREATED_DESCRIPTION), accountPassword), HttpStatus.CREATED);
    }
}
