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

import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerFlagDto;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SurveyDto;

/**
 * Initial data transfer object to hold data for display on the viewClientDetails page.
 *
 */
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class ClientInformationDto implements Serializable {

    private final ClientDisplayDto clientDisplay;
    private final CustomerAccountSummaryDto customerAccountSummary;
    private final ClientPerformanceHistoryDto clientPerformanceHistory;
    private final CustomerAddressDto address;

    private final List<CustomerNoteDto> recentCustomerNotes;
    private final List<CustomerFlagDto> customerFlags;
    private final List<LoanDetailDto> loanAccountsInUse;
    private final List<SavingsDetailDto> savingsAccountsInUse;
    private final CustomerMeetingDto customerMeeting;
    private final Boolean activeSurveys;
    private final List<SurveyDto> customerSurveys;
    
    private final List<LoanDetailDto> closedLoanAccounts;
    private final List<SavingsDetailDto> closedSavingsAccounts;

    public ClientInformationDto(final ClientDisplayDto clientDisplay,
            final CustomerAccountSummaryDto customerAccountSummary,
            final ClientPerformanceHistoryDto clientPerformanceHistory, final CustomerAddressDto address,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerFlagDto> customerFlags,
            final List<LoanDetailDto> loanAccountsInUse, final List<SavingsDetailDto> savingsAccountsInUse,
            final CustomerMeetingDto customerMeeting, final Boolean activeSurveys,
            final List<SurveyDto> customerSurveys) {
        this.clientDisplay = clientDisplay;
        this.customerAccountSummary = customerAccountSummary;
        this.clientPerformanceHistory = clientPerformanceHistory;
        this.address = address;
        this.recentCustomerNotes = recentCustomerNotes;
        this.customerFlags = customerFlags;
        this.loanAccountsInUse = loanAccountsInUse;
        this.savingsAccountsInUse = savingsAccountsInUse;
        this.customerMeeting = customerMeeting;
        this.activeSurveys = activeSurveys;
        this.customerSurveys = customerSurveys;
        this.closedLoanAccounts = Collections.emptyList();
        this.closedSavingsAccounts = Collections.emptyList();
    }

    public ClientInformationDto(final ClientDisplayDto clientDisplay,
    		final CustomerAccountSummaryDto customerAccountSummary,
    		final ClientPerformanceHistoryDto clientPerformanceHistory,
    		final CustomerAddressDto address,
    		final List<CustomerNoteDto> recentCustomerNotes,
    		final List<CustomerFlagDto> customerFlags,
    		final List<LoanDetailDto> loanAccountsInUse,
    		final List<SavingsDetailDto> savingsAccountsInUse,
    		final CustomerMeetingDto customerMeeting, Boolean activeSurveys,
    		final List<SurveyDto> customerSurveys,
    		final List<LoanDetailDto> closedLoanAccounts,
    		final List<SavingsDetailDto> closedSavingsAccounts) {
		this.clientDisplay = clientDisplay;
		this.customerAccountSummary = customerAccountSummary;
		this.clientPerformanceHistory = clientPerformanceHistory;
		this.address = address;
		this.recentCustomerNotes = recentCustomerNotes;
		this.customerFlags = customerFlags;
		this.loanAccountsInUse = loanAccountsInUse;
		this.savingsAccountsInUse = savingsAccountsInUse;
		this.customerMeeting = customerMeeting;
		this.activeSurveys = activeSurveys;
		this.customerSurveys = customerSurveys;
		this.closedLoanAccounts = closedLoanAccounts;
		this.closedSavingsAccounts = closedSavingsAccounts;
	}

	public ClientDisplayDto getClientDisplay() {
        return this.clientDisplay;
    }

    public CustomerAccountSummaryDto getCustomerAccountSummary() {
        return this.customerAccountSummary;
    }

    public ClientPerformanceHistoryDto getClientPerformanceHistory() {
        return this.clientPerformanceHistory;
    }

    public CustomerAddressDto getAddress() {
        return this.address;
    }

    public List<CustomerNoteDto> getRecentCustomerNotes() {
        return this.recentCustomerNotes;
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

	public List<LoanDetailDto> getClosedLoanAccounts() {
		return closedLoanAccounts;
	}

	public List<SavingsDetailDto> getClosedSavingsAccounts() {
		return closedSavingsAccounts;
	}
}