/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

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
