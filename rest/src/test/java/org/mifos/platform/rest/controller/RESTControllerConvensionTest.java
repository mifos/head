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
package org.mifos.platform.rest.controller;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import javassist.Modifier;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.platform.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class RESTControllerConvensionTest {

    @Test
    public void testConvension() throws Exception {
        for(Class<?> clazz : ClassUtils.getClasses("org.mifos.platform.rest.controller", "RESTController")) {
            verifyMethods(clazz);
        }
    }

    private void verifyMethods(Class<?> clazz) {
        for(Method method : clazz.getMethods()) {
            if(method.isAnnotationPresent(RequestMapping.class)) {
                String name = clazz.getName() +"."+ method.getName();
                Assert.assertFalse("Method should not be final, cglib Spring AOP can not intercept "
                                  + name, Modifier.isFinal(method.getModifiers()));
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                Assert.assertTrue("Exactly one request mapping method should be used in "+name,
                                  mapping.method().length == 1);
                if(mapping.method()[0] != RequestMethod.GET) {
                	for(Class<?> type : method.getParameterTypes()) {
                	    // Having these as argument will be a problem for serializing method
                		Assert.assertFalse("request arg is not allowed " + name, type.isInstance(ServletRequest.class));
                		Assert.assertFalse("response arg is not allowed " + name, type.isInstance(ServletResponse.class));
                		Assert.assertFalse("session arg is not allowed " + name, type.isInstance(HttpSession.class));
                	}
                }
            }
        }
    }

}
