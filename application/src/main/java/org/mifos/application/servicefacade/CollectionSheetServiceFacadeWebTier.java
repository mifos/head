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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.business.service.CollectionSheetEntryBusinessService;
import org.mifos.application.collectionsheet.util.helpers.BulkEntrySavingsCache;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;

/**
 * Default implementation of CollectionSheetServiceFacade.
 */
public class CollectionSheetServiceFacadeWebTier implements CollectionSheetServiceFacade {

    private final OfficePersistence officePersistence;
    private final MasterPersistence masterPersistence;
    private final PersonnelPersistence personnelPersistence;
    private final CustomerPersistence customerPersistence;
    private final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler;
    private final CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler;
    private final CollectionSheetEntryBusinessService collectionSheetEntryService;
    private final CollectionSheetEntryViewTranslator collectionSheetEntryViewTranslator;

    public CollectionSheetServiceFacadeWebTier(OfficePersistence officePersistence,
            MasterPersistence masterPersistence, PersonnelPersistence personnelPersistence,
            CustomerPersistence customerPersistence,
            CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler,
            CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler,
            CollectionSheetEntryBusinessService collectionSheetEntryService,
            CollectionSheetEntryViewTranslator collectionSheetEntryViewTranslator) {
        this.officePersistence = officePersistence;
        this.masterPersistence = masterPersistence;
        this.personnelPersistence = personnelPersistence;
        this.customerPersistence = customerPersistence;
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
        Date meetingDate = null;
        try {
            meetingDate = customerPersistence.getLastMeetingDateForCustomer(customerId);

            final boolean isBackDatedTrxnAllowed = AccountingRules.isBackDatedTxnAllowed();
            if (meetingDate == null && !isBackDatedTrxnAllowed) {
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
        
        final CollectionSheetEntryView bulkEntryParent = collectionSheetEntryViewAssembler.toDto(formEnteredDataDto);
        final CollectionSheetEntryGridDto collectionSheetGridView = collectionSheetEntryGridViewAssembler
                .toDto(
                formEnteredDataDto, userContext.getLocaleId());
        
        collectionSheetGridView.setTotalCustomers(bulkEntryParent.getCountOfCustomers());
        collectionSheetGridView.setBulkEntryParent(bulkEntryParent);
        
        return collectionSheetGridView;
    }
    
    public CollectionSheetEntryGridDto previewCollectionSheetEntry(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto, final CollectionSheetDataView dataView) {

        // TODO - keithw - pull out this and refactor from
        // CollectionSheetEntryBO
        previousCollectionSheetEntryDto.setcollectionSheetDataView(dataView);

        return previousCollectionSheetEntryDto;
    }

    public ErrorAndCollectionSheetDataDto prepareDataForCollectionSheetEntrySave(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto,
            final Short userId) {

        final CollectionSheetEntryDecomposedView decomposedViews = collectionSheetEntryViewTranslator
                .toDecomposedView(previousCollectionSheetEntryDto.getBulkEntryParent());

        final List<ClientBO> clients = new ArrayList<ClientBO>();
        final List<SavingsBO> savingsAccounts = new ArrayList<SavingsBO>();
        final List<String> customerNames = new ArrayList<String>();
        final List<String> savingsDepNames = new ArrayList<String>();
        final List<String> savingsWithNames = new ArrayList<String>();

        final Map<Integer, BulkEntrySavingsCache> savingsCache = new HashMap<Integer, BulkEntrySavingsCache>();

        collectionSheetEntryService.setData(decomposedViews.getParentCollectionSheetEntryViews(), savingsCache,
                savingsDepNames, savingsWithNames, userId, previousCollectionSheetEntryDto
                        .getReceiptId(), previousCollectionSheetEntryDto.getPaymentType().getId(),
                previousCollectionSheetEntryDto
                        .getReceiptDate(), previousCollectionSheetEntryDto.getTransactionDate());

        for (Integer accountId : savingsCache.keySet()) {
            final BulkEntrySavingsCache bulkEntrySavingsCache = savingsCache.get(accountId);
            if (bulkEntrySavingsCache.getYesNoFlag().equals(YesNoFlag.YES)) {
                savingsAccounts.add(bulkEntrySavingsCache.getAccount());
            }
        }

        final CollectionSheetErrorsView errors = new CollectionSheetErrorsView(customerNames, savingsDepNames,
                savingsWithNames);

        return new ErrorAndCollectionSheetDataDto(decomposedViews, errors, savingsAccounts, clients);
    }

    private List<ListItem<Short>> convertToPaymentTypesListItemDto(List<MasterDataEntity> paymentTypesList) {
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        for (MasterDataEntity paymentType : paymentTypesList) {
            paymentTypesDtoList.add(new ListItem<Short>(paymentType.getId(), paymentType.getName()));
        }
        return paymentTypesDtoList;
    }
}
