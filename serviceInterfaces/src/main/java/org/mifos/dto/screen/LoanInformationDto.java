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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.SurveyDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "SE_BAD_FIELD"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanInformationDto implements Serializable {

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
    private final Short accountTypeId;
    private final String officeName;
    private final Short officeId;
    private final Short personnelId;
    private final Date nextMeetingDate;
    private final String totalAmountDue;
    private final String totalAmountInArrears;
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
    private final Set<AccountFeesDto> accountFees;
    private final Date createdDate;
    private final LoanPerformanceHistoryDto performanceHistory;
    private final boolean group;
    private final List<LoanActivityDto> recentAccountActivities;
    private final List<CustomerNoteDto> recentNoteDtos;

    private final Boolean activeSurveys;
    private final List<SurveyDto> accountSurveys;
    private boolean disbursed;


    public LoanInformationDto(String prdOfferingName, String globalAccountNum, Short accountStateId, String accountStateName, boolean disbursed,
                              Set<String> accountFlagNames, Date disbursementDate, boolean redone, Integer businessActivityId,
                              Integer accountId, String gracePeriodTypeName, String interestTypeName,
                              Integer customerId, Short accountTypeId, Short officeId, Short personnelId, Date nextMeetingDate, String totalAmountDue,
                              String totalAmountInArrears, LoanSummaryDto loanSummary, boolean loanActivityDetails, Double interestRate,
                              boolean interestDeductedAtDisbursement, Short recurAfter, Short recurrenceId, boolean prinDueLastInst,
                              Short noOfInstallments, Short minNoOfInstall, Short maxNoOfInstall, Short gracePeriodDuration, String fundName,
                              Integer collateralTypeId, String collateralNote, String externalId,
                              Set<AccountFeesDto> accountFees, Date createdDate, LoanPerformanceHistoryDto performanceHistory, boolean group,
                              List<LoanActivityDto> recentAccountActivities, final Boolean activeSurveys, final List<SurveyDto> accountSurveys,
                              String customerName, String globalCustNum, String officeName, List<CustomerNoteDto> recentNoteDtos) {

        this.prdOfferingName = prdOfferingName;
        this.globalAccountNum = globalAccountNum;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.disbursed = disbursed;
        this.accountFlagNames = accountFlagNames;
        this.disbursementDate = disbursementDate;
        this.redone = redone;
        this.businessActivityId = businessActivityId;
        this.accountId = accountId;
        this.gracePeriodTypeName = gracePeriodTypeName;
        this.interestTypeName = interestTypeName;
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
        this.recentNoteDtos = recentNoteDtos;
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

    public String getTotalAmountDue() {
        return this.totalAmountDue;
    }

    public String getTotalAmountInArrears() {
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

    public List<CustomerNoteDto> getRecentNoteDtos() {
        return recentNoteDtos;
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
        return disbursed;
    }
}
