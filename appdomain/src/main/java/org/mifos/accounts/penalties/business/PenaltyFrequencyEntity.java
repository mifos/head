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

import java.util.Set;

import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.dto.domain.PenaltyFrequencyDto;

public class PenaltyFrequencyEntity extends MasterDataEntity {
    /** The composite primary key value */
    private Short id;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;
    
    public PenaltyFrequencyEntity(PenaltyFrequency penaltyFrequency) {
        this.id = penaltyFrequency.getValue();
    }

    protected PenaltyFrequencyEntity() {
    }

    public boolean isOneTime() {
        return getId().equals(PenaltyFrequency.NONE.getValue());
    }

    @Override
    public Short getId() {
        return id;
    }

    protected void setId(Short id) {
        this.id = id;
    }

    @Override
    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    public void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    @Override
    public String getName() {
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(getLookUpValue());
    }

    @Override
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    protected void setName(String name) {
        ApplicationContextProvider.getBean(MessageLookup.class).updateLookupValue(getLookUpValue(), name);
    }
    
    public PenaltyFrequencyDto toDto() {
        PenaltyFrequencyDto dto = new PenaltyFrequencyDto();
        dto.setId(id);
        dto.setName(getName());
        
        return dto;
    }
}
