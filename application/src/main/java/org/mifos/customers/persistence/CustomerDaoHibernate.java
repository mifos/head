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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerDto;
import org.mifos.customers.business.CustomerFlagDetailEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerPerformanceHistoryDto;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.struts.uihelpers.CustomerUIHelperFn;
import org.mifos.customers.util.helpers.CenterDisplayDto;
import org.mifos.customers.util.helpers.CenterPerformanceHistoryDto;
import org.mifos.customers.util.helpers.ClientDisplayDto;
import org.mifos.customers.util.helpers.ClientFamilyDetailDto;
import org.mifos.customers.util.helpers.CustomerAccountSummaryDto;
import org.mifos.customers.util.helpers.CustomerAddressDto;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerFlagDto;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerMeetingDto;
import org.mifos.customers.util.helpers.CustomerNoteDto;
import org.mifos.customers.util.helpers.CustomerPositionDto;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerSurveyDto;
import org.mifos.customers.util.helpers.GroupDisplayDto;
import org.mifos.customers.util.helpers.LoanCycleCounter;
import org.mifos.customers.util.helpers.LoanDetailDto;
import org.mifos.customers.util.helpers.Param;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class CustomerDaoHibernate implements CustomerDao {

    private final GenericDao genericDao;

    public CustomerDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public CustomerBO findCustomerById(final Integer customerId) {

        if (customerId == null) {
            throw new IllegalArgumentException("customerId cannot be null");
        }

        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);

        return (CustomerBO) genericDao.executeUniqueResultNamedQuery("customer.findById", queryParameters);
    }

    @Override
    public CenterBO findCenterBySystemId(String globalCustNum) {
        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalCustNum", globalCustNum);

        return (CenterBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_CENTER_BY_SYSTEMID,
                queryParameters);
    }

    @Override
    public GroupBO findGroupBySystemId(String globalCustNum) {

        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalCustNum", globalCustNum);

        return (GroupBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_GROUP_BY_SYSTEMID,
                queryParameters);
    }

    @Override
    public ClientBO findClientBySystemId(String globalCustNum) {
        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalCustNum", globalCustNum);

        return (ClientBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_CLIENT_BY_SYSTEMID,
                queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClientBO> findActiveClientsUnderGroup(final CustomerBO customer) {

        if (customer == null) {
            throw new IllegalArgumentException("customer cannot be null");
        }

        if (!customer.isGroup()) {
            throw new IllegalArgumentException("customer must be a group");
        }

        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("GROUP_ID", customer.getCustomerId());
        return (List<ClientBO>) genericDao.executeNamedQuery(NamedQueryConstants.ACTIVE_CLIENTS_UNDER_GROUP,
                queryParameters);
    }

    @Override
    public List<CustomFieldDto> retrieveCustomFieldsForCenter(UserContext userContext) {
        List<CustomFieldDefinitionEntity> customFieldsForCenter = retrieveCustomFieldEntitiesForCenter();

        return CustomFieldDefinitionEntity.toDto(customFieldsForCenter, userContext.getPreferredLocale());
    }

    @Override
    public final List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForCenter() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, EntityType.CENTER.getValue());

        return retrieveCustomFieldDefinitions(queryParameters);
    }

    @Override
    public List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForGroup() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, EntityType.GROUP.getValue());

        return retrieveCustomFieldDefinitions(queryParameters);

    }

    @Override
    public final List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForClient() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, EntityType.CLIENT.getValue());

        return retrieveCustomFieldDefinitions(queryParameters);
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldDefinitionEntity> retrieveCustomFieldDefinitions(Map<String, Object> queryParameters) {
        List<CustomFieldDefinitionEntity> customFieldsForCenter = (List<CustomFieldDefinitionEntity>) genericDao
                .executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
        return customFieldsForCenter;
    }

    @Override
    public List<FeeBO> retrieveFeesApplicableToCenters() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FeeCategory.ALLCUSTOMERS.toString(), FeeCategory.ALLCUSTOMERS.getValue());
        queryParameters.put("CUSTOMER_CATEGAORY", FeeCategory.CENTER.getValue());
        return retrieveFeesApplicableTo(queryParameters);
    }

    @Override
    public List<FeeBO> retrieveFeesApplicableToGroups() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FeeCategory.ALLCUSTOMERS.toString(), FeeCategory.ALLCUSTOMERS.getValue());
        queryParameters.put("CUSTOMER_CATEGAORY", FeeCategory.GROUP.getValue());
        return retrieveFeesApplicableTo(queryParameters);
    }

    @Override
    public List<FeeBO> retrieveFeesApplicableToClients() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FeeCategory.ALLCUSTOMERS.toString(), FeeCategory.ALLCUSTOMERS.getValue());
        queryParameters.put("CUSTOMER_CATEGAORY", FeeCategory.CLIENT.getValue());
        return retrieveFeesApplicableTo(queryParameters);
    }

    @Override
    public List<FeeBO> retrieveFeesApplicableToGroupsRefinedBy(MeetingBO meeting) {
        return refineFeesBy(retrieveFeesApplicableToGroups(), meeting);
    }

    @Override
    public List<FeeBO> retrieveFeesApplicableToClientsRefinedBy(MeetingBO meeting) {
        return refineFeesBy(retrieveFeesApplicableToClients(), meeting);
    }

    @SuppressWarnings("unchecked")
    private List<FeeBO> retrieveFeesApplicableTo(HashMap<String, Object> queryParameters) {
        return (List<FeeBO>) genericDao.executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOMER_FEES_BY_CATEGORY_TYPE,
                queryParameters);
    }

    private List<FeeBO> refineFeesBy(List<FeeBO> feeList, MeetingBO meeting) {
        List<FeeBO> fees = new ArrayList<FeeBO>();
        for (FeeBO fee : feeList) {
            if (fee.isOneTime() || (fee.isPeriodic() && isFrequencyMatches(fee, meeting))) {
                fees.add(fee);
            }
        }
        return fees;
    }

    private boolean isFrequencyMatches(FeeBO fee, MeetingBO meeting) {
        return (fee.isMonthly() && meeting.isMonthly()) || (fee.isWeekly() && meeting.isWeekly());
    }

    @Override
    public void save(CustomerBO customer) {
        this.genericDao.createOrUpdate(customer);
    }

    @Override
    public void save(CustomerAccountBO customerAccount) {
        this.genericDao.createOrUpdate(customerAccount);
    }

    @Override
    public void save(CustomerStatusEntity customerStatusEntity) {
        this.genericDao.createOrUpdate(customerStatusEntity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> findClientsThatAreNotCancelledOrClosedReturningDetailDto(String searchId, Short branchId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", branchId);
        List<CustomerDetailDto> clients = (List<CustomerDetailDto>) genericDao.executeNamedQuery("Customer.getListOfClientsUnderGroupOtherThanClosedAndCancelled", queryParameters);

        // bug #1417 - wrong client sort order. Client sort order on bulk
        // entry screens should match ordering on group details page.
        Collections.sort(clients, CustomerDetailDto.searchIdComparator());
        return clients;
    }

    @Override
    public List<CustomerDto> findClientsThatAreNotCancelledOrClosed(String parentSearchId, Short parentOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", CustomerLevel.CLIENT.getValue());

        return findCustomersThatAreNotClosedOrCanceled(queryParameters);
    }

    @Override
    public List<CustomerDto> findGroupsThatAreNotCancelledOrClosed(String parentSearchId, Short parentOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", CustomerLevel.GROUP.getValue());

        return findCustomersThatAreNotClosedOrCanceled(queryParameters);
    }

    @SuppressWarnings("unchecked")
    private List<CustomerDto> findCustomersThatAreNotClosedOrCanceled(Map<String, Object> queryParameters) {
        List<CustomerBO> queryResult = (List<CustomerBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED, queryParameters);

        List<CustomerDto> customerDtos = new ArrayList<CustomerDto>();
        for (CustomerBO customerBO : queryResult) {
            CustomerDto customerDto = new CustomerDto(customerBO.getCustomerId(), customerBO.getDisplayName(),
                    customerBO.getCustomerLevel().getId(), customerBO.getSearchId());

            customerDtos.add(customerDto);
        }

        return customerDtos;
    }

    @Override
    public QueryResult search(String searchString, PersonnelBO user) {
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();

        QueryInputs queryInputs = new QueryInputs();
        QueryResult queryResult = QueryFactory.getQueryResult(PersonnelConstants.USER_LIST);

        String officeSearchId = user.getOffice().getSearchId();

        namedQuery[0] = NamedQueryConstants.CENTER_SEARCH_COUNT;
        namedQuery[1] = NamedQueryConstants.CENTER_SEARCH;

        paramList.add(typeNameValue("String", "SEARCH_ID", officeSearchId + "%"));
        paramList.add(typeNameValue("String", "CENTER_NAME", searchString + "%"));
        paramList.add(typeNameValue("Short", "LEVEL_ID", CustomerLevel.CENTER.getValue()));
        paramList.add(typeNameValue("Short", "STATUS_ID", CustomerStatus.CENTER_ACTIVE.getValue()));
        paramList.add(typeNameValue("Short", "USER_ID", user.getPersonnelId()));
        paramList.add(typeNameValue("Short", "USER_LEVEL_ID", user.getLevelEnum().getValue()));
        paramList.add(typeNameValue("Short", "LO_LEVEL_ID", PersonnelConstants.LOAN_OFFICER));

        String[] aliasNames = { "parentOfficeId", "parentOfficeName", "centerSystemId", "centerName" };

        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setPath("org.mifos.customers.center.util.helpers.CenterSearchResultsDto");
        queryInputs.setAliasNames(aliasNames);
        queryInputs.setParamList(paramList);
        try {
            queryResult.setQueryInputs(queryInputs);
        } catch (HibernateSearchException e) {
            throw new MifosRuntimeException(e);
        }
        return queryResult;
    }

    private Param typeNameValue(final String type, final String name, final Object value) {
        return new Param(type, name, value);
    }

    @Override
    public List<ValueListElement> retrieveSalutations() {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.SALUTATION);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveMaritalStatuses() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.MARITAL_STATUS);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveBusinessActivities() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.BUSINESS_ACTIVITIES);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveCitizenship() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.CITIZENSHIP);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveEducationLevels() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.EDUCATION_LEVEL);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveEthinicity() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.ETHINICITY);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveGenders() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.GENDER);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveHandicapped() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.HANDICAPPED);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrievePoverty() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.POVERTY_STATUS);

        return retrieveMasterData(queryParameters);
    }

    @Override
    public List<ValueListElement> retrieveLivingStatus() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.LIVING_STATUS);

        return retrieveMasterData(queryParameters);
    }

    @SuppressWarnings("unchecked")
    private List<ValueListElement> retrieveMasterData(Map<String, Object> queryParameters) {
        List<ValueListElement> salutations = (List<ValueListElement>) this.genericDao.executeNamedQuery(
                NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE, queryParameters);
        return salutations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CenterDisplayDto getCenterDisplayDto(Integer centerId, UserContext userContext) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CENTER_ID", centerId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery("getCenterDisplayDto",
                queryParameters);

        if (queryResult.size() == 0) {
            throw new MifosRuntimeException("Center not found: " + centerId);
        }
        if (queryResult.size() > 1) {
            throw new MifosRuntimeException("Error finding Center id: " + centerId + " - Number found: "
                    + queryResult.size());
        }

        final Integer customerId = (Integer) queryResult.get(0)[0];
        final String globalCustNum = (String) queryResult.get(0)[1];
        final String displayName = (String) queryResult.get(0)[2];
        final Short branchId = (Short) queryResult.get(0)[3];
        final Date mfiJoiningDate = (Date) queryResult.get(0)[4];
        final Date createdDate = (Date) queryResult.get(0)[5];
        final Integer versionNo = (Integer) queryResult.get(0)[6];
        final String externalId = (String) queryResult.get(0)[7];
        final Short customerLevelId = (Short) queryResult.get(0)[8];
        final Short customerStatusId = (Short) queryResult.get(0)[9];
        final String lookupName = (String) queryResult.get(0)[10];
        final Short loanOfficerId = (Short) queryResult.get(0)[11];
        final String loanOfficerName = (String) queryResult.get(0)[12];
        final String customerStatusName = MessageLookup.getInstance().lookup(lookupName, userContext);

        return new CenterDisplayDto(customerId, globalCustNum, displayName, branchId, mfiJoiningDate, createdDate,
                versionNo, externalId, customerLevelId, customerStatusId, customerStatusName, loanOfficerId,
                loanOfficerName);
    }

    @SuppressWarnings("unchecked")
    public CustomerAccountSummaryDto getCustomerAccountSummaryDto(Integer customerId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        queryParameters.put("TODAY", new LocalDate().toString());

        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery("getCustomerAccountSummaryDto",
                queryParameters);

        if (queryResult.size() > 1) {
            throw new MifosRuntimeException("More than one row returned");
        }
        if (queryResult.size() == 0) {
            return null;
        }

        final String globalAccountNum = (String) queryResult.get(0)[0];
        // FIXME - johnw - currency
        final Short currency = (Short) queryResult.get(0)[1];
        final BigDecimal totalChargesDue = (BigDecimal) queryResult.get(0)[2];
        BigDecimal totalFeesDue = (BigDecimal) queryResult.get(0)[3];
        if (totalFeesDue == null) {
            totalFeesDue = BigDecimal.ZERO;
        }

        MifosCurrency mifosCurrency = Money.getDefaultCurrency();
        final Money totalAmountDue = new Money(mifosCurrency, (totalChargesDue.add(totalFeesDue)));

        return new CustomerAccountSummaryDto(globalAccountNum, totalAmountDue);
    }

    @Override
    public CustomerAddressDto getCustomerAddressDto(CustomerBO customer) {
        if (customer.getDisplayAddress() == null && customer.getAddress() == null) {
            return null;
        }

        String city = null;
        String state = null;
        String country = null;
        String zip = null;
        String phoneNumber = null;
        if (customer.getAddress() != null) {
            city = customer.getAddress().getCity();
            state = customer.getAddress().getState();
            country = customer.getAddress().getCountry();
            zip = customer.getAddress().getZip();
            phoneNumber = customer.getAddress().getPhoneNumber();
        }
        return new CustomerAddressDto(customer.getDisplayAddress(), city, state, zip, country, phoneNumber);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> getGroupsOtherThanClosedAndCancelledForGroup(String searchId, Short branchId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", branchId);
        List<CustomerDetailDto> groups = (List<CustomerDetailDto>) this.genericDao.executeNamedQuery(
                "Customer.getListOfGroupsUnderCenterOtherThanClosedAndCancelled", queryParameters);

        Collections.sort(groups, CustomerDetailDto.searchIdComparator());
        return groups;
    }

    @Override
    public List<CustomerNoteDto> getRecentCustomerNoteDto(Integer customerId) {
        Integer recent = 3;

        List<CustomerNoteDto> customerNotes = getCustomerNoteDto(customerId);
        if (customerNotes == null) {
            return null;
        }
        if (customerNotes.size() < (recent + 1)) {
            return customerNotes;
        }

        List<CustomerNoteDto> recentCustomerNotes = new ArrayList<CustomerNoteDto>(recent);
        for (int i = 0; i < recent; i++) {
            recentCustomerNotes.add(customerNotes.get(i));
        }
        return recentCustomerNotes;
    }

    @SuppressWarnings("unchecked")
    private List<CustomerNoteDto> getCustomerNoteDto(Integer customerId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery("Customer.getCustomerNoteDto",
                queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<CustomerNoteDto> customerNotes = new ArrayList<CustomerNoteDto>();
        Date commentDate;
        String comment;
        String personnelName;

        for (Object[] customerNote : queryResult) {
            commentDate = (Date) customerNote[0];
            comment = (String) customerNote[1];
            personnelName = (String) customerNote[2];

            customerNotes.add(new CustomerNoteDto(commentDate, comment, personnelName));
        }
        return customerNotes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerPositionDto> getCustomerPositionDto(final Integer parentId, final UserContext userContext) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("PARENT_ID", parentId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                "Customer.getCustomerPositionDto", queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();
        String lookupName;
        Integer customerId;
        String customerDisplayName;
        String positionName;

        for (Object[] customerPosition : queryResult) {
            lookupName = (String) customerPosition[0];
            customerId = (Integer) customerPosition[1];
            customerDisplayName = (String) customerPosition[2];
            positionName = MessageLookup.getInstance().lookup(lookupName, userContext);

            customerPositions.add(new CustomerPositionDto(positionName, customerId, customerDisplayName));
        }
        return customerPositions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SavingsDetailDto> getSavingsDetailDto(Integer customerId, UserContext userContext) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery("Customer.getSavingsDetailDto",
                queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<SavingsDetailDto> savingsDetails = new ArrayList<SavingsDetailDto>();
        String globalAccountNum;
        String prdOfferingName;
        Short accountStateId;
        String accountStateName;
        Money savingsBalance;
        String lookupName;
        Short currency;

        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        for (Object[] savingsDetail : queryResult) {
            globalAccountNum = (String) savingsDetail[0];
            prdOfferingName = (String) savingsDetail[1];
            accountStateId = (Short) savingsDetail[2];
            lookupName = (String) savingsDetail[3];
            accountStateName = MessageLookup.getInstance().lookup(lookupName, userContext);
            // TODO - use default currency or retrieved currency?
            currency = (Short) savingsDetail[4];
            savingsBalance = new Money(mifosCurrency, (BigDecimal) savingsDetail[5]);

            savingsDetails.add(new SavingsDetailDto(globalAccountNum, prdOfferingName, accountStateId,
                    accountStateName, savingsBalance));
        }
        return savingsDetails;
    }

    @Override
    public CustomerMeetingDto getCustomerMeetingDto(CustomerMeetingEntity customerMeeting, UserContext userContext) {

        if (customerMeeting != null) {
            String meetingSchedule = CustomerUIHelperFn.getMeetingSchedule(customerMeeting.getMeeting(), userContext);

            String updatedMeetingScheduleMessage = CustomerUIHelperFn.getUpdatedMeetingSchedule(customerMeeting,
                    userContext);
            String meetingPlace = customerMeeting.getMeeting().getMeetingPlace();

            return new CustomerMeetingDto(meetingSchedule, updatedMeetingScheduleMessage, meetingPlace);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerSurveyDto> getCustomerSurveyDto(final Integer customerId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                "Customer.getCustomerSurveyDto", queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<CustomerSurveyDto> customerSurveys = new ArrayList<CustomerSurveyDto>();
        Integer instanceId;
        String surveyName;
        Date dateConducted;

        for (Object[] customerSurvey : queryResult) {
            instanceId = (Integer) customerSurvey[0];
            surveyName = (String) customerSurvey[1];
            dateConducted = (Date) customerSurvey[2];

            customerSurveys.add(new CustomerSurveyDto(instanceId, surveyName, dateConducted));
        }
        return customerSurveys;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomFieldDto> getCustomFieldViewForCustomers(Integer customerId, Short entityTypeId,
            UserContext userContext) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        queryParameters.put("ENTITY_TYPE", entityTypeId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                "Customer.getCustomFieldViewForCustomers", queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        Short fieldId;
        String fieldValue;
        Short fieldType;
        Short mandatoryFlag;
        String entityName;

        boolean mandatory = false;
        String mandatoryString;

        for (Object[] customField : queryResult) {
            fieldId = (Short) customField[0];
            fieldValue = (String) customField[1];
            fieldType = (Short) customField[2];
            mandatoryFlag = (Short) customField[3];
            entityName = (String) customField[4];

            if (mandatoryFlag > 0) {
                mandatory = true;
            }

            mandatoryString = MessageLookup.getInstance().lookup(YesNoFlag.fromInt(mandatoryFlag),
                    userContext.getCurrentLocale());

            CustomFieldDto customFieldDto = new CustomFieldDto(fieldId, fieldValue, fieldType);
            customFieldDto.setMandatory(mandatory);
            customFieldDto.setLookUpEntityType(entityName);
            customFieldDto.setMandatoryString(mandatoryString);

            customFields.add(customFieldDto);
        }
        return customFields;
    }

    /**
     * FIXME: THIS METHOD DOES NOT WORK. Specifically, the portfolioAtRisk calculation. Please see issue 2204.
     */
    @Override
    public CenterPerformanceHistoryDto getCenterPerformanceHistory(String searchId, Short officeId) {
        Integer activeAndOnHoldGroupCount;
        Integer activeAndOnHoldClientCount;
        String totalSavings;
        String totalLoan;

        activeAndOnHoldGroupCount = getActiveAndOnHoldChildrenCount(searchId, officeId, CustomerLevel.GROUP);
        activeAndOnHoldClientCount = getActiveAndOnHoldChildrenCount(searchId, officeId, CustomerLevel.CLIENT);

        try {
            totalSavings = retrieveTotalSavings(searchId, officeId).toString();
        } catch (CurrencyMismatchException e) {
            totalSavings = localizedMessageLookup("errors.multipleCurrencies");
        }

        try {
            totalLoan = retrieveTotalLoan(searchId, officeId).toString();
        } catch (CurrencyMismatchException e) {
            totalLoan = localizedMessageLookup("errors.multipleCurrencies");
        }

        String portfolioAtRisk = "0.2";
        return new CenterPerformanceHistoryDto(activeAndOnHoldGroupCount, activeAndOnHoldClientCount, totalLoan,
                totalSavings, portfolioAtRisk);
    }

    @Override
    public Integer getActiveAndOnHoldClientCountForGroup(final String searchId, final Short branchId) {
        return getActiveAndOnHoldChildrenCount(searchId, branchId, CustomerLevel.CLIENT);
    }

    private Integer getActiveAndOnHoldChildrenCount(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());

        Long count = (Long) this.genericDao.executeUniqueResultNamedQuery(
                NamedQueryConstants.GET_ACTIVE_AND_ONHOLD_CHILDREN_COUNT, queryParameters);

        return count.intValue();
    }

    private Money retrieveTotalLoan(final String searchId, final Short officeId) {
        return retrieveTotalForQuery(NamedQueryConstants.RETRIEVE_TOTAL_LOAN_FOR_CUSTOMER, searchId, officeId);
    }

    private Money retrieveTotalSavings(final String searchId, final Short officeId) {
        return retrieveTotalForQuery(NamedQueryConstants.RETRIEVE_TOTAL_SAVINGS_FOR_CUSTOMER, searchId, officeId);
    }

    @SuppressWarnings("unchecked")
    private Money retrieveTotalForQuery(String query, final String searchId, final Short officeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING1", searchId);
        queryParameters.put("SEARCH_STRING2", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List queryResult = this.genericDao.executeNamedQuery(query, queryParameters);

        if (queryResult.size() > 1) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
        if (queryResult.size() == 0) {
            // if we found no results, then return zero using the default currency
            return new Money(Money.getDefaultCurrency(), "0.0");
        }
        Integer currencyId = (Integer) ((Object[]) queryResult.get(0))[0];
        MifosCurrency currency = AccountingRules.getCurrencyByCurrencyId(currencyId.shortValue());

        BigDecimal total = (BigDecimal) ((Object[]) queryResult.get(0))[1];

        return new Money(currency, total);
    }

    private String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonnelDto> findLoanOfficerThatFormedOffice(Short officeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", ClientConstants.LOAN_OFFICER_LEVEL);
        queryParameters.put("officeId", officeId);
        queryParameters.put("statusId", PersonnelConstants.ACTIVE);
        List<PersonnelDto> queryResult = (List<PersonnelDto>) this.genericDao.executeNamedQuery(
                NamedQueryConstants.FORMEDBY_LOANOFFICERS_LIST, queryParameters);
        return queryResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateGroupNameIsNotTakenForOffice(String displayName, Short officeId) throws CustomerException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerConstants.DISPLAY_NAME, displayName);
        queryParameters.put(CustomerConstants.OFFICE_ID, officeId);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.GET_GROUP_COUNT_BY_NAME,
                queryParameters);

        if (Integer.valueOf(queryResult.get(0).toString()) > 0) {
            throw new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SavingsDetailDto> retrieveSavingOfferingsApplicableToClient() {

        List<SavingsDetailDto> savingDetails = new ArrayList<SavingsDetailDto>();

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdApplicableTo", ApplicableTo.CLIENTS.getValue());

        List<SavingsOfferingBO> savingOfferings = (List<SavingsOfferingBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_ACTIVE_OFFERINGS_FOR_CUSTOMER, queryParameters);
        for (SavingsOfferingBO savingsOffering : savingOfferings) {

            SavingsDetailDto savingsDetailsWithOnlyPrdOfferingName = SavingsDetailDto.create(savingsOffering.getPrdOfferingId(), savingsOffering.getPrdOfferingName());
            savingDetails.add(savingsDetailsWithOnlyPrdOfferingName);
        }

        return savingDetails;
    }

    @Override
    public boolean validateGovernmentIdForClient(String governmentId, String clientName, DateTime dateOfBirth) {

        boolean result = doesClientExistInAnyStateButClosedWithSameGovernmentId(governmentId);
        if (!result) {
            result = checkForDuplicacyForClosedClientsOnNameAndDob(clientName, dateOfBirth);
        }
        return result;
    }

    private boolean doesClientExistInAnyStateButClosedWithSameGovernmentId(final String governmentId) {
        final Integer customerIdRepresentingClosedClient = Integer.valueOf(0);
        return checkForClientsBasedOnGovtId(NamedQueryConstants.GET_CLOSED_CLIENT_BASEDON_GOVTID, governmentId, customerIdRepresentingClosedClient);
    }

    @SuppressWarnings("unchecked")
    private boolean checkForClientsBasedOnGovtId(final String queryName, final String governmentId, final Integer customerId) {

        if (StringUtils.isBlank(governmentId)) {
            return false;
        }

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", CustomerLevel.CLIENT.getValue());
        queryParameters.put("GOVT_ID", governmentId);
        queryParameters.put("customerId", customerId);
        queryParameters.put("clientStatus", CustomerStatus.CLIENT_CLOSED.getValue());
        List queryResult = this.genericDao.executeNamedQuery(queryName, queryParameters);
        return ((Long) queryResult.get(0)).intValue() > 0;
    }

    private boolean checkForDuplicacyForClosedClientsOnNameAndDob(final String name, final DateTime dateOfBirth) {
        return checkForDuplicacyBasedOnName(NamedQueryConstants.GET_CLOSED_CLIENT_BASED_ON_NAME_DOB, name, dateOfBirth, Integer
                .valueOf(0));
    }

    @SuppressWarnings("unchecked")
    private boolean checkForDuplicacyBasedOnName(final String queryName, final String name, final DateTime dateOfBirth, final Integer customerId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clientName", name);
        queryParameters.put("LEVELID", CustomerLevel.CLIENT.getValue());
        queryParameters.put("DATE_OFBIRTH", dateOfBirth.toDate());
        queryParameters.put("customerId", customerId);
        queryParameters.put("clientStatus", CustomerStatus.CLIENT_CLOSED.getValue());
        List queryResult = this.genericDao.executeNamedQuery(queryName, queryParameters);
        return ((Number) queryResult.get(0)).intValue() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> findActiveCentersUnderUser(PersonnelBO personnel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.CENTER.getValue());
        queryParameters.put(CustomerSearchConstants.CENTER_ACTIVE, CustomerStatus.CENTER_ACTIVE.getValue());

        return (List<CustomerDetailDto>) this.genericDao.executeNamedQueryWithResultTransformer("Customer.get_loanofficer_list_of_active_centers", queryParameters, CustomerDetailDto.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> findGroupsUnderUser(PersonnelBO personnel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.GROUP.getValue());
        return (List<CustomerDetailDto>) this.genericDao.executeNamedQueryWithResultTransformer("Customer.get_loanofficer_list_of_groups", queryParameters, CustomerDetailDto.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAvgLoanAmountForMemberInGoodOrBadStanding(String groupSearchId, Short groupOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", groupSearchId + ".%");
        queryParameters.put("OFFICE_ID", groupOfficeId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery("Customer.getAvgLoanAmountForMemberInGoodOrBadStanding", queryParameters);

        if (queryResult.size() > 1) {
            return localizedMessageLookup("errors.multipleCurrencies");
        }
        if (queryResult.size() == 0) {
            return new Money(Money.getDefaultCurrency()).toString();
        }

        // TODO - use default currency or retrieved currency?
        Short currency = (Short) queryResult.get(0)[0];
        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        Integer numberOfLoansInGoodOrBadStanding = (Integer) queryResult.get(0)[1];
        Money totalLoanAmountInGoodOrBadStanding = new Money(mifosCurrency, (BigDecimal) queryResult.get(0)[2]);
        Money avgLoanAmountInGoodOrBadStanding = new Money(mifosCurrency);
        if (numberOfLoansInGoodOrBadStanding.intValue() > 0) {
            avgLoanAmountInGoodOrBadStanding = totalLoanAmountInGoodOrBadStanding
                    .divide(numberOfLoansInGoodOrBadStanding);
        }

        return avgLoanAmountInGoodOrBadStanding.toString();
    }

    @Override
    public String getTotalOutstandingLoanAmountForGroupAndClientsOfGroups(String groupSearchId, Short groupOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", groupSearchId);
        queryParameters.put("SEARCH_STRING2", groupSearchId + ".%");
        queryParameters.put("OFFICE_ID", groupOfficeId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery("Customer.getTotalOutstandingLoanAmountForGroupAndClientsOfGroups", queryParameters);

        if (queryResult.size() > 1) {
            return localizedMessageLookup("errors.multipleCurrencies");
        }
        if (queryResult.size() == 0) {
            return new Money(Money.getDefaultCurrency()).toString();
        }

        // TODO - use default currency or retrieved currency?
        Short currency = (Short) queryResult.get(0)[0];
        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        Money totalOutstandingLoanAmount = new Money(mifosCurrency, (BigDecimal) queryResult.get(0)[1]);
        return totalOutstandingLoanAmount.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getTotalSavingsAmountForGroupandClientsOfGroup(String groupSearchId, Short groupOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", groupSearchId);
        queryParameters.put("SEARCH_STRING2", groupSearchId + ".%");
        queryParameters.put("OFFICE_ID", groupOfficeId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery("Customer.getTotalSavingsAmountForGroupandClientsOfGroup", queryParameters);

        if (queryResult.size() > 1) {
            return localizedMessageLookup("errors.multipleCurrencies");
        }
        if (queryResult.size() == 0) {
            return new Money(Money.getDefaultCurrency()).toString();
        }

        // TODO - use default currency or retrieved currency?
        Short currency = (Short) queryResult.get(0)[0];
        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        Money totalSavingsAmount = new Money(mifosCurrency, (BigDecimal) queryResult.get(0)[1]);
        return totalSavingsAmount.toString();
    }

    @Override
    public List<LoanCycleCounter> fetchLoanCycleCounter(Integer customerId, Short customerLevelId) {

        List<LoanCycleCounter> loanCycles = new ArrayList<LoanCycleCounter>();

        if (CustomerLevel.GROUP.getValue().equals(customerLevelId)) {
            loanCycles = runLoanCycleQuery(NamedQueryConstants.FETCH_PRODUCT_NAMES_FOR_GROUP, customerId);
        } else if (CustomerLevel.CLIENT.getValue().equals(customerLevelId)) {
            loanCycles = runLoanCycleQuery(NamedQueryConstants.FETCH_PRODUCT_NAMES_FOR_CLIENT, customerId);
        }
        return loanCycles;
    }

    @SuppressWarnings("unchecked")
    private List<LoanCycleCounter> runLoanCycleQuery(final String queryName, final Integer customerId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        List<LoanCycleCounter> loanCycleCounters = new ArrayList<LoanCycleCounter>();
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery(queryName, queryParameters);
        if (null != queryResult && queryResult.size() > 0) {
            for (Object[] objects : queryResult) {
                loanCycleCounters.add(new LoanCycleCounter((String) objects[0], (Integer) objects[1]));
            }
        }
        return loanCycleCounters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GroupDisplayDto getGroupDisplayDto(Integer groupId, UserContext userContext) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("GROUP_ID", groupId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery("getGroupDisplayDto", queryParameters);

        if (queryResult.size() == 0) {
            throw new MifosRuntimeException("Group not found: " + groupId);
        }
        if (queryResult.size() > 1) {
            throw new MifosRuntimeException("Error finding Group id: " + groupId + " - Number found: "
                    + queryResult.size());
        }

        final Integer customerId = (Integer) queryResult.get(0)[0];
        final String globalCustNum = (String) queryResult.get(0)[1];
        final String displayName = (String) queryResult.get(0)[2];
        final String parentCustomerDisplayName = (String) queryResult.get(0)[3];
        final Short branchId = (Short) queryResult.get(0)[4];
        final String externalId = (String) queryResult.get(0)[5];
        final String customerFormedByDisplayName = (String) queryResult.get(0)[6];
        final Date customerActivationDate = (Date) queryResult.get(0)[7];
        final Short customerLevelId = (Short) queryResult.get(0)[8];
        final Short customerStatusId = (Short) queryResult.get(0)[9];
        final String lookupName = (String) queryResult.get(0)[10];
        final Boolean trained = (Boolean) queryResult.get(0)[11];
        final Date trainedDate = (Date) queryResult.get(0)[12];
        final Boolean blackListed = (Boolean) queryResult.get(0)[13];
        final Short loanOfficerId = (Short) queryResult.get(0)[14];
        final String loanOfficerName = (String) queryResult.get(0)[15];
        final String customerStatusName = MessageLookup.getInstance().lookup(lookupName, userContext);

        return new GroupDisplayDto(customerId, globalCustNum, displayName, parentCustomerDisplayName, branchId,
                externalId, customerFormedByDisplayName, customerActivationDate, customerLevelId, customerStatusId,
                customerStatusName, trained, trainedDate, blackListed, loanOfficerId, loanOfficerName);
    }

    @Override
    public List<CustomerFlagDto> getCustomerFlagDto(Set<CustomerFlagDetailEntity> customerFlagDetails) {
        if (customerFlagDetails != null) {
            List<CustomerFlagDto> customerFlags = new ArrayList<CustomerFlagDto>();
            for (CustomerFlagDetailEntity customerFlag : customerFlagDetails) {
                customerFlags.add(new CustomerFlagDto(customerFlag.getStatusFlag().getName()));
            }
            return customerFlags;
        }
        // FIXME - can i just return empty list instead of null?
        return null;
    }

    @Override
    public List<LoanDetailDto> getLoanDetailDto(List<LoanBO> loanAccounts) {
        // refactor this and correct statusName
        if (loanAccounts != null) {
            List<LoanDetailDto> loanDetail = new ArrayList<LoanDetailDto>();
            for (LoanBO loan : loanAccounts) {
                loanDetail.add(new LoanDetailDto(loan.getGlobalAccountNum(), loan.getLoanOffering()
                        .getPrdOfferingName(), loan.getAccountState().getId(), loan.getAccountState().getName(), loan
                        .getLoanSummary().getOutstandingBalance(), loan.getTotalAmountDue()));
            }
            return loanDetail;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClientDisplayDto getClientDisplayDto(Integer clientId, UserContext userContext) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CLIENT_ID", clientId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery("getClientDisplayDto", queryParameters);

        if (queryResult.size() == 0) {
            throw new MifosRuntimeException("Client not found: " + clientId);
        }
        if (queryResult.size() > 1) {
            throw new MifosRuntimeException("Error finding Client id: " + clientId + " - Number found: "
                    + queryResult.size());
        }

        final Integer customerId = (Integer) queryResult.get(0)[0];
        final String globalCustNum = (String) queryResult.get(0)[1];
        final String displayName = (String) queryResult.get(0)[2];
        final String parentCustomerDisplayName = (String) queryResult.get(0)[3];
        final String branchName = (String) queryResult.get(0)[4];
        final String externalId = (String) queryResult.get(0)[5];
        final String customerFormedByDisplayName = (String) queryResult.get(0)[6];
        final Date customerActivationDate = (Date) queryResult.get(0)[7];
        final Short customerLevelId = (Short) queryResult.get(0)[8];
        final Short customerStatusId = (Short) queryResult.get(0)[9];
        final String lookupName = (String) queryResult.get(0)[10];
        final Date trainedDate = (Date) queryResult.get(0)[11];
        final Date dateOfBirth = (Date) queryResult.get(0)[12];
        final String governmentId = (String) queryResult.get(0)[13];
        final Short groupFlag = (Short) queryResult.get(0)[14];
        final Boolean blackListed = (Boolean) queryResult.get(0)[15];
        final Short loanOfficerId = (Short) queryResult.get(0)[16];
        final String loanOfficerName = (String) queryResult.get(0)[17];
        final String businessActivitiesName = (String) queryResult.get(0)[18];
        final String handicappedName = (String) queryResult.get(0)[19];
        final String maritalStatusName = (String) queryResult.get(0)[20];
        final String citizenshipName = (String) queryResult.get(0)[21];
        final String ethnicityName = (String) queryResult.get(0)[22];
        final String educationLevelName = (String) queryResult.get(0)[23];
        final String povertyStatusName = (String) queryResult.get(0)[24];
        final Short numChildren = (Short) queryResult.get(0)[25];
        final Integer pictureId = (Integer) queryResult.get(0)[26];

        Boolean clientUnderGroup = false;
        if (groupFlag.compareTo(Short.valueOf("0")) > 0) {
            clientUnderGroup = true;
        }
        Boolean isCustomerPicture = false;
        if (pictureId != null) {
            isCustomerPicture = true;
        }
        final String customerStatusName = MessageLookup.getInstance().lookup(lookupName, userContext);
        final String businessActivities = MessageLookup.getInstance().lookup(businessActivitiesName, userContext);
        final String handicapped = MessageLookup.getInstance().lookup(handicappedName, userContext);
        final String maritalStatus = MessageLookup.getInstance().lookup(maritalStatusName, userContext);
        final String citizenship = MessageLookup.getInstance().lookup(citizenshipName, userContext);
        final String ethnicity = MessageLookup.getInstance().lookup(ethnicityName, userContext);
        final String educationLevel = MessageLookup.getInstance().lookup(educationLevelName, userContext);
        final String povertyStatus = MessageLookup.getInstance().lookup(povertyStatusName, userContext);

        String spouseFatherValue = null;
        String spouseFatherName = null;
        List<ClientFamilyDetailDto> familyDetails = null;
        Boolean areFamilyDetailsRequired = ClientRules.isFamilyDetailsRequired();

        if (areFamilyDetailsRequired) {
            familyDetails = new ArrayList<ClientFamilyDetailDto>();

            List<Object[]> familyDetailsQueryResult = (List<Object[]>) this.genericDao.executeNamedQuery("getClientFamilyDetailDto", queryParameters);

            for (Object[] familyDetail : familyDetailsQueryResult) {
                final String relationshipLookup = (String) familyDetail[0];
                final String familyDisplayName = (String) familyDetail[1];
                final Date familyDateOfBirth = (Date) familyDetail[2];
                final String genderLookup = (String) familyDetail[3];
                final String livingStatusLookup = (String) familyDetail[4];

                final String relationship = MessageLookup.getInstance().lookup(relationshipLookup, userContext);
                final String gender = MessageLookup.getInstance().lookup(genderLookup, userContext);
                final String livingStatus = MessageLookup.getInstance().lookup(livingStatusLookup, userContext);

                familyDetails.add(new ClientFamilyDetailDto(relationship, familyDisplayName, familyDateOfBirth, gender,
                        livingStatus));
            }
        } else {

            List<Object[]> clientNameDetailsQueryResult = (List<Object[]>) this.genericDao.executeNamedQuery("getClientNameDetailDto", queryParameters);

            final String spouseFatherValueLookUp = (String) clientNameDetailsQueryResult.get(0)[0];
            spouseFatherName = (String) clientNameDetailsQueryResult.get(0)[1];
            spouseFatherValue = MessageLookup.getInstance().lookup(spouseFatherValueLookUp, userContext);
        }

        return new ClientDisplayDto(customerId, globalCustNum, displayName, parentCustomerDisplayName, branchName,
                externalId, customerFormedByDisplayName, customerActivationDate, customerLevelId, customerStatusId,
                customerStatusName, trainedDate, dateOfBirth, governmentId, clientUnderGroup, blackListed,
                loanOfficerId, loanOfficerName, businessActivities, handicapped, maritalStatus, citizenship, ethnicity,
                educationLevel, povertyStatus, numChildren, isCustomerPicture, areFamilyDetailsRequired,
                spouseFatherValue, spouseFatherName, familyDetails);
    }

    @Override
    public CustomerPerformanceHistoryDto numberOfMeetings(boolean isPresent, Integer clientId) {

        CustomerPerformanceHistoryDto customerPerformanceHistoryDto = new CustomerPerformanceHistoryDto();

        java.util.Date dateOneYearBefore = new DateTime().minusYears(1).toDate();

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMERID", clientId);
        queryParameters.put("DATEONEYEARBEFORE", dateOneYearBefore);

        if (isPresent) {
            Long result = (Long) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSATTENDED, queryParameters);
            customerPerformanceHistoryDto.setMeetingsAttended(result.intValue());
        } else {
            Long result = (Long) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSMISSED, queryParameters);
            customerPerformanceHistoryDto.setMeetingsMissed(result.intValue());
        }

        return customerPerformanceHistoryDto;
    }

    @Override
    public CustomerStatusEntity findClientPendingStatus() {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("STATUS_ID", CustomerStatus.CLIENT_PENDING.getValue());

        return findCustomerStatusByStatusId(queryParameters);
    }

    @Override
    public CustomerStatusEntity findGroupPendingStatus() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("STATUS_ID", CustomerStatus.GROUP_PENDING.getValue());

        return findCustomerStatusByStatusId(queryParameters);
    }

    private CustomerStatusEntity findCustomerStatusByStatusId(Map<String, Object> queryParameters) {
        return (CustomerStatusEntity) genericDao.executeUniqueResultNamedQuery("findCustomerStatusByStatusId", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int countOfClients() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        List queryResult = this.genericDao.executeNamedQuery("countOfClients", queryParameters);

        return ((Long) queryResult.get(0)).intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int countOfGroups() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        List queryResult = this.genericDao.executeNamedQuery("countOfGroups", queryParameters);

        return ((Long) queryResult.get(0)).intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccountBO> findGLIMLoanAccountsApplicableTo(final Integer customerId, final Integer customerWithActiveAccount) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);

        List<LoanBO> queryResult = (List<LoanBO>) this.genericDao.executeNamedQuery("findGLIMLoanAccountsByCustomerId", queryParameters);

        List<AccountBO> matchingAccounts = new ArrayList<AccountBO>();
        for (LoanBO loanAccount : queryResult) {
            LoanBO parentAccount = loanAccount.getParentAccount();
            if (parentAccount.getCustomer().getCustomerId().equals(customerWithActiveAccount)) {
                matchingAccounts.add(loanAccount);
            }
        }

        return matchingAccounts;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FieldConfigurationEntity> findMandatoryConfigurableFieldsApplicableToCenter() {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityId", EntityType.CENTER.getValue());

        return (List<FieldConfigurationEntity>) this.genericDao.executeNamedQuery("findVisibleMandatoryConfigurableFieldsApplicableTo", queryParameters);
    }

    @Override
    public void updateLoanOfficersForAllChildrenAndAccounts(Short parentLO, String parentSearchId, Short parentOfficeId) {

        String hql = "update CustomerBO customer " + " set customer.personnel.personnelId = :parentLoanOfficer "
                + " where customer.searchId like :parentSearchId" + " and customer.office.officeId = :parentOfficeId";

        Query update = this.genericDao.createQueryForUpdate(hql);

        update.setParameter("parentLoanOfficer", parentLO);
        update.setParameter("parentSearchId", parentSearchId + ".%");
        update.setParameter("parentOfficeId", parentOfficeId);
        update.executeUpdate();

        updateLoanOfficersForAllChildrenAccounts(parentLO, parentSearchId, parentOfficeId);
    }

    /**
     * Update loan officer for all children accounts.
     *
     * This method was introduced for when a center is assigned a new loan officer, and this loan officer needs to be
     * re-assigned not just for the center's groups and clients, but for each account belonging to those customers.
     *
     * Note: Required as to fix issues 1570 and 1804
     * Note: 10/08/2008: direct sqls are used to improve performance (issue 2209)
     *
     * @param parentLO
     *            the parent loan officer
     * @param parentSearchId
     *            the parent search id
     * @param parentOfficeId
     *            the parent office id
     */
    public final void updateLoanOfficersForAllChildrenAccounts(final Short parentLO, String parentSearchId, final Short parentOfficeId) {

        if (parentLO == null || parentSearchId == null || parentOfficeId == null) {
            return;
        }

        ResultSet customerIds = null;
        Statement statement = null;
        String childrenSearchId = parentSearchId + ".%";

        try {
            Connection connection = ((GenericDaoHibernate)this.genericDao).getHibernateUtil().getSessionTL().connection();
            statement = connection.createStatement();
            String sql = " select customer_id from customer where " + " customer.search_id like '" + childrenSearchId
                    + "' and customer.branch_id = " + parentOfficeId.shortValue();
            customerIds = statement.executeQuery(sql);
            if (customerIds != null) {
                while (customerIds.next()) {
                    int customerId = customerIds.getInt("customer_id");
                    updateAccountsForOneCustomer(customerId, parentLO, connection);
                }
            }
        } catch (SQLException e) {
            throw new MifosRuntimeException(e);
        }
        finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (customerIds != null) {
                    customerIds.close();
                }
            } catch (SQLException e) {
                // ignore as can't rethrow from finally
            }
        }

    }

    private void updateAccountsForOneCustomer(final Integer customerId, final Short parentLO,
            final Connection connection) {

        try {
            Statement statement = connection.createStatement();
            String sql = "update account " + " set personnel_id = " + parentLO.shortValue()
                    + " where account.customer_id = " + customerId.intValue();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            throw new MifosRuntimeException(e);
        }
    }
}