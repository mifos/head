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
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.CustomerPictureEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.BasicGroupInfo;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.Param;
import org.mifos.customers.util.helpers.QueryParamConstants;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.MifosStringUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

@SuppressWarnings("unchecked")
public class CustomerPersistence extends LegacyGenericDao {

    private LegacyPersonnelDao legacyPersonnelDao;

    private LegacyPersonnelDao getLegacyPersonnelDao() {
        if (legacyPersonnelDao == null) {
            legacyPersonnelDao = ApplicationContextProvider.getBean(LegacyPersonnelDao.class);
        }
        return legacyPersonnelDao;
    }

    private static final Predicate CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS = new Predicate() {
        @Override
		public boolean evaluate(final Object object) {
            Set<AccountBO> accounts = ((ClientBO) object).getAccounts();
            return CollectionUtils.exists(accounts, new Predicate() {
                @Override
				public boolean evaluate(final Object object) {
                    AccountStateEntity accountState = ((AccountBO) object).getAccountState();
                    return new AccountStateEntity(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING).sameId(accountState);
                }
            });
        }
    };

    private static final Predicate CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT = new Predicate() {
        @Override
		public boolean evaluate(final Object arg0) {
            Set<AccountBO> accounts = ((ClientBO) arg0).getAccounts();
            return CollectionUtils.exists(accounts, new Predicate() {
                @Override
				public boolean evaluate(final Object arg0) {
                    AccountBO account = (AccountBO) arg0;
                    return AccountTypes.SAVINGS_ACCOUNT.getValue().equals(account.getAccountType().getAccountTypeId())
                            && new AccountStateEntity(AccountState.SAVINGS_ACTIVE).sameId(account.getAccountState());
                }
            });
        }
    };

    private class SearchTemplate {

        String searchString;
        Short officeId;
        Short userId;

        // queries must be set before calling "doSeach"
        String queryNoOffice;
        String queryNoOfficeCount;

        String queryNormal;
        String queryNormalCount;

        String queryNonOfficer;
        String queryNonOfficerCount;

        SearchTemplate(final String searchString, final Short officeId, final Short userId) {
            this.searchString = searchString;
            this.officeId = officeId;
            this.userId = userId;
        }

