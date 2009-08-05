package org.mifos.framework.util;

import java.math.BigDecimal;
import org.mifos.core.MifosException;

public class AccountingRulesParameters {
    private Integer numberOfInterestDays;
    private Integer digitsAfterDecimal;
    private Integer digitsAfterDecimalForInterest;
    private BigDecimal maxInterest;
    private BigDecimal minInterest;
    private Boolean backDatedTransactionsAllowed;

    public void setParameters(String numberOfInterestDays, String digitsAfterDecimal, String digitsAfterDecimalForInterest,
            String maxInterest, String minInterest, String backDatedTransactionsAllowed) throws MifosException {
        try {
            this.numberOfInterestDays = parseInteger(numberOfInterestDays);
            this.digitsAfterDecimal = parseInteger(digitsAfterDecimal);
            this.digitsAfterDecimalForInterest = parseInteger(digitsAfterDecimalForInterest);
            this.maxInterest = parseBigDecimal(maxInterest);
            this.minInterest = parseBigDecimal(minInterest);
            this.backDatedTransactionsAllowed = parseBoolean(backDatedTransactionsAllowed);
        } catch (NumberFormatException e) {
            throw new MifosException("Could not set parameters: ", e);
        }
    }
    
    public Integer getNumberOfInterestDays() {
        return this.numberOfInterestDays;
    }

    public Integer getDigitsAfterDecimal() {
        return this.digitsAfterDecimal;
    }

    public Integer getDigitsAfterDecimalForInterest() {
        return this.digitsAfterDecimalForInterest;
    }

    public BigDecimal getMaxInterest() {
        return this.maxInterest;
    }

    public BigDecimal getMinInterest() {
        return this.minInterest;
    }

    public Boolean getBackDatedTransactionsAllowed() {
        return this.backDatedTransactionsAllowed;
    }

    private BigDecimal parseBigDecimal(String valueToParse) {
        BigDecimal result = null;
        if (valueToParse != null) {
            result = new BigDecimal(valueToParse.trim());
        }
        return result;
    }    
    private Boolean parseBoolean(String valueToParse) {
        Boolean result = null;
        if (valueToParse != null) {
            result = Boolean.valueOf(valueToParse.trim());
        }
        return result;
    }    
    
    private Integer parseInteger(String valueToParse) {
        Integer result =  null;
        if (valueToParse != null) {
            result = new Integer(valueToParse.trim());
        }
        return result;
    }
    
    public String toString() {
        int initialStringBufferSize = 1000;
        StringBuffer result = new StringBuffer(initialStringBufferSize);
        result.append("AccountingRulesParameter:: ");
        result.append(" numberOfInterestDays: ");
        result.append(numberOfInterestDays);
        result.append(" digitsAfterDecimal: ");
        result.append(digitsAfterDecimal);
        result.append(" digitsAfterDecimalForInterest: ");
        result.append(digitsAfterDecimalForInterest);
        result.append(" maxInterest: ");
        result.append(maxInterest);
        result.append(" minInterest: ");
        result.append(minInterest);
        result.append(" backDatedTransactionsAllowed: ");
        result.append(backDatedTransactionsAllowed);
        return result.toString();
    }

}