/**

 * ViewClosedAccountsBusinessProcessor.java    version: xxx

 

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

package org.mifos.application.customer.business.handlers;

import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class is used as BusinessProcessor for viewing all closed accounts of a customer.
 */
public class ViewClosedAccountsBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * 
	 */
	public ViewClosedAccountsBusinessProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method is used to get a list of closed accounts.It obtains this using getClosedAccounts method of the dao
	 * which returns a list of closed accounts.The list is a collection of account helper objects which will have 
	 * the attributes to be displayed on the UI.  
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	protected void getClosedAccounts(Context context)throws SystemException,ApplicationException{
		
	}

}
