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

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifos.clientportfolio.loan.ui.DateValidator;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;

public class FundTransferFormBean implements Serializable {
    private static final long serialVersionUID = 1199468781413161456L;

    private static final String ERROR_MANDATORY = "errors.mandatory";
    private static final String ERROR_INVALID_DATE = "errors.invaliddate";
    private static final String ERROR_INVALID_AMOUNT = "error.penalty.incorrectDouble";
    private static final String ERROR_FUTURE_DATE = "errors.futuredate";
    private static final String ERROR_NOT_ENOUGH_BALANCE = "fundTransfer.error.notEnough";

    private static final String DATE_OF_TRXN = "Savings.dateOfTrxn";
    private static final String RECEIPT_DATE = "Savings.receiptDate";
    private static final String AMOUNT = "Amount";

    private static final String TRXN_DATE_SOURCE = "trxnDateDD";
    private static final String AMOUNT_SOURCE = "amount";
    private static final String RECEIPT_DATE_SOURCE = "receiptDateDD";

    private BigDecimal amount;

    private BigDecimal sourceBalance;
    private BigDecimal targetBalance;
    private String sourceGlobalAccNum;
    private String targetGlobalAccNum;

    private String trxnDateDD;
    private String trxnDateMM;
    private String trxnDateYY;

    private String receiptDateDD;
    private String receiptDateMM;
    private String receiptDateYY;

    private String receiptId;

    private boolean afterInit;

    private transient DateValidator dateValidator;

    private transient boolean amountBindingError = false;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTrxnDateDD() {
        return trxnDateDD;
    }

    public void setTrxnDateDD(String trxnDateDD) {
        this.trxnDateDD = trxnDateDD;
    }

    public String getTrxnDateMM() {
        return trxnDateMM;
    }

    public void setTrxnDateMM(String trxnDateMM) {
        this.trxnDateMM = trxnDateMM;
    }

    public String getTrxnDateYY() {
        return trxnDateYY;
    }

    public void setTrxnDateYY(String trxnDateYY) {
        this.trxnDateYY = trxnDateYY;
    }

    public String getReceiptDateDD() {
        return receiptDateDD;
    }

    public void setReceiptDateDD(String receiptDateDD) {
        this.receiptDateDD = receiptDateDD;
    }

    public String getReceiptDateMM() {
        return receiptDateMM;
    }

    public void setReceiptDateMM(String receiptDateMM) {
        this.receiptDateMM = receiptDateMM;
    }

    public String getReceiptDateYY() {
        return receiptDateYY;
    }

