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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;

/**
 * An Assembler for building {@link CollectionSheetFormEnteredDataDto} from
 * {@link BulkEntryActionForm}.
 */
public class FormEnteredDataAssembler {

    private final BulkEntryActionForm collectionSheetForm;
    private final CollectionSheetEntryFormDtoDecorator collectionSheetFormDtoDecorator;

    public FormEnteredDataAssembler(BulkEntryActionForm collectionSheetForm,
            CollectionSheetEntryFormDtoDecorator collectionSheetFormDtoDecorator) {
        this.collectionSheetForm = collectionSheetForm;
        this.collectionSheetFormDtoDecorator = collectionSheetFormDtoDecorator;
    }

    public CollectionSheetFormEnteredDataDto toDto() {
        final OfficeDetailsDto office = collectionSheetFormDtoDecorator.findSelectedBranchOfficeById(Short
                .valueOf(collectionSheetForm
                .getOfficeId()));
        final PersonnelDto loanOfficer = collectionSheetFormDtoDecorator.findSelectedLoanOfficerById(Short
                .valueOf(collectionSheetForm
                .getLoanOfficerId()));
        final CustomerDto selectedCustomer = collectionSheetFormDtoDecorator.findSelectedCustomerById(Integer
                .valueOf(collectionSheetForm
                .getCustomerId()));
        final ListItem<Short> selectedPaymentType = collectionSheetFormDtoDecorator.findSelectedPaymentTypeById(Short
                .valueOf(collectionSheetForm.getPaymentId()));
        final java.sql.Date meetingDate = collectionSheetFormDtoDecorator.getMeetingDateAsSqlDate();

        final java.sql.Date receiptDate = determineReceiptDateIfPopulated();
        CustomerDto selectedMember = null;
        CustomerDto selectedGroup = null;
        
        
        if(collectionSheetForm.getGroupId().length() > 0){
           selectedGroup = collectionSheetFormDtoDecorator.findSelectedGroupById(Integer.valueOf(collectionSheetForm
                    .getGroupId()));    
           if(collectionSheetForm.getMemberId().length() > 0){
           	selectedMember = collectionSheetFormDtoDecorator.findSelectedMemberById(Integer.valueOf(collectionSheetForm.getMemberId()));        
                      
           } 
        }

        return new CollectionSheetFormEnteredDataDto(office, loanOfficer, selectedCustomer, selectedPaymentType,
                meetingDate, receiptDate, collectionSheetForm.getReceiptId(),selectedGroup,selectedMember);
    }

    private java.sql.Date determineReceiptDateIfPopulated() {
        if (StringUtils.isNotBlank(collectionSheetForm.getReceiptDateYY())
                && StringUtils.isNotBlank(collectionSheetForm.getReceiptDateMM())
                && StringUtils.isNotBlank(collectionSheetForm.getReceiptDateDD())) {
            return new java.sql.Date(new DateTime(Integer.valueOf(collectionSheetForm.getReceiptDateYY()), Integer
                    .valueOf(collectionSheetForm.getReceiptDateMM()), Integer.valueOf(collectionSheetForm
                    .getReceiptDateDD()), 0, 0, 0, 0).toDate().getTime());
        }
        return null;
    }

}
