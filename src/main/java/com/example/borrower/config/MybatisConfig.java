package com.example.borrower.config;

import com.example.borrower.constants.CommonConstants;
import com.example.borrower.utils.SpringContextUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@AutoConfigureAfter(MultipleDataSource.class)
@Configuration
public class MybatisConfig extends MybatisAutoConfiguration {

    @Resource(name = CommonConstants.MASTER_DATASOURCE_NAME)
    private DataSource masterDataSource;

    @Autowired
    private SpringContextUtil springContextUtil;


    public MybatisConfig(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader,
                         ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }

    @Bean(name = "roundRobinDataSourceProxy")
    public DataSourceRouting roundRobinDataSourceProxy() {
        Map<Object, Object> targetDataResources = Maps.newHashMap();
        targetDataResources.put(CommonConstants.MASTER_DATASOURCE_NAME, masterDataSource);
        if (CollectionUtils.isNotEmpty(DbContextHolder.SLAVE_DATASOURCE_NAMES)) {
            List<String> slaveDataSourceNames = DbContextHolder.SLAVE_DATASOURCE_NAMES;
            slaveDataSourceNames.forEach((slaveDataSourceName) -> {
                DataSource slaveDataSource = (DataSource) springContextUtil.getBean(slaveDataSourceName);
                targetDataResources.put(slaveDataSourceName, slaveDataSource);
            });
        }
        DataSourceRouting proxy = new DataSourceRouting();
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
