package com.example.borrower.login.mapper;

import com.example.borrower.login.web.domain.UserResponse;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * Created by yunjie on 17-6-27.
 */
public interface LoginMapper {

    @Select("select account, name, cellphone, email, password_hash from user.UserLogin where id=1")
    @Results
            ({
                    @Result(property = "account", column = "account"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "cellphone", column = "cellphone"),
                    @Result(property = "passwordHash", column = "password_hash")
            })
    UserResponse login();

}
