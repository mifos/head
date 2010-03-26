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

import java.io.InputStream;
import java.util.List;

import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientNameDetailView;
import org.mifos.framework.business.util.Address;

public class ClientPersonalInfoUpdate {

    private final List<CustomerCustomFieldEntity> clientCustomFields;
    private final Address address;
    private final ClientDetailView clientDetail;
    private final ClientNameDetailView clientNameDetails;
    private final ClientNameDetailView spouseFather;
    private final InputStream picture;
    private final String governmentId;
    private final String clientDisplayName;
    private final String dateOfBirth;

    public ClientPersonalInfoUpdate(List<CustomerCustomFieldEntity> clientCustomFields, Address address,
            ClientDetailView clientDetail, ClientNameDetailView clientNameDetails, ClientNameDetailView spouseFather,
            InputStream picture, String governmentId, String clientDisplayName, String dateOfBirth) {
        this.clientCustomFields = clientCustomFields;
        this.address = address;
        this.clientDetail = clientDetail;
        this.clientNameDetails = clientNameDetails;
        this.spouseFather = spouseFather;
        this.picture = picture;
        this.governmentId = governmentId;
        this.clientDisplayName = clientDisplayName;
        this.dateOfBirth = dateOfBirth;
    }

    public List<CustomerCustomFieldEntity> getClientCustomFields() {
        return this.clientCustomFields;
    }

    public Address getAddress() {
        return this.address;
    }

    public ClientDetailView getClientDetail() {
        return this.clientDetail;
    }

    public ClientNameDetailView getClientNameDetails() {
        return this.clientNameDetails;
    }

    public ClientNameDetailView getSpouseFather() {
        return this.spouseFather;
    }

    public InputStream getPicture() {
        return this.picture;
    }

    public String getGovernmentId() {
        return this.governmentId;
    }

    public String getClientDisplayName() {
        return this.clientDisplayName;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }
}