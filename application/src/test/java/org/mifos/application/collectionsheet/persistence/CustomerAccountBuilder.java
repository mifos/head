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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;

/**
 *
 */
public class CustomerAccountBuilder {
    
    private CustomerAccountBO customerAccount;
    private CustomerBO customer;
    private OfficeBO office;
    private PersonnelBO loanOfficer;
    private final Set<AmountFeeBO> accountFees = new HashSet<AmountFeeBO>();
    
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final Date createdDate = new DateTime().minusDays(14).toDate();
    
    public CustomerAccountBO buildForUnitTests() {

        customerAccount = new CustomerAccountBO(customer, accountFees, office, loanOfficer, createdDate,
                createdByUserId, false);
        return customerAccount;
    }

    public CustomerAccountBO buildForIntegrationTests() {
        
        customerAccount = new CustomerAccountBO(customer, accountFees, office, loanOfficer, createdDate,
                createdByUserId, true);
        return customerAccount;
    }
    
    public CustomerAccountBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
        return this;
    }
    
    public CustomerAccountBuilder withFee(final AmountFeeBO withFee) {
        accountFees.add(withFee);
        return this;
    }

    public CustomerAccountBuilder withOffice(final OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }

    public CustomerAccountBuilder withLoanOfficer(final PersonnelBO withLoanOfficer) {
        this.loanOfficer = withLoanOfficer;
        return this;
    }
}
