/**

 * CustomerHistoricalDataBusinessProcessor.java    version: 1.0



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

package org.mifos.application.customer.business.handlers;

import java.sql.Date;

import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.customer.dao.CustomerHistoricalDataDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomerHistoricalData;
import org.mifos.application.personnel.dao.PersonnelDAO;
import org.mifos.application.personnel.util.valueobjects.Personnel;

/** 
 *  This is the business processor for the Customer Historical Data module. 
 *  It takes care of handling all the business logic related to customer historical data
 *  @author navitas
 */
public class CustomerHistoricalDataBusinessProcessor extends MifosBusinessProcessor {

	/** 
     *  This method is used to retrieve historical data for a customer.
     *  It first retrieves the customer with given customerId. If customer not found it throws the exception.
     *  From retrieved customer, it retireves its historical data if any.
     *  If historical data value object is found it sets back to context.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void get(Context context)throws ApplicationException,SystemException{
		try{
		CustomerHistoricalDataDAO custHistoricalDataDAO = this.getCustomerHistoricalDataDAO();
		LinkParameters params = (LinkParameters)context.getBusinessResults(CustomerConstants.LINK_VALUES);
		CustomerHistoricalData customerHistoricalData=custHistoricalDataDAO.get(params.getCustomerId());
		if(null!=customerHistoricalData){
			context.setValueObject(customerHistoricalData);
			context.addBusinessResults(CustomerConstants.IS_HISTORICAL_DATA_PRESENT,CustomerConstants.YES);
		}else{
			context.addBusinessResults(CustomerConstants.IS_HISTORICAL_DATA_PRESENT,CustomerConstants.NO);
		}
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception se){
			throw new CustomerException (CustomerConstants.UNKNOWN_EXCEPTION);
		}
	}

	/** 
     *  This method is used to update historical data for a customer.
     *  It first retrieves the customer with given customerId. If customer not found it throws the exception.
     *  On retrieved customer, it sets customer historical data and updates the customer.
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void updateInitial(Context context)throws ApplicationException,SystemException{
		try{
		CustomerHistoricalData customerHistoricalData =(CustomerHistoricalData)context.getValueObject();
		if(null!=customerHistoricalData){
			CustomerHistoricalDataDAO custHistoricalDataDAO = this.getCustomerHistoricalDataDAO();
			Object obj=context.getBusinessResults(CustomerConstants.IS_HISTORICAL_DATA_PRESENT);
		
			Personnel personnel = new PersonnelDAO().getUser(context.getUserContext().getId());
			if(obj!=null && ((String)obj).equals(CustomerConstants.YES)){
				customerHistoricalData.setUpdatedBy(personnel);
				customerHistoricalData.setUpdatedDate(new CustomerHelper().getCurrentDate());
			}else{
				customerHistoricalData.setHistoricalId(null);
				customerHistoricalData.setCreatedBy(personnel);
				customerHistoricalData.setCreatedDate(new CustomerHelper().getCurrentDate());
			}
		}
		}catch(SystemException se){
			throw se;
		}catch(Exception se){
			throw new CustomerException (CustomerConstants.UNKNOWN_EXCEPTION);
		}
	}
	
	 /** 
	   * This method returns instance of CustomerHistoricalDataDAO
	   * @return CustomerHistoricalDataDAO instance
	   * @throws SystemException
	   */  
	private CustomerHistoricalDataDAO getCustomerHistoricalDataDAO() throws SystemException{
		CustomerHistoricalDataDAO custHistoricalDataDAO = null;
		try{
			custHistoricalDataDAO = (CustomerHistoricalDataDAO)getDAO(PathConstants.CUSTOMER_HISTORICAL_DATA_PATH);
		}catch(ResourceNotCreatedException rnce){
			throw rnce;
		}
		return custHistoricalDataDAO;
	}
}