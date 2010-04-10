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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerPerformanceHistoryView;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.business.CustomerView;
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
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerListDto;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.LoanCycleCounter;
import org.mifos.customers.util.helpers.Param;
import org.mifos.customers.util.helpers.QueryParamConstants;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.Money;

public class CustomerPersistence extends Persistence {

    private static final Predicate CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS = new Predicate() {
        public boolean evaluate(final Object object) {
            Set<AccountBO> accounts = ((ClientBO) object).getAccounts();
            return CollectionUtils.exists(accounts, new Predicate() {
                public boolean evaluate(final Object object) {
                    AccountStateEntity accountState = ((AccountBO) object).getAccountState();
                    return new AccountStateEntity(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING).sameId(accountState);
                }
            });
        }
    };

    private static final Predicate CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT = new Predicate() {
        public boolean evaluate(final Object arg0) {
            Set<AccountBO> accounts = ((ClientBO) arg0).getAccounts();
            return CollectionUtils.exists(accounts, new Predicate() {
                public boolean evaluate(final Object arg0) {
                    AccountBO account = (AccountBO) arg0;
                    return AccountTypes.SAVINGS_ACCOUNT.getValue().equals(account.getAccountType().getAccountTypeId())
                            && new AccountStateEntity(AccountState.SAVINGS_ACTIVE).sameId(account.getAccountState());
                }
            });
        }
    };

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

