/**

 * InsuranceProductDAO.java    version: xxx

 

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

package org.mifos.application.productdefinition.dao;

import java.util.Set;

import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class InsuranceProductDAO extends DAO {

	/**
	 * 
	 */
	public InsuranceProductDAO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/** Create the insurance product instance.
	 * @see org.mifos.framework.dao.DAO#create(org.mifos.framework.util.valueobjects.Context)
	 */
	public void create(Context context)throws SystemException,ApplicationException{
		
	}
	

	
	/**
	 * Checks if the product with the specified name already exists in the database.
	 * @param productInstanceName - Name of the productInstance to be checked in the database.
	 * @return - true if product with same name already exists
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 */
	public boolean checkIfProductExists(String productInstanceName)throws SystemException{
		return false;
	}
	
	/**Checks if the fees with the specified fee ids are still in active state. 
	 * @param associatedFeeIds - Set of fee ids associated with the insurance product instance
	 * @return - true if any of the feeids in the set is in inactive state.
	 * @throws SystemException - this is thrown if there is any hibernate exception
	 */
	private boolean checkIfFeeInactive(Set associatedFeeIds)throws SystemException{
		return false;
	}
	
	/**
	 * This retrieves the insurance product instance based on the system id
	 * @param insuranceSystemId
	 * @return
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 * @throws ApplicationException
	 */
	public ValueObject findBySystemId(String insuranceSystemId)throws SystemException,ApplicationException{
		return null;
	}
	
/**
	 * This searches the insurance products irrespective of their status.
	 * This obtains a collection comprising just of product offering id, the system id and the name.
	 * @param context
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 * @throws ApplicationException
	 */
	public void getInsuranceProducts(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * Updates the insurance product instance.
	 * Before updating it checks if there is already any product instance with the same name and id  
	 * @see org.mifos.framework.dao.DAO#update(org.mifos.framework.util.valueobjects.Context)
	 */
	public void update(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * Checks if the product with the specified name and different id already exists in the database.
	 * @param productInstanceName - Name of the productInstance to be checked in the database.
	 * @return - true if product with same name already exists
	 * @throws SystemException- this is thrown if there is any hibernate exception
	 */
	public boolean checkIfProductExists(String productInstanceName, Integer productId)throws SystemException{
		return false;
	}

}
