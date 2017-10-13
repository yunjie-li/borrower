package com.example.borrower;

import com.example.borrower.login.service.LoginService;
import com.example.borrower.login.web.domain.UserResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BorrowerApplication.class)
@WebAppConfiguration
public class BorrowerApplicationTests {

    @Autowired
    private LoginService loginService;

    @Test
    public void contextLoads() {
        List<UserResponse> userResponse = loginService.login();
        System.out.println("users:" + userResponse);
    }

}
