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

package org.mifos.customers;

import java.util.List;

import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.framework.business.util.Address;

/**
 * use builders instead of templates.
 */
@Deprecated
public class CustomerTemplateImpl implements CustomerTemplate {
    private CustomerStatus customerStatus;
    private String displayName;
    private Address address;
    private List<CustomFieldDto> customFieldDtos;
    private List<FeeDto> feeDtos;
    private String externalId;
    private Short loanOfficerId;

    protected CustomerTemplateImpl(String displayName, CustomerStatus status) {
        this.displayName = displayName;
        this.loanOfficerId = PersonnelConstants.SYSTEM_USER;
        this.customerStatus = status;
    }

    @Override
	public CustomerStatus getCustomerStatus() {
        return this.customerStatus;
    }

    @Override
	public String getDisplayName() {
        return this.displayName;
    }

    @Override
	public Address getAddress() {
        return this.address;
    }

    @Override
	public List<CustomFieldDto> getCustomFieldViews() {
        return this.customFieldDtos;
    }

    @Override
	public List<FeeDto> getFees() {
        return this.feeDtos;
    }

    @Override
	public String getExternalId() {
        return this.externalId;
    }

    @Override
	public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }
}
