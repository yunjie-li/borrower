package com.example.borrower.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author yunjie.
 * @since 17-10-13.
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public Object getBean(String name) {
    return applicationContext.getBean(name);
  }

}
