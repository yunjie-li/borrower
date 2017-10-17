package com.example.borrower.config;

import com.example.borrower.constants.CommonConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@Configuration
@Slf4j
public class MultipleDataSource implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private static final String DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";

    private static final String SLAVE_DATASOURCE_NAMES = "names";

    private static final String DATASOURCE_TYPE_VALUE = "type";

    private static final String DATASOURCE_DRIVER_CLASSNAME_VALUE = "driverClassName";

    private static final String DATASOURCE_URL_VALUE = "url";

    private static final String DATASOURCE_USERNAME_VALUE = "username";

    private static final String DATASOURCE_PASSWORD_VALUE = "password";

    private Map<String, Object> DATASOURCE_PROPERTIES = Maps.newHashMap();

    private Map<String, Map<String, Object>> dataSourceMap = Maps.newHashMap();

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if (MapUtils.isNotEmpty(dataSourceMap)) {
            dataSourceMap.forEach((slaveDataSourceName, dataSourceProperty) -> {
                Object type = dataSourceProperty.get(DATASOURCE_TYPE_VALUE);
                if (type == null) {
                    type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
                }
                try {
                    registerBean(beanDefinitionRegistry, slaveDataSourceName, Class.forName(type.toString()));
                } catch (ClassNotFoundException e) {
                    log.error("register slave datasource exception:", e);
                }
                DbContextHolder.SLAVE_DATASOURCE_NAMES.add(slaveDataSourceName);
            });
        }
    }

    /**
     * 注册Bean到Spring
     *
     * @param registry
     * @param name
     * @param beanClass
     */
    private void registerBean(BeanDefinitionRegistry registry, String name, Class<?> beanClass) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        // 可以自动生成name
        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, registry));
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        if (MapUtils.isNotEmpty(dataSourceMap)) {
            dataSourceMap.forEach((slaveDataSourceName, dataSourceProperty) -> {
                BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(slaveDataSourceName);
                MutablePropertyValues mpv = beanDefinition.getPropertyValues();
                Map<String, Object> values = Maps.newHashMap(DATASOURCE_PROPERTIES);
                values.remove(DATASOURCE_TYPE_VALUE);
                values.put(DATASOURCE_DRIVER_CLASSNAME_VALUE, dataSourceProperty.get(DATASOURCE_DRIVER_CLASSNAME_VALUE));
                values.put(DATASOURCE_URL_VALUE, dataSourceProperty.get(DATASOURCE_URL_VALUE));
                values.put(DATASOURCE_USERNAME_VALUE, dataSourceProperty.get(DATASOURCE_USERNAME_VALUE));
                values.put(DATASOURCE_PASSWORD_VALUE, dataSourceProperty.get(DATASOURCE_PASSWORD_VALUE));
                mpv.addPropertyValues(values);
            });
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, CommonConstants.SLAVE_DATASOURCE_PREFIX + CommonConstants.POINT_SEPARATOR);
        if (propertyResolver.getProperty(SLAVE_DATASOURCE_NAMES) != null) {
            DATASOURCE_PROPERTIES = new RelaxedPropertyResolver(environment, CommonConstants.SPRING_DATASOURCE_PREFIX).getSubProperties(CommonConstants.POINT_SEPARATOR);
            List<String> slaveDataSourceNames = Arrays.asList(propertyResolver.getProperty(SLAVE_DATASOURCE_NAMES).replace(" ", "").split(CommonConstants.COMMA_SEPARATOR));
            if (CollectionUtils.isNotEmpty(slaveDataSourceNames)) {
                slaveDataSourceNames.forEach((slaveDataSourceName) ->
                        dataSourceMap.put(slaveDataSourceName, propertyResolver.getSubProperties(CommonConstants.POINT_SEPARATOR + slaveDataSourceName + CommonConstants.POINT_SEPARATOR))
                );
            }
        }
    }

}
