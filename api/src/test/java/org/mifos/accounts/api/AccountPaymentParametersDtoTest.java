/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountPaymentParametersDtoTest {
    UserReferenceDto user = new UserReferenceDto((short) -1);
    AccountReferenceDto account = new AccountReferenceDto(-1);
    BigDecimal paymentAmount = new BigDecimal("0.0");
    LocalDate paymentDate = new LocalDate(1990, 1, 1);
    @Mock
    PaymentTypeDto paymentType;
    String emptyComment = "";

    @Test(expected = IllegalArgumentException.class)
    public void userMakingPaymentCannotBeNull() {
        new AccountPaymentParametersDto(null, account, paymentAmount, paymentDate, paymentType, emptyComment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void accountCannotBeNull() {
        new AccountPaymentParametersDto(user, null, paymentAmount, paymentDate, paymentType, emptyComment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paymentAmountCannotBeNull() {
        new AccountPaymentParametersDto(user, account, null, paymentDate, paymentType, emptyComment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paymentDateCannotBeNull() {
        new AccountPaymentParametersDto(user, account, paymentAmount, null, paymentType, emptyComment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void paymentTypeCannotBeNull() {
        new AccountPaymentParametersDto(user, account, paymentAmount, paymentDate, null, emptyComment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void commentCannotBeNull() {
        new AccountPaymentParametersDto(user, account, paymentAmount, paymentDate, paymentType, null);
    }

    @Test
    public void receiptDateAndReceiptIdCanBeNull() {
        new AccountPaymentParametersDto(user, account, paymentAmount, paymentDate, paymentType, emptyComment, null,
                null);
    }
}
