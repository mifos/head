/**

 * AccountTrxnBusinessProcessor.java    version: xxx



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

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.dao.AccountTrxnDAO;
import org.mifos.application.accounts.loan.business.util.helpers.LoanHeaderObject;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerTrxnBuilder;
import org.mifos.application.accounts.util.helpers.LoanTrxnBuilder;
import org.mifos.application.accounts.util.helpers.SavingsTrxnBuilder;
import org.mifos.application.accounts.util.helpers.TrxnObjectBuilder;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.business.util.helpers.HeaderObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.DataTypeConstants;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

public class AccountTrxnBusinessProcessor extends MifosBusinessProcessor {

	public AccountTrxnBusinessProcessor() {
		super();
	}

	public void load(Context context) throws SystemException,
			ApplicationException {

		/*
		 * AccountTrxnDAO dao = new AccountTrxnDAO(); AccountActionDate acct =
		 * dao.load(accountId);
		 */
		context
				.addAttribute(getMasterData(
						"PaymentType",
						context.getUserContext().getLocaleId(),
						"paymentType",
						"org.mifos.application.productdefinition.util.valueobjects.PaymentType",
						"paymentTypeId"));

		AccountTrxnDAO dao = new AccountTrxnDAO();
		AccountPayment pmntOld = (AccountPayment) (context.getValueObject());
		Integer accountId = pmntOld.getAccountId();
		Short accountType = pmntOld.getAccountType();
		Short personnelId = context.getUserContext().getId();
		context.addBusinessResults("AccountId",accountId);
		try{
		//get the current installment details
		AccountActionDate acctDate = dao.getPaymentDetail(accountId);

		if (acctDate != null && acctDate.getActionDateId()!=null && acctDate.getActionDateId() != 0) {
			// System.out.println("************************** The action date id  is - " + acctDate.getActionDateId());
			TrxnObjectBuilder builder = null;
			// populate the AccountTrxn VO from AccountActionDate VO
			// check here if the account type is loan, saving or customer
			if (accountType.equals(AccountTypes.LOANACCOUNT.getValue())) {
				builder = new LoanTrxnBuilder();
			} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT.getValue())) {
				// do the customer processing here
				builder = new CustomerTrxnBuilder();
			} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
				// do the savings processing here
				builder = new SavingsTrxnBuilder();
			}

			AccountPayment pmnt = builder.build(acctDate, personnelId);
			if(pmnt==null)
				;
				// System.out.println("The new Payment Object is NULL");
			else
				pmnt.setAccountType(accountType);
			//AccountPayment pmnt = new AccountPayment();

			context.setValueObject(pmnt);
			context.addBusinessResults(AccountConstants.ACCOUNT_ACTION_DATE_KEY, acctDate);

		}else{
			// System.out.println("The account Date value object obtained was null");
			//The account date object is null which implies there are no payments which can be made
			// System.out.println(context.toString());
			// System.out.println(context.getValueObject().toString());
			context.setValueObject(null);
		}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public void getInstallmentHistory(Context context) throws SystemException,
			ApplicationException {
		AccountPayment accountPmntVO = (AccountPayment) context.getValueObject();
		AccountTrxnDAO dao = new AccountTrxnDAO();
		List<AccountActionDate> list = dao.getInstallmentHistory(accountPmntVO
				.getAccountId());
		SearchResults result = new SearchResults();
		result.setResultName(AccountConstants.ACCOUNT_GETINSTALLMENTS);
		result.setValue(list);
		context.addAttribute(result);
	}

	public SearchResults getMasterData(String entityName, Short localeId,
			String searchResultName, String classpath, String column)
			throws SystemException, ApplicationException {
		SearchResults searchResults = null;
		MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
		searchResults = masterDataRetriever.retrieveMasterData(entityName,
				localeId, searchResultName, classpath, column);

		return searchResults;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#fetchHeader(org.mifos.framework.util.valueobjects.Context, java.lang.String)
	 */
	public HeaderObject fetchHeader(Context context, String businessAction) throws SystemException, ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside fetch header with method : " + businessAction);
		if("load".equals(businessAction)) {
			Account account = (Account)DAO.getEntity("org.mifos.application.accounts.util.valueobjects.Account",context.getBusinessResults("AccountId") ,DataTypeConstants.Integer );
			LoanHeaderObject loanHeader = new LoanHeaderObject();
			CustomerMaster customerMaster = (CustomerMaster)context.getBusinessResults(AccountConstants.CUSTOMERMASTER);
			List<CustomerMaster> customerMasterList = CustomerUtilDAO.getParentHierarchy(account.getCustomer().getCustomerId());
			Office office = (Office)DAO.getEntity("org.mifos.application.office.util.valueobjects.Office",account.getOfficeId() ,DataTypeConstants.Short );
			String OfficeName = office.getOfficeName();
			loanHeader.setCustomerMasterList(customerMasterList);
			loanHeader.setOfficeName(OfficeName);
			loanHeader.setOfficeId(account.getOfficeId());

			return loanHeader;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#getBusinessActionList()
	 */
	protected List<String> getBusinessActionList() {
		List headerMethodList = new ArrayList();
		headerMethodList.add("load");
		return headerMethodList;
	}





}
