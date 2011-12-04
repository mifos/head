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
package org.mifos.platform.rest.approval.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mifos.platform.rest.approval.domain.ApprovalMethod;
import org.mifos.platform.rest.approval.domain.MethodArgHolder;
import org.mifos.platform.rest.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Aspect
public class AspectJRESTApprovalInterceptor {

    @Autowired
    ApprovalService approvalService;

    @Pointcut("execution(public * org.mifos.platform.rest.controller.*RESTController.*(..))")
    public void restMethods() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {
    }

    @Around("restMethods() && requestMapping()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getStaticPart().getSignature();
        if(signature instanceof MethodSignature){

            MethodSignature ms = (MethodSignature) signature;
            Class<?>[] types = ms.getParameterTypes();
            RequestMapping mapping = ms.getMethod().getAnnotation(RequestMapping.class);

            if(mapping.method().length == 1 && mapping.method()[0] == RequestMethod.GET) {
                return pjp.proceed();
            }

            Object[] values = pjp.getArgs();
            MethodArgHolder argsHolder = new MethodArgHolder();
            argsHolder.setTypes(types);
            argsHolder.setValues(values);

            ApprovalMethod method = new ApprovalMethod();
            method.setArgsHolder(argsHolder);
            method.setName(ms.getMethod().getName());
            method.setType(ms.getDeclaringType());

            approvalService.create(method);
        }
        return null;
    }

}