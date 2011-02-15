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

package org.mifos.dto;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("PMD")
@RunWith(MockitoJUnitRunner.class)
public class AccountPaymentParametersDtoTest {

    private UserReferenceDto user = new UserReferenceDto(Short.valueOf("-1"));
    private AccountReferenceDto account = new AccountReferenceDto(Integer.valueOf(-1));
    private BigDecimal paymentAmount = new BigDecimal("0.0");
    private LocalDate paymentDate = new LocalDate(1990, 1, 1);

    @Mock
    private PaymentTypeDto paymentType;
    private String emptyComment = "";

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
    public void commentCannotBeNull() {
        new AccountPaymentParametersDto(user, account, paymentAmount, paymentDate, paymentType, null);
    }

    @Test
    public void receiptDateAndReceiptIdCanBeNull() {
        new AccountPaymentParametersDto(user, account, paymentAmount, paymentDate, paymentType, emptyComment, null,
                null, null);
    }
}