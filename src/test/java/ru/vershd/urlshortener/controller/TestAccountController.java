package ru.vershd.urlshortener.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.vershd.urlshortener.dao.AccountDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAccountController {

    private static final String DEFAULT_ACCOUNT_NAME = "Account";

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
        mockMvc.perform(put("/account/")
                .content("")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    public void testRegister() throws Exception {
        mockMvc.perform(post("/account/")
                .content("{\"AccountId\":\"" + DEFAULT_ACCOUNT_NAME + "\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        boolean exists = accountDao.existsByName(DEFAULT_ACCOUNT_NAME);
        Assert.assertTrue("Account not created", exists);
    }

    @Test
    @Transactional
    public void testRegisterWithEmptyName() throws Exception {
        mockMvc.perform(post("/account/")
                .content("{\"AccountId\":\"\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testRegisterDuplicate() throws Exception {
        mockMvc.perform(post("/account/")
                .content("{\"AccountId\":\"" + DEFAULT_ACCOUNT_NAME + "\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/account/")
                .content("{\"AccountId\":\"" + DEFAULT_ACCOUNT_NAME + "\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
