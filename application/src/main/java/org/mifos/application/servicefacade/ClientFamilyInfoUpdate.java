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

package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.customers.client.business.ClientFamilyDetailDto;
import org.mifos.customers.client.business.ClientNameDetailDto;

public class ClientFamilyInfoUpdate {

    private final Integer customerId;
    private final Integer oldVersionNum;
    private final List<Integer> familyPrimaryKey;
    private final List<ClientNameDetailDto> familyNames;
    private final List<ClientFamilyDetailDto> familyDetails;

    public ClientFamilyInfoUpdate(Integer customerId, Integer oldVersionNum, List<Integer> familyPrimaryKey, List<ClientNameDetailDto> familyNames, List<ClientFamilyDetailDto> familyDetails) {
        this.customerId = customerId;
        this.oldVersionNum = oldVersionNum;
        this.familyPrimaryKey = familyPrimaryKey;
        this.familyNames = familyNames;
        this.familyDetails = familyDetails;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getOldVersionNum() {
        return this.oldVersionNum;
    }

    public List<Integer> getFamilyPrimaryKey() {
        return this.familyPrimaryKey;
    }

    public List<ClientNameDetailDto> getFamilyNames() {
        return this.familyNames;
    }

    public List<ClientFamilyDetailDto> getFamilyDetails() {
        return this.familyDetails;
    }
}