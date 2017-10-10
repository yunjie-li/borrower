package com.example.borrower.login.service;

import com.example.borrower.annotation.SlaveConnection;
import com.example.borrower.login.mapper.LoginMapper;
import com.example.borrower.login.web.domain.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yunjie on 17-6-27.
 */
@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @SlaveConnection
    public UserResponse login() {
        return loginMapper.login();
    }

}
