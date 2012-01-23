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

package org.mifos.ui.loan.controller;

import java.io.Serializable;
import java.math.BigDecimal;

import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
public class ClearOverpaymentFormBean implements Serializable {

    private String overpaymentId = "";
    private Number originalOverpaymentAmount = BigDecimal.ZERO;
    private Number actualOverpaymentAmount = BigDecimal.ZERO;

    @Autowired
    private transient MifosBeanValidator validator;

    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

    public void validateEditOverpaymentClearStep(ValidationContext context) {
        validateEnterOverpaymentClearStep(context);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="REC_CATCH_EXCEPTION", justification="should be the exception thrown by jodatime but not sure what it is right now.")
    public void validateEnterOverpaymentClearStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();

        validator.validate(this, messages);
    }

    public String getOverpaymentId() {
        return overpaymentId;
    }

    public void setOverpaymentId(String overpaymentId) {
        this.overpaymentId = overpaymentId;
    }

    public Number getOriginalOverpaymentAmount() {
        return originalOverpaymentAmount;
    }

    public void setOriginalOverpaymentAmount(Number originalOverpaymentAmount) {
        this.originalOverpaymentAmount = originalOverpaymentAmount;
    }

    public Number getActualOverpaymentAmount() {
        return actualOverpaymentAmount;
    }

    public void setActualOverpaymentAmount(Number actualOverpaymentAmount) {
        this.actualOverpaymentAmount = actualOverpaymentAmount;
    }
}