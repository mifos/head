package org.mifos.platform.persistence;

import org.aopalliance.aop.Advice;
import org.springframework.aop.support.DefaultIntroductionAdvisor;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class QueryIntroductionAdvisor extends DefaultIntroductionAdvisor {
    private static final long serialVersionUID = 1L;
    public QueryIntroductionAdvisor(Advice advice) {
        super(advice);
    }


    /**
     * TODO: As long as I remember, this used to be a MUST.
     * Especially when using a ProxyFactoryBean and using <AOP:config>
     * refer to Spring AOP documentation chapter
     */
    @Override
    @SuppressWarnings({"PMD.OnlyOneReturn","PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public boolean matches(Class clazz) {

        // FIXME - keithw - quick hack at reducing scope of advice to avoid matching all classes
        if (clazz.getName().startsWith("java.")) {
            return false;
        }

        if (clazz.getName().startsWith("com.") && !clazz.getName().startsWith("com.sun.proxy.")) {
            return false;
        }

        if (clazz.getName().startsWith("org.spring")) {
            return false;
        }

        if (clazz.getName().startsWith("org.hibernate")) {
            return false;
        }

        if (clazz.getName().startsWith("org.mifos.platform.") && clazz.getName().contains("persistence")) {
            return true;
        }

        if (clazz.getName().startsWith("org.mifos.") && clazz.getName().contains("persistence.Fee")) {
            return false;
        }

        if (clazz.getName().startsWith("org.mifos.")) {
            return false;
        }

        return true;
    }
}
