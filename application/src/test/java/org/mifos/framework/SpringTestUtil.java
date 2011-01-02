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

package org.mifos.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * A (hopefully) temporary class to encapsulate Spring/Mifos integration. (-Adam
 * 06-FEB-2008)
 */
public class SpringTestUtil {
    private static ApplicationContext appContext = null;

    /**
     * Use the root logger for lack of research as to which logger would be more
     * appropriate. {@link LoggerConstants#CONFIGURATION_LOGGER} might also be a
     * good choice.
     */
    private static final Logger logger = LoggerFactory.getLogger(SpringTestUtil.class);

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    public static void setAppContext(ApplicationContext context) {
        SpringTestUtil.appContext = context;
    }
}
