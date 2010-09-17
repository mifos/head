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

package org.mifos.framework.components.logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ServiceLogger {

    public static final ServiceLogger MUTED_LOGGER = new ServiceLogger() {
        @Override
        public void endOfServiceCall(Method method, Object[] args) {
        }

        @Override
        public void startOfServiceCall(Method method, Object[] args) {
        }
    };

    public static final ServiceLogger ARGS_LOGGER = new ServiceLogger() {
        final Logger logger = LoggerFactory.getLogger(ServiceLogger.class);

        @Override
        public void endOfServiceCall(Method method, Object[] args) {
        }

        @Override
        public void startOfServiceCall(Method method, Object[] args) {
            logger.info(method.getName() + ":" + Arrays.toString(args));
            for (Object arg : args) {
                if (arg instanceof Date) {
                    logger.info("Date" + ((Date) arg).getTime());
                }
            }
        }
    };

    public void startOfServiceCall(Method method, Object[] args);

    public void endOfServiceCall(Method method, Object[] args);
}
