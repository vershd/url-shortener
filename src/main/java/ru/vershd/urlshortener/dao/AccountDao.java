package ru.vershd.urlshortener.dao;

import ru.vershd.urlshortener.model.account.Account;

import javax.sql.DataSource;

public interface AccountDao {

    void create(String name, String password);

    Account getByName(String name);

    boolean existsByName(String name);
}
