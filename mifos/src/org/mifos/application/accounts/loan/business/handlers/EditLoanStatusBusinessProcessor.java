/**

 * EditLoanStatusBusinessProcessor.java    version: 1.0



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
package org.mifos.application.accounts.loan.business.handlers;


import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.loan.dao.EditLoanStatusDAO;
import org.mifos.application.accounts.loan.dao.LoanDAO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.EditLoanStatus;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.configuration.util.helpers.PathConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ResourceNotCreatedException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;







/**
 * This class acts as businessProcessor for creating/updating loan accounts.
 * @author ashishsm
 *
 */

public class EditLoanStatusBusinessProcessor extends MifosBusinessProcessor{
	/**
     *  This method is called to load change status Page.
     *  It load all states along with flags(if any) to which loan status can be changed
     *  It also loads the name of currentStatus and currentFlag to show on preview page
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void loadInitial(Context context) throws ApplicationException, SystemException{
		EditLoanStatus loanStatus=(EditLoanStatus)context.getValueObject();
		List statusList =getEditLoanStatusDAO().getStatusList(context.getUserContext().getLocaleId(),loanStatus.getCurrentStatusId());
		context.addAttribute(getResultObject(LoanConstants.STATUS_LIST,statusList));
		context.addAttribute(getResultObject(LoanConstants.OLD_STATUS_NAME,getEditLoanStatusDAO().getStatusName(context.getUserContext().getLocaleId(),loanStatus.getCurrentStatusId())));
		//context.addAttribute(getResultObject(LoanConstants.FLAG_NAME,getEditLoanStatusDAO().getFlagName(context.getUserContext().getLocaleId(),loanStatus.getCurrentStatusId())));
	}

    /**
     *  This method handles preview for change loan account status.
     *  It retrieves check list for this status of the loan_Account
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
    public void previewInitial(Context context)throws ApplicationException, SystemException{
		try{
			EditLoanStatus loanStatus = (EditLoanStatus)context.getValueObject();

			short localeId=context.getUserContext().getLocaleId();
			//retrive checklist details for given locale and status
			context.addAttribute(getEditLoanStatusDAO().getAccountCheckList(localeId,loanStatus.getNewStatusId(),new Short(AccountTypes.LOANACCOUNT)));

			//build Customer Note object
			loanStatus.getNotes().setCommentDate(new Date(new java.util.Date().getTime()));
			loanStatus.getNotes().setOfficerId(context.getUserContext().getId());
			Personnel officer = new Personnel();
			officer.setPersonnelId(context.getUserContext().getId());
			officer.setDisplayName(context.getUserContext().getName());
			loanStatus.getNotes().setOfficer(officer);
			context.removeAttribute(LoanConstants.NEW_STATUS_NAME);
			context.removeAttribute(LoanConstants.FLAG_NAME);
			context.addAttribute(this.getResultObject(LoanConstants.NEW_STATUS_NAME, getEditLoanStatusDAO().getStatusName(context.getUserContext().getLocaleId(),loanStatus.getNewStatusId())));
			if(loanStatus.getFlagId()!=null)
				context.addAttribute(this.getResultObject(LoanConstants.FLAG_NAME, getEditLoanStatusDAO().getFlagName(context.getUserContext().getLocaleId(),loanStatus.getFlagId())));

		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
    }

	/**
     *  This method is called to load change status Page.
     *  It load all states along with flags(if any) to which loan status can be changed
     *  It also loads the name of currentStatus and currentFlag to show on preview page
     *  @param context an instance of Context
     *  @throws ApplicationException
     *  @throws SystemException
     */
	public void update(Context context) throws ApplicationException, SystemException{
		try{
			EditLoanStatus loanStatus = (EditLoanStatus)context.getValueObject();
			Account loan = (Account)new LoanDAO().findByPK(loanStatus.getAccountId());
			validateStatus(loanStatus);
			if(null!=loan.getCustomer().getPersonnelId())
			   checkPermissionForStatusChange(loanStatus.getNewStatusId(),context.getUserContext(),loanStatus.getFlagId(),loan.getOfficeId(),loan.getCustomer().getPersonnelId());
			else
				checkPermissionForStatusChange(loanStatus.getNewStatusId(),context.getUserContext(),loanStatus.getFlagId(),loan.getOfficeId(),context.getUserContext().getId());	
			//set new account_id
			loan.setAccountStateId(loanStatus.getNewStatusId());
			if(loanStatus.getNewStatusId().equals(Short.valueOf("6")) || loanStatus.getNewStatusId().equals(Short.valueOf("7")) || loanStatus.getNewStatusId().equals(Short.valueOf("8")) || loanStatus.getNewStatusId().equals(Short.valueOf("10")))
				loan.setClosedDate(new Date(System.currentTimeMillis()));
			//set accountid in notes
			loanStatus.getNotes().setAccountId(loanStatus.getAccountId());
			getEditLoanStatusDAO().updateStatus(loanStatus,loan);
		}catch(SystemException se){
			throw se;
		}catch(ApplicationException ae){
			throw ae;
		}catch(Exception e ){
			throw new CustomerException(CustomerConstants.UNKNOWN_EXCEPTION,e);
		}
	}


	private void validateStatus(EditLoanStatus loanStatus)throws ApplicationException{

	}

	public void getStatusHistory(Context context)throws ApplicationException,SystemException{
		EditLoanStatus loanStatus = (EditLoanStatus)context.getValueObject();
		SearchResults historyList= getEditLoanStatusDAO().getStatusHistory(loanStatus.getAccountId(),context.getUserContext().getLocaleId());
		if(historyList!=null && historyList.getValue()!=null)
		// System.out.println("---------------size of history list obtained"+((List)historyList.getValue()).size());
		context.addAttribute(getResultObject(LoanConstants.STATUS_HISTORY,historyList.getValue()));
	}

	/**
	 * This method creates a new SearchResults object with values as passed in parameters
	 * @param resultName the name with which framework will put resultvalue in request
	 * @param resultValue that need to be put in request
	 * @return SearchResults instance
	 */
	private SearchResults getResultObject(String resultName, Object resultValue){
		SearchResults result = new SearchResults();
		result.setResultName(resultName);
		result.setValue(resultValue);
		return result;
	}

	/**
     * This method returns instance of EditLoanStatusDAO
     * @return EditLoanStatusDAO instance
     * @throws SystemException
     */
  	private EditLoanStatusDAO getEditLoanStatusDAO()throws SystemException{
  		EditLoanStatusDAO loanStatusDAO=null;
  		try{
  			loanStatusDAO = (EditLoanStatusDAO)getDAO(PathConstants.LOAN_STATUS_PATH);
  		}catch(ResourceNotCreatedException rnce){
  		}
  		return loanStatusDAO;
  	}
	private void checkPermissionForStatusChange(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId) throws SecurityException{
		if(!isPermissionAllowed(newState,userContext,flagSelected,recordOfficeId,recordLoanOfficerId))
			  throw new SecurityException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED); 	 
	}
	private boolean isPermissionAllowed(Short newState,UserContext userContext,Short flagSelected,Short recordOfficeId,Short recordLoanOfficerId){
		
		return ActivityMapper.getInstance().isStateChangePermittedForAccount(newState.shortValue(),null!=flagSelected?flagSelected.shortValue():0,userContext,recordOfficeId,recordLoanOfficerId);
	}
}