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

import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientNameDetailView;

public class ClientDetailDto {

    private final String governmentId;
    private final String dateOfBirth;
    private final ClientDetailView customerDetail;
    private final ClientNameDetailView clientName;
    private final ClientNameDetailView spouseName;
    private final String clientDisplayName;

    public ClientDetailDto(String governmentId, String dateOfBirth, ClientDetailView customerDetail,
            ClientNameDetailView clientName, ClientNameDetailView spouseName) {
        this.governmentId = governmentId;
        this.dateOfBirth = dateOfBirth;
        this.customerDetail = customerDetail;
        this.clientName = clientName;
        this.clientDisplayName = clientName.getDisplayName();
        this.spouseName = spouseName;
    }

    public ClientDetailDto(String governmentId, String dateOfBirth, String clientDisplayName) {
        this.governmentId = governmentId;
        this.dateOfBirth = dateOfBirth;
        this.clientDisplayName = clientDisplayName;
        this.customerDetail = null;
        this.clientName = null;
        this.spouseName = null;
    }

    public String getGovernmentId() {
        return this.governmentId;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public ClientDetailView getCustomerDetail() {
        return this.customerDetail;
    }

    public ClientNameDetailView getClientName() {
        return this.clientName;
    }

    public ClientNameDetailView getSpouseName() {
        return this.spouseName;
    }

    public String getClientDisplayName() {
        return this.clientDisplayName;
    }
}