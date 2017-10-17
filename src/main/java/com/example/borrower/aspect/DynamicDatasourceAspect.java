package com.example.borrower.aspect;

import com.example.borrower.annotation.DynamicDataSource;
import com.example.borrower.config.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@Slf4j
@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component
public class DynamicDatasourceAspect {

    @Around("slaveConnectionAnnotation(dynamicDataSource)")
    public Object proceed(ProceedingJoinPoint proceedingJoinPoint, DynamicDataSource dynamicDataSource) throws Throwable {
        try {
            DynamicDataSourceContextHolder.setDataSource(dynamicDataSource.name());
            return proceedingJoinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }

    @Pointcut("@annotation(dynamicDataSource)")
    public void slaveConnectionAnnotation(DynamicDataSource dynamicDataSource) {

    }

}
