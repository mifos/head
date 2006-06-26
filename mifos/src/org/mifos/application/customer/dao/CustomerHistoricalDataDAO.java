/**

 * CustomerHistoricalDataDAO.java    version: 1.0



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

package org.mifos.application.customer.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.customer.exceptions.CustomerNotFoundException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerHistoricalData;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class denotes the DAO layer for the CustomerHistoricalData module.
 * @author navitas
 */
public class CustomerHistoricalDataDAO extends DAO {

	/**
	 * This method is called to get the Customer Historical Data for the customer.
	 * It first retrieves the customer with given customerId. If customer not found it throws the exception.
     * It returns the historical data value object.
	 * @param context instance of Context
	 * @throws SystemException
	 * @throws ApplicationException
	 */	
	public CustomerHistoricalData get(Integer customerId)throws ApplicationException,SystemException{
		Session session = null;
		try{
			session=HibernateUtil.getSession();
			Customer customer=getCustomer(customerId);
			return customer.getHistoricalData();
		} 
		catch(HibernateProcessException hse){
			throw hse;
		}
		finally{
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * This method is called to update the Customer Historical Data.
	 * It first retrieves the customer with given customerId. If customer not found it throws the exception.
     * It set the historical data to the customer and then updates the customer to save historical data.
	 * @param context instance of Context
	 * @throws SystemException
	 * @throws ApplicationException
	 */		
	public void update(Context context)throws ApplicationException,SystemException{
		Session session = null;
		CustomerHistoricalData customerHistoricalData =(CustomerHistoricalData)context.getValueObject();
		Customer customer=getCustomer(customerHistoricalData.getCustomerId());
		Transaction tx=null;
		try{
			session=HibernateUtil.getSession();
			tx = session.beginTransaction();
			customer.setCustomerHistoricalData(customerHistoricalData);
			
			customer.setUpdatedBy(context.getUserContext().getId());
			customer.setUpdatedDate(new CustomerHelper().getCurrentDate());
			
			customer.setCustomerAddressDetail(null);
			customer.setCustomerDetail(null);
			session.update(customer);
			tx.commit();
		} 
		catch(HibernateProcessException hse){
			throw hse;
		}
		finally{
			HibernateUtil.closeSession(session);
		}
	}
	
	/**
	 * This method is the helper method to get the customer when customerId is passed in
	 * @param session is the current hibernate session
     * @param customerId is the customerId of the customer
     * @throws SystemException
	 * @throws ApplicationException
	 */	
	private Customer getCustomer(Integer customerId)throws ApplicationException,SystemException
	{
		Session session = null;
		Customer customer = null;
		try
		{
			session=HibernateUtil.getSession();
			customer=(Customer)session.get(org.mifos.application.customer.util.valueobjects.Customer.class,customerId);
			if(customer==null){
				String []values = new String[1];
				values[0]=customerId.toString();
				throw new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND,values);
			}
			return customer;
		} 
		catch(HibernateProcessException hse){
			throw hse;
		}
		finally{
			HibernateUtil.closeSession(session);
		}
	}
}

