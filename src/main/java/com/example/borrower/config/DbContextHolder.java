package com.example.borrower.config;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
public class DbContextHolder {

    private static final ThreadLocal<String> SLAVE_DATA_SOURCE = new ThreadLocal<>();

    public static List<String> SLAVE_DATASOURCE_NAMES = Lists.newArrayList();

    public static boolean MASTER = true;

    private static AtomicInteger count = new AtomicInteger();

    public static void setSlaveDataSource() {
        if (CollectionUtils.isNotEmpty(SLAVE_DATASOURCE_NAMES)) {
            SLAVE_DATA_SOURCE.set(SLAVE_DATASOURCE_NAMES.get(count.getAndAdd(1) % SLAVE_DATASOURCE_NAMES.size()));
        }
    }

    public static String getSlaveDataSourceName() {
        return SLAVE_DATA_SOURCE.get();
    }

    public static void clear() {
        MASTER = true;
        SLAVE_DATA_SOURCE.remove();
    }

}
