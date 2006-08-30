package org.mifos.framework.components.scheduler;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Factory to generate SchedulerException class instances.
 */
public class SchedulerExceptionFactory {
	/**
	 * Method to create and return ScchedulerException.
	 * @param patternKey using patternKey error msg can be obtained from resources.
	 * @param argKey is an argument to be substituted in error msgs.
	 * @return an instance of SchedulerException
	 */
	static SchedulerException getSchedulerException(String patternKey, String argKey){
		ResourceBundle resourceBundle = ResourceBundle.getBundle(Constants.Resources);
		String []a= new String[1];
		String pattern = resourceBundle.getString(patternKey);
		String arg0 = resourceBundle.getString(argKey);
		a[0]=arg0;
		MessageFormat formatter = new MessageFormat (pattern);
		String str = formatter.format(a);
		SchedulerException se = new SchedulerException(str);
		return se;
	}
	
	/**
	 * Method to create and return ScchedulerException.
	 * @param arg is a msg or a msgkey
	 * @param fromResouces is true when arg is key, is false when arg is msg. 
	 * @return an instance of SchedulerException
	 */
	static SchedulerException getSchedulerException(String arg , boolean fromResources){
		String msg;
		if (fromResources)
			msg = ResourceBundle.getBundle(Constants.Resources).getString(arg);
		else
			msg = arg;
		SchedulerException se = new SchedulerException(msg);
		return se;
	}
}
