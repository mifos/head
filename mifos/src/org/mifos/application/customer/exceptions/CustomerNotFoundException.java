/**

 * CustomerNotFoundException.java    version: 1.0



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
package org.mifos.application.customer.exceptions;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * This exception is thrown when customer is not found in the database
 * @author navitas
 */
public class CustomerNotFoundException extends ApplicationException{

	/** Simple Constructor for CustomerNotFoundException*/
	public CustomerNotFoundException() {
		super();
	}
	
	/** Constructor for CustomerNotFoundException
	 * @param key
	 */
	public CustomerNotFoundException(String key) {
		super(key);
	}
	
	/** Constructor for CustomerNotFoundException
	 * @param key
	 * @param cause
	 */
	public CustomerNotFoundException(String key,Throwable cause) {
		super(key,cause);
	}
	/** Constructor for CustomerNotFoundException
	 * @param key
	 * @param values
	 */
	public CustomerNotFoundException(String key,Object[] values) {
		super(key, values);
	}
	
	/** Constructor for CustomerNotFoundException
	 * @param cause
	 */
	public CustomerNotFoundException(Throwable cause) {
		super(cause);
	}
	
	/** Constructor for CustomerNotFoundException
	 * @param values
	 */
	public CustomerNotFoundException(Object[] values) {
		super(values);
	}

}
