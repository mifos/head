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
package org.mifos.application.servicefacade;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.mifos.customers.business.CustomerBO;

/**
 * I encapsulate the data types used for {@link CustomerBO} hierarchy's.
 */
public class CustomerHierarchyParams {

    private final Integer customerAtTopOfHierarchyId;
    private final Short branchId;
    private final String searchId;
    private final LocalDate transactionDate;

    public CustomerHierarchyParams(final Integer customerAtTopOfHierarchyId, final Short branchId,
            final String searchId, final LocalDate transactionDate) {
        this.customerAtTopOfHierarchyId = customerAtTopOfHierarchyId;
        this.branchId = branchId;
        this.searchId = searchId;
        this.transactionDate = transactionDate;
    }

    public Integer getCustomerAtTopOfHierarchyId() {
        return this.customerAtTopOfHierarchyId;
    }

    public Short getBranchId() {
        return this.branchId;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    @Override
    public boolean equals(final Object obj) {
        CustomerHierarchyParams rhs = (CustomerHierarchyParams) obj;
        return EqualsBuilder.reflectionEquals(this, rhs);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
