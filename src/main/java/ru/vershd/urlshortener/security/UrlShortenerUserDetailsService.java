package ru.vershd.urlshortener.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vershd.urlshortener.dao.AccountDao;
import ru.vershd.urlshortener.model.account.Account;

import java.util.Collections;

@Service
public class UrlShortenerUserDetailsService implements UserDetailsService {

    private final AccountDao accountDao;

    @Autowired
    public UrlShortenerUserDetailsService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!accountDao.existsByName(username)) {
            throw new UsernameNotFoundException("could not find the user '"
                    + username + "'");
        }

        Account account = accountDao.getByName(username);
        return new User(account.getName(), account.getPassword(), Collections.singletonList(() -> "ROLE_ADMIN"));
    }
}
