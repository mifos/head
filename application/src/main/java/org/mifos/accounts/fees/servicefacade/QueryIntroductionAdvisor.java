package org.mifos.accounts.fees.servicefacade;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

public class QueryIntroductionAdvisor extends DefaultIntroductionAdvisor {

    public QueryIntroductionAdvisor(Advice advice) {
        super(advice);
        //super(new QueryIntroductionInterceptor());
        // TODO Auto-generated constructor stub
    }


    /**
     * TODO: As long as I remember, this used to be a MUST.
     * Especially when using a ProxyFactoryBean and using <AOP:config>
     * refer to Spring AOP documentation chapter
     *
     */
    @Override
    public boolean matches(Class clazz) {
        return super.matches(clazz);
    }
}
