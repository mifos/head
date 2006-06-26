/**

 * HibernateSystemException.java    version: xxx



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
 * @author ashishsm
 *
 */
public class HibernateSystemException extends SystemException {

	/**
	 *
	 */
	public HibernateSystemException() {
		super();

	}

	/**
	 * @param cause
	 */
	public HibernateSystemException(Throwable cause) {
		super(cause);

	}

	/**
	 * @param values
	 */
	public HibernateSystemException(Object[] values) {
		super(values);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param values
	 * @param cause
	 */
	public HibernateSystemException(Object[] values, Throwable cause) {
		super(values, cause);
		// TODO Auto-generated constructor stub
	}
	public String getKey(){
		return "exception.framework.SystemException.HibernateConnectionException";
	}

}
