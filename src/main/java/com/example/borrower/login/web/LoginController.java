package com.example.borrower.login.web;

import com.example.borrower.login.service.LoginService;
import com.example.borrower.login.web.domain.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yunjie on 17-6-23.
 */

@RestController
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserResponse login() {
        UserResponse userResponse = loginService.login();
        log.info("user : ", userResponse);
        return userResponse;
    }

}
