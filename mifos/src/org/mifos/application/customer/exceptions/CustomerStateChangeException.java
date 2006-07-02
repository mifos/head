/**

 * CustomerStateChangeException.java    version: 1.0



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
package org.mifos.application.customer.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This exception is thrown when customer state can not be changed
 * @author navitas
 */
public class CustomerStateChangeException extends ApplicationException{
	
	/** Simple Constructor for CustomerException*/
	public CustomerStateChangeException() {
		super();
	}
	
	/** Constructor for CustomerNotFoundException
	 * @param key
	 * @param cause
	 */
	public CustomerStateChangeException(String key,Throwable cause) {
		super(key,cause);
	}
	
	/** Constructor for CustomerStateChangeException
	 * @param key
	 */
	public CustomerStateChangeException(String key) {
		super(key);
	}
	
	/** Constructor for CustomerStateChangeException
	 * @param key
	 * @param values
	 */
	
	public CustomerStateChangeException(String key,Object[] values) {
		super(key, values);
	}
	
	/** Constructor for CustomerStateChangeException
	 * @param cause
	 */
	public CustomerStateChangeException(Throwable cause) {
		super(cause);
	}
	
	/** Constructor for CustomerStateChangeException
	 * @param values
	 */
	public CustomerStateChangeException(Object[] values) {
		super(values);
	}
	
}
