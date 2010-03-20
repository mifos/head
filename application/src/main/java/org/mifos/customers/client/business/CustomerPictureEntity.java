/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.sql.Blob;

import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.business.PersistentObject;

public class CustomerPictureEntity extends PersistentObject {

    private final Integer pictureId;

    private final CustomerBO customer;

    private Blob picture;

    public CustomerPictureEntity(CustomerBO customer, Blob picture) {
        super();
        this.pictureId = null;
        this.customer = customer;
        this.picture = picture;
    }

    protected CustomerPictureEntity() {
        super();
        this.pictureId = null;
        this.customer = null;
        this.picture = null;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

    public Integer getPictureId() {
        return pictureId;
    }
}
