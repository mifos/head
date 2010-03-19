/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.customers.group.business.service;

import java.util.List;

import org.mifos.application.master.business.CustomFieldView;
import org.mifos.customers.util.helpers.CustomFieldDto;
import org.mifos.customers.util.helpers.CustomerAccountSummaryDto;
import org.mifos.customers.util.helpers.CustomerAddressDto;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerFlagDto;
import org.mifos.customers.util.helpers.CustomerMeetingDto;
import org.mifos.customers.util.helpers.CustomerNoteDto;
import org.mifos.customers.util.helpers.CustomerPositionDto;
import org.mifos.customers.util.helpers.CustomerSurveyDto;
import org.mifos.customers.util.helpers.GroupDisplayDto;
import org.mifos.customers.util.helpers.GroupPerformanceHistoryDto;
import org.mifos.customers.util.helpers.LoanDetailDto;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.business.service.DataTransferObject;

/**
 * Data transfer object to hold data for display on the viewgroupdetails.jsp page.
 */
public class GroupInformationDto implements DataTransferObject {

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
    private final List<CustomerPositionDto> customerPositions;
    private final List<CustomerFlagDto> customerFlags;
    private final List<LoanDetailDto> loanAccountsInUse;
    private final List<SavingsDetailDto> savingsAccountsInUse;
    private final CustomerMeetingDto customerMeeting;
    private final Boolean activeSurveys;
    private final List<CustomerSurveyDto> customerSurveys;
    private final List<CustomFieldView> customFields;

    public GroupInformationDto(final GroupDisplayDto groupDisplay,
            final CustomerAccountSummaryDto customerAccountSummary, GroupPerformanceHistoryDto groupPerformanceHistory,
            final CustomerAddressDto address, final List<CustomerDetailDto> clientsOtherThanClosedAndCancelled,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerPositionDto> customerPositions,
            final List<CustomerFlagDto> customerFlags, final List<LoanDetailDto> loanAccountsInUse,
            final List<SavingsDetailDto> savingsAccountsInUse, final CustomerMeetingDto customerMeeting,
            final Boolean activeSurveys, final List<CustomerSurveyDto> customerSurveys,
            final List<CustomFieldView> customFields) {

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

    public List<CustomerPositionDto> getCustomerPositions() {
        return this.customerPositions;
    }

    public CustomerAddressDto getaddress() {
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

    public List<CustomerSurveyDto> getCustomerSurveys() {
        return this.customerSurveys;
    }

    public List<CustomFieldView> getCustomFields() {
        return this.customFields;
    }

}
