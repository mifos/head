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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryCustomerAccountInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryLoanInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntrySavingsInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.framework.util.helpers.Money;

import edu.emory.mathcs.backport.java.util.Collections;

public class CollectionSheetDtoTranslatorImpl implements CollectionSheetDtoTranslator {

    public CollectionSheetEntryGridDto toLegacyDto(final CollectionSheetDto collectionSheet,
            final CollectionSheetFormEnteredDataDto formEnteredDataDto, final List<CustomValueListElement> attendanceTypesList,
            final MifosCurrency currency) {
        
        final CollectionSheetEntryView collectionSheetEntryViewHierarchy = createEntryViewHierarchyFromCollectionSheetData(
                collectionSheet.getCollectionSheetCustomer(), currency);

        final PersonnelView loanOfficer = formEnteredDataDto.getLoanOfficer();
        final OfficeView office = formEnteredDataDto.getOffice();
        final ListItem<Short> paymentType = formEnteredDataDto.getPaymentType();
        final Date meetingDate = formEnteredDataDto.getMeetingDate();
        final String receiptId = formEnteredDataDto.getReceiptId();
        final Date receiptDate = formEnteredDataDto.getReceiptDate();

        final List<ProductDto> loanProductDtos = createListOfLoanProducts(collectionSheet.getCollectionSheetCustomer());
        final List<ProductDto> savingProductDtos = createListOfSavingProducts(collectionSheet
                .getCollectionSheetCustomer());

        final CollectionSheetEntryGridDto translatedGridDto = new CollectionSheetEntryGridDto(
                collectionSheetEntryViewHierarchy, loanOfficer, office, paymentType, meetingDate, receiptId,
                receiptDate, loanProductDtos, savingProductDtos, attendanceTypesList);

        return translatedGridDto;
    }
    
    private List<ProductDto> createListOfSavingProducts(final List<CollectionSheetCustomerDto> collectionSheetCustomer) {

        final Set<ProductDto> savingProductsSet = new HashSet<ProductDto>();

        for (CollectionSheetCustomerDto collectionSheetCustomerDto : collectionSheetCustomer) {

            for (CollectionSheetCustomerSavingDto saving : collectionSheetCustomerDto
                    .getCollectionSheetCustomerSaving()) {

                ProductDto productDto = new ProductDto(saving.getProductId(), saving.getProductShortName());
                savingProductsSet.add(productDto);
            }
        }

        List<ProductDto> savingProductsOrderedByName = new ArrayList<ProductDto>(savingProductsSet);
        Collections.sort(savingProductsOrderedByName, new ProductDtoComparator());

        return savingProductsOrderedByName;
    }

    private List<ProductDto> createListOfLoanProducts(final List<CollectionSheetCustomerDto> collectionSheetCustomer) {

        final Set<ProductDto> loanProductsSet = new HashSet<ProductDto>();

        for (CollectionSheetCustomerDto collectionSheetCustomerDto : collectionSheetCustomer) {

            for (CollectionSheetCustomerLoanDto loan : collectionSheetCustomerDto.getCollectionSheetCustomerLoan()) {

                ProductDto productDto = new ProductDto(loan.getProductId(), loan.getProductShortName());
                loanProductsSet.add(productDto);
            }
        }

        List<ProductDto> loanProductsOrderedByName = new ArrayList<ProductDto>(loanProductsSet);
        Collections.sort(loanProductsOrderedByName, new ProductDtoComparator());

        return loanProductsOrderedByName;
    }

