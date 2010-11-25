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

package org.mifos.customers.client.business;

import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.business.util.Name;

public class ClientNameDetailEntity extends AbstractEntity {

    private final Integer customerNameId;
    private CustomerBO customer;
    private Short nameType;
    private Integer salutation;
    private String secondMiddleName;
    private String displayName;
    private Name name;

    public ClientNameDetailEntity(ClientBO client, String secondMiddleName, ClientNameDetailDto view) {
        super();
        this.customerNameId = null;
        this.customer = client;
        this.nameType = view.getNameType();
        this.salutation = view.getSalutation();
        this.secondMiddleName = secondMiddleName;
        view.setNames(ClientRules.getNameSequence());
        this.displayName = view.getDisplayName();
        this.name = new Name(view.getFirstName(), view.getMiddleName(), view.getSecondLastName(), view.getLastName());
    }

    protected ClientNameDetailEntity() {
        super();
        this.customerNameId = null;
        this.customer = null;
        this.nameType = null;
        this.salutation = null;
        this.secondMiddleName = null;
        this.displayName = null;
        this.name = null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getSalutation() {
        return salutation;
    }

    public void setSalutation(Integer salutation) {
        this.salutation = salutation;
    }

    public String getSecondMiddleName() {
        return secondMiddleName;
    }

    public void setSecondMiddleName(String secondMiddleName) {
        this.secondMiddleName = secondMiddleName;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public Short getNameType() {
        return this.nameType;
    }

    public void setNameType(Short nameType) {
        this.nameType = nameType;
    }

    public Integer getCustomerNameId() {
        return customerNameId;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void updateNameDetails(ClientNameDetailDto nameView) {
        this.nameType = nameView.getNameType();
        this.salutation = nameView.getSalutation();
        this.displayName = nameView.getDisplayName();
        this.name = new Name(nameView.getFirstName(), nameView.getMiddleName(), nameView.getSecondLastName(), nameView.getLastName());
    }

    public ClientNameDetailDto toDto() {

        ClientNameDetailDto clientNameDetail = new ClientNameDetailDto(this.nameType, this.salutation,
                new StringBuilder(this.displayName), this.name.getFirstName(),
                this.name.getMiddleName(), this.name.getLastName(), this.name.getSecondLastName(), this.customerNameId);
        clientNameDetail.setNames(ClientRules.getNameSequence());

        return clientNameDetail;
    }

    public boolean isNotClientNameType() {
        return this.nameType.shortValue() != ClientConstants.CLIENT_NAME_TYPE;
    }

    public boolean matchesCustomerId(Integer customerNameIdToMatch) {
        return this.customerNameId.equals(customerNameIdToMatch);
    }

    public void setClient(ClientBO client) {
        this.customer = client;
    }
}