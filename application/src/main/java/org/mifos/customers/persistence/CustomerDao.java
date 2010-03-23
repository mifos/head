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

package org.mifos.customers.persistence;

import java.util.List;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelView;
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
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.util.UserContext;

/**
 *
 */
public interface CustomerDao {

    void save(CustomerBO customer);

    void save(CustomerAccountBO customerAccount);

    CustomerBO findCustomerById(Integer customerId);

    ClientBO findClientBySystemId(String globalCustNum);

    GroupBO findGroupBySystemId(String globalCustNum);

    CenterBO findCenterBySystemId(String globalCustNum);

    List<ClientBO> findActiveClientsUnderGroup(CustomerBO customer);

    List<CustomFieldView> retrieveCustomFieldsForCenter(UserContext userContext);

    List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForCenter();

    List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForGroup();

    List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForClient();

    List<FeeBO> retrieveFeesApplicableToCenters();

    List<FeeBO> retrieveFeesApplicableToGroups();

    List<FeeBO> retrieveFeesApplicableToClients();

    List<FeeBO> retrieveFeesApplicableToGroupsRefinedBy(MeetingBO customerMeeting);

    List<FeeBO> retrieveFeesApplicableToClientsRefinedBy(MeetingBO customerMeetingValue);

    List<CustomerView> findClientsThatAreNotCancelledOrClosed(String parentSearchId, Short parentOfficeId);

    List<CustomerView> findGroupsThatAreNotCancelledOrClosed(String parentSearchId, Short parentOfficeId);

    QueryResult search(String normalisedSearchString, PersonnelBO user);

    List<ValueListElement> retrieveSalutations();

    List<ValueListElement> retrieveMaritalStatuses();

    List<ValueListElement> retrieveCitizenship();

    List<ValueListElement> retrieveBusinessActivities();

    List<ValueListElement> retrieveEducationLevels();

    List<ValueListElement> retrieveGenders();

    List<ValueListElement> retrieveHandicapped();

    List<ValueListElement> retrieveEthinicity();

    List<ValueListElement> retrievePoverty();

    CenterDisplayDto getCenterDisplayDto(Integer centerId, UserContext userContext);

    CustomerAccountSummaryDto getCustomerAccountSummaryDto(Integer centerId);

    CustomerAddressDto getCustomerAddressDto(CustomerBO center);

    List<CustomerDetailDto> getGroupsOtherThanClosedAndCancelledForGroup(String searchId, Short branchId);

    List<CustomerNoteDto> getRecentCustomerNoteDto(Integer centerId);

    List<CustomerPositionDto> getCustomerPositionDto(Integer centerId, UserContext userContext);

    CustomerMeetingDto getCustomerMeetingDto(CustomerMeetingEntity customerMeeting, UserContext userContext);

    List<CustomerSurveyDto> getCustomerSurveyDto(Integer centerId);

    List<CustomFieldView> getCustomFieldViewForCustomers(Integer centerId, Short value, UserContext userContext);

    CenterPerformanceHistoryDto getCenterPerformanceHistory(String searchId, Short branchId);

    // FIXME - #000003 - keithw - below are non customer related methods to be moved out
    void validateGroupNameIsNotTakenForOffice(String displayName, Short officeId) throws CustomerException;

    List<SavingsDetailDto> getSavingsDetailDto(Integer centerId, UserContext userContext);

    List<SavingsDetailDto> retrieveSavingOfferingsApplicableToClient();

    List<PersonnelView> findLoanOfficerThatFormedOffice(Short officeId);

}