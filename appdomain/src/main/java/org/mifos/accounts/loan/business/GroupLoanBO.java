package org.mifos.accounts.loan.business;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupLoanBO extends LoanBO {

    private static final Logger logger = LoggerFactory.getLogger(GroupLoanBO.class);
    
    private Set<LoanBO> childAccounts;

    public Set<LoanBO> getChildAccounts() {
        return childAccounts;
    }

    public void setChildAccounts(Set<LoanBO> childAccounts) {
        this.childAccounts = childAccounts;
    }
    
}
