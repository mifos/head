package org.mifos.accounts.api;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifos.application.master.util.helpers.PaymentTypes;

public class AccountPaymentParametersDTO {
    public UserReferenceDTO userMakingPayment;
    public AccountReferenceDTO account;
    public BigDecimal paymentAmount;
    public LocalDate paymentDate;
    public LocalDate receiptDate;
    public String receiptId;
    public PaymentTypeDTO paymentType;
    public String comment;

    public AccountPaymentParametersDTO(UserReferenceDTO userMakingPayment, AccountReferenceDTO account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDTO paymentType, String comment) {
        this.userMakingPayment = userMakingPayment;
        this.account = account;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
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