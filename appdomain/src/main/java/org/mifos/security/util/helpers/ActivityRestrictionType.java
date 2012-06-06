package org.mifos.security.util.helpers;

import org.mifos.config.LocalizedTextLookup;

public enum ActivityRestrictionType implements LocalizedTextLookup {

    MAX_LOAN_AMOUNT_FOR_APPROVE((short)1, "ActivityRestriction-MaxLoanAmountForApprove");
    
    short value;
    String messageKey;
    
    private ActivityRestrictionType(short value, String messageKey) {
        this.value = value;
        this.messageKey = messageKey;
    }

    @Override
    public String getPropertiesKey() {
        return messageKey;
    }

    public Short getValue(){
        return value;
    }
}
