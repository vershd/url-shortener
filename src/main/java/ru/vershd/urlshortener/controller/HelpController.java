package ru.vershd.urlshortener.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HelpController {

    @RequestMapping(value = "/help")
    void getHelp(HttpServletResponse response) throws IOException {
        response.sendRedirect("/documentation/swagger-ui.html");
    }
}
