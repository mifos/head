package org.mifos.application.servicefacade;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

public class ApplicationContextProvider implements FactoryBean<ApplicationContextProvider> {

    private static ApplicationContextProvider applicationContextProvider;
    private static ApplicationContextHolder applicationContextHolder;

    public static ApplicationContextProvider getInstance() {
        if(applicationContextProvider == null) {
            applicationContextProvider = new ApplicationContextProvider();
        }
        return applicationContextProvider;
    }

    public void setApplicationContextHolder(ApplicationContextHolder applicationContextHolder) {
        ApplicationContextProvider.applicationContextHolder = applicationContextHolder;
    }

    public static <T> T getBean(Class<T> clazz) {
        return ApplicationContextProvider.applicationContextHolder.getApplicationContext().getBean(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return ApplicationContextProvider.applicationContextHolder.getApplicationContext();
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