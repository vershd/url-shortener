package ru.vershd.urlshortener.model.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountRegistrationResult {

    @JsonProperty(required = true)
    private boolean status;
    @JsonProperty(required = true)
    private String description;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String password;


    public AccountRegistrationResult(boolean status, String description, String password) {
        this.status = status;
        this.description = description;
        this.password = password;
    }

    public AccountRegistrationResult(boolean status, String description) {
        this(status, description, null);
    }
}
