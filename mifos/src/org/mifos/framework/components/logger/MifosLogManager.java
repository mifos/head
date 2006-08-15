/**
 * MifosLogManager.java version:1.0
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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.ResourceBundleNotFoundException;
import org.mifos.framework.util.helpers.ResourceLoader;

/**	@author sumeethaec
 *  Created Date: 28-07-05
 *  A class with static methods to obtain instances of the logger. It also keeps a HashMap of the actual logger instances per module
 */
public class MifosLogManager {

	/**Contains all the loggers that have been created.
	 * Key is the name of the logger and the value is logger instance
	 */
	private static HashMap <String, MifosLogger> loggerRepository;

	/**
	 * Method to initialize the loggerRepository and configure the root logger from the loggerconfiguration.xml
	 * A root logger instance is also created and the resource bundle for the locale of the MFI is associated with the logger
	 * @param fileName The loggerconfiguration xml file
	 * @throws  LoggerConfigurationException
	 */
	public static void configure(String fileName)throws LoggerConfigurationException{
		//Initialises a logger with the name com.mifos which acts as the ancestor for all the other loggers
		try {
			readConfiguration(fileName);
			MifosLogger logger = new Log4jLogger(LoggerConstants.ROOTLOGGER , getResourceBundle(LoggerConstants.LOGGERRESOURCEBUNDLE));
			loggerRepository=new HashMap<String, MifosLogger>(20);
			loggerRepository.put(LoggerConstants.ROOTLOGGER, logger);

		}catch (ResourceBundleNotFoundException rbnfe) {
			throw new LoggerConfigurationException(rbnfe);
		}catch(URISyntaxException urise){
			throw new LoggerConfigurationException(urise);
		}catch(MalformedURLException mfue){
			throw new LoggerConfigurationException(mfue);
		}
	}

	/**
	 * Function to obtain an instance of the mifos logger. This calls a helper method to create the logger
	 * @param name The name the logger will be associated with
	 * @return  An instance of the MifosLogger
	 */
	public static MifosLogger getLogger(String name){
		return getLogger(name,null);
	}//end-method getLogger
	
	/**
	 * Function to obtain an instance of the mifos logger. This calls a helper method to create the logger
	 * A resource bundle can aslo be associated with this logger
	 * @param name The name the logger will be associated with
	 * @param resourceBundleName The name of the resource bundle from where the logger takes its log statements
	 * @return An instance of the MifosLogger
	 */
	public static MifosLogger getLogger(String name, String resourceBundleName){
		return getLoggerHelper(name,resourceBundleName);
	}//end-method getLogger

	/**
	 * Function to obtain an instance of the mifos logger. If it is already present it is retrieved from
	 * the logger repository, else it is created and added to the repository. A resource bundle can also be associated
	 * with this logger
	 * @param name The name the logger will be associated with
	 * @param resourceBundleName The name of the resource bundle from where the logger takes its log statements.
	 * 							 If it is null then the logger is created using the name
	 * @return An instance of the MifosLogger
	 */
	public static MifosLogger getLoggerHelper(String name, String resourceBundleName){

			MifosLogger logger=null;
			//checks to see if the logger repository already contains an instance of the logger.
			//If it does that instance is returned
			if(loggerRepository.containsKey(name)) {
				logger=loggerRepository.get(name);
			}
			//Since the logger repository doesnt contain an instance, a new instance is created and put into the logger repository
			else{
				//taking care of the scenario: two users creating the same instance of a logger and trying to push into
				//the repository. To avoid this the repository is synchronised allowing only one person to push the
				//instance in. For the second user a check on the availability is done again
				synchronized (loggerRepository) {
					if(loggerRepository.containsKey(name) ) {
						logger=loggerRepository.get(name);
					}
					else{
						if(resourceBundleName!=null)
							try {
								logger = new Log4jLogger(name , getResourceBundle(resourceBundleName));
							} catch (ResourceBundleNotFoundException rbnfe) {
								rbnfe.printStackTrace();
							}
						else
							logger = new Log4jLogger(name);
					loggerRepository.put(name,logger);

					}
				}

			}

			return logger;
		}//end-method getLoggerHelper


	/**
	 * Obtains the Resource bundle for a particular MFI Locale
	 * @param resourceBundleName The name of the resource bundle
	 * @return The resouce bundle for the locale
	 * @throws ResourceBundleNotFoundException
	 */
	public static ResourceBundle getResourceBundle(String resourceBundleName)throws ResourceBundleNotFoundException{
		Locale mfiLocale=getMFILocale();
		ResourceBundleFactory resourceBundleFactory  = ResourceBundleFactory.getInstance();
		return resourceBundleFactory.getResourceBundle(resourceBundleName , mfiLocale);
	}//end-method getResourceBundle

	/**
	 * Configures the root logger from the loggerconfiguration.xml
	 * A root logger instance is also created and the resource bundle for the locale of the MFI is associated
	 * with the logger
	 */

	public static void readConfiguration(String fileName)throws MalformedURLException,URISyntaxException{
		URL url = null;
			try {
				url = ResourceLoader.getURI(fileName).toURL();
			} catch (MalformedURLException mfue) {
				throw mfue;
			} catch (URISyntaxException urise) {
				throw urise;
			}
			//url = MifosLogManager.class.getClassLoader().getResource(fileName);

		MifosDOMConfigurator.configureAndWatch(url.getPath(),LoggerConstants.DELAY);

	}//end-method readConfiguration

	/**
	 * Function to obtain the MFI Locale
	 * @return The locale of the MFI
	 */
	public static Locale getMFILocale(){
		return ApplicationConfig.getMFILocale();
	}//end-method getMFILocale
}//~
