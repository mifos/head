package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;

public interface PrincipalWithInterestGenerator {

    List<InstallmentPrincipalAndInterest> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails);

}