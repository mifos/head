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

import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * Class contains utility methods for setting up, processing and configuring a
 * sample collection sheet hierarchy for testing.
 */
public class TestSaveCollectionSheetUtils {

    public TestSaveCollectionSheetUtils() {
        collectionSheetService = DependencyInjectedServiceLocator.locateCollectionSheetService();
        currency = Money.getDefaultCurrency();
        userContext = TestUtils.makeUser();
    }

    /*
 * 
 */
    /*
     * Below are variables that can be configured to create invalid entries for
     * testing. They are injected during the assembling of the dto's. This is a
     * very ugly way to inject invalid values. Set methods would be far tidier
     * and more normal. But I (JW) preferred to keep all the save collection
     * sheet dto's as create-only and non-updatable and pay the coding price for
     * testing.
     */
    private final Integer nonExistingId = 500000;
    private Boolean firstClientExists = true;
    private Short firstClientAttendanceType = AttendanceType.ABSENT.getValue();
    private Short firstGroupAttendanceType = null;
    private Boolean individualSavingsAccountUnderFirstGroup = false;
    private Boolean firstClientParentIdInvalid = false;
    private ClientBO nonCenterClient = null;
    private Short loanInvalidCurrency = null;
    private Boolean loanAccountIdInvalid = false;
    private Boolean loanAccountUnderFirstGroupWithFirstClientLoanId = false;
    private Boolean firstClientClosed = false;
    private Boolean loanAccountCancelled = false;
    private Boolean customerAccountIdtoLoanAccountIdForFirstClient = false;
    private Boolean loanAccountIdtoCustomerAccountIdForFirstClient = false;
    private Boolean savingsAccountIdtoLoanAccountIdForFirstClient = false;
    private Integer firstClientCustomerAccountId = null;
    private Boolean overpayLoan = false;
    private Boolean duplicateFirstClient = false;
    private Boolean duplicateFirstClientLoanAccount = false;
    private Boolean overpayFirstClientAccountCollectionFee = false;
    private Boolean underpayFirstClientAccountCollectionFee = false;
    private Boolean invalidDisbursalAmountFirstClient = false;

    /*
     * 
     */
    CenterBO center;
    GroupBO group;
    ClientBO client;
    LoanBO loan;
    CenterBO anotherCenter;
    GroupBO anotherGroup;
    ClientBO anotherClient;

    private CollectionSheetService collectionSheetService;
    private UserContext userContext;
    private MifosCurrency currency;

