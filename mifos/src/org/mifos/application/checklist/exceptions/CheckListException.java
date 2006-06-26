/**
 * * CheckListException.java    version: 1.0

 

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
package org.mifos.application.checklist.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * @author imtiyazmb
 *
 */
public class CheckListException extends ApplicationException {
	/**
	 *default constructor 
	 */
	public CheckListException() {
		super();
	}

	/**
	 * @param values
	 */
	public CheckListException(Object[] values) {
		super(values);
	}

	/**
	 * @param cause
	 */
	public CheckListException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param key
	 */
	public CheckListException(String key) {
		super(key);
	}

	/**
	 * @param key
	 * @param values
	 */
	public CheckListException(String key, Object[] values) {
		super(key, values);
	}

	/**
	 * @param key
	 * @param cause
	 */
	public CheckListException(String key, Throwable cause) {
		super(key, cause);
	}
	
	/**
	 * serial version UID for serialization.
	 */
	private static final long serialVersionUID = 6223987933034497348L;
}
