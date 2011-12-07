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

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.MethodArgHolder;
import org.mifos.rest.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class ApprovalMethodInvocationHandler implements MethodInvocationHandler {

	@Autowired
    ApprovalService approvalService;

    @Override
    public Object process(MethodInvocation invocation) throws Throwable {
    	Method m = invocation.getMethod();
        RequestMapping mapping = m.getAnnotation(RequestMapping.class);

        if(isReadOnly(mapping)) {
            return invocation.proceed();
        }

        Object[] argValues = invocation.getArguments();
        Class<?>[] argTypes = m.getParameterTypes();
        String methodName = m.getName();
        Class<?> methodClassType = m.getDeclaringClass();

        MethodArgHolder args = new MethodArgHolder(argTypes, argValues);
        ApprovalMethod method = new ApprovalMethod(methodName, methodClassType, args);
        approvalService.create(method);
        return invocation.proceed();
    }

	private boolean isReadOnly(RequestMapping mapping) {
		return mapping.method().length == 1 && mapping.method()[0] == RequestMethod.GET;
	}

}