    /**
     * Write a sample center hierarchy, retrieve the collection sheet
     * information and put it into a saveCollectionSheetDto.
     * 
     * Sample hierarchy contains one center, one group and one client with a
     * loan to be disbursed and 1 weekly account collection fee
     * 
     * Finally, change client and loan status if configured to do so.
     */
    public SaveCollectionSheetDto createSampleSaveCollectionSheet() throws AccountException, CustomerException {

        LocalDate transactionDate = new LocalDate();
        try {
            createSampleCenterHierarchy(DateUtils.getDateFromLocalDate(transactionDate));
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
        SaveCollectionSheetDto saveCollectionSheet = assembleSaveCollectionSheetFromCreatedCenterHierarchy(transactionDate);

        if (firstClientClosed) {
            // Set Client status to closed - unfortunately have to set
            // userContext to add customer note
            client.setUserContext(userContext);
            client.changeStatus(CustomerStatus.CLIENT_CLOSED, CustomerStatusFlag.CLIENT_CLOSED_BLACKLISTED,
                    "Close Client");
            client.update();
            StaticHibernateUtil.commitTransaction();
        }

        if (loanAccountCancelled) {
            loan.changeStatus(AccountState.LOAN_CANCELLED, AccountStateFlag.LOAN_OTHER.getValue(), "Loan Cancelled");
            loan.update();
            StaticHibernateUtil.commitTransaction();
        }

        return saveCollectionSheet;
    }

    /**
     * Retrieve collection sheet information and put it into a
     * saveCollectionSheetDto
     */
    public SaveCollectionSheetDto assembleSaveCollectionSheetFromCreatedCenterHierarchy(LocalDate transactionDate) {

        CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(center.getCustomerId(),
                transactionDate);

        return assembleSaveCollectionSheetDto(collectionSheet, transactionDate);
    }

    /**
     * By default generates 1 center, 1 group and 1 client with 1 loan to be
     * disbursed and 1 weekly account collection fee. Can be configured to add
     * in other invalid entries.
     */
    public void createSampleCenterHierarchy(Date date) throws Exception {

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();

        center = new CenterBuilder().withSearchId("10.4").withMeeting(weeklyMeeting).withName("Center").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.saveCustomer(center);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withParentCustomer(center).build();
        IntegrationTestObjectMother.saveCustomer(group);

        AmountFeeBO weeklyPeriodicFeeForFirstClients = new FeeBuilder().appliesToClientsOnly().withFeeAmount("87.0")
                .withName("First Client Weekly Periodic Fee").withSameRecurrenceAs(weeklyMeeting).withOffice(
                        sampleBranchOffice()).build();
        weeklyPeriodicFeeForFirstClients.save();

        client = new ClientBuilder().withFee(weeklyPeriodicFeeForFirstClients).withMeeting(weeklyMeeting).withName(
                "Client 1").withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(group)
                .buildForIntegrationTests();
        IntegrationTestObjectMother.saveCustomer(client);

        client.setClientPerformanceHistory(new ClientPerformanceHistoryEntity(client));
        client.update();
        StaticHibernateUtil.commitTransaction();

        MeetingBO loanMeeting = TestObjectFactory.createLoanMeeting(client.getCustomerMeeting().getMeeting());

        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan", ApplicableTo.CLIENTS, date,
                PrdStatus.LOAN_ACTIVE, 1200.0, 1.2, 12, InterestType.FLAT, loanMeeting);

        loan = null;
        try {
            loan = LoanBO.createIndividualLoan(userContext, loanOffering, client, AccountState.LOAN_APPROVED,
                    new Money(currency, "1200.0"), Short.valueOf("12"), date, false, false, 10.0, (short) 0,
                    new FundBO(), new ArrayList<FeeView>(), null);

        } catch (AccountException e) {
            throw new Exception(e);
        }

        loan.save();
        StaticHibernateUtil.commitTransaction();
    }

