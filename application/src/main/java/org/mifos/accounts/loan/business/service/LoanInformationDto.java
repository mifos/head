package org.mifos.accounts.loan.business.service;

import org.mifos.framework.util.helpers.Money;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountTypeEntity;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.accounts.loan.business.LoanSummaryEntity;
import org.mifos.accounts.loan.business.MaxMinNoOfInstall;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.framework.business.service.DataTransferObject;

public class LoanInformationDto implements DataTransferObject {

    private final Integer accountId;
    private final String globalAccountNum;
    private final AccountStateEntity accountState;
    private final Integer customerId;
    private final CustomerLevelEntity customerLevel;
    private final String prdOfferingName;
    private final Set<AccountFlagMapping> accountFlags;
    private final Date disbursementDate;
    private final boolean redone;
    private final Integer businessActivityId;
    private final Set<AccountActionDateEntity> accountActionDates;
    private final GracePeriodTypeEntity gracePeriodType;
    private final InterestTypesEntity interestType;
    private MeetingBO loanMeeting;
    private final Set<AccountNotesEntity> accountNotes;
    private final List<AccountNotesEntity> recentAccountNotes;
    private final AccountTypeEntity accountType;
    private final Short officeId;
    private final Short personnelId;
    private final Date nextMeetingDate;
    private final Money totalAmountDue;
    private final Money totalAmountInArrears;
    private final LoanSummaryEntity loanSummary;
    private final List<LoanActivityEntity> loanActivityDetails;
    private final Double interestRate;
    private final boolean intrestDeductedAtDisbursement;
    private final Short recurAfter;
    private final Short recurrenceId;
    private final boolean prinDueLastInst;
    private final Short noOfInstallments;
    private final MaxMinNoOfInstall maxMinNoOfInstall;
    private final Short gracePeriodDuration;
    private final String fundName;
    private final Integer collateralTypeId;
    private final String collateralNote;
    private final String externalId;
    private final Set<AccountCustomFieldEntity> accountCustomFields;
    private final Set<AccountFeesEntity> accountFees;
    private final Date createdDate;
    private final LoanPerformanceHistoryEntity performanceHistory;
    private final boolean group;

