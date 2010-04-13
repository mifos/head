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

package org.mifos.customers.business;

import java.util.Set;

import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerLevel;

/**
 * This class represents customer level e.g client,center etc. Most code will want {@link CustomerLevel} (or perhaps
 * something else entirely, like calling a method in {@link CustomerBO} which can be implemented differently for
 * {@link ClientBO}, {@link CenterBO}, and {@link GroupBO}).
 */
// FIXME: this should just be an enum, not a persisted class
public class CustomerLevelEntity extends MasterDataEntity {

    /** The composite primary key value */
    private Short id;

    private Short localeId;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    // Set/gotten via .hbm.xml file
    private CustomerLevelEntity parentCustomerLevel;

    /*
     * Adding a default constructor is hibernate's requirement and should not be used to create a valid Object.
     */
    protected CustomerLevelEntity() {
        super();
    }

    public CustomerLevelEntity(CustomerLevel customerLevel) {
        this.id = customerLevel.getValue();
    }

    public Short getId() {
        return id;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    public String getName() {
        String name = MessageLookup.getInstance().lookup(getLookUpValue());
        return name;

    }

    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    public CustomerLevelEntity getParentCustomerLevel() {
        return parentCustomerLevel;
    }

    /**
     * Based on the customer level , it returns the product applicable type. This is being used, when savings/loan
     * products are to find as per customer level.
     */
    public Short getProductApplicableType() {
        if (getId().equals(CustomerLevel.CLIENT.getValue())) {
            return ApplicableTo.CLIENTS.getValue();
        } else if (getId().equals(CustomerLevel.GROUP.getValue())) {
            return ApplicableTo.GROUPS.getValue();
        } else {
            return ApplicableTo.CENTERS.getValue();
        }
    }

    public boolean isCenter() {
        return getId().equals(CustomerLevel.CENTER.getValue());
    }

    public boolean isClient() {
        return getId().equals(CustomerLevel.CLIENT.getValue());
    }

    public boolean isGroup() {
        return getId().equals(CustomerLevel.GROUP.getValue());
    }

    protected void setId(Short id) {
        this.id = id;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    protected void setName(String name) {
        MessageLookup.getInstance().updateLookupValue(getLookUpValue(), name);
    }

}