    public void setReceiptDateYY(String receiptDateYY) {
        this.receiptDateYY = receiptDateYY;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public BigDecimal getSourceBalance() {
        return sourceBalance;
    }

    public void setSourceBalance(BigDecimal sourceBalance) {
        this.sourceBalance = sourceBalance;
    }

    public BigDecimal getTargetBalance() {
        return targetBalance;
    }

    public void setTargetBalance(BigDecimal targetBalance) {
        this.targetBalance = targetBalance;
    }

    public boolean isAfterInit() {
        return afterInit;
    }

    public void setAfterInit(boolean afterInit) {
        this.afterInit = afterInit;
    }

    public String getSourceGlobalAccNum() {
        return sourceGlobalAccNum;
    }

    public void setSourceGlobalAccNum(String sourceGlobalAccNum) {
        this.sourceGlobalAccNum = sourceGlobalAccNum;
    }

    public String getTargetGlobalAccNum() {
        return targetGlobalAccNum;
    }

    public void setTargetGlobalAccNum(String targetGlobalAccNum) {
        this.targetGlobalAccNum = targetGlobalAccNum;
    }

    public LocalDate getTrxnDate() {
        LocalDate date = null;
        if (isTrxnDateEntered()) {
            Integer day = Integer.parseInt(trxnDateDD);
            Integer month = Integer.parseInt(trxnDateMM);
            Integer year = Integer.parseInt(trxnDateYY);
            date = new LocalDate(year, month, day);
        }
        return date;
    }

    public void setTrxnDate(LocalDate date) {
        if (date == null) {
            trxnDateDD = "";
            trxnDateMM = "";
            trxnDateYY = "";
        } else {
            trxnDateDD = String.valueOf(date.getDayOfMonth());
            trxnDateMM = String.valueOf(date.getMonthOfYear());
            trxnDateYY = String.valueOf(date.getYear());
        }
    }

    public LocalDate getReceiptDate() {
        LocalDate date = null;
        if (isReceiptDateEntered()) {
            Integer day = Integer.parseInt(receiptDateDD);
            Integer month = Integer.parseInt(receiptDateMM);
            Integer year = Integer.parseInt(receiptDateYY);
            date = new LocalDate(year, month, day);
        }
        return date;
    }

    public void setReceiptDate(LocalDate date) {
        if (date == null) {
            receiptDateDD = "";
            receiptDateMM = "";
            receiptDateYY = "";
        } else {
            receiptDateDD = String.valueOf(date.getDayOfMonth());
            receiptDateMM = String.valueOf(date.getMonthOfYear());
            receiptDateYY = String.valueOf(date.getYear());
        }
    }

    public void validateEnterDetailsStep(ValidationContext context) {
        amountBindingError = false;
        MessageContext messageContext = context.getMessageContext();

        handleBindingErrors(messageContext);
        validateTrxnDate(messageContext);
        validateReceiptDate(messageContext);
        validateAmount(messageContext);
    }

    private void handleBindingErrors(MessageContext context) {
        if (context.hasErrorMessages()) {
            Message[] messages = context.getAllMessages();
            context.clearMessages();

            for (Message message : messages) {
                if (message.getSource().equals(AMOUNT_SOURCE)) {
                    context.addMessage(getErrorMsg(ERROR_INVALID_AMOUNT, AMOUNT_SOURCE, AMOUNT));
                    amountBindingError = true;
                }
            }
        }
    }

    private void validateTrxnDate(MessageContext context) {
        if (!isTrxnDateEntered()) { // NOPMD
            context.addMessage(getErrorMsg(ERROR_MANDATORY, TRXN_DATE_SOURCE, DATE_OF_TRXN));
        } else if (!getDateValidator().formsValidDate(trxnDateDD, trxnDateMM, trxnDateYY)) { // NOPMD
            context.addMessage(getErrorMsg(ERROR_INVALID_DATE, TRXN_DATE_SOURCE, DATE_OF_TRXN));
        } else if (getDateValidator().isFutureDate(getTrxnDate())) {
            context.addMessage(getErrorMsg(ERROR_FUTURE_DATE, TRXN_DATE_SOURCE, DATE_OF_TRXN));
        }
    }

    private void validateReceiptDate(MessageContext context) {
        if (isReceiptDateEntered()) {
            if (!getDateValidator().formsValidDate(receiptDateDD, receiptDateMM, receiptDateYY)) { // NOPMD
                context.addMessage(getErrorMsg(ERROR_INVALID_DATE, RECEIPT_DATE_SOURCE, RECEIPT_DATE));
            } else if (getDateValidator().isFutureDate(getReceiptDate())) {
                context.addMessage(getErrorMsg(ERROR_FUTURE_DATE, RECEIPT_DATE_SOURCE, RECEIPT_DATE));
            }
        }
    }

    private void validateAmount(MessageContext context) {
        if (!amountBindingError) {
            if (amount == null) {
                context.addMessage(getErrorMsg(ERROR_MANDATORY, AMOUNT_SOURCE, AMOUNT));
            } else if (amount.max(BigDecimal.ZERO).equals(BigDecimal.ZERO)) {
                context.addMessage(getErrorMsg(ERROR_INVALID_AMOUNT, AMOUNT_SOURCE, AMOUNT));
            } else if (sourceBalance.subtract(amount).doubleValue() < 0) {
                context.addMessage(getErrorMsg(ERROR_NOT_ENOUGH_BALANCE, AMOUNT_SOURCE));
            }
        }
    }

    private MessageResolver getErrorMsg(String messageKey, String source) {
        return getErrorMsg(messageKey, source, null);
    }

    private MessageResolver getErrorMsg(String messageKey, String source, String resolvableArg) {
        MessageBuilder mb = new MessageBuilder().error().source(source).code(messageKey);
        if (resolvableArg != null) {
            mb.resolvableArg(resolvableArg);
        }
        return mb.build();
    }

    public boolean isReceiptDateEntered() {
        return StringUtils.isNotBlank(receiptDateDD) || StringUtils.isNotBlank(receiptDateMM)
                || StringUtils.isNotBlank(receiptDateYY);
    }

    public boolean isTrxnDateEntered() {
        return StringUtils.isNotBlank(trxnDateDD) || StringUtils.isNotBlank(trxnDateMM)
                || StringUtils.isNotBlank(trxnDateYY);
    }

    private DateValidator getDateValidator() {
        if (dateValidator == null) {
            dateValidator = new DateValidator();
        }
        return dateValidator;
    }
}
