package ru.vershd.urlshortener.dao;

import ru.vershd.urlshortener.model.url.Url;

import java.util.Map;

public interface UrlDao {

    void create(Url url);

    Url getByShortUrlAndAccountName(String shortUrl, String accountName);

    boolean existsByUrlAndAccountName(String url, String accountName);

    boolean existsByShortUrlAndAccountName(String url, String accountName);

    Map<String, String> getByAccountName(String accountName);
}
