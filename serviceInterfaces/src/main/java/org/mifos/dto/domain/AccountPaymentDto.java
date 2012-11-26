package org.mifos.dto.domain;

import java.util.Date;
import java.util.Map;

@SuppressWarnings("PMD")
public class AccountPaymentDto {

    private Double totalAmount;
    private final Date transactionDate;
    private String receiptNumber;
    private final Date receiptDate;
    private Short paymentTypeId;
    private Map<String, Double> memberNumWithAmount;
    
    public AccountPaymentDto(Double totalAmount, Date transactionDate, String receiptNumber, Date receiptDate,
            Short paymentTypeId) {
        this.totalAmount = totalAmount;
        this.transactionDate = null != transactionDate ? (Date)transactionDate.clone(): null;
        this.receiptNumber = receiptNumber;
        this.receiptDate = null != receiptDate ? (Date)receiptDate.clone() : null;
        this.paymentTypeId = paymentTypeId;
    }
    
    public AccountPaymentDto(Double totalAmount, Date transactionDate, String receiptNumber, Date receiptDate,
            Short paymentTypeId, Map<String, Double> memberNumWithAmount) {
        this.totalAmount = totalAmount;
        this.transactionDate = (Date)transactionDate.clone();
        this.receiptNumber = receiptNumber;
        this.receiptDate = (Date)receiptDate.clone();
        this.paymentTypeId = paymentTypeId;
        this.memberNumWithAmount = memberNumWithAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getTransactionDate() {
        if (null != transactionDate) {
            return (Date) transactionDate.clone();
        }
        return null;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Date getReceiptDate() {
        if (null != receiptDate) {
            return (Date) receiptDate.clone();
        }
        return null;
    }

    public Short getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Short paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public Map<String, Double> getMemberNumWithAmount() {
        return memberNumWithAmount;
    }

    public void setMemberNumWithAmount(Map<String, Double> memberNumWithAmount) {
        this.memberNumWithAmount = memberNumWithAmount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((memberNumWithAmount == null) ? 0 : memberNumWithAmount.hashCode());
        result = prime * result + ((paymentTypeId == null) ? 0 : paymentTypeId.hashCode());
        result = prime * result + ((receiptDate == null) ? 0 : receiptDate.hashCode());
        result = prime * result + ((receiptNumber == null) ? 0 : receiptNumber.hashCode());
        result = prime * result + ((totalAmount == null) ? 0 : totalAmount.hashCode());
        result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }
    
    
}
