/**

 * Customer.java    version: xxx



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

package org.mifos.application.customer.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

/**
 * A class that represents a customer entity after being created.
 * 
 * @author ashishsm
 */
public class CustomerBO extends BusinessObject {

	private Integer customerId;

	private String globalCustNum;

	private String displayName;

	private String displayAddress;

	private String externalId;	

	private Short groupFlag;

	private Short trained;

	private Date trainedDate;

	private Date mfiJoiningDate;

	private String searchId;

	private Integer maxChildCount;

	private Short hoUpdated;

	private Short clientConfidential;

	private Date customerActivationDate;

	private CustomerStatusEntity customerStatus;

	private Set<CustomerCustomFieldEntity> customFields;

	private Set<CustomerPositionEntity> customerPositions;

	private Set<CustomerFlagEntity> customerFlags;

	private CustomerBO parentCustomer;

	private Set<AccountBO> accounts;

	private CustomerLevelEntity customerLevel;

	private PersonnelBO personnel;
	
	private PersonnelBO customerFormedByPersonnel;

	private OfficeBO office;

	private CustomerAddressDetailEntity customerAddressDetail;

	private CustomerDetailEntity customerDetail;
	
	private CustomerAccountBO customerAccount;

	private CustomerMeetingEntity customerMeeting;

	private CustomerHierarchyEntity customerHierarchy;

	private CustomerNoteEntity customerNote;

	private CustomerHistoricalDataEntity historicalData;

	private Short blackListed;
	
	private CustomerPersistenceService dbService;
	
	
	public CustomerBO() {
		this.office = new OfficeBO();
		this.customerAddressDetail = new CustomerAddressDetailEntity();
		this.customerDetail = new CustomerDetailEntity();
		this.customerDetail = new CustomerDetailEntity();
		this.customerAccount = new CustomerAccountBO();
		this.customerHierarchy = new CustomerHierarchyEntity();
		this.customerLevel = new CustomerLevelEntity();
		this.customerPositions = new TreeSet<CustomerPositionEntity>();
		this.customerNote = new CustomerNoteEntity();
		this.historicalData = new CustomerHistoricalDataEntity();
		this.accounts = new HashSet<AccountBO>();
		this.customerStatus = new CustomerStatusEntity();
	}

	public CustomerBO(UserContext userContext) {
		super(userContext);

	}

	private Short getBlackListed() {
		return blackListed;
	}

	private void setBlackListed(Short blackListed) {
		this.blackListed = blackListed;
	}

	public boolean isBlackList() {
		return this.blackListed > 0;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {

		this.customerId = customerId;
	}

	public CustomerLevelEntity getCustomerLevel() {
		return this.customerLevel;
	}

	public void setCustomerLevel(CustomerLevelEntity customerLevel) {

		this.customerLevel = customerLevel;
	}

	public String getGlobalCustNum() {
		return this.globalCustNum;
	}

	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}

	public PersonnelBO getPersonnel() {
		return this.personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

	public CustomerStatusEntity getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatusEntity customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayAddress() {
		return this.displayAddress;
	}

	public void setDisplayAddress(String displayAddress) {
		this.displayAddress = displayAddress;
	}

	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}


	private Short getGroupFlag() {
		return this.groupFlag;
	}

	private void setGroupFlag(Short groupFlag) {
		this.groupFlag = groupFlag;
	}

	public boolean isAGroupFlag() {
		return this.groupFlag > 0;
	}

	public void addGroupFlag(boolean groupFlag) {
		this.groupFlag = (short) (groupFlag ? 1 : 0);
	}

	private Short getTrained() {
		return this.trained;
	}

	private void setTrained(Short trained) {
		this.trained = trained;
	}

	public boolean hasTrained() {
		return this.trained > 0;
	}

	public void addTrained(boolean trained) {
		this.trained = (short) (trained ? 1 : 0);
	}

	public Date getTrainedDate() {
		return this.trainedDate;
	}

	public void setTrainedDate(Date trainedDate) {
		this.trainedDate = trainedDate;
	}

	public String getSearchId() {
		return this.searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public Integer getMaxChildCount() {
		return this.maxChildCount;
	}

	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}

	public Short getHoUpdated() {
		return this.hoUpdated;
	}

