package ru.vershd.urlshortener.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.vershd.urlshortener.model.account.Account;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAccountDao {

    @Autowired
    private AccountDao accountDao;

    private final String DEFAULT_NAME = "account";
    private final String DEFAULT_PASSWORD = "password";


    @Test
    @Transactional
    public void testCreate() {
        accountDao.create(DEFAULT_NAME, DEFAULT_PASSWORD);

        Account account = accountDao.getByName(DEFAULT_NAME);
        Assert.assertNotNull("Account not created", account);
        Assert.assertEquals("Wrong account name", DEFAULT_NAME, account.getName());
        Assert.assertEquals("Wrong account password", DEFAULT_PASSWORD, account.getPassword());
    }

    @Test(expected = DuplicateKeyException.class)
    @Transactional
    public void testCreateDuplicate() {
        accountDao.create(DEFAULT_NAME, DEFAULT_PASSWORD);
        accountDao.create(DEFAULT_NAME, DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void testCreateWithUglyName() {
        final String uglyName = "\\//..,,zxca123||||ЛЛЛЛ";
        accountDao.create(uglyName, DEFAULT_PASSWORD);

        Account account = accountDao.getByName(uglyName);
        Assert.assertNotNull("Account not created", account);
        Assert.assertEquals("Wrong account name", uglyName, account.getName());
        Assert.assertEquals("Wrong account password", DEFAULT_PASSWORD, account.getPassword());
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testCreateWithEmptyName() {
        final String name = "";
        accountDao.create(name, DEFAULT_PASSWORD);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testCreateWithEmptyPassword() {
        final String password = "";
        accountDao.create(DEFAULT_NAME, password);
    }

    @Test
    @Transactional
    public void testGetByMissingName() {
        Account account = accountDao.getByName(DEFAULT_NAME);
        Assert.assertNull(account);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testGetByEmptyName() {
        accountDao.getByName("");
    }

    @Test
    @Transactional
    public void testExistsByName() {
        accountDao.create(DEFAULT_NAME, DEFAULT_PASSWORD);
        boolean result = accountDao.existsByName(DEFAULT_NAME);
        Assert.assertTrue(result);
    }

    @Test
    @Transactional
    public void testExistsByMissingName() {
        boolean result = accountDao.existsByName(DEFAULT_NAME);
        Assert.assertFalse(result);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testExistsByEmptyName() {
        accountDao.existsByName("");
    }
}
