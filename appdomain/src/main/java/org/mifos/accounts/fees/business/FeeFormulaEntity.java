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

package org.mifos.accounts.fees.business;

import java.util.Set;

import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.dto.domain.FeeFormulaDto;

public class FeeFormulaEntity extends MasterDataEntity {

    private FeeFormula feeFormula;

    /** The composite primary key value */
    private Short id;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    protected FeeFormulaEntity() {
    }

    public FeeFormulaEntity(FeeFormula feeFormula) {
        this.id  = feeFormula.getValue();
        this.feeFormula = feeFormula;
    }

    public FeeFormula getFeeFormula() {
        if (feeFormula == null) {
            try {
                feeFormula = FeeFormula.getFeeFormula(this.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return feeFormula;
    }

    public String getFormulaString() {
        if (feeFormula == null) {
            try {
                feeFormula = FeeFormula.getFeeFormula(this.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return "Formula: % " + ApplicationContextProvider.getBean(MessageLookup.class).lookup(feeFormula);
    }

    public String getFormulaStringThatHasName() {
        return "Formula: % " + getName();
    }

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    @Override
    public String getName() {
        String name = ApplicationContextProvider.getBean(MessageLookup.class).lookup(getLookUpValue());
        return name;

    }

    @Override
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    protected void setId(Short id) {
        this.id = id;
    }

    public void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    protected void setName(String name) {
        ApplicationContextProvider.getBean(MessageLookup.class).updateLookupValue(getLookUpValue(), name);
    }

    public FeeFormulaDto toDto() {
        FeeFormulaDto feeFormulaDto = new FeeFormulaDto();
        feeFormulaDto.setId(this.id);
        feeFormulaDto.setName(this.getName());
        return feeFormulaDto;
    }
}