package ru.vershd.urlshortener.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.requestCache().disable();
        http.authorizeRequests().antMatchers("/account*").permitAll();
        http.authorizeRequests().antMatchers("/help*").permitAll();
        http.authorizeRequests().antMatchers("/documentation*").permitAll();
        http.authorizeRequests().antMatchers("/swagger*").permitAll();

        http.authorizeRequests().antMatchers("/register*").fullyAuthenticated();
        http.authorizeRequests().antMatchers("/statistic*").fullyAuthenticated();
        http.authorizeRequests().antMatchers("/*").fullyAuthenticated();


        http.httpBasic().and().userDetailsService(userDetailsService);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
