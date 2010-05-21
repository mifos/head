package org.mifos.accounts.fees.servicefacade;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MifosApplicationContext implements ApplicationContextAware {

    private static ApplicationContext ctx = null;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static Object getBean(String beanName) {
        return (ctx != null) ?
            ctx.getBean(beanName) : null;
    }
}
