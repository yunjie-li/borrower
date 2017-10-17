package com.example.borrower.login.web;

import com.example.borrower.login.service.LoginService;
import com.example.borrower.login.web.domain.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yunjie on 17-6-23.
 */

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping(value = "/login_robin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserResponse> loginRobin() {
        List<UserResponse> users = loginService.loginRobin();
        log.info("user : {} ", users);
        return users;
    }

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserResponse> login() {
        List<UserResponse> users = loginService.login();
        log.info("user : {} ", users);
        return users;
    }

    @GetMapping(value = "/register", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Boolean register() {
        try {
            return loginService.register();
        } catch (Exception e) {
            log.error("exception:", e);
        }
        return Boolean.TRUE;
    }

}
