package com.example.borrower;

import com.example.borrower.config.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@SpringBootApplication
@MapperScan("com.example.borrower.*.mapper")
@Import({DynamicDataSourceRegister.class}) // 注册动态多数据源
public class BorrowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BorrowerApplication.class, args);
    }

}
