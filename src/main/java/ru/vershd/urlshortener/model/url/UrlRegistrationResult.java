package ru.vershd.urlshortener.model.url;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UrlRegistrationResult {

    @JsonProperty
    private String shortUrl;

    public UrlRegistrationResult(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
