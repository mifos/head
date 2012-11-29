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
package org.mifos.accounts.savings.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.savings.business.SavingsActivityEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.interest.EndOfDayDetail;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingsAccountDto;
import org.mifos.application.servicefacade.CustomerHierarchyParams;
import org.mifos.customers.business.CustomerBO;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.NoteSearchDto;
import org.mifos.dto.screen.NotesSearchResultsDto;
import org.mifos.dto.screen.SearchDetailsDto;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class SavingsDaoHibernate implements SavingsDao {

    private static final Logger logger = LoggerFactory.getLogger(SavingsDaoHibernate.class);

    private final GenericDao baseDao;

    @Autowired
    public SavingsDaoHibernate(final GenericDao baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(mandatorySavingsOnRootCustomer, mandatorySavingsOnRestOfHierarchy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllVoluntarySavingAccountsForClientsAndGroupsWithCompleteGroupStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> voluntarySavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForClientsAndGroupsWithCompleteGroupStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> voluntarySavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForClientsAndGroupsWithCompleteGroupStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(voluntarySavingsOnRootCustomer, voluntarySavingsOnRestOfHierarchy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllMandatorySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> centerOrPerIndividualGroupSavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> perIndividualGroupSavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(centerOrPerIndividualGroupSavingsOnRootCustomer,
                perIndividualGroupSavingsOnRestOfHierarchy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllVoluntarySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {
        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> centerOrPerIndividualGroupSavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> perIndividualGroupSavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(centerOrPerIndividualGroupSavingsOnRootCustomer,
                perIndividualGroupSavingsOnRestOfHierarchy);
    }

    @SuppressWarnings("unchecked")
    private List<CollectionSheetCustomerSavingDto> nullSafeSavingsHierarchy(
            final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRootCustomer,
            final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRestOfHierarchy) {

        List<CollectionSheetCustomerSavingDto> nullSafeSavings = (List<CollectionSheetCustomerSavingDto>) ObjectUtils
                .defaultIfNull(mandatorySavingsOnRootCustomer, new ArrayList<CollectionSheetCustomerSavingDto>());

        List<CollectionSheetCustomerSavingDto> nullSafeRest = (List<CollectionSheetCustomerSavingDto>) ObjectUtils
                .defaultIfNull(mandatorySavingsOnRestOfHierarchy, new ArrayList<CollectionSheetCustomerSavingDto>());

        nullSafeSavings.addAll(nullSafeRest);
        return nullSafeSavings;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingsAccountDto> findAllSavingAccountsForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams) {
        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());
        topOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        //snip the '.%' from SEARCH_ID
        topOfHierarchyParameters.put("SEARCH_ID_NO_PERCENTAGE", customerHierarchyParams.getSearchId().substring(0, customerHierarchyParams.getSearchId().length() - 2));

        return (List<CollectionSheetCustomerSavingsAccountDto>) baseDao.executeNamedQueryWithResultTransformer(
                "findAllSavingAccountsForCustomerHierarchy", topOfHierarchyParameters,
                CollectionSheetCustomerSavingsAccountDto.class);
    }

    @Override
    public SavingsBO findBySystemId(String globalAccountNumber) {
        logger.debug("In SavingsPersistence::findBySystemId(), globalAccountNumber: " + globalAccountNumber);
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.GLOBAL_ACCOUNT_NUMBER, globalAccountNumber);
        Object queryResult = baseDao.executeUniqueResultNamedQuery(NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
        SavingsBO savings = queryResult == null ? null : (SavingsBO) queryResult;
        if (savings != null && savings.getRecommendedAmount() == null) {
            savings.setRecommendedAmount(new Money(savings.getCurrency()));
            Hibernate.initialize(savings.getAccountActionDates());
            Hibernate.initialize(savings.getAccountNotes());
            Hibernate.initialize(savings.getAccountFlags());
        }
        return savings;
    }

    @Override
	public void save(List<SavingsBO> savingsAccounts) {
        final Session session = baseDao.getSession();
        for (SavingsBO savingsBO : savingsAccounts) {
            session.saveOrUpdate(savingsBO);
        }
    }

    @Override
    public SavingsBO findById(Long savingsId) {
        return findById(savingsId.intValue());
    }

    @Override
    public SavingsBO findById(Integer savingsId) {
        return (SavingsBO) this.baseDao.getSession().get(SavingsBO.class, savingsId);
    }

    @Override
    public void save(SavingsBO savingsAccount) {
        this.baseDao.createOrUpdate(savingsAccount);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EndOfDayDetail> retrieveAllEndOfDayDetailsFor(MifosCurrency currency, Long savingsId) {

        List<EndOfDayDetail> allEndOfDayDetailsForAccount = new ArrayList<EndOfDayDetail>();

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", savingsId.intValue());
        List<Object[]> queryResult = (List<Object[]>) this.baseDao.executeNamedQuery("savings.retrieveAllEndOfDayTransactionDetails", queryParameters);

        if (queryResult != null) {
            for (Object[] dailyRecord : queryResult) {
                Date dayOfYear = (Date)dailyRecord[0];
                BigDecimal totalDeposits = (BigDecimal)dailyRecord[1];
                BigDecimal totalWithdrawals = (BigDecimal)dailyRecord[2];
                BigDecimal totalInterest = (BigDecimal)dailyRecord[3];

                EndOfDayDetail endOfDayDetail = new EndOfDayDetail(new LocalDate(dayOfYear), new Money(currency, totalDeposits), new Money(currency, totalWithdrawals), new Money(currency, totalInterest));
                allEndOfDayDetailsForAccount.add(endOfDayDetail);
            }
        }

        return allEndOfDayDetailsForAccount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Integer> retrieveAllActiveAndInActiveSavingsAccountsPendingInterestPostingOn(LocalDate interestPostingDate) {

        List<Integer> postingPendingAccounts = new ArrayList<Integer>();

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("currentDate", interestPostingDate.toDateMidnight().toDate());
        List<Integer> queryResult = (List<Integer>) this.baseDao.executeNamedQuery("accounts.retrieveSavingsAccountsIntPost", queryParameters);
        if (queryResult != null) {
            postingPendingAccounts = new ArrayList<Integer>(queryResult);
        }

        return postingPendingAccounts;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NotesSearchResultsDto searchNotes(NoteSearchDto noteSearch) {
        Session session = StaticHibernateUtil.getSessionTL();
        Criteria criteriaQuery = session.createCriteria(AccountNotesEntity.class);
        criteriaQuery.add(Restrictions.eq("account.accountId", noteSearch.getAccountId()));
        criteriaQuery.addOrder(Order.desc("commentId"));

        Integer totalNumberOfSavingsNotes = 10;

        int firstResult = (noteSearch.getPage() * noteSearch.getPageSize()) - noteSearch.getPageSize();
        criteriaQuery.setFirstResult(firstResult);
        criteriaQuery.setMaxResults(noteSearch.getPageSize());

        List<AccountNotesEntity> pagedResults = criteriaQuery.list();
        List<CustomerNoteDto> pageDtoResults = new ArrayList<CustomerNoteDto>();
        for (AccountNotesEntity note : pagedResults) {
            pageDtoResults.add(new CustomerNoteDto(note.getCommentDate(), note.getComment(), note.getPersonnelName()));
        }

        SearchDetailsDto searchDetails = new SearchDetailsDto(totalNumberOfSavingsNotes.intValue(), firstResult, noteSearch.getPage(), noteSearch.getPageSize());
        NotesSearchResultsDto resultsDto = new NotesSearchResultsDto(searchDetails, pageDtoResults);

        return resultsDto;
    }

    @Override
    public void prepareForInterestRecalculation(SavingsBO savingsAccount, Date fromDate) {
        List<AccountPaymentEntity> paymentsForRemoval = 
                savingsAccount.getInterestPostingPaymentsForRemoval(fromDate);
        this.save(savingsAccount);
        
        for (AccountPaymentEntity payment : paymentsForRemoval) {
            this.baseDao.delete(payment);
        }
        
        List<SavingsActivityEntity> activitesForRemoval =
                savingsAccount.getInterestPostingActivitesForRemoval(fromDate);
        for (SavingsActivityEntity activity : activitesForRemoval) {
            this.baseDao.delete(activity);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SavingsScheduleEntity> retrieveAllCustomerSchedules(Integer savingAccountId, 
        Integer customerId) {

        List<SavingsScheduleEntity> customerSchedules = new ArrayList<SavingsScheduleEntity>();
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("account_id", findById(savingAccountId));
        queryParameters.put("customer_id", this.baseDao.getSession().get(CustomerBO.class, customerId) );
        List<SavingsScheduleEntity> queryResult = (List<SavingsScheduleEntity>) this.baseDao.executeNamedQuery
            (NamedQueryConstants.SAVING_SCHEDULE_GET_ALL_CUSTOMER_SCHEDULES, queryParameters);
        if (queryResult != null) {
            customerSchedules = new ArrayList<SavingsScheduleEntity>(queryResult);
        }

        return customerSchedules;
    }

    @Override
    public void updateSavingScheduleEntity(
        List<SavingsScheduleEntity> savingScheduleList) {
         for(SavingsScheduleEntity savingsScheduleEntity : savingScheduleList) {
             savingsScheduleEntity.setDeposit(Money.zero());
             this.baseDao.createOrUpdate(savingsScheduleEntity);
         }
    }
    
}
