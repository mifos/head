/**

 * IDGenerationException.java    version: xxx

 

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

package org.mifos.application.accounts.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This class indicates that there was an exception while generating
 * global account number(account system id) 
 * @author ashishsm
 *
 */
public class IDGenerationException extends ApplicationException {

	/**
	 * 
	 */
	public IDGenerationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param values
	 */
	public IDGenerationException(Object[] values) {
		super(values);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public IDGenerationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param key
	 */
	public IDGenerationException(String key) {
		super(key);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param key
	 * @param values
	 */
	public IDGenerationException(String key, Object[] values) {
		super(key, values);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param key
	 * @param cause
	 */
	public IDGenerationException(String key, Throwable cause) {
		super(key, cause);
		// TODO Auto-generated constructor stub
	}

}
