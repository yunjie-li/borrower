package com.example.borrower.interceptor;

import com.example.borrower.annotation.SlaveConnection;
import com.example.borrower.config.DbContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReadOnlyConnectionInterceptor implements Ordered {

    @Around("@annotation(slaveConnection)")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, SlaveConnection slaveConnection) throws Throwable {
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

    @Override
    public int getOrder() {
        return 0;
    }

}
