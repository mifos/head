/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.client.business.ClientBO;
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
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.ChapterNum;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 * A class that represents a customer entity after being created.
 */
public abstract class CustomerBO extends AbstractBusinessObject {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERLOGGER);

    private final Integer customerId;
    private String globalCustNum;
    private String displayName;
    private String displayAddress;
    private String externalId;
    private Short trained;
    private Date trainedDate;
    private Date mfiJoiningDate;
    private String searchId;
    private Integer maxChildCount = Integer.valueOf(0);
    private Date customerActivationDate;
    private CustomerStatusEntity customerStatus;
    private Set<CustomerCustomFieldEntity> customFields;
    private Set<CustomerPositionEntity> customerPositions;
    private Set<CustomerFlagDetailEntity> customerFlags;
    private CustomerBO parentCustomer;
    private Set<AccountBO> accounts;
    private CustomerAccountBO customerAccount;
    private final CustomerLevelEntity customerLevel;
    private PersonnelBO personnel;
    private final PersonnelBO formedByPersonnel;
    private OfficeBO office;
    private CustomerAddressDetailEntity customerAddressDetail;
    private CustomerMeetingEntity customerMeeting;
    private Set<CustomerHierarchyEntity> customerHierarchies;
    private Set<CustomerMovementEntity> customerMovements;
    private CustomerHistoricalDataEntity historicalData;
    public Short blackListed = YesNoFlag.NO.getValue();
    private Set<CustomerNoteEntity> customerNotes;
    private Set<CustomerBO> children;

    private CustomerPersistence customerPersistence = null;
    private PersonnelPersistence personnelPersistence = null;
    private MasterPersistence masterPersistence = null;

    /**
     * default constructor for hibernate
     */
    protected CustomerBO() {
        this(null, null, null, null, null);
        this.globalCustNum = null;
    }

    /**
     * @deprecated - use minimal legal constructor or static factory methods of subclasses in builder
     */
    @Deprecated
    public CustomerBO(final CustomerLevel customerLevel, final CustomerStatus customerStatus, final String name,
            final OfficeBO office, final PersonnelBO loanOfficer, final CustomerMeetingEntity customerMeeting,
            final CustomerBO parentCustomer) {
        super();
        this.customerId = null;
        this.displayName = name;
        this.office = office;
        this.personnel = loanOfficer;
        this.customerMeeting = customerMeeting;
        this.customerMeeting.setCustomer(this);
        this.parentCustomer = parentCustomer;

        this.accounts = new HashSet<AccountBO>();
        this.customerLevel = new CustomerLevelEntity(customerLevel);
        this.customerStatus = new CustomerStatusEntity(customerStatus);
        this.formedByPersonnel = null;

        this.customerNotes = new HashSet<CustomerNoteEntity>();
        this.customerFlags = new HashSet<CustomerFlagDetailEntity>();
        this.userContext = new UserContext();
        this.userContext.setBranchGlobalNum(office.getGlobalOfficeNum());
    }

    /**
     * @deprecated - use minimal legal constructor or static factory methods of subclasses in builder
     */
    @Deprecated
    protected CustomerBO(final Integer customerId, final CustomerLevelEntity customerLevel,
            final PersonnelBO formedByPersonnel, final PersonnelBO personnel, final String displayName) {
        super();
        this.customerId = customerId;
        this.customerLevel = customerLevel;
        this.formedByPersonnel = formedByPersonnel;
        this.personnel = personnel;
        this.displayName = displayName;
    }

    /**
     * minimal legal constructor for center, groups and clients.
     */
    public CustomerBO(UserContext userContext, String customerName, CustomerLevel customerLevel, CustomerStatus customerStatus,
            DateTime mfiJoiningDate, OfficeBO office, MeetingBO meeting, PersonnelBO loanOfficer, PersonnelBO formedBy) {
        super(userContext);
        this.customerId = null;
        this.customerHierarchies = new HashSet<CustomerHierarchyEntity>();
        this.customerMovements = new HashSet<CustomerMovementEntity>();
        this.customerPositions = new HashSet<CustomerPositionEntity>();
        this.customFields = new HashSet<CustomerCustomFieldEntity>();
        this.accounts = new HashSet<AccountBO>();
        this.children = new HashSet<CustomerBO>();
        this.customerFlags = new HashSet<CustomerFlagDetailEntity>();
        this.customerNotes = new HashSet<CustomerNoteEntity>();
        this.mfiJoiningDate = mfiJoiningDate.toDate();
        this.displayName = customerName;
        this.office = office;
        this.personnel = loanOfficer;
        this.formedByPersonnel = formedBy;
        this.customerLevel = new CustomerLevelEntity(customerLevel);
        this.customerMeeting = new CustomerMeetingEntity(this, meeting);
        this.customerStatus = new CustomerStatusEntity(customerStatus);
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

        this.parentCustomer = parentCustomer;

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

    public Integer getCustomerId() {
        return customerId;
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

    public final void setCustomerStatus(final CustomerStatusEntity customerStatus) {
        this.customerStatus = customerStatus;
    }

    public void updateCustomerStatus(CustomerStatus newCustomerStatus) {
        this.customerStatus = new CustomerStatusEntity(newCustomerStatus);
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
        return customerMeeting.getMeeting();
    }

    public void setMeeting(final MeetingBO meeting) {
        this.customerMeeting = new CustomerMeetingEntity(this, meeting);
    }

    public void setCustomerMeeting(final CustomerMeetingEntity customerMeeting) {
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

    /**
     * @deprecated - use {@link CustomerDao}.
     */
    @Deprecated
    protected void persist() throws CustomerException {
        try {
            getCustomerPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
        }
    }

    /**
     * @deprecated - use {@link CustomerDao}.
     */
    @Deprecated
    public void update(final UserContext userContext, final String externalId, final Address address,
            final List<CustomFieldDto> customFields, final List<CustomerPositionDto> customerPositions)
            throws CustomerException, InvalidDateException {
        this.setUserContext(userContext);
        this.setExternalId(externalId);
        updateAddress(address);
        updateCustomFields(customFields);
        updateCustomerPositions(customerPositions);
        this.update();
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

    public void adjustPmnt(final String adjustmentComment) throws ApplicationException, SystemException {
        getCustomerAccount().adjustPmnt(adjustmentComment);
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

    public abstract void updateMeeting(MeetingBO meeting) throws CustomerException;

    protected void saveUpdatedMeeting(final MeetingBO meeting) throws CustomerException {
        logger.debug("In CustomerBO::saveUpdatedMeeting(), customerId: " + getCustomerId());
        getCustomerMeeting().setUpdatedMeeting(meeting);
        setUpdatedMeetingForChildren(meeting);
        getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
        persist();
    }

    private void setUpdatedMeetingForChildren(final MeetingBO meeting) throws CustomerException {
        logger.debug("In CustomerBO::setUpdatedMeetingForChildren(), customerId: " + getCustomerId());
        Set<CustomerBO> childList = getChildren();
        if (childList != null) {
            for (CustomerBO child : childList) {
                child.setUserContext(getUserContext());
                child.updateMeeting(meeting);
            }
        }
    }

    public void changeUpdatedMeeting() throws CustomerException {
        logger.debug("In CustomerBO::changeUpdatedMeeting(), customerId: " + getCustomerId());
        MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
        MeetingBO oldMeeting = getCustomerMeeting().getMeeting();
        if (newMeeting != null) {
            if (sameRecurrence(oldMeeting, newMeeting)) {
                logger.debug("In CustomerBO::changeUpdatedMeeting(), Same Recurrence Found, customerId: "
                        + getCustomerId());
                updateMeeting(oldMeeting, newMeeting);
                resetUpdatedMeetingForChildren(oldMeeting);
                if (getParentCustomer() == null) {
                    deleteMeeting(newMeeting);
                }
            } else {
                logger.debug("In CustomerBO::changeUpdatedMeeting(), Different Recurrence Found, customerId: "
                        + getCustomerId());
                getCustomerMeeting().setMeeting(newMeeting);
                resetUpdatedMeetingForChildren(newMeeting);
                if (getParentCustomer() == null) {
                    deleteMeeting(oldMeeting);
                }
            }
            getCustomerMeeting().setUpdatedMeeting(null);
        }
        persist();
    }

    protected void resetUpdatedMeetingForChildren(final MeetingBO currentMeeting) throws CustomerException {
        logger.debug("In CustomerBO::resetUpdatedMeetingForChildren(), customerId: " + getCustomerId());
        Set<CustomerBO> childList = getChildren();
        if (childList != null) {
            for (CustomerBO child : childList) {
                child.getCustomerMeeting().setMeeting(currentMeeting);
                child.getCustomerMeeting().setUpdatedMeeting(null);
                child.resetUpdatedMeetingForChildren(currentMeeting);
                child.persist();
            }
        }
    }

    protected void deleteMeeting(final MeetingBO meeting) throws CustomerException {
        logger.debug("In CustomerBO::deleteMeeting(), customerId: " + getCustomerId());
        try {
            if (meeting != null) {
                logger.debug("In CustomerBO::deleteMeeting(), customerId: " + getCustomerId() + " , meetingId: "
                        + meeting.getMeetingId());
                getCustomerPersistence().deleteMeeting(meeting);
            }
        } catch (PersistenceException pe) {
            throw new CustomerException(pe);
        }
    }

    protected void updateMeeting(final MeetingBO oldMeeting, final MeetingBO newMeeting) throws CustomerException {
        try {
            if (oldMeeting.isWeekly()) {
                oldMeeting.update(newMeeting.getMeetingDetails().getWeekDay(), newMeeting.getMeetingPlace());
            } else if (oldMeeting.isMonthlyOnDate()) {
                oldMeeting.update(newMeeting.getMeetingDetails().getDayNumber(), newMeeting.getMeetingPlace());
            } else if (oldMeeting.isMonthly()) {
                oldMeeting.update(newMeeting.getMeetingDetails().getWeekDay(), newMeeting.getMeetingDetails()
                        .getWeekRank(), newMeeting.getMeetingPlace());
            }

        } catch (MeetingException me) {
            throw new CustomerException(me);
        }
    }

    private boolean sameRecurrence(final MeetingBO oldMeeting, final MeetingBO newMeeting) {
        return oldMeeting.isWeekly() && newMeeting.isWeekly() || oldMeeting.isMonthlyOnDate()
                && newMeeting.isMonthlyOnDate() || oldMeeting.isMonthly() && !oldMeeting.isMonthlyOnDate()
                && newMeeting.isMonthly() && !newMeeting.isMonthlyOnDate();
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

    public List<LoanBO> getOpenIndividualLoanAccounts() {
        List<LoanBO> loanAccounts = new ArrayList<LoanBO>();
        for (AccountBO account : getAccounts()) {
            if (account.isOfType(AccountTypes.INDIVIDUAL_LOAN_ACCOUNT) && account.isOpen()) {
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
            if (account.isLoanAccount() && account.isOpen()) {
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

    public void decrementChildCount() {
        this.maxChildCount = this.getMaxChildCount().intValue() - 1;
    }

    public CustomerLevel getLevel() {
        return CustomerLevel.getLevel(getCustomerLevel().getId());
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
            throw new CustomerException(CustomerConstants.INVALID_LOAN_OFFICER);
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

    public final boolean isDifferentBranch(final OfficeBO otherOffice) {
        return !isSameBranch(otherOffice);
    }

    public void updateCustomFields(final List<CustomFieldDto> customFields) throws InvalidDateException {
        if (customFields != null) {
            for (CustomFieldDto fieldView : customFields) {
                if (fieldView.getFieldTypeAsEnum() == CustomFieldType.DATE
                        && StringUtils.isNotBlank(fieldView.getFieldValue())) {
                    fieldView.convertDateToUniformPattern(getUserContext().getPreferredLocale());
                }

                for (CustomerCustomFieldEntity fieldEntity : getCustomFields()) {
                    if (fieldView.getFieldId().equals(fieldEntity.getFieldId())) {
                        fieldEntity.setFieldValue(fieldView.getFieldValue());
                    }
                }
            }
        }
    }

    public void updateCustomerPositions(final List<CustomerPositionDto> customerPositions) throws CustomerException {
        if (customerPositions != null) {
            for (CustomerPositionDto positionView : customerPositions) {
                boolean isPositionFound = false;
                for (CustomerPositionEntity positionEntity : getCustomerPositions()) {
                    if (positionView.getPositionId().equals(positionEntity.getPosition().getId())) {
                        positionEntity.setCustomer(getCustomer(positionView.getCustomerId()));
                        isPositionFound = true;
                        break;
                    }
                }
                if (!isPositionFound) {
                    addCustomerPosition(new CustomerPositionEntity(new PositionEntity(positionView.getPositionId()),
                            getCustomer(positionView.getCustomerId()), this));
                }
            }
        }
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

    /**
     * @deprecated - use {@link CustomerDao}
     */
    @Deprecated
    protected void updateLoanOfficer(final Short loanOfficerId) throws Exception {

        try {
            if (isLOChanged(loanOfficerId)) {
                // If a new loan officer has been assigned, then propagate this
                // change to the customer's children and to their associated
                // accounts.
                getCustomerPersistence().updateLOsForAllChildren(loanOfficerId, getSearchId(),
                        getOffice().getOfficeId());
                getCustomerPersistence().updateLOsForAllChildrenAccounts(loanOfficerId, getSearchId(),
                        getOffice().getOfficeId());
                if (loanOfficerId != null) {
                    this.personnel = getPersonnelPersistence().getPersonnel(loanOfficerId);
                } else {
                    this.personnel = null;
                }
            }
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }
    }

    public void setLoanOfficer(PersonnelBO loanOfficer) {
        this.personnel = loanOfficer;
    }

    public boolean isLoanOfficerChanged(PersonnelBO oldLoanOfficer) {

        if (oldLoanOfficer == null && this.personnel == null) {
            return false;
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
        CustomerMovementEntity newCustomerMovement = new CustomerMovementEntity(this, new DateTimeService()
                .getCurrentJavaDateTime());
        this.addCustomerMovement(newCustomerMovement);
    }

    protected void changeParentCustomer(final CustomerBO newParent) throws CustomerException {
        CustomerBO oldParent = getParentCustomer();
        setParentCustomer(newParent);

        CustomerHierarchyEntity currentHierarchy = getActiveCustomerHierarchy();
        if (null != currentHierarchy) {
            currentHierarchy.makeInactive(userContext.getId());
        }
        addCustomerHierarchy(new CustomerHierarchyEntity(this, newParent));
        handleParentTransfer();
        childRemovedForParent(oldParent);
        childAddedForParent(newParent);
        setSearchId(newParent.getSearchId() + "." + String.valueOf(newParent.getMaxChildCount()));

        oldParent.setUserContext(getUserContext());
        oldParent.update();
        newParent.setUserContext(getUserContext());
        newParent.update();
    }

    protected String generateSystemId() {
        String systemId = "";
        int numberOfZeros = CustomerConstants.SYSTEM_ID_LENGTH - String.valueOf(getCustomerId()).length();

        for (int i = 0; i < numberOfZeros; i++) {
            systemId = systemId + "0";
        }

        return getOffice().getGlobalOfficeNum() + "-" + systemId + getCustomerId();
    }

    protected void handleParentTransfer() {
        setPersonnel(getParentCustomer().getPersonnel());
        if (getParentCustomer().getCustomerMeeting() != null) {
            if (getCustomerMeeting() != null) {
                if (!getCustomerMeeting().getMeeting().getMeetingId().equals(
                        getParentCustomer().getCustomerMeeting().getMeeting().getMeetingId())) {
                    setUpdatedMeeting(getParentCustomer().getCustomerMeeting().getMeeting());
                }
            } else {
                setCustomerMeeting(createCustomerMeeting(getParentCustomer().getCustomerMeeting().getMeeting()));
            }
        } else if (getCustomerMeeting() != null) {
            deleteCustomerMeeting();
        }
    }

    public void setUpdatedMeeting(final MeetingBO meeting) {
        getCustomerMeeting().setUpdatedMeeting(meeting);
        getCustomerMeeting().setUpdatedFlag(YesNoFlag.YES.getValue());
    }

    /**
     * @deprecated - move methods that call persistence out of domain model.
     */
    @Deprecated
    protected void deleteCustomerMeeting() {
        logger.debug("In CustomerBO::deleteCustomerMeeting(), customerId: " + getCustomerId());
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
                addCustomField(new CustomerCustomFieldEntity(customField.getFieldId(), customField.getFieldValue(),
                        this));
            }
        }
    }

    private void inheritDetailsFromParent(final CustomerBO parentCustomer) throws CustomerException {
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
        if (StringUtils.isBlank(displayName)) {
            throw new CustomerException(CustomerConstants.INVALID_NAME);
        }

        validateOffice();
        validateLoanOfficer();
        validateFormedBy();
        validateTrained();
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

    private CustomerBO getCustomer(final Integer customerId) throws CustomerException {
        try {
            return customerId != null ? getCustomerPersistence().getCustomer(customerId) : null;
        } catch (PersistenceException pe) {
            throw new CustomerException(pe);
        }
    }

    public void childAddedForParent(final CustomerBO parent) {
        parent.incrementChildCount();
    }

    protected void childRemovedForParent(final CustomerBO parent) {
        parent.decrementChildCount();
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

    public boolean hasActiveLoanAccountsForProduct(final LoanOfferingBO loanOffering) {
        for (AccountBO account : getAccounts()) {
            if (account.isActiveLoanAccount() && ((LoanBO) account).getLoanOffering().isOfSameOffering(loanOffering)) {
                return true;
            }
        }
        return false;
    }

    protected void generateSearchId() throws CustomerException {
        int count;
        if (getParentCustomer() != null) {
            childAddedForParent(getParentCustomer());
            this.setSearchId(getParentCustomer().getSearchId() + "." + getParentCustomer().getMaxChildCount());
        } else {
            try {
                count = getCustomerPersistence().getCustomerCountForOffice(CustomerLevel.CLIENT,
                        getOffice().getOfficeId());
            } catch (PersistenceException pe) {
                throw new CustomerException(pe);
            }
            String searchId = GroupConstants.PREFIX_SEARCH_STRING + ++count;
            this.setSearchId(searchId);
        }
    }

    public void removeGroupMemberShip(final PersonnelBO personnel, final String comment) throws PersistenceException,
            CustomerException {
        PersonnelBO user = getPersonnelPersistence().getPersonnel(getUserContext().getId());
        CustomerNoteEntity accountNotesEntity = new CustomerNoteEntity(comment, new DateTimeService()
                .getCurrentJavaSqlDate(), user, this);
        this.addCustomerNotes(accountNotesEntity);

        resetPositions(getParentCustomer());
        getParentCustomer().setUserContext(getUserContext());
        getParentCustomer().update();

        setPersonnel(personnel);
        setParentCustomer(null);
        generateSearchId();
        update();
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

    public PersonnelPersistence getPersonnelPersistence() {
        if (null == personnelPersistence) {
            personnelPersistence = new PersonnelPersistence();
        }
        return personnelPersistence;
    }

    public void setPersonnelPersistence(final PersonnelPersistence personnelPersistence) {
        this.personnelPersistence = personnelPersistence;
    }

    public MasterPersistence getMasterPersistence() {
        if (null == masterPersistence) {
            masterPersistence = new MasterPersistence();
        }
        return masterPersistence;
    }

    public void setMasterPersistence(final MasterPersistence masterPersistence) {
        this.masterPersistence = masterPersistence;
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
            if (!isBlackListed()) {
                this.customerFlags.clear();
            }
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

        return new CustomerDetailDto(this.customerId, this.displayName, this.searchId, this.globalCustNum,
                loanOfficerId, this.externalId, address);
    }

    public boolean hasSameIdentityAs(CustomerBO customer) {
        if ((this.customerId == null && customer.getCustomerId() == null) && (this.globalCustNum == null && customer.getGlobalCustNum() == null)) {
            return this.displayName.equals(customer.displayName);
        }
        return this.globalCustNum.equals(customer.getGlobalCustNum());
    }
}