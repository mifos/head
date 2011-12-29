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

package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


/**
 * Data transfer object to hold data for display on the viewCenterDetails.jsp page
 */
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class CenterInformationDto implements Serializable {

    private final CenterDisplayDto centerDisplay;
    private final CustomerAccountSummaryDto customerAccountSummary;
    private final CenterPerformanceHistoryDto centerPerformanceHistory;
    private final CustomerAddressDto address;
    private final List<CustomerDetailDto> groupsOtherThanClosedAndCancelled;
    private final List<CustomerNoteDto> recentCustomerNotes;
    private final List<CustomerPositionOtherDto> customerPositions;
    private final List<SavingsDetailDto> savingsAccountsInUse;
    private final CustomerMeetingDto customerMeeting;
    private final Boolean activeSurveys;
    private final List<SurveyDto> customerSurveys;
    private final List<CustomFieldDto> customFields;

    private final List<SavingsDetailDto> closedSavingsAccounts;
    
    public CenterInformationDto(final CenterDisplayDto centerDisplay,
            final CustomerAccountSummaryDto customerAccountSummary,
            final CenterPerformanceHistoryDto centerPerformanceHistory, final CustomerAddressDto address,
            final List<CustomerDetailDto> groupsOtherThanClosedAndCancelled,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerPositionOtherDto> customerPositions,
            final List<SavingsDetailDto> savingsAccountsInUse, final CustomerMeetingDto customerMeeting,
            final Boolean activeSurveys, final List<SurveyDto> customerSurveys,
            final List<CustomFieldDto> customFields) {
        this.centerDisplay = centerDisplay;
        this.customerAccountSummary = customerAccountSummary;
        this.centerPerformanceHistory = centerPerformanceHistory;
        this.address = address;
        this.groupsOtherThanClosedAndCancelled = groupsOtherThanClosedAndCancelled;
        this.recentCustomerNotes = recentCustomerNotes;
        this.customerPositions = customerPositions;
        this.savingsAccountsInUse = savingsAccountsInUse;
        this.customerMeeting = customerMeeting;
        this.activeSurveys = activeSurveys;
        this.customerSurveys = customerSurveys;
        this.customFields = customFields;
        this.closedSavingsAccounts = Collections.emptyList();
    }
    
    public CenterInformationDto(final CenterDisplayDto centerDisplay,
            final CustomerAccountSummaryDto customerAccountSummary,
            final CenterPerformanceHistoryDto centerPerformanceHistory, final CustomerAddressDto address,
            final List<CustomerDetailDto> groupsOtherThanClosedAndCancelled,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerPositionOtherDto> customerPositions,
            final List<SavingsDetailDto> savingsAccountsInUse, final CustomerMeetingDto customerMeeting,
            final Boolean activeSurveys, final List<SurveyDto> customerSurveys,
            final List<CustomFieldDto> customFields,
            final List<SavingsDetailDto> closedSavingsAccounts) {
        this.centerDisplay = centerDisplay;
        this.customerAccountSummary = customerAccountSummary;
        this.centerPerformanceHistory = centerPerformanceHistory;
        this.address = address;
        this.groupsOtherThanClosedAndCancelled = groupsOtherThanClosedAndCancelled;
        this.recentCustomerNotes = recentCustomerNotes;
        this.customerPositions = customerPositions;
        this.savingsAccountsInUse = savingsAccountsInUse;
        this.customerMeeting = customerMeeting;
        this.activeSurveys = activeSurveys;
        this.customerSurveys = customerSurveys;
        this.customFields = customFields;
        this.closedSavingsAccounts = closedSavingsAccounts;
    }

    public CenterDisplayDto getCenterDisplay() {
        return this.centerDisplay;
    }

    public CustomerAccountSummaryDto getCustomerAccountSummary() {
        return this.customerAccountSummary;
    }

    public CenterPerformanceHistoryDto getCenterPerformanceHistory() {
        return this.centerPerformanceHistory;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="NM_CONFUSING", justification="its not confusing...")
    public CustomerAddressDto getAddress() {
        return this.address;
    }

    public List<CustomerDetailDto> getGroupsOtherThanClosedAndCancelled() {
        return this.groupsOtherThanClosedAndCancelled;
    }

    public List<CustomerNoteDto> getRecentCustomerNotes() {
        return this.recentCustomerNotes;
    }

    public List<CustomerPositionOtherDto> getCustomerPositions() {
        return this.customerPositions;
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

	public List<SavingsDetailDto> getClosedSavingsAccounts() {
		return closedSavingsAccounts;
	}
    
}