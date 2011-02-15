package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;

public class EqualInstallmentGeneratorFactoryImpl implements EqualInstallmentGeneratorFactory {

    @Override
    public EqualInstallmentGenerator create(InterestType interestType, LoanUsuageDetail loanUsageDetail, Money loanInterest) {

        // FIXME - KEITHW - we should really just remove these checks.
        if (loanUsageDetail.isInterestDeductedAtDisbursement()) {
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
        }

        if (loanUsageDetail.isPrincipalDueOnLastInstallment()) {
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
        }

        switch (interestType) {
        case FLAT:
//            return allFlatInstallments_v2(loanInterest);
            return new FlatLoanInterestEqualInstallmentGenerator(loanInterest);
        case DECLINING:
        case DECLINING_PB:
            return new DecliningBalanceEqualInstallmentGenerator();
//            return allDecliningInstallments_v2();
        case DECLINING_EPI:
            return new DecliningBalanceEqualPrincipalEqualInstallmentGenerator();
//            return allDecliningEPIInstallments_v2();
        default:
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
        }
    }
}