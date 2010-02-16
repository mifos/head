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

package org.mifos.customers.util.helpers;

import java.io.Serializable;

public class LoanCycleCounter implements Serializable {

    private String offeringName;

    private int counter;

    public LoanCycleCounter() {

    }

    public LoanCycleCounter(String offeringName) {
        this.offeringName = offeringName;
    }

    public LoanCycleCounter(String offeringName, int counter) {
        this.offeringName = offeringName;
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getOfferingName() {
        return offeringName;
    }

    public void setOfferingName(String offeringName) {
        this.offeringName = offeringName;
    }

    /**
     * This method return true even if the offering name is same, it does not
     * bother about the counter value.
     */
    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof LoanCycleCounter) {
            LoanCycleCounter otherObj = (LoanCycleCounter) obj;
            return this.offeringName.equals(otherObj.offeringName);
        }
        return super.equals(obj);

    }

    /*
     * Since equals uses the offeringName String for Equality, use the same for
     * hashcode
     */
    @Override
    public int hashCode() {
        if (null != this.offeringName) {
            return this.offeringName.hashCode();
        }
        return super.hashCode();
    }

    public void incrementCounter() {
        this.counter++;

    }
}