    public LoanInformationDto(String prdOfferingName, String globalAccountNum, AccountStateEntity accountState,
            Set<AccountFlagMapping> accountFlags, Date disbursementDate, boolean redone, Integer businessActivityId,
            Integer accountId,Set<AccountActionDateEntity> accountActionDates,
            GracePeriodTypeEntity gracePeriodType, InterestTypesEntity interestType,MeetingBO loanMeeting, Set<AccountNotesEntity> accountNotes,
            List<AccountNotesEntity> recentAccountNotes, CustomerLevelEntity customerLevel, Integer customerId,
            AccountTypeEntity accountType, Short officeId, Short personnelId, Date nextMeetingDate, Money totalAmountDue,
            Money totalAmountInArrears, LoanSummaryEntity loanSummary,
            List<LoanActivityEntity> loanActivityDetails, Double interestRate, boolean intrestDeductedAtDisbursement,
            Short recurAfter, Short recurrenceId, boolean prinDueLastInst, Short noOfInstallments,
            MaxMinNoOfInstall maxMinNoOfInstall, Short gracePeriodDuration, String fundName, Integer collateralTypeId,
            String collateralNote, String externalId, Set<AccountCustomFieldEntity> accountCustomFields,
            Set<AccountFeesEntity> accountFees, Date createdDate, LoanPerformanceHistoryEntity performanceHistory, boolean group) {
        super();
        this.prdOfferingName = prdOfferingName;
        this.globalAccountNum = globalAccountNum;
        this.accountState = accountState;
        this.accountFlags = accountFlags;
        this.disbursementDate = disbursementDate;
        this.redone = redone;
        this.businessActivityId = businessActivityId;
        this.accountId = accountId;
        this.accountActionDates = accountActionDates;
        this.gracePeriodType = gracePeriodType;
        this.interestType = interestType;
        this.loanMeeting = loanMeeting;
        this.accountNotes = accountNotes;
        this.recentAccountNotes = recentAccountNotes;
        this.customerLevel = customerLevel;
        this.customerId = customerId;
        this.accountType = accountType;
        this.officeId = officeId;
        this.personnelId = personnelId;
        this.nextMeetingDate = nextMeetingDate;
        this.totalAmountDue = totalAmountDue;
        this.totalAmountInArrears = totalAmountInArrears;
        this.loanSummary = loanSummary;
        this.loanActivityDetails = loanActivityDetails;
        this.interestRate = interestRate;
        this.intrestDeductedAtDisbursement = intrestDeductedAtDisbursement;
        this.recurAfter = recurAfter;
        this.recurrenceId = recurrenceId;
        this.prinDueLastInst = prinDueLastInst;
        this.noOfInstallments = noOfInstallments;
        this.maxMinNoOfInstall = maxMinNoOfInstall;
        this.gracePeriodDuration = gracePeriodDuration;
        this.fundName = fundName;
        this.collateralTypeId = collateralTypeId;
        this.collateralNote = collateralNote;
        this.externalId = externalId;
        this.accountCustomFields = accountCustomFields;
        this.accountFees = accountFees;
        this.createdDate = createdDate;
        this.performanceHistory = performanceHistory;
        this.group = group;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public AccountStateEntity getAccountState() {
        return this.accountState;
    }

    public Set<AccountFlagMapping> getAccountFlags() {
        return this.accountFlags;
    }

    public Date getDisbursementDate() {
        return this.disbursementDate;
    }

    public boolean isRedone() {
        return this.redone;
    }

    public Integer getBusinessActivityId() {
        return this.businessActivityId;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public Set<AccountActionDateEntity> getAccountActionDates() {
        return this.accountActionDates;
    }

    public GracePeriodTypeEntity getGracePeriodType() {
        return this.gracePeriodType;
    }

    public InterestTypesEntity getInterestType() {
        return this.interestType;
    }

    public MeetingBO getLoanMeeting() {
        return this.loanMeeting;
    }

    public Set<AccountNotesEntity> getAccountNotes() {
        return this.accountNotes;
    }

    public List<AccountNotesEntity> getRecentAccountNotes() {
        return this.recentAccountNotes;
    }

    public CustomerLevelEntity getCustomerLevel() {
        return this.customerLevel;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public AccountTypeEntity getAccountType() {
        return this.accountType;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Short getPersonnelId() {
        return this.personnelId;
    }

    public Date getNextMeetingDate() {
        return this.nextMeetingDate;
    }

    public Money getTotalAmountDue() {
        return this.totalAmountDue;
    }

    public Money getTotalAmountInArrears() {
        return this.totalAmountInArrears;
    }

    public LoanSummaryEntity getLoanSummary() {
        return this.loanSummary;
    }

    public List<LoanActivityEntity> getLoanActivityDetails() {
        return this.loanActivityDetails;
    }

    public Double getInterestRate() {
        return this.interestRate;
    }

    public boolean isIntrestDeductedAtDisbursement() {
        return this.intrestDeductedAtDisbursement;
    }

    public Short getRecurAfter() {
        return this.recurAfter;
    }

    public Short getRecurrenceId() {
        return this.recurrenceId;
    }

    public boolean isPrinDueLastInst() {
        return this.prinDueLastInst;
    }

    public Short getNoOfInstallments() {
        return this.noOfInstallments;
    }

    public MaxMinNoOfInstall getMaxMinNoOfInstall() {
        return this.maxMinNoOfInstall;
    }

    public Short getGracePeriodDuration() {
        return this.gracePeriodDuration;
    }

    public String getFundName() {
        return this.fundName;
    }

    public Integer getCollateralTypeId() {
        return this.collateralTypeId;
    }

    public String getCollateralNote() {
        return this.collateralNote;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public Set<AccountCustomFieldEntity> getAccountCustomFields() {
        return this.accountCustomFields;
    }

    public Set<AccountFeesEntity> getAccountFees() {
        return this.accountFees;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public LoanPerformanceHistoryEntity getPerformanceHistory() {
        return this.performanceHistory;
    }

    public boolean isGroup() {
        return this.group;
    }
}
