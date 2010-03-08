/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class contains a logger object log the messages. Also contains functions
 * to log the messages at different levels
 */
public class Log4jLogger extends MifosLogger {

    /** logger to log the statements */
    private Logger logger = null;

    /**
     * Constructor: Obtains an instance of the logger with a specified name.
     * Root logger is "org.mifos"
     *
     * @param name
     *            The name of the Logger
     */
    public Log4jLogger(String name) {
        logger = Logger.getLogger(name);
    }

    /**
     * Constructor: Obtains an instance of the logger with a specified name.
     * Root logger is "org.mifos"
     *
     * @param name
     *            The name of the Logger
     * @param resourceBundle
     *            The resource bundle associated with the logger
     */
    public Log4jLogger(String name, ResourceBundle resourceBundle) {
        logger = Logger.getLogger(name);
        // sets the resource bundle for the logger
        logger.setResourceBundle(resourceBundle);
    }

    @Override
    public String getUserID() {
        return ApplicationConfig.getUserId();
    }

    @Override
    public String getOfficeID() {
        return ApplicationConfig.getOfficeId();
    }

    @Override
    protected void logKey(Level level, String key, Object[] args1, Throwable exception) {
        logger.l7dlog(level, key, args1, exception);
    }

    @Override
    protected void logNonKey(Level level, String message, Throwable exception) {
        logger.log(level, message, exception);
    }

}
