package org.mifos.framework.components.logger;

import org.apache.log4j.Level;

public abstract class MifosLogger {

	/**
	 * Log statements with level DEBUG
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			  then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 				   treated as a key in the resource bundle or as a string
	 * @param args  List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	public void debug(String key, boolean asString, Object[] args){
		logMessage(Level.DEBUG, key, asString, args, null);
	}

	/**
	 * Log statements with level DEBUG
	 * @param message Message to be printed
	 */
	public void debug(String message) {
		logMessage(Level.DEBUG, message, false, null, null);
	}

	/**
	 * Log statements with level INFO
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */

	public void info(String key, boolean asString, Object[] args) {
		logMessage(Level.INFO, key, asString, args, null);
	}

	/**
	 * Log statements with level INFO
	 * @param message Message to be printed 	
	 */
	public void info(String message) {
		logMessage(Level.INFO ,message, false , null ,null);
	}

	/**
	 * Log statements with level WARN
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 */
	public void warn(String key, boolean asString, Object[] args) {
		logMessage(Level.WARN, key, asString, args, null);
	}

	/**
	 * Log statements with level WARN
	 * @param message Message to be printed 	
	 */

	public void warn(String message) {
		logMessage(Level.WARN ,message, false , null ,null);
	}

	/**
	 * Log statements with level WARN and print stack trace
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 * @param t Exception object whose stack trace will be printed
	 */
	public void warn(String key, boolean asString, Object[] args,
			Throwable t) {
		logMessage(Level.WARN, key, asString, args, t);
	}

	/**
	 * Log statements with level ERROR
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	public void error(String key, boolean asString, Object[] args) {
		logMessage(Level.ERROR, key, asString, args, null);
	}

	/**
	 * Log statements with level ERROR and print stack trace
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 * @param t Exception object whose stack trace will be printed
	 */
	public void error(String key, boolean asString, Object[] args,
			Throwable t) {
		logMessage(Level.ERROR, key, asString, args, t);
	}

	/**
	 * Log statements with level ERROR
	 * @param message Message to be printed 	
	 */
	public void error(String message) {
		logMessage(Level.ERROR, message, false, null, null);
	}

	/**
	 * Log statements with level FATAL
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 */
	public void fatal(String key, boolean asString, Object[] args) {
		logMessage(Level.FATAL, key, asString, args, null);
	}

	/**
	 * Log statements with level FATAL and print stack trace
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 * @param t Exception object whose stack trace will be printed
	 */
	public void fatal(String key, boolean asString, Object[] args,
			Throwable t) {
		logMessage(Level.FATAL ,key, asString , args ,t);
	}

	/**
	 * Log statements with level FATAL
	 * @param message Message to be printed 	
	 */
	public void fatal(String message) {
		logMessage(Level.FATAL ,message, false , null ,null);
	}

	/**
	 * Description: Function to obtain the userID of the person logging the statements
	 * @return  UserId of the person
	 */
	public abstract String getUserID();

	/**
	 * Function to obtain the officeID of the user logging the statements
	 * @return  OfficeId of the person
	 */
	public abstract String getOfficeID();

	/**
	 * Log statements with the designated Level
	 * @param level Level with which the message should be logged
	 * @param key Key present in the resource bundle. If asString parameter is false
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 * @param t Throwable object containing the exception
	 */
	protected abstract void logMessage(Level level, String key, boolean asString, 
		Object[] args, Throwable t);

}
