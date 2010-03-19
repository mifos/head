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

import org.hibernate.Session;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerBusinessService;
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

    private final CustomerBusinessService customerBusinessService;
    private final CustomerDao customerDao;
    
    public WebTierGroupDetailsServiceFacade(CustomerDao customerDao, CustomerBusinessService customerBusinessService) {
        this.customerBusinessService = customerBusinessService;
        this.customerDao = customerDao;
    }

    @Override
    public GroupInformationDto getGroupInformationDto(String globalCustNum, UserContext userContext)
            throws ServiceException {

        runningTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        
        markposition("Very Start");
        GroupBO group = (GroupBO) this.customerBusinessService.findBySystemId(globalCustNum);
        if (group == null) {
            throw new MifosRuntimeException("Group not found for globalCustNum: " + globalCustNum);
        }
        markposition("got group");

        GroupDisplayDto groupDisplay = this.customerBusinessService.getGroupDisplayDto(group.getCustomerId(),
                userContext);
        markposition("got groupDisplay");

        Integer groupId = group.getCustomerId();
        String searchId = group.getSearchId();
        Short branchId = groupDisplay.getBranchId();

       CustomerAccountSummaryDto customerAccountSummary = this.customerDao.getCustomerAccountSummaryDto(
                groupId);
        markposition("got customerAccountSummary");

        GroupPerformanceHistoryDto groupPerformanceHistory = assembleGroupPerformanceHistoryDto(group
                .getGroupPerformanceHistory(), searchId, branchId, groupId);
        markposition("got groupPerformanceHistory");

        CustomerAddressDto groupAddress = this.customerDao.getCustomerAddressDto(group);
        markposition("got groupAddress");

        List<CustomerDetailDto> clients = this.customerBusinessService.getClientsOtherThanClosedAndCancelledForGroup(
                searchId, branchId);
        markposition("got clients");

        List<CustomerNoteDto> recentCustomerNotes = this.customerDao.getRecentCustomerNoteDto(groupId);
        markposition("got recentCustomerNotes");

        List<CustomerPositionDto> customerPositions = this.customerDao.getCustomerPositionDto(groupId, userContext);
        markposition("got customerPositions");

        List<CustomerFlagDto> customerFlags = this.customerBusinessService.getCustomerFlagDto(group.getCustomerFlags());
        markposition("got customerFlags");

        // List<LoanDetailDto> loanDetail = getCustomerBusinessService().getLoanDetailDto(groupId, userContext);
        List<LoanDetailDto> loanDetail = this.customerBusinessService.getLoanDetailDto(group.getOpenLoanAccounts());
        markposition("got loanDetail");

        List<SavingsDetailDto> savingsDetail = this.customerDao.getSavingsDetailDto(groupId, userContext);
        markposition("got savingsDetail");

        CustomerMeetingDto customerMeeting = this.customerDao.getCustomerMeetingDto(group.getCustomerMeeting(), userContext);
        markposition("got customerMeeting");

        Boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.GROUP);
        markposition("got activeSurveys");

        List<CustomerSurveyDto> customerSurveys = this.customerDao.getCustomerSurveyDto(groupId);
        markposition("got customerSurveys");

        List<CustomFieldView> customFields = this.customerDao.getCustomFieldViewForCustomers(groupId,
                EntityType.GROUP.getValue(), userContext);      
        markposition("got customFields");

        System.out.println("group finished: " + (System.currentTimeMillis() - startTime));

        return new GroupInformationDto(groupDisplay, customerAccountSummary, groupPerformanceHistory, groupAddress,
                clients, recentCustomerNotes, customerPositions, customerFlags, loanDetail, savingsDetail,
                customerMeeting, activeSurveys, customerSurveys, customFields);
    }

    private GroupPerformanceHistoryDto assembleGroupPerformanceHistoryDto(
            GroupPerformanceHistoryEntity groupPerformanceHistory, String searchId, Short branchId, Integer groupId)
            throws ServiceException {

        Long thisStartTime = runningTime;
        Integer activeClientCount = this.customerBusinessService.getActiveAndOnHoldClientCountForGroup(searchId,
                branchId);
        System.out.println("activeClientCount finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

        Money lastGroupLoanAmountMoney = groupPerformanceHistory.getLastGroupLoanAmount();
        String lastGroupLoanAmount = "";
        if (lastGroupLoanAmountMoney != null) {
            lastGroupLoanAmount = lastGroupLoanAmountMoney.toString();
        }
        System.out.println("lastGroupLoanAmountMoney finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

        String avgLoanAmountForMember = this.customerBusinessService.getAvgLoanAmountForMemberInGoodOrBadStanding(
                searchId, branchId);
        System.out.println("avgLoanAmountForMember finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

        String totalOutStandingLoanAmount = this.customerBusinessService
                .getTotalOutstandingLoanAmountForGroupAndClientsOfGroups(searchId, branchId);
        System.out.println("totalOutStandingLoanAmount finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

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
        System.out.println("portfolioAtRisk finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

        totalSavingsAmount = this.customerBusinessService.getTotalSavingsAmountForGroupandClientsOfGroup(searchId,
                branchId);
        System.out.println("totalSavingsAmount finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

        List<LoanCycleCounter> loanCycleCounters = this.customerBusinessService.fetchLoanCycleCounter(groupId,
                CustomerLevel.GROUP.getValue());
        System.out.println("fetchLoanCycleCounter finished: " + (System.currentTimeMillis() - thisStartTime));
        thisStartTime = System.currentTimeMillis();

        System.out.println("Total GPH: " + (System.currentTimeMillis() - runningTime));
        return new GroupPerformanceHistoryDto(activeClientCount.toString(), lastGroupLoanAmount,
                avgLoanAmountForMember, totalOutStandingLoanAmount, portfolioAtRisk, totalSavingsAmount,
                loanCycleCounters);
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }

    Long runningTime = null;
    Long startTime = null;

    private void markposition(String string) {

        Session session = new SurveysPersistence().getSession();
        Long timeTaken = (System.currentTimeMillis() - runningTime);
        session.createSQLQuery("select 'A' from customer where 1=0 and display_name = 'Finished: " + string + "'")
                .list();

        System.out.println(string + ": " + timeTaken);
        runningTime = System.currentTimeMillis();
    }
}
