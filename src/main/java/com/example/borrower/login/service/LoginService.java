package com.example.borrower.login.service;

import com.example.borrower.annotation.SlaveDataSource;
import com.example.borrower.login.mapper.LoginMapper;
import com.example.borrower.login.web.domain.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yunjie on 17-6-27.
 */
@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @SlaveDataSource
    public List<UserResponse> login() {
        return loginMapper.login();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean register() {
        loginMapper.update();
        return loginMapper.register();
    }

}
