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
import org.mifos.security.util.UserContext;

public class RatePenaltyBO extends PenaltyBO {
    private Double rate;
    private PenaltyFormulaEntity formula;
    
    public RatePenaltyBO(final UserContext userContext, final String name, final PenaltyCategoryEntity categoryEntity,
            final PenaltyPeriodEntity periodEntity, final Integer duration, final Double min, final Double max,
            final PenaltyFrequencyEntity frequencyEntity, final GLCodeEntity glCodeEntity, final PenaltyFormulaEntity formulaEntity,
            final Double rate) throws PenaltyException {
        super(userContext, name, categoryEntity, periodEntity, duration, min, max, frequencyEntity, glCodeEntity);

        validateFields(rate, formulaEntity);
        this.formula = formulaEntity;
        this.rate = rate;
    }
    
    protected RatePenaltyBO() {
        super();
        this.rate = null;
        this.formula = null;
    }

    public Double getRate() {
        return rate;
    }
    
    public void setRate(Double rate) {
        this.rate = rate;
    }

    public PenaltyFormulaEntity getFormula() {
        return formula;
    }

    public void setFormula(PenaltyFormulaEntity formula) {
        this.formula = formula;
    }
    
    private void validateFields(Double rate, PenaltyFormulaEntity formula) throws PenaltyException {
        if (rate == null || rate.doubleValue() <= 0.0 || formula == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_RATE_OR_FORMULA);
        }
    }
}
