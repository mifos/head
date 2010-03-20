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
package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link CollectionSheetServiceFacadeWebTier}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CollectionSheetDtoTranslatorImplTest {

    private static final Short defaultCurrencyId = Short.valueOf("2");
    private static MifosCurrency defaultCurrency;

    // class under test
    private CollectionSheetDtoTranslator collectionSheetDtoTranslator;

    // collaborators
    @Mock
    private CollectionSheetCustomerDto centerCustomer;
    @Mock
    private CollectionSheetDto collectionSheetData;
    @Mock
    private CollectionSheetFormEnteredDataDto formEnteredDataDto;
    @Mock
    private CollectionSheetCustomerSavingDto collectionSheetSaving;
    @Mock
    private CollectionSheetCustomerLoanDto collectionSheetLoan;

    @BeforeClass
    public static void setupStaticClientRules() {
        defaultCurrency = new MifosCurrency(defaultCurrencyId, null, null, null);
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupSUTAndInjectMocksAsDependencies() {
        collectionSheetDtoTranslator = new CollectionSheetDtoTranslatorImpl();
    }

    @Test
    public void shouldTranslateFormEnteredDataOnToCollectionSheetEntryGridDto() throws Exception {

        // setup
        final List<CollectionSheetCustomerDto> collectionSheetCustomer = Arrays.asList(centerCustomer);

        final PersonnelView loanOfficer = new PersonnelView(Short.valueOf("1"), "loanOfficer");
        final OfficeView office = new OfficeView(Short.valueOf("1"), "office", OfficeLevel.BRANCHOFFICE,
                "levelNameKey", Integer.valueOf(1));
        final Short paymentTypeId = Short.valueOf("99");
        final ListItem<Short> paymentType = new ListItem<Short>(paymentTypeId, "item1");
        final Date today = new DateTime().toDate();
        final String receiptNumber = "XXX-120";

        final Integer accountId = Integer.valueOf("55");
        final Short currencyId = Short.valueOf("2");
        final Double totalCustomerAccountCollectionFee = Double.valueOf("29.87");
        final CollectionSheetCustomerAccountDto customerAccountDto = new CollectionSheetCustomerAccountDto(accountId,
                currencyId, totalCustomerAccountCollectionFee);

        // stubbing
        when(collectionSheetData.getCollectionSheetCustomer()).thenReturn(collectionSheetCustomer);
        when(centerCustomer.getCollectionSheetCustomerAccount()).thenReturn(customerAccountDto);

        when(formEnteredDataDto.getLoanOfficer()).thenReturn(loanOfficer);
        when(formEnteredDataDto.getOffice()).thenReturn(office);
        when(formEnteredDataDto.getPaymentType()).thenReturn(paymentType);
        when(formEnteredDataDto.getMeetingDate()).thenReturn(today);
        when(formEnteredDataDto.getReceiptDate()).thenReturn(today);
        when(formEnteredDataDto.getReceiptId()).thenReturn(receiptNumber);

        // exercise test
        final CollectionSheetEntryGridDto formDto = collectionSheetDtoTranslator.toLegacyDto(collectionSheetData,
                formEnteredDataDto, null, defaultCurrency);

        // verification
        assertThat(formDto.getTotalCustomers(), is(collectionSheetCustomer.size()));

        // assert that the parent entry view exists with correctly populated

        // assert form selected data set on dto
        assertThat(formDto.getLoanOfficer(), is(loanOfficer));
        assertThat(formDto.getOffice(), is(office));
        assertThat(formDto.getPaymentType(), is(paymentType));
        assertThat(formDto.getTransactionDate(), is(today));
        assertThat(formDto.getReceiptDate(), is(today));
        assertThat(formDto.getReceiptId(), is(receiptNumber));
        assertThat(formDto.getPaymentTypeId(), is(paymentTypeId));
    }

    @Test
    public void shouldTranslateLoanSavingsAndCustomerDetailsOnToCollectionSheetEntryGridDto() throws Exception {

        // setup
        final Integer customerId = Integer.valueOf(7);

        final Integer customerAccountId = Integer.valueOf("55");
        final Integer savingsAccountId = Integer.valueOf("77");
        final String savingsProductShortName = "sav1";
        final Double savingsDepositDue = Double.valueOf("125.00");

        final Integer loanAccountId = Integer.valueOf("101");
        final String loanProductShortName = "lon1";
        final Double totalRepaymentDue = Double.valueOf("67.00");

        final Double totalCustomerAccountCollectionFee = Double.valueOf("29.87");
        final CollectionSheetCustomerAccountDto customerAccountDto = new CollectionSheetCustomerAccountDto(
                customerAccountId, defaultCurrencyId, totalCustomerAccountCollectionFee);

        final List<CollectionSheetCustomerDto> collectionSheetCustomer = Arrays.asList(centerCustomer);
        final List<CollectionSheetCustomerSavingDto> collectionSheetCustomerSavings = Arrays
                .asList(collectionSheetSaving);
        final List<CollectionSheetCustomerLoanDto> collectionSheetCustomerLoans = Arrays.asList(collectionSheetLoan);

        // stubbing
        when(collectionSheetData.getCollectionSheetCustomer()).thenReturn(collectionSheetCustomer);

        when(centerCustomer.getCustomerId()).thenReturn(customerId);
        when(centerCustomer.getCollectionSheetCustomerAccount()).thenReturn(customerAccountDto);
        when(centerCustomer.getCollectionSheetCustomerSaving()).thenReturn(collectionSheetCustomerSavings);
        when(centerCustomer.getCollectionSheetCustomerLoan()).thenReturn(collectionSheetCustomerLoans);

        // stub savings
        when(collectionSheetSaving.getCustomerId()).thenReturn(customerId);
        when(collectionSheetSaving.getAccountId()).thenReturn(savingsAccountId);
        when(collectionSheetSaving.getProductShortName()).thenReturn(savingsProductShortName);
        when(collectionSheetSaving.getTotalDepositAmount()).thenReturn(savingsDepositDue);

        // stub loans
        when(collectionSheetLoan.getCustomerId()).thenReturn(customerId);
        when(collectionSheetLoan.getAccountId()).thenReturn(loanAccountId);
        when(collectionSheetLoan.getProductShortName()).thenReturn(loanProductShortName);
        when(collectionSheetLoan.getPayInterestAtDisbursement()).thenReturn(Constants.NO);
        when(collectionSheetLoan.getTotalRepaymentDue()).thenReturn(totalRepaymentDue);
        when(collectionSheetLoan.getAccountStateId()).thenReturn(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue());

        // exercise test
        final CollectionSheetEntryGridDto formDto = collectionSheetDtoTranslator.toLegacyDto(collectionSheetData,
                formEnteredDataDto, null, defaultCurrency);

        // verification
        assertThat(formDto.getTotalCustomers(), is(collectionSheetCustomer.size()));

        // assert that the parent entry view exists with correctly populated
        final CollectionSheetEntryView collectionSheetEntryParent = formDto.getBulkEntryParent();
        assertThat(collectionSheetEntryParent, is(notNullValue()));

        assertThat(collectionSheetEntryParent.getAttendence(), is(Short.valueOf("0")));
        assertThat(collectionSheetEntryParent.getCountOfCustomers(), is(1));
        assertThat(collectionSheetEntryParent.getCustomerDetail().getCustomerId(), is(customerId));

        // customer account details
        assertThat(collectionSheetEntryParent.getCustomerAccountDetails().getAccountId(), is(customerAccountId));
        assertThat(collectionSheetEntryParent.getCustomerAccountDetails().getTotalAmountDue().getAmountDoubleValue(),
                is(totalCustomerAccountCollectionFee));

        // savings account
        List<SavingsAccountView> savingAccounts = collectionSheetEntryParent.getSavingsAccountDetails();
        assertThat(savingAccounts.size(), is(1));
        assertThat(savingAccounts.get(0).getAccountId(), is(savingsAccountId));
        assertThat(savingAccounts.get(0).getCustomerId(), is(customerId));
        assertThat(savingAccounts.get(0).getTotalDepositDue(), is(savingsDepositDue));

        // loan accounts
        List<LoanAccountsProductView> loanAccountProductViews = collectionSheetEntryParent.getLoanAccountDetails();
        assertThat(loanAccountProductViews.size(), is(1));
        assertThat(loanAccountProductViews.get(0).getPrdOfferingShortName(), is(loanProductShortName));
        assertThat(loanAccountProductViews.get(0).getTotalAmountDue(), is(totalRepaymentDue));

        assertThat(loanAccountProductViews.get(0).getLoanAccountViews().size(), is(1));
        assertThat(loanAccountProductViews.get(0).getLoanAccountViews().get(0).getTotalAmountDue(),
                is(totalRepaymentDue));
    }
}
