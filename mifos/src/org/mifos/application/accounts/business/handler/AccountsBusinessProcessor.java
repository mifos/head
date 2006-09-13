/**

* AccountsBusinessProcessor.java    version: xxx



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

package org.mifos.application.accounts.business.handler;


import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.dao.AccountsDAO;
import org.mifos.application.accounts.loan.dao.LoanDAO;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PathConstants;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.customer.util.valueobjects.CustomerLevel;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
* This class acts as base class for business processors of all account types.
*/
public class AccountsBusinessProcessor extends MifosBusinessProcessor {
	
	public AccountsBusinessProcessor() {
		super();
	}
	
	/**
	 * This method is called before getPrdOfferings and is used to get the customer master to be displayed on the UI.
	 * It adds the customer master retrieved in the context as business results.
	 */
	public void getPrdOfferingsInitial(Context context)throws SystemException,ApplicationException{
		// gets the customer name to be dispalyed on the UI and sets it in the business results
		context.addBusinessResults(AccountConstants.CUSTOMERMASTER, getCustomerMaster(context.getValueObject(),context.getPath()));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After getting the relevant client information based on the client id passed");
	}
	/**
	 * This method gets the corresponding product offerings to be displayed in the UI.
	 * This method is called from the action class in response to a method invoked from UI.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getPrdOfferings(Context context)throws SystemException,ApplicationException {
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside getPrdOfferings method in the business processor");
		context.addAttribute(getPrdOfferingsHelper(context));
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After getting the applicable prd offerings based on account type");
	}
	

	/**
	 * Gets the customer Master data based on the customer id which is set in the value object.
	 * 
	 * @param valueObject
	 */
	protected CustomerMaster getCustomerMaster(ValueObject valueObject,String path)throws SystemException {
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside customer master");
		
		Account account = (Account)valueObject;
		CustomerMaster customerMaster = null;
		AccountsDAO accountsDAO = (AccountsDAO)getDAO(path);
		
		HashMap queryParameters = new HashMap();
		queryParameters.put(AccountConstants.CUSTOMERID, account.getCustomer().getCustomerId());
		List queryList = accountsDAO.executeNamedQuery(NamedQueryConstants.RETRIEVECUSTOMERMASTER, queryParameters);
		
		// queryList can be null if the query does not return any results.
		if(null != queryList && ! queryList.isEmpty()){
		// we are retrieving only first record because the query would return only one result.
			customerMaster = (CustomerMaster)queryList.get(0);
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("got customer master corresponding to the id passed above. " );
		}
		return customerMaster;
	}

	/**
	 * This gets the applicable  product offerings for the client.The base class implementation is blank.
	 * It would be overridden by the implementing account business processors
	 * @param string
	 */
	protected SearchResults getPrdOfferingsHelper(Context context )throws SystemException {
		CustomerMaster customerMaster = (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside get applicable prd offerings of loan business processor "  );
		if(context.getPath().equals(PathConstants.LOANACCOUNTSPATH)){
			LoanDAO loanDao = (LoanDAO)getDAO(context.getPath());
			Loan loan = (Loan)context.getValueObject();
			CustomerLevel customerLevel = new CustomerLevel();
			customerLevel.setLevelId(customerMaster.getCustomerLevelId());
			loan.getCustomer().setCustomerLevel(customerLevel);
			return loanDao.getApplicableLoanProducts(loan.getCustomer(),customerMaster.getRecurAfter());
		}
		return null;
		
	}

	
}