    /**
     * clears persistent objects created by this class
     */
    public void clearObjects() throws Exception {

        if (loan != null) {
            loan = (LoanBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, loan.getAccountId());
        }
        if (client != null) {
            client = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class, client.getCustomerId());
        }
        if (group != null) {
            group = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
        }
        if (center != null) {
            center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        }
        if (anotherClient != null) {
            anotherClient = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
                    anotherClient.getCustomerId());
        }
        if (anotherGroup != null) {
            anotherGroup = (GroupBO) StaticHibernateUtil.getSessionTL()
                    .get(GroupBO.class, anotherGroup.getCustomerId());
        }
        if (anotherCenter != null) {
            anotherCenter = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class,
                    anotherCenter.getCustomerId());
        }
        TestObjectFactory.cleanUp(loan);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.cleanUp(anotherClient);
        TestObjectFactory.cleanUp(anotherGroup);
        TestObjectFactory.cleanUp(anotherCenter);

    }

    /*
     * 
     */
    private ClientBO getAnotherClientFromAnotherCenter() throws Exception {

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();

        anotherCenter = new CenterBuilder().withMeeting(weeklyMeeting).withName("Another Center").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.saveCustomer(anotherCenter);

        anotherGroup = new GroupBuilder().withMeeting(weeklyMeeting).withName("Another Group").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(anotherCenter).build();
        IntegrationTestObjectMother.saveCustomer(anotherGroup);

        anotherClient = new ClientBuilder().withMeeting(weeklyMeeting).withName("Another Client 1").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(anotherGroup)
                .buildForIntegrationTests();
        IntegrationTestObjectMother.saveCustomer(anotherClient);

        return anotherClient;
    }

    private SaveCollectionSheetDto assembleSaveCollectionSheetDto(CollectionSheetDto collectionSheet,
            LocalDate transactionDate) {

        SaveCollectionSheetDto saveCollectionSheet = null;
        try {
            saveCollectionSheet = new SaveCollectionSheetDto(assembleSCSCustomers(collectionSheet
                    .getCollectionSheetCustomer()), PaymentTypes.VOUCHER.getValue(), transactionDate, "my new receipt",
                    transactionDate, testUser().getPersonnelId());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        return saveCollectionSheet;

    }

    /**
     * @param collectionSheetCustomers
     * @return
     */
    private List<SaveCollectionSheetCustomerDto> assembleSCSCustomers(
            List<CollectionSheetCustomerDto> collectionSheetCustomers) {

        List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
        Boolean firstClient = true;
        Boolean firstGroup = true;
        Integer numberToCreate = 1;
        for (CollectionSheetCustomerDto collectionSheetCustomer : collectionSheetCustomers) {

            Integer customerId = collectionSheetCustomer.getCustomerId();
            Integer parentCustomerId = collectionSheetCustomer.getParentCustomerId();
            Short attendanceId = null;

            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = assembleSCSCustomerAccount(
                    collectionSheetCustomer.getCollectionSheetCustomerAccount(), collectionSheetCustomer.getLevelId(),
                    firstClient);
            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = assembleSCSCustomerLoans(
                    collectionSheetCustomer.getCollectionSheetCustomerLoan(), collectionSheetCustomer.getLevelId(),
                    firstClient);
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = assembleSCSCustomerSavings(collectionSheetCustomer
                    .getCollectionSheetCustomerSaving());
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings = assembleSCSCustomerSavings(collectionSheetCustomer
                    .getIndividualSavingAccounts());

            if (collectionSheetCustomer.getLevelId().compareTo(CustomerLevel.CLIENT.getValue()) == 0) {

                attendanceId = AttendanceType.PRESENT.getValue();

                if (firstClient) {
                    if (duplicateFirstClient) {
                        numberToCreate = 2;
                        duplicateFirstClient = false;
                    }
                    if (!firstClientExists) {
                        customerId = nonExistingId;
                    }
                    if (firstClientParentIdInvalid) {
                        parentCustomerId = nonExistingId;
                    }
                    if (savingsAccountIdtoLoanAccountIdForFirstClient) {
                        if (saveCollectionSheetCustomerSavings == null) {
                            saveCollectionSheetCustomerSavings = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
                        }
                        saveCollectionSheetCustomerSavings.add(assembleOptionalSavingsAccount(loan.getAccountId()));
                    }
                    attendanceId = firstClientAttendanceType;

                    firstClient = false;
                }
            }

            if (collectionSheetCustomer.getLevelId().compareTo(CustomerLevel.GROUP.getValue()) == 0) {

                if (firstGroup) {
                    attendanceId = firstGroupAttendanceType;

                    if (individualSavingsAccountUnderFirstGroup) {
                        if (saveCollectionSheetCustomerIndividualSavings == null) {
                            saveCollectionSheetCustomerIndividualSavings = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
                        }
                        saveCollectionSheetCustomerIndividualSavings
                                .add(assembleOptionalGroupIndividualSavingsAccount());
                    }
                    if (loanAccountUnderFirstGroupWithFirstClientLoanId) {
                        if (saveCollectionSheetCustomerLoans == null) {
                            saveCollectionSheetCustomerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();
                        }
                        saveCollectionSheetCustomerLoans.add(assembleOptionalGroupLoanAccount(loan.getAccountId()));
                    }
                    firstGroup = false;
                }
            }

            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = null;

            for (Integer i = 0; i < numberToCreate; i++) {
                try {
                    saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(customerId, parentCustomerId,
                            attendanceId, saveCollectionSheetCustomerAccount, saveCollectionSheetCustomerLoans,
                            saveCollectionSheetCustomerSavings, saveCollectionSheetCustomerIndividualSavings);
                } catch (SaveCollectionSheetException e) {
                    throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                }

                saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);
            }
        }

        if (nonCenterClient != null) {
            SaveCollectionSheetCustomerDto anotherSaveCollectionSheetCustomer = null;
            try {
                anotherSaveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                        nonCenterClient.getCustomerId(), nonCenterClient.getParentCustomer().getCustomerId(),
                        AttendanceType.PRESENT.getValue(), null, null, null, null);
            } catch (SaveCollectionSheetException e) {
                throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
            }
            saveCollectionSheetCustomers.add(anotherSaveCollectionSheetCustomer);
        }

        return saveCollectionSheetCustomers;
    }

    private SaveCollectionSheetCustomerAccountDto assembleSCSCustomerAccount(
            CollectionSheetCustomerAccountDto collectionSheetCustomerAccount, Short customerLevelId, Boolean firstClient) {

        if (null != collectionSheetCustomerAccount && collectionSheetCustomerAccount.getAccountId() != -1) {

            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = null;

            Integer accountId = collectionSheetCustomerAccount.getAccountId();

            if (loanAccountIdtoCustomerAccountIdForFirstClient
                    && (customerLevelId.compareTo(CustomerLevel.CLIENT.getValue()) == 0) && firstClient) {
                firstClientCustomerAccountId = accountId;
            }

            if (customerAccountIdtoLoanAccountIdForFirstClient
                    && (customerLevelId.compareTo(CustomerLevel.CLIENT.getValue()) == 0) && firstClient) {
                accountId = loan.getAccountId();
            }

            BigDecimal accountCollectionFee = new BigDecimal(collectionSheetCustomerAccount
                    .getTotalCustomerAccountCollectionFee());

            if (overpayFirstClientAccountCollectionFee
                    && (customerLevelId.compareTo(CustomerLevel.CLIENT.getValue()) == 0) && firstClient) {
                accountCollectionFee = new BigDecimal(1000000.00);
            }

            if (underpayFirstClientAccountCollectionFee
                    && (customerLevelId.compareTo(CustomerLevel.CLIENT.getValue()) == 0) && firstClient) {
                accountCollectionFee = new BigDecimal(1.45);
            }
            try {
                saveCollectionSheetCustomerAccount = new SaveCollectionSheetCustomerAccountDto(accountId,
                        collectionSheetCustomerAccount.getCurrencyId(), accountCollectionFee);
            } catch (SaveCollectionSheetException e) {
                throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
            }

            return saveCollectionSheetCustomerAccount;

        }
        return null;
    }

    private List<SaveCollectionSheetCustomerLoanDto> assembleSCSCustomerLoans(
            List<CollectionSheetCustomerLoanDto> collectionSheetCustomerLoans, Short customerLevelId,
            Boolean firstClient) {
        if (null != collectionSheetCustomerLoans && collectionSheetCustomerLoans.size() > 0) {

            Integer numberToCreate = 1;
            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();
            Boolean firstLoan = true;
            for (CollectionSheetCustomerLoanDto collectionSheetCustomerLoan : collectionSheetCustomerLoans) {
                SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan = null;

                Integer accountId = collectionSheetCustomerLoan.getAccountId();
                BigDecimal loanRepayment = new BigDecimal(collectionSheetCustomerLoan.getTotalRepaymentDue());
                BigDecimal loanDisbursement = new BigDecimal(collectionSheetCustomerLoan.getTotalDisbursement());

                Short accountCurrencyId = collectionSheetCustomerLoan.getCurrencyId();

                if (firstLoan && (customerLevelId.compareTo(CustomerLevel.CLIENT.getValue()) == 0) && firstClient) {
                    if (duplicateFirstClientLoanAccount) {
                        numberToCreate = 2;
                        duplicateFirstClientLoanAccount = false;
                    }
                    if (overpayLoan) {
                        loanRepayment = loanRepayment.add(new BigDecimal(1000000.00));
                        loanDisbursement = BigDecimal.ZERO;
                    }
                    if (invalidDisbursalAmountFirstClient) {
                        loanDisbursement = new BigDecimal(1.10);
                    }

                    if (loanInvalidCurrency != null) {
                        accountCurrencyId = loanInvalidCurrency;
                    }
                    if (loanAccountIdtoCustomerAccountIdForFirstClient) {
                        accountId = firstClientCustomerAccountId;
                    }
                    if (loanAccountIdInvalid) {
                        accountId = nonExistingId;
                    }
                }
                for (Integer i = 0; i < numberToCreate; i++) {
                    try {
                        saveCollectionSheetCustomerLoan = new SaveCollectionSheetCustomerLoanDto(accountId,
                                accountCurrencyId, loanRepayment, loanDisbursement);
                    } catch (SaveCollectionSheetException e) {
                        throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                    }
                    saveCollectionSheetCustomerLoans.add(saveCollectionSheetCustomerLoan);
                }
                firstLoan = false;
            }

            return saveCollectionSheetCustomerLoans;
        }
        return null;
    }

    private List<SaveCollectionSheetCustomerSavingDto> assembleSCSCustomerSavings(
            List<CollectionSheetCustomerSavingDto> collectionSheetCustomerSavings) {

        if (null != collectionSheetCustomerSavings && collectionSheetCustomerSavings.size() > 0) {

            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
            for (CollectionSheetCustomerSavingDto collectionSheetCustomerSaving : collectionSheetCustomerSavings) {
                SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving = null;

                try {
                    saveCollectionSheetCustomerSaving = new SaveCollectionSheetCustomerSavingDto(
                            collectionSheetCustomerSaving.getAccountId(),
                            collectionSheetCustomerSaving.getCurrencyId(), collectionSheetCustomerSaving
                                    .getDepositDue(), new BigDecimal(0.0));
                } catch (SaveCollectionSheetException e) {
                    throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                }

                saveCollectionSheetCustomerSavings.add(saveCollectionSheetCustomerSaving);
            }
            return saveCollectionSheetCustomerSavings;
        }
        return null;
    }

    private SaveCollectionSheetCustomerSavingDto assembleOptionalGroupIndividualSavingsAccount() {

        SaveCollectionSheetCustomerSavingDto groupIndividualSavingsAccount = null;

        try {
            groupIndividualSavingsAccount = new SaveCollectionSheetCustomerSavingDto(99999, currency.getCurrencyId(),
                    BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        return groupIndividualSavingsAccount;
    }

    private SaveCollectionSheetCustomerLoanDto assembleOptionalGroupLoanAccount(Integer loanAccountId) {

        SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan = null;

        try {
            saveCollectionSheetCustomerLoan = new SaveCollectionSheetCustomerLoanDto(loanAccountId, currency
                    .getCurrencyId(), BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        return saveCollectionSheetCustomerLoan;
    }

    private SaveCollectionSheetCustomerSavingDto assembleOptionalSavingsAccount(Integer accountId) {
        SaveCollectionSheetCustomerSavingDto savingsAccount = null;

        try {
            savingsAccount = new SaveCollectionSheetCustomerSavingDto(accountId, currency.getCurrencyId(),
                    BigDecimal.ZERO, BigDecimal.ZERO);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        return savingsAccount;
    }

    /*
     * 
     * methods to configure invalid entries below
     */
    /**
     * The first client in this sample collection sheet hierarchy will be given
     * an id that doesn't exist
     */
    public void setFirstClientDoesntExist() {
        this.firstClientExists = false;
    }

    /**
     * The first client in this sample collection sheet hierarchy will be given
     * an AttendanceType as specified
     */
    public void setFirstClientAttendanceType(Short attendanceType) {
        this.firstClientAttendanceType = attendanceType;
    }

    /**
     * The first group in this sample collection sheet hierarchy will be given
     * an AttendanceType as specified
     */
    public void setFirstGroupAttendanceType(short attendanceType) {
        this.firstGroupAttendanceType = attendanceType;
    }

    /**
     * The first client in this sample collection sheet hierarchy will have its
     * status set to closed
     */
    public void setFirstClientClosed() {
        this.firstClientClosed = true;
    }

    /**
     * The first group in this sample collection sheet hierarchy will have an
     * individual savings account added to it
     */
    public void setIndividualSavingsAccountUnderFirstGroup() {
        this.individualSavingsAccountUnderFirstGroup = true;
    }

    /**
     * The first client in this sample collection sheet hierarchy will have its
     * parent's id set to a non-existing value
     */
    public void setFirstClientParentIdInvalid() {
        this.firstClientParentIdInvalid = true;
    }

    /**
     * Another collection sheet hierarchy will be created and a client from it
     * will be added to this sample collection sheet hierarchy
     */
    public void addAnotherClientFromADifferentCenter() throws Exception {
        nonCenterClient = getAnotherClientFromAnotherCenter();
    }

    /**
     * The loan account attached to the first client in this sample collection
     * sheet hierarchy will have its currencyId set to an invalid value
     */
    public void setLoanAccountInvalidCurrency() {
        Integer defaultCurrencyId = Integer.valueOf(currency.getCurrencyId().toString()) + 1;
        this.loanInvalidCurrency = Short.valueOf(defaultCurrencyId.toString());
    }

    /**
     * The loan account attached to the first client in this sample collection
     * sheet hierarchy will have its id set to an non-existing value
     */
    public void setLoanAccountIdInvalid() {
        this.loanAccountIdInvalid = true;
    }

    /**
     * The loan account attached to the first client in this sample collection
     * sheet hierarchy will have its status set to cancelled
     */
    public void setLoanAccountCancelled() {
        this.loanAccountCancelled = true;
    }

    /**
     * The first group in this sample collection sheet hierarchy will have a
     * loan account added to it with an account id equal to the loan account
     * belonging to the first client in this sample collection sheet hierarchy
     */
    public void setLoanAccountUnderFirstGroupWithFirstClientLoanId() {
        this.loanAccountUnderFirstGroupWithFirstClientLoanId = true;
    }

    /**
     * The first client in this sample collection sheet hierarchy will have its
     * customer account id set to equal its loan account id
     */
    public void setCustomerAccountIdtoLoanAccountIdForFirstClient() {
        this.customerAccountIdtoLoanAccountIdForFirstClient = true;
    }

    /**
     * The first client in this sample collection sheet hierarchy will have its
     * loan account id set to equal its customer account id
     */
    public void setLoanAccountIdtoCustomerAccountIdForFirstClient() {
        this.loanAccountIdtoCustomerAccountIdForFirstClient = true;
    }

    /**
     * The first client in this sample collection sheet hierarchy will have a
     * savings account added with the savings account id set to equal its loan
     * account id
     */
    public void setSavingsAccountIdtoLoanAccountIdForFirstClient() {
        this.savingsAccountIdtoLoanAccountIdForFirstClient = true;
    }

    /**
     * The first client's loan account will have a repayment set that is greater
     * than the outstanding loan amount
     */
    public void setOverpayLoan() {
        this.overpayLoan = true;
    }

    /**
     * The first client will be duplicated
     */
    public void setDuplicateFirstClient() {
        this.duplicateFirstClient = true;
    }

    /**
     * The first client's loan account will be duplicated
     */
    public void setDuplicateFirstClientLoanAccount() {
        this.duplicateFirstClientLoanAccount = true;
    }

    public void setOverpayFirstClientAccountCollectionFee() {
        this.overpayFirstClientAccountCollectionFee = true;
    }

    public void setUnderpayFirstClientAccountCollectionFee() {
        this.underpayFirstClientAccountCollectionFee = true;
    }

    public void setInvalidDisbursalAmountFirstClient() {
        this.invalidDisbursalAmountFirstClient = true;
    }

}
