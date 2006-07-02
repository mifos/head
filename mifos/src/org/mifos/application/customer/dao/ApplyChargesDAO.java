/**

 * ApplyChargesDAO.java    version: xxx

 

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

package org.mifos.application.customer.dao;

import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is used as DAO for Applying Charges for customers.
 * @author ashishsm
 *
 */
public class ApplyChargesDAO extends DAO {

	/**
	 * 
	 */
	public ApplyChargesDAO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * The query to obtain the list of fees takes into account the following criteria: -
	 * 1)Status of the fee.
	 * 2)Fee category - It should get the fee based on the category which could be derived based on the customerLevel which is in the ValueObject.
	 * 3)It should get only the fee which are not already associated with the current customer.
	 * @param customerId
	 * @param customerLevel
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private SearchResults getFeeList(Integer customerId,Short customerLevel)throws SystemException,ApplicationException {
		return null;
	}
	
	/**
	 * It inserts a record in  the customerAccount table.For this it retrieves the CustomerAccount oject based on the customerId which is in the value object.
	 * It creates an object of type AccountFees and then updates the account table.
	 * Need to think more about the version No.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * It updates the account fees table which should have a flag indicating that this particular fees has been removed.
	 * For updating it needs to follow the same procedure as that of update.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void remove(Context context) throws SystemException,ApplicationException {
	}
	
	/**
	 * As of now it just gets the list of active recurring fees associated with the customer account.
	 * It puts them in the context to be displayed on the UI.
	 * @param context
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void get(Context context) throws SystemException,ApplicationException {
	}
}
