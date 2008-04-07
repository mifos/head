package org.mifos.application.customer;

import java.util.List;

import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.business.util.Address;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 */
public class CustomerTemplateImpl implements CustomerTemplate {
    private CustomerStatus customerStatus;
    private String displayName;
    private Address address;
    private List<CustomFieldView> customFieldViews;
    private List<FeeView> feeViews;
    private String externalId;
    private Short loanOfficerId;

    protected CustomerTemplateImpl(String displayName, CustomerStatus status) {
        this.displayName = displayName;
        this.loanOfficerId = PersonnelConstants.SYSTEM_USER;
        this.customerStatus = status;
    }

    public CustomerStatus getCustomerStatus() {
        return this.customerStatus;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Address getAddress() {
        return this.address;
    }

    public List<CustomFieldView> getCustomFieldViews() {
        return this.customFieldViews;
    }

    public List<FeeView> getFees() {
        return this.feeViews;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }
}
