/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers.business;

import static org.apache.commons.lang.math.NumberUtils.SHORT_ZERO;
import static org.mifos.framework.util.helpers.MoneyUtils.zero;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.CalendarUtils;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.ChapterNum;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 * A class that represents a customer entity after being created.
 */
public abstract class CustomerBO extends AbstractBusinessObject {
    private Integer customerId;

    private String globalCustNum;
    private String externalId;
    private String searchId;

    //business meta data
    private final Set<ClientNameDetailEntity> nameDetailSet;
    private String displayName;
    private String displayAddress;
    private Short trained = 0;
    private Date trainedDate;
    private Date mfiJoiningDate;
    private Set<CustomerCustomFieldEntity> customFields;
    private Set<CustomerFlagDetailEntity> customerFlags;
    private Integer maxChildCount = 0;
    private Date customerActivationDate;
    private CustomerStatusEntity customerStatus;
    private Set<CustomerPositionEntity> customerPositions;
    private CustomerAddressDetailEntity customerAddressDetail;
    private Set<CustomerNoteEntity> customerNotes;

    //business attributes
    private CustomerBO parentCustomer;
    private Set<AccountBO> accounts;
    private final CustomerLevelEntity customerLevel;
    private CustomerMeetingEntity customerMeeting;
    private Set<CustomerHierarchyEntity> customerHierarchies;
    private Set<CustomerMovementEntity> customerMovements;
    private CustomerHistoricalDataEntity historicalData;
    public Short blackListed = YesNoFlag.NO.getValue();
    private Set<CustomerBO> children;
    private CustomerAccountBO customerAccount;

    private CustomerPersistence customerPersistence = null;
    private LegacyPersonnelDao personnelPersistence = null;
    private CustomerDao customerDao = null;    

    //associations
    private PersonnelBO personnel;
    private final PersonnelBO formedByPersonnel;
    private OfficeBO office;

    /**
     * default constructor for hibernate
     */
    protected CustomerBO() {
        this(null, null, null, null, null);
    }

    /**
     * @deprecated - use minimal legal constructor or static factory methods of subclasses in builder
     */
    @Deprecated
    protected CustomerBO(final Integer customerId, final CustomerLevelEntity customerLevel,
            final PersonnelBO formedByPersonnel, final PersonnelBO personnel, final String displayName) {
        super();
        this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
        this.customerId = customerId;
        this.customerLevel = customerLevel;
        this.formedByPersonnel = formedByPersonnel;
        this.personnel = personnel;
        this.displayName = displayName;
    }

    /**
     * minimal legal constructor for center, groups and clients.
     */
    public CustomerBO(UserContext userContext, String customerName, CustomerLevel customerLevel,
            CustomerStatus customerStatus, DateTime mfiJoiningDate, OfficeBO office, MeetingBO meeting,
            PersonnelBO loanOfficer, PersonnelBO formedBy) {
        super(userContext);
        this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
        this.customerId = null;
        this.customerHierarchies = new HashSet<CustomerHierarchyEntity>();
        this.customerMovements = new HashSet<CustomerMovementEntity>();
        this.customerPositions = new HashSet<CustomerPositionEntity>();
        this.customFields = new HashSet<CustomerCustomFieldEntity>();
        this.accounts = new HashSet<AccountBO>();
        this.children = new HashSet<CustomerBO>();
        this.customerFlags = new HashSet<CustomerFlagDetailEntity>();
        this.customerNotes = new HashSet<CustomerNoteEntity>();
        if (mfiJoiningDate != null) {
            this.mfiJoiningDate = mfiJoiningDate.toDate();
        }
        this.displayName = customerName;
        this.office = office;
        this.personnel = loanOfficer;
        this.formedByPersonnel = formedBy;
        this.customerLevel = new CustomerLevelEntity(customerLevel);
        if (meeting != null) {
            this.customerMeeting = new CustomerMeetingEntity(this, meeting);
        }
        this.customerStatus = new CustomerStatusEntity(customerStatus);
        this.setCreateDetails();
    }

    /**
     * @deprecated use constructor above
     */
    @Deprecated
    protected CustomerBO(final UserContext userContext, final String displayName, final CustomerLevel customerLevel,
            final CustomerStatus customerStatus, final String externalId, final Date mfiJoiningDate,
            final Address address, final List<CustomFieldDto> customFields, final List<FeeDto> fees,
            final PersonnelBO formedBy, final OfficeBO office, final CustomerBO parentCustomer,
            final MeetingBO meeting, final PersonnelBO loanOfficer) throws CustomerException {

        super(userContext);

        this.nameDetailSet = new HashSet<ClientNameDetailEntity>();
        customerHierarchies = new HashSet<CustomerHierarchyEntity>();
        customerMovements = new HashSet<CustomerMovementEntity>();
        customerPositions = new HashSet<CustomerPositionEntity>();
        validateFields(displayName, customerStatus);
        this.customFields = new HashSet<CustomerCustomFieldEntity>();
        this.accounts = new HashSet<AccountBO>();
        this.customerNotes = new HashSet<CustomerNoteEntity>();
        this.customerPositions = new HashSet<CustomerPositionEntity>();
        this.externalId = externalId;
        this.mfiJoiningDate = mfiJoiningDate;
        this.displayName = displayName;
        this.customerLevel = new CustomerLevelEntity(customerLevel);

        createAddress(address);

        if (parentCustomer != null) {
            inheritDetailsFromParent(parentCustomer);
        } else {
            personnel = loanOfficer;
            customerMeeting = createCustomerMeeting(meeting);
            this.office = office;
        }

        formedByPersonnel = formedBy;

        setParentCustomer(parentCustomer);

        createCustomFields(customFields);

        this.customerStatus = new CustomerStatusEntity(customerStatus);
        this.maxChildCount = 0;
        this.blackListed = YesNoFlag.NO.getValue();
        this.customerId = null;
        this.historicalData = null;
        this.customerFlags = new HashSet<CustomerFlagDetailEntity>();

        this.customerAccount = createCustomerAccount(fees);
        this.addAccount(createCustomerAccount(fees));

        this.setCreateDetails();

    }

