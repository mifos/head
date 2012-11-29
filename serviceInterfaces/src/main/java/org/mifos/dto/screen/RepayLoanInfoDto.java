package org.mifos.dto.screen;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

public class RepayLoanInfoDto {
    private final String globalAccountNum;
    private final String earlyRepayAmount;
    private final String receiptNumber;
    private final Date receiptDate;
    private final String paymentTypeId;
    private final Short id;
    private final boolean waiveInterest;
    private final Date dateOfPayment;
    private final BigDecimal waivedAmount;
    private final BigDecimal totalRepaymentAmount;
    private  Map<String, Double> memberValues;
    private Integer savingsPaymentId;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public RepayLoanInfoDto(String globalAccountNum, String earlyRepayAmount, String receiptNumber,
                            Date receiptDate, String paymentTypeId, Short id, boolean waiveInterest,
                            Date dateOfPayment, BigDecimal totalRepaymentAmount, BigDecimal waivedAmount) {
        this.globalAccountNum = globalAccountNum;
        this.earlyRepayAmount = earlyRepayAmount;
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.paymentTypeId = paymentTypeId;
        this.id = id;
        this.waiveInterest = waiveInterest;
        this.dateOfPayment = dateOfPayment;
        this.waivedAmount = waivedAmount;
        this.totalRepaymentAmount = totalRepaymentAmount;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public String getEarlyRepayAmount() {
        return earlyRepayAmount;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public Short getId() {
        return id;
    }

    public boolean isWaiveInterest() {
        return waiveInterest;
    }

    public Date getDateOfPayment() {
        return dateOfPayment;
    }

    public BigDecimal getWaivedAmount() {
        return waivedAmount;
    }

    public BigDecimal getTotalRepaymentAmount() {
        return totalRepaymentAmount;
    }

    public Integer getSavingsPaymentId() {
        return savingsPaymentId;
    }

    public void setSavingsPaymentId(Integer savingsPaymentId) {
        this.savingsPaymentId = savingsPaymentId;
    }

    public Map<String, Double> getMembersValue() {
        return memberValues;
    }
}
