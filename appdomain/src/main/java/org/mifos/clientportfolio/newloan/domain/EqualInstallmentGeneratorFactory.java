package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.util.helpers.Money;

public interface EqualInstallmentGeneratorFactory {

    PrincipalWithInterestGenerator create(InterestType interestType, Money loanInterest, boolean variableInstallmentLoanProduct);

}
