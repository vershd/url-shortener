package ru.vershd.urlshortener.model.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountRegistrationRequest {

    @JsonProperty(value = "AccountId", required = true)
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
