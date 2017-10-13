package com.example.borrower.aspect;

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
public class SlaveDatasourceAspect {

    @Around("slaveConnectionAnnotation()")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            DbContextHolder.MASTER = false;
            DbContextHolder.setSlaveDataSourceName();
            return proceedingJoinPoint.proceed();
        } finally {
            DbContextHolder.clear();
        }
    }

    @Pointcut("@annotation(com.example.borrower.annotation.SlaveDataSource)")
    public void slaveConnectionAnnotation() {

    }

}
