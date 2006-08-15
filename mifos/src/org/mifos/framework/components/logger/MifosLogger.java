package org.mifos.framework.components.logger;

public interface MifosLogger {

	/**
	 * Function to log statements with level DEBUG
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			  then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 				   treated as a key in the resource bundle or as a string
	 * @param args  List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	public abstract void debug(String key, boolean asString, Object[] args);//end-method debug

	/**
	 * Function to log statements with level DEBUG
	 * @param message Message to be printed
	 * 
	 */
	public abstract void debug(String message);//end-method debug

	/**
	 * Function to log statements with level INFO
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */

	public abstract void info(String key, boolean asString, Object[] args);//end-method info

	/**
	 * Function to log statements with level INFO
	 * @param message Message to be printed 	
	 */

	public abstract void info(String message);//end-method info

	/**
	 * Function to log statements with level WARN
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 */
	public abstract void warn(String key, boolean asString, Object[] args); //end-method warn

	/**
	 * Function to log statements with level WARN
	 * @param message Message to be printed 	
	 */

	public abstract void warn(String message);//end-method warn

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
	public abstract void warn(String key, boolean asString, Object[] args,
			Throwable t);//end-method warn	

	/**
	 * Function to log statements with level ERROR
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 	
	 */
	public abstract void error(String key, boolean asString, Object[] args); //end-method error

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
	public abstract void error(String key, boolean asString, Object[] args,
			Throwable t); //end-method error

	/**
	 * Function to log statements with level ERROR
	 * @param message Message to be printed 	
	 */
	public abstract void error(String message);//end-method error

	/**
	 * Function to log statements with level FATAL
	 * @param key Key present in the resource bundle. If asString parameter is false 
	 * 			   then this is treated as the string to be displayed
	 * @param asString Boolean value which decides if the parameter "key" should be 
	 * 					treated as a key in the resource bundle or as a string
	 * @param args List of parameters which can be used to replace placeholders 
	 * 				in a message. 
	 */
	public abstract void fatal(String key, boolean asString, Object[] args);//end-method fatal

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
	public abstract void fatal(String key, boolean asString, Object[] args,
			Throwable t);//end-method fatal

	/**
	 * Function to log statements with level FATAL
	 * @param message Message to be printed 	
	 */
	public abstract void fatal(String message);//end-method fatal

	/**
	 * Description: Function to obtain the userID of the person logging the statements
	 * @return  UserId of the person
	 */
	public abstract String getUserID();//end-method getUserID

	/**
	 * Function to obtain the officeID of the user logging the statements
	 * @return  OfficeId of the person
	 */
	public abstract String getOfficeID();//end-method getOfficeID

}