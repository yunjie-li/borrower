package com.example.borrower.config;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DbContextHolder {

    private static final ThreadLocal<String> slaveDataSourceName = new ThreadLocal<>();

    public static List<String> slaveDataSourceNames = null;

    public static boolean HAS_SLAVE_DATA_SOURCE = false;

    public static boolean MASTER = true;

    private static AtomicInteger count = new AtomicInteger();

    public static void setSlaveDataSourceName() {
        if (HAS_SLAVE_DATA_SOURCE) {
            slaveDataSourceName.set(slaveDataSourceNames.get(count.getAndAdd(1) % slaveDataSourceNames.size()));
        }
    }

    public static String getSlaveDataSourceName() {
        return slaveDataSourceName.get();
    }

    public static void clear() {
        slaveDataSourceName.remove();
    }

}
