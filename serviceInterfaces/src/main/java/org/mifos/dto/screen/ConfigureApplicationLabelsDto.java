/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.dto.screen;

import org.mifos.dto.domain.AccountStatusesLabelDto;
import org.mifos.dto.domain.ConfigurableLookupLabelDto;
import org.mifos.dto.domain.GracePeriodDto;
import org.mifos.dto.domain.OfficeLevelDto;

public class ConfigureApplicationLabelsDto {

    private final OfficeLevelDto officeLevels;
    private final GracePeriodDto gracePeriodDto;
    private final ConfigurableLookupLabelDto lookupLabels;
    private final AccountStatusesLabelDto accountStatusLabels;

    public ConfigureApplicationLabelsDto(OfficeLevelDto officeLevels, GracePeriodDto gracePeriodDto, ConfigurableLookupLabelDto lookupLabels, AccountStatusesLabelDto accountStatusLabels) {
        this.officeLevels = officeLevels;
        this.gracePeriodDto = gracePeriodDto;
        this.lookupLabels = lookupLabels;
        this.accountStatusLabels = accountStatusLabels;
    }

    public OfficeLevelDto getOfficeLevels() {
        return this.officeLevels;
    }

    public GracePeriodDto getGracePeriodDto() {
        return this.gracePeriodDto;
    }

    public ConfigurableLookupLabelDto getLookupLabels() {
        return this.lookupLabels;
    }

    public AccountStatusesLabelDto getAccountStatusLabels() {
        return this.accountStatusLabels;
    }
}