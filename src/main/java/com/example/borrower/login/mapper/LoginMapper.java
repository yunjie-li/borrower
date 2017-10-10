package com.example.borrower.login.mapper;

import com.example.borrower.login.web.domain.UserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Created by yunjie on 17-6-27.
 */
@Repository
public interface LoginMapper {

    @Select("select account, name, cellphone, email from borrower.user where id=1")
    @Results
            ({
                    @Result(property = "account", column = "account"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "cellphone", column = "cellphone"),
            })
    UserResponse login();

}
