package org.mifos.accounts.fees.persistence;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;


public class QueryIntroductionInterceptor extends DelegatingIntroductionInterceptor {

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        if (mi.getThis() instanceof QueryExecutor) {
            QueryExecutor qryExec = (QueryExecutor) mi.getThis();
            String methodName = mi.getMethod().getName();
            //TODO: introduce a @InceptFindQuery, @noInterceptFindQuery() annotation, and check with mi.getMethod().getAnnotations()??
            if(methodName.startsWith("retrieve")) {
                Object[] arguments = mi.getArguments();
                //mi.getMethod().getGenericReturnType()
                //TODO check if return type is of collection or not. and return accordingly
                return qryExec.execFindQuery(methodName, arguments);
            }
        }
        return super.invoke(mi);
    }
}
