/**

 * CustomerException.java    version: 1.0



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
 * This is general exception for the personnel
 * @author navitas
 */
public class StatusChangeException extends ApplicationException{
	
	/** Simple Constructor for StatusChangeException*/
	public StatusChangeException() {
		super();
	}
	/** Constructor for StatusChangeException
	 * @param key
	 */
	public StatusChangeException(String key) {
		super(key);
	}
	
	/** Constructor for StatusChangeException
	 * @param key
	 * @param values
	 */
	public StatusChangeException(String key,Object[] values) {
		super(key, values);
	}

	/** Constructor for StatusChangeException
	 * @param cause
	 */
	public StatusChangeException(Throwable cause) {
		super(cause);
	}
	
	/** Constructor for StatusChangeException
	 * @param values
	 */
	public StatusChangeException(Object[] values) {
		super(values);
	}
/** Constructor for StatusChangeException
	 * @param cause
	 */
	public StatusChangeException(String key ,Throwable cause) {
		super(key , cause);
	}

}
