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

package org.mifos.customers.office.business;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.business.util.Address;

public class OfficeAddressEntity extends AbstractEntity {

    private final Short officeAdressId;

    private final OfficeBO office;

    private Address address;

    public OfficeAddressEntity(OfficeBO office, Address address) {
        super();

        this.office = office;
        this.address = address;
        this.officeAdressId = null;
    }

    protected OfficeAddressEntity() {
        super();
        officeAdressId = null;
        office = null;
        address = new Address();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
