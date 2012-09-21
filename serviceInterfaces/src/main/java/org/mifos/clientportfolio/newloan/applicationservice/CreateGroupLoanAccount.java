package org.mifos.clientportfolio.newloan.applicationservice;

import java.math.BigDecimal;
import java.util.List;

public class CreateGroupLoanAccount {

    private final List<CreateLoanAccount> memberAccounts;
    private final BigDecimal totalLoanAmount;
    private final CreateLoanAccount groupLoanAccountDetails;

    public CreateGroupLoanAccount(List<CreateLoanAccount> memberDetails,
            BigDecimal totalLoanAmount, CreateLoanAccount loanAccountDetails) {
        this.memberAccounts = memberDetails;
        this.totalLoanAmount = totalLoanAmount;
        this.groupLoanAccountDetails = loanAccountDetails;
    }

    public List<CreateLoanAccount> getMemberDetails() {
        return memberAccounts;
    }

    public BigDecimal getTotalLoanAmount() {
        return totalLoanAmount;
    }

    public CreateLoanAccount getGroupLoanAccountDetails() {
        return groupLoanAccountDetails;
    }
}
