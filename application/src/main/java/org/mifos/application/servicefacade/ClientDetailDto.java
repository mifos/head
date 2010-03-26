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
import org.mifos.customers.client.util.helpers.ClientConstants;

public class ClientDetailDto {

    private final String governmentId;
    private final String dateOfBirth;
    private final ClientDetailView customerDetail;
    private final ClientNameDetailView clientName;
    private final ClientNameDetailView spouseName;
    private final String clientDisplayName;
    private final boolean groupFlagIsSet;
    private final Integer parentGroupId;
    private final boolean trained;
    private final String trainedDate;

    public ClientDetailDto(String governmentId, String dateOfBirth, ClientDetailView customerDetail,
            ClientNameDetailView clientName, ClientNameDetailView spouseName, boolean groupFlagIsSet,
            Integer parentGroupId, boolean trained, String trainedDate) {
        this.governmentId = governmentId;
        this.dateOfBirth = dateOfBirth;
        this.customerDetail = customerDetail;
        this.clientName = clientName;
        if (clientName != null) {
            this.clientDisplayName = clientName.getDisplayName();
        } else {
            this.clientDisplayName = "";
        }
        this.spouseName = spouseName;
        this.groupFlagIsSet = groupFlagIsSet;
        this.parentGroupId = parentGroupId;
        this.trained = trained;
        this.trainedDate = trainedDate;
    }

    public ClientDetailDto(String governmentId, String dateOfBirth, String clientDisplayName) {
        this.governmentId = governmentId;
        this.dateOfBirth = dateOfBirth;
        this.clientDisplayName = clientDisplayName;
        this.customerDetail = null;
        this.clientName = null;
        this.spouseName = null;
        this.groupFlagIsSet = false;
        this.parentGroupId = null;
        this.trained = false;
        this.trainedDate = null;
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

    public boolean isGroupFlagIsSet() {
        return this.groupFlagIsSet;
    }

    public Integer getParentGroupId() {
        return this.parentGroupId;
    }

    public boolean isTrained() {
        return this.trained;
    }

    public String getTrainedDate() {
        return this.trainedDate;
    }

    public String getGroupFlagAsString() {
        if (this.groupFlagIsSet) {
            return ClientConstants.YES;
        }
        return ClientConstants.NO;
    }

    public String getTrainedAsString() {
        if (this.trained) {
            return ClientConstants.YES;
        }
        return ClientConstants.NO;
    }
}