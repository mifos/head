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

import javassist.Modifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.platform.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Some conventions have to be applied on REST controllers in order to make sure that AOP
 * advice works on them, this test check those conventions
 * (may be a custom findbug or PMD rule can replace it)
 *<br>
 * <ul>
 * <li>They all should be under same package “org.mifos.rest.controller” (child packages are okay)</li>
 * <li>Controller public methods should not be final</li>
 * <li>There should be “only one” RequestMethod on a RequestMapping (GET, POST, DELETE).</li>
 * <li> {@link HttpServletRequest}, {@link HTTPServletResponse}, {@link HttpSession} should not be passed as argument
 *      due to serialization overhead</li>
 * </ul>
 */
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
                        Assert.assertFalse("request arg is not allowed " + name, type.equals(HttpServletRequest.class));
                        Assert.assertFalse("response arg is not allowed " + name, type.equals(HttpServletResponse.class));
                        Assert.assertFalse("session arg is not allowed " + name, type.equals(HttpSession.class));
                	}
                }
            }
        }
    }

}
