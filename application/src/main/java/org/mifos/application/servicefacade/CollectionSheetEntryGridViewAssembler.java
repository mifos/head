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
import java.util.List;

import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * I am responsible for assembling the collection sheet entry grid view.
 */
public class CollectionSheetEntryGridViewAssembler {

    private final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler;
    private final CustomerPersistence customerPersistence;
    private final MasterPersistence masterPersistence;

    public CollectionSheetEntryGridViewAssembler(final CustomerPersistence customerPersistence,
            final MasterPersistence masterPersistence,
            final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler) {
                this.customerPersistence = customerPersistence;
        this.masterPersistence = masterPersistence;
        this.collectionSheetEntryViewAssembler = collectionSheetEntryViewAssembler;
    }

    public CollectionSheetEntryGridDto toDto(final CollectionSheetFormEnteredDataDto formEnteredDataDto) {

        final java.sql.Date meetingDate = new java.sql.Date(formEnteredDataDto.getMeetingDate().getTime());
        final String selectedCustomerSearchId = formEnteredDataDto.getCustomer().getCustomerSearchId();
        final Short loanOfficerId = formEnteredDataDto.getLoanOfficer().getPersonnelId();
        final Short officeId = formEnteredDataDto.getOffice().getOfficeId();
        
        final List<CustomerView> customerHierarchy = customerPersistence.findCustomerHierarchyForOfficeBySearchId(
                officeId, selectedCustomerSearchId);
        final int countOfCustomers = customerHierarchy.size();
        
        final CollectionSheetEntryView collectionSheetParent = collectionSheetEntryViewAssembler
                .toDto(
                formEnteredDataDto, customerHierarchy, countOfCustomers);
        
        List<ProductDto> loanProductDtos = new ArrayList<ProductDto>();
        List<ProductDto> savingProductDtos = new ArrayList<ProductDto>();

        try {

            loanProductDtos = customerPersistence.getLoanProducts(meetingDate,
                    selectedCustomerSearchId, loanOfficerId);

            savingProductDtos = customerPersistence.getSavingsProducts(meetingDate,
                    selectedCustomerSearchId, loanOfficerId);

            List<CustomValueListElement> attendanceTypesList = masterPersistence.getCustomValueList(
                    MasterConstants.ATTENDENCETYPES, "org.mifos.application.master.business.CustomerAttendanceType",
                    "attendanceId")
                    .getCustomValueListElements();
            
            final CollectionSheetEntryGridDto collectionSheetGridView = new CollectionSheetEntryGridDto(
                    collectionSheetParent,
                    formEnteredDataDto.getLoanOfficer(),
                    formEnteredDataDto.getOffice(),
                    formEnteredDataDto.getPaymentType(),
                    formEnteredDataDto.getMeetingDate(),
                    formEnteredDataDto.getReceiptId(),
                    formEnteredDataDto.getReceiptDate(),
                    loanProductDtos, savingProductDtos,
                    attendanceTypesList
                    );

            return collectionSheetGridView;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        }
    }
}
