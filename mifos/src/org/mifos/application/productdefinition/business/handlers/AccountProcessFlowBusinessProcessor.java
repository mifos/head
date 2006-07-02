/**

 * AccountProcessFlowBusinessProcessor.java    version: xxx

 

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

package org.mifos.application.productdefinition.business.handlers;

import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.MethodInvoker;
import org.mifos.framework.util.valueobjects.Context;

/**
 * @author ashishsm
 *
 */
public class AccountProcessFlowBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * 
	 */
	public AccountProcessFlowBusinessProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
/**
	 * Call get on the DAO.This needs to take care of forming sub objects based on product type
	 * because in the UI these have to be shown grouped by product type  
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void get(Context context) throws SystemException,ApplicationException
	{
		
		super.get(context);
	}
	
/**
	 * This should call update on the DAO it should also to handle concurrency exception.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context) throws SystemException,
	ApplicationException
	{
		
		super.update(context);
		
		
	}
	
	/**
	 * This should take care of the business logic that if a state has been slected and its being deselected then should not be any existing user in that state.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context) throws SystemException,
	ApplicationException
	{
			
	}
	
/**
	 * No need to override.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void cancel(Context context) throws SystemException,ApplicationException {
	}
}
