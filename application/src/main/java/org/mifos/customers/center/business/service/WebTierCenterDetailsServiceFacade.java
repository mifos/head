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

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
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
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class WebTierCenterDetailsServiceFacade implements CenterDetailsServiceFacade {

    private final CustomerDao customerDao;

    public WebTierCenterDetailsServiceFacade(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public CenterInformationDto getCenterInformationDto(String globalCustNum, UserContext userContext)
            throws ServiceException {

        CenterBO center = customerDao.findCenterBySystemId(globalCustNum);
        if (center == null) {
            throw new MifosRuntimeException("Center not found for globalCustNum" + globalCustNum);
        }

        CenterDisplayDto centerDisplay = customerDao.getCenterDisplayDto(center.getCustomerId(),
                userContext);

        Integer centerId = center.getCustomerId();
        String searchId = center.getSearchId();
        Short branchId = centerDisplay.getBranchId();

        CustomerAccountSummaryDto customerAccountSummary = customerDao.getCustomerAccountSummaryDto(centerId);

        CenterPerformanceHistoryDto centerPerformanceHistory = customerDao.getCenterPerformanceHistory(searchId, branchId);

        CustomerAddressDto centerAddress = customerDao.getCustomerAddressDto(center);

        List<CustomerDetailDto> groups = customerDao.getGroupsOtherThanClosedAndCancelledForGroup(searchId, branchId);

        List<CustomerNoteDto> recentCustomerNotes = customerDao.getRecentCustomerNoteDto(centerId);

        List<CustomerPositionDto> customerPositions = customerDao.getCustomerPositionDto(centerId, userContext);

        List<SavingsDetailDto> savingsDetail = customerDao.getSavingsDetailDto(centerId, userContext);

        CustomerMeetingDto customerMeeting = customerDao.getCustomerMeetingDto(center.getCustomerMeeting(), userContext);

        Boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.CENTER);

        List<CustomerSurveyDto> customerSurveys = customerDao.getCustomerSurveyDto(centerId);

        List<CustomFieldView> customFields = customerDao.getCustomFieldViewForCustomers(centerId, EntityType.CENTER.getValue(), userContext);

        return new CenterInformationDto(centerDisplay, customerAccountSummary, centerPerformanceHistory, centerAddress,
                groups, recentCustomerNotes, customerPositions, savingsDetail, customerMeeting, activeSurveys,
                customerSurveys, customFields);
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }

}