    public List<CustomerView> getChildrenForParent(final Integer customerId, final String searchId, final Short officeId)
            throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List<CustomerView> queryResult = executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CHILDREN_FORPARENT,
                queryParameters);
        return queryResult;
    }

    public List<Integer> getChildrenForParent(final String searchId, final Short officeId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.GET_CHILDREN_FOR_PARENT, queryParameters);
        return queryResult;
    }

    public List<CustomerView> getActiveParentList(final Short personnelId, final Short customerLevelId, final Short officeId)
            throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("personnelId", personnelId);
        queryParameters.put("customerLevelId", customerLevelId);
        queryParameters.put("officeId", officeId);

        List<CustomerView> queryResult = executeNamedQuery(NamedQueryConstants.GET_PARENTCUSTOMERS_FOR_LOANOFFICER,
                queryParameters);
        return queryResult;

    }

    public Date getLastMeetingDateForCustomer(final Integer customerId) throws PersistenceException {
        Date meetingDate = null;
        Date actionDate = new DateTimeService().getCurrentJavaSqlDate();
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        queryParameters.put("ACTION_DATE", actionDate);
        meetingDate =  (Date) execUniqueResultNamedQuery(
                NamedQueryConstants.GET_LAST_MEETINGDATE_FOR_CUSTOMER, queryParameters);
        return meetingDate;
    }

    /**
     * @deprecated use {@link CustomerDao#findCustomerById(Integer)}
     */
    @Deprecated
    public CustomerBO getCustomer(final Integer customerId) throws PersistenceException {
        return (CustomerBO) getPersistentObject(CustomerBO.class, customerId);
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

    public QueryResult search(final String searchString, final Short officeId, final Short userId, final Short userOfficeId)
            throws PersistenceException {

        QueryResult queryResult = null;

        try {

            queryResult = new AccountPersistence().search(searchString, officeId);
            if (queryResult == null) {
                queryResult = idSearch(searchString, officeId, userId);
                if (queryResult == null) {
                    queryResult = governmentIdSearch(searchString, officeId, userId);
                    if (queryResult == null) {
                        queryResult = mainSearch(searchString, officeId, userId, userOfficeId);
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

    public QueryResult searchGroupClient(final String searchString, final Short userId) throws ConfigurationException,
            PersistenceException {
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
        QueryInputs queryInputs = new QueryInputs();
        QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.ACCOUNTSEARCHRESULTS);
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);
        if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
            namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_CLIENT_COUNT_LO;
            namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_CLIENT_LO;
            paramList.add(typeNameValue("Short", "PERSONNEL_ID", userId));
        } else {
            namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_CLIENT_COUNT;
            namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_CLIENT;
        }

        paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
        paramList.add(typeNameValue("String", "SEARCH_STRING", searchString + "%"));
        paramList.add(typeNameValue("Boolean", "GROUP_LOAN_ALLOWED", ClientRules.getGroupCanApplyLoans() ? Boolean.TRUE
                : Boolean.FALSE));

        String[] aliasNames = { "clientName", "clientId", "groupName", "centerName", "officeName", "globelNo" };
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setPath("org.mifos.accounts.util.helpers.AccountSearchResults");
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
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);
        if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
            namedQuery[0] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_COUNT;
            namedQuery[1] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS;
            paramList.add(typeNameValue("Short", "PERSONNEL_ID", userId));
        } else {
            namedQuery[0] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO;
            namedQuery[1] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_NOLO;
        }
        paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
        paramList.add(typeNameValue("String", "SEARCH_STRING", searchString + "%"));

        String[] aliasNames = { "clientName", "clientId", "groupName", "centerName", "officeName", "globelNo" };
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setPath("org.mifos.accounts.util.helpers.AccountSearchResults");
        queryInputs.setAliasNames(aliasNames);
        queryInputs.setParamList(paramList);
        try {
            queryResult.setQueryInputs(queryInputs);
        } catch (HibernateSearchException e) {
            throw new PersistenceException(e);
        }

        return queryResult;

    }

    private QueryResult mainSearch(final String searchString, final Short officeId, final Short userId, final Short userOfficeId)
            throws PersistenceException, HibernateSearchException {
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
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
        paramList.add(typeNameValue("String", "SEARCH_STRING", searchString + "%"));
        if (searchString.contains(" ")) {
            paramList.add(typeNameValue("String", "SEARCH_STRING1", searchString
                    .substring(0, searchString.indexOf(" "))));
            paramList.add(typeNameValue("String", "SEARCH_STRING2", searchString.substring(
                    searchString.indexOf(" ") + 1, searchString.length())));
        } else {
            paramList.add(typeNameValue("String", "SEARCH_STRING1", searchString));
            paramList.add(typeNameValue("String", "SEARCH_STRING2", ""));
        }
        setParams(paramList, userId);
        queryResult.setQueryInputs(queryInputs);
        return queryResult;

    }

    private void setParams(final List<Param> paramList, final Short userId) throws PersistenceException {
        paramList.add(typeNameValue("Short", "USERID", userId));
        paramList.add(typeNameValue("Short", "LOID", PersonnelLevel.LOAN_OFFICER.getValue()));
        paramList.add(typeNameValue("Short", "LEVELID", CustomerLevel.CLIENT.getValue()));
        paramList.add(typeNameValue("Short", "USERLEVEL_ID", new PersonnelPersistence().getPersonnel(userId)
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
        queryInputs.setPath("org.mifos.customers.business.CustomerSearch");
        queryInputs.setAliasNames(getAliasNames());
        return queryInputs;

    }

    private QueryResult idSearch(final String searchString, final Short officeId, final Short userId) throws HibernateSearchException,
            SystemException, PersistenceException {
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
        queryInputs.setPath("org.mifos.customers.business.CustomerSearch");
        queryInputs.setAliasNames(Names);
        queryResult.setQueryInputs(queryInputs);
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setParamList(paramList);
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);

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

    private QueryResult governmentIdSearch(final String searchString, final Short officeId, final Short userId) throws HibernateSearchException,
            SystemException, PersistenceException {
        if (!isCustomerExistWithGovernmentId(searchString)) {
            return null;
        }
        String[] namedQuery = new String[2];
        List<Param> paramList = new ArrayList<Param>();
        QueryInputs queryInputs = new QueryInputs();
        String[] Names = { "customerId", "centerName", "centerGlobalCustNum", "customerType", "branchGlobalNum",
                "branchName", "loanOfficerName", "loanOffcerGlobalNum", "customerStatus", "groupName",
                "groupGlobalCustNum", "clientName", "clientGlobalCustNum", "loanGlobalAccountNumber" };
        QueryResult queryResult = QueryFactory.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
        queryInputs.setPath("org.mifos.customers.business.CustomerSearch");
        queryInputs.setAliasNames(Names);
        queryResult.setQueryInputs(queryInputs);
        queryInputs.setQueryStrings(namedQuery);
        queryInputs.setParamList(paramList);
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);

        if (officeId != null && officeId.shortValue() == 0) {
            namedQuery[0] = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID_COUNT;
            namedQuery[1] = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_NOOFFICEID;
            if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
                paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId()));
            } else {
                paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
            }
        } else {
            paramList.add(typeNameValue("Short", "OFFICEID", officeId));
            if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
                paramList.add(typeNameValue("String", "ID", personnel.getPersonnelId()));
                namedQuery[0] = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT;
                namedQuery[1] = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH;
            } else {
                paramList.add(typeNameValue("String", "SEARCH_ID", personnel.getOffice().getSearchId() + "%"));
                namedQuery[0] = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_COUNT_NONLO;
                namedQuery[1] = NamedQueryConstants.CUSTOMER_GOVERNMENT_ID_SEARCH_NONLO;
            }

        }

        paramList.add(typeNameValue("String", "SEARCH_STRING", searchString));

        return queryResult;

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

    protected List<CustomerBO> getChildrenOtherThanClosedAndCancelled(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(
                NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED, queryParameters);
        return queryResult;
    }

    protected List<CustomerBO> getAllChildren(final String parentSearchId, final Short parentOfficeId, final CustomerLevel childrenLevel)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL,
                queryParameters);
        return queryResult;
    }

    protected List<CustomerBO> getActiveAndOnHoldChildren(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ACTIVE_AND_ONHOLD_CHILDREN,
                queryParameters);
        return queryResult;
    }

    public List<CustomerBO> getChildren(final String parentSearchId, final Short parentOfficeId, final CustomerLevel childrenLevel,
            final ChildrenStateType childrenStateType) throws PersistenceException {
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

    public CustomerPerformanceHistoryView numberOfMeetings(final boolean isPresent, final Integer customerId)
            throws PersistenceException, InvalidDateException {
        Session session = null;
        Query query = null;
        CustomerPerformanceHistoryView customerPerformanceHistoryView = new CustomerPerformanceHistoryView();
        session = getHibernateUtil().getSessionTL();
        String systemDate = DateUtils.getCurrentDate();
        Date localDate = DateUtils.getLocaleDate(systemDate);
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTime(localDate);
        currentDate.add(currentDate.YEAR, -1);
        Date dateOneYearBefore = new Date(currentDate.getTimeInMillis());
        if (isPresent) {
            query = session.getNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSATTENDED);
            query.setInteger("CUSTOMERID", customerId);
            query.setDate("DATEONEYEARBEFORE", dateOneYearBefore);
            customerPerformanceHistoryView.setMeetingsAttended(new Long((Long) query.uniqueResult()).intValue());
        } else {
            query = session.getNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSMISSED);
            query.setInteger("CUSTOMERID", customerId);
            query.setDate("DATEONEYEARBEFORE", dateOneYearBefore);
            customerPerformanceHistoryView.setMeetingsMissed(new Long((Long) query.uniqueResult()).intValue());
        }
        return customerPerformanceHistoryView;
    }

    public CustomerPerformanceHistoryView getLastLoanAmount(final Integer customerId) throws PersistenceException {
        Query query = null;
        Session session = getHibernateUtil().getSessionTL();
        CustomerPerformanceHistoryView customerPerformanceHistoryView = null;
        if (null != session) {
            query = session.getNamedQuery(NamedQueryConstants.GETLASTLOANAMOUNT);
        }
        query.setInteger("CUSTOMERID", customerId);
        Object obj = query.uniqueResult();
        if (obj != null) {
            customerPerformanceHistoryView = new CustomerPerformanceHistoryView();
            customerPerformanceHistoryView.setLastLoanAmount(query.uniqueResult().toString());
        }
        return customerPerformanceHistoryView;
    }

    public List<CustomerStatusEntity> getCustomerStates(final Short optionalFlag) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OPTIONAL_FLAG", optionalFlag);
        List<CustomerStatusEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATES,
                queryParameters);
        return queryResult;
    }

    public List<Integer> getCustomersWithUpdatedMeetings() throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("updated", YesNoFlag.YES.getValue());
        List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.GET_UPDATED_CUSTOMER_MEETINGS,
                queryParameters);
        return queryResult;
    }

    public List<AccountBO> retrieveAccountsUnderCustomer(final String searchId, final Short officeId, final Short accountTypeId)
            throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING1", searchId);
        queryParameters.put("SEARCH_STRING2", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("ACCOUNT_TYPE_ID", accountTypeId);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_CUSTOMER, queryParameters);
    }

    public List<CustomerBO> getAllChildrenForParent(final String searchId, final Short officeId, final Short customerLevelId)
            throws PersistenceException {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("LEVEL_ID", customerLevelId);
        List<CustomerBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_CHILDREN, queryParameters);
        return queryResult;
    }

    public List<Integer> getCustomers(final Short customerLevelId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", customerLevelId);
        List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_CUSTOMERS, queryParameters);
        return queryResult;
    }

    public List<CustomerCheckListBO> getStatusChecklist(final Short statusId, final Short customerLevelId)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CHECKLIST_STATUS", CheckListConstants.STATUS_ACTIVE);
        queryParameters.put("STATUS_ID", statusId);
        queryParameters.put("LEVEL_ID", customerLevelId);
        List<CustomerCheckListBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATE_CHECKLIST,
                queryParameters);
        return queryResult;
    }

    public int getCustomerCountForOffice(final CustomerLevel customerLevel, final Short officeId) throws PersistenceException {
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


    public List<Integer> getCustomerSearchIds(final Short customerLevelId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", customerLevelId);
        List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_CUSTOMERS, queryParameters);
        return queryResult;
    }

    public List<LoanCycleCounter> fetchLoanCycleCounter(final CustomerBO customerBO) throws PersistenceException {
        if (customerBO.isGroup()) {
            return runLoanCycleQuery(NamedQueryConstants.FETCH_PRODUCT_NAMES_FOR_GROUP, customerBO);
        } else if (customerBO.isClient()) {
            return runLoanCycleQuery(NamedQueryConstants.FETCH_PRODUCT_NAMES_FOR_CLIENT, customerBO);
        }
        return new ArrayList<LoanCycleCounter>();
    }

    private List<LoanCycleCounter> runLoanCycleQuery(final String queryName, final CustomerBO customerBO)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerBO.getCustomerId());
        List<LoanCycleCounter> loanCycleCounters = new ArrayList<LoanCycleCounter>();
        List<Object[]> queryResult = executeNamedQuery(queryName, queryParameters);
        if (null != queryResult && queryResult.size() > 0) {
            for (Object[] objects : queryResult) {
                loanCycleCounters.add(new LoanCycleCounter((String) objects[0], (Integer) objects[1]));
            }
        }
        return loanCycleCounters;
    }

    public List<CustomerStatusEntity> retrieveAllCustomerStatusList(final Short levelId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", levelId);
        List<CustomerStatusEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATUS_LIST,
                queryParameters);
        for (CustomerStatusEntity customerStatus : queryResult) {
            for (CustomerStatusFlagEntity customerStatusFlagEntity : customerStatus.getFlagSet()) {
                initialize(customerStatusFlagEntity);
                initialize(customerStatusFlagEntity.getNames());
            }
            initialize(customerStatus.getLookUpValue());
        }
        return queryResult;
    }

    public QueryResult getAllCustomerNotes(final Integer customerId) throws PersistenceException {
        QueryResult notesResult = null;
        try {
            Session session = null;
            notesResult = QueryFactory.getQueryResult("NotesSearch");
            session = notesResult.getSession();
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

    public List<PersonnelView> getFormedByPersonnel(final Short levelId, final Short officeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", levelId);
        queryParameters.put("officeId", officeId);
        queryParameters.put("statusId", PersonnelConstants.ACTIVE);
        List<PersonnelView> queryResult = executeNamedQuery(NamedQueryConstants.FORMEDBY_LOANOFFICERS_LIST,
                queryParameters);
        return queryResult;
    }

    public CustomerPictureEntity retrievePicture(final Integer customerId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_PICTURE, queryParameters);

        return (CustomerPictureEntity) queryResult.get(0);
    }

    public List<AccountBO> getAllClosedAccount(final Integer customerId, final Short accountTypeId) throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        queryParameters.put("accountTypeId", accountTypeId);
        List queryResult = executeNamedQuery(NamedQueryConstants.VIEWALLCLOSEDACCOUNTS, queryParameters);
        return queryResult;

    }

    public Short getLoanOfficerForCustomer(final Integer customerId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        List queryResult = executeNamedQuery(NamedQueryConstants.GET_LO_FOR_CUSTOMER, queryParameters);
        return (Short) queryResult.get(0);
    }

    public void updateLOsForAllChildren(final Short parentLO, final String parentSearchId, final Short parentOfficeId) {
        String hql = "update CustomerBO customer " + " set customer.personnel.personnelId = :parentLoanOfficer "
                + " where customer.searchId like :parentSearchId" + " and customer.office.officeId = :parentOfficeId";
        Session session = getHibernateUtil().getSessionTL();
        getHibernateUtil().startTransaction();
        Query update = session.createQuery(hql);
        update.setParameter("parentLoanOfficer", parentLO);
        update.setParameter("parentSearchId", parentSearchId + ".%");
        update.setParameter("parentOfficeId", parentOfficeId);
        update.executeUpdate();
    }

    private void updateAccountsForOneCustomer(final Integer customerId, final Short parentLO, final Connection connection)
            throws Exception {

        Statement statement = connection.createStatement();
        String sql = "update account " + " set personnel_id = " + parentLO.shortValue()
                + " where account.customer_id = " + customerId.intValue();
        statement.executeUpdate(sql);
        statement.close();
    }

    /**
     * Update loan officer for all children accounts.
     *
     * This method was introduced for when a center is assigned a new loan
     * officer, and this loan officer needs to be re-assigned not just for the
     * center's groups and clients, but for each account belonging to those
     * customers.
     *
     * Note: Required as to fix issues 1570 and 1804 Note 10/08/2008: direct
     * sqls are used to improve performance (issue 2209)
     *
     * @param parentLO
     *            the parent loan officer
     * @param parentSearchId
     *            the parent search id
     * @param parentOfficeId
     *            the parent office id
     */

    public void updateLOsForAllChildrenAccounts(final Short parentLO, String parentSearchId, final Short parentOfficeId)
            throws Exception {

        if (parentLO == null || parentSearchId == null || parentOfficeId == null) {
            return;
        }

        ResultSet customerIds = null;
        Statement statement = null;
        parentSearchId = parentSearchId + ".%";

        try {
            Connection connection = getHibernateUtil().getSessionTL().connection();
            statement = connection.createStatement();
            String sql = " select customer_id from customer where " + " customer.search_id like '" + parentSearchId
                    + "' and customer.branch_id = " + parentOfficeId.shortValue();
            customerIds = statement.executeQuery(sql);
            if (customerIds != null) {
                while (customerIds.next()) {
                    int customerId = customerIds.getInt("customer_id");
                    updateAccountsForOneCustomer(customerId, parentLO, connection);
                }

            }

        }

        finally {
            if (statement != null) {
                statement.close();
            }
            if (customerIds != null) {
                customerIds.close();
            }
        }

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

    public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(final Integer accountId, final Date transactionDate)
            throws PersistenceException {
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

    public Integer getCustomerReplacementsCountForOffice(final OfficeBO office, final Short fieldId, final String fieldValue)
            throws PersistenceException {
        List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE,
                office);
        CollectionUtils.filter(clients, new FieldStatePredicate(fieldId, fieldValue));
        return clients.size();
    }

    public Integer getVeryPoorReplacementsCountForOffice(final OfficeBO office, final Short fieldId, final String fieldValue)
            throws PersistenceException {
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

    public Integer getVeryPoorDormantClientsCountByLoanAccountForOffice(final OfficeBO office, final Integer loanCyclePeriod)
            throws PersistenceException {
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

    public Integer getVeryPoorDormantClientsCountBySavingAccountForOffice(final OfficeBO office, final Integer loanCyclePeriod)
            throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE,
                populateDormantQueryParams(office, loanCyclePeriod)));
    }

    public Money getTotalAmountForGroup(final Integer groupId, final AccountState accountState) throws PersistenceException {
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
        List queryResult = executeNamedQuery(
                NamedQueryConstants.GET_LOAN_SUMMARY_CURRENCIES_FOR_GROUP, params);
        if (queryResult.size() > 1) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
        if (queryResult.size() == 0) {
            // if we found no results, then return the default currency
            return Money.getDefaultCurrency();
        }
        Short currencyId = (Short)queryResult.get(0);
        return AccountingRules.getCurrencyByCurrencyId(currencyId);
    }

    public List<BasicGroupInfo> getAllBasicGroupInfo() throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_ALL_BASIC_GROUP_INFO, params);
    }

    public Money getTotalAmountForAllClientsOfGroup(final Short officeId, final AccountState accountState, final String searchIdString)
            throws PersistenceException {
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

    private MifosCurrency getCurrencyForTotalAmountForAllClientsOfGroup(final Short officeId, final AccountState accountState, final String searchIdString)
    throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("officeId", officeId);
        params.put("accountState", accountState.getValue());
        params.put("searchId", searchIdString);
        List queryResult = executeNamedQuery(
                NamedQueryConstants.GET_LOAN_SUMMARY_CURRENCIES_FOR_ALL_CLIENTS_OF_GROUP, params);
        if (queryResult.size() > 1) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
        if (queryResult.size() == 0) {
            // if we found no results, then return the default currency
            return Money.getDefaultCurrency();
        }
        Short currencyId = (Short)queryResult.get(0);
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

        public boolean evaluate(final Object object) {
            Set<CustomerCustomFieldEntity> customFields = ((ClientBO) object).getCustomFields();
            return CollectionUtils.exists(customFields, new Predicate() {
                public boolean evaluate(final Object object) {
                    CustomerCustomFieldEntity field = (CustomerCustomFieldEntity) object;
                    return fieldValue.equals(field.getFieldValue()) && fieldId.equals(field.getFieldId());
                }
            });
        }
    }

    public Integer getActiveAndOnHoldChildrenCount(final String parentSearchId, final Short parentOfficeId,
            final CustomerLevel childrenLevel) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
        queryParameters.put("OFFICE_ID", parentOfficeId);
        queryParameters.put("LEVEL_ID", childrenLevel.getValue());
        Integer count = ((Long) execUniqueResultNamedQuery(NamedQueryConstants.GET_ACTIVE_AND_ONHOLD_CHILDREN_COUNT,
                queryParameters)).intValue();
        return count;
    }

    public Money retrieveTotalLoan(final String searchId, final Short officeId) throws PersistenceException {
        return retrieveTotalForQuery(NamedQueryConstants.RETRIEVE_TOTAL_LOAN_FOR_CUSTOMER, searchId, officeId);
    }

    public Money retrieveTotalSavings(final String searchId, final Short officeId) throws PersistenceException {
        return retrieveTotalForQuery(NamedQueryConstants.RETRIEVE_TOTAL_SAVINGS_FOR_CUSTOMER, searchId, officeId);
    }

    private Money retrieveTotalForQuery(String query, final String searchId, final Short officeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SEARCH_STRING1", searchId);
        queryParameters.put("SEARCH_STRING2", searchId + ".%");
        queryParameters.put("OFFICE_ID", officeId);
        List queryResult = executeNamedQuery(
                query, queryParameters);

        if (queryResult.size() > 1) {
            throw new CurrencyMismatchException(ExceptionConstants.ILLEGALMONEYOPERATION);
        }
        if (queryResult.size() == 0) {
            // if we found no results, then return zero using the default currency
            return new Money(Money.getDefaultCurrency(),"0.0");
        }
        Integer currencyId = (Integer)((Object[])queryResult.get(0))[0];
        MifosCurrency currency =  AccountingRules.getCurrencyByCurrencyId(currencyId.shortValue());

        BigDecimal total = (BigDecimal)((Object[])queryResult.get(0))[1];

        return new Money(currency, total);
    }


    public CollectionSheetCustomerDto findCustomerWithNoAssocationsLoaded(final Integer customerId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);

        return (CollectionSheetCustomerDto) execUniqueResultNamedQueryWithResultTransformer(
                "findCustomerWithNoAssocationsLoaded", queryParameters, CollectionSheetCustomerDto.class);

    }

    @SuppressWarnings("unchecked")
    public List<CustomerListDto> getListOfActiveCentersUnderUser(PersonnelBO personnel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.CENTER.getValue());
        queryParameters.put(CustomerSearchConstants.CENTER_ACTIVE, CustomerStatus.CENTER_ACTIVE.getValue());

        return executeNamedQueryWithResultTransformer(
                "Customer.get_loanofficer_list_of_active_centers", queryParameters, CustomerListDto.class);

    }

    @SuppressWarnings("unchecked")
    public List<CustomerListDto> getListOfGroupsUnderUser(PersonnelBO personnel) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel.getPersonnelId());
        queryParameters.put(CustomerSearchConstants.OFFICEID, personnel.getOffice().getOfficeId());
        queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID, CustomerLevel.GROUP.getValue());
        return executeNamedQueryWithResultTransformer(
                "Customer.get_loanofficer_list_of_groups", queryParameters, CustomerListDto.class);
    }

}
