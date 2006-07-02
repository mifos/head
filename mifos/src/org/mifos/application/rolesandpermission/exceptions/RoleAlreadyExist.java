/**

 * RoleAlreadyExist.java    version: 1.0

 

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

/**
 * 
 */
package org.mifos.application.rolesandpermission.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This class represent RoleAlreadyExist exception ,which is thrown if the user 
 * is creating new role in the system and that role already exist in the system.
 * This exception is also thrown if the user is trying to modufy the role and he
 * Changes the name of the role and the changed name already exist in the system
 * @author rajenders
 *
 */
public class RoleAlreadyExist extends ApplicationException {
	
	private static final long serialVersionUID = 8765432l;

	/**
	 *  default constructor
	 */
	public RoleAlreadyExist() {
		super();
	}

	/**
	 * Constructor taking values as arguments to show in message
	 * @param values
	 */
	public RoleAlreadyExist(Object[] values) {
		super(values);
	}

	/**
	 * Constructor with Throwable as argument
	 * @param cause
	 */
	public RoleAlreadyExist(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor taking the key in resource bundle to show the message 
	 * @param key
	 */
	public RoleAlreadyExist(String key) {
		super(key);
	}

	/**
	 * Constructor taking key and argument to show in message
	 * @param key
	 * @param values
	 */
	public RoleAlreadyExist(String key, Object[] values) {
		super(key, values);

	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.exceptions.ApplicationException#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		super.setKey(key);
	}

	
}
