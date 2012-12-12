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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerFlagDetailEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerPerformanceHistoryDto;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.struts.uihelpers.CustomerUIHelperFn;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.Param;
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
import org.mifos.dto.screen.ClientFamilyDetailOtherDto;
import org.mifos.dto.screen.GroupDisplayDto;
import org.mifos.dto.screen.LoanCycleCounter;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.fileupload.domain.ClientFileEntity;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.ChapterNum;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.MifosStringUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerDaoHibernate implements CustomerDao {

    private final GenericDao genericDao;

    @Autowired
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
    public CustomerBO findCustomerBySystemId(String globalCustNum) {
        if (StringUtils.isBlank(globalCustNum)) {
            throw new IllegalArgumentException("globalCustNum cannot be null or empty.");
        }

        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalCustNum", globalCustNum);

        return (CustomerBO) genericDao.executeUniqueResultNamedQuery("customer.findBySystemId", queryParameters);
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
    public ClientBO findClientById(Integer clientId) {
        return (ClientBO) genericDao.getSession().get(ClientBO.class, clientId);
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
    public List<FeeBO> retrieveFeesApplicableToCenters() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FeeCategory.ALLCUSTOMERS.toString(), FeeCategory.ALLCUSTOMERS.getValue());
        queryParameters.put("CUSTOMER_CATEGAORY", FeeCategory.CENTER.getValue());
        return retrieveFeesApplicableTo(queryParameters);
    }
    
    @Override
    public Date getLastMeetingDateForCustomer(final Integer customerId) {
        Date meetingDate = null;
        Date actionDate = new DateTimeService().getCurrentJavaSqlDate();
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        queryParameters.put("ACTION_DATE", actionDate);
        meetingDate = (Date) genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_LAST_MEETINGDATE_FOR_CUSTOMER, queryParameters);
        return meetingDate;
    }

    @Override
    public Date getFirstMeetingDateForCustomer(Integer customerId) {
        Date meetingDate = null;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        meetingDate = (Date) genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_FIRST_MEETINGDATE_FOR_CUSTOMER, queryParameters);
        return meetingDate;
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
    public void save(AccountBO customerAccount) {
        this.genericDao.createOrUpdate(customerAccount);
    }

    @Override
    public void save(CustomerStatusEntity customerStatusEntity) {
        this.genericDao.createOrUpdate(customerStatusEntity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> findClientsThatAreNotCancelledOrClosedReturningDetailDto(String searchId,
            Short branchId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", branchId);
        List<CustomerDetailDto> clients = (List<CustomerDetailDto>) genericDao.executeNamedQuery(
                "Customer.getListOfClientsUnderGroupOtherThanClosedAndCancelled", queryParameters);

        // bug #1417 - wrong client sort order. Client sort order on bulk
        // entry screens should match ordering on group details page.
        Collections.sort(clients, searchIdComparator());
        return clients;
    }

    public static Comparator<CustomerDetailDto> searchIdComparator() {
        return new Comparator<CustomerDetailDto>() {
            @Override
			public int compare(final CustomerDetailDto o1, final CustomerDetailDto o2) {
                return ChapterNum.compare(o1.getSearchId(), o2.getSearchId());
            }
        };
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
        List<CustomerBO> queryResult = (List<CustomerBO>) genericDao.executeNamedQuery(
                NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED, queryParameters);

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
        paramList.add(typeNameValue("String", "CENTER_NAME", "%" + searchString + "%"));
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
    public List<ValueListElement> retrieveEthnicity() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.ETHNICITY);

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
        final String customerStatusName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupName);

        return new CenterDisplayDto(customerId, globalCustNum, displayName, branchId, mfiJoiningDate, createdDate,
                versionNo, externalId, customerLevelId, customerStatusId, customerStatusName, loanOfficerId,
                loanOfficerName);
    }

    @Override
    public CustomerAccountSummaryDto getCustomerAccountSummaryDto(Integer customerId) {
        final HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);

        CustomerAccountBO customerAccount = (CustomerAccountBO) genericDao.executeUniqueResultNamedQuery("customer.viewCustomerAccount", queryParameters);

        return new CustomerAccountSummaryDto(customerAccount.getGlobalAccountNum(), customerAccount.getTotalPaymentDue().toString());
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
        String displayAddress = null;
        if (customer.getAddress() != null) {
            city = customer.getAddress().getCity();
            state = customer.getAddress().getState();
            country = customer.getAddress().getCountry();
            zip = customer.getAddress().getZip();
            phoneNumber = customer.getAddress().getPhoneNumber();
            displayAddress = customer.getAddress().getDisplayAddress();
        }

        if (StringUtils.isBlank(displayAddress)) {
            displayAddress = customer.getDisplayAddress();
        }

        return new CustomerAddressDto(displayAddress, city, state, zip, country, phoneNumber);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> getGroupsOtherThanClosedAndCancelledForGroup(String searchId, Short branchId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", branchId);
        List<CustomerDetailDto> groups = (List<CustomerDetailDto>) this.genericDao.executeNamedQuery(
                "Customer.getListOfGroupsUnderCenterOtherThanClosedAndCancelled", queryParameters);

        Collections.sort(groups, searchIdComparator());
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
    public List<CustomerPositionOtherDto> getCustomerPositionDto(final Integer parentId, final UserContext userContext) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("PARENT_ID", parentId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                "Customer.getCustomerPositionDto", queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<CustomerPositionOtherDto> customerPositions = new ArrayList<CustomerPositionOtherDto>();
        String lookupName;
        Integer customerId;
        String customerDisplayName;
        String positionName;

        for (Object[] customerPosition : queryResult) {
            lookupName = (String) customerPosition[0];
            customerId = (Integer) customerPosition[1];
            customerDisplayName = (String) customerPosition[2];
            positionName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupName);

            customerPositions.add(new CustomerPositionOtherDto(positionName, customerId, customerDisplayName));
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
        BigDecimal maxWithdrawalAmount;
        String savingsType = "";

        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        for (Object[] savingsDetail : queryResult) {
            globalAccountNum = (String) savingsDetail[0];
            prdOfferingName = (String) savingsDetail[1];
            accountStateId = (Short) savingsDetail[2];
            lookupName = (String) savingsDetail[3];
            accountStateName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupName);
            // TODO - use default currency or retrieved currency?
            currency = (Short) savingsDetail[4];
            savingsBalance = new Money(mifosCurrency, (BigDecimal) savingsDetail[5]);
            
            try {
                SavingsBO savingsBO = (SavingsBO) new AccountBusinessService().findBySystemId(globalAccountNum);
                maxWithdrawalAmount = savingsBO.getSavingsOffering().getMaxAmntWithdrawl().getAmount();
                if (savingsBO.getSavingsOffering().getSavingsType().getLookUpValue() != null) {
                    savingsType = savingsBO.getSavingsOffering().getSavingsType().getName();
                }
                
                savingsDetails.add(new SavingsDetailDto(globalAccountNum, prdOfferingName, accountStateId,
                        accountStateName, savingsBalance.toString(),maxWithdrawalAmount, savingsType));
            } catch (ServiceException e) {
                throw new MifosRuntimeException(e);
            }
            
        }
        return savingsDetails;
    }

    @Override
    public CustomerMeetingDto getCustomerMeetingDto(CustomerMeetingEntity customerMeeting, UserContext userContext) {

        if (customerMeeting != null) {
            String meetingSchedule = CustomerUIHelperFn.getMeetingSchedule(customerMeeting.getMeeting(), userContext);

            String meetingPlace = customerMeeting.getMeeting().getMeetingPlace();

            return new CustomerMeetingDto(meetingSchedule, meetingPlace);
        }
        return null;
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
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(key);
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
        List queryResult = this.genericDao.executeNamedQuery("Customer.getGroupCountByGroupNameAndOffice", queryParameters);

        if (Integer.valueOf(queryResult.get(0).toString()) > 0) {
            throw new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, new Object[] {displayName});
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateCenterNameIsNotTakenForOffice(String displayName, Short officeId) throws CustomerException {
        if(StringUtils.isBlank(displayName)) {
            throw new CustomerException(CustomerConstants.INVALID_NAME, new Object[] {displayName});
        }
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerConstants.DISPLAY_NAME, displayName);
        queryParameters.put(CustomerConstants.OFFICE_ID, officeId);

        List queryResult = this.genericDao.executeNamedQuery("Customer.getCenterCount", queryParameters);

        if (Integer.valueOf(queryResult.get(0).toString()) > 0) {
            throw new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, new Object[] {displayName});
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SavingsDetailDto> retrieveSavingOfferingsApplicableToClient() {

        List<SavingsDetailDto> savingDetails = new ArrayList<SavingsDetailDto>();

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdApplicableTo", ApplicableTo.CLIENTS.getValue());

        List<SavingsOfferingBO> savingOfferings = (List<SavingsOfferingBO>) this.genericDao.executeNamedQuery(
                NamedQueryConstants.GET_ACTIVE_OFFERINGS_FOR_CUSTOMER, queryParameters);
        for (SavingsOfferingBO savingsOffering : savingOfferings) {

            SavingsDetailDto savingsDetailsWithOnlyPrdOfferingName = SavingsDetailDto.create(savingsOffering
                    .getPrdOfferingId(), savingsOffering.getPrdOfferingName());
            savingDetails.add(savingsDetailsWithOnlyPrdOfferingName);
        }

        return savingDetails;
    }

    @Override
    public boolean validateGovernmentIdForClient(String governmentId) {
        return checkForClientsBasedOnGovtId(NamedQueryConstants.GET_CLOSED_CLIENT_BASED_ON_GOVT_ID, governmentId,
                Integer.valueOf(0), CustomerStatus.CLIENT_CLOSED);
    }

    @Override
    public boolean validateForClosedClientsOnNameAndDob(final String name, final DateTime dateOfBirth) {
        return checkForDuplicacyBasedOnName(NamedQueryConstants.GET_CLOSED_CLIENT_BASED_ON_NAME_DOB, name, dateOfBirth,
                Integer.valueOf(0), CustomerStatus.CLIENT_CLOSED);
    }

    @Override
    public boolean validateForBlackListedClientsOnNameAndDob(final String name, final DateTime dateOfBirth) {
        return checkForDuplicacyBasedOnName(NamedQueryConstants.GET_BLACKLISTED_CLIENT_BASED_ON_NAME_DOB, name, dateOfBirth,
                Integer.valueOf(0), CustomerStatus.CLIENT_CANCELLED);
    }

    @SuppressWarnings("unchecked")
    private boolean checkForClientsBasedOnGovtId(final String queryName, final String governmentId,
            final Integer customerId, CustomerStatus customerStatus) {

        String trimmedGovtId = StringUtils.trim(governmentId);
        if (StringUtils.isBlank(trimmedGovtId)) {
            return false;
        }

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", CustomerLevel.CLIENT.getValue());
        queryParameters.put("GOVT_ID", trimmedGovtId);
        queryParameters.put("customerId", customerId);
        queryParameters.put("clientStatus", customerStatus.getValue());
        List queryResult = this.genericDao.executeNamedQuery(queryName, queryParameters);
        int queryResultValue = ((Long) queryResult.get(0)).intValue();
        return (customerId > 0 ? queryResultValue > 0 : queryResultValue > 1);
    }

    @SuppressWarnings("unchecked")
    private boolean checkForDuplicacyBasedOnName(final String queryName, final String name, final DateTime dateOfBirth,
            final Integer customerId, CustomerStatus customerStatus) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clientName", name);
        queryParameters.put("LEVELID", CustomerLevel.CLIENT.getValue());
        queryParameters.put("DATE_OFBIRTH", dateOfBirth.toDate());
        queryParameters.put("customerId", customerId);
        queryParameters.put("clientStatus", customerStatus.getValue());
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

        return (List<CustomerDetailDto>) this.genericDao.executeNamedQueryWithResultTransformer(
                "Customer.get_loanofficer_list_of_active_centers", queryParameters, CustomerDetailDto.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDetailDto> findGroupsUnderUser(PersonnelBO personnel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.GROUP.getValue());
        return (List<CustomerDetailDto>) this.genericDao.executeNamedQueryWithResultTransformer(
                "Customer.get_loanofficer_list_of_groups", queryParameters, CustomerDetailDto.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getAvgLoanAmountForMemberInGoodOrBadStanding(String groupSearchId, Short groupOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", groupSearchId + ".%");
        queryParameters.put("OFFICE_ID", groupOfficeId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery(
                "Customer.getAvgLoanAmountForMemberInGoodOrBadStanding", queryParameters);

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

    @SuppressWarnings("unchecked")
    @Override
    public String getTotalLoanAmountForGroup(String groupSearchId, Short groupOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", groupSearchId);
        queryParameters.put("SEARCH_STRING2", groupSearchId + ".%");
        queryParameters.put("OFFICE_ID", groupOfficeId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery("Customer.getTotalLoanAmountForGroup", queryParameters);

        if (queryResult.size() > 1) {
            return localizedMessageLookup("errors.multipleCurrencies");
        }
        if (queryResult.size() == 0) {
            return new Money(Money.getDefaultCurrency()).toString();
        }

        // TODO - use default currency or retrieved currency?
        Short currency = (Short) queryResult.get(0)[0];
        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        Money totalLoanAmount = new Money(mifosCurrency, (BigDecimal) queryResult.get(0)[1]);
        return totalLoanAmount.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getTotalOutstandingLoanAmountForGroupAndClientsOfGroups(String groupSearchId, Short groupOfficeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", groupSearchId);
        queryParameters.put("SEARCH_STRING2", groupSearchId + ".%");
        queryParameters.put("OFFICE_ID", groupOfficeId);
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery(
                "Customer.getTotalOutstandingLoanAmountForGroupAndClientsOfGroups", queryParameters);

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
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery(
                "Customer.getTotalSavingsAmountForGroupandClientsOfGroup", queryParameters);

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
        List<Object[]> queryResult = (List<Object[]>) genericDao.executeNamedQuery("getGroupDisplayDto",
                queryParameters);

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
        final String customerStatusName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupName);

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
                        .getLoanSummary().getOutstandingBalance().toString(), loan.getTotalAmountDue().toString(), loan.getAccountType().getAccountTypeId()));
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
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery("getClientDisplayDto",
                queryParameters);

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
        final Integer branchId = (Integer) queryResult.get(0)[26];

        Boolean clientUnderGroup = false;
        if (groupFlag.compareTo(Short.valueOf("0")) > 0) {
            clientUnderGroup = true;
        }

        final String customerStatusName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupName);
        final String businessActivities = ApplicationContextProvider.getBean(MessageLookup.class).lookup(businessActivitiesName);
        final String handicapped = ApplicationContextProvider.getBean(MessageLookup.class).lookup(handicappedName);
        final String maritalStatus = ApplicationContextProvider.getBean(MessageLookup.class).lookup(maritalStatusName);
        final String citizenship = ApplicationContextProvider.getBean(MessageLookup.class).lookup(citizenshipName);
        final String ethnicity = ApplicationContextProvider.getBean(MessageLookup.class).lookup(ethnicityName);
        final String educationLevel = ApplicationContextProvider.getBean(MessageLookup.class).lookup(educationLevelName);
        final String povertyStatus = ApplicationContextProvider.getBean(MessageLookup.class).lookup(povertyStatusName);

        String spouseFatherValue = null;
        String spouseFatherName = null;
        List<ClientFamilyDetailOtherDto> familyDetails = null;
        Boolean areFamilyDetailsRequired = ClientRules.isFamilyDetailsRequired();

        if (areFamilyDetailsRequired) {
            familyDetails = new ArrayList<ClientFamilyDetailOtherDto>();

            List<Object[]> familyDetailsQueryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                    "getClientFamilyDetailDto", queryParameters);

            for (Object[] familyDetail : familyDetailsQueryResult) {
                final String relationshipLookup = (String) familyDetail[0];
                final String familyDisplayName = (String) familyDetail[1];
                final Date familyDateOfBirth = (Date) familyDetail[2];
                final String genderLookup = (String) familyDetail[3];
                final String livingStatusLookup = (String) familyDetail[4];

                final String relationship = ApplicationContextProvider.getBean(MessageLookup.class).lookup(relationshipLookup);
                final String gender = ApplicationContextProvider.getBean(MessageLookup.class).lookup(genderLookup);
                final String livingStatus = ApplicationContextProvider.getBean(MessageLookup.class).lookup(livingStatusLookup);

                String dateOfBirthAsString = "";
                if (familyDateOfBirth != null) {
                    dateOfBirthAsString = DateUtils.makeDateAsSentFromBrowser(familyDateOfBirth);
                }
                familyDetails.add(new ClientFamilyDetailOtherDto(relationship, familyDisplayName, familyDateOfBirth, gender,
                        livingStatus, dateOfBirthAsString));
            }
        } else {

            List<Object[]> clientNameDetailsQueryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                    "getClientNameDetailDto", queryParameters);

            if (clientNameDetailsQueryResult.size() > 0) {
                final String spouseFatherValueLookUp = (String) clientNameDetailsQueryResult.get(0)[0];
                spouseFatherName = (String) clientNameDetailsQueryResult.get(0)[1];
                spouseFatherValue = ApplicationContextProvider.getBean(MessageLookup.class).lookup(spouseFatherValueLookUp);
            }
        }

        Integer age = null;
        if (dateOfBirth != null) {
            age = DateUtils.DateDiffInYears(new java.sql.Date(dateOfBirth.getTime()));
        }

        return new ClientDisplayDto(customerId, globalCustNum, displayName, parentCustomerDisplayName, branchId, branchName,
                externalId, customerFormedByDisplayName, customerActivationDate, customerLevelId, customerStatusId,
                customerStatusName, trainedDate, dateOfBirth, governmentId, clientUnderGroup, blackListed,
                loanOfficerId, loanOfficerName, businessActivities, handicapped, maritalStatus, citizenship, ethnicity,
                educationLevel, povertyStatus, numChildren, areFamilyDetailsRequired,
                spouseFatherValue, spouseFatherName, familyDetails, age);
    }

    @Override
    public CustomerPerformanceHistoryDto numberOfMeetings(boolean isPresent, Integer clientId) {

        CustomerPerformanceHistoryDto customerPerformanceHistoryDto = new CustomerPerformanceHistoryDto();

        Date dateOneYearBefore = new DateTime().minusYears(1).toDate();

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMERID", clientId);
        queryParameters.put("DATEONEYEARBEFORE", dateOneYearBefore);

        if (isPresent) {
            Long result = (Long) this.genericDao.executeUniqueResultNamedQuery(
                    NamedQueryConstants.NUMBEROFMEETINGSATTENDED, queryParameters);
            customerPerformanceHistoryDto.setMeetingsAttended(result.intValue());
        } else {
            Long result = (Long) this.genericDao.executeUniqueResultNamedQuery(
                    NamedQueryConstants.NUMBEROFMEETINGSMISSED, queryParameters);
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
        return (CustomerStatusEntity) genericDao.executeUniqueResultNamedQuery("findCustomerStatusByStatusId",
                queryParameters);
    }

    @SuppressWarnings("unchecked")
    private int maxIdOfCustomersWithNoParentWithinOffice(Short officeIdValue) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeIdValue);
        queryParameters.put("PARENT_CUSTOMER", null);

        List queryResult = this.genericDao.executeNamedQuery("maxIdOfCustomersWithNoParentWithinOffice",
                queryParameters);

        Object result = queryResult.get(0);

        if (result == null) {
            return 0;
        }

        return ((Number) result).intValue();
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
    public int countOfActiveClients() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        List queryResult = this.genericDao.executeNamedQuery("countOfActiveClients", queryParameters);

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
    public int countOfActiveGroups() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        List queryResult = this.genericDao.executeNamedQuery("countOfActiveGroups", queryParameters);

        return ((Long) queryResult.get(0)).intValue();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int countOfActiveCenters() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        List queryResult = this.genericDao.executeNamedQuery("countOfActiveCenters", queryParameters);

        return ((Long) queryResult.get(0)).intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccountBO> findGLIMLoanAccountsApplicableTo(final Integer customerId,
            final Integer customerWithActiveAccount) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);

        List<LoanBO> queryResult = (List<LoanBO>) this.genericDao.executeNamedQuery("findGLIMLoanAccountsByCustomerId",
                queryParameters);

        List<AccountBO> matchingAccounts = new ArrayList<AccountBO>();
        for (LoanBO loanAccount : queryResult) {
            LoanBO parentAccount = loanAccount.getParentAccount();
            if (parentAccount.getCustomer().getCustomerId().equals(customerWithActiveAccount)) {
                matchingAccounts.add(loanAccount);
            }
        }

        return matchingAccounts;
    }

    @Override
    public void checkPermissionForDefaultFeeRemoval(UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws CustomerException {
        if (!isPermissionAllowed(userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    @Override
    public void checkPermissionForDefaultFeeRemovalFromLoan(UserContext userContext, CustomerBO customer) throws CustomerException {
        if (!ActivityMapper.getInstance().isRemoveFeesPermittedForAccounts(AccountTypes.LOAN_ACCOUNT, customer.getLevel(),
                userContext, customer.getOfficeId(), customer.getLoanOfficerId())) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }

    }

    private boolean isPermissionAllowed(UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isRemoveFeesPermittedForAccounts(AccountTypes.CUSTOMER_ACCOUNT,
                CustomerLevel.CLIENT, userContext, recordOfficeId, recordLoanOfficerId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FieldConfigurationEntity> findMandatoryConfigurableFieldsApplicableToCenter() {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityId", EntityType.CENTER.getValue());

        return (List<FieldConfigurationEntity>) this.genericDao.executeNamedQuery(
                "findVisibleMandatoryConfigurableFieldsApplicableTo", queryParameters);
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
     * Note: Required as to fix issues 1570 and 1804 Note: 10/08/2008: direct sqls are used to improve performance
     * (issue 2209)
     *
     * @param parentLO
     *            the parent loan officer
     * @param parentSearchId
     *            the parent search id
     * @param parentOfficeId
     *            the parent office id
     */
    public final void updateLoanOfficersForAllChildrenAccounts(final Short parentLO, String parentSearchId,
            final Short parentOfficeId) {

        if (parentLO == null || parentSearchId == null || parentOfficeId == null) {
            return;
        }

        ResultSet customerIds = null;
        Statement statement = null;
        String childrenSearchId = parentSearchId + ".%";

        try {
            Connection connection = StaticHibernateUtil.getSessionTL()
                    .connection();
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
        } finally {
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

    @Override
    public final void checkPermissionForStatusChange(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) throws CustomerException {
        if (!isPermissionAllowed(newState, userContext, flagSelected, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isStateChangePermittedForCustomer(newState.shortValue(),
                null != flagSelected ? flagSelected.shortValue() : 0, userContext, recordOfficeId, recordLoanOfficerId);
    }

    @Override
    public void validateClientForDuplicateNameOrGovtId(String name, Date dob, String governmentId) throws CustomerException {

        Integer customerId = Integer.valueOf(0);

        if (StringUtils.isNotBlank(governmentId)) {
            if (checkForDuplicacyOnGovtIdForNonClosedClients(governmentId, customerId) == true) {
                String label = ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.GOVERNMENT_ID);
                throw new CustomerException(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION, new Object[] { governmentId,
                        label });
            }
        }

        if (checkForDuplicacyForNonClosedClientsOnNameAndDob(name, dob, customerId) == true) {
            throw new CustomerException(CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION,
                    new Object[] { name });
        }
    }

    // Returns true if another client with same govt id is found with a state other than closed
    private boolean checkForDuplicacyOnGovtIdForNonClosedClients(final String governmentId, final Integer customerId) {
        return checkForClientsBasedOnGovtId("Customer.getNonClosedClientBasedOnGovtId", governmentId, customerId, CustomerStatus.CLIENT_CLOSED);
    }

    // returns true if a duplicate client is found with same display name and dob in state other than closed
    private boolean checkForDuplicacyForNonClosedClientsOnNameAndDob(final String name, final Date dob, final Integer customerId) {
        return checkForDuplicacyBasedOnName("Customer.getNonClosedClientBasedOnNameAndDateOfBirth", name, dob, customerId);
    }

    @SuppressWarnings("unchecked")
    private boolean checkForDuplicacyBasedOnName(final String queryName, final String name, final Date dob, final Integer customerId) {
        String trimmedName = StringUtils.trim(name);
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clientName", trimmedName);
        queryParameters.put("LEVELID", CustomerLevel.CLIENT.getValue());
        queryParameters.put("DATE_OFBIRTH", dob);
        queryParameters.put("customerId", customerId);
        queryParameters.put("clientStatus", CustomerStatus.CLIENT_CLOSED.getValue());
        Long count = (Long) this.genericDao.executeUniqueResultNamedQuery(queryName, queryParameters);
        return count > 0;
    }

    @Override
    public int retrieveLastSearchIdValueForNonParentCustomersInOffice(final Short officeId) {
        final int maxCustomerId = maxIdOfCustomersWithNoParentWithinOffice(officeId);
        int maxValue = 0;
        if (maxCustomerId > 0) {
            final CustomerBO lastEnteredNonParentCustomerUnderOffice = findCustomerById(maxCustomerId);
            final String searchId = lastEnteredNonParentCustomerUnderOffice.getSearchId();

            int suffixValue = 0;
            if (searchId.startsWith(GroupConstants.PREFIX_SEARCH_STRING) && searchId.lastIndexOf('.') == 1) {
                suffixValue = Integer.parseInt(searchId.substring(2));
            } else if (searchId.startsWith(GroupConstants.PREFIX_SEARCH_STRING)) {
                // legacy format 1.x.y instead of just 1.x for customers directly under office (don't care about level)
                String nextWholeNumber = searchId.substring(2, searchId.indexOf('.', 2));
                suffixValue = Integer.parseInt(nextWholeNumber) + 25;
            }

            if (suffixValue > maxValue) {
                maxValue = suffixValue;
            }

        }
        return maxValue;
    }

    @Override
    public void checkPermissionForEditMeetingSchedule(UserContext userContext, CustomerBO customer)
            throws CustomerException {

        Short recordOfficeId = customer.getOffice().getOfficeId();
        Short recordLoanOfficerId = userContext.getId();

        if (customer.getPersonnel() != null) {
            recordLoanOfficerId = customer.getPersonnel().getPersonnelId();
        }

        if (!isPermissionAllowed(customer.getLevel(), userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(CustomerLevel customerLevel, UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isEditMeetingSchedulePermittedForCustomers(customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    @Override
    public List<ValueListElement> retrieveTitles() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.PERSONNEL_TITLE);

        return retrieveMasterData(queryParameters);
    }


    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> List<T> doFetchListOfMasterDataFor(Class<T> type) {
        Session session = StaticHibernateUtil.getSessionTL();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
        }
        return masterEntities;
    }

    @Override
    public List<PersonnelLevelEntity> retrievePersonnelLevels() {
        return doFetchListOfMasterDataFor(PersonnelLevelEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDto> findCustomersWithGivenPhoneNumber(String phoneNumber) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("phoneNumberStripped", MifosStringUtils.removeNondigits(phoneNumber));
        List<CustomerBO> queryResult = (List<CustomerBO>) genericDao.executeNamedQuery("Customer.findCustomersWithGivenPhoneNumber", queryParameters);
        List<CustomerDto> customerDtos = new ArrayList<CustomerDto>();
        for (CustomerBO customerBO : queryResult) {
            CustomerDto customerDto = new CustomerDto(customerBO.getCustomerId(), customerBO.getDisplayName(),
                    customerBO.getCustomerLevel().getId(), customerBO.getSearchId());

            customerDtos.add(customerDto);
        }
        return customerDtos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccountBO> retrieveAllClosedLoanAndSavingsAccounts(Integer customerId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        List<AccountBO> queryResult = (List<AccountBO>) this.genericDao.executeNamedQuery("customer.viewallclosedloanandsavingsaccounts", queryParameters);

        List<AccountBO> closedLoanAndSavingsAccounts = new ArrayList<AccountBO>();
        if (queryResult != null) {
            closedLoanAndSavingsAccounts.addAll(queryResult);
        }

        return closedLoanAndSavingsAccounts;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerDto> findTopOfHierarchyCustomersUnderLoanOfficer(CustomerLevel customerLevel,
            Short loanOfficerId, Short officeId) {

        List<CustomerDto> activeTopOfHierarchyCustomer = new ArrayList<CustomerDto>();

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerLevelId", customerLevel.getValue());
        queryParameters.put("personnelId", loanOfficerId);
        queryParameters.put("officeId", officeId);

        List<CustomerDto> queryResult = (List<CustomerDto>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_PARENTCUSTOMERS_FOR_LOANOFFICER, queryParameters);
        if (queryResult != null) {
            activeTopOfHierarchyCustomer.addAll(queryResult);
        }

        return activeTopOfHierarchyCustomer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClientBO> findActiveClientsUnderParent(String searchId, Short officeId) {

        List<ClientBO> activeClients = new ArrayList<ClientBO>();

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List<ClientBO> queryResult = (List<ClientBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.ACTIVE_CLIENTS_UNDER_PARENT, queryParameters);
        if (queryResult != null) {
            activeClients.addAll(queryResult);
        }
        return activeClients;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClientBO> findAllExceptClosedAndCancelledClientsWithoutGroupForLoanOfficer(Short loanOfficerId, Short officeId) {

        List<ClientBO> clients = new ArrayList<ClientBO>();

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("PERSONNEL_ID", loanOfficerId);
        queryParameters.put("OFFICE_ID", officeId);
        List<ClientBO> queryResult = (List<ClientBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.ALL_EXCEPT_CANCELLED_CLOSED_CLIENTS_WITHOUT_GROUP_FOR_LOAN_OFFICER, queryParameters);
        if (queryResult != null) {
            clients.addAll(queryResult);
        }
        return clients;
    }

    @Override
    public List<ClientBO> findAllExceptClosedAndCancelledClientsUnderParent(String searchId, Short officeId) {
        List<ClientBO> clients = new ArrayList<ClientBO>();

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List<ClientBO> queryResult = (List<ClientBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.ALL_EXCEPT_CANCELLED_CLOSED_CLIENTS_UNDER_PARENT, queryParameters);
        if (queryResult != null) {
            clients.addAll(queryResult);
        }
        return clients;
    }

    @Override
    public void save(CustomerCheckListBO customerChecklist) {
        this.genericDao.createOrUpdate(customerChecklist);
    }

    @Override
    public void save(AccountCheckListBO accountCheckListBO) {
        this.genericDao.createOrUpdate(accountCheckListBO);
    }

    @Override
    public List<DateTime> getAccountActionDatesForCustomer(Integer customerId) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        
        List<java.sql.Date> queryList = (List<java.sql.Date>) genericDao.executeNamedQuery(
                NamedQueryConstants.GET_ACTION_DATES_FOR_CUSTOMER, queryParameters);
        
        List<DateTime> dateList =  new ArrayList<DateTime>();
        
        for (java.sql.Date sqlDate : queryList) {
            dateList.add(new DateTime(sqlDate));
        }
        
        return dateList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClientBO> findAllBorrowers() {
        List<ClientBO> borrowers = new ArrayList<ClientBO>();
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        List<ClientBO> queryList = (List<ClientBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_BORROWERS,queryParameters);
        if (queryList !=null){
            borrowers.addAll(queryList);
        }
        return borrowers;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<GroupBO> findAllBorrowersGroup() {
        List<GroupBO> borrowers = new ArrayList<GroupBO>();
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        List<GroupBO> queryList = (List<GroupBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_BORROWERS_GROUP,queryParameters);
        if (queryList !=null){
            borrowers.addAll(queryList);
        }
        return borrowers;
    }

    @Override
    public int countAllBorrowers() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.COUNT_ALL_BORROWERS, queryParameters);
        return ((BigInteger) queryResult.get(0)).intValue();
    }

    @Override
    public int countAllBorrowersGroup() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.COUNT_ALL_BORROWERS_GROUP, queryParameters);
        return ((BigInteger) queryResult.get(0)).intValue();
    }

    @Override
    public ClientFileEntity getUploadedFile(Long fileId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("fileId", fileId);
        return (ClientFileEntity) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_UPLOADED_FILE, queryParameters);
    }
    
    @Override
    public List<ClientFileEntity> getClientAllUploadedFiles(Integer clientId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clientId", clientId);
        Object result =  this.genericDao.executeNamedQuery(NamedQueryConstants.GET_CLIENT_ALL_UPLOADED_FILES, queryParameters);
        return (List<ClientFileEntity>) result;
    }
    
    @Override
    public ClientFileEntity getClientUploadedFileByName(Integer clientId, String fileName) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("clientId", clientId);
        queryParameters.put("fileName", fileName);
        Object result = ((Object[])this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_CLIENT_UPLOADED_FILE_BY_NAME, queryParameters))[0];
        return (ClientFileEntity) result;
    }
    
    public List<ClientBO> findBorrowersUnderLoanOfficer(Short loanOffID) {
        List<ClientBO> borrowers = new ArrayList<ClientBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List<ClientBO> queryList = (List<ClientBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_BORROWERS_UNDER_LOANOFF,queryParameters);
        if (queryList !=null){
            borrowers.addAll(queryList);
        }
        return borrowers;
    }

    @Override
    public int countBorrowersUnderLoanOfficer(Short loanOffID) {
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.COUNT_BORROWERS_UNDER_LOANOFF, queryParameters);
        return ((BigInteger) queryResult.get(0)).intValue();
    }

    @Override
    public int countBorrowersGroupUnderLoanOfficer(Short loanOffID) {
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.COUNT_BORROWERS_GROUP_UNDER_LOANOFF, queryParameters);
        return ((BigInteger) queryResult.get(0)).intValue();
    }

    @Override
    public List<GroupBO> findBorrowersGroupUnderLoanOfficer(Short loanOffID) {
        List<GroupBO> borrowers = new ArrayList<GroupBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List<GroupBO> queryList = (List<GroupBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_BORROWERS_GROUP_UNDER_LOANOFF,queryParameters);
        if (queryList !=null){
            borrowers.addAll(queryList);
        }
        return borrowers;
    }

    @Override
    public int countOfActiveClientsUnderLoanOfficer(Short loanOffID) {
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List queryResult = this.genericDao.executeNamedQuery("countOfActiveClientsUnderLoanOfficerID", queryParameters);

        return ((Long) queryResult.get(0)).intValue();
    }

    @Override
    public int countOfActiveGroupsUnderLoanOfficer(Short loanOffID) {
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List queryResult = this.genericDao.executeNamedQuery("countOfActiveGroupsUnderLoanOfficerID", queryParameters);

        return ((Long) queryResult.get(0)).intValue();
    }

    @Override
    public int countOfActiveCentersUnderLoanOfficer(Short loanOffID) {
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List queryResult = this.genericDao.executeNamedQuery("countOfActiveCentersUnderLoanOfficerID", queryParameters);
        return ((Long) queryResult.get(0)).intValue();
    }

    @Override
    public List<ClientBO> findAllActiveClients() {
        List<ClientBO> customers = new ArrayList<ClientBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        List<ClientBO> queryList = (List<ClientBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVE_CLIENTS,queryParameters);
        if (queryList !=null){
            customers.addAll(queryList);
        }
        return customers;
    }

    @Override
    public List<GroupBO> findAllActiveGroups() {
        List<GroupBO> customers = new ArrayList<GroupBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        List<GroupBO> queryList = (List<GroupBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVE_GROUPS,queryParameters);
        if (queryList !=null){
            customers.addAll(queryList);
        }
        return customers;
    }

    @Override
    public List<CenterBO> findAllActiveCenters() {
        List<CenterBO> customers = new ArrayList<CenterBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        List<CenterBO> queryList = (List<CenterBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVE_CENTERS,queryParameters);
        if (queryList !=null){
            customers.addAll(queryList);
        }
        return customers;
    }

    @Override
    public List<ClientBO> findActiveClientsUnderLoanOfficer(Short loanOffID) {
        List<ClientBO> customers = new ArrayList<ClientBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List<ClientBO> queryList = (List<ClientBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CLIENTS_UNDER_LOANOFF,queryParameters);
        if (queryList !=null){
            customers.addAll(queryList);
        }
        return customers;
    }

    @Override
    public List<GroupBO> findActiveGroupsUnderLoanOfficer(Short loanOffID) {
        List<GroupBO> customers = new ArrayList<GroupBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List<GroupBO> queryList = (List<GroupBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ACTIVE_GROUPS_UNDER_LOANOFF,queryParameters);
        if (queryList !=null){
            customers.addAll(queryList);
        }
        return customers;
    }

    @Override
    public List<CenterBO> findActiveCentersUnderLoanOfficer(Short loanOffID) {
        List<CenterBO> customers = new ArrayList<CenterBO>();
        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("ID", loanOffID);
        List<CenterBO> queryList = (List<CenterBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CENTERS_UNDER_LOANOFF,queryParameters);
        if (queryList !=null){
            customers.addAll(queryList);
        }
        return customers;
    }
}