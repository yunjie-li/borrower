package com.example.borrower;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.borrower.*.mapper")
public class BorrowerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BorrowerApplication.class, args);
	}

}
