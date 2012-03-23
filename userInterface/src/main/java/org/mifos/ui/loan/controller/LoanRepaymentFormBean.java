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
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.mifos.clientportfolio.loan.ui.DateValidator;
import org.mifos.platform.validation.MifosBeanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
public class LoanRepaymentFormBean implements Serializable {

    private String globalAccountNumber = "";
    @NotNull   
    private Number paymentAmount = BigDecimal.ZERO;
    private String receiptId = "";
    private String paymentType;
    
    private Integer paymentDateDD;
    private Integer paymentDateMM;
    private Integer paymentDateYY;
    
    private Integer receiptDateDD;
    private Integer receiptDateMM;
    private Integer receiptDateYY;
    
    private Map<String, String> allowedPaymentTypes;
    private LocalDate lastPaymentDate;
    
    @Autowired
    private transient MifosBeanValidator validator;

    private transient DateValidator dateValidator;
    
    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }
    
    public void validateEnterLoanRepaymentDetails(ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();
        
        Errors errors = validator.checkConstraints(this);
        if (errors.hasErrors()) {
            for (FieldError fieldError : errors.getFieldErrors()) {
                String arg = "";
                if ("paymentAmount".equals(fieldError.getField())) {
                    arg = "Amount"; 
                }                
                messageContext.addMessage(buildValidationMessage("errors.mandatory", fieldError.getField(), arg));
            }
        }               
            
        if (dateValidator == null) {
            dateValidator = new DateValidator();
        }
        
        //payment date validation
        if (!dateValidator.formsValidDate(paymentDateDD, paymentDateMM, paymentDateYY)) {  
            messageContext.addMessage(buildValidationMessage("errors.invaliddate", "paymentDate", "accounts.date_of_trxn"));
        } else if (getPaymentDate().isAfter(new LocalDate())) {
            messageContext.addMessage(buildValidationMessage("errors.futuredate", "paymentDate", "accounts.date_of_trxn"));
        } else if (getPaymentDate().isBefore(lastPaymentDate)) {
            messageContext.addMessage(buildValidationMessage("errors.payment.date.before.last.payment", "paymentDate", "accounts.date_of_trxn")); 
        }
        
        //receipt date validation
        if (isReceiptDateSpecified() && !dateValidator.formsValidDate(receiptDateDD, receiptDateMM, receiptDateYY)) {
            messageContext.addMessage(buildValidationMessage("errors.invaliddate", "receiptDate", "accounts.receiptdate"));
        }
        
        //amount validation
        if (paymentAmount != null && paymentAmount.doubleValue() <= 0) {
            messageContext.addMessage(buildValidationMessage("error.penalty.incorrectDouble", "paymentAmount", "Amount"));          
        }
    }
    
    private MessageResolver buildValidationMessage(String errorCode, String source, String resolvableArg) {
        String[] args = (resolvableArg == null) ? new String[] { } : new String[] { resolvableArg };
        return buildValidationMessage(errorCode, source, args);
    }
    
    private MessageResolver buildValidationMessage(String errorCode, String source, String[] resolvableArgs) {
        MessageBuilder builder = new MessageBuilder().error().source(source).code(errorCode);
        for (String arg : resolvableArgs) {
            builder.resolvableArg(arg);
        }
        return builder.build();
    }
  
    public LocalDate getPaymentDate() {
        LocalDate paymentDate = null;
        if (paymentDateDD != null && paymentDateMM != null && paymentDateYY != null) {
            paymentDate = new LocalDate(paymentDateYY, paymentDateMM, paymentDateDD);
        }
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        paymentDateDD = paymentDate.getDayOfMonth();
        paymentDateMM = paymentDate.getMonthOfYear();
        paymentDateYY = paymentDate.getYear();
    }
    
    public LocalDate getReceiptDate() {
        LocalDate receiptDate = null;
        if (receiptDateDD != null && receiptDateMM != null && receiptDateYY != null) {
            receiptDate = new LocalDate(receiptDateYY, receiptDateMM, receiptDateDD);
        }
        return receiptDate;
    }

    private boolean isReceiptDateSpecified() {
        return receiptDateDD != null || receiptDateMM != null || receiptDateYY != null;
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

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    
    public Short getPaymentTypeId() {
        return (paymentType == null) ? null : Short.parseShort(paymentType);
    }

    public void setAllowedPaymentTypes(Map<String, String> allowedPaymentTypes) {
        this.allowedPaymentTypes = allowedPaymentTypes;
    }
    
    public Map<String, String> getAllowedPaymentTypes() {
        return allowedPaymentTypes;
    }
    
    public String getPaymentTypeName() {
        return allowedPaymentTypes.get(paymentType);
    }

    public Integer getPaymentDateDD() {
        return paymentDateDD;
    }

    public void setPaymentDateDD(Integer paymentDateDD) {
        this.paymentDateDD = paymentDateDD;
    }

    public Integer getPaymentDateMM() {
        return paymentDateMM;
    }

    public void setPaymentDateMM(Integer paymentDateMM) {
        this.paymentDateMM = paymentDateMM;
    }

    public Integer getPaymentDateYY() {
        return paymentDateYY;
    }

    public void setPaymentDateYY(Integer paymentDateYY) {
        this.paymentDateYY = paymentDateYY;
    }

    public Integer getReceiptDateDD() {
        return receiptDateDD;
    }

    public void setReceiptDateDD(Integer receiptDateDD) {
        this.receiptDateDD = receiptDateDD;
    }

    public Integer getReceiptDateMM() {
        return receiptDateMM;
    }

    public void setReceiptDateMM(Integer receiptDateMM) {
        this.receiptDateMM = receiptDateMM;
    }

    public Integer getReceiptDateYY() {
        return receiptDateYY;
    }

    public void setReceiptDateYY(Integer receiptDateYY) {
        this.receiptDateYY = receiptDateYY;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
}