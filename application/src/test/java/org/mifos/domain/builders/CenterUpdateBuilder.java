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

package org.mifos.domain.builders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.customers.business.CustomerPositionDto;
import org.mifos.framework.business.util.Address;

public class CenterUpdateBuilder {

    public CenterUpdate build() {

        int versionNum = 1;
        int customerId = -1;
        Short loanOfficerId = -1;
        String externalId = null;
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        Address address = null;
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        return new CenterUpdate(customerId, versionNum, loanOfficerId, externalId, mfiJoiningDate, address, customFields, customerPositions);
    }

}