    private CustomerDao getCustomerDao() {
        if (customerDao == null) {
            customerDao = ApplicationContextProvider.getBean(CustomerDao.class);
        }
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Set<ClientNameDetailEntity> getNameDetailSet() {
        return nameDetailSet;
    }

    /**
     * Most callers will want to call {@link #getLevel()} instead.
     */
    public CustomerLevelEntity getCustomerLevel() {
        return this.customerLevel;
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

    public void setPersonnel(final PersonnelBO personnel) {
        this.personnel = personnel;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Most callers will instead want an enum - call {@link #getStatus()} for that.
     */
    public CustomerStatusEntity getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(final CustomerStatusEntity customerStatus) {
        this.customerStatus = customerStatus;
    }

    public void updateCustomerStatus(CustomerStatus newCustomerStatus) {
        this.customerStatus = new CustomerStatusEntity(newCustomerStatus);
    }

    public void updateCustomerStatus(CustomerStatus newStatus, CustomerNoteEntity customerNote,
            CustomerStatusFlagEntity customerStatusFlagEntity) {
        this.customerStatus = new CustomerStatusEntity(newStatus);
        addCustomerNotes(customerNote);
        if (customerStatusFlagEntity != null) {
            addCustomerFlag(customerStatusFlagEntity);
        }
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(final String displayAddress) {
        this.displayAddress = displayAddress;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    protected void setTrained(final Short trained) {
        this.trained = trained;
    }

    public Date getTrainedDate() {
        return this.trainedDate;
    }

    public void setTrainedDate(final Date trainedDate) {
        this.trainedDate = trainedDate;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public void setSearchId(final String searchId) {
        this.searchId = searchId;
    }

    public Integer getMaxChildCount() {
        return this.maxChildCount;
    }

    public Date getMfiJoiningDate() {
        return mfiJoiningDate;
    }

    public void setMfiJoiningDate(final Date mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public Date getCustomerActivationDate() {
        return customerActivationDate;
    }

    public void setCustomerActivationDate(final Date customerActivationDate) {
        this.customerActivationDate = customerActivationDate;
    }

    public void setParentCustomer(final CustomerBO parentCustomer) {
        this.parentCustomer = parentCustomer;
        if (parentCustomer != null) {
            parentCustomer.incrementChildCount();
        }
    }

    public CustomerBO getParentCustomer() {
        return parentCustomer;
    }

    /*
     * This only appears to be used in tests
     */
    public final void addChild(final CustomerBO existingClient) {
        this.children.add(existingClient);
    }

    public Set<CustomerBO> getChildren() {
        if (children == null) {
            return new HashSet<CustomerBO>();
        }

        return children;
    }

    public CustomerAddressDetailEntity getCustomerAddressDetail() {
        return customerAddressDetail;
    }

    public void setCustomerAddressDetail(final CustomerAddressDetailEntity customerAddressDetail) {
        this.customerAddressDetail = customerAddressDetail;
    }

    public OfficeBO getOffice() {
        return office;
    }

    protected void setOffice(final OfficeBO office) {
        this.office = office;
    }

    public CustomerMeetingEntity getCustomerMeeting() {
        return customerMeeting;
    }

    public MeetingBO getCustomerMeetingValue() {
        if (customerMeeting == null) {
            return null;
        }
        return customerMeeting.getMeeting();
    }

    public void setMeeting(final MeetingBO meeting) {
        this.customerMeeting = new CustomerMeetingEntity(this, meeting);
    }

    public void setCustomerMeeting(final CustomerMeetingEntity customerMeeting) {
        this.customerMeeting = customerMeeting;
    }

    public Set<AccountBO> getAccounts() {
        if (accounts == null) {
            return new HashSet<AccountBO>();
        }
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

    public void setTrained(final boolean trained) {
        this.trained = (short) (trained ? 1 : 0);
    }

    public void addCustomerHierarchy(final CustomerHierarchyEntity hierarchy) {
        if (hierarchy != null) {
            this.customerHierarchies.add(hierarchy);
        }
    }

    public void addCustomerPosition(final CustomerPositionEntity customerPosition) {
        this.customerPositions.add(customerPosition);
    }

    protected void addCustomerMovement(final CustomerMovementEntity customerMovement) {
        if (customerMovement != null) {
            this.customerMovements.add(customerMovement);
        }
    }

    public void addCustomField(final CustomerCustomFieldEntity customField) {
        if (customField != null) {
            this.customFields.add(customField);
        }
    }

    public void addCustomerNotes(final CustomerNoteEntity customerNote) {
        this.customerNotes.add(customerNote);
    }

    public void addCustomerFlag(final CustomerStatusFlagEntity customerStatusFlagEntity) {
        CustomerFlagDetailEntity customerFlag = new CustomerFlagDetailEntity(this, customerStatusFlagEntity, this
                .getUserContext().getId(), new DateTimeService().getCurrentJavaDateTime());
        this.customerFlags.add(customerFlag);
        if (customerStatusFlagEntity.isBlackListed()) {
            this.blacklist();
        }
    }

    public boolean isTrained() {
        return trained.equals(YesNoFlag.YES.getValue());
    }

    public boolean isBlackListed() {
        return blackListed != null && blackListed.equals(YesNoFlag.YES.getValue());
    }

    public Address getAddress() {
        return customerAddressDetail != null ? customerAddressDetail.getAddress() : null;
    }

    public CustomerStatus getStatus() {
        return CustomerStatus.fromInt(customerStatus.getId());
    }

    public void generateGlobalCustomerNumber() throws CustomerException {
        globalCustNum = generateSystemId();

        CustomerAccountBO customerAccount = getCustomerAccount();
        if (customerAccount != null) {
            customerAccount.generateCustomerAccountSystemId();
        }
    }

    /**
     * @deprecated - use {@link CustomerDao}.
     */
    @Deprecated
    public void update() throws CustomerException {
        try {
            setUpdateDetails();
            getCustomerPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
        }
    }

    public void updateAddress(final Address address) {

        if (address == null) {
            if (this.customerAddressDetail != null) {
                this.customerAddressDetail.setAddress(null);
                this.customerAddressDetail = null;
            }
        } else if (getCustomerAddressDetail() == null) {
            setCustomerAddressDetail(new CustomerAddressDetailEntity(this, address));
        } else {
            getCustomerAddressDetail().setAddress(address);
        }
    }

    public CustomerAccountBO getCustomerAccount() {
        CustomerAccountBO customerAccount = null;
        for (AccountBO account : accounts) {
            if (account.getType() == AccountTypes.CUSTOMER_ACCOUNT) {
                customerAccount = (CustomerAccountBO) account;
            }
        }
        return customerAccount;
    }

    public List<CustomerNoteEntity> getRecentCustomerNotes() {
        List<CustomerNoteEntity> notes = new ArrayList<CustomerNoteEntity>();
        int count = 0;
        for (CustomerNoteEntity customerNote : getCustomerNotes()) {
            if (count > 2) {
                break;
            }
            notes.add(customerNote);
            count++;
        }
        return notes;
    }

    public CustomerHierarchyEntity getActiveCustomerHierarchy() {
        CustomerHierarchyEntity hierarchy = null;
        for (CustomerHierarchyEntity customerHierarchyEntity : customerHierarchies) {
            if (customerHierarchyEntity.isActive()) {
                hierarchy = customerHierarchyEntity;
                break;
            }
        }
        return hierarchy;
    }

    public CustomerMovementEntity getActiveCustomerMovement() {
        CustomerMovementEntity movement = null;
        for (CustomerMovementEntity customerMovementEntity : customerMovements) {
            if (customerMovementEntity.isActive()) {
                movement = customerMovementEntity;
                break;
            }
        }
        return movement;
    }

    public void updateHistoricalData(final CustomerHistoricalDataEntity historicalData) {
        if (historicalData != null) {
            mfiJoiningDate = historicalData.getMfiJoiningDate();
        }
        this.historicalData = historicalData;
    }

    public CustomerHistoricalDataEntity getHistoricalData() {
        if (historicalData != null) {
            historicalData.setMfiJoiningDate(mfiJoiningDate);
        }
        return historicalData;
    }

    /**
     * @deprecated - use {@link CustomerDao}
     */
    @Deprecated
    public List<CustomerBO> getChildren(final CustomerLevel customerLevel, final ChildrenStateType stateType)
            throws CustomerException {
        try {
            return getCustomerPersistence().getChildren(getSearchId(), getOffice().getOfficeId(), customerLevel,
                    stateType);
        } catch (PersistenceException pe) {
            throw new CustomerException(pe);
        }
    }

    public void adjustPmnt(final String adjustmentComment, final PersonnelBO loggedInUser) throws ApplicationException, SystemException {
        getCustomerAccount().adjustPmnt(adjustmentComment, loggedInUser);
    }

    public abstract boolean isActive();

    /**
     * Is this customer active (but based on level rather than discriminator columm)? (Is there any way this will ever
     * be different from {@link #isActive()}? I suspect not, but I'm not sure).
     */
    public boolean isActiveViaLevel() {
        return getCustomerLevel().isGroup() && getStatus() == CustomerStatus.GROUP_ACTIVE
                || getCustomerLevel().isClient() && getStatus() == CustomerStatus.CLIENT_ACTIVE
                || getCustomerLevel().isCenter() && getStatus() == CustomerStatus.CENTER_ACTIVE;
    }

    public Money getBalanceForAccountsAtRiskForTask(MifosCurrency currency) {
        Money amount = new Money(currency);
        for (AccountBO account : getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) account).isAccountActive()) {
                LoanBO loan = (LoanBO) account;
                if (loan.getAccountState().getId().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue())) {
                    amount = amount.add(loan.getRemainingPrincipalAmount());
                }
            }
        }
        return amount;
    }

    public Money getOutstandingLoanAmount(MifosCurrency currency) {
        Money amount = new Money(currency);
        Set<AccountBO> accounts = getAccounts();
        if (accounts != null) {
            for (AccountBO account : getAccounts()) {
                if (account.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) account).isAccountActive()) {
                    Money remainingPrincipalAmount = ((LoanBO) account).getRemainingPrincipalAmount();
                    if (amount.getAmount().equals(BigDecimal.ZERO)) {
                        amount = remainingPrincipalAmount;
                    } else {
                        amount = amount.add(remainingPrincipalAmount);
                    }
                }
            }
        }
        return amount;
    }

    public Integer getActiveLoanCounts() {
        Integer countOfActiveLoans = 0;
        for (AccountBO account : getAccounts()) {
            if (account.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) account).isAccountActive()) {
                countOfActiveLoans++;
            }
        }
        return countOfActiveLoans;
    }

    /*
     * This method is unused and is a candidate for removal.
     */
    @Deprecated
    public BigDecimal getDelinquentPortfolioAmount(MifosCurrency currency) {
        Money amountOverDue = new Money(currency);
        Money totalOutStandingAmount = new Money(currency);
        for (AccountBO accountBO : getAccounts()) {
            if (accountBO.getType() == AccountTypes.LOAN_ACCOUNT && ((LoanBO) accountBO).isAccountActive()) {
                amountOverDue = amountOverDue.add(((LoanBO) accountBO).getTotalPrincipalAmountInArrears());
                totalOutStandingAmount = totalOutStandingAmount.add(((LoanBO) accountBO).getLoanSummary()
                        .getOriginalPrincipal());
            }
        }
        if (totalOutStandingAmount.isNonZero()) {
            return amountOverDue.divide(totalOutStandingAmount);
        }
        return BigDecimal.ZERO;
    }

    public CustomerPerformanceHistory getPerformanceHistory() {
        if (this instanceof ClientBO) {
            return ((ClientBO) this).getClientPerformanceHistory();
        } else if (this instanceof GroupBO) {
            return ((GroupBO) this).getGroupPerformanceHistory();
        } else {
            return null;
        }
    }

    public Money getSavingsBalance(MifosCurrency currency) {
        Money amount = new Money(currency);
        for (AccountBO account : getAccounts()) {
            if (account.getType() == AccountTypes.SAVINGS_ACCOUNT) {
                SavingsBO savingsBO = (SavingsBO) account;
                amount = amount.add(savingsBO.getSavingsBalance());
            }
        }
        return amount;
    }
    
    public Money getLoanBalance(MifosCurrency currency){
        Money amount = new Money(currency);
        for (AccountBO account : getAccounts()) {
            if (account.isActiveLoanAccount()) {
                LoanBO loanBO = (LoanBO) account;
                amount = amount.add(loanBO.getLoanSummary().getTotalAmntDue());
            }
        }
        return amount;
    }

    public List<LoanBO> getOpenLoanAccounts() {
        List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
        for (AccountBO account : getAccounts()) {
            if (account.isLoanAccount() && account.isOpen()) {
                loanAccounts.add((LoanBO) account);
            }
        }
        return loanAccounts;
    }
    
    //method returns also new kind of group loan account
    //for group details
    public List<LoanBO> getOpenLoanAccountsAndGroupLoans() {
        List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
        for (AccountBO account : getAccounts()) {
            if ((account.isLoanAccount() || account.isGroupLoanAccount()) && account.isOpen()) {
                loanAccounts.add((LoanBO) account);
            }
        }
        return loanAccounts;
    }

    public List<LoanBO> getOpenIndividualLoanAccounts() {
        List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
        for (AccountBO account : getAccounts()) {
            if (account.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT) && account.isOpen()) {
                loanAccounts.add((LoanBO) account);
            }
        }
        return loanAccounts;
    }
    
