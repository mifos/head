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

package org.mifos.accounts.api;


/**
 * The Class PaymentTypeDto represents a type of payment (such
 * as cash or cheque) that can be used to make a payment.
 */
public class PaymentTypeDto {

    /** The value of the internal ID used for this payment type. */
    private final short value;

    /** The name of the payment type. */
    private final String name;

    /*
     * Only allow these to be constructed within this package and
     * then passed back out to constrain what is passed in.
     */
    /**
     * Instantiates a new payment type dto.
     *
     * @param value the ID value
     * @param name the name
     */
    PaymentTypeDto(short value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Gets the ID value.
     *
     * @return the value
     */
    public short getValue() {
        return value;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + this.value;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentTypeDto other = (PaymentTypeDto) obj;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;
        if (this.value != other.value)
            return false;
        return true;
    }

}
