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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryCustomerAccountInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryLoanInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntrySavingsInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Default implementation of {@link CollectionSheetServiceFacade}.
 */
public class CollectionSheetServiceFacadeWebTier implements CollectionSheetServiceFacade {

    private final OfficePersistence officePersistence;
    private final MasterPersistence masterPersistence;
    private final PersonnelPersistence personnelPersistence;
    private final CustomerPersistence customerPersistence;
    private final CollectionSheetService collectionSheetService;
    private final CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler;
    private final ClientAttendanceAssembler clientAttendanceAssembler;
    private final LoanAccountAssembler loanAccountAssembler;
    private final CustomerAccountAssembler customerAccountAssembler;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final AccountPaymentAssembler accountPaymentAssembler;

    public CollectionSheetServiceFacadeWebTier(final OfficePersistence officePersistence,
            final MasterPersistence masterPersistence, final PersonnelPersistence personnelPersistence,
            final CustomerPersistence customerPersistence, final CollectionSheetService collectionSheetService,
            final CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler,
            final ClientAttendanceAssembler clientAttendanceAssembler, final LoanAccountAssembler loanAccountAssembler,
            final CustomerAccountAssembler customerAccountAssember,
            final SavingsAccountAssembler savingsAccountAssembler, final AccountPaymentAssembler accountPaymentAssembler) {
        this.officePersistence = officePersistence;
        this.masterPersistence = masterPersistence;
        this.personnelPersistence = personnelPersistence;
        this.customerPersistence = customerPersistence;
        this.collectionSheetService = collectionSheetService;
        this.collectionSheetEntryGridViewAssembler = collectionSheetEntryGridViewAssembler;
        this.clientAttendanceAssembler = clientAttendanceAssembler;
        this.loanAccountAssembler = loanAccountAssembler;
        this.customerAccountAssembler = customerAccountAssember;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.accountPaymentAssembler = accountPaymentAssembler;
    }

    public CollectionSheetEntryFormDto loadAllActiveBranchesAndSubsequentDataIfApplicable(final UserContext userContext) {

        final Short branchId = userContext.getBranchId();
        final Short centerHierarchyExists = ClientRules.getCenterHierarchyExists() ? Constants.YES : Constants.NO;

        List<OfficeView> activeBranches = new ArrayList<OfficeView>();
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        List<CustomerView> customerList = new ArrayList<CustomerView>();
        List<PersonnelView> loanOfficerList = new ArrayList<PersonnelView>();
        Short reloadFormAutomatically = Constants.YES;
        final Short backDatedTransactionAllowed = Constants.NO;

        try {
            final List<MasterDataEntity> paymentTypesList = masterPersistence.retrieveMasterEntities(
                    PaymentTypeEntity.class, Short.valueOf("1"));
            paymentTypesDtoList = convertToPaymentTypesListItemDto(paymentTypesList);

            activeBranches = officePersistence.getActiveOffices(branchId);
            if (activeBranches.size() == 1) {
                loanOfficerList = personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
                        branchId, userContext.getId(), userContext.getLevelId());

                if (loanOfficerList.size() == 1) {

                    Short customerLevel;
                    if (centerHierarchyExists.equals(Constants.YES)) {
                        customerLevel = Short.valueOf(CustomerLevel.CENTER.getValue());
                    } else {
                        customerLevel = Short.valueOf(CustomerLevel.GROUP.getValue());
                    }

                    customerList = customerPersistence.getActiveParentList(loanOfficerList.get(0).getPersonnelId(),
                            customerLevel, branchId);

                    if (customerList.size() == 1) {
                        reloadFormAutomatically = Constants.YES;
                    }

                    reloadFormAutomatically = Constants.NO;
                }
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        return new CollectionSheetEntryFormDto(activeBranches, paymentTypesDtoList, loanOfficerList, customerList,
                reloadFormAutomatically, centerHierarchyExists, backDatedTransactionAllowed);
    }

    public CollectionSheetEntryFormDto loadLoanOfficersForBranch(final Short branchId, final UserContext userContext,
            final CollectionSheetEntryFormDto formDto) {

        List<PersonnelView> loanOfficerList = new ArrayList<PersonnelView>();
        try {
            loanOfficerList = personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
                    branchId, userContext.getId(), userContext.getLevelId());
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(),
                loanOfficerList, formDto.getCustomerList(), formDto.getReloadFormAutomatically(), formDto
                        .getCenterHierarchyExists(), formDto.getBackDatedTransactionAllowed());
    }

