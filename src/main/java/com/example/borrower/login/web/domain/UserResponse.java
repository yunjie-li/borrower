package com.example.borrower.login.web.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by yunjie on 17-6-27.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {

    private String account;

    private String name;

    private String cellphone;

    private String email;

    private String passwordHash;

}
