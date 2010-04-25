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

package org.mifos.customers.center.business.service;

import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.customers.util.helpers.CenterDisplayDto;
import org.mifos.customers.util.helpers.CenterPerformanceHistoryDto;
import org.mifos.customers.util.helpers.CustomerAccountSummaryDto;
import org.mifos.customers.util.helpers.CustomerAddressDto;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerMeetingDto;
import org.mifos.customers.util.helpers.CustomerNoteDto;
import org.mifos.customers.util.helpers.CustomerPositionDto;
import org.mifos.customers.util.helpers.CustomerSurveyDto;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.business.service.DataTransferObject;

/**
 * Data transfer object to hold data for display on the viewCenterDetails.jsp page
 */
public class CenterInformationDto implements DataTransferObject {

    private final CenterDisplayDto centerDisplay;
    private final CustomerAccountSummaryDto customerAccountSummary;
    private final CenterPerformanceHistoryDto centerPerformanceHistory;
    private final CustomerAddressDto address;
    private final List<CustomerDetailDto> groupsOtherThanClosedAndCancelled;
    private final List<CustomerNoteDto> recentCustomerNotes;
    private final List<CustomerPositionDto> customerPositions;
    private final List<SavingsDetailDto> savingsAccountsInUse;
    private final CustomerMeetingDto customerMeeting;
    private final Boolean activeSurveys;
    private final List<CustomerSurveyDto> customerSurveys;
    private final List<CustomFieldDto> customFields;

    public CenterInformationDto(final CenterDisplayDto centerDisplay,
            final CustomerAccountSummaryDto customerAccountSummary,
            final CenterPerformanceHistoryDto centerPerformanceHistory, final CustomerAddressDto address,
            final List<CustomerDetailDto> groupsOtherThanClosedAndCancelled,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerPositionDto> customerPositions,
            final List<SavingsDetailDto> savingsAccountsInUse, final CustomerMeetingDto customerMeeting,
            final Boolean activeSurveys, final List<CustomerSurveyDto> customerSurveys,
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

    public CustomerAddressDto getAddress() {
        return this.address;
    }

    public List<CustomerDetailDto> getGroupsOtherThanClosedAndCancelled() {
        return this.groupsOtherThanClosedAndCancelled;
    }

    public List<CustomerNoteDto> getRecentCustomerNotes() {
        return this.recentCustomerNotes;
    }

    public List<CustomerPositionDto> getCustomerPositions() {
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

    public List<CustomerSurveyDto> getCustomerSurveys() {
        return this.customerSurveys;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

}
