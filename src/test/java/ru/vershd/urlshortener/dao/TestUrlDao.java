package ru.vershd.urlshortener.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.vershd.urlshortener.model.url.Url;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUrlDao {

    @Autowired
    private UrlDao urlDao;
    @Autowired
    private AccountDao accountDao;

    private final String DEFAULT_URL = "url";
    private final String DEFAULT_SHORT_URL = "srtUrl";
    private final String DEFAULT_ACCOUNT_NAME = "account";
    private final String DEFAULT_ACCOUNT_PASSWORD = "password";

    private Url getDefaultUrl() {
        Url url = new Url();
        url.setUrl(DEFAULT_URL);
        url.setShortUrl(DEFAULT_SHORT_URL);
        url.setAccountName(DEFAULT_ACCOUNT_NAME);

        return url;
    }

    @Test
    @Transactional
    public void testCreate() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        Url result = urlDao.getByShortUrlAndAccountName(DEFAULT_SHORT_URL, DEFAULT_ACCOUNT_NAME);
        Assert.assertNotNull("Url not created", result);
        Assert.assertEquals("Wrong url", url.getUrl(), result.getUrl());
        Assert.assertEquals("Wrong shortUrl", url.getShortUrl(), result.getShortUrl());
        Assert.assertEquals("Wrong account name", url.getAccountName(), result.getAccountName());
        Assert.assertEquals("Wrong redirectType", url.getRedirectType(), result.getRedirectType());
    }

    @Test(expected = DuplicateKeyException.class)
    @Transactional
    public void testCreateDuplicate() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);
        urlDao.create(url);
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void testCreateWithoutAccount() {
        Url url = getDefaultUrl();
        urlDao.create(url);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testCreateWithEmptyUrl() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        url.setUrl("");
        urlDao.create(url);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testCreateWithEmptyShortUrl() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        url.setShortUrl("");
        urlDao.create(url);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    @Transactional
    public void testCreateWithEmptyAccountName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        url.setAccountName("");
        urlDao.create(url);
    }

    @Test(expected = DataIntegrityViolationException.class)
    @Transactional
    public void testCreateWithInvalidAccountName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        url.setAccountName(DEFAULT_ACCOUNT_NAME+" ");
        urlDao.create(url);
    }

    @Test
    @Transactional
    public void testExistsByUrlAndShortName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        boolean result = urlDao.existsByUrlAndAccountName(DEFAULT_URL, DEFAULT_ACCOUNT_NAME);
        Assert.assertTrue("Url not created", result);
    }

    @Test
    @Transactional
    public void testExistsByWrongUrlAndShortName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        boolean result = urlDao.existsByUrlAndAccountName(DEFAULT_URL + " ", DEFAULT_ACCOUNT_NAME);
        Assert.assertFalse(result);
    }

    @Test
    @Transactional
    public void testExistsByUrlAndWrongShortName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        boolean result = urlDao.existsByUrlAndAccountName(DEFAULT_URL, DEFAULT_ACCOUNT_NAME + " ");
        Assert.assertFalse(result);
    }

    @Test
    @Transactional
    public void testExistsByShortUrlAndShortName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        boolean result = urlDao.existsByShortUrlAndAccountName(DEFAULT_SHORT_URL, DEFAULT_ACCOUNT_NAME);
        Assert.assertTrue("Url not created", result);
    }

    @Test
    @Transactional
    public void testExistsByWrongShortUrlAndShortName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        boolean result = urlDao.existsByShortUrlAndAccountName(DEFAULT_SHORT_URL + " ", DEFAULT_ACCOUNT_NAME);
        Assert.assertFalse(result);
    }

    @Test
    @Transactional
    public void testExistsByShortUrlAndWrongShortName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);

        boolean result = urlDao.existsByShortUrlAndAccountName(DEFAULT_SHORT_URL, DEFAULT_ACCOUNT_NAME + " ");
        Assert.assertFalse(result);
    }


    @Test
    @Transactional
    public void testGetByAccountName() {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);

        Url url = getDefaultUrl();
        urlDao.create(url);
        url.setUrl("testUrl");
        url.setShortUrl("654321");
        urlDao.create(url);

        Map<String, String> map = urlDao.getByAccountName(DEFAULT_ACCOUNT_NAME);
        Assert.assertNotNull("Urls not found", map);
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(map.containsKey(DEFAULT_URL));
        Assert.assertTrue(map.containsKey("testUrl"));
    }

    @Test
    @Transactional
    public void testGetByWrongAccountName() {
        Map<String, String> map = urlDao.getByAccountName(DEFAULT_ACCOUNT_NAME);
        Assert.assertTrue(map.size() == 0);
    }
}
