package com.example.borrower.config;

import com.example.borrower.constants.CommonConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
public class DynamicDataSourceContextHolder {

    public static final ThreadLocal<String> CURRENT_DATA_SOURCE = new ThreadLocal<>();

    //所有读数据源的名称，会有简单的负载均衡
    public static List<String> SLAVE_DATASOURCE_NAMES = Lists.newArrayList();

    //所有其他数据源，使用时需指定数据源名称
    public static Map<String, String> APPOINT_DATASOURCE_NAMES = Maps.newHashMap();

    private static AtomicInteger COUNT = new AtomicInteger();

    public static void clear() {
        CURRENT_DATA_SOURCE.set(CommonConstants.DEFAULT_DATASOURCE_NAME);
    }

    public static void initCustomerDataSource(Set<String> customerDataSourceNames) {
        customerDataSourceNames.forEach((customerDataSourceName) -> {
            APPOINT_DATASOURCE_NAMES.put(customerDataSourceName, customerDataSourceName);
            if (StringUtils.startsWith(customerDataSourceName, CommonConstants.SLAVE_DATASOURCE_NAME_PREFIX)) {
                SLAVE_DATASOURCE_NAMES.add(customerDataSourceName);
            }
        });
    }

    public static String dataSource() {
        return CURRENT_DATA_SOURCE.get();
    }

    private static String RobinSlaveDataSource() {
        return SLAVE_DATASOURCE_NAMES.get(COUNT.getAndAdd(1) % SLAVE_DATASOURCE_NAMES.size());
    }

    public static void initDefaultDataSource() {
        CURRENT_DATA_SOURCE.set(CommonConstants.DEFAULT_DATASOURCE_NAME);
        APPOINT_DATASOURCE_NAMES.put(CommonConstants.DEFAULT_DATASOURCE_NAME, CommonConstants.DEFAULT_DATASOURCE_NAME);
    }

    public static void setDataSource(String dataSourceName) {
        validateDataSourceName(dataSourceName);
        if (StringUtils.isNotBlank(dataSourceName)) {
            CURRENT_DATA_SOURCE.set(APPOINT_DATASOURCE_NAMES.get(dataSourceName));
            return;
        }
        CURRENT_DATA_SOURCE.set(RobinSlaveDataSource());
    }

    private static void validateDataSourceName(String dataSourceName) {
        if (StringUtils.isNotBlank(dataSourceName) && (APPOINT_DATASOURCE_NAMES.get(dataSourceName) == null)) {
            throw new RuntimeException("dataSource:" + dataSourceName + " not exist!");
        }
    }

}
