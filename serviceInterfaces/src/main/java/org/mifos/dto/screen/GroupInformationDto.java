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
import java.util.Collections;
import java.util.List;

import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerFlagDto;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.CustomerPositionOtherDto;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SurveyDto;

/**
 * Data transfer object to hold data for display on the viewgroupdetails.jsp page.
 */
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class GroupInformationDto implements Serializable {

    private final GroupDisplayDto groupDisplay;
    private final CustomerAccountSummaryDto customerAccountSummary;
    private final GroupPerformanceHistoryDto groupPerformanceHistory;
    /*
     * johnw - I can't find anywhere an address can be entered against a group (even if no center hierarchy). However,
     * the web UI does attempt to display at the moment.
     */
    private final CustomerAddressDto address;
    private final List<CustomerDetailDto> clientsOtherThanClosedAndCancelled;
    private final List<CustomerNoteDto> recentCustomerNotes;
    private final List<CustomerPositionOtherDto> customerPositions;
    private final List<CustomerFlagDto> customerFlags;
    private final List<LoanDetailDto> loanAccountsInUse;
    private final List<SavingsDetailDto> savingsAccountsInUse;
    private final CustomerMeetingDto customerMeeting;
    private final Boolean activeSurveys;
    private final List<SurveyDto> customerSurveys;
    private final List<CustomFieldDto> customFields;
    
    private final List<LoanDetailDto> closedLoanAccounts;
    private final List<SavingsDetailDto> closedSavingsAccounts;

    public GroupInformationDto(final GroupDisplayDto groupDisplay,
            final CustomerAccountSummaryDto customerAccountSummary, GroupPerformanceHistoryDto groupPerformanceHistory,
            final CustomerAddressDto address, final List<CustomerDetailDto> clientsOtherThanClosedAndCancelled,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerPositionOtherDto> customerPositions,
            final List<CustomerFlagDto> customerFlags, final List<LoanDetailDto> loanAccountsInUse,
            final List<SavingsDetailDto> savingsAccountsInUse, final CustomerMeetingDto customerMeeting,
            final Boolean activeSurveys, final List<SurveyDto> customerSurveys,
            final List<CustomFieldDto> customFields) {

        this.groupDisplay = groupDisplay;
        this.customerAccountSummary = customerAccountSummary;
        this.groupPerformanceHistory = groupPerformanceHistory;
        this.address = address;
        this.clientsOtherThanClosedAndCancelled = clientsOtherThanClosedAndCancelled;
        this.recentCustomerNotes = recentCustomerNotes;
        this.customerPositions = customerPositions;
        this.customerFlags = customerFlags;
        this.loanAccountsInUse = loanAccountsInUse;
        this.savingsAccountsInUse = savingsAccountsInUse;
        this.customerMeeting = customerMeeting;
        this.activeSurveys = activeSurveys;
        this.customerSurveys = customerSurveys;
        this.customFields = customFields;
        this.closedLoanAccounts = Collections.emptyList();
        this.closedSavingsAccounts = Collections.emptyList();
    }
    
    public GroupInformationDto(final GroupDisplayDto groupDisplay,
            final CustomerAccountSummaryDto customerAccountSummary, GroupPerformanceHistoryDto groupPerformanceHistory,
            final CustomerAddressDto address, final List<CustomerDetailDto> clientsOtherThanClosedAndCancelled,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerPositionOtherDto> customerPositions,
            final List<CustomerFlagDto> customerFlags, final List<LoanDetailDto> loanAccountsInUse,
            final List<SavingsDetailDto> savingsAccountsInUse, final CustomerMeetingDto customerMeeting,
            final Boolean activeSurveys, final List<SurveyDto> customerSurveys,
            final List<CustomFieldDto> customFields, final List<LoanDetailDto> closedLoanAccounts,
            final List<SavingsDetailDto> closedSavingsAccounts) {

        this.groupDisplay = groupDisplay;
        this.customerAccountSummary = customerAccountSummary;
        this.groupPerformanceHistory = groupPerformanceHistory;
        this.address = address;
        this.clientsOtherThanClosedAndCancelled = clientsOtherThanClosedAndCancelled;
        this.recentCustomerNotes = recentCustomerNotes;
        this.customerPositions = customerPositions;
        this.customerFlags = customerFlags;
        this.loanAccountsInUse = loanAccountsInUse;
        this.savingsAccountsInUse = savingsAccountsInUse;
        this.customerMeeting = customerMeeting;
        this.activeSurveys = activeSurveys;
        this.customerSurveys = customerSurveys;
        this.customFields = customFields;
		this.closedLoanAccounts = closedLoanAccounts;
		this.closedSavingsAccounts = closedSavingsAccounts;
    }

    public GroupDisplayDto getGroupDisplay() {
        return this.groupDisplay;
    }

    public CustomerAccountSummaryDto getCustomerAccountSummary() {
        return this.customerAccountSummary;
    }

    public GroupPerformanceHistoryDto getGroupPerformanceHistory() {
        return this.groupPerformanceHistory;
    }

    public List<CustomerDetailDto> getClientsOtherThanClosedAndCancelled() {
        return this.clientsOtherThanClosedAndCancelled;
    }

    public List<CustomerNoteDto> getRecentCustomerNotes() {
        return this.recentCustomerNotes;
    }

    public List<CustomerPositionOtherDto> getCustomerPositions() {
        return this.customerPositions;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="NM_CONFUSING", justification="its not confusing...")
    public CustomerAddressDto getAddress() {
        return this.address;
    }

    public List<CustomerFlagDto> getCustomerFlags() {
        return this.customerFlags;
    }

    public List<LoanDetailDto> getLoanAccountsInUse() {
        return this.loanAccountsInUse;
    }

    public List<SavingsDetailDto> getSavingsAccountsInUse() {
        return this.savingsAccountsInUse;
    }

    public CustomerMeetingDto getCustomerMeeting() {
        return this.customerMeeting;
    }

    public Boolean getActiveSurveys() {
        return this.activeSurveys;
    }

    public List<SurveyDto> getCustomerSurveys() {
        return this.customerSurveys;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

	public List<LoanDetailDto> getClosedLoanAccounts() {
		return closedLoanAccounts;
	}

	public List<SavingsDetailDto> getClosedSavingsAccounts() {
		return closedSavingsAccounts;
	}

}