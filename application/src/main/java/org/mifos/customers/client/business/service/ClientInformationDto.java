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

package org.mifos.customers.client.business.service;

import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.customers.util.helpers.ClientDisplayDto;
import org.mifos.customers.util.helpers.ClientPerformanceHistoryDto;
import org.mifos.customers.util.helpers.CustomerAccountSummaryDto;
import org.mifos.customers.util.helpers.CustomerAddressDto;
import org.mifos.customers.util.helpers.CustomerFlagDto;
import org.mifos.customers.util.helpers.CustomerMeetingDto;
import org.mifos.customers.util.helpers.CustomerNoteDto;
import org.mifos.customers.util.helpers.CustomerSurveyDto;
import org.mifos.customers.util.helpers.LoanDetailDto;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.business.service.DataTransferObject;

/**
 * Initial data transfer object to hold data for display on the viewClientDetails page.
 * 
 */
public class ClientInformationDto implements DataTransferObject {
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
    private final List<CustomerSurveyDto> customerSurveys;
    private final List<CustomFieldDto> customFields;

    public ClientInformationDto(final ClientDisplayDto clientDisplay,
            final CustomerAccountSummaryDto customerAccountSummary,
            final ClientPerformanceHistoryDto clientPerformanceHistory, final CustomerAddressDto address,
            final List<CustomerNoteDto> recentCustomerNotes, final List<CustomerFlagDto> customerFlags,
            final List<LoanDetailDto> loanAccountsInUse, final List<SavingsDetailDto> savingsAccountsInUse,
            final CustomerMeetingDto customerMeeting, final Boolean activeSurveys,
            final List<CustomerSurveyDto> customerSurveys, final List<CustomFieldDto> customFields) {
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
        this.customFields = customFields;
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

    public List<CustomerSurveyDto> getCustomerSurveys() {
        return this.customerSurveys;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

}
