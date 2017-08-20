package ru.vershd.urlshortener.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import ru.vershd.urlshortener.model.url.Url;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UrlDaoImpl implements UrlDao {

    private final JdbcTemplate jdbcTemplateObject;

    private static final String CREATE_QUERY = "INSERT INTO URL (url, shortUrl, redirectType, hitCount, accountName) values (?, ?, ?, ?, ?)";
    private static final String GET_BY_SHORT_URL_AND_ACCOUNT_NAME_QUERY = "SELECT * FROM URL WHERE shortUrl = ? AND accountName = ?";
    private static final String EXISTS_BY_SHORTURL_AND_ACCOUNT_NAME_QUERY = "SELECT COUNT(*) FROM URL WHERE shortUrl = ? AND accountName = ?";
    private static final String EXISTS_BY_URL_AND_ACCOUNT_NAME_QUERY = "SELECT COUNT(*) FROM URL WHERE url = ? AND accountName = ?";
    private static final String UPDATE_HIT_COUNT_QUERY = "UPDATE URL SET hitCount = hitCount + 1 WHERE shortUrl = ? AND accountName = ?";
    private static final String LIST_BY_ACCOUNT_NAME_QUERY = "SELECT url, hitCount FROM URL WHERE accountName = ?";

    @Autowired
    public UrlDaoImpl(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(Url url) {
        Assert.notNull(url, "Url cannot be null");
        Assert.hasLength(url.getUrl(), "Url cannot be emptry");
        Assert.hasLength(url.getShortUrl(), "ShortUrl cannot be emptry");
        Assert.hasLength(url.getAccountName(), "Account name cannot be emptry");

        jdbcTemplateObject.update(CREATE_QUERY, url.getUrl(), url.getShortUrl(), url.getRedirectType(), url.getHitCount(), url.getAccountName());
    }

    @Override
    public Url getByShortUrlAndAccountName(String shortUrl, String accountName) {
        jdbcTemplateObject.update(UPDATE_HIT_COUNT_QUERY, shortUrl, accountName);
        return jdbcTemplateObject.queryForObject(GET_BY_SHORT_URL_AND_ACCOUNT_NAME_QUERY,
                new Object[]{shortUrl, accountName}, (rs, rowNum) -> {
                    Url url = new Url();
                    url.setUrl(rs.getString("url"));
                    url.setShortUrl(rs.getString("shortUrl"));
                    url.setRedirectType(rs.getInt("redirectType"));
                    url.setHitCount(rs.getInt("hitCount"));
                    url.setAccountName(rs.getString("accountName"));
                    return url;
                });
    }

    @Override
    public boolean existsByUrlAndAccountName(String url, String accountName) {
        return jdbcTemplateObject.queryForObject(EXISTS_BY_URL_AND_ACCOUNT_NAME_QUERY,
                new Object[]{url, accountName}, Integer.class) != 0;
    }

    @Override
    public boolean existsByShortUrlAndAccountName(String url, String accountName) {
        return jdbcTemplateObject.queryForObject(EXISTS_BY_SHORTURL_AND_ACCOUNT_NAME_QUERY,
                new Object[]{url, accountName}, Integer.class) != 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getByAccountName(String accountName) {
        return (Map<String, String>) jdbcTemplateObject.query(LIST_BY_ACCOUNT_NAME_QUERY, new Object[]{accountName}, (ResultSetExtractor) rs -> {
            Map<String, String> map = new HashMap<>();
            while (rs.next()) {
                String item = rs.getString("url");
                String price = rs.getString("hitCount");
                map.put(item, price);
            }
            return map;
        });
    }
}
