package com.example.borrower.config;

import com.example.borrower.constants.CommonConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@Configuration
@Slf4j
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final String DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";

    private static final String CUSTOMER_DATASOURCE_NAMES = "names";

    private static final String DATASOURCE_TYPE_VALUE = "type";

    private static final String DATASOURCE_DRIVER_CLASSNAME_VALUE = "driverClassName";

    private static final String DATASOURCE_URL_VALUE = "url";

    private static final String DATASOURCE_USERNAME_VALUE = "username";

    private static final String DATASOURCE_PASSWORD_VALUE = "password";

    private static final String DEFAULT_TARGET_DATASOURCE = "defaultTargetDataSource";

    private static final String TARGET_DATASOURCE = "targetDataSources";

    private DataSource defaultDataSource;

    private ConversionService conversionService = new DefaultConversionService();

    private PropertyValues dataSourcePropertyValues;

    private Map<String, DataSource> customDataSources = Maps.newHashMap();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<String, Object> targetDataSources = Maps.newHashMap();
        // 将主数据源添加到更多数据源中
        targetDataSources.put(CommonConstants.DEFAULT_DATASOURCE_NAME, defaultDataSource);
        DynamicDataSourceContextHolder.initDefaultDataSource();
        // 添加更多数据源
        targetDataSources.putAll(customDataSources);
        DynamicDataSourceContextHolder.initCustomerDataSource(customDataSources.keySet());
        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSourceRouting.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue(DEFAULT_TARGET_DATASOURCE, defaultDataSource);
        mpv.addPropertyValue(TARGET_DATASOURCE, targetDataSources);
        beanDefinitionRegistry.registerBeanDefinition(CommonConstants.DEFAULT_DATASOURCE_NAME, beanDefinition);
    }

    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initCustomDataSources(environment);
    }

    private void initDefaultDataSource(Environment environment) {
        // 读取主数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, CommonConstants.SPRING_DATASOURCE_PREFIX + CommonConstants.POINT_SEPARATOR);
        Map<String, Object> defaultDataSourceProperty = Maps.newHashMap();
        defaultDataSourceProperty.put(DATASOURCE_TYPE_VALUE, propertyResolver.getProperty(DATASOURCE_TYPE_VALUE));
        defaultDataSourceProperty.put(DATASOURCE_DRIVER_CLASSNAME_VALUE, propertyResolver.getProperty(DATASOURCE_DRIVER_CLASSNAME_VALUE));
        defaultDataSourceProperty.put(DATASOURCE_URL_VALUE, propertyResolver.getProperty(DATASOURCE_URL_VALUE));
        defaultDataSourceProperty.put(DATASOURCE_USERNAME_VALUE, propertyResolver.getProperty(DATASOURCE_USERNAME_VALUE));
        defaultDataSourceProperty.put(DATASOURCE_PASSWORD_VALUE, propertyResolver.getProperty(DATASOURCE_PASSWORD_VALUE));
        defaultDataSource = buildDataSource(defaultDataSourceProperty);
        dataBinder(defaultDataSource, environment);
    }

    /**
     * 创建DataSource
     *
     * @return
     */
    public DataSource buildDataSource(Map<String, Object> dataSourcePropertyMap) {
        try {
            Object type = dataSourcePropertyMap.get(DATASOURCE_TYPE_VALUE);
            if (type == null) {
                type = DATASOURCE_TYPE_DEFAULT;
            }
            Class<? extends DataSource> dataSourceType;
            dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            return DataSourceBuilder.create().driverClassName(dataSourcePropertyMap.get(DATASOURCE_DRIVER_CLASSNAME_VALUE).toString())
                    .url(dataSourcePropertyMap.get(DATASOURCE_URL_VALUE).toString()).username(dataSourcePropertyMap.get(DATASOURCE_USERNAME_VALUE).toString())
                    .password(dataSourcePropertyMap.get(DATASOURCE_PASSWORD_VALUE).toString()).type(dataSourceType).build();
        } catch (ClassNotFoundException e) {
            log.error("build data source exception:", e);
        }
        return null;
    }

    /**
     * 为DataSource绑定更多数据
     *
     * @param dataSource
     * @param env
     */
    private void dataBinder(DataSource dataSource, Environment env) {
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);
        if (dataSourcePropertyValues == null) {
            Map<String, Object> rpr = new RelaxedPropertyResolver(env, CommonConstants.SPRING_DATASOURCE_PREFIX).getSubProperties(CommonConstants.POINT_SEPARATOR);
            Map<String, Object> values = Maps.newHashMap(rpr);
            // 排除已经设置的属性
            values.remove(DATASOURCE_TYPE_VALUE);
            values.remove(DATASOURCE_DRIVER_CLASSNAME_VALUE);
            values.remove(DATASOURCE_URL_VALUE);
            values.remove(DATASOURCE_URL_VALUE);
            values.remove(DATASOURCE_PASSWORD_VALUE);
            dataSourcePropertyValues = new MutablePropertyValues(values);
        }
        dataBinder.bind(dataSourcePropertyValues);
    }

    /**
     * 初始化更多数据源
     *
     * @author SHANHY
     * @create 2016年1月24日
     */
    private void initCustomDataSources(Environment environment) {
        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, CommonConstants.CUSTOMER_DATASOURCE_PREFIX + CommonConstants.POINT_SEPARATOR);
        if (propertyResolver.getProperty(CUSTOMER_DATASOURCE_NAMES) != null) {
            List<String> slaveDataSourceNames = Arrays.asList(propertyResolver.getProperty(CUSTOMER_DATASOURCE_NAMES).replace(" ", "").split(CommonConstants.COMMA_SEPARATOR));
            slaveDataSourceNames.forEach((customerDataSourceName) -> {
                        Map<String, Object> customerProperty = propertyResolver.getSubProperties(customerDataSourceName + CommonConstants.POINT_SEPARATOR);
                        DataSource customerDataSource = buildDataSource(customerProperty);
                        customDataSources.put(customerDataSourceName, customerDataSource);
                        dataBinder(customerDataSource, environment);
                    }
            );
        }
    }

}
