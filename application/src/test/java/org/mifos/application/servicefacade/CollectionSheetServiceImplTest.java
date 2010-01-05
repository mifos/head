/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link CollectionSheetServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionSheetServiceImplTest {

    // class under test
    private CollectionSheetService collectionSheetService;

    // collaborators
    @Mock
    private ClientAttendanceDao clientAttendanceDao;
    @Mock
    private LoanPersistence loanPersistence;
    @Mock
    private AccountPersistence accountPersistence;
    @Mock
    private SavingsPersistence savingsPersistence;
    @Mock
    private CollectionSheetDao collectionSheetDao;
    
    @Before
    public void setupAndInjectDependencies() {

        collectionSheetService = new CollectionSheetServiceImpl(clientAttendanceDao, loanPersistence,
                accountPersistence, savingsPersistence, collectionSheetDao);
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataCustomersCorrespondingToCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();
        
        final Integer customerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(customerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(customerId, transactionDate)).thenReturn(customerHierarchyList);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(customerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        assertThat(hierarchyRoot.getCollectionSheetCustomerAccount(), is(notNullValue()));
        assertThat(hierarchyRoot.getCollectionSheetCustomerLoan(), is(notNullValue()));
        assertThat(hierarchyRoot.getCollectionSheetCustomerSaving(), is(notNullValue()));
    }

    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllLoanRepaymentsDueForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();

        final Integer centerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);
        
        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // loanRepayments
        final CollectionSheetCustomerLoanDto firstLoanAccount = new CollectionSheetCustomerLoanDto();
        firstLoanAccount.setPrincipalDue(BigDecimal.valueOf(Double.valueOf("25.2")));

        final List<CollectionSheetCustomerLoanDto> centerLoanAccounts = new ArrayList<CollectionSheetCustomerLoanDto>();
        centerLoanAccounts.add(firstLoanAccount);
        
        final Map<Integer, List<CollectionSheetCustomerLoanDto>> loanRepaymentsDue = new HashMap<Integer, List<CollectionSheetCustomerLoanDto>>();
        loanRepaymentsDue.put(centerId, centerLoanAccounts);

        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findAllLoanRepaymentsForCustomerHierarchy(branchId, searchId + ".%",
                        transactionDate, centerId))
                .thenReturn(loanRepaymentsDue);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);
        
        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));
        
        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        assertThat(hierarchyRoot.getCollectionSheetCustomerLoan().size(), is(1));
        final CollectionSheetCustomerLoanDto returnedLoan = hierarchyRoot.getCollectionSheetCustomerLoan().get(0);
        assertThat(returnedLoan.getTotalRepaymentDue(), is(Double.valueOf("25.2")));
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllLoanRepaymentsDueWithCorrespondingAccountCollectionFeesForLoansForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();

        final Integer centerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // loan repayments
        final Integer loanAccountId = Integer.valueOf(999);
        final CollectionSheetCustomerLoanDto firstLoanAccount = new CollectionSheetCustomerLoanDto();
        firstLoanAccount.setPrincipalDue(BigDecimal.valueOf(Double.valueOf("25.2")));
        firstLoanAccount.setAccountId(loanAccountId);

        final List<CollectionSheetCustomerLoanDto> centerLoanAccounts = new ArrayList<CollectionSheetCustomerLoanDto>();
        centerLoanAccounts.add(firstLoanAccount);

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> loanRepaymentsDue = new HashMap<Integer, List<CollectionSheetCustomerLoanDto>>();
        loanRepaymentsDue.put(centerId, centerLoanAccounts);

        // outstanding fees
        final CollectionSheetLoanFeeDto outstandingLoanFee = new CollectionSheetLoanFeeDto();
        outstandingLoanFee.setFeeAmountDue(BigDecimal.valueOf(Double.valueOf("13.1")));
        outstandingLoanFee.setAccountId(loanAccountId);
        
        final List<CollectionSheetLoanFeeDto> loanFeeList = Arrays.asList(outstandingLoanFee);

        final Map<Integer, List<CollectionSheetLoanFeeDto>> feesForLoanAccount = new HashMap<Integer, List<CollectionSheetLoanFeeDto>>();
        feesForLoanAccount.put(loanAccountId, loanFeeList);
        
        final Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> outstandingFeesOnLoans = new HashMap<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>>();
        outstandingFeesOnLoans.put(centerId, feesForLoanAccount);

        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findAllLoanRepaymentsForCustomerHierarchy(branchId, searchId + ".%",
                        transactionDate, centerId))
                .thenReturn(loanRepaymentsDue);
        when(
                collectionSheetDao.findOutstandingFeesForLoansOnCustomerHierarchy(branchId, searchId + ".%",
                        transactionDate, centerId)).thenReturn(outstandingFeesOnLoans);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        assertThat(hierarchyRoot.getCollectionSheetCustomerLoan().size(), is(1));
        final CollectionSheetCustomerLoanDto returnedLoan = hierarchyRoot.getCollectionSheetCustomerLoan().get(0);
        assertThat(returnedLoan.getTotalRepaymentDue(), is(Double.valueOf("38.3")));
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllAccountCollectionForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();

        final Integer centerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // account collections
        final Integer customerAccountId = Integer.valueOf(123);
        final Short customerAccountCurrencyId = Short.valueOf("34");
        final CollectionSheetCustomerAccountCollectionDto customerAccountCollection = new CollectionSheetCustomerAccountCollectionDto();
        customerAccountCollection.setAccountId(customerAccountId);
        customerAccountCollection.setCurrencyId(customerAccountCurrencyId);
        customerAccountCollection.setMiscFeesDue(BigDecimal.valueOf(Double.valueOf("2.2")));
        final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollectionList = Arrays
                .asList(customerAccountCollection);
        
        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> accountCollectionsDue = new HashMap<Integer, List<CollectionSheetCustomerAccountCollectionDto>>();
        accountCollectionsDue.put(centerId, customerAccountCollectionList);

        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findAccountCollectionsOnCustomerAccount(branchId, searchId + ".%", transactionDate,
                        centerId)).thenReturn(accountCollectionsDue);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        final CollectionSheetCustomerAccountDto returnedCustomerAccount = hierarchyRoot
                .getCollectionSheetCustomerAccount();
        assertThat(returnedCustomerAccount.getTotalCustomerAccountCollectionFee(), is(Double.valueOf("2.2")));
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllAccountCollectionAndCorrespondingCollectionFeesForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();

        final Integer centerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // account collections
        final Integer customerAccountId = Integer.valueOf(123);
        final Short customerAccountCurrencyId = Short.valueOf("34");
        final CollectionSheetCustomerAccountCollectionDto customerAccountCollection = new CollectionSheetCustomerAccountCollectionDto();
        customerAccountCollection.setAccountId(customerAccountId);
        customerAccountCollection.setCurrencyId(customerAccountCurrencyId);
        customerAccountCollection.setMiscFeesDue(BigDecimal.valueOf(Double.valueOf("2.2")));
        final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollectionList = Arrays
                .asList(customerAccountCollection);

        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> accountCollectionsDue = new HashMap<Integer, List<CollectionSheetCustomerAccountCollectionDto>>();
        accountCollectionsDue.put(centerId, customerAccountCollectionList);
        
        // account collection fees
        final CollectionSheetCustomerAccountCollectionDto customerAccountCollectionFees = new CollectionSheetCustomerAccountCollectionDto();
        customerAccountCollectionFees.setAccountId(customerAccountId);
        customerAccountCollectionFees.setCurrencyId(customerAccountCurrencyId);
        customerAccountCollectionFees.setFeeAmountDue(BigDecimal.valueOf(Double.valueOf("16.8")));
        
        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> accountCollectionFeesDue = new HashMap<Integer, List<CollectionSheetCustomerAccountCollectionDto>>();
        accountCollectionFeesDue.put(centerId, Arrays.asList(customerAccountCollectionFees));

        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findAccountCollectionsOnCustomerAccount(branchId, searchId + ".%", transactionDate,
                        centerId)).thenReturn(accountCollectionsDue);
        when(
                collectionSheetDao.findOutstandingFeesForCustomerAccountOnCustomerHierarchy(branchId, searchId + ".%",
                        transactionDate, centerId)).thenReturn(accountCollectionFeesDue);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        final CollectionSheetCustomerAccountDto returnedCustomerAccount = hierarchyRoot
                .getCollectionSheetCustomerAccount();
        assertThat(returnedCustomerAccount.getTotalCustomerAccountCollectionFee(), is(Double.valueOf("19.0")));
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllSavingAccountsForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();
        final Integer centerId = Integer.valueOf(3);
        final String searchId = "1.1";
        final Short branchId = Short.valueOf("6");
        final CustomerHierarchyParams customerHierarchyParams = new CustomerHierarchyParams(centerId, branchId,
                searchId + ".%", transactionDate);

        final String name = "center";
        final Short level = Short.valueOf("3");
        final Short attendance = AttendanceType.ABSENT.getValue();
        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // savings
        final Integer savingAccountId = Integer.valueOf(24);
        final CollectionSheetCustomerSavingDto savingAccount = new CollectionSheetCustomerSavingDto();
        savingAccount.setAccountId(savingAccountId);
        savingAccount.setDepositDue(BigDecimal.valueOf(Double.valueOf("25.0")));
        
        final CollectionSheetCustomerSavingDto savingAccount2 = new CollectionSheetCustomerSavingDto();
        savingAccount2.setAccountId(savingAccountId);
        savingAccount2.setDepositDue(BigDecimal.valueOf(Double.valueOf("25.0")));
        
        
        final Map<Integer, List<CollectionSheetCustomerSavingDto>> savingAccounts = new HashMap<Integer, List<CollectionSheetCustomerSavingDto>>();
        savingAccounts.put(centerId, Arrays.asList(savingAccount));
        savingAccounts.put(centerId, Arrays.asList(savingAccount2));
        
        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findSavingsDepositsforCustomerHierarchy(customerHierarchyParams))
                .thenReturn(savingAccounts);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        assertThat(hierarchyRoot.getCollectionSheetCustomerSaving().size(), is(1));
        final CollectionSheetCustomerSavingDto returnedSavingsAccount = hierarchyRoot
                .getCollectionSheetCustomerSaving().get(0);
        assertThat(returnedSavingsAccount.getTotalDepositAmount(), is(Double.valueOf("25.0")));
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllLoanDisbursementsForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();

        final Integer centerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // loan disbursements
        final CollectionSheetCustomerLoanDto loanDisbursement = new CollectionSheetCustomerLoanDto();
        loanDisbursement.setDisbursementAmount(BigDecimal.valueOf(Double.valueOf("19.2")));

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> loanDisbursements = new HashMap<Integer, List<CollectionSheetCustomerLoanDto>>();
        loanDisbursements.put(centerId, Arrays.asList(loanDisbursement));

        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findLoanDisbursementsForCustomerHierarchy(branchId, searchId + ".%",
                        transactionDate, centerId))
                .thenReturn(loanDisbursements);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        assertThat(hierarchyRoot.getCollectionSheetCustomerLoan().size(), is(1));
        final CollectionSheetCustomerLoanDto returnedDisbursement = hierarchyRoot.getCollectionSheetCustomerLoan().get(
                0);
        assertThat(returnedDisbursement.getTotalDisbursement(), is(Double.valueOf("19.2")));
    }
    
    @Test
    public void shouldRetrieveCollectionSheetDataContainingAllLoanDisbursementsAndCorrespondingDisbursementFeesForCustomerHierarchy() {

        // setup
        final LocalDate transactionDate = new LocalDate();

        final Integer centerId = Integer.valueOf(3);
        final String name = "center";
        final Short level = Short.valueOf("3");
        final String searchId = "1.1";
        final Short attendance = AttendanceType.ABSENT.getValue();
        final Short branchId = Short.valueOf("6");

        final CollectionSheetCustomerDto center = new CollectionSheetCustomerDto(centerId, name, level, searchId,
                attendance, branchId);

        final List<CollectionSheetCustomerDto> customerHierarchyList = new ArrayList<CollectionSheetCustomerDto>();
        customerHierarchyList.add(center);

        // loan disbursements
        final Integer loanDisbursementAccountId = Integer.valueOf(35);
        final CollectionSheetCustomerLoanDto loanDisbursement = new CollectionSheetCustomerLoanDto();
        loanDisbursement.setDisbursementAmount(BigDecimal.valueOf(Double.valueOf("19.2")));
        loanDisbursement.setAccountId(loanDisbursementAccountId);

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> loanDisbursements = new HashMap<Integer, List<CollectionSheetCustomerLoanDto>>();
        loanDisbursements.put(centerId, Arrays.asList(loanDisbursement));
        
        // stubbing
        when(collectionSheetDao.findCustomerHierarchy(centerId, transactionDate)).thenReturn(customerHierarchyList);
        when(
                collectionSheetDao.findLoanDisbursementsForCustomerHierarchy(branchId, searchId + ".%",
                        transactionDate, centerId))
                .thenReturn(loanDisbursements);

        // exercise test
        final CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(centerId,
                transactionDate);

        // verifications
        assertThat(collectionSheet.getCollectionSheetCustomer().size(), is(1));

        final CollectionSheetCustomerDto hierarchyRoot = collectionSheet.getCollectionSheetCustomer().get(0);
        assertThat(hierarchyRoot.getCollectionSheetCustomerLoan().size(), is(1));
        final CollectionSheetCustomerLoanDto returnedDisbursement = hierarchyRoot.getCollectionSheetCustomerLoan().get(
                0);
        assertThat(returnedDisbursement.getTotalDisbursement(), is(Double.valueOf("19.2")));
        assertThat(returnedDisbursement.getAmountDueAtDisbursement(), is(Double.valueOf("0.0")));
    }
}