    public CollectionSheetEntryFormDto loadCustomersForBranchAndLoanOfficer(final Short personnelId,
            final Short officeId, final CollectionSheetEntryFormDto formDto) {

        Short customerLevel;
        if (formDto.getCenterHierarchyExists().equals(Constants.YES)) {
            customerLevel = Short.valueOf(CustomerLevel.CENTER.getValue());
        } else {
            customerLevel = Short.valueOf(CustomerLevel.GROUP.getValue());
        }

        List<CustomerView> customerList = new ArrayList<CustomerView>();
        try {
            customerList = customerPersistence.getActiveParentList(personnelId, customerLevel, officeId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(), formDto
                .getLoanOfficerList(), customerList, formDto.getReloadFormAutomatically(), formDto
                .getCenterHierarchyExists(), formDto.getBackDatedTransactionAllowed());
    }

    public CollectionSheetEntryFormDto loadMeetingDateForCustomer(final Integer customerId,
            final CollectionSheetEntryFormDto formDto) {

        Short backDatedTransactionAllowed = Constants.NO;
        java.util.Date meetingDate = null;
        try {
            meetingDate = customerPersistence.getLastMeetingDateForCustomer(customerId);

            final boolean isBackDatedTrxnAllowed = AccountingRules.isBackDatedTxnAllowed();
            if (meetingDate == null) {
                meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
            }
            backDatedTransactionAllowed = isBackDatedTrxnAllowed ? Constants.YES : Constants.NO;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(), formDto
                .getLoanOfficerList(), formDto.getCustomerList(), formDto.getReloadFormAutomatically(), formDto
                .getCenterHierarchyExists(), backDatedTransactionAllowed, meetingDate);
    }

    public CollectionSheetEntryGridDto generateCollectionSheetEntryGridView(
            final CollectionSheetFormEnteredDataDto formEnteredDataDto, final MifosCurrency currency) {

         final CollectionSheetEntryGridDto collectionSheetGridView = collectionSheetEntryGridViewAssembler
                .toDto(formEnteredDataDto);

        return collectionSheetGridView;

        // FIXME - keithw - commented out code for tranlation new approach for
        // fethcing data
        // approx 2 days left to complete.
        // final CollectionSheetDto collectionSheet =
        // collectionSheetService.retrieveCollectionSheet(formEnteredDataDto
        // .getCustomer().getCustomerId(), formEnteredDataDto.getMeetingDate());
        //
        // try {
        // final List<CustomValueListElement> attendanceTypesList =
        // masterPersistence.getCustomValueList(
        // MasterConstants.ATTENDENCETYPES,
        // "org.mifos.application.master.business.CustomerAttendanceType",
        // "attendanceId").getCustomValueListElements();
        //
        // final CollectionSheetEntryGridDto translatedGridView =
        // translate(collectionSheet, formEnteredDataDto,
        // attendanceTypesList, currency);
        //
        // return translatedGridView;
        // } catch (SystemException e) {
        // throw new MifosRuntimeException(e);
        // } catch (ApplicationException e) {
        // throw new MifosRuntimeException(e);
        // }
    }

    public CollectionSheetEntryGridDto translate(final CollectionSheetDto collectionSheet,
            final CollectionSheetFormEnteredDataDto formEnteredDataDto,
            final List<CustomValueListElement> attendanceTypesList, final MifosCurrency currency) {
        
        final CollectionSheetEntryView collectionSheetEntryViewHierarchy = createEntryViewHierarchyFromCollectionSheetData(collectionSheet
                .getCollectionSheetCustomer(), currency);
        
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
                collectionSheetEntryViewHierarchy,
                loanOfficer, office, paymentType, meetingDate, receiptId, receiptDate, loanProductDtos,
                savingProductDtos, attendanceTypesList);
        
        return translatedGridDto;
    }

    public CollectionSheetEntryGridDto previewCollectionSheetEntry(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto, final CollectionSheetDataView dataView) {

        CollectionSheetEntryGridDto newCollectionSheetEntryGridDto = null;

        final CollectionSheetEntryView bulkEntryParent = previousCollectionSheetEntryDto.getBulkEntryParent();
        switch (CustomerLevel.getLevel(bulkEntryParent.getCustomerDetail().getCustomerLevelId())) {
        case CENTER:
            newCollectionSheetEntryGridDto = new CollectionSheetEntryGridDtoTranslator().translateAsCenter(
                    previousCollectionSheetEntryDto, dataView);
            break;
        case GROUP:
        case CLIENT:
            newCollectionSheetEntryGridDto = new CollectionSheetEntryGridDtoTranslator().translateAsGroup(
                    previousCollectionSheetEntryDto, dataView);
            break;
        }

        return newCollectionSheetEntryGridDto;
    }

