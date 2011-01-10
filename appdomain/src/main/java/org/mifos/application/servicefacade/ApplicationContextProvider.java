package org.mifos.application.servicefacade;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware, FactoryBean<ApplicationContextProvider> {

    private static ApplicationContextProvider applicationContextProvider;

    public static ApplicationContextProvider getInstance() {
        if(applicationContextProvider == null) {
            applicationContextProvider = new ApplicationContextProvider();
        }
        return applicationContextProvider;
    }

    private static ApplicationContext ctx = null;

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = ctx;
    }

    public static <T> T getBean(Class<T> clazz) {
        return getInstance().getApplicationContext().getBean(clazz);
    }

    @Override
    public ApplicationContextProvider getObject() throws Exception {
        return getInstance();
    }

    @Override
    public Class<ApplicationContextProvider> getObjectType() {
        return ApplicationContextProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}