/**

 * CustomerBO.java    version: xxx



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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

/**
 * A class that represents a customer entity after being created.
 */
public abstract class CustomerBO extends BusinessObject {

	private final Integer customerId;

	private String globalCustNum;

	private String displayName;

	private String displayAddress;

	private String externalId;

	private Short trained;

	private Date trainedDate;

	private Date mfiJoiningDate;

	private String searchId;

	private Integer maxChildCount;

	private Date customerActivationDate;

	private CustomerStatusEntity customerStatus;

	private Set<CustomerCustomFieldEntity> customFields;

	private Set<CustomerPositionEntity> customerPositions;

	private Set<CustomerFlagDetailEntity> customerFlags;

	private CustomerBO parentCustomer;

	private Set<AccountBO> accounts;

	private final CustomerLevelEntity customerLevel;

	private PersonnelBO personnel;

	private final PersonnelBO formedByPersonnel;

	private OfficeBO office;

	private CustomerAddressDetailEntity customerAddressDetail;

	private CustomerMeetingEntity customerMeeting;

	private Set<CustomerHierarchyEntity> customerHierarchies;

	private Set<CustomerMovementEntity> customerMovements;
	
	private CustomerHistoricalDataEntity historicalData;

	private Short blackListed;

	private Set<CustomerNoteEntity> customerNotes;
	
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERLOGGER);
	
	private Set<CustomerBO> children;

	protected CustomerBO() {
		super();
		this.customerId = null;
		this.globalCustNum = null;
		this.customerLevel = null;
		this.formedByPersonnel = null;
	}

	protected CustomerBO(UserContext userContext, String displayName,
			CustomerLevel customerLevel, CustomerStatus customerStatus,
			String externalId, Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedBy, Short officeId, CustomerBO parentCustomer,
			MeetingBO meeting, Short loanOfficerId) throws CustomerException {
		
		super(userContext);
		try{
		customerHierarchies = new HashSet<CustomerHierarchyEntity>();
		customerMovements = new HashSet<CustomerMovementEntity>();
		customerPositions = new HashSet<CustomerPositionEntity>();
		validateFields(displayName, customerStatus, officeId, parentCustomer);
		this.customFields = new HashSet<CustomerCustomFieldEntity>();
		this.accounts = new HashSet<AccountBO>();
		this.customerNotes = new HashSet<CustomerNoteEntity>();
		this.customerPositions = new HashSet<CustomerPositionEntity>();		
		this.externalId = externalId;
		this.mfiJoiningDate = mfiJoiningDate;
		this.displayName = displayName;
		this.customerLevel = new CustomerLevelEntity(customerLevel);

		createAddress(address);
		
		if(parentCustomer!=null)
			inheritDetailsFromParent(parentCustomer);
		else{
			if (loanOfficerId != null)
				this.personnel = new PersonnelPersistence().getPersonnel(loanOfficerId);
			this.customerMeeting = createCustomerMeeting(meeting);
			if(officeId!=null)
				this.office = new OfficePersistence().getOffice(officeId);
		}

		if (formedBy != null)
			this.formedByPersonnel = new PersonnelPersistence()
					.getPersonnel(formedBy);
		else
			this.formedByPersonnel = null;

		this.parentCustomer = parentCustomer;

		createCustomFields(customFields);
		
		this.customerStatus = new CustomerStatusEntity(customerStatus);
		this.maxChildCount = 0;
		this.blackListed = YesNoFlag.NO.getValue();
		this.customerId = null;
		this.historicalData = null;
		this.customerFlags = new HashSet<CustomerFlagDetailEntity>();

		this.addCustomerAccount(createCustomerAccount(fees));

		this.setCreateDetails();
		}
		catch (PersistenceException e) {
			throw new CustomerException(e);
		}
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public CustomerLevelEntity getCustomerLevel() {
		return this.customerLevel;
	}

	public String getGlobalCustNum() {
		return this.globalCustNum;
	}

	public PersonnelBO getPersonnel() {
		return this.personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public CustomerStatusEntity getCustomerStatus() {
		return customerStatus;
	}		

	public void setCustomerStatus(CustomerStatusEntity customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getDisplayAddress() {
		return displayAddress;
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

	protected void setTrained(Short trained) {
		this.trained = trained;
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

	protected void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	
	public Integer getMaxChildCount() {
		return this.maxChildCount;
	}

	public Date getMfiJoiningDate() {
		return mfiJoiningDate;
	}

	public void setMfiJoiningDate(Date mfiJoiningDate) {
		this.mfiJoiningDate = mfiJoiningDate;
	}

	public Date getCustomerActivationDate() {
		return customerActivationDate;
	}
	
	protected void setCustomerActivationDate(Date customerActivationDate) {
		this.customerActivationDate = customerActivationDate;
	}
	
	public void setParentCustomer(CustomerBO parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

	public CustomerBO getParentCustomer() {
		return parentCustomer;
	}
	
	public Set<CustomerBO> getChildren() {
		return children;
	}

	public CustomerAddressDetailEntity getCustomerAddressDetail() {
		return customerAddressDetail;
	}

	public void setCustomerAddressDetail(
			CustomerAddressDetailEntity customerAddressDetail) {
		this.customerAddressDetail = customerAddressDetail;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	public CustomerMeetingEntity getCustomerMeeting() {
		return customerMeeting;
	}

	public void setCustomerMeeting(CustomerMeetingEntity customerMeeting) {
		this.customerMeeting = customerMeeting;
	}
	
	public Set<AccountBO> getAccounts() {
		return accounts;
	}	
	
	public Set<CustomerCustomFieldEntity> getCustomFields() {
		return customFields;
	}
	
	public Set<CustomerPositionEntity> getCustomerPositions() {
		return customerPositions;
	}
	
	public Set<CustomerFlagDetailEntity> getCustomerFlags() {
		return customerFlags;
	}
	
	public Set<CustomerNoteEntity> getCustomerNotes() {
		return customerNotes;
	}

	public PersonnelBO getCustomerFormedByPersonnel() {
		return formedByPersonnel;
	}
	
	public void setTrained(boolean trained) {
		this.trained = (short) (trained ? 1 : 0);
	}
	
	public void addCustomerHierarchy(CustomerHierarchyEntity hierarchy) {
		if (hierarchy != null) {
			this.customerHierarchies.add(hierarchy);
		}
	}
	
	public void addCustomerPosition(CustomerPositionEntity customerPosition) {
		this.customerPositions.add(customerPosition);
	}	
	
	public void addCustomerMovement(CustomerMovementEntity customerMovement) {
		if (customerMovement != null) {
			this.customerMovements.add(customerMovement);
		}
	}
	
	public void addCustomField(CustomerCustomFieldEntity customField) {
		if (customField != null) {
			this.customFields.add(customField);
		}
	}
	
	public void addCustomerNotes(CustomerNoteEntity customerNote) {
		this.customerNotes.add(customerNote);
	}
	
	public void addCustomerFlag(CustomerStatusFlagEntity customerStatusFlagEntity) {
		CustomerFlagDetailEntity customerFlag = new CustomerFlagDetailEntity(
				this, customerStatusFlagEntity,this.getUserContext().getId(),new Date());
		this.customerFlags.add(customerFlag);
	}
	
	public boolean isTrained() {
		return trained.equals(YesNoFlag.YES.getValue());
	}
	
	public boolean isBlackListed() {
		if(blackListed != null)
			return blackListed.equals(YesNoFlag.YES.getValue());
		return false;
	}
	
	public Address getAddress() {
		return customerAddressDetail != null ? customerAddressDetail
				.getAddress() : null;
	}

	public CustomerStatus getStatus(){
		return CustomerStatus.getStatus(customerStatus.getId());	
	}
	
	public void save() throws CustomerException {
		try {
			new CustomerPersistence().createOrUpdate(this);
			String gCustNum = generateSystemId();
			globalCustNum = (gCustNum);
			new CustomerPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CustomerException(
					CustomerConstants.CREATE_FAILED_EXCEPTION, e);
		}
	}

	public void update() throws CustomerException {
		try {
			setUpdateDetails();
			new CustomerPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CustomerException(
					CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
		}
	}

	public void update(UserContext userContext, String externalId, Address address,  List<CustomFieldView> customFields, List<CustomerPositionView> customerPositions) throws CustomerException {
		this.setUserContext(userContext);
		this.setExternalId(externalId);
		updateAddress(address);
		updateCustomFields(customFields);
		updateCustomerPositions(customerPositions);
		this.update();
	}
	
	
	public void updateAddress(Address address) throws CustomerException {
		if(getCustomerAddressDetail()==null)
			setCustomerAddressDetail(new CustomerAddressDetailEntity(this, address));
		else			
			getCustomerAddressDetail().setAddress(address);
	}

	public CustomerAccountBO getCustomerAccount() {
		CustomerAccountBO customerAccount = null;
		for (AccountBO account : accounts) {
			if (account.getAccountType().getAccountTypeId().equals(
					Short.valueOf(AccountTypes.CUSTOMERACCOUNT.getValue())))
				customerAccount = (CustomerAccountBO) account;
		}
		return customerAccount;
	}

	public List<LoanBO> getActiveAndApprovedLoanAccounts(Date transactionDate) {
		List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
		for (AccountBO account : accounts) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())) {
				short accounStateId = account.getAccountState().getId()
						.shortValue();
				LoanBO loan = (LoanBO) account;
				if (accounStateId == AccountStates.LOANACC_ACTIVEINGOODSTANDING
						|| accounStateId == AccountStates.LOANACC_BADSTANDING) {
					loanAccounts.add(loan);
				} else if (accounStateId == AccountStates.LOANACC_APPROVED
						|| accounStateId == AccountStates.LOANACC_DBTOLOANOFFICER) {
					if (transactionDate.compareTo(loan.getDisbursementDate()) >= 0)
						loanAccounts.add(loan);
				}
			}
		}
		return loanAccounts;
	}

	public List<SavingsBO> getActiveSavingsAccounts() {
		List<SavingsBO> savingsAccounts = new ArrayList<SavingsBO>();
		for (AccountBO account : accounts) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.SAVINGSACCOUNT.getValue())
					&& account.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED)
				savingsAccounts.add((SavingsBO) account);
		}
		return savingsAccounts;
	}

	public List<CustomerNoteEntity> getRecentCustomerNotes() {
		List<CustomerNoteEntity> notes = new ArrayList<CustomerNoteEntity>();
		int count = 0;
		for (CustomerNoteEntity customerNote : getCustomerNotes()) {
			if (count > 2)
				break;
			notes.add(customerNote);
			count++;
		}
		return notes;
	}

	public CustomerHierarchyEntity getActiveCustomerHierarchy() {
		CustomerHierarchyEntity hierarchy = null;
		for(CustomerHierarchyEntity customerHierarchyEntity : customerHierarchies){
			if(customerHierarchyEntity.isActive()){
				hierarchy = customerHierarchyEntity;
				break;
			}
		}
		return hierarchy;
	}

	public CustomerMovementEntity getActiveCustomerMovement() {
		CustomerMovementEntity movement = null;
		for(CustomerMovementEntity customerMovementEntity : customerMovements){
			if(customerMovementEntity.isActive()){
				movement = customerMovementEntity;
				break;
			}
		}
		return movement;
	}
	

	public void updateHistoricalData(CustomerHistoricalDataEntity historicalData) {
		if (historicalData != null) 
			mfiJoiningDate = historicalData.getMfiJoiningDate();
		this.historicalData = historicalData;	
	}
	
	public CustomerHistoricalDataEntity getHistoricalData() {
		if(historicalData != null)
			historicalData.setMfiJoiningDate(mfiJoiningDate);
		return historicalData;
	}
	
	public List<CustomerBO> getChildren(CustomerLevel customerLevel, ChildrenStateType stateType)
	throws CustomerException {
		try{
			return new CustomerPersistence().getChildren(getSearchId(),
					getOffice().getOfficeId(), customerLevel, stateType);
		}catch(PersistenceException pe){
			throw new CustomerException(pe);
		}
	}

	public void adjustPmnt(String adjustmentComment)
			throws ApplicationException, SystemException {
		getCustomerAccount().adjustPmnt(adjustmentComment);
	}

	public abstract boolean isActive();

	public Money getBalanceForAccountsAtRisk() {
		Money amount = new Money();
		for (AccountBO account : getAccounts()) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) account).isAccountActive()) {
				LoanBO loan = (LoanBO) account;
				if (loan.hasPortfolioAtRisk())
					amount = amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}

	public Money getOutstandingLoanAmount() {
		Money amount = new Money();
		for (AccountBO account : getAccounts()) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) account).isAccountActive()) {
				amount = amount.add(((LoanBO) account)
						.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}

	public Integer getActiveLoanCounts() {
		Integer countOfActiveLoans = 0;
		for (AccountBO account : getAccounts()) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) account).isAccountActive()) {
				countOfActiveLoans++;
			}
		}
		return countOfActiveLoans;
	}

	public Money getDelinquentPortfolioAmount() {
		Money amountOverDue = new Money();
		Money totalOutStandingAmount = new Money();
		for (AccountBO accountBO : getAccounts()) {
			if (accountBO.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) accountBO).isAccountActive()) {
				amountOverDue = amountOverDue.add(((LoanBO) accountBO)
						.getTotalPrincipalAmountInArrears());
				totalOutStandingAmount = totalOutStandingAmount
						.add(((LoanBO) accountBO).getLoanSummary()
								.getOriginalPrincipal());
			}
		}
		if (totalOutStandingAmount.getAmountDoubleValue() != 0.0)
			return new Money(String.valueOf(amountOverDue
					.getAmountDoubleValue()
					/ totalOutStandingAmount.getAmountDoubleValue()));
		return new Money();
	}

	public abstract CustomerPerformanceHistory getPerformanceHistory();

	public Money getSavingsBalance() {
		Money amount = new Money();
		for (AccountBO account : getAccounts()) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.SAVINGSACCOUNT.getValue())) {
				SavingsBO savingsBO = (SavingsBO) account;
				amount = amount.add(savingsBO.getSavingsBalance());
			}
		}
		return amount;
	}

