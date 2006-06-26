/**

 * AssociatePaymentTypes.java    version: xxx

 

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

package org.mifos.application.productdefinition.business.handlers;

import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * @author ashishsm
 *
 */
public class PmntTypesBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * 
	 */
	public PmntTypesBusinessProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This should cosult the dao and load the master data of payment Types using a named query and MasterDataRetriever.
	 * It should also load the existing payment types associated which would the product types displayed on UI.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * This will throw ConcurrencyException.
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * This might need to do some  validations  which might not be done at the UI.
	 * for e.g. some payment types are mandatory for certain product types.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context)throws SystemException,ApplicationException{
		
	}

}
