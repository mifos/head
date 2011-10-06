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

package org.mifos.domain.builders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerPositionDto;

public class CenterUpdateBuilder {

    public CenterUpdate build() {

        int versionNum = 1;
        int customerId = -1;
        Short loanOfficerId = -1;
        String externalId = null;
        String updatedDisplayName = "Center "+RandomStringUtils.random(5);
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        AddressDto address = null;
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        return new CenterUpdate(customerId, updatedDisplayName, versionNum, loanOfficerId, externalId, mfiJoiningDate, address, customFields, customerPositions);
    }
}