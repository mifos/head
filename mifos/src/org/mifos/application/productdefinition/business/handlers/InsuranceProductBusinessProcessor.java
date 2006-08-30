/**

 * InsuranceProductBusinessProcessor.java    version: xxx

 

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
import org.mifos.framework.util.valueobjects.Context;

public class InsuranceProductBusinessProcessor extends MifosBusinessProcessor {

	public InsuranceProductBusinessProcessor() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This would load the master data for prdCategory of insurance type.
	 * It also needs to get master list of applicable for and fee types based on insurance product type.
	 * This should not show any product category which is in status inactive neither should it show fee of status inactive.
	 * This master data would be stored in context.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * This would generate SystemId for the product using IDGenerator.
	 * This should also check if the product offering already exists in the system.
	 * If it exists it throws DuplicateProductInstance exception.
	 * It also checks that all the fees being assigned to it are active because they could be made inactive by some other concurrent user.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * This retrieves the insurance product based on the system id by calling findBySystemId on the dao
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#get(org.mifos.framework.util.valueobjects.Context)
	 */
	public void get(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * This searches the insurance products irrespective of their status.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getInsuranceProducts(Context context)throws SystemException,ApplicationException{
		
	}
	
/**
	 * This would load the master data for prdCategory of insurance type.
	 * It also needs to get master list of applicable for and fee types based on insurance product type.
	 * This should not show any product category which is in status inactive neither should it show fee of status inactive.
	 * This would also get a master list of states for the product instance.
	 * This master data would be stored in context.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void manageInitial(Context context)throws SystemException,ApplicationException{
		
	}
	
/**
	 * This should also check if the product offering already exists in the system.
	 * If it exists it throws DuplicateProductInstance exception.
	 * It also checks that all the fees being assigned to it are active because they could be made inactive by some other concurrent user.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context)throws SystemException,ApplicationException{
		
	}

}
