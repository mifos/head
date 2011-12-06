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

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.platform.rest.approval.domain.ApprovalMethod;
import org.mifos.platform.rest.approval.service.ApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Aspect
public class AspectJRESTApprovalInterceptor {

	public static final String REST_METHOD = "execution(public * *.*(..))";
	
	public static final String REQUEST_MAPPING = "@annotation(org.springframework.web.bind.annotation.RequestMapping)";
	
	public static final Logger LOG = LoggerFactory.getLogger(AspectJRESTApprovalInterceptor.class);
	
	@Autowired
    ApprovalService approvalService;

    @Pointcut(REST_METHOD)
    public void restMethods() {
    }

    @Pointcut(REQUEST_MAPPING)
    public void requestMapping() {
    }

    // FIXME : Spring STS has bug which does not allow commented way
    //@Around(REST_METHOD + " && " + REQUEST_MAPPING)
    @Around("restMethods() && requestMapping()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getStaticPart().getSignature();
        LOG.debug(this.getClass().getSimpleName() + " staring");
		
        if (signature instanceof MethodSignature) {
			MethodSignature ms = (MethodSignature) signature;
			Method m = ms.getMethod();
			RequestMapping mapping = m.getAnnotation(RequestMapping.class);

			if (isReadOnly(mapping)) {
				LOG.debug(m.getName() + " is read only, hence returning control");
				return pjp.proceed();
			}

			Class<?> methodClassType = m.getDeclaringClass();
			
			if(!methodClassType.getSimpleName().endsWith("RESTController")) {
				LOG.debug(m.getName() + " is not from REST controller, hence returning control");
				return pjp.proceed();
			}
			
			Object[] argValues = pjp.getArgs();

			Class<?>[] argTypes = m.getParameterTypes();
			String methodName = m.getName();

			ApprovalMethod method = new ApprovalMethod(methodName, methodClassType, argTypes, argValues);

			if (approvalService == null) {
				// FIXME : somehow autowiring is not working 
				approvalService = ApplicationContextProvider.getBean(ApprovalService.class);
			}
			approvalService.create(method);
		}
        return pjp.proceed();
    }

	private boolean isReadOnly(RequestMapping mapping) {
		return mapping.method().length != 1 || mapping.method()[0] == RequestMethod.GET;
	}

}