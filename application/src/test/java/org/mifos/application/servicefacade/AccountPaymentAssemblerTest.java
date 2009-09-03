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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelLevelEntity;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link CollectionSheetServiceFacadeWebTier}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountPaymentAssemblerTest {

    // system under test (SUT)
    private AccountPaymentAssembler accountPaymentAssembler;

    // create test-doubles for all depended-on-components (DOC)s
    @Mock
    private PersonnelPersistence personnelPersistence;
    
    @Mock
    private CollectionSheetEntryGridDto collectionSheetEntryGridDto;
    
    private PersonnelBO personnelBO;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
         MifosLogManager.configureLogging();
    }

    @Before
    public void setupSUTAndInjectMocksAsDependencies() {
        final Short userId = Short.valueOf("1");
        personnelBO = new PersonnelBO(userId, "", "", new PersonnelLevelEntity(PersonnelLevel.LOAN_OFFICER));
        
        accountPaymentAssembler = new AccountPaymentAssembler(personnelPersistence);
    }
    
    @Test
    public void shouldAssembleAccountPaymentEntityWithDataFromCollectionSheetEntryGridDto() {

        // setup
        final String receiptNumber = "XXX-Receipt";
        final Short paymentTypeId = PaymentTypes.CASH.getValue();
        final Date paymentDate = new Date();

        // stubbing
        when(collectionSheetEntryGridDto.getReceiptId()).thenReturn(receiptNumber);
        when(collectionSheetEntryGridDto.getPaymentTypeId()).thenReturn(paymentTypeId);
        when(collectionSheetEntryGridDto.getTransactionDate()).thenReturn(paymentDate);
        when(collectionSheetEntryGridDto.getReceiptDate()).thenReturn(paymentDate);
        
        when(personnelPersistence.findPersonnelById(personnelBO.getPersonnelId())).thenReturn(personnelBO);

        // exercise test
        final AccountPaymentEntity accountPayment = accountPaymentAssembler
                .fromDto(personnelBO.getPersonnelId(),
                collectionSheetEntryGridDto);

        // verification
        assertThat(accountPayment.getPaymentId(), is(nullValue()));
        assertThat(accountPayment.getAccount(), is(nullValue()));
        assertThat(accountPayment.getAmount().getAmountDoubleValue(), is(Double.valueOf("0.0")));
        
        assertThat(accountPayment.getReceiptNumber(), is(receiptNumber));
        assertThat(accountPayment.getPaymentType().getId(), is(paymentTypeId));
        assertThat(accountPayment.getPaymentDate(), is(paymentDate));
        assertThat(accountPayment.getReceiptDate(), is(paymentDate));
        assertThat(accountPayment.getCreatedByUser(), is(personnelBO));
    }
    
    @Test
    public void shouldNotSetAccountWhenAssemblingAccountPaymentEntityFromCollectionSheetEntryGridDto() {

        // exercise test
        final AccountPaymentEntity accountPayment = accountPaymentAssembler.fromDto(personnelBO.getPersonnelId(),
                collectionSheetEntryGridDto);

        // verification
        assertThat(accountPayment.getPaymentId(), is(nullValue()));
        assertThat(accountPayment.getAccount(), is(nullValue()));
    }

    @Test
    public void shouldNotSetAmountToZeroWhenAssemblingAccountPaymentEntityFromCollectionSheetEntryGridDto() {

        // exercise test
        final AccountPaymentEntity accountPayment = accountPaymentAssembler.fromDto(personnelBO.getPersonnelId(),
                collectionSheetEntryGridDto);

        // verification
        assertThat(accountPayment.getAmount().getAmountDoubleValue(), is(Double.valueOf("0.0")));
    }
    
    @Test
    public void shouldNotSetPaymentIdToNullWhenAssemblingAccountPaymentEntityFromCollectionSheetEntryGridDto() {

        // exercise test
        final AccountPaymentEntity accountPayment = accountPaymentAssembler.fromDto(personnelBO.getPersonnelId(),
                collectionSheetEntryGridDto);

        // verification
        assertThat(accountPayment.getPaymentId(), is(nullValue()));
    }
}
