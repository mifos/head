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
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.business.service.CollectionSheetEntryBusinessService;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * Default implementation of CollectionSheetServiceFacade.
 */
public class CollectionSheetServiceFacadeWebTier implements CollectionSheetServiceFacade {

    private final OfficePersistence officePersistence;
    private final MasterPersistence masterPersistence;
    private final PersonnelPersistence personnelPersistence;
    private final CustomerPersistence customerPersistence;
    private final SavingsPersistence savingsPersistence;
    private final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler;
    private final CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler;
    private final CollectionSheetEntryBusinessService collectionSheetEntryService;
    private final CollectionSheetEntryViewTranslator collectionSheetEntryViewTranslator;

    public CollectionSheetServiceFacadeWebTier(OfficePersistence officePersistence,
            MasterPersistence masterPersistence, PersonnelPersistence personnelPersistence,
            CustomerPersistence customerPersistence,
            SavingsPersistence savingsPersistence,
            CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler,
            CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler,
            CollectionSheetEntryBusinessService collectionSheetEntryService,
            CollectionSheetEntryViewTranslator collectionSheetEntryViewTranslator) {
        this.officePersistence = officePersistence;
        this.masterPersistence = masterPersistence;
        this.personnelPersistence = personnelPersistence;
        this.customerPersistence = customerPersistence;
        this.savingsPersistence = savingsPersistence;
        this.collectionSheetEntryViewAssembler = collectionSheetEntryViewAssembler;
        this.collectionSheetEntryGridViewAssembler = collectionSheetEntryGridViewAssembler;
        this.collectionSheetEntryService = collectionSheetEntryService;
        this.collectionSheetEntryViewTranslator = collectionSheetEntryViewTranslator;
    }

