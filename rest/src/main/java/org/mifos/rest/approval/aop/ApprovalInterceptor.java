/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.rest.approval.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ApprovalInterceptor implements MethodInterceptor {

    @Autowired
    MethodInvocationHandler invocationHandler;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if(invocation.getMethod().isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = (RequestMapping) invocation.getMethod().getAnnotations()[0];
            if(requestMapping.method()[0] != RequestMethod.GET) {
                return invocationHandler.process(invocation);
            }
        }
        return invocation.proceed();
    }
}
