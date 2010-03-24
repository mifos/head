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

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.ClientDisplayDto;
import org.mifos.customers.util.helpers.ClientPerformanceHistoryDto;
import org.mifos.customers.util.helpers.CustomerAccountSummaryDto;
import org.mifos.customers.util.helpers.CustomerAddressDto;
import org.mifos.customers.util.helpers.CustomerFlagDto;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerMeetingDto;
import org.mifos.customers.util.helpers.CustomerNoteDto;
import org.mifos.customers.util.helpers.CustomerSurveyDto;
import org.mifos.customers.util.helpers.LoanCycleCounter;
import org.mifos.customers.util.helpers.LoanDetailDto;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class WebTierClientDetailsServiceFacade implements ClientDetailsServiceFacade {

    private final CustomerBusinessService customerBusinessService;
    private final CustomerDao customerDao;

    public WebTierClientDetailsServiceFacade(CustomerDao customerDao, CustomerBusinessService customerBusinessService) {
        this.customerBusinessService = customerBusinessService;
        this.customerDao = customerDao;
    }

    @Override
    public ClientInformationDto getClientInformationDto(String globalCustNum, UserContext userContext)
            throws ServiceException {

        ClientBO client = customerDao.findClientBySystemId(globalCustNum);
        if (client == null) {
            throw new MifosRuntimeException("Client not found for globalCustNum, levelId: " + globalCustNum);
        }

        ClientDisplayDto clientDisplay = customerBusinessService.getClientDisplayDto(client.getCustomerId(),
                userContext);

        Integer clientId = client.getCustomerId();
        String searchId = client.getSearchId();
        Short branchId = client.getOffice().getOfficeId();

        CustomerAccountSummaryDto customerAccountSummary = this.customerDao.getCustomerAccountSummaryDto(
                clientId);

        ClientPerformanceHistoryDto clientPerformanceHistory = assembleClientPerformanceHistoryDto(client
                .getClientPerformanceHistory(), clientId, searchId, branchId);

        CustomerAddressDto clientAddress = this.customerDao.getCustomerAddressDto(client);

        List<CustomerNoteDto> recentCustomerNotes = customerDao.getRecentCustomerNoteDto(clientId);

        List<CustomerFlagDto> customerFlags = customerBusinessService.getCustomerFlagDto(client.getCustomerFlags());

        List<LoanDetailDto> loanDetail = customerBusinessService.getLoanDetailDto(client.getOpenLoanAccounts());

        List<SavingsDetailDto> savingsDetail = customerDao.getSavingsDetailDto(clientId, userContext);

        CustomerMeetingDto customerMeeting = customerDao.getCustomerMeetingDto(client.getCustomerMeeting(), userContext);

        Boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.CLIENT);

        List<CustomerSurveyDto> customerSurveys = customerDao.getCustomerSurveyDto(clientId);

        List<CustomFieldView> customFields = customerDao.getCustomFieldViewForCustomers(clientId,
                EntityType.CLIENT.getValue(), userContext);

        return new ClientInformationDto(clientDisplay, customerAccountSummary, clientPerformanceHistory, clientAddress,
                recentCustomerNotes, customerFlags, loanDetail, savingsDetail, customerMeeting, activeSurveys, customerSurveys, customFields);
    }

    private ClientPerformanceHistoryDto assembleClientPerformanceHistoryDto(
            ClientPerformanceHistoryEntity clientPerformanceHistory, Integer clientId, String searchId, Short officeId)
            throws ServiceException {

        Integer loanCycleNumber = clientPerformanceHistory.getLoanCycleNumber();

        Money lastLoanAmount = clientPerformanceHistory.getLastLoanAmount();

        Integer noOfActiveLoans = clientPerformanceHistory.getNoOfActiveLoans();

        String delinquentPortfolioAmountString;
        try {
            Money delinquentPortfolioAmount = clientPerformanceHistory.getDelinquentPortfolioAmount();
            delinquentPortfolioAmountString = delinquentPortfolioAmount.toString();
        } catch (CurrencyMismatchException e) {
            delinquentPortfolioAmountString = localizedMessageLookup("errors.multipleCurrencies");
        }

        // TODO currency mismatch check
        Money totalSavingsAmount = clientPerformanceHistory.getTotalSavingsAmount();

        Integer meetingsAttended;
        try {
            meetingsAttended = customerBusinessService.numberOfMeetings(true, clientId).getMeetingsAttended();
        } catch (InvalidDateException e) {
            throw new ServiceException(e);
        }

        Integer meetingsMissed;
        try {
            meetingsMissed = customerBusinessService.numberOfMeetings(false, clientId).getMeetingsMissed();
        } catch (InvalidDateException e) {
            throw new ServiceException(e);
        }

        List<LoanCycleCounter> loanCycleCounters = customerBusinessService.fetchLoanCycleCounter(clientId,
                CustomerLevel.CLIENT.getValue());

        return new ClientPerformanceHistoryDto(loanCycleNumber, lastLoanAmount, noOfActiveLoans,
                delinquentPortfolioAmountString, totalSavingsAmount, meetingsAttended, meetingsMissed,
                loanCycleCounters);
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }

}
