package org.mifos.dto.screen;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class GroupLoanMemberAdjustmentDto implements Comparable<GroupLoanMemberAdjustmentDto>, Serializable{
    private static final long serialVersionUID = 1L;
    
    private final Integer paymentId;
    private final Integer accountId;
    private final BigDecimal previousAmount;
    private final BigDecimal newAmount;
    private final LocalDate paymentDate;
    
    private final String globalAccountNum;
    private final String globalCustNum;
    private final String clientDisplayName;
    
    public GroupLoanMemberAdjustmentDto(Integer paymentId, Integer accountId, BigDecimal previousAmount,
            BigDecimal newAmount, LocalDate paymentDate, String globalAccountNum, String globalCustNum,
            String clientDisplayName) {
        this.paymentId = paymentId;
        this.accountId = accountId;
        this.previousAmount = previousAmount;
        this.newAmount = newAmount;
        this.paymentDate = paymentDate;
        this.globalAccountNum = globalAccountNum;
        this.globalCustNum = globalCustNum;
        this.clientDisplayName = clientDisplayName;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public BigDecimal getPreviousAmount() {
        return previousAmount;
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public String getGlobalCustNum() {
        return globalCustNum;
    }

    public String getClientDisplayName() {
        return clientDisplayName;
    }
    
    @Override
    public int compareTo(GroupLoanMemberAdjustmentDto o) {
        return this.getAccountId().compareTo(o.getAccountId());
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof GroupLoanMemberAdjustmentDto) {
            isEqual = this.paymentId.equals(((GroupLoanMemberAdjustmentDto)obj).getPaymentId());
        }
        return isEqual;
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(paymentId).hashCode();
    }
}
