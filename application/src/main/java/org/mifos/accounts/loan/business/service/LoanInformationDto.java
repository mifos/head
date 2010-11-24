package org.mifos.accounts.loan.business.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.loan.business.LoanActivityDto;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.api.DataTransferObject;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.framework.util.helpers.Money;

public class LoanInformationDto implements DataTransferObject {

    private final Integer accountId;
    private final String globalAccountNum;
    private final Short accountStateId;
    private final String accountStateName;
    private final String customerName;
    private final String globalCustNum;
    private final Integer customerId;
    private final String prdOfferingName;
    private final Set<String> accountFlagNames;
    private final Date disbursementDate;
    private final boolean redone;
    private final Integer businessActivityId;
    private final String gracePeriodTypeName;
    private final String interestTypeName;
    private final List<AccountNotesEntity> recentAccountNotes;
    private final Short accountTypeId;
    private final String officeName;
    private final Short officeId;
    private final Short personnelId;
    private final Date nextMeetingDate;
    private final Money totalAmountDue;
    private final Money totalAmountInArrears;
    private final LoanSummaryDto loanSummary;
    private final boolean loanActivityDetails;
    private final Double interestRate;
    private final boolean interestDeductedAtDisbursement;
    private final Short recurAfter;
    private final Short recurrenceId;
    private final boolean prinDueLastInst;
    private final Short noOfInstallments;
    private final Short minNoOfInstall;
    private final Short maxNoOfInstall;
    private final Short gracePeriodDuration;
    private final String fundName;
    private final Integer collateralTypeId;
    private final String collateralNote;
    private final String externalId;
    private final Set<AccountCustomFieldEntity> accountCustomFields;
    private final Set<AccountFeesDto> accountFees;
    private final Date createdDate;
    private final LoanPerformanceHistoryDto performanceHistory;
    private final boolean group;
    private final List<LoanActivityDto> recentAccountActivities;

    private final Boolean activeSurveys;
    private final List<SurveyDto> accountSurveys;


    public LoanInformationDto(String prdOfferingName, String globalAccountNum, Short accountStateId, String accountStateName,
                              Set<String> accountFlagNames, Date disbursementDate, boolean redone, Integer businessActivityId,
                              Integer accountId,String gracePeriodTypeName, String interestTypeName, List<AccountNotesEntity> recentAccountNotes,
                              Integer customerId, Short accountTypeId, Short officeId, Short personnelId, Date nextMeetingDate, Money totalAmountDue,
                              Money totalAmountInArrears, LoanSummaryDto loanSummary, boolean loanActivityDetails, Double interestRate,
                              boolean interestDeductedAtDisbursement,Short recurAfter, Short recurrenceId, boolean prinDueLastInst,
                              Short noOfInstallments, Short minNoOfInstall, Short maxNoOfInstall, Short gracePeriodDuration, String fundName,
                              Integer collateralTypeId,String collateralNote, String externalId, Set<AccountCustomFieldEntity> accountCustomFields,
                              Set<AccountFeesDto> accountFees, Date createdDate, LoanPerformanceHistoryDto performanceHistory, boolean group,
                              List<LoanActivityDto> recentAccountActivities, final Boolean activeSurveys, final List<SurveyDto> accountSurveys,
                              String customerName, String globalCustNum, String officeName) {

        this.prdOfferingName = prdOfferingName;
        this.globalAccountNum = globalAccountNum;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.accountFlagNames = accountFlagNames;
        this.disbursementDate = disbursementDate;
        this.redone = redone;
        this.businessActivityId = businessActivityId;
        this.accountId = accountId;
        this.gracePeriodTypeName = gracePeriodTypeName;
        this.interestTypeName = interestTypeName;
        this.recentAccountNotes = recentAccountNotes;
        this.customerId = customerId;
        this.accountTypeId = accountTypeId;
        this.officeId = officeId;
        this.personnelId = personnelId;
        this.nextMeetingDate = nextMeetingDate;
        this.totalAmountDue = totalAmountDue;
        this.totalAmountInArrears = totalAmountInArrears;
        this.loanSummary = loanSummary;
        this.loanActivityDetails = loanActivityDetails;
        this.interestRate = interestRate;
        this.interestDeductedAtDisbursement = interestDeductedAtDisbursement;
        this.recurAfter = recurAfter;
        this.recurrenceId = recurrenceId;
        this.prinDueLastInst = prinDueLastInst;
        this.noOfInstallments = noOfInstallments;
        this.minNoOfInstall = minNoOfInstall;
        this.maxNoOfInstall = maxNoOfInstall;
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
        this.recentAccountActivities = recentAccountActivities;

        this.activeSurveys = activeSurveys;
        this.accountSurveys = accountSurveys;

        this.customerName = customerName;
        this.globalCustNum = globalCustNum;
        this.officeName = officeName;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public Short getAccountStateId() {
        return this.accountStateId;
    }

    public String getAccountStateName() {
        return this.accountStateName;
    }

    public Set<String> getAccountFlagNames() {
        return this.accountFlagNames;
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

    public String getGracePeriodTypeName() {
        return this.gracePeriodTypeName;
    }

    public String getInterestTypeName() {
        return this.interestTypeName;
    }

    public List<AccountNotesEntity> getRecentAccountNotes() {
        return this.recentAccountNotes;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Short getAccountTypeId() {
        return this.accountTypeId;
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

    public LoanSummaryDto getLoanSummary() {
        return this.loanSummary;
    }

    public boolean getLoanActivityDetails() {
        return this.loanActivityDetails;
    }

    public Double getInterestRate() {
        return this.interestRate;
    }

    public boolean getInterestDeductedAtDisbursement() {
        return this.interestDeductedAtDisbursement;
    }

    public Short getRecurAfter() {
        return this.recurAfter;
    }

    public Short getRecurrenceId() {
        return this.recurrenceId;
    }

    public boolean getPrinDueLastInst() {
        return this.prinDueLastInst;
    }

    public Short getNoOfInstallments() {
        return this.noOfInstallments;
    }

    public Short getMinNoOfInstall() {
        return this.minNoOfInstall;
    }

    public Short getMaxNoOfInstall() {
        return this.maxNoOfInstall;
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

    public Set<AccountFeesDto> getAccountFees() {
        return this.accountFees;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public LoanPerformanceHistoryDto getPerformanceHistory() {
        return this.performanceHistory;
    }

    public boolean isGroup() {
        return this.group;
    }

    public List<LoanActivityDto> getRecentAccountActivity() {
        return this.recentAccountActivities;
    }

    public Boolean getActiveSurveys() {
        return this.activeSurveys;
    }

    public List<SurveyDto> getAccountSurveys() {
        return this.accountSurveys;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getGlobalCustNum() {
        return globalCustNum;
    }

    public String getOfficeName() {
        return officeName;
    }

    public boolean isDisbursed() {
        return AccountState.isDisbursed(accountStateId);
    }
}
