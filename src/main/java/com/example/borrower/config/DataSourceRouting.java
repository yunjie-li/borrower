package com.example.borrower.config;

import com.example.borrower.constants.CommonConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author yunjie.
 * @since 17-10-16.
 */
public class DataSourceRouting extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        if (DbContextHolder.MASTER) {
            return CommonConstants.MASTER_DATASOURCE_NAME;
        }
        if (CollectionUtils.isEmpty(DbContextHolder.SLAVE_DATASOURCE_NAMES)) {
            return CommonConstants.MASTER_DATASOURCE_NAME;
        }
        return DbContextHolder.getSlaveDataSourceName();
    }
}