    private CollectionSheetEntryView createEntryViewHierarchyFromCollectionSheetData(
            final List<CollectionSheetCustomerDto> collectionSheetCustomerHierarchy, final MifosCurrency currency) {

        final int countOfCustomers = collectionSheetCustomerHierarchy.size();
        CollectionSheetEntryView parentView = null;

        for (CollectionSheetCustomerDto customer : collectionSheetCustomerHierarchy) {

            final CustomerView parentCustomerDetail = new CustomerView(customer.getCustomerId(), customer.getName(),
                    customer.getParentCustomerId(), customer.getLevelId());

            CollectionSheetEntryView childView = new CollectionSheetEntryView(parentCustomerDetail, currency);
            childView.setAttendence(customer.getAttendanceId());
            childView.setCountOfCustomers(countOfCustomers);

            final Integer accountId = customer.getCollectionSheetCustomerAccount().getAccountId();
            final Integer customerId = customer.getCustomerId();
            final Short installmentId = null;
            final Integer actionDateId = null;
            final Date actionDate = null;

            final Money miscFee = new Money(customer.getCollectionSheetCustomerAccount()
                    .getTotalCustomerAccountCollectionFee().toString());
            final Money miscFeePaid = new Money("0.0");
            final Money miscPenalty = new Money("0.0");
            final Money miscPenaltyPaid = new Money("0.0");

            final CustomerAccountView customerAccountDetails = new CustomerAccountView(customer
                    .getCollectionSheetCustomerAccount().getAccountId(), customer.getCustomerId());
            customerAccountDetails.setAccountId(customer.getCollectionSheetCustomerAccount().getAccountId());

            // we only create one installment fee and set the total amount due
            // in the miscFee column for now
            final CollectionSheetEntryInstallmentView installmentView = new CollectionSheetEntryCustomerAccountInstallmentView(
                    accountId, customerId, installmentId, actionDateId, actionDate, miscFee, miscFeePaid, miscPenalty,
                    miscPenaltyPaid);
            final List<CollectionSheetEntryInstallmentView> installmentViewList = java.util.Arrays
                    .asList(installmentView);
            customerAccountDetails.setAccountActionDates(installmentViewList);

            childView.setCustomerAccountDetails(customerAccountDetails);

            // saving accounts
            for (CollectionSheetCustomerSavingDto customerSavingDto : customer.getCollectionSheetCustomerSaving()) {

                final Integer savCustomerId = customerSavingDto.getCustomerId();
                final Integer savAccountId = customerSavingDto.getAccountId();
                final String savingProductShortName = customerSavingDto.getProductShortName();
                final Short savOfferingId = customerSavingDto.getProductId();
                final Short savingsTypeId = Short.valueOf("1");
                final Short recommendedAmntUnitId = customerSavingDto.getRecommendedAmountUnitId();

                final SavingsAccountView savingsAccount = new SavingsAccountView(savAccountId, savCustomerId,
                        savingProductShortName, savOfferingId, savingsTypeId, recommendedAmntUnitId);

                final Short savInstallmentId = null;
                final Integer savActionDateId = null;
                final Date savActionDate = null;

                final Money savDeposit = new Money(customerSavingDto.getTotalDepositAmount().toString());
                final Money savDepositPaid = new Money("0.0");

                final CollectionSheetEntryInstallmentView accountTrxnDetail = new CollectionSheetEntrySavingsInstallmentView(
                        savAccountId, savCustomerId, savInstallmentId, savActionDateId, savActionDate, savDeposit,
                        savDepositPaid);
                savingsAccount.addAccountTrxnDetail(accountTrxnDetail);

                childView.addSavingsAccountDetail(savingsAccount);
            }

            // special savings accounts to be paid individually by clients
            for (CollectionSheetCustomerSavingDto clientIndividualSavingsAccount : customer
                    .getIndividualSavingAccounts()) {

                final Integer savCustomerId = clientIndividualSavingsAccount.getCustomerId();
                final Integer savAccountId = clientIndividualSavingsAccount.getAccountId();
                final String savingProductShortName = clientIndividualSavingsAccount.getProductShortName();
                final Short savOfferingId = clientIndividualSavingsAccount.getProductId();
                final Short savingsTypeId = Short.valueOf("1");
                final Short recommendedAmntUnitId = Short.valueOf("1");

                final SavingsAccountView savingsAccount = new SavingsAccountView(savAccountId, savCustomerId,
                        savingProductShortName, savOfferingId, savingsTypeId, recommendedAmntUnitId);

                final Short savInstallmentId = null;
                final Integer savActionDateId = null;
                final Date savActionDate = null;

                final Money savDeposit = new Money(clientIndividualSavingsAccount.getDepositDue().toString());
                final Money savDepositPaid = new Money(clientIndividualSavingsAccount.getDepositPaid().toString());

                final CollectionSheetEntryInstallmentView accountTrxnDetail = new CollectionSheetEntrySavingsInstallmentView(
                        savAccountId, savCustomerId, savInstallmentId, savActionDateId, savActionDate, savDeposit,
                        savDepositPaid);
                savingsAccount.addAccountTrxnDetail(accountTrxnDetail);

                childView.addSavingsAccountDetail(savingsAccount);
            }

            // loan accounts
            for (CollectionSheetCustomerLoanDto customerLoanDto : customer.getCollectionSheetCustomerLoan()) {

                final Integer loanAccountId = customerLoanDto.getAccountId();
                final Integer loanCustomerId = customerLoanDto.getCustomerId();
                final String loanOfferingShortName = customerLoanDto.getProductShortName();
                final Short loanOfferingId = customerLoanDto.getProductId();
                final Short loanInstallmentId = null;
                final Integer loanActionDateId = null;
                final Date loanActionDate = null;
                final Short loanAccountState = customerLoanDto.getAccountStateId();
                final Short interestDeductedAtDisbursement = customerLoanDto.getPayInterestAtDisbursement();
                final Money loanAmount = new Money(customerLoanDto.getTotalDisbursement().toString());

                final Money principal = new Money(customerLoanDto.getTotalRepaymentDue().toString());

                final LoanAccountView loanAccount = new LoanAccountView(loanAccountId, loanCustomerId,
                        loanOfferingShortName, loanOfferingId, loanAccountState, interestDeductedAtDisbursement,
                        loanAmount);
                loanAccount.setAmountPaidAtDisbursement(customerLoanDto.getAmountDueAtDisbursement());

                final CollectionSheetEntryInstallmentView accountTrxnDetail = new CollectionSheetEntryLoanInstallmentView(
                        loanAccountId, loanCustomerId, loanInstallmentId, loanActionDateId, loanActionDate, principal,
                        new Money(), new Money(), new Money(), new Money(), new Money(), new Money(), new Money(),
                        new Money(), new Money());
                loanAccount.addTrxnDetails(Arrays.asList(accountTrxnDetail));

                childView.addLoanAccountDetails(loanAccount);
            }

            // center-group-client hierarchy
            if (parentView == null) {
                parentView = childView;
            } else {
                addChildToAppropriateParent(parentView, childView);
            }
        }

        return parentView;
    }

    private boolean addChildToAppropriateParent(final CollectionSheetEntryView rootNode,
            final CollectionSheetEntryView childNodeToBeAdded) {

        if (childNodeToBeAdded.getCustomerDetail().getParentCustomerId().equals(
                rootNode.getCustomerDetail().getCustomerId())) {
            rootNode.addChildNode(childNodeToBeAdded);
            return true;
        }

        for (CollectionSheetEntryView child : rootNode.getCollectionSheetEntryChildren()) {
            if (addChildToAppropriateParent(child, childNodeToBeAdded)) {
                return true;
            }
        }

        return false;
    }
}

class ProductDtoComparator implements Comparator<ProductDto> {

    @Override
    public int compare(final ProductDto arg0, final ProductDto arg1) {
        return arg0.getShortName().compareTo(arg1.getShortName());
    }
}