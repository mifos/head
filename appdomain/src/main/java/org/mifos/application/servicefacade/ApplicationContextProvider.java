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

package org.mifos.application.servicefacade;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

public class ApplicationContextProvider implements FactoryBean<ApplicationContextProvider> {

    private static ApplicationContextProvider applicationContextProvider;
    private static ApplicationContextHolder applicationContextHolder;

    public static ApplicationContextProvider getInstance() {
        if(applicationContextProvider == null) {
            applicationContextProvider = new ApplicationContextProvider();
        }
        return applicationContextProvider;
    }

    public void setApplicationContextHolder(ApplicationContextHolder applicationContextHolder) {
        ApplicationContextProvider.applicationContextHolder = applicationContextHolder;
    }

    public static <T> T getBean(Class<T> clazz) {
        return ApplicationContextProvider.applicationContextHolder.getApplicationContext().getBean(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return ApplicationContextProvider.applicationContextHolder.getApplicationContext();
    }

    @Override
    public ApplicationContextProvider getObject() throws Exception {
        return getInstance();
    }

    @Override
    public Class<ApplicationContextProvider> getObjectType() {
        return ApplicationContextProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}