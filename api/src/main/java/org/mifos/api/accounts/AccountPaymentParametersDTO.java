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

package org.mifos.api.accounts;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public class AccountPaymentParametersDTO {
    public final UserReferenceDTO userMakingPayment;
    public final AccountReferenceDTO account;
    public final BigDecimal paymentAmount;
    public final LocalDate paymentDate;
    public final LocalDate receiptDate;
    public final String receiptId;
    public final PaymentTypeDTO paymentType;
    public final String comment;

    public AccountPaymentParametersDTO(UserReferenceDTO userMakingPayment, AccountReferenceDTO account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDTO paymentType, String comment) {
        this.userMakingPayment = userMakingPayment;
        this.account = account;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.receiptDate = null;
        this.receiptId = null;
        this.comment = comment;
    }

    public AccountPaymentParametersDTO(UserReferenceDTO userMakingPayment, AccountReferenceDTO account,
            BigDecimal paymentAmount, LocalDate paymentDate, LocalDate receiptDate, String receiptId,
            PaymentTypeDTO paymentType, String comment) {
        super();
        this.userMakingPayment = userMakingPayment;
        this.account = account;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.paymentType = paymentType;
        this.comment = comment;
    }

}
