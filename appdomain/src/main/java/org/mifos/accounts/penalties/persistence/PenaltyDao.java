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

package org.mifos.accounts.penalties.persistence;

import java.util.List;

import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyCategoryEntity;
import org.mifos.accounts.penalties.business.PenaltyFormulaEntity;
import org.mifos.accounts.penalties.business.PenaltyFrequencyEntity;
import org.mifos.accounts.penalties.business.PenaltyPeriodEntity;
import org.mifos.accounts.penalties.business.PenaltyStatusEntity;
import org.mifos.accounts.penalties.util.helpers.PenaltyCategory;
import org.mifos.accounts.penalties.util.helpers.PenaltyFormula;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;

public interface PenaltyDao {
    public void save(PenaltyBO penaltyBO);
    
    public PenaltyBO findPenalty(int penaltyId);
    
    public List<PenaltyBO> findAllLoanPenalties();
    
    public List<PenaltyBO> findAllSavingPenalties();

    public PenaltyFrequencyEntity findPenaltyFrequencyEntityByType(PenaltyFrequency penaltyFrequency);

    public PenaltyCategoryEntity findPenaltyCategoryEntityByType(PenaltyCategory penaltyCategory);

    public PenaltyPeriodEntity findPenaltyPeriodEntityByType(PenaltyPeriod penaltyPeriod);

    public PenaltyFormulaEntity findPenaltyFormulaEntityByType(PenaltyFormula penaltyFormula);
    
    public PenaltyStatusEntity findPenaltyStatusEntityByType(PenaltyStatus penaltyStatus);

    public List<PenaltyCategoryEntity> getPenaltiesCategories();

    public List<PenaltyPeriodEntity> getPenaltiesPeriods();

    public List<PenaltyFormulaEntity> getPenaltiesFormulas();

    public List<PenaltyFrequencyEntity> getPenaltiesFrequencies();

    public List<PenaltyStatusEntity> getPenaltiesStatuses();
    
}
