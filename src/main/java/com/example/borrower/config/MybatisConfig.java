package com.example.borrower.config;

import com.example.borrower.utils.SpringContextUtil;
import com.google.common.collect.Maps;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@AutoConfigureAfter(MultipleDataSource.class)
@Configuration
public class MybatisConfig extends MybatisAutoConfiguration {

    @Resource(name = MASTER_DATA_SOURCE_KEY)
    private DataSource masterDataSource;

    @Autowired
    private SpringContextUtil springContextUtil;

    private static final String MASTER_DATA_SOURCE_KEY = "masterDataSource";

    public MybatisConfig(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader,
                         ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }

    @Bean(name = "roundRobinDataSourceProxy")
    public AbstractRoutingDataSource roundRobinDataSourceProxy() {

        Map<Object, Object> targetDataResources = Maps.newHashMap();
        targetDataResources.put(MASTER_DATA_SOURCE_KEY, masterDataSource);

        if (DbContextHolder.HAS_SLAVE_DATA_SOURCE) {
            List<String> slaveDataSourceNames = DbContextHolder.slaveDataSourceNames;
            slaveDataSourceNames.forEach((slaveDataSourceName) -> {
                DataSource slaveDataSource = (DataSource) springContextUtil.getBean(slaveDataSourceName);
                targetDataResources.put(slaveDataSourceName, slaveDataSource);
            });
        }

        AbstractRoutingDataSource proxy = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                if (DbContextHolder.MASTER) {
                    return MASTER_DATA_SOURCE_KEY;
                }
                if (!DbContextHolder.HAS_SLAVE_DATA_SOURCE) {
                    return MASTER_DATA_SOURCE_KEY;
                }
                return DbContextHolder.getSlaveDataSourceName();
            }
        };
        proxy.setDefaultTargetDataSource(masterDataSource);
        proxy.setTargetDataSources(targetDataResources);
        proxy.afterPropertiesSet();
        return proxy;
    }

    @Bean
    @Override
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        return super.sqlSessionFactory(roundRobinDataSourceProxy());
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(roundRobinDataSourceProxy());
    }

}
