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

package org.mifos.customers.persistence;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerFlagDetailEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerPerformanceHistoryDto;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.dto.domain.CenterDisplayDto;
import org.mifos.dto.domain.CenterPerformanceHistoryDto;
import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerFlagDto;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.CustomerPositionOtherDto;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ClientDisplayDto;
import org.mifos.dto.screen.GroupDisplayDto;
import org.mifos.dto.screen.LoanCycleCounter;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.util.UserContext;

/**
 *
 */
public interface CustomerDao {

    void save(CustomerBO customer);

    void save(AccountBO customerAccount);

    void save(CustomerStatusEntity cse);

    void save(CustomerCheckListBO customerCheckListBO);

    void save(AccountCheckListBO accountCheckListBO);

    CustomerBO findCustomerById(Integer customerId);

    CustomerBO findCustomerBySystemId(String globalCustNum);

    ClientBO findClientBySystemId(String globalCustNum);

    GroupBO findGroupBySystemId(String globalCustNum);

    CenterBO findCenterBySystemId(String globalCustNum);

    List<ClientBO> findActiveClientsUnderGroup(CustomerBO customer);

    List<FeeBO> retrieveFeesApplicableToCenters();

    List<FeeBO> retrieveFeesApplicableToGroups();

    List<FeeBO> retrieveFeesApplicableToClients();

    List<FeeBO> retrieveFeesApplicableToGroupsRefinedBy(MeetingBO customerMeeting);

    List<FeeBO> retrieveFeesApplicableToClientsRefinedBy(MeetingBO customerMeetingValue);

    List<CustomerDetailDto> findClientsThatAreNotCancelledOrClosedReturningDetailDto(String searchId, Short branchId);

    List<CustomerDto> findClientsThatAreNotCancelledOrClosed(String parentSearchId, Short parentOfficeId);

    List<CustomerDto> findGroupsThatAreNotCancelledOrClosed(String parentSearchId, Short parentOfficeId);

    QueryResult search(String normalisedSearchString, PersonnelBO user);

    List<ValueListElement> retrieveSalutations();

    List<ValueListElement> retrieveTitles();

    List<ValueListElement> retrieveMaritalStatuses();

    List<ValueListElement> retrieveCitizenship();

    List<ValueListElement> retrieveBusinessActivities();

    List<ValueListElement> retrieveEducationLevels();

    List<ValueListElement> retrieveGenders();

    List<ValueListElement> retrieveHandicapped();

    List<ValueListElement> retrieveEthinicity();

    List<ValueListElement> retrievePoverty();

    List<ValueListElement> retrieveLivingStatus();

    CenterDisplayDto getCenterDisplayDto(Integer centerId, UserContext userContext);

    CustomerAccountSummaryDto getCustomerAccountSummaryDto(Integer centerId);

    CustomerAddressDto getCustomerAddressDto(CustomerBO center);

    List<CustomerDetailDto> getGroupsOtherThanClosedAndCancelledForGroup(String searchId, Short branchId);

    List<CustomerNoteDto> getRecentCustomerNoteDto(Integer centerId);

    List<CustomerPositionOtherDto> getCustomerPositionDto(Integer centerId, UserContext userContext);

    CustomerMeetingDto getCustomerMeetingDto(CustomerMeetingEntity customerMeeting, UserContext userContext);

    CenterPerformanceHistoryDto getCenterPerformanceHistory(String searchId, Short branchId);

    Integer getActiveAndOnHoldClientCountForGroup(String searchId, Short branchId);

    List<CustomerDetailDto> findActiveCentersUnderUser(PersonnelBO personnel);

    List<CustomerDetailDto> findGroupsUnderUser(PersonnelBO personnel);

    CustomerStatusEntity findClientPendingStatus();

    CustomerStatusEntity findGroupPendingStatus();

    int countOfClients();

    int countOfGroups();

    int retrieveLastSearchIdValueForNonParentCustomersInOffice(Short officeIdValue);

    List<FieldConfigurationEntity> findMandatoryConfigurableFieldsApplicableToCenter();

    void updateLoanOfficersForAllChildrenAndAccounts(Short loanOfficerId, String searchId, Short officeId);

    boolean validateGovernmentIdForClient(String governmentId);

    void validateClientForDuplicateNameOrGovtId(String name, Date dob, String governmentId) throws CustomerException;

    void validateGroupNameIsNotTakenForOffice(String displayName, Short officeId) throws CustomerException;

    void validateCenterNameIsNotTakenForOffice(String displayName, Short officeId) throws CustomerException;

    void checkPermissionForStatusChange(Short value, UserContext userContext, Short statusFlagId, Short officeId, Short personnelId) throws CustomerException;

    void checkPermissionForEditMeetingSchedule(UserContext userContext, CustomerBO customer) throws CustomerException;

    boolean validateForClosedClientsOnNameAndDob(final String name, final DateTime dateOfBirth);

    boolean validateForBlackListedClientsOnNameAndDob(String clientName, DateTime dateOfBirth);

    // FIXME - #000003 - keithw - inspect below methods to check are they non customer related methods to be moved out to other DAOs
    List<SavingsDetailDto> getSavingsDetailDto(Integer centerId, UserContext userContext);

    List<SavingsDetailDto> retrieveSavingOfferingsApplicableToClient();

    List<PersonnelDto> findLoanOfficerThatFormedOffice(Short officeId);

    String getAvgLoanAmountForMemberInGoodOrBadStanding(String searchId, Short branchId);

    String getTotalLoanAmountForGroup(String groupSearchId, Short groupOfficeId);

    String getTotalOutstandingLoanAmountForGroupAndClientsOfGroups(String searchId, Short branchId);

    String getTotalSavingsAmountForGroupandClientsOfGroup(String searchId, Short branchId);

    List<LoanCycleCounter> fetchLoanCycleCounter(Integer groupId, Short value);

    GroupDisplayDto getGroupDisplayDto(Integer customerId, UserContext userContext);

    ClientDisplayDto getClientDisplayDto(Integer customerId, UserContext userContext);

    List<CustomerFlagDto> getCustomerFlagDto(Set<CustomerFlagDetailEntity> customerFlags);

    List<LoanDetailDto> getLoanDetailDto(List<LoanBO> openLoanAccounts);

    CustomerPerformanceHistoryDto numberOfMeetings(boolean bool, Integer clientId);

    List<AccountBO> findGLIMLoanAccountsApplicableTo(Integer customerId, Integer customerWithActiveAccount);

    void checkPermissionForDefaultFeeRemoval(UserContext userContext, Short officeId, Short personnelId) throws CustomerException;

    void checkPermissionForDefaultFeeRemovalFromLoan(UserContext userContext, CustomerBO customer) throws CustomerException;

    List<PersonnelLevelEntity> retrievePersonnelLevels();

    /**
     * <code>phoneNumber</code> is stripped to contain numeric characters only
     */
    List<CustomerDto> findCustomersWithGivenPhoneNumber(String phoneNumber);

    List<AccountBO> retrieveAllClosedLoanAndSavingsAccounts(Integer customerId);

    List<CustomerDto> findTopOfHierarchyCustomersUnderLoanOfficer(CustomerLevel customerLevel, Short loanOfficerId,
            Short officeId);

    List<ClientBO> findActiveClientsUnderParent(String searchId, Short branchId);

    List<ClientBO> findActiveClientsWithoutGroupForLoanOfficer(Short loanOfficerId, Short officeId);

    ClientBO findClientById(Integer integer);

	Date getLastMeetingDateForCustomer(Integer customerId);
}
