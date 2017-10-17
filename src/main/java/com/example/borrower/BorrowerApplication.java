package com.example.borrower;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@SpringBootApplication
@MapperScan("com.example.borrower.*.mapper")
public class BorrowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowerApplication.class, args);
    }

}
