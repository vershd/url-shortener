package ru.vershd.urlshortener.controller;

import org.apache.commons.text.RandomStringGenerator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.vershd.urlshortener.dao.UrlDao;
import ru.vershd.urlshortener.model.url.Url;
import ru.vershd.urlshortener.model.url.UrlRegistrationRequest;
import ru.vershd.urlshortener.model.url.UrlRegistrationResult;
import ru.vershd.urlshortener.util.MessageService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

@RestController
public class UrlController {

    private static final int SHORT_URL_LENGTH = 6;
    private static final String INVALID_URL = "url.invalid";
    private static final String URL_ALREADY_REGISTERED = "url.already.registered";
    private final UrlDao urlDao;
    private final MessageService messageService;

    private UrlValidator urlValidator = new UrlValidator();

    private RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(LETTERS, DIGITS)
            .build();


    @Autowired
    public UrlController(UrlDao urlDao, MessageService messageService) {
        this.urlDao = urlDao;
        this.messageService = messageService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUrl(HttpServletRequest request, @RequestBody UrlRegistrationRequest urlRegistrationRequest) {
        if (!isRequestValid(urlRegistrationRequest)) {
            return new ResponseEntity<>(messageService.getMessage(INVALID_URL), HttpStatus.BAD_REQUEST);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accountName = auth.getName();

        if (urlDao.existsByUrlAndAccountName(urlRegistrationRequest.getUrl(), accountName)) {
            return new ResponseEntity<>(messageService.getMessage(URL_ALREADY_REGISTERED), HttpStatus.CONFLICT);
        }

        String appUri = request.getScheme() + "://" +
                request.getServerName() +
                ":" +
                request.getServerPort() + "/";

        Url url = new Url();
        url.setUrl(urlRegistrationRequest.getUrl());
        url.setShortUrl(generateShortUrl());
        url.setRedirectType(urlRegistrationRequest.getRedirectType());
        url.setAccountName(accountName);
        urlDao.create(url);

        return new ResponseEntity<>(new UrlRegistrationResult(appUri + url.getShortUrl()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{shortUrl}", method = RequestMethod.GET)
    public void redirect(@PathVariable String shortUrl, HttpServletResponse resp) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accountName = auth.getName();

        if (!urlDao.existsByShortUrlAndAccountName(shortUrl, accountName)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            Url url = urlDao.getByShortUrlAndAccountName(shortUrl, accountName);
            resp.setStatus(url.getRedirectType());
            resp.setHeader(HttpHeaders.LOCATION, url.getUrl());
            resp.setHeader(HttpHeaders.CONNECTION, "close");
        }
    }

    @RequestMapping(value = "/statistic/{accountName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> redirect(@PathVariable String accountName) throws Exception {
       Map<String, String> urlList = urlDao.getByAccountName(accountName);
       return new ResponseEntity<>(urlList, HttpStatus.OK);
    }

    private boolean isRequestValid(UrlRegistrationRequest urlRegistrationRequest) {

        return (urlRegistrationRequest.getRedirectType() == HttpStatus.MOVED_PERMANENTLY.value()
                || urlRegistrationRequest.getRedirectType() == HttpStatus.FOUND.value())
                && urlValidator.isValid(urlRegistrationRequest.getUrl());
    }

    /**
     * Ideally we should use some cryptographic hash and ensure there are no collisions
     * But in this case it would be an overengineering
     */
    private String generateShortUrl() {
        return generator.generate(SHORT_URL_LENGTH);
    }
}
