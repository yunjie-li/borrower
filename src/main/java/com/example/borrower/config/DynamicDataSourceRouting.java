package com.example.borrower.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author yunjie.
 * @since 17-10-16.
 */
public class DynamicDataSourceRouting extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.dataSource();
    }

}
