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
package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryDto;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataDto;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default implementation of {@link CollectionSheetServiceFacade}.
 */
public class CollectionSheetServiceFacadeWebTier implements CollectionSheetServiceFacade {

    private OfficePersistence officePersistence = new OfficePersistence();
    private CustomerPersistence customerPersistence = new CustomerPersistence();
    private final CollectionSheetService collectionSheetService;
    private final CollectionSheetDtoTranslator collectionSheetTranslator;

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    private LegacyPersonnelDao legacyPersonnelDao;


    @Autowired
    public CollectionSheetServiceFacadeWebTier(final CollectionSheetService collectionSheetService,
            final CollectionSheetDtoTranslator collectionSheetTranslator) {
        this.collectionSheetService = collectionSheetService;
        this.collectionSheetTranslator = collectionSheetTranslator;
    }

    public CollectionSheetServiceFacadeWebTier(final OfficePersistence officePersistence,
            final LegacyMasterDao legacyMasterDao, final LegacyPersonnelDao personnelPersistence,
            final CustomerPersistence customerPersistence, final CollectionSheetService collectionSheetService,
            final CollectionSheetDtoTranslator collectionSheetTranslator) {
        this.officePersistence = officePersistence;
        this.legacyMasterDao = legacyMasterDao;
        this.legacyPersonnelDao = personnelPersistence;
        this.customerPersistence = customerPersistence;
        this.collectionSheetService = collectionSheetService;
        this.collectionSheetTranslator = collectionSheetTranslator;
    }

    @Override
	public CollectionSheetEntryFormDto loadAllActiveBranchesAndSubsequentDataIfApplicable(final UserContext userContext) {

        final Short branchId = userContext.getBranchId();
        final Short centerHierarchyExists = ClientRules.getCenterHierarchyExists() ? Constants.YES : Constants.NO;

        List<OfficeDetailsDto> activeBranches = new ArrayList<OfficeDetailsDto>();
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        List<CustomerDto> customerList = new ArrayList<CustomerDto>();
        List<PersonnelDto> loanOfficerList = new ArrayList<PersonnelDto>();
        Short reloadFormAutomatically = Constants.YES;
        final Short backDatedTransactionAllowed = Constants.NO;

        try {
            final List<PaymentTypeEntity> paymentTypesList = legacyMasterDao.findMasterDataEntitiesWithLocale(
                    PaymentTypeEntity.class);
            paymentTypesDtoList = convertToPaymentTypesListItemDto(paymentTypesList);

            activeBranches = officePersistence.getActiveOffices(branchId);
            if (activeBranches.size() == 1) {
                loanOfficerList = legacyPersonnelDao.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
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

    @Override
	public CollectionSheetEntryFormDto loadLoanOfficersForBranch(final Short branchId, final UserContext userContext,
            final CollectionSheetEntryFormDto formDto) {

        List<PersonnelDto> loanOfficerList = new ArrayList<PersonnelDto>();
        try {
            loanOfficerList = legacyPersonnelDao.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER,
                    branchId, userContext.getId(), userContext.getLevelId());
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(),
                loanOfficerList, formDto.getCustomerList(), formDto.getReloadFormAutomatically(), formDto
                        .getCenterHierarchyExists(), formDto.getBackDatedTransactionAllowed());
    }

    @Override
	public CollectionSheetEntryFormDto loadCustomersForBranchAndLoanOfficer(final Short personnelId,
            final Short officeId, final CollectionSheetEntryFormDto formDto) {

        Short customerLevel;
        if (formDto.getCenterHierarchyExists().equals(Constants.YES)) {
            customerLevel = Short.valueOf(CustomerLevel.CENTER.getValue());
        } else {
            customerLevel = Short.valueOf(CustomerLevel.GROUP.getValue());
        }

        List<CustomerDto> customerList = new ArrayList<CustomerDto>();
        try {
            customerList = customerPersistence.getActiveParentList(personnelId, customerLevel, officeId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
        return new CollectionSheetEntryFormDto(formDto.getActiveBranchesList(), formDto.getPaymentTypesList(), formDto
                .getLoanOfficerList(), customerList, formDto.getReloadFormAutomatically(), formDto
                .getCenterHierarchyExists(), formDto.getBackDatedTransactionAllowed());
    }

    @Override
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

    @Override
	public CollectionSheetEntryGridDto generateCollectionSheetEntryGridView(
            final CollectionSheetFormEnteredDataDto formEnteredDataDto, final MifosCurrency currency) {

        final CollectionSheetDto collectionSheet = getCollectionSheet(formEnteredDataDto
                .getCustomer().getCustomerId(), DateUtils.getLocalDateFromDate(formEnteredDataDto.getMeetingDate()));

        try {
            final List<CustomValueListElementDto> attendanceTypesList = legacyMasterDao.getCustomValueList(
                    MasterConstants.ATTENDENCETYPES, "org.mifos.application.master.business.CustomerAttendanceType",
                    "attendanceId").getCustomValueListElements();

            final CollectionSheetEntryGridDto translatedGridView = collectionSheetTranslator.toLegacyDto(
                    collectionSheet, formEnteredDataDto, attendanceTypesList, currency);

            return translatedGridView;
        } catch (SystemException e) {
            throw new MifosRuntimeException(e);
        }
    }
    
    @Override
    public CollectionSheetDto getCollectionSheet(Integer customerId, LocalDate meetingDate) {
    	return collectionSheetService.retrieveCollectionSheet(customerId, meetingDate);
    }

    @Override
	public CollectionSheetEntryGridDto previewCollectionSheetEntry(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto, final CollectionSheetDataDto dataView) {

        CollectionSheetEntryGridDto newCollectionSheetEntryGridDto = null;

        final CollectionSheetEntryDto bulkEntryParent = previousCollectionSheetEntryDto.getBulkEntryParent();
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

    @Override
	public CollectionSheetErrorsDto saveCollectionSheet(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto, final Short userId) {

        final SaveCollectionSheetDto saveCollectionSheetDto = new SaveCollectionSheetFromLegacyAssembler()
                .fromWebTierLegacyStructuretoSaveCollectionSheetDto(previousCollectionSheetEntryDto, userId);

        return saveCollectionSheet(saveCollectionSheetDto);
    }
    
    @Override
	public CollectionSheetErrorsDto saveCollectionSheet(final SaveCollectionSheetDto saveCollectionSheet) {

        CollectionSheetErrorsDto collectionSheetErrorsDto = null;
        try {
            collectionSheetErrorsDto = collectionSheetService.saveCollectionSheet(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        return collectionSheetErrorsDto;
    }

    private List<ListItem<Short>> convertToPaymentTypesListItemDto(final List<? extends MasterDataEntity> paymentTypesList) {
        List<ListItem<Short>> paymentTypesDtoList = new ArrayList<ListItem<Short>>();
        for (MasterDataEntity paymentType : paymentTypesList) {

            if (paymentType instanceof PersonnelStatusEntity) {
                String name = ApplicationContextProvider.getBean(MessageLookup.class).lookup(paymentType.getLookUpValue());
                ((PersonnelStatusEntity) paymentType).setName(name);
            }

            if (paymentType instanceof PersonnelLevelEntity) {
                String name = ApplicationContextProvider.getBean(MessageLookup.class).lookup(paymentType.getLookUpValue());
                ((PersonnelLevelEntity) paymentType).setName(name);
            }

            paymentTypesDtoList.add(new ListItem<Short>(paymentType.getId(), paymentType.getName()));
        }
        return paymentTypesDtoList;
    }
}