    public List<LoanBO> getOpenGroupLoanAccounts() {
        List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
        for (AccountBO account : getAccounts()) {
            if (account.isOfType(AccountTypes.GROUP_LOAN_ACCOUNT) && account.isOpen()) {
                loanAccounts.add((LoanBO) account);
            }
        }
        return loanAccounts;
    }

    public List<SavingsBO> getOpenSavingAccounts() {
        List<SavingsBO> savingAccounts = new ArrayList<SavingsBO>();
        for (AccountBO account : getAccounts()) {
            if (account.isSavingsAccount() && account.isOpen()) {
                savingAccounts.add((SavingsBO) account);
            }
        }
        return savingAccounts;
    }

    public boolean isAnyLoanAccountOpen() {
        for (AccountBO account : getAccounts()) {
            if ((account.isLoanAccount() || account.isGroupLoanAccount()) && account.isOpen()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnySavingsAccountOpen() {
        for (AccountBO account : getAccounts()) {
            if (account.isSavingsAccount() && account.isOpen()) {
                return true;
            }
        }
        return false;
    }

    void resetPositionsAssignedToClient(final Integer clientId) {
        if (getCustomerPositions() != null) {
            for (CustomerPositionEntity position : getCustomerPositions()) {
                if (position.getCustomer() != null && position.getCustomer().getCustomerId().equals(clientId)) {
                    position.setCustomer(null);
                }
            }
        }
    }

    public void incrementChildCount() {
        this.maxChildCount = this.getMaxChildCount().intValue() + 1;
    }

    public CustomerLevel getLevel() {
        return CustomerLevel.getLevel(getCustomerLevel().getId());
    }

    protected void validateMeetingEntity(final CustomerMeetingEntity meeting) throws CustomerException {
        if (meeting == null) {
            throw new CustomerException(CustomerConstants.INVALID_MEETING);
        }
        validateMeeting(meeting.getMeeting());
    }

    protected void validateMeeting(final MeetingBO meeting) throws CustomerException {
        if (meeting == null) {
            throw new CustomerException(CustomerConstants.INVALID_MEETING);
        }
    }

    public final void validateOffice() throws CustomerException {
        if (office == null) {
            throw new CustomerException(CustomerConstants.INVALID_OFFICE);
        }
    }

    @Deprecated
    protected void validateOffice(final OfficeBO office) throws CustomerException {
        if (office == null) {
            throw new CustomerException(CustomerConstants.INVALID_OFFICE);
        }
    }

    public final void validateLoanOfficer() throws CustomerException {
        if (this.personnel == null) {
            throw new CustomerException(CustomerConstants.ERRORS_SELECT_LOAN_OFFICER);
        }
    }

    @Deprecated
    public final void validateLO(final PersonnelBO loanOfficer) throws CustomerException {
        if (loanOfficer == null || loanOfficer.getPersonnelId() == null) {
            throw new CustomerException(CustomerConstants.INVALID_LOAN_OFFICER);
        }
    }

    @Deprecated
    public final void validateLO(final Short loanOfficerId) throws CustomerException {
        if (loanOfficerId == null) {
            throw new CustomerException(CustomerConstants.INVALID_LOAN_OFFICER);
        }
    }

    public CustomerMeetingEntity createCustomerMeeting(final MeetingBO meeting) {
        return meeting != null ? new CustomerMeetingEntity(this, meeting) : null;
    }

    public abstract boolean isActiveForFirstTime(Short oldStatus, Short newStatusId);

    public boolean checkStatusChangeCancelToPartial(final CustomerStatus oldStatus, final CustomerStatus newStatus) {
        if ((oldStatus.equals(CustomerStatus.GROUP_CANCELLED) || oldStatus.equals(CustomerStatus.CLIENT_CANCELLED))
                && (newStatus.equals(CustomerStatus.GROUP_PARTIAL) || newStatus.equals(CustomerStatus.CLIENT_PARTIAL))) {
            return true;
        }
        return false;
    }

    public final boolean isSameBranch(final OfficeBO officeObj) {
        return this.office.getGlobalOfficeNum().equals(officeObj.getGlobalOfficeNum());
    }

    public boolean isDifferentBranch(final OfficeBO otherOffice) {
        return !isSameBranch(otherOffice);
    }

    public void checkIfClientIsATitleHolder() throws CustomerException {
        if (getParentCustomer() != null) {
            for (CustomerPositionEntity position : getParentCustomer().getCustomerPositions()) {
                if (position.getCustomer() != null
                        && position.getCustomer().getCustomerId().intValue() == this.getCustomerId().intValue()) {
                    // position.getPosition().getId().shortValue()==new
                    // Short("1").shortValue())
                    throw new CustomerException(CustomerConstants.CLIENT_IS_A_TITLE_HOLDER_EXCEPTION);
                }
            }

        }
    }

    public void setLoanOfficer(PersonnelBO loanOfficer) {
        this.personnel = loanOfficer;
    }

    public boolean isLoanOfficerChanged(PersonnelBO oldLoanOfficer) {

        if (oldLoanOfficer == null && this.personnel == null) {
            return false;
        }

        if (oldLoanOfficer != null && this.personnel == null) {
            return true;
        }

        if (oldLoanOfficer == null && this.personnel != null) {
            return true;
        }

        return oldLoanOfficer.isDifferentIdentityTo(this.personnel);
    }

    @Deprecated
    public boolean isLOChanged(final Short loanOfficerId) {
        return getPersonnel() == null && loanOfficerId != null || getPersonnel() != null && loanOfficerId == null
                || getPersonnel() != null && loanOfficerId != null
                && !getPersonnel().getPersonnelId().equals(loanOfficerId);

    }

    public final int countOfCustomerMovements() {
        return this.customerMovements.size();
    }

    public void makeCustomerMovementEntries(final OfficeBO officeToTransfer) {
        CustomerMovementEntity currentCustomerMovement = getActiveCustomerMovement();
        if (currentCustomerMovement == null) {
            currentCustomerMovement = new CustomerMovementEntity(this, getCreatedDate());
            this.addCustomerMovement(currentCustomerMovement);
        }

        currentCustomerMovement.makeInactive(userContext.getId());
        this.setOffice(officeToTransfer);
        CustomerMovementEntity newCustomerMovement = new CustomerMovementEntity(this, new DateTimeService().getCurrentJavaDateTime());
        this.addCustomerMovement(newCustomerMovement);
    }

    protected String generateSystemId() {
        String systemId = "";
        int numberOfZeros = CustomerConstants.SYSTEM_ID_LENGTH - String.valueOf(getCustomerId()).length();

        for (int i = 0; i < numberOfZeros; i++) {
            systemId = systemId + "0";
        }

        return getOffice().getGlobalOfficeNum() + "-" + systemId + getCustomerId();
    }

    /**
     * @deprecated - move methods that call persistence out of domain model.
     */
    @Deprecated
    protected void deleteCustomerMeeting() {
        try {
            getCustomerPersistence().deleteCustomerMeeting(this);
            setCustomerMeeting(null);
        } catch (PersistenceException pe) {
            new CustomerException(pe);
        }
    }

    private void createAddress(final Address address) {
        if (address != null) {
            this.customerAddressDetail = new CustomerAddressDetailEntity(this, address);
            this.displayAddress = this.customerAddressDetail.getDisplayAddress();
        }
    }

    /**
     * @deprecated - using deprecated {@link CustomerAccountBO} constructor
     */
    @Deprecated
    private CustomerAccountBO createCustomerAccount(final List<FeeDto> fees) throws CustomerException {
        try {
            return new CustomerAccountBO(userContext, this, fees);
        } catch (AccountException ae) {
            throw new CustomerException(ae);
        }
    }

    private void createCustomFields(final List<CustomFieldDto> customFields) {
        if (customFields != null) {
            for (CustomFieldDto customField : customFields) {
                addCustomField(new CustomerCustomFieldEntity(customField.getFieldId(), customField.getFieldValue(), customField.getFieldType(), this));
            }
        }
    }

    private void inheritDetailsFromParent(final CustomerBO parentCustomer) {
        personnel = parentCustomer.getPersonnel();
        office = parentCustomer.getOffice();
        if (parentCustomer.getCustomerMeeting() != null) {
            customerMeeting = createCustomerMeeting(parentCustomer.getCustomerMeeting().getMeeting());
        }
        this.addCustomerHierarchy(new CustomerHierarchyEntity(this, parentCustomer));
    }

    public void addAccount(final AccountBO account) {
        this.accounts.add(account);
    }

    public void validate() throws CustomerException {
        validateName();
        validateOffice();
        if (isActive()) {
            validateLoanOfficer();
        }
    }

    private void validateName() throws CustomerException {
        if (StringUtils.isBlank(displayName)) {
            throw new CustomerException(CustomerConstants.ERRORS_SPECIFY_NAME);
        }
    }

    public final void validateTrained() throws CustomerException {
        if (this.isTrained() && this.trainedDate == null || !this.isTrained() && this.trainedDate != null) {
            throw new CustomerException(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE);
        }
    }

    public final void validateFormedBy() throws CustomerException {
        if (this.formedByPersonnel == null) {
            throw new CustomerException(CustomerConstants.INVALID_FORMED_BY);
        }
    }

    @Deprecated
    public void validateFields(final String displayName, final CustomerStatus customerStatus) throws CustomerException {
        if (StringUtils.isBlank(displayName)) {
            throw new CustomerException(CustomerConstants.INVALID_NAME);
        }
        if (customerStatus == null) {
            throw new CustomerException(CustomerConstants.INVALID_STATUS);
        }
    }

    public void resetPositions(final CustomerBO newParent) {
        newParent.resetPositionsAssignedToClient(this.getCustomerId());
    }

    protected void validateMeetingRecurrenceForTransfer(final MeetingBO meetingFrom, final MeetingBO meetingTo)
            throws CustomerException {
        if (meetingFrom.isWeekly() && meetingTo.isMonthly() || meetingFrom.isMonthly() && meetingTo.isWeekly()) {
            throw new CustomerException(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH);
        }
    }

    public boolean hasActiveLoanAccounts() {
        for (AccountBO account : getAccounts()) {
            if (account.isActiveLoanAccount()) {
                return true;
            }
        }
        return false;
    }

    public boolean isDisbursalPreventedDueToAnyExistingActiveLoansForTheSameProduct(final LoanOfferingBO loanOffering) {
        if (definedAsSameForAllLoan(loanOffering)) {
            return false;
        }

        for (AccountBO account : getAccounts()) {
            if (account.isActiveLoanAccount()) {
                LoanOfferingBO compareLoanOffering = ((LoanBO) account).getLoanOffering();
                if (compareLoanOffering.isOfSameOffering(loanOffering)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean definedAsSameForAllLoan(LoanOfferingBO loanOffering) {
        if (loanOffering.getLoanAmountSameForAllLoan() == null || loanOffering.getLoanAmountSameForAllLoan().size() == 0) {
            return false;
        }
        if (loanOffering.getNoOfInstallSameForAllLoan() == null || loanOffering.getNoOfInstallSameForAllLoan().size() == 0) {
            return false;
        }
        return true;
    }

    public void generateSearchId() {
        if (getParentCustomer() != null) {
            this.setSearchId(getParentCustomer().getSearchId() + "." + getParentCustomer().getMaxChildCount());
        } else {
            String searchId = GroupConstants.PREFIX_SEARCH_STRING + getCustomerId();
            this.setSearchId(searchId);
        }
    }

    protected void handleAddClientToGroup() {
        setPersonnel(getParentCustomer().getPersonnel());
        if (getCustomerMeeting() != null) {
            deleteCustomerMeeting();
            setCustomerMeeting(createCustomerMeeting(getParentCustomer().getCustomerMeeting().getMeeting()));

        } else {
            setCustomerMeeting(createCustomerMeeting(getParentCustomer().getCustomerMeeting().getMeeting()));
        }
    }

    @Override
    public String toString() {
        return "{" + customerId + ", " + displayName + "}";
    }

    public boolean isCenter() {
        return getCustomerLevel().isCenter();
    }

    public boolean isGroup() {
        return getCustomerLevel().isGroup();
    }

    public boolean isClient() {
        return getCustomerLevel().isClient();
    }

    /**
     * Returns the amount which this customer
     *
     **/
    public Money getMaxLoanAmount(final LoanOfferingBO loanOffering) {
        ArrayList<Money> loanAmounts = new ArrayList<Money>();
        Set<AccountBO> accounts = getAccounts();
        for (AccountBO accountBO : accounts) {
            // If account not in loan obligations met, continue to next loan
            // account
            if (!accountBO.isInState(AccountState.LOAN_CLOSED_OBLIGATIONS_MET)) {
                continue;
            }

            if (accountBO.isLoanAccount() && ((LoanBO) accountBO).isOfProductOffering(loanOffering)) {
                loanAmounts.add(((LoanBO) accountBO).getLoanAmount());
            }
        }
        if (loanAmounts.isEmpty()) {
            loanAmounts.add(zero());
        }
        return Collections.max(loanAmounts);
    }

    public Short getMaxLoanCycleForProduct(final PrdOfferingBO prdOffering) {
        // implement strategy and delegate logic to PerformanceHistory instead
        // of being here
        // only checking for clients
        if (getPerformanceHistory() instanceof ClientPerformanceHistoryEntity) {
            return ((ClientPerformanceHistoryEntity) getPerformanceHistory()).getMaxLoanCycleForProduct(prdOffering);
        } else if (getPerformanceHistory() instanceof GroupPerformanceHistoryEntity) {
            return ((GroupPerformanceHistoryEntity) getPerformanceHistory()).getMaxLoanCycleForProduct(prdOffering);
        }
        return SHORT_ZERO;
    }

    /**
     * <code>searchId</code> should indicate the order in which clients became part of a group. This method was
     * originally created for use in fixing <a href="https://mifos.dev.java.net/issues/show_bug.cgi?id=1417">bug
     * #1417</a>.
     *
     * @return A {@link java.util.Comparator} useful for comparing customers by searchId.
     */
    public static Comparator<CustomerBO> searchIdComparator() {
        return new Comparator<CustomerBO>() {
            @Override
			public int compare(final CustomerBO o1, final CustomerBO o2) {
                return ChapterNum.compare(o1.getSearchId(), o2.getSearchId());
            }
        };
    }

    public void updatePerformanceHistoryOnDisbursement(final LoanBO loan, final Money disburseAmount)
            throws CustomerException {
    }

    public void updatePerformanceHistoryOnWriteOff(final LoanBO loan) throws CustomerException {
    }

    public void updatePerformanceHistoryOnReversal(final LoanBO loan, final Money lastLoanAmount)
            throws CustomerException {
    }

    public void updatePerformanceHistoryOnRepayment(final LoanBO loan, final Money totalAmount)
            throws CustomerException {
    }

    public void updatePerformanceHistoryOnLastInstlPayment(final LoanBO loan, final Money totalAmount)
            throws CustomerException {
    }

    public void addCustomerAccount(final CustomerAccountBO customerAccount) {
        this.accounts.add(customerAccount);
    }

    public void setCustomerAccount(CustomerAccountBO customerAccount) {
        this.customerAccount = customerAccount;
        this.accounts.add(customerAccount);
    }

    public CustomerPersistence getCustomerPersistence() {
        if (null == customerPersistence) {
            customerPersistence = new CustomerPersistence();
        }
        return this.customerPersistence;
    }

    public void setCustomerPersistence(final CustomerPersistence customerPersistence) {
        this.customerPersistence = customerPersistence;
    }

    public LegacyPersonnelDao getPersonnelPersistence() {
        if (null == personnelPersistence) {
            personnelPersistence = ApplicationContextProvider.getBean(LegacyPersonnelDao.class);
        }
        return personnelPersistence;
    }

    public void setPersonnelPersistence(final LegacyPersonnelDao personnelPersistence) {
        this.personnelPersistence = personnelPersistence;
    }

    public void validateLoanOfficerIsActive() throws CustomerException {
        PersonnelBO loanOfficer = this.personnel;
        if (loanOfficer != null) {
            if (!loanOfficer.isActive()
                    || !(loanOfficer.getOffice().getOfficeId().equals(this.office.getOfficeId()) || !loanOfficer
                            .isLoanOfficer())) {
                throw new CustomerException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
            }
        }
    }

    public void validateChangeToActive() throws CustomerException {
        if (this.personnel == null || this.personnel.getPersonnelId() == null) {
            throw new CustomerException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
        }
    }

    public void validateNoActiveAccountExist() throws CustomerException {
        if (this.isAnyLoanAccountOpen() || this.isAnySavingsAccountOpen()) {
            throw new CustomerException(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
        }
    }

    public void clearCustomerFlagsIfApplicable(CustomerStatus oldStatus, CustomerStatus newStatus) {
        if (checkStatusChangeCancelToPartial(oldStatus, newStatus)) {
            this.customerFlags.clear();
        }
    }

    public void blacklist() {
        this.blackListed = YesNoFlag.YES.getValue();
    }

    public CustomerDetailDto toCustomerDetailDto() {

        Short loanOfficerId = null;
        if (this.personnel != null) {
            loanOfficerId = this.personnel.getPersonnelId();
        }

        Address address = new Address();
        if (this.customerAddressDetail != null) {
            address = this.customerAddressDetail.getAddress();
        }

        AddressDto addressDto = null;
        if (address != null) {
            addressDto = Address.toDto(address);
        }

        return new CustomerDetailDto(this.customerId, this.displayName, this.searchId, this.globalCustNum,
                loanOfficerId, this.externalId, addressDto);
    }

    public boolean hasSameIdentityAs(CustomerBO customer) {
        if ((this.customerId == null && customer.getCustomerId() == null)
                && (this.globalCustNum == null && customer.getGlobalCustNum() == null)) {
            return this.displayName.equals(customer.displayName);
        }
        return this.globalCustNum.equals(customer.getGlobalCustNum());
    }

    public void validateVersion(Integer newVersionNum) throws CustomerException {
        if (!this.versionNo.equals(newVersionNum)) {
            throw new CustomerException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    public boolean isNameDifferent(final String nameToCheck) {
        return !this.displayName.equalsIgnoreCase(nameToCheck);
    }

    public void updateCenterDetails(UserContext userContext, CenterUpdate centerUpdate) throws CustomerException {
        this.setUserContext(userContext);
        this.setUpdateDetails();

        this.setExternalId(centerUpdate.getExternalId());
        AddressDto dto = centerUpdate.getAddress();
        if ( dto != null) {
            Address address = new Address(dto.getLine1(), dto.getLine2(), dto.getLine3(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getZip(), dto.getPhoneNumber());
            this.updateAddress(address);
        }

        try {
            if (centerUpdate.getMfiJoiningDate() != null) {
                DateTime mfiJoiningDate = CalendarUtils.getDateFromString(centerUpdate.getMfiJoiningDate(), userContext
                        .getPreferredLocale());
                this.setMfiJoiningDate(mfiJoiningDate.toDate());
            }
        } catch (InvalidDateException e) {
            throw new CustomerException(CustomerConstants.MFI_JOINING_DATE_MANDATORY, e);
        }
    }

    public Short getOfficeId() {
        return office.getOfficeId();
    }

    public void validateIsTopOfHierarchy() throws CustomerException {
        if (this.parentCustomer != null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }
    }
    
    public boolean isTopOfHierarchy() {
    	if (this.parentCustomer == null) {
    		return true;
    	}
    	return false;
    }

    public boolean hasMeetingDifferentTo(MeetingBO groupMeeting) {
        MeetingBO customerMeeting = getCustomerMeetingValue();

        if ((groupMeeting.getMeetingId() == null) && (customerMeeting.getMeetingId() == null)) {
            return false;
        }

        return !groupMeeting.getMeetingId().equals(customerMeeting.getMeetingId());
    }

    public Short getLoanOfficerId() {
        Short loanOfficerId = null;
        if (this.personnel != null) {
            loanOfficerId = this.personnel.getPersonnelId();
        }
        return loanOfficerId;
    }

    // To be used strictly from test code
    @Deprecated
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public boolean isValidMeetingDate(Date transactionDate) throws MeetingException {
        return customerMeeting.getMeeting().isValidMeetingDateUntilNextYear(transactionDate);
    }

    public CustomerDto toCustomerDto() {
        Short statusId = (getStatus() == null) ? null : getStatus().getValue();
        Short customerLevelId = (getCustomerLevel() == null) ? null : getCustomerLevel().getId();
        Short personnelId = (getPersonnel() == null) ? null : getPersonnel().getPersonnelId();

        return new CustomerDto(getCustomerId(), getDisplayName(), getGlobalCustNum(), statusId, customerLevelId,
                getVersionNo(), getOfficeId(), personnelId);
    }
    /**
     * Checks if any account (either savings or loan) is in 'active' state
     * @return true if any of customer's accounts is in active state
     */
    public boolean isAnyAccountActive(){
        boolean activeAccountExits=false;
        for (AccountBO account : getAccounts()) {
            activeAccountExits=account.isActiveLoanAccount() || account.isActiveSavingsAccount();
            if(activeAccountExits){
                break;
            }           
        }
        return activeAccountExits;
    }
    
    /**
     * Checks if there are any active periodic fees for customer.
     * @return true if at least one active and periodic fee is found, otherwise false
     */
    public boolean isAnyPeriodicFeeActive() {
    	for (AccountBO account : getAccounts()) {
    		if(!account.isAnyPeriodicFeeActive()) {
    			return true;
    		}
    	}
    	return false;
    }
}