public void changeStatus(Short newStatusId, Short flagId, String comment)
	throws	CustomerException{
		logger.debug("In CustomerBO::changeStatus(), newStatusId: " + newStatusId);
		Short oldStatusId = getCustomerStatus().getId();
		validateStatusChange(newStatusId);
		if(getPersonnel()!=null)
			validateLoanOfficerAssigned();
		if(checkStatusChangeCancelToPartial(CustomerStatus.getStatus(oldStatusId),CustomerStatus.getStatus(newStatusId))) {
			if(!isBlackListed())
				getCustomerFlags().clear();
		}
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomerStatusEntity customerStatus;
		try {
			customerStatus = (CustomerStatusEntity) masterPersistence
					.getPersistentObject(CustomerStatusEntity.class, newStatusId);
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		}
		customerStatus.setLocaleId(this.getUserContext().getLocaleId());
		CustomerStatusFlagEntity customerStatusFlagEntity = null;
		if (flagId != null) {
			try {
				customerStatusFlagEntity = (CustomerStatusFlagEntity) masterPersistence
						.getPersistentObject(CustomerStatusFlagEntity.class, flagId);
			} catch (PersistenceException e) {
				throw new CustomerException(e);
			}
		}
		CustomerNoteEntity customerNote = createCustomerNotes(comment);
		this.setCustomerStatus(customerStatus);
		this.addCustomerNotes(customerNote);
		if (customerStatusFlagEntity != null) {
			customerStatusFlagEntity.setLocaleId(this.getUserContext()
					.getLocaleId());
			this.addCustomerFlag(customerStatusFlagEntity);
			if(customerStatusFlagEntity.isBlackListed())
				blackListed = YesNoFlag.YES.getValue();
		}
		if (checkNewStatusIsFirstTimeActive(oldStatusId, newStatusId)) {
			try {
				this.getCustomerAccount().generateCustomerFeeSchedule();
			} catch (AccountException ae) {
				throw new CustomerException(ae);
			}
		}
		this.update();
		logger.debug("In CustomerBO::changeStatus(), successfully changed status, newStatusId: " + newStatusId);
	}
		
	private void validateLoanOfficerAssigned() throws CustomerException {
		logger.debug("In CustomerBO::validateLoanOfficerAssigned()");
		if(!(personnel.isActive())||!(personnel.getOffice().getOfficeId().equals(office.getOfficeId()) ||!(personnel.isLoanOfficer())))
				throw new CustomerException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
		logger.debug("In CustomerBO::validateLoanOfficerAssigned(), completed");
	}
		
	public List<LoanBO> getOpenLoanAccounts(){
		List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
		for (AccountBO account : getAccounts()) {
			if (account.getType().equals(AccountTypes.LOANACCOUNT) && account.isOpen())
					loanAccounts.add((LoanBO)account);
		}
		return loanAccounts;
	}	
	
	public List<SavingsBO> getOpenSavingAccounts(){
		List<SavingsBO> savingAccounts = new ArrayList<SavingsBO>();
		for (AccountBO account : getAccounts()) {
			if (account.getType().equals(AccountTypes.SAVINGSACCOUNT) && account.isOpen())
				savingAccounts.add((SavingsBO)account);
		}
		return savingAccounts;
	}
	
	public boolean isAnyLoanAccountOpen(){
		for (AccountBO account : getAccounts()) {
			if (account.getType().equals(AccountTypes.LOANACCOUNT) && account.isOpen())
					return true;
		}
		return false;
	}
	
	public boolean isAnySavingsAccountOpen(){
		for (AccountBO account : getAccounts()) {
			if (account.getType().equals(AccountTypes.SAVINGSACCOUNT) && account.isOpen())
				return true;
		}
		return false;
	}
	
	public void resetPositionsAssignedToClient(Integer clientId){
		if(getCustomerPositions()!=null){
			for(CustomerPositionEntity position: getCustomerPositions())
				if(position.getCustomer()!=null && position.getCustomer().getCustomerId().equals(clientId))
					position.setCustomer(null);
		}
	}
	
	public void incrementChildCount(){
		this.maxChildCount = this.getMaxChildCount().intValue()+1;
	}
	
	public void decrementChildCount(){
		this.maxChildCount=this.getMaxChildCount().intValue()-1;
	}
	
	protected void validateMeeting(MeetingBO meeting) throws CustomerException {
		if (meeting == null)
			throw new CustomerException(CustomerConstants.INVALID_MEETING);
	}

	protected void validateOffice(Short officeId) throws CustomerException {
		if (officeId == null)
			throw new CustomerException(CustomerConstants.INVALID_OFFICE);
	}
	
	protected void validateLO(Short loanOfficerId) throws CustomerException {
		if (loanOfficerId == null)
			throw new CustomerException(CustomerConstants.INVALID_LOAN_OFFICER);
	}

	protected void validateLO(PersonnelBO loanOfficer) throws CustomerException {
		if(getPersonnel()==null || getPersonnel().getPersonnelId()==null){
			throw new CustomerException(CustomerConstants.INVALID_LOAN_OFFICER);
		}
	}
	
	protected CustomerMeetingEntity createCustomerMeeting(MeetingBO meeting) {
		return meeting != null ? new CustomerMeetingEntity(this, meeting)
				: null;
	}

	protected abstract boolean checkNewStatusIsFirstTimeActive(Short oldStatus,
			Short newStatusId);
	private  boolean checkStatusChangeCancelToPartial(CustomerStatus oldStatus,
			CustomerStatus newStatus){
		if((oldStatus.equals(CustomerStatus.GROUP_CANCELLED) || oldStatus.equals(CustomerStatus.CLIENT_CANCELLED)) && 
				(newStatus.equals(CustomerStatus.GROUP_PARTIAL) || newStatus.equals(CustomerStatus.CLIENT_PARTIAL))) {
			return true;
		}
		return false;
	}

	protected abstract void validateStatusChange(Short newStatusId)
			throws CustomerException;

	protected boolean isSameBranch(OfficeBO officeObj){
		return this.office.getOfficeId().equals(officeObj.getOfficeId());
	}
	
	protected void updateCustomFields(List<CustomFieldView> customFields){
		if(customFields!=null){
			for(CustomFieldView fieldView : customFields){
				if (fieldView.getFieldType().equals(CustomFieldType.DATE.getValue()) && StringUtils.isNullAndEmptySafe(fieldView
						.getFieldValue()))
					fieldView.convertDateToUniformPattern(getUserContext().getPereferedLocale());
				for(CustomerCustomFieldEntity fieldEntity: getCustomFields())
					if(fieldView.getFieldId().equals(fieldEntity.getFieldId()))
						fieldEntity.setFieldValue(fieldView.getFieldValue());
			}
		}
	}
	
	protected void updateCustomerPositions(List<CustomerPositionView> customerPositions){
		if(customerPositions!=null){
			for(CustomerPositionView positionView: customerPositions){
				boolean isPositionFound = false;
				for(CustomerPositionEntity positionEntity: getCustomerPositions()){
					if(positionView.getPositionId().equals(positionEntity.getPosition().getId())){
						positionEntity.setCustomer(getCustomer(positionView.getCustomerId()));
						isPositionFound = true;
						break;
					}			
				}
				if(!isPositionFound){
					addCustomerPosition(new CustomerPositionEntity(new PositionEntity(positionView.getPositionId()),getCustomer(positionView.getCustomerId()), this));
				}
			}
		}
	}
	
	protected void updateLoanOfficer(Short loanOfficerId){
		if(isLOChanged(loanOfficerId)){
			new CustomerPersistence().updateLOsForAllChildren(loanOfficerId, getSearchId(), getOffice().getOfficeId());
			if(loanOfficerId!=null)
				this.personnel = new PersonnelPersistence().getPersonnel(loanOfficerId);
			else
				this.personnel = null;
		}
	}
	
	private boolean isLOChanged(Short loanOfficerId){
		return ((getPersonnel()==null && loanOfficerId!=null) 
				|| (getPersonnel()!=null && loanOfficerId==null)
				|| (getPersonnel()!=null && loanOfficerId!=null && !getPersonnel().getPersonnelId().equals(loanOfficerId)));
		
	}

	protected void makeCustomerMovementEntries(OfficeBO officeToTransfer){
		CustomerMovementEntity currentCustomerMovement = getActiveCustomerMovement();
		if(currentCustomerMovement == null){
			currentCustomerMovement = new CustomerMovementEntity(this, getCreatedDate());
			this.addCustomerMovement(currentCustomerMovement);
		}
		
		currentCustomerMovement.makeInActive(userContext.getId());
		this.setOffice(officeToTransfer);
		CustomerMovementEntity newCustomerMovement = new CustomerMovementEntity(this, new Date());
		this.addCustomerMovement(newCustomerMovement);
	}
	
	protected void changeParentCustomer(CustomerBO newParent)throws CustomerException{
		CustomerBO oldParent = getParentCustomer();
		setParentCustomer(newParent);
		
		CustomerHierarchyEntity currentHierarchy = getActiveCustomerHierarchy();
		currentHierarchy.makeInActive(userContext.getId());
		this.addCustomerHierarchy(new CustomerHierarchyEntity(this,newParent));
		
		this.handleParentTransfer();		
		oldParent.decrementChildCount();
		newParent.incrementChildCount();
		setSearchId(newParent.getSearchId()+ "."+ String.valueOf(newParent.getMaxChildCount()));
		
		oldParent.setUserContext(getUserContext());
		oldParent.update();
		newParent.setUserContext(getUserContext());
		newParent.update();
	}
	
	protected String generateSystemId(){
		String systemId="";
		int numberOfZeros = CustomerConstants.SYSTEM_ID_LENGTH - String.valueOf(getCustomerId()).length();
		
		for(int i=0;i<numberOfZeros;i++)
			systemId = systemId + "0";
		
		return getOffice().getGlobalOfficeNum() + "-" + systemId + getCustomerId();
	}
	
	protected void handleParentTransfer()throws CustomerException{
		setPersonnel(getParentCustomer().getPersonnel());
		if(getParentCustomer().getCustomerMeeting()!=null){
			if(getCustomerMeeting()!=null)
				getCustomerMeeting().setMeeting(getParentCustomer().getCustomerMeeting().getMeeting());
			else
				setCustomerMeeting(createCustomerMeeting(getParentCustomer().getCustomerMeeting().getMeeting()));
			getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
		}
		else{
			if(getCustomerMeeting()!=null){
				try{
					new CustomerPersistence().deleteMeeting(this);
				}catch(PersistenceException pe){
					new CustomerException(pe);
				}
			}
		}
	}
	
	private void createAddress(Address address){
		if (address != null) {
			this.customerAddressDetail = new CustomerAddressDetailEntity(this,
					address);
			this.displayAddress = this.customerAddressDetail
					.getDisplayAddress();
		}
	}
	
	private CustomerAccountBO createCustomerAccount(List<FeeView> fees)
	throws CustomerException {
		try {
			return new CustomerAccountBO(userContext, this, fees);
		} catch (AccountException ae) {
			throw new CustomerException(ae);
		}
	}
	
	private void createCustomFields(List<CustomFieldView> customFields){
		if (customFields != null)
			for (CustomFieldView customField : customFields)
				addCustomField(new CustomerCustomFieldEntity(customField
						.getFieldId(), customField.getFieldValue(), this));
	}
	
	private void inheritDetailsFromParent(CustomerBO parentCustomer){
		this.personnel = parentCustomer.getPersonnel();
		this.office = parentCustomer.getOffice();
		if(parentCustomer.getCustomerMeeting()!=null)
			this.customerMeeting = createCustomerMeeting(parentCustomer
					.getCustomerMeeting().getMeeting());
		this.addCustomerHierarchy(new CustomerHierarchyEntity(this, parentCustomer));
	}	
	
	private CustomerNoteEntity createCustomerNotes(String comment){
		CustomerNoteEntity customerNote = new CustomerNoteEntity(comment,
				new java.sql.Date(System.currentTimeMillis()), new PersonnelPersistence().getPersonnel(getUserContext().getId()), this);
		return customerNote;
	}
	
	private void addCustomerAccount(CustomerAccountBO customerAccount) {
		this.accounts.add(customerAccount);
	}
	
	private void validateFields(String displayName,
			CustomerStatus customerStatus, Short officeId, CustomerBO parentCustomer)
			throws CustomerException {
		if (StringUtils.isNullOrEmpty(displayName))
			throw new CustomerException(CustomerConstants.INVALID_NAME);
		if (customerStatus == null)
			throw new CustomerException(CustomerConstants.INVALID_STATUS);		
	}
	
	private CustomerBO getCustomer(Integer customerId){
		return customerId!=null ? new CustomerPersistence().getCustomer(customerId) : null;
	}
}
