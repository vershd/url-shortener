package ru.vershd.urlshortener.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.vershd.urlshortener.dao.AccountDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUrlController {

    private static final String DEFAULT_ACCOUNT_NAME = "Account";
    private static final String DEFAULT_ACCOUNT_PASSWORD = "Password";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountDao accountDao;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Transactional
    public void testRegisterWithWrongMethod() throws Exception {
        mockMvc.perform(put("/register/")
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    @WithMockUser(username = DEFAULT_ACCOUNT_NAME)
    public void testRegister() throws Exception {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);
        mockMvc.perform(post("/register/")
                .content("{\"url\":\"http://google.com\",\"redirectType\":301}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        boolean exists = accountDao.existsByName(DEFAULT_ACCOUNT_NAME);
        Assert.assertTrue("Account not created", exists);
    }

    @Test
    @Transactional
    @WithMockUser(username = DEFAULT_ACCOUNT_NAME)
    public void testRegisterDuplicate() throws Exception {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);
        mockMvc.perform(post("/register/")
                .content("{\"url\":\"http://google.com\",\"redirectType\":301}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/register/")
                .content("{\"url\":\"http://google.com\",\"redirectType\":302}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @Transactional
    @WithMockUser(username = DEFAULT_ACCOUNT_NAME)
    public void testRedirect() throws Exception {
        accountDao.create(DEFAULT_ACCOUNT_NAME, DEFAULT_ACCOUNT_PASSWORD);
        ResultActions actions = mockMvc.perform(post("/register/")
                .content("{\"url\":\"http://google.com\",\"redirectType\":301}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String result = actions.andReturn().getResponse().getContentAsString();
        String shortUrl = result.split("\":\"")[1];
        shortUrl = shortUrl.substring(shortUrl.lastIndexOf("/"), shortUrl.lastIndexOf("\""));
        mockMvc.perform(get(shortUrl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMovedPermanently());
    }
}
