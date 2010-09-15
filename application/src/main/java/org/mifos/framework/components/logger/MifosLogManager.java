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

import java.io.IOException;

import org.apache.log4j.xml.DOMConfigurator;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.FilePaths;

/**
 * A class with static methods to obtain instances of the logger. It also keeps a HashMap of the actual logger instances
 * per module
 */
public class MifosLogManager {

    private static long DELAY = 3600000;

    private static Boolean initialized = false;

    public static void configure() throws LoggerConfigurationException, IOException {
        if (!initialized) {
            String configFileName = new ConfigurationLocator().getFilePath(FilePaths.LOG_CONFIGURATION_FILE);
            DOMConfigurator.configureAndWatch(configFileName, DELAY);
        }
    }
}
