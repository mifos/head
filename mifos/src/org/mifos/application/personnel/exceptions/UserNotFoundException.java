/**

 * CustomerNotFoundException.java    version: 1.0



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
package org.mifos.application.personnel.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This exception is thrown when user is not found in the database
 * @author navitas
 */
public class UserNotFoundException extends ApplicationException{

	/** Simple Constructor for UserNotFoundException*/
	public UserNotFoundException() {
		super();
	}
	
	/** Constructor for UserNotFoundException
	 * @param key
	 */
	public UserNotFoundException(String key) {
		super(key);
	}
	
	/** Constructor for UserNotFoundException
	 * @param key
	 * @param cause
	 */
	public UserNotFoundException(String key,Throwable cause) {
		super(key,cause);
	}
	/** Constructor for UserNotFoundException
	 * @param key
	 * @param values
	 */
	public UserNotFoundException(String key,Object[] values) {
		super(key, values);
	}
	
	/** Constructor for UserNotFoundException
	 * @param cause
	 */
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}
	
	/** Constructor for UserNotFoundException
	 * @param values
	 */
	public UserNotFoundException(Object[] values) {
		super(values);
	}

}
