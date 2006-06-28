/**

 * ClosedAccSearchBusinessProcessor.java    version: xxx

 

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
package org.mifos.application.accounts.business.handler;

import java.util.List;

import org.mifos.application.accounts.dao.ClosedAccSearchDAO;
import org.mifos.application.accounts.loan.dao.LoanDAO;
import org.mifos.application.accounts.loan.util.valueobjects.Loan;
import org.mifos.application.accounts.loan.util.valueobjects.RecentAccountActivity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.util.helpers.ClosedAccSearchConstants;
import org.mifos.application.accounts.util.helpers.PathConstants;
import org.mifos.application.accounts.util.valueobjects.ClientUpcomingFeecahrges;
import org.mifos.application.accounts.util.valueobjects.ClosedAccSearch;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.util.valueobjects.ClientChangeLog;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * @author mohammedn
 *
 */
public class ClosedAccSearchBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * default constructor
	 */
	public ClosedAccSearchBusinessProcessor() {
	}
	
	/**
	 * To get all the closed accounts of the client
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getAllClosedAccounts(Context context) throws SystemException,ApplicationException {
		ClosedAccSearch closedAccSearch=(ClosedAccSearch)context.getValueObject();
		if(null != closedAccSearch) {
			UserContext userContext=context.getUserContext();
			Short localeId=userContext.getLocaleId();
			Integer customerId=closedAccSearch.getCustomerId();
			ClosedAccSearchDAO closedAccSearchDAO=(ClosedAccSearchDAO)getDAO(context.getPath());
			List<Loan> accountsList=closedAccSearchDAO.getAllClosedAccounts(customerId);
			List<SavingsBO> savingsAccountList = new SavingsBusinessService().getAllClosedAccounts(customerId);
			for(SavingsBO savingsBO:savingsAccountList) {
				savingsBO.getAccountState().setLocaleId(localeId);
			}
			SearchResults accountStates=closedAccSearchDAO.getAccStates(localeId);
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.CLOSEDACCOUNTSLIST,accountsList));
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.CLOSEDSAVINGSACCOUNTSLIST,savingsAccountList));
			context.addAttribute(accountStates);
		}
	}
	
	/**
	 * TO get the client charges details  and recent activities
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getClientChargesDetails(Context context) throws SystemException,ApplicationException {
		ClosedAccSearch closedAccSearch=(ClosedAccSearch)context.getValueObject();
		if(null != closedAccSearch) {
			Integer accountId=closedAccSearch.getAccountId();
			Integer customerId=closedAccSearch.getCustomerId();
			ClosedAccSearchDAO closedAccSearchDAO=(ClosedAccSearchDAO)getDAO(context.getPath());
			List<ClientUpcomingFeecahrges> upcomingFeecahrgesList=closedAccSearchDAO.getClientUpcomingFeeCharges(accountId);
			List<ClientUpcomingFeecahrges> RecurenceFeecahrgesList=closedAccSearchDAO.getRecurrenceFeeCharges(accountId);			
			Money clientFeeChargeDue=closedAccSearchDAO.getClientFeeCahrgesDue(accountId);
			Money clientFeeChargeOverDue=closedAccSearchDAO.getClientFeeCahrgesOverDue(accountId);
			String upcomingChargesDate=closedAccSearchDAO.getUpcomingChargesDate(accountId);			
			HibernateUtil.closeSession();
			CustomerBusinessService customerService=(CustomerBusinessService)ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.CLIENTRECENTACCACTIVITYLIST,customerService.getRecentActivityView(customerId)));			
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.CLIENTUPCOMINGFEECHARGESLIST,upcomingFeecahrgesList));
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.RECURRENCEFEESCHARGESLIST,RecurenceFeecahrgesList));
			context.addBusinessResults(ClosedAccSearchConstants.CLIENTFEECHARGEDUE,clientFeeChargeDue);
			context.addBusinessResults(ClosedAccSearchConstants.CLIENTFEECHARGEOVERDUE,clientFeeChargeOverDue);
			context.addBusinessResults(ClosedAccSearchConstants.UPCOMINGCHARGESDATE,upcomingChargesDate);
		}
		
	}
	
	public void getAllRecentAccountActivity(Context context) throws SystemException,ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside getAllRecentAccountActivity method ");
		ClosedAccSearch closedAccSearch=(ClosedAccSearch)context.getValueObject();
		if(null != closedAccSearch) {
			Integer accountId=closedAccSearch.getAccountId();
			LoanDAO loanDAO=(LoanDAO)getDAO(PathConstants.LOANACCOUNTSPATH);
			List<RecentAccountActivity> recentAccountActivityList=loanDAO.getRecentAccountActivity(accountId,-1);
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.CUSTOMERRECENTACCACTIVITYLIST,recentAccountActivityList));
		}
	}
	
	/**
	 * To get the change log of the client
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getChangeLogDetails(Context context) throws SystemException,ApplicationException {
		ClosedAccSearch closedAccSearch=(ClosedAccSearch)context.getValueObject();
		if(null != closedAccSearch) {
			Integer customerId=Integer.valueOf(closedAccSearch.getCustomerId());
			Short entityId=closedAccSearch.getEntityTypeId();
			ClosedAccSearchDAO closedAccSearchDAO=(ClosedAccSearchDAO)getDAO(context.getPath());
			List<ClientChangeLog> clientChangeLogList=closedAccSearchDAO.getClientChangeLog(
					customerId,entityId);
			context.addAttribute(new SearchResults(ClosedAccSearchConstants.CLIENTCHANGELOGLIST,clientChangeLogList));
		}
	}
	
}
