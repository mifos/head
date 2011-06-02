package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;

public class EqualInstallmentGeneratorFactoryImpl implements EqualInstallmentGeneratorFactory {

    @Override
    public PrincipalWithInterestGenerator create(InterestType interestType, Money loanInterest, boolean variableInstallmentLoanProduct) {

        if (variableInstallmentLoanProduct) {
            switch (interestType) {
            case FLAT:
                throw new MifosRuntimeException("interestType not supported: " + interestType);
            case DECLINING:
                return new VariableInstallmentPrincipalWithInterestGenerator();
            case DECLINING_EPI:
                throw new MifosRuntimeException("interestType not supported: " + interestType);
            case DECLINING_PB:
                throw new MifosRuntimeException("interestType not supported: " + interestType);
            default:
                throw new MifosRuntimeException("interestType not supported: " + interestType);
            }
        }
        
        switch (interestType) {
        case FLAT:
            return new FlatLoanPrincipalWithInterestGenerator(loanInterest);
        case DECLINING:
        case DECLINING_PB:
            return new DecliningBalancePrincipalWithInterestGenerator();
        case DECLINING_EPI:
            return new DecliningBalanceEqualPrincipalWithInterestGenerator();
        default:
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
        }
    }
}