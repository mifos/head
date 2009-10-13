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
