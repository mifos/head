/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
package org.mifos.framework.spring;

import java.net.URISyntaxException;
import java.util.ArrayList;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A (hopefully) temporary class to encapsulate Spring/Mifos integration. (-Adam
 * 06-FEB-2008)
 */
public class SpringUtil {
	private static ApplicationContext appContext = null;
	private static MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ROOTLOGGER);

	public static void initializeSpring() {
		String[] configFiles = getConfigFiles();
		appContext = new ClassPathXmlApplicationContext(configFiles);
	}

	private static String[] getConfigFiles() {
		ArrayList<String> configFiles = new ArrayList<String>();

		// required config file. exception thrown if not found.
		configFiles.add(FilePaths.SPRING_CONFIG_CORE);

		try {
			if (null != ResourceLoader.getURI(FilePaths.SPRING_CONFIG_CUSTOM_BEANS)) {
				logger.info("using " + FilePaths.SPRING_CONFIG_CUSTOM_BEANS
						+ " for custom bean configuration");
				configFiles.add(FilePaths.SPRING_CONFIG_CUSTOM_BEANS);
			}
		}
		catch (URISyntaxException e) {
			logger.debug(FilePaths.SPRING_CONFIG_CUSTOM_BEANS
					+ " not found in application classpath. Ignoring.");
		}

		return configFiles.toArray(new String[] {});
	}

	public static ApplicationContext getAppContext() {
		return appContext;
	}

	public static void setAppContext(ApplicationContext context) {
		SpringUtil.appContext = context;
	}
}
