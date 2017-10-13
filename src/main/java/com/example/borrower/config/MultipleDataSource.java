package com.example.borrower.config;

import com.example.borrower.constants.CommonConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@AutoConfigureAfter(DataSourceConfig.class)
@Slf4j
public class MultipleDataSource implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private static final String DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";

    private Map<String, Map<String, Object>> dataSourceMap = Maps.newHashMap();

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try {
            if (dataSourceMap.isEmpty()) {
                return;
            }
            for (Map.Entry<String, Map<String, Object>> entry : dataSourceMap.entrySet()) {
                Object type = entry.getValue().get("type");
                if (type == null) {
                    type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
                }
                registerBean(beanDefinitionRegistry, entry.getKey(), Class.forName(type.toString()));
            }
        } catch (ClassNotFoundException e) {
            log.error("construct data source type exception:", e);
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
        configurableListableBeanFactory.getBeanDefinition("masterDataSource").setPrimary(true);
        BeanDefinition beanDefinition;
        Map<String, Object> dataSource;
        for (Map.Entry<String, Map<String, Object>> entry : dataSourceMap.entrySet()) {
            beanDefinition = configurableListableBeanFactory.getBeanDefinition(entry.getKey());
            MutablePropertyValues mpv = beanDefinition.getPropertyValues();
            dataSource = entry.getValue();
            mpv.addPropertyValue("driverClassName", dataSource.get("driverClassName"));
            mpv.addPropertyValue("url", dataSource.get("url"));
            mpv.addPropertyValue("username", dataSource.get("username"));
            mpv.addPropertyValue("password", dataSource.get("password"));
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, CommonConstants.SLAVE_DATASOURCE_PREFIX);
        if (propertyResolver.getProperty(CommonConstants.SLAVE_DATASOURCE_NAMES) == null) {
            return;
        }
        DbContextHolder.HAS_SLAVE_DATA_SOURCE = true;
        List<String> slaveDataSourceNames = Arrays.asList(propertyResolver.getProperty(CommonConstants.SLAVE_DATASOURCE_NAMES).replace(" ", "").split(CommonConstants.COMMA_SEPARATOR));
        DbContextHolder.slaveDataSourceNames = slaveDataSourceNames;
        slaveDataSourceNames.forEach((slaveDataSourceName) -> {
            Map<String, Object> dsMap = propertyResolver.getSubProperties(slaveDataSourceName + CommonConstants.POINT_SEPARATOR);
            dataSourceMap.put(slaveDataSourceName, dsMap);
        });
    }

}
