package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.mifos.accounts.loan.util.helpers.EMIInstallment;

public interface EqualInstallmentGenerator {

    List<EMIInstallment> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails);

}