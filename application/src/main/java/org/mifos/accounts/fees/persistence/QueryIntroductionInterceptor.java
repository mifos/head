package org.mifos.accounts.fees.persistence;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;


public class QueryIntroductionInterceptor extends DelegatingIntroductionInterceptor {

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (mi.getThis() instanceof QueryExecutor) {
            QueryExecutor qryExec = (QueryExecutor) mi.getThis();
            String methodName = mi.getMethod().getName();
            //TODO: introduce a @noIntercept() annotation, and check with mi.getMethod().getAnnotations()??
            if(methodName.startsWith("find") || methodName.startsWith("retrieve")) {
                Object[] arguments = mi.getArguments();
                return qryExec.execFindQuery(methodName, arguments);
            }
        }
        return super.invoke(mi);
    }
}
