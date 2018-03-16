package com.infly.common.util.quartz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JobStartListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {

    try {
      Scheduler scheduler = (Scheduler) getContext().getBean("jobFactory");
      scheduler.start();
    } catch (SchedulerException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
  
  private static ApplicationContext ctx = null;

  public static ApplicationContext getContext() {
    if (ctx == null) {
      ctx = new ClassPathXmlApplicationContext(
          new String[] {"classpath:applicationContext-Spring-data-redis.xml",
              "classpath:b2bplatform-applicationContext-cache.xml"});
    }
    return ctx;
  }

}

