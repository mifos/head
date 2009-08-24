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
import java.util.HashMap;
import java.util.List;

import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.client.business.service.ClientService;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

/**
 * I am responsible for assembling the collection sheet entry grid view.
 */
public class CollectionSheetEntryGridViewAssembler {

    private final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler;
    private final CustomerPersistence customerPersistence;
    private final MasterPersistence masterPersistence;
    private final ClientService clientService;

    public CollectionSheetEntryGridViewAssembler(final CustomerPersistence customerPersistence,
            final MasterPersistence masterPersistence, final ClientService clientService,
            final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler) {
                this.customerPersistence = customerPersistence;
        this.masterPersistence = masterPersistence;
        this.clientService = clientService;
        this.collectionSheetEntryViewAssembler = collectionSheetEntryViewAssembler;
    }

    public CollectionSheetEntryGridDto toDto(final CollectionSheetFormEnteredDataDto formEnteredDataDto,
            final Short localeId) {

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

            
            // FIXME - keithw - this fetch of clients and their attendances
            // below are connected
            final List<CustomerView> customers = customerPersistence
                    .findClientsThatAreActiveOrOnHoldInCustomerHierarchy(
                    selectedCustomerSearchId, officeId);

            ArrayList<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
            for (CustomerView client : customers) {
                ClientAttendanceDto clientAttendanceDto = new ClientAttendanceDto(client.getCustomerId(), meetingDate);
                clientAttendanceDtos.add(clientAttendanceDto);
            }
            HashMap<Integer, ClientAttendanceDto> result;

            try {
                result = clientService.getClientAttendance(clientAttendanceDtos);
            } catch (ServiceException e) {
                result = new HashMap<Integer, ClientAttendanceDto>();
                for (ClientAttendanceDto clientAttendanceDto : clientAttendanceDtos) {
                    result.put(clientAttendanceDto.getClientId(), clientAttendanceDto);
                }
            }
            // end of fix me

            List<CustomValueListElement> attendanceTypesList = masterPersistence.getCustomValueList(
                    MasterConstants.ATTENDENCETYPES, localeId,
                    "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId")
                    .getCustomValueListElements();
            
            final CollectionSheetEntryGridDto collectionSheetGridView = new CollectionSheetEntryGridDto(
                    collectionSheetParent,
                    formEnteredDataDto.getLoanOfficer(),
                    formEnteredDataDto.getOffice(),
                    formEnteredDataDto.getPaymentType(),
                    formEnteredDataDto.getMeetingDate(),
                    formEnteredDataDto.getReceiptId(),
                    formEnteredDataDto.getReceiptDate(),
                    loanProductDtos, savingProductDtos, result,
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
