/**

 * CollectionSheetBO.java    version: 1.0

 

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
package org.mifos.application.collectionsheet.business;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountType;
import org.mifos.application.collectionsheet.persistence.service.CollectionSheetPersistenceService;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class CollectionSheetBO extends BusinessObject {
	
	
	public CollectionSheetBO() {
		
	}
	
	private Integer collSheetID;
	
	private Date collSheetDate;
	
	private Short statusFlag;
	
	private Date runDate;
	
	private Set<CollSheetCustBO> collectionSheetCustomers;
	
	
	public Date getCollSheetDate() {
		return collSheetDate;
	}
	
	
	public void setCollSheetDate(Date collSheetDate) {
		this.collSheetDate = collSheetDate;
	}
	
	
	public Integer getCollSheetID() {
		return collSheetID;
	}
	
	
	public void setCollSheetID(Integer collSheetID) {
		this.collSheetID = collSheetID;
	}
	
	
	public Date getRunDate() {
		return runDate;
	}
	
	
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	
	
	public Short getStatusFlag() {
		return statusFlag;
	}
	
	
	public void setStatusFlag(Short statusFlag) {
		this.statusFlag = statusFlag;
	}
	
	public Set<CollSheetCustBO> getCollectionSheetCustomers() {
		return collectionSheetCustomers;
	}
	
	public void setCollectionSheetCustomers(
			Set<CollSheetCustBO> collectionSheetCustomerSet) {
		this.collectionSheetCustomers = collectionSheetCustomerSet;
	}
	
	/**
	 * It adds the passed collectionSheetCustomer object to the set of collectionsheet customers.
	 * If the set is null it creates a new hash set and adds the collectionsheetCustomer object to it 
	 * setting the bidirectional relation ship.
	 * @param collectionSheetCustomer
	 */
	public void addCollectionSheetCustomer(CollSheetCustBO collectionSheetCustomer){
		collectionSheetCustomer.setCollectionSheet(this);
		if(null == collectionSheetCustomers){
			collectionSheetCustomers = new HashSet<CollSheetCustBO>();
		}
			collectionSheetCustomers.add(collectionSheetCustomer);
		
	}
	
	
	/**
	 *  @return - It returns the collectionSheetCustomer object reference from the set if it finds 
	 *  a collection sheet customer in the collectionSheetCustomers with the same customerId
	 *  else returns null. 
	 */
	public CollSheetCustBO getCollectionSheetCustomerForCustomerId(Integer customerId) {
		if(null != collectionSheetCustomers && collectionSheetCustomers.size()>0){
			for(CollSheetCustBO collectionSheetCustomer : collectionSheetCustomers){
				if(collectionSheetCustomer.getCustId().equals(customerId)){
					return collectionSheetCustomer;
				}
			}
		}
		return null;
		
	}
	
	
	/**
	 * This method takes a list of loan accounts which are due for disbursal and adds them to the collection sheet loan 
	 * details set associated with the corresponding customer id.It first checks if the customer record already
	 * exists in the collectionSheetCustomer set,if it does not exist  it adds the  customer record first and then 
	 * adds the collectionSheetloandetails  object to the customer record. 
	 * @param loanWithDisbursalDate
	 */
	public void addLoanDetailsForDisbursal(List<LoanBO> loanWithDisbursalDate) {
		if(null != loanWithDisbursalDate && loanWithDisbursalDate.size() > 0){
			for(LoanBO loan : loanWithDisbursalDate){
				CollSheetLnDetailsEntity collSheetLnDetail = null;
				CollSheetCustBO collectionSheetCustomer = getCollectionSheetCustomerForCustomerId(loan.getCustomer().getCustomerId());
				if(null == collectionSheetCustomer){
					collectionSheetCustomer = new CollSheetCustBO();
					collectionSheetCustomer.populateCustomerDetails(loan.getCustomer());
					MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after addng customer detals");
					addCollectionSheetCustomer(collectionSheetCustomer);
					
				}
				collSheetLnDetail = new CollSheetLnDetailsEntity();
				collSheetLnDetail.addDisbursalDetails(loan);
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after addng disbursal details of loan detals");
				collectionSheetCustomer.addCollectionSheetLoanDetail(collSheetLnDetail);
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after addng loan details to customer record");
			}
			
		}
		
	}
	
	
	/**
	 *This method updates the collective totals for the collectionsheetCustomer records in the set.
	 *It iterates over the set and updates the totals for each record which the sum of self and
	 *the group or client under it.Hence if the record is that of center it adds the totals of self and
	 *groups and clients under that center or if it is that of a group it sums the totals of self and
	 *clients under it. 
	 */
	public void updateCollectiveTotals(){
		for(CollSheetCustBO collSheetCustomer : collectionSheetCustomers){
			if(collSheetCustomer.getCustLevel() == CustomerConstants.CLIENT_LEVEL_ID){
				int customerId = collSheetCustomer.getCustId();
				int parentCustomerId  = collSheetCustomer.getParentCustomerId();
				CollSheetCustBO collSheetParentCust = getCollectionSheetCustomerForCustomerId(parentCustomerId);
				// it would be null in case a client belongs directly to a branch and does not belong to a group.
				if(null != collSheetParentCust){
					collSheetParentCust.addCollectiveTotalsForChild(collSheetCustomer);
				}
				
			}
			
		}
		
		for(CollSheetCustBO collSheetCustomer : collectionSheetCustomers){
			if(collSheetCustomer.getCustLevel() == CustomerConstants.GROUP_LEVEL_ID){
				int customerId = collSheetCustomer.getCustId();
				int parentCustomerId  = collSheetCustomer.getParentCustomerId();
				CollSheetCustBO collSheetParentCust = getCollectionSheetCustomerForCustomerId(parentCustomerId);
				// it would be null in case center hierarchy does not exist.
				if(null != collSheetParentCust){
					collSheetParentCust.addCollectiveTotalsForChild(collSheetCustomer);
				}
			}
			
		}
	}
	
	
	/**
	 * This creates a collection sheet object in the database, before persisting the object it
	 * implicitly sets the state to started.
	 */
	public void create()throws SystemException,ApplicationException {
		CollectionSheetPersistenceService collSheetPersistService = (CollectionSheetPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.CollectionSheet);
		this.statusFlag = CollectionSheetConstants.COLLECTIONSHEETGENERATIONSTARTED;
		collSheetPersistService.create(this);
		
		
	}
	
	/**
	 * Populates customer and customer account details.
	 * 
	 * This is achieved by retrieving customer objects from the list accountActionDates passed as parameter, 
	 * also if the account associated with that item of accountActionDates is a CustomerAccount
	 * it populates the relevant details from that object like fees , misc penalty etc.
	 * 
	 */
	 void pouplateCustAndCustAccntDetails(List<AccountActionDateEntity> accountActionDates){
		
			for(AccountActionDateEntity accountActionDate : accountActionDates){
				// it might be present in the set if that customer was already added because it has
				//got more than one loan/savings account.
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("checkin if the customer already exists in the list");
				CollSheetCustBO collectionSheetCustomer = getCollectionSheetCustomerForCustomerId(accountActionDate.getCustomer().getCustomerId());
			
				if(null == collectionSheetCustomer){
					collectionSheetCustomer = new CollSheetCustBO();
					CustomerBO customer = accountActionDate.getCustomer();
				
					// add customer details to the fields in collectionSheetCustomer object.
					collectionSheetCustomer.populateCustomerDetails(customer);
					MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after adding customer details");
				}
				// here we need not check if it was already in the list because there can be only
				// one customer account with a customer so it would definitely would not have been 
				// added to the list.
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("account type id is " + accountActionDate.getAccount().getAccountType());
				if(accountActionDate.getAccount().getAccountType().getAccountTypeId().equals(AccountType.CUSTOMERACCOUNT.getValue()) ){
					
					collectionSheetCustomer.populateAccountDetails(accountActionDate);
					MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after adding account details");
				}
				addCollectionSheetCustomer(collectionSheetCustomer);
			}
				
	}
	
	
	/**
	 * 
	 * This method creates an object of CollSheetLnDetailsEntity for every record in accountActiondates if the account 
	 * type associated with that record is a loan account.It populates the due and over due amounts for that CollectionSheetLnDetailsEntity object
	 * and then adds that object to the respective collectionsheetCustBO object. 
	 * @param accountActionDates
	 */
	 void populateLoanAccounts(List<AccountActionDateEntity> accountActionDates)throws SystemException,ApplicationException {
		
		//This method assumes that Customer details for customer id to which the loan account belongs are already
		//  populated because of a call to pouplateCustAndCustAccntDetails() method before calling this method.
		
		// iterate over the accountActionDateList and check if there is any loan account due to meet today
		// if yes add it to the collectionSheetCustomer.
		for(AccountActionDateEntity accountActionDate : accountActionDates){
			System.out.println("t accoutns size: "+ accountActionDates.size());
			if(accountActionDate.getAccount().getAccountType().getAccountTypeId().equals(AccountType.LOANACCOUNT.getValue()) ){
				System.out.println("Loan accoutns size: "+ accountActionDate);
				CollSheetLnDetailsEntity collectionSheetLoanDetail = new CollSheetLnDetailsEntity();
				collectionSheetLoanDetail.addAccountDetails(accountActionDate);
				getCollectionSheetCustomerForCustomerId(accountActionDate.getCustomer().getCustomerId()).addCollectionSheetLoanDetail(collectionSheetLoanDetail);
			}
		}
		
	}
	
	
	/**
	 * This method terates over  the lst of account acton dates and if the account is of type savings account,
	 * it creates a new object of CollSheetSavingsEntityDetaislEntity sets the account totals in that object
	 * and then adds that object to the record with corresponding cutomerid in collectionSheetcustomer set. 
	 * @param accountActionDates
	 */
	 void populateSavingsAccounts(List<AccountActionDateEntity> accountActionDates) throws SystemException,ApplicationException {
		
		//	This method assumes that Customer details for customer id to which the loan account belongs are already
		//  populated because of a call to pouplateCustAndCustAccntDetails() method before calling this method.
		
		// iterate over the accountActionDateList and check if there is any savings account due to meet today
		// if yes add it to the collectionSheetCustomer.
		 for(AccountActionDateEntity accountActionDate : accountActionDates){
			if(accountActionDate.getAccount().getAccountType().getAccountTypeId().equals(AccountType.SAVINGSACCOUNT.getValue())){
				 CollSheetSavingsDetailsEntity collSheetSavingsDetail = new CollSheetSavingsDetailsEntity();
				 collSheetSavingsDetail.addAccountDetails(accountActionDate);
				 getCollectionSheetCustomerForCustomerId(accountActionDate.getCustomer().getCustomerId()).addCollectionSheetSavingsDetail(collSheetSavingsDetail);
			}
		}
	}
	
	
	/**
	 * This sets the collection sheet record with the status id passed and then updates the record in the database.
	 * @param statusId
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void update(Short statusId)throws SystemException,ApplicationException {
		
		CollectionSheetPersistenceService collSheetPersistService = (CollectionSheetPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.CollectionSheet);
		this.statusFlag = statusId;
		collSheetPersistService.update(this);
		
	}


	public void populateAccountActionDates(List<AccountActionDateEntity> accountActionDates)throws SystemException,ApplicationException {
		pouplateCustAndCustAccntDetails(accountActionDates);
		MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after populate customers");
		populateLoanAccounts(accountActionDates);
		MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after populate loan accounts");
		populateSavingsAccounts(accountActionDates);
		MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("after populate after populate savings account");
		
	}


	@Override
	public Short getEntityID() {
		return null;
	}
}
