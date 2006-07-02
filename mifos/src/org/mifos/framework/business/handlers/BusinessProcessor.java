/**

 * BusinessProcessor.java    version: 1.0

 

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

package org.mifos.framework.business.handlers;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/** 
 *  Any BusinessProcessor in the application needs to implement this interface either directly or by extending from some helper class which implements this interface.
 *  BusinessProcessor would be the class which will have all business logic required for the CRUD operations. 
 */
public interface BusinessProcessor {

	
	/**
	 * Implementation of this method needs to handle delegation of any calls to this method to other methods which will have the actual implementation.
	 * This method is called by the <code>Delegator</code> in an attempt to delegate the call from the Action class to the BusinessProcessor. 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void execute(Context context)throws SystemException,ApplicationException;
	
}
