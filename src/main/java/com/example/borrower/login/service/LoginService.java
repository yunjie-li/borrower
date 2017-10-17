package com.example.borrower.login.service;

import com.example.borrower.annotation.DynamicDataSource;
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

    @DynamicDataSource(name = "datasource")
    public List<UserResponse> login() {
        return loginMapper.login();
    }

    @DynamicDataSource()
    public List<UserResponse> loginRobin() {
        return loginMapper.login();
    }

    @DynamicDataSource(name = "abc_datasource")
    @Transactional(rollbackFor = Exception.class)
    public Boolean register() {
        loginMapper.update();
        return loginMapper.register();
    }

}
