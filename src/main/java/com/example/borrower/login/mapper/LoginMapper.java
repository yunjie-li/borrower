package com.example.borrower.login.mapper;

import com.example.borrower.login.web.domain.UserResponse;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yunjie on 17-6-27.
 */
@Repository
public interface LoginMapper {

    @Select("select account, name, cellphone, email from user where id=1")
    @Results
            ({
                    @Result(property = "account", column = "account"),
                    @Result(property = "name", column = "name"),
                    @Result(property = "cellphone", column = "cellphone"),
            })
    List<UserResponse> login();

    @Insert("INSERT INTO user (id, account, name, cellphone, email, create_date, update_date) VALUES (1, 'yunjie', '哈哈', '15434245753', '29445346@qq.com', '2017-09-30 18:23:01', '2017-09-30 18:23:01')")
    Boolean register();

    @Update("update user set account = 'yunjie1' where id = 1")
    void update();
}