    public CollectionSheetErrorsView saveCollectionSheet(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto,
            final CollectionSheetEntryDecomposedView decomposedViews, final Short userId) {

        final AccountPaymentEntity payment = accountPaymentAssembler.fromDto(userId, previousCollectionSheetEntryDto);

        final List<String> failedSavingsDepositAccountNums = new ArrayList<String>();
        final List<String> failedSavingsWithdrawalNums = new ArrayList<String>();
        final List<String> failedCustomerAccountPaymentNums = new ArrayList<String>();

        final List<CollectionSheetEntryView> collectionSheeetEntryViews = decomposedViews
                .getParentCollectionSheetEntryViews();
        final List<SavingsBO> savingsAccounts = savingsAccountAssembler.fromDto(collectionSheeetEntryViews,
                previousCollectionSheetEntryDto, userId, failedSavingsDepositAccountNums, failedSavingsWithdrawalNums);

        final List<CollectionSheetEntryView> collectionSheetEntryViews = decomposedViews
                .getParentCollectionSheetEntryViews();
        final List<ClientAttendanceBO> clientAttendances = clientAttendanceAssembler.fromDto(collectionSheetEntryViews,
                payment.getPaymentDate());

        final List<LoanAccountsProductView> loanAccountProductViews = decomposedViews.getLoanAccountViews();
        final List<LoanBO> loanAccounts = loanAccountAssembler.fromDto(loanAccountProductViews, payment);

        final List<CustomerAccountView> customerAccountViews = decomposedViews.getCustomerAccountViews();
        final List<AccountBO> customerAccounts = customerAccountAssembler.fromDto(customerAccountViews, payment,
                failedCustomerAccountPaymentNums);

        collectionSheetService.saveCollectionSheet(clientAttendances, loanAccounts, customerAccounts, savingsAccounts);

        return new CollectionSheetErrorsView(failedSavingsDepositAccountNums, failedSavingsWithdrawalNums,
                failedCustomerAccountPaymentNums);
    }

    private List<ListItem<Short>> convertToPaymentTypesListItemDto(final List<MasterDataEntity> paymentTypesList) {
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        for (MasterDataEntity paymentType : paymentTypesList) {
            paymentTypesDtoList.add(new ListItem<Short>(paymentType.getId(), paymentType.getName()));
        }
        return paymentTypesDtoList;
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

        final Map<Integer, SavingsAccountView> perIndividualSavingAccountsByGroupId = new HashMap<Integer, SavingsAccountView>();
        final int countOfCustomers = collectionSheetCustomerHierarchy.size();
        CollectionSheetEntryView parentView = null;
        
        for (CollectionSheetCustomerDto customer : collectionSheetCustomerHierarchy) {

            final CustomerView parentCustomerDetail = new CustomerView(customer.getCustomerId(),
                    customer.getName(), customer.getParentCustomerId(),
                    customer.getLevelId());

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
            customerAccountDetails.setAccountId(customer.getCollectionSheetCustomerAccount()
                    .getAccountId());

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
            for (CollectionSheetCustomerSavingDto customerSavingDto : customer
                    .getCollectionSheetCustomerSaving()) {

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
                
                if (RecommendedAmountUnit.PER_INDIVIDUAL.getValue().equals(recommendedAmntUnitId)
                        && CustomerLevel.GROUP.getValue().equals(customer.getLevelId())) {
                    perIndividualSavingAccountsByGroupId.put(customerId, savingsAccount);
                } else {
                    childView.addSavingsAccountDetail(savingsAccount);
                }
                
                childView.addSavingsAccountDetail(savingsAccount);
            }

            // loan accounts
            for (CollectionSheetCustomerLoanDto customerLoanDto : customer
                    .getCollectionSheetCustomerLoan()) {

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
                        loanOfferingShortName, loanOfferingId, loanAccountState, interestDeductedAtDisbursement, loanAmount);
                loanAccount.setAmountPaidAtDisbursement(customerLoanDto.getAmountDueAtDisbursement());

                final CollectionSheetEntryInstallmentView accountTrxnDetail = new CollectionSheetEntryLoanInstallmentView(
                        loanAccountId, loanCustomerId, loanInstallmentId, loanActionDateId, loanActionDate, principal,
                        new Money(), new Money(), new Money(), new Money(), new Money(), new Money(), new Money(),
                        new Money(), new Money());
                loanAccount.addTrxnDetails(Arrays.asList(accountTrxnDetail));

                childView.addLoanAccountDetails(loanAccount);
            }
            
            // handle per-individual savings accounts on groups
            if (CustomerLevel.CLIENT.getValue().equals(customer.getLevelId())) {
                final Integer parentCustomerId = customer.getParentCustomerId();
                if (parentCustomerId != null) {
                    final SavingsAccountView perIndivualSavingsApplicableToClient = perIndividualSavingAccountsByGroupId
                            .get(parentCustomerId);
                    if (perIndivualSavingsApplicableToClient != null) {
                        childView.addSavingsAccountDetail(perIndivualSavingsApplicableToClient);
                    }
                }
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
