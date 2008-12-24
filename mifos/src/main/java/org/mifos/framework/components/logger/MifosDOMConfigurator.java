/** 
 * MifosDOMConfigurator.java version:1.0 
 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.
 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 
 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 
 * and how it is applied. 
 *
 */
package org.mifos.framework.components.logger;

import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;

/**	
 *  Class contains functions to configure the logger by reading the configuration files
 */
public class MifosDOMConfigurator extends DOMConfigurator {
	/**
	 * Function to configure the logger from the configuration file
	 * @param filename The configuration file for the logger
	 * @param repository 
	 */
	@Override
	public void doConfigure(String filename, LoggerRepository repository) {
	   super.doConfigure(filename, repository);
	}

	/**
	 * Function to configure logger from configuration file.<!--> If the configuration file is changed while 
	 * the application is running then the logger will reflect the changes
	 * @param configFilename The configuration file
	 * @param delay The delay time after which the file should be checked for changes 
	 */
	public static void configureAndWatch(String configFilename, long delay) {
		MifosXMLWatchdog xdog = new MifosXMLWatchdog(configFilename);
	    xdog.setDelay(delay);
	    xdog.start();
	}
}
