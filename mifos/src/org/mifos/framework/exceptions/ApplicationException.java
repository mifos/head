/**
 
 * ApplicationException.java    version: 1.0
 
 
 
 * Copyright © 2005-2006 Grameen Foundation USA
 
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
package org.mifos.framework.exceptions;

/**
 * These are the type of exception which are based out of some BusinessLogic where the user needs to be notified about it and then the user will take certain action based on that.
 * @author ashishsm
 *
 */
public class ApplicationException extends Exception {
	
	/** 
	 *  This is a  string which points to the actual message in the resource bundle .So the exception message to be shown to the user would be taken from the resource bundle and hence could be localized.
	 */
	protected String key = null;
	
	/** 
	 *  This is an array of object which might be needed to pass certain parameters to the string in the resource bundle. 
	 */
	protected Object[] values = null;
	
	public ApplicationException() {
	}
	
	public ApplicationException(Object[] values) {
		this.values = values;
	}
	
	public ApplicationException(Throwable cause) {
		super.initCause(cause);
	}
	
	public ApplicationException(String key ) {
		this.key = key;
	}
	
	public ApplicationException(String key ,Object[] values) {
		this.key = key;
		this.values = values;
	}
	
	public ApplicationException(String key ,Throwable cause) {
		this.key = key;
		super.initCause(cause);
	}
	
	public ApplicationException(String key ,Throwable cause,Object[] values) {
		this.key = key;
		super.initCause(cause);
		this.values = values;
	}
	
	/**
	 * Returns the key which maps to an entry in ExceptionResources file.
	 * The message corresponding to this key is used for logging purposes
	 * as well as for displaying message to the user 
	 * @return
	 */
	public String getKey() {
		if(null == key){
			return "exception.framework.ApplicationException";
		}else{
			return this.key;
		}
		
	}
	
	/**
	 * @return Returns the values}.
	 */
	public Object[] getValues() {
		return values;
	}
	
	/**
	 * @param values The values to set.
	 */
	public void setValues(Object[] values) {
		this.values = values;
	}
	
	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	
}