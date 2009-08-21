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

import java.util.Date;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class AccountPaymentAssembler {

    private final PersonnelPersistence personnelPersistence;

    public AccountPaymentAssembler(PersonnelPersistence personnelPersistence) {
        this.personnelPersistence = personnelPersistence;
    }

    public AccountPaymentEntity fromDto(final Short userId,
            final CollectionSheetEntryGridDto collectionSheetEntryGridDto) {
        
        final String receiptNumber = collectionSheetEntryGridDto.getReceiptId();
        final PaymentTypeEntity paymentType = new PaymentTypeEntity(collectionSheetEntryGridDto.getPaymentTypeId());
        final Date paymentDate = collectionSheetEntryGridDto.getTransactionDate();
        final Date receiptDate = collectionSheetEntryGridDto.getReceiptDate();

        final PersonnelBO user = personnelPersistence.findPersonnelById(userId);
        final AccountPaymentEntity payment = new AccountPaymentEntity(null, new Money(), receiptNumber, receiptDate,
                paymentType, paymentDate);
        payment.setCreatedByUser(user);

        return payment;
    }

}