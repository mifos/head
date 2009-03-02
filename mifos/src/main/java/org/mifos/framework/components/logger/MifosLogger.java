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
	 * Log statements with level ERROR
	 * @param message Message to be printed
	 * @param throwable Exception to log
	 */
	public void error(String message, Throwable throwable) {
        logMessage(Level.ERROR, message, false, null, throwable);
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
	protected void logMessage(Level level, 
		String key, boolean asString, Object[] args, Throwable t) {
		if (!asString) {
			//creating message object. the userId and the officeID will be appended to the message string
			Message message = new Message(key, getUserID(), getOfficeID());
			
			logNonKey(level, message.toString(), t);
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
			
			logKey(level, key, args1, t);
			
		}
	}

	/**
	 * Low-level method to log, when a message is supplied (not a key).
	 */
	protected abstract void logNonKey(Level level, 
		String message, Throwable exception);

	/**
	 * Low-level method to log, when a key is supplied (not a message).
	 */
	protected abstract void logKey(Level level, 
		String key, Object[] args1, Throwable exception);

}
