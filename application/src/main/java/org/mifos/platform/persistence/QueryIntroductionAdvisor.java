package org.mifos.platform.persistence;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

public class QueryIntroductionAdvisor extends DefaultIntroductionAdvisor {

    public QueryIntroductionAdvisor(Advice advice) {
        super(advice);
    }


    /**
     * TODO: As long as I remember, this used to be a MUST.
     * Especially when using a ProxyFactoryBean and using <AOP:config>
     * refer to Spring AOP documentation chapter
     */
    @Override
    public boolean matches(Class clazz) {
        return false; // super.matches(clazz);
    }
}