    public CollectionSheetEntryFormDto loadAllActiveBranchesAndSubsequentDataIfPossible(final UserContext userContext) {

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
        
        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(),
                loanOfficerList, formDto.getCustomerList(), formDto.getReloadFormAutomatically(), formDto
                        .getCenterHierarchyExists(), formDto.getBackDatedTransactionAllowed());
    }

    public CollectionSheetEntryFormDto loadCustomersForBranchAndLoanOfficer(Short personnelId, Short officeId,
            CollectionSheetEntryFormDto formDto) {

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }

        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(), formDto
                .getLoanOfficerList(), formDto.getCustomerList(), formDto.getReloadFormAutomatically(), formDto
                .getCenterHierarchyExists(), backDatedTransactionAllowed, meetingDate);
    }

    public CollectionSheetEntryGridDto generateCollectionSheetEntryGridView(
            final CollectionSheetFormEnteredDataDto formEnteredDataDto,
            final UserContext userContext) {
        
        final CollectionSheetEntryView collectionSheetParent = collectionSheetEntryViewAssembler.toDto(formEnteredDataDto);
        final CollectionSheetEntryGridDto collectionSheetGridView = collectionSheetEntryGridViewAssembler
                .toDto(
                formEnteredDataDto, userContext.getLocaleId(), collectionSheetParent);
        
        return collectionSheetGridView;
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
    
    public ErrorAndCollectionSheetDataDto prepareSavingAccountsForCollectionSheetEntrySave(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto,
            final Short userId) {

        final CollectionSheetEntryDecomposedView decomposedViews = collectionSheetEntryViewTranslator
                .toDecomposedView(previousCollectionSheetEntryDto.getBulkEntryParent());

        final List<SavingsBO> savingsAccounts = new ArrayList<SavingsBO>();
        final List<String> failedSavingsDepositAccountNums = new ArrayList<String>();
        final List<String> failedSavingsWithdrawalNums = new ArrayList<String>();

        for (CollectionSheetEntryView parent : decomposedViews.getParentCollectionSheetEntryViews()) {

            for (SavingsAccountView savingAccount : parent.getSavingsAccountDetails()) {
                
                // create set of SavingAccountView's to ensure no duplicate's
                final Money amountToDeposit = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                        savingAccount.getDepositAmountEntered());
                final Money amountToWithdraw = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                        savingAccount.getWithDrawalAmountEntered());

                final String receiptNumber = previousCollectionSheetEntryDto.getReceiptId();
                final Date receiptDate = previousCollectionSheetEntryDto.getReceiptDate();
                final PaymentTypeEntity paymentType = new PaymentTypeEntity(previousCollectionSheetEntryDto
                        .getPaymentType().getId());
                final Date paymentDate = previousCollectionSheetEntryDto.getTransactionDate();
                
                final Integer accountId = savingAccount.getAccountId();
                boolean storeAccountForSavingLater = false;
                try {
                    final SavingsBO account = savingsPersistence.findById(accountId);
                    final PersonnelBO user = personnelPersistence.findPersonnelById(userId);
                    
                    final List<SavingsScheduleEntity> scheduledDeposits = new ArrayList<SavingsScheduleEntity>();
                    final List<CollectionSheetEntryInstallmentView> scheduledSavingInstallments = savingAccount
                            .getAccountTrxnDetails();
                    
                    for (CollectionSheetEntryInstallmentView installment : scheduledSavingInstallments) {
                        SavingsScheduleEntity savingsScheduledInstallment = (SavingsScheduleEntity) account
                                .getAccountActionDate(installment.getInstallmentId());
                        scheduledDeposits.add(savingsScheduledInstallment);
                    }
                    
                    if (amountToDeposit.getAmount() != null
                            && amountToDeposit.getAmountDoubleValue() > Double.valueOf("0.0")) {

                        final AccountPaymentEntity accountDeposit = new AccountPaymentEntity(account, amountToDeposit,
                                receiptNumber, receiptDate, paymentType, paymentDate);
                        accountDeposit.setCreatedByUser(user);

                        try {
                            account.deposit(accountDeposit, scheduledDeposits);
                            storeAccountForSavingLater = true;
                        } catch (AccountException e) {
                            failedSavingsDepositAccountNums.add(account.getGlobalAccountNum());
                        }
                    }
                    
                    if (amountToWithdraw.getAmount() != null
                            && amountToWithdraw.getAmountDoubleValue() > Double.valueOf("0.0")) {
                        final AccountPaymentEntity accountWithdrawal = new AccountPaymentEntity(account,
                                amountToWithdraw, receiptNumber, receiptDate, paymentType, paymentDate);
                        accountWithdrawal.setCreatedByUser(user);

                        try {
                            account.withdraw(accountWithdrawal);
                            storeAccountForSavingLater = true;
                        } catch (AccountException e) {
                            failedSavingsWithdrawalNums.add(account.getGlobalAccountNum());
                        }
                    }
                    
                    if (storeAccountForSavingLater) {
                        savingsAccounts.add(account);
                    }
                    
                } catch (PersistenceException pe) {
                    throw new RuntimeException(pe);
                }
            }
        }

        final CollectionSheetErrorsView errors = new CollectionSheetErrorsView(failedSavingsDepositAccountNums,
                failedSavingsWithdrawalNums,
                new ArrayList<String>());

        return new ErrorAndCollectionSheetDataDto(decomposedViews, errors, savingsAccounts);
    }
    
    public CollectionSheetErrorsView saveCollectionSheet(CollectionSheetEntryGridDto previousCollectionSheetEntryDto,
            ErrorAndCollectionSheetDataDto errorAndCollectionSheetDataDto, final Short userId) {

        // TODO - keithw - completely refactor how the collectionSheet entries
        // are saved
        // NOTE: CollectionSheetEntry is not a concept in our Domain model.
        // As far as i understand its a batch save of accounts (loanAccounts and
        // savingAccounts) and other info such as transactions/meetings etc
        // on loan accounts: a loan is disbursed (money given out to
        // clients(customer))
        // on saving accounts: money is saved or withdrawn?

        java.sql.Date receiptDate = null;
        if (previousCollectionSheetEntryDto.getReceiptDate() != null) {
            receiptDate = new java.sql.Date(previousCollectionSheetEntryDto.getReceiptDate().getTime());
        }
        
        collectionSheetEntryService.saveData(errorAndCollectionSheetDataDto.getDecomposedViews().getLoanAccountViews(),
                userId, previousCollectionSheetEntryDto.getReceiptId(),
                previousCollectionSheetEntryDto
                        .getPaymentType().getId(), receiptDate, new java.sql.Date(previousCollectionSheetEntryDto
                        .getTransactionDate().getTime()),
                errorAndCollectionSheetDataDto.getSavingsAccounts(), errorAndCollectionSheetDataDto.getErrors()
                        .getSavingsDepNames(),
                errorAndCollectionSheetDataDto.getDecomposedViews().getCustomerAccountViews(),
                errorAndCollectionSheetDataDto.getErrors().getCustomerAccountNumbers(),
                errorAndCollectionSheetDataDto.getDecomposedViews().getParentCollectionSheetEntryViews());

        // // TODO - keithw - implement the following service for saving
        // collection sheets.
        // CollectionSheetService collectionSheetServiceToBeImplemented = new
        // CollectionSheetServiceImpl();
        //
        // // 1. build up model correctly from DTO information
        // // fetch account/client information
        // // make disbursals/withdrawals per account
        // List<AccountBO> accounts = new ArrayList<AccountBO>();
        // collectionSheetServiceToBeImplemented.saveCollectionSheet(accounts);
        
        return errorAndCollectionSheetDataDto.getErrors();
    }

    private List<ListItem<Short>> convertToPaymentTypesListItemDto(List<MasterDataEntity> paymentTypesList) {
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        for (MasterDataEntity paymentType : paymentTypesList) {
            paymentTypesDtoList.add(new ListItem<Short>(paymentType.getId(), paymentType.getName()));
        }
        return paymentTypesDtoList;
    }
}
