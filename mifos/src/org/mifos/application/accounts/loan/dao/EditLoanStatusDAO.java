/**

 * EditLoanStatusDAO.java    version: 1.0



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

package org.mifos.application.accounts.loan.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.dao.AccountNotesDAO;
import org.mifos.application.accounts.dao.AccountsDAO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.loan.util.valueobjects.EditLoanStatus;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.valueobjects.Account;
import org.mifos.application.accounts.util.valueobjects.AccountFlagDetail;
import org.mifos.application.accounts.util.valueobjects.AccountStatusChangeHistory;
import org.mifos.application.customer.client.util.valueobjects.Client;
import org.mifos.application.customer.client.util.valueobjects.ClientPerformanceHistory;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.util.valueobjects.AccountState;
import org.mifos.application.master.util.valueobjects.FlagMaster;
import org.mifos.application.master.util.valueobjects.StatusMaster;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConcurrencyException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.valueobjects.SearchResults;

public class EditLoanStatusDAO extends DAO{
	
	/**
	 * This method updates the account status in the database. It also makes entry in accounts notes table and
	 * account status change history
	 * @param loanStatus  instance of LoanStatus
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	public void updateStatus(EditLoanStatus loanStatus,Account loan) throws ApplicationException,SystemException{
		Transaction tx = null;
		Session session = null;
		AccountStatusChangeHistory history = getAccountStatusChangeHistory(loanStatus);
		AccountFlagDetail flagDetail=getAccountFlagDetail(loanStatus);
	   	try{
	   		session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			//update account status
	   		session.update(loan);
		    
		    //add flag if any
		    if(null!=flagDetail)
		    	session.save(flagDetail);
		    
		    //add note
		    new AccountNotesDAO().addNotes(session,loanStatus.getNotes());
		    
		    //save status change history object
		    session.save(history);
		    
		    //add row in account status change history
		    
		    updateCustomerHistory(session,loan,loanStatus);
		    
		    tx.commit();
	   	}catch(StaleObjectStateException sose){
	   		tx.rollback();
			throw new ConcurrencyException(ExceptionConstants.CONCURRENCYEXCEPTION,sose);
	   	}
	   	catch(HibernateProcessException hpe){
	   		tx.rollback();
	   		throw hpe;
	   	}
	}
	
	
	private void updateCustomerHistory(Session session,Account loan,EditLoanStatus loanStatus){
		if(loan.getCustomer().getCustomerLevel().getLevelId().equals(CustomerConstants.CLIENT_LEVEL_ID)){
			Client client=(Client)session.get(Client.class,loan.getCustomer().getCustomerId());
			if(loanStatus.getNewStatusId().equals(Short.valueOf(AccountStates.LOANACC_WRITTENOFF)) || loanStatus.getNewStatusId().equals(Short.valueOf(AccountStates.LOANACC_RESCHEDULED)) || loanStatus.getNewStatusId().equals(Short.valueOf(AccountStates.LOANACC_CANCEL))) {
				ClientPerformanceHistory clientPerfHistory = client.getPerformanceHistory();
				if(clientPerfHistory != null) {
					clientPerfHistory.setLoanCycleNumber(clientPerfHistory.getLoanCycleNumber()-1);
					if(loanStatus.getNewStatusId().equals(Short.valueOf(AccountStates.LOANACC_WRITTENOFF)) || loanStatus.getNewStatusId().equals(Short.valueOf(AccountStates.LOANACC_RESCHEDULED))) {
						clientPerfHistory.setNoOfActiveLoans(clientPerfHistory.getNoOfActiveLoans()-1);
					}
					session.update(clientPerfHistory);
				}
			}
		}
	}

	/**
	 * This is the helper method to create account flag detail
	 * @param loanStatus  instance of LoanStatus
	 * @return  instance of AccountFlagDetail
	 */
	private AccountFlagDetail getAccountFlagDetail(EditLoanStatus loanStatus){
		AccountFlagDetail flagDetail = null;
		if(loanStatus.getFlagId()!=null && loanStatus.getFlagId()!=0){
			flagDetail=new AccountFlagDetail();
			flagDetail.setAccountId(loanStatus.getAccountId());
			flagDetail.setCreatedBy(loanStatus.getNotes().getOfficerId());
			flagDetail.setCreatedDate(new Date(new java.util.Date().getTime()));
			flagDetail.setFlagId(loanStatus.getFlagId());
		}
		return flagDetail;
	}
	
	/**
	 * This is the helper method to create status change history object
	 * account status change history
	 * @param loanStatus  instance of LoanStatus 
	 */
	private AccountStatusChangeHistory getAccountStatusChangeHistory(EditLoanStatus loanStatus){
		AccountStatusChangeHistory history = new AccountStatusChangeHistory();
		history.setAccountId(loanStatus.getAccountId());
		history.setChangedDate(new Date(new java.util.Date().getTime()));
		history.setOldStatus(loanStatus.getCurrentStatusId());
		history.setNewStatus(loanStatus.getNewStatusId());
		history.setPersonnelId(loanStatus.getNotes().getOfficerId());
		return history;
	}
	
	/**
	 * This method returns the list of next available status 
	 * to which a loan account can move as per the loan account state flow diagram
	 * @param localeId  user locale
	 * @param statusId status id
	 * @return List next applicable status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */		
	public List getStatusList(short localeId, short status)throws ApplicationException,SystemException{
		List<StatusMaster> statusList = new ArrayList<StatusMaster>();
		List<AccountState> applicabelStatusList=null;
		List<StatusMaster> statusMasterList=null;
		List<FlagMaster> flagMasterList=null;
		if(status!=AccountStates.LOANACC_OBLIGATIONSMET &&
				status!=AccountStates.LOANACC_RESCHEDULED &&
					status!=AccountStates.LOANACC_WRITTENOFF && 
					status!=AccountStates.LOANACC_CANCEL){
			//get list of all status
				applicabelStatusList=new AccountsDAO().getStatesCurrentlyInUse(new Short(AccountTypes.LOANACCOUNT));
			// get status names as per locales
				statusMasterList=getStatusMaster(localeId,new Short(AccountTypes.LOANACCOUNT));
			//get flag names as per locales
				flagMasterList=getFlagMaster(localeId,new Short(AccountTypes.LOANACCOUNT));
		}
		
		
		
		switch (status){
			case AccountStates.LOANACC_PARTIALAPPLICATION :
				if(isStatusDefined(applicabelStatusList,AccountStates.LOANACC_PENDINGAPPROVAL))
					statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_PENDINGAPPROVAL));
				else
					statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_APPROVED));
				
				statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_CANCEL));
		  		break;
			case AccountStates.LOANACC_PENDINGAPPROVAL:
				statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_PARTIALAPPLICATION));
				statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_APPROVED));
				if(isStatusDefined(applicabelStatusList,AccountStates.LOANACC_DBTOLOANOFFICER))
					statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_DBTOLOANOFFICER));
				else
					//statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_ACTIVEINGOODSTANDING));
				statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_CANCEL));
				
				break;
			case AccountStates.LOANACC_APPROVED:
				if(isStatusDefined(applicabelStatusList,AccountStates.LOANACC_DBTOLOANOFFICER))
					statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_DBTOLOANOFFICER));
				else
					//statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_ACTIVEINGOODSTANDING));
		  		statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_CANCEL));
		  		break;
			case AccountStates.LOANACC_DBTOLOANOFFICER:
				//statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_ACTIVEINGOODSTANDING));
		  		statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_CANCEL));
		  		break;
			case AccountStates.LOANACC_ACTIVEINGOODSTANDING:
				//statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_BADSTANDING));
		  		//statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_OBLIGATIONSMET));
		  		statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_RESCHEDULED));
		  		statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_WRITTENOFF));
		  		break;
			case AccountStates.LOANACC_BADSTANDING:
				//statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_ACTIVEINGOODSTANDING));
				statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_RESCHEDULED));
		  		statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_WRITTENOFF));
		  		break;
			//case AccountStates.LOANACC_CANCEL:
			//	statusList.add(getStatusWithFlags(statusMasterList,flagMasterList,AccountStates.LOANACC_PARTIALAPPLICATION));
		  default:
		  }
		  return statusList;
	  }
	
	/**
	 * This method is the helper method that returns a status along with its assoicated flags
	 * @param localeId  user locale
	 * @param statusId status id
	 * @return SearchResults status list
	 * @throws ApplicationException
	 * @throws SystemException
	 */	
	private StatusMaster getStatusWithFlags(List<StatusMaster>statusMasterList,List<FlagMaster>flagList,short statusId) throws ApplicationException,SystemException{
	   	StatusMaster statusMaster = getStatus(statusMasterList,statusId);
	   	statusMaster.setFlagList(getStatusFlags(flagList,statusId));
	  	return statusMaster;
	}
	
	/**
	 * This method is the helper method that returns a status along with its assoicated flags
	 * @param localeId  user locale
	 */
	private List<FlagMaster>getStatusFlags(List<FlagMaster>flagMasterList,short statusId){
		List<FlagMaster> flagList = null;
		if(flagMasterList!=null){
			for(int i=0;i<flagMasterList.size();i++){
				FlagMaster master=flagMasterList.get(i);
				if(master.getStatusId().shortValue()==statusId){
					if(flagList==null)
						flagList = new ArrayList<FlagMaster>();
					flagList.add(master);
				}
					
			}
		}
		return flagList;
	}
	
	/**
	 * This method is a helper that that finds a satus in the master list based on passed in statusId 
	 * @param statusMasterList, master status list
	 * @param statusId is the id of configurable status that need to be checked
	 * @return List of status lookups values as per locales
	 */	
	private StatusMaster getStatus(List<StatusMaster>statusMasterList,short statusId){
		if(null!=statusMasterList){
			for(int i=0;i<statusMasterList.size();i++){
				StatusMaster sm =(StatusMaster)statusMasterList.get(i);
				if(sm.getStatusId().shortValue()==statusId)
					return sm;
			}
		}
		return null;
	}

	/**
	 * This method returns all status names based on locales 
	 * @param localeId, tells the locales for which status names are to be obtained
	 * @param statusId is the id of configurable status that need to be checked
	 * @return List of status lookups values as per locales
	 * @throws ApplicationException
	 * @throws SystemException 
	 */	
	private List<StatusMaster>getStatusMaster(short localeId,short prdTypeId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
		
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.ACCOUNT_STATUS, LoanConstants.STATUS_LIST);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("prdTypeId",prdTypeId);
		SearchResults sr = masterDataRetriever.retrieve();
		return (sr.getValue()!=null)?(List<StatusMaster>)sr.getValue():null;
	}
	/**
	 * This method returns all flag names based on locales 
	 * @param localeId, tells the locales for which status names are to be obtained
	 * @param statusId is the id of configurable status that need to be checked
	 * @return List of status lookups values as per locales
	 * @throws ApplicationException
	 * @throws SystemException 
	 */	
	private List<FlagMaster>getFlagMaster(short localeId,short prdTypeId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
		
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.ACCOUNT_FLAGS, LoanConstants.FLAG_LIST);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("prdTypeId",prdTypeId);
		SearchResults sr = masterDataRetriever.retrieve();
		return (sr.getValue()!=null)?(List<FlagMaster>)sr.getValue():null;
	}
	
	/** 
	 * This method returns the check list associated with a account type and status for the given locale.
	 * All the items in this check list have to be ticked before the account status can be changed
	 * @param localeId, tells the locales for which checklists are to be obtained
	 * @param accountStatusId, id of the status for checklists are to be obtained
	 * @throws ApplicationException
	 * @throws SystemException 
	 */
	public SearchResults getAccountCheckList(short localeId , short accountStatusId, short accountTypeId )throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.ACCOUNT_CHECKLIST, LoanConstants.CHECKLIST);
		//masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("accountTypeId",accountTypeId);
		masterDataRetriever.setParameter("accountStatus",accountStatusId);
		masterDataRetriever.setParameter("checklistStatus",1);
		return masterDataRetriever.retrieve();
	}
	
	/** 
	 * This method returns the name of the status when status id and locale Id is passed in as parameter.
	 * @param localeId, tells the locales for which status name is to be obtained
	 * @param statusId, id of the status for which name is to be obtained
	 * @throws ApplicationException
	 * @throws SystemException 
	 */
	public String getStatusName(short localeId,short statusId) throws ApplicationException,SystemException{
		List<StatusMaster> statusMasterList=getStatusMaster(localeId,new Short(AccountTypes.LOANACCOUNT));
		if(statusMasterList!=null){
			for(int i=0;i<statusMasterList.size();i++){
				StatusMaster sm =(StatusMaster)statusMasterList.get(i);
				if(sm.getStatusId().shortValue()==statusId)
					return sm.getStatusName();
			}
		}
		return null;
	}
	
	
	public String getFlagName(short localeId,short flagId) throws ApplicationException,SystemException{
		List<FlagMaster> flagMasterList=getFlagMaster(localeId,new Short(AccountTypes.LOANACCOUNT));
		for(FlagMaster flagMaster : flagMasterList) {
			if(flagMaster.getFlagId().equals(flagId))
				return flagMaster.getFlagName();
		}
		return null;	
	}
	
	public SearchResults getStatusHistory(Integer accountId,short localeId)throws ApplicationException,SystemException{
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare(NamedQueryConstants.ACCOUNT_STATE_CHANGE_HISTORY, LoanConstants.CHECKLIST);
		masterDataRetriever.setParameter("localeId" ,localeId );
		masterDataRetriever.setParameter("accountId",accountId);
		return masterDataRetriever.retrieve();
	}
	
	/**
	 * This method tells if a configurable status is defined or not 
	 * @param applicabelStatusList, list of all applicable status list
	 * @param statusId is the id of configurable status that need to be checked
	 * @return true if status is defined, otherwise false
	 */		
	private boolean isStatusDefined(List<AccountState> applicabelStatusList,short statusId){
		if(null!=applicabelStatusList){
			for(int i=0;i<applicabelStatusList.size();i++){
				AccountState accountState=applicabelStatusList.get(i);
				if(accountState.getAccountStateId().shortValue()==statusId)
					return (accountState.getCurrentlyInUse().shortValue()==Constants.YES) ?true :false;
			}
		}
		return false;
	}
}
