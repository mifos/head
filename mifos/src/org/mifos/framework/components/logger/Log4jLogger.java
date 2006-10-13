/** 
 * MifosLogger.java version:1.0
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
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**	
 *  This class contains a logger object log the messages. 
 *  Also contains functions to log the messages at different levels 
 */ 
public class Log4jLogger extends MifosLogger {

	/**logger to log the statements*/
	private Logger logger = null;
		
	/**
	 * Constructor: 
	 * Obtains an instance of the logger with a specified name. 
	 * Root logger is "org.mifos"
	 * @param name The name of the Logger
	 */
	public Log4jLogger(String name) {
		logger = Logger.getLogger(name);
	}

	/**
	 * Constructor: 
	 * Obtains an instance of the logger with a specified name. 
	 * 				Root logger is "org.mifos"
	 * @param name The name of the Logger
	 * @param resourceBundle The resource bundle associated with the logger
	 */
	public Log4jLogger(String name, ResourceBundle resourceBundle) {
		logger = Logger.getLogger(name);
		//sets the resource bundle for the logger
		logger.setResourceBundle(resourceBundle);
	}
	
	@Override
	public String getUserID(){
		return ApplicationConfig.getUserId();
	}

	@Override
	public String getOfficeID(){
		return ApplicationConfig.getOfficeId();
	}
	
	@Override
	protected void logMessage(Level level, String key, boolean asString, 
			Object[] args, Throwable t) {
		if (!asString) {
			//creating message object. the userId and the officeID will be appended to the message string
			Message m = new Message(key, getUserID(),getOfficeID());
			
			logger.log(level,m.toString(),t);
		}
		else{
			//if argument list is not empty then the key is used to retrieve the string from the resource bundle 
			//and the place holders are replaced. The userId and OfficeId are also attached to the list of arguments. 
			//If the log statement doesnt have any placeholders then the argument list contains only the userid and office id 
			int length = 0;
			if (args!=null && args.length != 0)
				length=args.length;
			
			Object[] args1=new Object[length+2];
			//copies the list of arguments to new array and the userid and office are attached
			if (args!=null && args.length != 0)
				System.arraycopy(args, 0, args1, 0, length);
			
			args1[length] = getUserID();
			args1[length+1] = getOfficeID();
			
			logger.l7dlog(level, key, args1, t );
			
		}
	}
	
}