        QueryResult doSearch()
                throws HibernateSearchException, SystemException, PersistenceException {

            String[] namedQuery = new String[2];
            List<Param> paramList = new ArrayList<Param>();
            QueryInputs queryInputs = new QueryInputs();
            String[] Names = {"customerId", "centerName", "centerGlobalCustNum", "customerType", "branchGlobalNum",
                "branchName", "loanOfficerName", "loanOffcerGlobalNum", "customerStatus", "groupName",
                "groupGlobalCustNum", "clientName", "clientGlobalCustNum", "loanGlobalAccountNumber"};
            QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
            queryInputs.setPath("org.mifos.customers.business.CustomerSearchDto");
            queryInputs.setAliasNames(Names);
            queryResult.setQueryInputs(queryInputs);
            queryInputs.setQueryStrings(namedQuery);
            queryInputs.setParamList(paramList);
            PersonnelBO personnel = getLegacyPersonnelDao().getPersonnel(userId);

            if (officeId != null && officeId.shortValue() == 0) {
                namedQuery[0] = queryNoOfficeCount;
                namedQuery[1] = queryNoOffice;
                if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
                    paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId()));
                } else {
                    paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
                }
            } else {
                paramList.add(typeNameValue("Short", "OFFICEID", officeId));
                if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
                    paramList.add(typeNameValue("String", "ID", personnel.getPersonnelId()));
                    namedQuery[0] = queryNormalCount;
                    namedQuery[1] = queryNormal;
                } else {
                    paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
                    namedQuery[0] = queryNonOfficerCount;
                    namedQuery[1] = queryNonOfficer;
                }
            }

            paramList.add(typeNameValue("String", "SEARCH_STRING", searchString));

            return queryResult;

        }
    }

    public void saveCustomer(final CustomerBO customer) throws CustomerException {
        try {
            createOrUpdate(customer);
            customer.generateGlobalCustomerNumber();
            createOrUpdate(customer);
        } catch (PersistenceException e) {
            throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION, e);
        }
    }

    public CustomerPersistence() {
    }

    public List<CustomerDto> getChildrenForParent(final Integer customerId, final String searchId, final Short officeId)
            throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List<CustomerDto> queryResult = executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CHILDREN_FORPARENT,
                queryParameters);
        return queryResult;
    }

    /**
     * @deprecated - use customerDao findTopOfHierarchyCustomers
     */
    @Deprecated
    public List<CustomerDto> getActiveParentList(final Short personnelId, final Short customerLevelId,
            final Short officeId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("personnelId", personnelId);
        queryParameters.put("customerLevelId", customerLevelId);
        queryParameters.put("officeId", officeId);

        List<CustomerDto> queryResult = executeNamedQuery(NamedQueryConstants.GET_PARENTCUSTOMERS_FOR_LOANOFFICER,
                queryParameters);
        return queryResult;

    }

    public Date getLastMeetingDateForCustomer(final Integer customerId) throws PersistenceException {
        Date meetingDate = null;
        Date actionDate = new DateTimeService().getCurrentJavaSqlDate();
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        queryParameters.put("ACTION_DATE", actionDate);
        meetingDate = (Date) execUniqueResultNamedQuery(NamedQueryConstants.GET_LAST_MEETINGDATE_FOR_CUSTOMER,
                queryParameters);
        return meetingDate;
    }

    /**
     * @deprecated use {@link CustomerDao#findCustomerById(Integer)}
     */
    @Deprecated
    public CustomerBO getCustomer(final Integer customerId) throws PersistenceException {
        return getPersistentObject(CustomerBO.class, customerId);
    }

    public CustomerBO findBySystemId(final String globalCustNum) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        CustomerBO customer = null;
        queryParameters.put("globalCustNum", globalCustNum);
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID,
                queryParameters);
        if (null != queryResult && queryResult.size() > 0) {
            customer = queryResult.get(0);
        }
        return customer;
    }

    /**
     * @deprecated use {@link CustomerDao#findCenterBySystemId(String)}.
     */
    @Deprecated
    public CustomerBO findBySystemId(final String globalCustNum, final Short levelId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        CustomerBO customer = null;
        queryParameters.put("globalCustNum", globalCustNum);
        if (levelId.shortValue() == CustomerLevel.CENTER.getValue()) {
            List<CenterBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_CENTER_BY_SYSTEMID, queryParameters);
            if (null != queryResult && queryResult.size() > 0) {
                customer = queryResult.get(0);
                initializeCustomer(customer);
            }
        } else if (levelId.shortValue() == CustomerLevel.GROUP.getValue()) {
            List<GroupBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_GROUP_BY_SYSTEMID, queryParameters);
            if (null != queryResult && queryResult.size() > 0) {
                customer = queryResult.get(0);
                initializeCustomer(customer);
            }

        } else if (levelId.shortValue() == CustomerLevel.CLIENT.getValue()) {
            List<ClientBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_CLIENT_BY_SYSTEMID, queryParameters);
            if (null != queryResult && queryResult.size() > 0) {
                customer = queryResult.get(0);
                initializeCustomer(customer);
            }
        }

        return customer;
    }

    public QueryResult search(final String searchString, final Short officeId, final Short userId,
            final Short userOfficeId, final Map<Short, Boolean> customerLevelIds) throws PersistenceException {

        QueryResult queryResult = null;

        try {

           queryResult = ApplicationContextProvider.getBean(LegacyAccountDao.class).search(searchString, officeId);
            if (queryResult == null) {
                queryResult = idSearch(searchString, officeId, userId);
                if (queryResult == null) {
                    queryResult = governmentIdSearch(searchString, officeId, userId);
                    if (queryResult == null) {
                        queryResult = phoneNumberSearch(searchString, officeId, userId);
                        if (queryResult == null) {
                            queryResult = mainSearch(searchString, officeId, userId, userOfficeId, customerLevelIds);
                        }
                    }
                }
            }

        } catch (HibernateSearchException e) {
            throw new PersistenceException(e);
        } catch (SystemException e) {
            throw new PersistenceException(e);
        }

        return queryResult;
    }

    public QueryResult searchGroupClient(final String searchString, final Short userId, boolean isNewGLIMCreation) throws ConfigurationException,
            PersistenceException {
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
        QueryInputs queryInputs = new QueryInputs();
        QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.ACCOUNTSEARCHRESULTS);
        PersonnelBO personnel = getLegacyPersonnelDao().getPersonnel(userId);
        if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
            namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_CLIENT_COUNT_LO;
            namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_CLIENT_LO;
            paramList.add(typeNameValue("Short", "PERSONNEL_ID", userId));
        } 
        else if (isNewGLIMCreation) {
            namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_FOR_GROUP_LOAN_COUNT;
            namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_FOR_GROUP_LOAN;
        }
        else {
            namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_CLIENT_COUNT;
            namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_CLIENT;
        }

        paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
        paramList.add(typeNameValue("String", "SEARCH_STRING", "%" + searchString + "%"));
        paramList.add(typeNameValue("Boolean", "GROUP_LOAN_ALLOWED", ClientRules.getGroupCanApplyLoans() ? Boolean.TRUE
                : Boolean.FALSE));

        String[] aliasNames = { "clientName", "clientId", "groupName", "centerName", "officeName", "globelNo" };
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setPath("org.mifos.accounts.util.helpers.AccountSearchResultsDto");
        queryInputs.setAliasNames(aliasNames);
        queryInputs.setParamList(paramList);
        try {
            queryResult.setQueryInputs(queryInputs);
        } catch (HibernateSearchException e) {
            throw new PersistenceException(e);
        }

        return queryResult;

    }

    public QueryResult searchCustForSavings(final String searchString, final Short userId) throws PersistenceException {
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
        QueryInputs queryInputs = new QueryInputs();
        QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.CUSTOMERSFORSAVINGSACCOUNT);
        PersonnelBO personnel = getLegacyPersonnelDao().getPersonnel(userId);
        if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
            namedQuery[0] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_COUNT;
            namedQuery[1] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS;
            paramList.add(typeNameValue("Short", "PERSONNEL_ID", userId));
        } else {
            namedQuery[0] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO;
            namedQuery[1] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_NOLO;
        }
        paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
        paramList.add(typeNameValue("String", "SEARCH_STRING", "%" + searchString + "%"));

        String[] aliasNames = { "clientName", "clientId", "groupName", "centerName", "officeName", "globelNo" };
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setPath("org.mifos.accounts.util.helpers.AccountSearchResultsDto");
        queryInputs.setAliasNames(aliasNames);
        queryInputs.setParamList(paramList);
        try {
            queryResult.setQueryInputs(queryInputs);
        } catch (HibernateSearchException e) {
            throw new PersistenceException(e);
        }

        return queryResult;

    }

    private QueryResult mainSearch(final String searchString, final Short officeId, final Short userId,
            final Short userOfficeId, final Map<Short, Boolean> customerLevelIds) throws PersistenceException, HibernateSearchException {
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
        for (CustomerLevel customerLevel : CustomerLevel.values()) {
            paramList.add(typeNameValue("Short", customerLevel.toString() + "_SEARCH", customerLevelIds.get(customerLevel.getValue())));
        }
        QueryInputs queryInputs = setQueryInputsValues(namedQuery, paramList);
        QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
        if (officeId.shortValue() != 0) {
            namedQuery[0] = NamedQueryConstants.CUSTOMER_SEARCH_COUNT;
            namedQuery[1] = NamedQueryConstants.CUSTOMER_SEARCH;
            paramList.add(typeNameValue("Short", "OFFICEID", officeId));

        } else {
            namedQuery[0] = NamedQueryConstants.CUSTOMER_SEARCH_COUNT_NOOFFICEID;
            namedQuery[1] = NamedQueryConstants.CUSTOMER_SEARCH_NOOFFICEID;
            paramList.add(typeNameValue("String", "OFFICE_SEARCH_ID", new OfficePersistence().getOffice(userOfficeId)
                    .getSearchId()
                    + "%"));
        }
        paramList.add(typeNameValue("String", "SEARCH_STRING", "%" + searchString + "%"));
        if (searchString.contains(" ")) {
            List<String> words = new ArrayList<String>(Arrays.asList(searchString.split(" +")));
            // we support up to 3 words for client search, so join more words with spaces or fill with empty
            // strings to get exactly 3 words
            if (words.size() > 3) {
                for (int i = 3; i < words.size(); ++i) {
                    words.set(2, words.get(2) + " " + words.get(i));
                }
                words = words.subList(0, 3);
            } else if (words.size() < 3) {
                int elementsToAdd = 3 - words.size();
                for (int i = 0; i < elementsToAdd; ++i) {
                    words.add("");
                }
            }

            paramList.add(typeNameValue("String", "SEARCH_STRING1", "%"+words.get(0)+"%"));
            paramList.add(typeNameValue("String", "SEARCH_STRING2", "%"+words.get(1)+"%"));
            paramList.add(typeNameValue("String", "SEARCH_STRING3", "%"+words.get(2)+"%"));
        } else {
            paramList.add(typeNameValue("String", "SEARCH_STRING1", searchString));
            paramList.add(typeNameValue("String", "SEARCH_STRING2", ""));
            paramList.add(typeNameValue("String", "SEARCH_STRING3", ""));
        }
        setParams(paramList, userId);
        queryResult.setQueryInputs(queryInputs);
        return queryResult;

    }

    private void setParams(final List<Param> paramList, final Short userId) throws PersistenceException {
        paramList.add(typeNameValue("Short", "USERID", userId));
        paramList.add(typeNameValue("Short", "LOID", PersonnelLevel.LOAN_OFFICER.getValue()));
        paramList.add(typeNameValue("Short", "USERLEVEL_ID", getLegacyPersonnelDao().getPersonnel(userId)
                .getLevelEnum().getValue()));
    }

    private String[] getAliasNames() {
        String[] aliasNames = { "customerId", "centerName", "centerGlobalCustNum", "customerType", "branchGlobalNum",
                "branchName", "loanOfficerName", "loanOffcerGlobalNum", "customerStatus", "groupName",
                "groupGlobalCustNum", "clientName", "clientGlobalCustNum", "loanGlobalAccountNumber" };

        return aliasNames;

    }

    private QueryInputs setQueryInputsValues(final String[] namedQuery, final List<Param> paramList) {
        QueryInputs queryInputs = new QueryInputs();
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setParamList(paramList);
        queryInputs.setPath("org.mifos.customers.business.CustomerSearchDto");
        queryInputs.setAliasNames(getAliasNames());
        return queryInputs;

    }

    private QueryResult idSearch(final String searchString, final Short officeId, final Short userId)
            throws HibernateSearchException, SystemException, PersistenceException {
        if (!isCustomerExist(searchString)) {
            return null;
        }
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
        QueryInputs queryInputs = new QueryInputs();
        String[] Names = { "customerId", "centerName", "centerGlobalCustNum", "customerType", "branchGlobalNum",
                "branchName", "loanOfficerName", "loanOffcerGlobalNum", "customerStatus", "groupName",
                "groupGlobalCustNum", "clientName", "clientGlobalCustNum", "loanGlobalAccountNumber" };
        QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
        queryInputs.setPath("org.mifos.customers.business.CustomerSearchDto");
        queryInputs.setAliasNames(Names);
        queryResult.setQueryInputs(queryInputs);
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setParamList(paramList);
        PersonnelBO personnel = getLegacyPersonnelDao().getPersonnel(userId);

        if (officeId != null && officeId.shortValue() == 0) {
            namedQuery[0] = NamedQueryConstants.CUSTOMER_ID_SEARCH_NOOFFICEID_COUNT;
            namedQuery[1] = NamedQueryConstants.CUSTOMER_ID_SEARCH_NOOFFICEID;
            if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
                paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId()));
            } else {
                paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
            }
        } else {
            paramList.add(typeNameValue("Short", "OFFICEID", officeId));
            if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
                paramList.add(typeNameValue("String", "ID", personnel.getPersonnelId()));
                namedQuery[0] = NamedQueryConstants.CUSTOMER_ID_SEARCH_COUNT;
                namedQuery[1] = NamedQueryConstants.CUSTOMER_ID_SEARCH;
            } else {
                paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
                namedQuery[0] = NamedQueryConstants.CUSTOMER_ID_SEARCH_COUNT_NONLO;
                namedQuery[1] = NamedQueryConstants.CUSTOMER_ID_SEARCH_NONLO;
            }

        }

        paramList.add(typeNameValue("String", "SEARCH_STRING", searchString));

        return queryResult;

    }

    private QueryResult governmentIdSearch(final String searchString, final Short officeId, final Short userId)
            throws HibernateSearchException, SystemException, PersistenceException {

        if (!isCustomerExistWithGovernmentId(searchString)) {
            return null;
        }

        SearchTemplate template = new SearchTemplate(searchString, officeId, userId);

        template.queryNoOfficeCount = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID_COUNT;
        template.queryNoOffice = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID;

        template.queryNormalCount = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT;
        template.queryNormal = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH;

        template.queryNonOfficerCount = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT_NONLO;
        template.queryNonOfficer = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_NONLO;

        return template.doSearch();
    }

   private QueryResult phoneNumberSearch(final String searchString, final Short officeId, final Short userId)
            throws HibernateSearchException, SystemException, PersistenceException {

        String phoneNumberWithStrippedNonnumerics = MifosStringUtils.removeNondigits(searchString);
        if (phoneNumberWithStrippedNonnumerics.isEmpty()) {
            return null;
        }
        List<CustomerDto> customersWithThisPhoneNumber = new CustomerDaoHibernate((new GenericDaoHibernate())).
                findCustomersWithGivenPhoneNumber(phoneNumberWithStrippedNonnumerics);
        if (customersWithThisPhoneNumber == null || customersWithThisPhoneNumber.isEmpty()) {
            return null;
        }
        SearchTemplate template = new SearchTemplate(phoneNumberWithStrippedNonnumerics, officeId, userId);

        template.queryNoOfficeCount = NamedQueryConstants.CUSTOMER_PHONE_SEARCH_NOOFFICEID_COUNT;
        template.queryNoOffice = NamedQueryConstants.CUSTOMER_PHONE_SEARCH_NOOFFICEID;

        template.queryNormalCount = NamedQueryConstants.CUSTOMER_PHONE_SEARCH_COUNT;
        template.queryNormal = NamedQueryConstants.CUSTOMER_PHONE_SEARCH;

        template.queryNonOfficerCount = NamedQueryConstants.CUSTOMER_PHONE_SEARCH_COUNT_NONLO;
        template.queryNonOfficer = NamedQueryConstants.CUSTOMER_PHONE_SEARCH_NONLO;

        return template.doSearch();
   }

    private boolean isCustomerExist(final String globalCustNum) throws PersistenceException {

        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("globalCustNum", globalCustNum);
        Integer count = new Long((Long) execUniqueResultNamedQuery(
                NamedQueryConstants.CUSTOMER_FIND_COUNT_BY_SYSTEM_ID, queryParameters)).intValue();
        return count != null && count > 0 ? true : false;

    }

    private boolean isCustomerExistWithGovernmentId(final String governmentId) throws PersistenceException {

        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("governmentId", governmentId);
        Integer count = new Long((Long) execUniqueResultNamedQuery(
                NamedQueryConstants.CUSTOMER_FIND_COUNT_BY_GOVERNMENT_ID, queryParameters)).intValue();
        return count != null && count > 0 ? true : false;

    }

    private void initializeCustomer(final CustomerBO customer) {
        customer.getGlobalCustNum();
        customer.getOffice().getOfficeId();
        customer.getOffice().getOfficeName();
        customer.getCustomerLevel().getId();
        customer.getDisplayName();
        if (customer.getParentCustomer() != null) {
            customer.getParentCustomer().getGlobalCustNum();
            customer.getParentCustomer().getCustomerId();
            customer.getParentCustomer().getCustomerLevel().getId();
            if (customer.getParentCustomer().getParentCustomer() != null) {
                customer.getParentCustomer().getParentCustomer().getGlobalCustNum();
                customer.getParentCustomer().getParentCustomer().getCustomerId();
                customer.getParentCustomer().getParentCustomer().getCustomerLevel().getId();
            }
        }
    }

    protected List<CustomerBO> getChildrenOtherThanClosed(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED,
                queryParameters);
        return queryResult;
    }

    protected List<CustomerBO> getChildrenOtherThanClosedAndCancelled(final String parentSearchId,
            final Short parentOfficeId, final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(
                NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED, queryParameters);
        return queryResult;
    }

    protected List<CustomerBO> getAllChildren(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL,
                queryParameters);
        return queryResult;
    }

    public List<CustomerBO> getActiveAndOnHoldChildren(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ACTIVE_AND_ONHOLD_CHILDREN,
                queryParameters);
        return queryResult;
    }

    public List<CustomerBO> getChildren(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel, final ChildrenStateType childrenStateType) throws PersistenceException {
        if (childrenStateType.equals(ChildrenStateType.OTHER_THAN_CLOSED)) {
            return getChildrenOtherThanClosed(parentSearchId, parentOfficeId, childrenLevel);
        }
        if (childrenStateType.equals(ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED)) {
            return getChildrenOtherThanClosedAndCancelled(parentSearchId, parentOfficeId, childrenLevel);
        }
        if (childrenStateType.equals(ChildrenStateType.ALL)) {
            return getAllChildren(parentSearchId, parentOfficeId, childrenLevel);
        }
        if (childrenStateType.equals(ChildrenStateType.ACTIVE_AND_ONHOLD)) {
            return getActiveAndOnHoldChildren(parentSearchId, parentOfficeId, childrenLevel);
        }
        return null;
    }

    public List<SavingsBO> retrieveSavingsAccountForCustomer(final Integer customerId) throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER, queryParameters);
    }

    public List<CustomerStatusEntity> getCustomerStates(final Short optionalFlag) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OPTIONAL_FLAG", optionalFlag);
        List<CustomerStatusEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATES,
                queryParameters);
        return queryResult;
    }

    public List<AccountBO> retrieveAccountsUnderCustomer(final String searchId, final Short officeId,
            final Short accountTypeId) throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING1", searchId);
        queryParameters.put("SEARCH_STRING2", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("ACCOUNT_TYPE_ID", accountTypeId);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_CUSTOMER, queryParameters);
    }

    public List<CustomerBO> getAllChildrenForParent(final String searchId, final Short officeId,
            final Short customerLevelId) throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("LEVEL_ID", customerLevelId);
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_CHILDREN, queryParameters);
        return queryResult;
    }

    @SuppressWarnings("unchecked")
    public List<CustomerCheckListBO> getStatusChecklist(final Short statusId, final Short customerLevelId)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CHECKLIST_STATUS", CheckListConstants.STATUS_ACTIVE);
        queryParameters.put("STATUS_ID", statusId);
        queryParameters.put("LEVEL_ID", customerLevelId);
        List<CustomerCheckListBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATE_CHECKLIST,queryParameters);
        return queryResult;
    }

    public int getCustomerCountForOffice(final CustomerLevel customerLevel, final Short officeId)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", customerLevel.getValue());
        queryParameters.put("OFFICE_ID", officeId);
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_COUNT_FOR_OFFICE, queryParameters);
        return getCountFromQueryResult(queryResult);
    }

    /**
     * Get a list of the search ids under a given office which are of the form 1.x (eg. 1.23)
     * and return the largest suffix of those returned
     * @param customerLevel whether to search for client/group/center
     * @param officeId id of the office to search under
     * @return
     * @throws PersistenceException
     */
    public int getMaxSearchIdSuffix(final CustomerLevel customerLevel, final Short officeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", customerLevel.getValue());
        queryParameters.put("OFFICE_ID", officeId);
        List<String> queryResult = executeNamedQuery(NamedQueryConstants.GET_SEARCH_IDS_FOR_OFFICE, queryParameters);
        int maxValue = 0;
        for (String searchId: queryResult) {
            if (searchId.startsWith("1.") && searchId.lastIndexOf('.') == 1) {
                int suffixValue = Integer.parseInt(searchId.substring(2));
                if (suffixValue > maxValue) {
                    maxValue = suffixValue;
                }
            }
        }
        return maxValue;
    }

    public List<CustomerStatusEntity> retrieveAllCustomerStatusList(final Short levelId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", levelId);
        List<CustomerStatusEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATUS_LIST,
                queryParameters);
        for (CustomerStatusEntity customerStatus : queryResult) {
            for (CustomerStatusFlagEntity customerStatusFlagEntity : customerStatus.getFlagSet()) {
                Hibernate.initialize(customerStatusFlagEntity);
                Hibernate.initialize(customerStatusFlagEntity.getNames());
            }
            Hibernate.initialize(customerStatus.getLookUpValue());
        }
        return queryResult;
    }

    public QueryResult getAllCustomerNotes(final Integer customerId) throws PersistenceException {
        QueryResult notesResult = null;
        try {
            Session session = null;
            notesResult = QueryFactory.getQueryResult("NotesSearch");
            session = StaticHibernateUtil.getSessionTL();
            Query query = session.getNamedQuery(NamedQueryConstants.GETALLCUSTOMERNOTES);
            query.setInteger("CUSTOMER_ID", customerId);
            notesResult.executeQuery(query);
        } catch (HibernateProcessException hpe) {
            throw new PersistenceException(hpe);
        } catch (HibernateSearchException hse) {
            throw new PersistenceException(hse);
        }
        return notesResult;
    }

    public CustomerPictureEntity retrievePicture(final Integer customerId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_PICTURE, queryParameters);

        return (CustomerPictureEntity) queryResult.get(0);
    }

    public List<AccountBO> getAllClosedAccount(final Integer customerId, final Short accountTypeId)
            throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        queryParameters.put("accountTypeId", accountTypeId);
        List queryResult = executeNamedQuery("customer.viewallclosedaccounts", queryParameters);
        return queryResult;
    }

    public Short getLoanOfficerForCustomer(final Integer customerId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_LO_FOR_CUSTOMER, queryParameters);
        return (Short) queryResult.get(0);
    }

    private void updateAccountsForOneCustomer(final Integer customerId, final Short parentLO,
            final Connection connection) throws Exception {

        Statement statement = connection.createStatement();
        String sql = "update account " + " set personnel_id = " + parentLO.shortValue()
                + " where account.customer_id = " + customerId.intValue();
        statement.executeUpdate(sql);
        statement.close();
    }

    public void deleteCustomerMeeting(final CustomerBO customer) throws PersistenceException {
        delete(customer.getCustomerMeeting());
    }

    public void deleteMeeting(final MeetingBO meeting) throws PersistenceException {
        if (meeting != null) {
            delete(meeting);
        }
    }

    public List<AccountBO> getCustomerAccountsForFee(final Short feeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FEEID", feeId);
        return executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_ACCOUNTS_FOR_FEE, queryParameters);
    }

    public CustomerAccountBO getCustomerAccountWithAccountActionsInitialized(final Integer accountId)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("accountId", accountId);
        List obj = executeNamedQuery("accounts.retrieveCustomerAccountWithAccountActions", queryParameters);
        Object[] obj1 = (Object[]) obj.get(0);
        return (CustomerAccountBO) obj1[0];
    }

    public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(final Integer accountId,
            final Date transactionDate) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", accountId);
        queryParameters.put("ACTION_DATE", transactionDate);
        queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
        return executeNamedQuery(NamedQueryConstants.CUSTOMER_ACCOUNT_ACTIONS_DATE, queryParameters);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerBO> getActiveCentersUnderUser(final PersonnelBO personnel) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.CENTER.getValue());
        queryParameters.put(CustomerSearchConstants.CENTER_ACTIVE, CustomerStatus.CENTER_ACTIVE.getValue());
        return executeNamedQuery(NamedQueryConstants.SEARCH_CENTERS_FOR_LOAN_OFFICER, queryParameters);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerBO> getGroupsUnderUser(final PersonnelBO personnel) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.GROUP.getValue());
        return executeNamedQuery(NamedQueryConstants.SEARCH_GROUPS_FOR_LOAN_OFFICER, queryParameters);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerBO> getCustomersByLevelId(final Short customerLevelId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerLevelId", customerLevelId);

        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMERS_BY_LEVELID, queryParameters);
        return queryResult;

    }

    public Integer getActiveClientCountForOffice(final OfficeBO office) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICE, office);
        return getCountFromQueryResult(executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CLIENTS_COUNT_UNDER_OFFICE,
                params));
    }

    public Integer getVeryPoorClientCountForOffice(final OfficeBO office) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICE, office);
        return getCountFromQueryResult(executeNamedQuery(NamedQueryConstants.GET_VERY_POOR_CLIENTS_COUNT_UNDER_OFFICE,
                params));
    }

    public Integer getActiveOrHoldClientCountForOffice(final OfficeBO office) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICE, office);
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE, params));
    }

    public Integer getVeryPoorActiveOrHoldClientCountForOffice(final OfficeBO office) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICE, office);
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_VERY_POOR_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE, params));
    }

    public Integer getActiveBorrowersCountForOffice(final OfficeBO office) throws PersistenceException {
        List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE, office);
        CollectionUtils.filter(clients, CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS);
        return clients.size();
    }

    private List<ClientBO> runQueryForOffice(final String queryName, final OfficeBO office) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICE, office);
        return executeNamedQuery(queryName, params);
    }

    public Integer getVeryPoorActiveBorrowersCountForOffice(final OfficeBO office) throws PersistenceException {
        List<ClientBO> veryPoorActiveBorrowers = runQueryForOffice(
                NamedQueryConstants.GET_VERY_POOR_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE, office);
        CollectionUtils.filter(veryPoorActiveBorrowers, CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS);
        return veryPoorActiveBorrowers.size();
    }

    public Integer getCustomerReplacementsCountForOffice(final OfficeBO office, final Short fieldId,
            final String fieldValue) throws PersistenceException {
        List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE,
                office);
        CollectionUtils.filter(clients, new FieldStatePredicate(fieldId, fieldValue));
        return clients.size();
    }

    public Integer getVeryPoorReplacementsCountForOffice(final OfficeBO office, final Short fieldId,
            final String fieldValue) throws PersistenceException {
        List<ClientBO> veryPoorClients = runQueryForOffice(NamedQueryConstants.GET_VERY_POOR_CLIENTS_UNDER_OFFICE,
                office);
        CollectionUtils.filter(veryPoorClients, new FieldStatePredicate(fieldId, fieldValue));
        return veryPoorClients.size();
    }

    public Integer getDormantClientsCountByLoanAccountForOffice(final OfficeBO office, final Integer loanCyclePeriod)
            throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE, populateDormantQueryParams(
                        office, loanCyclePeriod)));
    }

    public Integer getVeryPoorDormantClientsCountByLoanAccountForOffice(final OfficeBO office,
            final Integer loanCyclePeriod) throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE,
                populateDormantQueryParams(office, loanCyclePeriod)));
    }

    public Integer getDormantClientsCountBySavingAccountForOffice(final OfficeBO office, final Integer loanCyclePeriod)
            throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE, populateDormantQueryParams(
                        office, loanCyclePeriod)));
    }

    public Integer getVeryPoorDormantClientsCountBySavingAccountForOffice(final OfficeBO office,
            final Integer loanCyclePeriod) throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE,
                populateDormantQueryParams(office, loanCyclePeriod)));
    }

    public Money getTotalAmountForGroup(final Integer groupId, final AccountState accountState)
            throws PersistenceException {
        MifosCurrency currency = getCurrencyForTotalAmountForGroup(groupId, accountState);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customerId", groupId);
        params.put("accountState", accountState.getValue());
        BigDecimal amount = getCalculateValueFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_TOTAL_AMOUNT_FOR_GROUP, params));
        Money totalAmount = new Money(currency, amount);
        return totalAmount;
    }

    private MifosCurrency getCurrencyForTotalAmountForGroup(final Integer groupId, final AccountState accountState)
            throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("customerId", groupId);
        params.put("accountState", accountState.getValue());
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_LOAN_SUMMARY_CURRENCIES_FOR_GROUP, params);
        if (queryResult.size() > 1) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
        if (queryResult.size() == 0) {
            // if we found no results, then return the default currency
            return Money.getDefaultCurrency();
        }
        Short currencyId = (Short) queryResult.get(0);
        return AccountingRules.getCurrencyByCurrencyId(currencyId);
    }

    public List<BasicGroupInfo> getAllBasicGroupInfo() throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_ALL_BASIC_GROUP_INFO, params);
    }

    public Money getTotalAmountForAllClientsOfGroup(final Short officeId, final AccountState accountState,
            final String searchIdString) throws PersistenceException {
        MifosCurrency currency = getCurrencyForTotalAmountForAllClientsOfGroup(officeId, accountState, searchIdString);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("officeId", officeId);
        params.put("accountState", accountState.getValue());
        params.put("searchId", searchIdString);

        BigDecimal amount = getCalculateValueFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_TOTAL_AMOUNT_FOR_ALL_CLIENTS_OF_GROUP, params));
        Money totalAmount = new Money(currency, amount);
        return totalAmount;
    }

    private MifosCurrency getCurrencyForTotalAmountForAllClientsOfGroup(final Short officeId,
            final AccountState accountState, final String searchIdString) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("officeId", officeId);
        params.put("accountState", accountState.getValue());
        params.put("searchId", searchIdString);
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_LOAN_SUMMARY_CURRENCIES_FOR_ALL_CLIENTS_OF_GROUP,
                params);
        if (queryResult.size() > 1) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
        if (queryResult.size() == 0) {
            // if we found no results, then return the default currency
            return Money.getDefaultCurrency();
        }
        Short currencyId = (Short) queryResult.get(0);
        return AccountingRules.getCurrencyByCurrencyId(currencyId);
    }

    private HashMap<String, Object> populateDormantQueryParams(final OfficeBO office, final Integer loanCyclePeriod) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CustomerSearchConstants.OFFICEID, office.getOfficeId());
        params.put("loanCyclePeriod", loanCyclePeriod);
        params.put(QueryParamConstants.CUSTOMER_LEVEL_ID, CustomerLevel.CLIENT);
        return params;
    }

    public Integer getDropOutClientsCountForOffice(final OfficeBO office) throws PersistenceException {
        return getCountFromQueryResult(runQueryForOffice(NamedQueryConstants.GET_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE,
                office));
    }

    public Integer getVeryPoorDropOutClientsCountForOffice(final OfficeBO office) throws PersistenceException {
        return getCountFromQueryResult(runQueryForOffice(
                NamedQueryConstants.GET_VERY_POOR_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE, office));
    }

    public Integer getOnHoldClientsCountForOffice(final OfficeBO office) throws PersistenceException {
        return getCountFromQueryResult(runQueryForOffice(NamedQueryConstants.GET_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE,
                office));
    }

    public Integer getVeryPoorOnHoldClientsCountForOffice(final OfficeBO office) throws PersistenceException {
        return getCountFromQueryResult(runQueryForOffice(
                NamedQueryConstants.GET_VERY_POOR_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE, office));
    }

    public Integer getActiveSaversCountForOffice(final OfficeBO office) throws PersistenceException {
        List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_ACTIVE_SAVERS_COUNT_UNDER_OFFICE, office);
        CollectionUtils.filter(clients, CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT);
        return clients.size();
    }

    public Integer getVeryPoorActiveSaversCountForOffice(final OfficeBO office) throws PersistenceException {
        List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_VERY_POOR_ACTIVE_SAVERS_COUNT_UNDER_OFFICE,
                office);
        CollectionUtils.filter(clients, CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT);
        return clients.size();
    }

    static private class FieldStatePredicate implements Predicate {

        private final Short fieldId;
        private final String fieldValue;

        public FieldStatePredicate(final Short fieldId, final String fieldValue) {
            this.fieldId = fieldId;
            this.fieldValue = fieldValue;
        }

        @Override
		public boolean evaluate(final Object object) {
            Set<CustomerCustomFieldEntity> customFields = ((ClientBO) object).getCustomFields();
            return CollectionUtils.exists(customFields, new Predicate() {
                @Override
				public boolean evaluate(final Object object) {
                    CustomerCustomFieldEntity field = (CustomerCustomFieldEntity) object;
                    return fieldValue.equals(field.getFieldValue()) && fieldId.equals(field.getFieldId());
                }
            });
        }
    }

    public CollectionSheetCustomerDto findCustomerWithNoAssocationsLoaded(final Integer customerId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);

        return execUniqueResultNamedQueryWithResultTransformer(
                "findCustomerWithNoAssocationsLoaded", queryParameters, CollectionSheetCustomerDto.class);

    }

    @SuppressWarnings("unchecked")
    public List<LoanDetailDto> getLoanDetailDto(Integer customerId, UserContext userContext)
            throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        List<Object[]> queryResult = executeNamedQuery("Customer.getLoanDetailDto", queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<LoanDetailDto> loanDetails = new ArrayList<LoanDetailDto>();
        String globalAccountNum;
        String prdOfferingName;
        Short accountStateId;
        String accountStateName;
        Money outstandingBalance;
        Money totalAmountDue;
        String lookupName;
        Short currency;

        MifosCurrency mifosCurrency = Money.getDefaultCurrency();

        for (Object[] loanDetail : queryResult) {
            globalAccountNum = (String) loanDetail[0];
            prdOfferingName = (String) loanDetail[1];
            accountStateId = (Short) loanDetail[2];
            lookupName = (String) loanDetail[3];
            accountStateName = ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupName);
            // TODO - use default currency or retrieved currency?
            currency = (Short) loanDetail[4];
            outstandingBalance = new Money(mifosCurrency, (BigDecimal) loanDetail[5]);
            // TODO
            totalAmountDue = new Money(mifosCurrency, "7.7");

            loanDetails.add(new LoanDetailDto(globalAccountNum, prdOfferingName, accountStateId, accountStateName,
                    outstandingBalance.toString(), totalAmountDue.toString()));
        }
        return loanDetails;
    }

    protected String localizedMessageLookup(String key) {
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(key);
    }

}
