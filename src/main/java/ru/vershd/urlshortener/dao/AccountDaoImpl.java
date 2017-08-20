package ru.vershd.urlshortener.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import ru.vershd.urlshortener.model.account.Account;

import javax.sql.DataSource;

@Repository
public class AccountDaoImpl implements AccountDao {

    private final JdbcTemplate jdbcTemplateObject;

    private static final String CREATE_QUERY = "INSERT INTO Account (name, password) values (?, ?)";
    private static final String GET_BY_NAME_QUERY = "SELECT * FROM Account WHERE name = ?";
    private static final String EXISTS_BY_NAME_QUERY = "SELECT COUNT(*) FROM Account WHERE name = ?";

    @Autowired
    public AccountDaoImpl(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(String name, String password) {
        Assert.hasLength(name, "Name cannot be empty");
        Assert.hasLength(password, "Password cannot be empty");

        jdbcTemplateObject.update(CREATE_QUERY, name, password);
    }

    @Override
    public Account getByName(String name) {
        Assert.hasLength(name, "Name cannot be empty");
        Account result = null;

        try {
            result = jdbcTemplateObject.queryForObject(GET_BY_NAME_QUERY,
                    new Object[]{name}, (rs, rowNum) -> {
                        Account account = new Account();
                        account.setName(rs.getString("name"));
                        account.setPassword(rs.getString("password"));

                        return account;
                    });
        }
        catch (EmptyResultDataAccessException ex) {
            System.out.println("Account not found: " + name);
        }

        return result;
    }

    @Override
    public boolean existsByName(String name) {
        Assert.hasLength(name, "Name cannot be empty");
        return jdbcTemplateObject.queryForObject(EXISTS_BY_NAME_QUERY,
                new Object[]{name}, Integer.class) != 0;
    }
}