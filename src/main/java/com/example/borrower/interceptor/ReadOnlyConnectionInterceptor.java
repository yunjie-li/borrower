package com.example.borrower.interceptor;

import com.example.borrower.config.DbContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ReadOnlyConnectionInterceptor {

    @Around("slaveConnectionAnnotation()")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            log.info("set database connection to read only");
            DbContextHolder.setDbType(DbContextHolder.DbType.SLAVE);
            Object result = proceedingJoinPoint.proceed();
            return result;
        } finally {
            DbContextHolder.clearDbType();
            log.info("restore database connection");
        }
    }

    @Pointcut("@annotation(com.example.borrower.annotation.SlaveConnection)")
    public void slaveConnectionAnnotation() {

    }

}
