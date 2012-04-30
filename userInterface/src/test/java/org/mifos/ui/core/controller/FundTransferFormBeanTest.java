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
package org.mifos.ui.core.controller;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mifos.ui.validation.StubValidationContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.validation.ValidationContext;

public class FundTransferFormBeanTest {

    @Test
    public void testDateValidation() {
        FundTransferFormBean formBean = new FundTransferFormBean();
        ValidationContext validationContext = new StubValidationContext();

        formBean.setAmount(BigDecimal.ONE);
        formBean.setSourceBalance(BigDecimal.ONE);

        // invalid dates
        formBean.setTrxnDateDD("Invalid");
        formBean.setTrxnDateMM("10");
        formBean.setTrxnDateYY("2012");

        formBean.setReceiptDateDD("");
        formBean.setReceiptDateMM("11");
        formBean.setReceiptDateYY("2011");

        formBean.validateEnterDetailsStep(validationContext);
        assertEquals(validationContext.getMessageContext().getAllMessages().length, 2);

        int i = 0;
        for (Message msg : validationContext.getMessageContext().getAllMessages()) {
            if (i == 0) {
                assertEquals(msg.getSource(), "trxnDateDD");
            } else if (i == 1) {
                assertEquals(msg.getSource(), "receiptDateDD");
            }
            i++;
        }

        formBean.setTrxnDate(new LocalDate());
        formBean.setReceiptDate(null);

        validationContext.getMessageContext().clearMessages();
        formBean.validateEnterDetailsStep(validationContext);
        assertEquals(validationContext.getMessageContext().getAllMessages().length, 0);
    }

    @Test
    public void testAmountValidation() {
        FundTransferFormBean formBean = new FundTransferFormBean();
        ValidationContext validationContext = new StubValidationContext();

        formBean.setSourceBalance(new BigDecimal("100"));
        formBean.setTrxnDate(new LocalDate());

        // amount bigger then balance
        formBean.setAmount(new BigDecimal("200"));
        formBean.validateEnterDetailsStep(validationContext);

        assertEquals(validationContext.getMessageContext().getAllMessages().length, 1);
        // proper amount
        formBean.setAmount(new BigDecimal("100"));
        formBean.validateEnterDetailsStep(validationContext);

        validationContext.getMessageContext().clearMessages();
        assertEquals(validationContext.getMessageContext().getAllMessages().length, 0);
    }
}
