package com.example.borrower.login.web.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Created by yunjie on 17-6-27.
 */

@Data
@Builder
public class UserResponse {

    private String account;

    private String name;

    private String cellphone;

    private String email;

    private String passwordHash;

}
