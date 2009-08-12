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
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.client.business.service.ClientService;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

/**
 * I am responsible for assembling the collection sheet entry grid view.
 */
public class CollectionSheetEntryGridViewAssembler {

    private final CustomerPersistence customerPersistence;
    private final MasterPersistence masterPersistence;
    private final ClientService clientService;

    public CollectionSheetEntryGridViewAssembler(CustomerPersistence customerPersistence,
            MasterPersistence masterPersistence, ClientService clientService) {
                this.customerPersistence = customerPersistence;
        this.masterPersistence = masterPersistence;
        this.clientService = clientService;
    }

    public CollectionSheetEntryGridDto toDto(final CollectionSheetFormEnteredDataDto formEnteredDataDto,
            final Short localeId, final CollectionSheetEntryView collectionSheetParent) {

        final java.sql.Date meetingDate = formEnteredDataDto.getMeetingDate();
        final String selectedCustomerSearchId = formEnteredDataDto.getCustomer().getCustomerSearchId();
        final Short loanOfficerId = formEnteredDataDto.getLoanOfficer().getPersonnelId();
        final Short officeId = formEnteredDataDto.getOffice().getOfficeId();
        
        List<ProductDto> loanProductDtos = new ArrayList<ProductDto>();
        List<ProductDto> savingProductDtos = new ArrayList<ProductDto>();

        try {

            // TODO - use DAO method that puts into DTO from hibernate query
            List<PrdOfferingBO> loanProducts = customerPersistence.getLoanProducts(meetingDate,
                    selectedCustomerSearchId, loanOfficerId);
            for (PrdOfferingBO prdOffering : loanProducts) {
                loanProductDtos.add(new ProductDto(prdOffering.getPrdOfferingId(), prdOffering
                        .getPrdOfferingShortName()));
            }

            // TODO - use DAO method that puts into DTO from hibernate query
            List<PrdOfferingBO> savingProducts = customerPersistence.getSavingsProducts(meetingDate,
                    selectedCustomerSearchId, loanOfficerId);
            for (PrdOfferingBO prdOffering : savingProducts) {
                savingProductDtos.add(new ProductDto(prdOffering.getPrdOfferingId(), prdOffering
                        .getPrdOfferingShortName()));
            }

            List<CustomerBO> customers = customerPersistence.getClientsUnderParent(selectedCustomerSearchId, officeId);

            ArrayList<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
            for (CustomerBO client : customers) {
                ClientAttendanceDto clientAttendanceDto = new ClientAttendanceDto(client.getCustomerId(), meetingDate);
                clientAttendanceDtos.add(clientAttendanceDto);
            }
            HashMap<Integer, ClientAttendanceDto> result;

            // TODO - keithw - review the need for call to getClientAttendance
            try {
                result = clientService.getClientAttendance(clientAttendanceDtos);
            } catch (ServiceException e) {
                result = new HashMap<Integer, ClientAttendanceDto>();
                for (ClientAttendanceDto clientAttendanceDto : clientAttendanceDtos) {
                    result.put(clientAttendanceDto.getClientId(), clientAttendanceDto);
                }
            }

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
            throw new RuntimeException(e);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
    }
}
