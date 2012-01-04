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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.MethodArgHolder;
import org.mifos.rest.approval.service.ApprovalService;
import org.mifos.rest.config.RESTConfigKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Spring AspectJ Around advice intercept any REST call which is not GET (read-only) <br><br>
 * (Wanted to use Spring AOP {@link MethodInvocationInterceptor}) but it’s not working for
 * some reason though we have {@link ApprovalInterceptor} and its tests and it shows pointcut
 * in Springsource STS, but at runtime it’s not working). The downside of using AspectJ based intercepter is
 * the dependency on AJDT tooling and m2e aspectj-maven support would be required for
 * REST module which makes development environment a little heavier.
 *
 * @author ugupta
 */
@Aspect
public class AspectJRESTApprovalInterceptor {

	public static final String REST_METHOD = "execution(public * org.mifos.platform.rest.controller..*RESTController.*(..))";

	public static final String REQUEST_MAPPING = "@annotation(org.springframework.web.bind.annotation.RequestMapping)";

	public static final String EXCLUDE_APPROVAL_API = "!execution(public * org.mifos.platform.rest.controller.ApprovalRESTController.*(..))";

	public static final Logger LOG = LoggerFactory.getLogger(AspectJRESTApprovalInterceptor.class);

	@Autowired
	private ParameterNameDiscoverer parameterNameDiscoverer;

	@Autowired
    ApprovalService approvalService;

	@Autowired
	ConfigurationServiceFacade configurationServiceFacade;

    @Pointcut(REST_METHOD)
    public void restMethods() {}

    @Pointcut(EXCLUDE_APPROVAL_API)
    public void excludeAPI() {}

    @Pointcut(REQUEST_MAPPING)
    public void requestMapping() {}

    @Around("restMethods() && requestMapping() && excludeAPI()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getStaticPart().getSignature();
        LOG.debug(this.getClass().getSimpleName() + " staring");

        // FIXME : somehow autowiring is not working
        if (approvalService == null) {approvalService = ApplicationContextProvider.getBean(ApprovalService.class);}
        if (configurationServiceFacade == null) {configurationServiceFacade = ApplicationContextProvider.getBean(ConfigurationServiceFacade.class);}
        if (parameterNameDiscoverer == null) {parameterNameDiscoverer = ApplicationContextProvider.getBean(ParameterNameDiscoverer.class);}

        if(!RESTConfigKey.isApprovalRequired(configurationServiceFacade)) {
            LOG.debug(pjp.getSignature() + " skip approval");
            return pjp.proceed();
        }

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
			String[] names = parameterNameDiscoverer.getParameterNames(m);

	        MethodArgHolder args = new MethodArgHolder(argTypes, argValues, names);
			ApprovalMethod method = new ApprovalMethod(methodName, methodClassType, args);
			approvalService.create(method);
		}
        return pjp.proceed();
    }

	private boolean isReadOnly(RequestMapping mapping) {
		return mapping.method().length != 1 || mapping.method()[0] == RequestMethod.GET;
	}

}