	public void setHoUpdated(Short hoUpdated) {
		this.hoUpdated = hoUpdated;
	}

	public Short getClientConfidential() {
		return this.clientConfidential;
	}

	public void setClientConfidential(Short clientConfidential) {
		this.clientConfidential = clientConfidential;
	}

	public CustomerAddressDetailEntity getCustomerAddressDetail() {
		return customerAddressDetail;
	}

	public void setCustomerAddressDetail(
			CustomerAddressDetailEntity customerAddressDetail) {
		if (null != customerAddressDetail) {
			customerAddressDetail.setCustomer(this);
		}
		this.customerAddressDetail = customerAddressDetail;
	}

	public CustomerDetailEntity getCustomerDetail() {
		return customerDetail;
	}

	public void setCustomerDetail(CustomerDetailEntity customerDetail) {
		this.customerDetail = customerDetail;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	public CustomerAccountBO getCustomerAccount() {
		for(AccountBO account : accounts) {
			if(account.getAccountType().getAccountTypeId().equals(Short.valueOf(AccountTypes.CUSTOMERACCOUNT)))
				return (CustomerAccountBO)account;
		}
		return null;
	}
	
	public void setCustomerAccount(CustomerAccountBO customerAccount) {
		this.customerAccount = customerAccount;
	}

	public CustomerMeetingEntity getCustomerMeeting() {
		return customerMeeting;
	}

	public void setCustomerMeeting(CustomerMeetingEntity customerMeeting) {
		if (null != customerMeeting)
			customerMeeting.setCustomer(this);
		this.customerMeeting = customerMeeting;
	}

	public CustomerHierarchyEntity getCustomerHierarchy() {
		return customerHierarchy;
	}

	public void setCustomerHierarchy(CustomerHierarchyEntity customerHierarchy) {
		this.customerHierarchy = customerHierarchy;
	}

	public Set<CustomerPositionEntity> getCustomerPositions() {
		return customerPositions;
	}

	private void setCustomerPositions(
			Set<CustomerPositionEntity> customerPositions) {
		this.customerPositions = customerPositions;

	}
	
	public void addCustomerPosition(CustomerPositionEntity customerPosition) {
		customerPosition.setCustomer(this);
		this.customerPositions.add(customerPosition);
	}

	private void setHistoricalData(CustomerHistoricalDataEntity historicalData) {

		if (historicalData != null) {
			historicalData.setMfiJoiningDate(mfiJoiningDate);
		}

		this.historicalData = historicalData;

	}

	public void setCustomerHistoricalData(
			CustomerHistoricalDataEntity historicalData) {

		if (historicalData != null) {
			this.mfiJoiningDate = historicalData.getMfiJoiningDate();
			historicalData.setCustomer(this);

		}

		setHistoricalData(historicalData);

	}

	public CustomerHistoricalDataEntity getHistoricalData() {
		return historicalData;
	}

	public Set<CustomerFlagEntity> getCustomerFlags() {
		return customerFlags;
	}

	private void setCustomerFlags(Set<CustomerFlagEntity> customerFlag) {
		this.customerFlags = customerFlag;
	}
	
	public void addCustomerFlag(CustomerFlagEntity customerFlag) {
		customerFlag.setCustomer(this);
		this.customerFlags.add(customerFlag);
	}

	public CustomerNoteEntity getCustomerNote() {
		return customerNote;
	}

	public void setCustomerNote(CustomerNoteEntity customerNote) {
		this.customerNote = customerNote;
	}

	public Date getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	public void setMfiJoiningDate(Date mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}
	
	public void setSelectedFeeSet(Set<AccountFeesEntity> selectedFeeSet) {
		if (this.customerAccount != null && selectedFeeSet != null) {
			for (AccountFeesEntity fee : selectedFeeSet) {
				fee.setAccount(this.customerAccount);
				this.customerAccount.addAccountFees(fee);
			}
		}
		Set<AccountBO> accountsSet = new HashSet<AccountBO>();
		accountsSet.add(this.customerAccount);
		this.accounts = accountsSet;
	}

	public void setParentCustomer(CustomerBO parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	public CustomerBO getParentCustomer() {
		return parentCustomer;
	}

	public Set<AccountBO> getAccounts() {
		return accounts;
	}

	private void setAccounts(Set<AccountBO> customerAccounts) {
		this.accounts = customerAccounts;
	}
	
	public void addCustomerAccount(CustomerAccountBO customerAccount) {
		customerAccount.setCustomer(this);
		this.accounts.add(customerAccount);
	}

	public Date getCustomerActivationDate() {
		return customerActivationDate;
	}

	public void setCustomerActivationDate(Date activationDate) {
		this.customerActivationDate = activationDate;
	}

	public Set<CustomerCustomFieldEntity> getCustomFields() {
		return customFields;
	}

	private void setCustomFields(Set<CustomerCustomFieldEntity> customFields) {
		this.customFields = customFields;
	}
	
	public void addCustomField(CustomerCustomFieldEntity customField) {
		if(customField !=null){
		customField.setCustomer(this);
		this.customFields.add(customField);
		}
	}

	public void setCustomField(CustomerCustomFieldEntity customerCustomFieldView) {
		this.customFields.add(customerCustomFieldView);
	}
	
	public List<CustomerBO> getChildren(Short customerLevel)throws PersistenceException,ServiceException{
		return getDBService().getChildrenForParent(getSearchId(), getOffice().getOfficeId(),customerLevel);
	}
	
	public PersonnelBO getCustomerFormedByPersonnel() {
		return customerFormedByPersonnel;
	}

	public void setCustomerFormedByPersonnel(PersonnelBO customerFormedByPersonnel) {
		this.customerFormedByPersonnel = customerFormedByPersonnel;
	}
	protected CustomerPersistenceService getDBService()throws ServiceException{
		if(dbService==null){
			dbService=(CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(
				PersistenceServiceName.Customer);
		}
		return dbService;
	}
	
		public boolean isAdjustPossibleOnLastTrxn() {
		return customerAccount.isAdjustPossibleOnLastTrxn();
	}
	
	public void adjustPmnt(String adjustmentComment) throws ApplicationException,SystemException {
		getCustomerAccount().adjustPmnt(adjustmentComment);
	}
	public boolean isCustomerActive()
	{
		return false;
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Customer;
	}
	
	public void generatePortfolioAtRisk()throws PersistenceException, ServiceException{}
	
	public Money getBalanceForAccountsAtRisk(){
		Money amount=new Money();
		for(AccountBO account : getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)
					&& ((LoanBO)account).isAccountActive()){
				LoanBO loan=(LoanBO)account;
				if(loan.hasPortfolioAtRisk())
					amount=amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}
	
	public Money getOutstandingLoanAmount(){
		Money amount=new Money();
		for(AccountBO account : getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)
					&& ((LoanBO)account).isAccountActive()){
				LoanBO loan=(LoanBO)account;
				amount=amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}
	
	public Money getTotalLoanAmount(){
		Money amount=new Money();
		for(AccountBO account : getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)){
				LoanBO loan=(LoanBO)account;
				amount=amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}

	public Money getDelinquentPortfolioAmount(){
		Money amountOverDue=new Money();
		Money totalOutStandingAmount=new Money();
		for(AccountBO accountBO : getAccounts()){
			if(accountBO.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)){
				amountOverDue=amountOverDue.add(accountBO.getTotalPrincipalAmountInArrears());
				totalOutStandingAmount=totalOutStandingAmount.add(((LoanBO)accountBO).getRemainingPrincipalAmount());
			}
		}
		if(totalOutStandingAmount.getAmountDoubleValue()!=0.0)
			return new Money(String.valueOf(amountOverDue.getAmountDoubleValue()/totalOutStandingAmount.getAmountDoubleValue()));
		return new Money();
	}
	
	
	public CustomerPerformanceHistory getCustomerPerformanceHistory(){
		return getPerformanceHistory();
	}
	protected CustomerPerformanceHistory getPerformanceHistory() {
		return null;
	}
	
	public Money getSavingsBalance(){
		Money amount=new Money();
		for(AccountBO account : getAccounts()){
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.SAVING_TYPE)){
				SavingsBO savingsBO=(SavingsBO)account;
				amount=amount.add(savingsBO.getSavingsBalance());
			}
		}
		return amount;
	}
}
