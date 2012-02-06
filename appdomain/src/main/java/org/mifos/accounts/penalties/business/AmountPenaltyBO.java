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

package org.mifos.accounts.penalties.business;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.penalties.exceptions.PenaltyException;
import org.mifos.accounts.penalties.util.helpers.PenaltyConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public class AmountPenaltyBO extends PenaltyBO {
    private Money amount;
    
    public AmountPenaltyBO(final UserContext userContext, final String name, final PenaltyCategoryEntity categoryEntity,
            final PenaltyPeriodEntity periodEntity, final Integer duration, final Integer min, final Integer max,
            final PenaltyFrequencyEntity frequencyEntity, final GLCodeEntity glCodeEntity, final Money amount) throws PenaltyException {
        super(userContext, name, categoryEntity, periodEntity, duration, min, max, frequencyEntity, glCodeEntity);
        
        validateAmount(amount);
        this.amount = amount;
    }
    
    protected AmountPenaltyBO() {
        super();
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }
    
    private void validateAmount(final Money amount) throws PenaltyException {
        if (amount == null || amount.isLessThanOrEqualZero()) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_AMOUNT);
        }
    }
}
