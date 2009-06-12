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

package org.mifos.application.customer.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.StateEntity;
import org.mifos.application.util.helpers.YesNoFlag;

/**
 * Should be replaced by {@link CustomerStatus}.
 */
public class CustomerStatusEntity extends StateEntity {

    private String description;

    private CustomerLevelEntity customerLevel;

    private Short optional;

    private Set<CustomerStatusFlagEntity> flagSet;

    public CustomerStatusEntity(CustomerStatus customerStatus) {
        super(customerStatus.getValue());
        this.flagSet = new HashSet<CustomerStatusFlagEntity>();
    }

    /*
     * Adding a default constructor is hibernate's requirement and should not be
     * used to create a valid Object.
     */
    protected CustomerStatusEntity() {
    }

    public CustomerStatusEntity(Short customerStateId) {
        super(customerStateId);
        this.flagSet = new HashSet<CustomerStatusFlagEntity>();

    }

    public CustomerLevelEntity getCustomerLevel() {
        return customerLevel;
    }

    public String getDescription() {
        return description;
    }

    public Short getOptional() {
        return optional;
    }

    /**
     * Safer, preferred method for getting the "optional" property. Can't be
     * "isOptional()" because hibernate gets confused.
     */
    public boolean getIsOptional() {
        return optional.equals(YesNoFlag.YES.getValue());
    }

    public void setOptional(Short optional) {
        this.optional = optional;
    }

    /**
     * Safer, preferred method for setting the "optional" property. Can't be
     * "setOptional(boolean)" because hibernate gets confused.
     */
    public void setIsOptional(boolean optional) {
        if (optional)
            setOptional(YesNoFlag.YES.getValue());
        else
            setOptional(YesNoFlag.NO.getValue());
    }

    public Set<CustomerStatusFlagEntity> getFlagSet() {
        return flagSet;
    }

    public void setFlagSet(Set<CustomerStatusFlagEntity> flagSet) {
        this.flagSet = flagSet;
    }

}
