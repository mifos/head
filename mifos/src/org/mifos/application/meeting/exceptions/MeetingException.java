/**

 * MeetingException.java    version: 1.0

 

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
package org.mifos.application.meeting.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This class represent the meeting exception 
 * @author rajenders
 *
 */
public class MeetingException extends ApplicationException {
	private static final long   serialVersionUID=000l;

	/**
	 * Default constructor
	 */
	public MeetingException() {
	}

	/**
	 * Constructor taking values array as message parameters
	 * @param values array of objects 
	 */
	public MeetingException(Object[] values) {
		super(values);
	}

	/**
	 * Constructor taking cause as parameter
	 * @param cause Throwable object
	 */
	public MeetingException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor taking key as parameter
	 * @param key key to the message
	 */
	public MeetingException(String key) {
		super(key);
	}

	/**
	 * Constructor taking the key and values as parameter
	 * @param key key to message
	 * @param values array of object to be used in formatting the message 
	 */
	public MeetingException(String key, Object[] values) {
		super(key, values);
	}

	/**
	 * Constructor  taking the key and Throwable object as argument
	 * @param key key to message
	 * @param cause Throwable object
	 */
	public MeetingException(String key, Throwable cause) {
		super(key, cause);
	}

}
