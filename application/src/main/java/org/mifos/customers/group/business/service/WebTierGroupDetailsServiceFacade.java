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

package org.mifos.customers.group.business.service;

import java.util.List;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerAccountSummaryDto;
import org.mifos.customers.util.helpers.CustomerAddressDto;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerFlagDto;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerMeetingDto;
import org.mifos.customers.util.helpers.CustomerNoteDto;
import org.mifos.customers.util.helpers.CustomerPositionDto;
import org.mifos.customers.util.helpers.CustomerSurveyDto;
import org.mifos.customers.util.helpers.GroupDisplayDto;
import org.mifos.customers.util.helpers.GroupPerformanceHistoryDto;
import org.mifos.customers.util.helpers.LoanCycleCounter;
import org.mifos.customers.util.helpers.LoanDetailDto;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class WebTierGroupDetailsServiceFacade implements GroupDetailsServiceFacade {

    private final CustomerDao customerDao;

    public WebTierGroupDetailsServiceFacade(final CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public GroupInformationDto getGroupInformationDto(String globalCustNum, UserContext userContext)
            throws ServiceException {

        GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);
        if (group == null) {
            throw new MifosRuntimeException("Group not found for globalCustNum: " + globalCustNum);
        }

        GroupDisplayDto groupDisplay = this.customerDao.getGroupDisplayDto(group.getCustomerId(), userContext);

        Integer groupId = group.getCustomerId();
        String searchId = group.getSearchId();
        Short branchId = groupDisplay.getBranchId();

        CustomerAccountSummaryDto customerAccountSummary = this.customerDao.getCustomerAccountSummaryDto(groupId);

        GroupPerformanceHistoryDto groupPerformanceHistory = assembleGroupPerformanceHistoryDto(group
                .getGroupPerformanceHistory(), searchId, branchId, groupId);

        CustomerAddressDto groupAddress = this.customerDao.getCustomerAddressDto(group);
        List<CustomerDetailDto> clients = this.customerDao.findClientsThatAreNotCancelledOrClosedReturningDetailDto(searchId, branchId);

        List<CustomerNoteDto> recentCustomerNotes = this.customerDao.getRecentCustomerNoteDto(groupId);
        List<CustomerPositionDto> customerPositions = this.customerDao.getCustomerPositionDto(groupId, userContext);
        List<CustomerFlagDto> customerFlags = this.customerDao.getCustomerFlagDto(group.getCustomerFlags());
        List<LoanDetailDto> loanDetail = this.customerDao.getLoanDetailDto(group.getOpenLoanAccounts());
        List<SavingsDetailDto> savingsDetail = this.customerDao.getSavingsDetailDto(groupId, userContext);

        CustomerMeetingDto customerMeeting = this.customerDao.getCustomerMeetingDto(group.getCustomerMeeting(), userContext);

        boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.GROUP);

        List<CustomerSurveyDto> customerSurveys = this.customerDao.getCustomerSurveyDto(groupId);

        List<CustomFieldDto> customFields = this.customerDao.getCustomFieldViewForCustomers(groupId, EntityType.GROUP
                .getValue(), userContext);

        return new GroupInformationDto(groupDisplay, customerAccountSummary, groupPerformanceHistory, groupAddress,
                clients, recentCustomerNotes, customerPositions, customerFlags, loanDetail, savingsDetail,
                customerMeeting, activeSurveys, customerSurveys, customFields);
    }

    private GroupPerformanceHistoryDto assembleGroupPerformanceHistoryDto(
            GroupPerformanceHistoryEntity groupPerformanceHistory, String searchId, Short branchId, Integer groupId) {

        Integer activeClientCount = this.customerDao.getActiveAndOnHoldClientCountForGroup(searchId, branchId);

        Money lastGroupLoanAmountMoney = groupPerformanceHistory.getLastGroupLoanAmount();
        String lastGroupLoanAmount = "";
        if (lastGroupLoanAmountMoney != null) {
            lastGroupLoanAmount = lastGroupLoanAmountMoney.toString();
        }

        String avgLoanAmountForMember = this.customerDao.getAvgLoanAmountForMemberInGoodOrBadStanding(searchId, branchId);
        String totalOutStandingLoanAmount = this.customerDao.getTotalOutstandingLoanAmountForGroupAndClientsOfGroups(searchId, branchId);

        String portfolioAtRisk;
        String totalSavingsAmount;

        try {
            if (groupPerformanceHistory.getPortfolioAtRisk() == null) {
                portfolioAtRisk = "0";
            } else {
                portfolioAtRisk = groupPerformanceHistory.getPortfolioAtRisk().toString();
            }
        } catch (CurrencyMismatchException e) {
            portfolioAtRisk = localizedMessageLookup("errors.multipleCurrencies");
        }

        totalSavingsAmount = this.customerDao.getTotalSavingsAmountForGroupandClientsOfGroup(searchId, branchId);

        List<LoanCycleCounter> loanCycleCounters = this.customerDao.fetchLoanCycleCounter(groupId, CustomerLevel.GROUP.getValue());

        return new GroupPerformanceHistoryDto(activeClientCount.toString(), lastGroupLoanAmount,
                avgLoanAmountForMember, totalOutStandingLoanAmount, portfolioAtRisk, totalSavingsAmount,
                loanCycleCounters);
    }

    private String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }
}