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


/**	@author sumeethaec
 *  Created Date: 28-07-05
 *  This class contains a logger object log the messages. Also contains functions to log the messages at different levels 
 */ 
public class MifosLogger  {

	/**logger to log the statements*/
	private Logger logger=null;
		
	/**
	 * Constructor: 
	 * Obtains an instance of the logger with a specified name. <!-->Root logger is "org.mifos"
	 * @param name The name of the Logger
	 */
	public MifosLogger(String name) {
		logger = Logger.getLogger(name);
	}//end-constructor
	/**
	 * Constructor: 
	 * Obtains an instance of the logger with a specified name. 
	 * 				Root logger is "org.mifos"
	 * @param name The name of the Logger
	 * @param resourceBundle The resource bundle associated with the logger
	 */
	public MifosLogger(String name , ResourceBundle resourceBundle) {
		logger = Logger.getLogger(name);
		//sets the resource bundle for the logger
		logger.setResourceBundle(resourceBundle);
	}//end-constructor 
	
	/**
	 * Function to log statements with level DEBUG
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			  then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 				   treated as a key in the resource bundle or as a string
	 * @param args  List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	public void debug(String key, boolean asString ,Object[] args){
		logMessage(Level.DEBUG ,key, asString , args,null);
		
	}//end-method debug
	
	/**
	 * Function to log statements with level DEBUG
	 * @param message Message to be printed
	 * 
	 */
	public void debug(String message){
		logMessage(Level.DEBUG ,message, false , null ,null);
		 
	}//end-method debug
	
	/**
	 * Function to log statements with level INFO
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	
	public void info(String key, boolean asString , Object[] args){
		logMessage(Level.INFO ,key, asString , args,null);
	}//end-method info
	
	/**
	 * Function to log statements with level INFO
	 * @param message Message to be printed 	
	 */
	
	public void info(String message){
		logMessage(Level.INFO ,message, false , null ,null);
	}//end-method info
	/**
	 * Function to log statements with level WARN
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 */
	public void warn(String key, boolean asString , Object[] args){
		
		logMessage(Level.WARN ,key, asString , args , null);
		
	}	//end-method warn
	
	/**
	 * Function to log statements with level WARN
	 * @param message Message to be printed 	
	 */
	
	public void warn(String message){
		logMessage(Level.WARN ,message, false , null ,null);
	}//end-method warn
	/**
	 * Function to log statements with level WARN and print stack trace
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 * @param t Exception object whose stack trace will be printed
	 */
	public void warn(String key, boolean asString , Object[] args , Throwable t){
	
		logMessage(Level.WARN ,key, asString , args ,t);
	}//end-method warn	
	
	
	/**
	 * Function to log statements with level ERROR
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	public void error(String key, boolean asString , Object[] args){
		
		logMessage(Level.ERROR ,key, asString , args , null);
	}	//end-method error
	/**
	 * Function to log statements with level ERROR and print stack trace
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 * @param t Exception object whose stack trace will be printed
	 */
	public void error(String key, boolean asString , Object[] args,Throwable t){
		logMessage(Level.ERROR ,key, asString , args ,t);
		
	}	//end-method error
	/**
	 * Function to log statements with level ERROR
	 * @param message Message to be printed 	
	 */
	public void error(String message){
		logMessage(Level.ERROR ,message, false , null ,null);
	}//end-method error
	/**
	 * Function to log statements with level FATAL
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 */
	public void fatal(String key, boolean asString , Object[] args){
		
		logMessage(Level.FATAL ,key, asString , args , null);
		
		
	}//end-method fatal
	/**
	 * Function to log statements with level FATAL and print stack trace
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 * @param t Exception object whose stack trace will be printed
	 */
	public void fatal(String key, boolean asString , Object[] args,Throwable t){
		
		logMessage(Level.FATAL ,key, asString , args ,t);
	}//end-method fatal
	/**
	 * Function to log statements with level FATAL
	 * @param message Message to be printed 	
	 */
	public void fatal(String message){
		logMessage(Level.FATAL ,message, false , null ,null);
	}//end-method fatal
	/**
	 * Description: Function to obtain the userID of the person logging the statements
	 * @return  UserId of the person
	 */
	public String getUserID(){
		return ApplicationConfig.getUserId();
	}//end-method getUserID
	/**
	 * Function to obtain the officeID of the user logging the statements
	 * @return  OfficeId of the person
	 */
	public String getOfficeID(){
		return ApplicationConfig.getOfficeId();
	
	}//end-method getOfficeID
	
	/**
	 * Function to log statements with the designated Level
	 * @param level Level with which the message should be logged
	 * @param key Key present in the resource bundle. If asString parameter is false
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 * @param t Throwable object containing the exception
	 */
	private void logMessage(Level level, String key,  boolean asString , Object[] args, Throwable t){
		
		if(!asString){
			
			//creating message object. the userId and the officeID will be appended to the message string
			Message m = new Message(key, getUserID(),getOfficeID());
			
			logger.log(level,m.toString(),t);
		}
		else{
			//if argument list is not empty then the key is used to retrieve the string from the resource bundle 
			//and the place holders are replaced. The userId and OfficeId are also attached to the list of arguments. 
			//If the log statement doesnt have any placeholders then the argument list contains only the userid and office id 
			int length = 0;
			if(args!=null && args.length!=0 )
				length=args.length;
			
			Object[] args1=new Object[length+2];
			//copies the list of arguments to new array and the userid and office are attached
			if(args!=null && args.length!=0 )
				System.arraycopy(args,0,args1,0,length);
			
			args1[length] = getUserID();
			args1[length+1] = getOfficeID();
			
			logger.l7dlog(level , key, args1, t );
			
		}
	}//end-method logMessage
	
}
