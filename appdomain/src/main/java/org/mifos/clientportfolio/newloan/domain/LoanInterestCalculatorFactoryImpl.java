/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.core.MifosRuntimeException;

public class LoanInterestCalculatorFactoryImpl implements LoanInterestCalculatorFactory {

    @Override
    public LoanInterestCalculator create(InterestType interestType, boolean variableInstallmentLoanProduct) {

        if (variableInstallmentLoanProduct) {
            switch (interestType) {
            case FLAT:
                throw new MifosRuntimeException("interestType not supported: " + interestType);
            case DECLINING:
                return new DecliningBalanceWithInterestCalculatedDailyLoanInterestCalculator();
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
            return new FlatLoanInterestCalculator();
        case DECLINING:
            return new DecliningBalanceLoanInterestCalculator();
        case DECLINING_EPI:
            return new DecliningBalanceWithEqualPrincipalInstallmentsLoanInterestCalculator();
        case DECLINING_PB:
            return new NullLoanInterestCalculator();
        default:
            throw new MifosRuntimeException("interestType not supported: " + interestType);
        }
    }
}