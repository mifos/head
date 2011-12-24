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
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;
import org.mifos.platform.rest.controller.ApprovalRESTController;
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

public class ApprovalMethodInvocationHandler implements MethodInvocationHandler {

    public static final Logger LOG = LoggerFactory.getLogger(ApprovalMethodInvocationHandler.class);

    @Autowired
    private ParameterNameDiscoverer parameterNameDiscoverer;

    @Autowired
    ApprovalService approvalService;

    @Autowired
    ConfigurationServiceFacade configurationServiceFacade;

    @Override
    public Object process(MethodInvocation invocation) throws Throwable {
        LOG.debug(this.getClass().getSimpleName() + " staring");

        // FIXME : somehow autowiring is not working
        if (approvalService == null) {approvalService = ApplicationContextProvider.getBean(ApprovalService.class);}
        if (configurationServiceFacade == null) {configurationServiceFacade = ApplicationContextProvider.getBean(ConfigurationServiceFacade.class);}

        if(!RESTConfigKey.isApprovalRequired(configurationServiceFacade)) {
            LOG.debug(invocation + " skip approval");
            return invocation.proceed();
        }

            Method m = invocation.getMethod();
            RequestMapping mapping = m.getAnnotation(RequestMapping.class);

            if (isReadOnly(mapping)) {
                LOG.debug(m.getName() + " is read only, hence returning control");
                return invocation.proceed();
            }

            Class<?> methodClassType = m.getDeclaringClass();

            if(!methodClassType.getSimpleName().endsWith("RESTController")) {
                LOG.debug(m.getName() + " is not from REST controller, hence returning control");
                return invocation.proceed();
            }

            if(methodClassType.equals(ApprovalRESTController.class)) {
                LOG.debug(m.getName() + " is from Approval REST controller, hence returning control");
                return invocation.proceed();
            }

            Object[] argValues = invocation.getArguments();

            Class<?>[] argTypes = m.getParameterTypes();
            String methodName = m.getName();
            String[] names = parameterNameDiscoverer.getParameterNames(m);

            MethodArgHolder args = new MethodArgHolder(argTypes, argValues, names);
            ApprovalMethod method = new ApprovalMethod(methodName, methodClassType, args);
            approvalService.create(method);

        return invocation.proceed();
    }

	private boolean isReadOnly(RequestMapping mapping) {
		return mapping.method().length == 1 && mapping.method()[0] == RequestMethod.GET;
	}

}
