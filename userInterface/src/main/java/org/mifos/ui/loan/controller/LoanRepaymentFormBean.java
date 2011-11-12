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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.PersonnelNoteDto;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.format.annotation.NumberFormat;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
public class LoanRepaymentFormBean implements Serializable {

    private String globalAccountNumber = "";
    private LocalDate paymentDate = new LocalDate();
    private Number paymentAmount = BigDecimal.ZERO;

    @Autowired
    private transient MifosBeanValidator validator;

    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

    public void validateEditUserDetailsStep(ValidationContext context) {
        validateEnterUserDetailsStep(context);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="REC_CATCH_EXCEPTION", justification="should be the exception thrown by jodatime but not sure what it is right now.")
    public void validateEnterUserDetailsStep(ValidationContext context) {
        MessageContext messages = context.getMessageContext();

        validator.validate(this, messages);
    }

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Number getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(Number paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getGlobalAccountNumber() {
		return globalAccountNumber;
	}

	public void setGlobalAccountNumber(String globalAccountNumber) {
		this.globalAccountNumber = globalAccountNumber;
	}
}