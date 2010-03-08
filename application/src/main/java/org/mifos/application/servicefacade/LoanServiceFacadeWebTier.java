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

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;

/**
 * Implementation of {@link LoanServiceFacade} for web application usage.
 */
public class LoanServiceFacadeWebTier implements LoanServiceFacade {

    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;

    public LoanServiceFacadeWebTier(final LoanProductDao loanProductDao, final CustomerDao customerDao) {
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
    }

    @Override
    public List<LoanOfferingBO> loadActiveProductsApplicableForCustomer(final CustomerBO customer) {

        final List<LoanOfferingBO> applicableLoanProducts = new ArrayList<LoanOfferingBO>();

        final List<LoanOfferingBO> loanOfferings = loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(customer
                .getCustomerLevel());

        final MeetingBO customerMeeting = customer.getCustomerMeetingValue();
        for (LoanOfferingBO loanProduct : loanOfferings) {
            if (MeetingBO.isMeetingMatched(customerMeeting, loanProduct.getLoanOfferingMeetingValue())) {
                applicableLoanProducts.add(loanProduct);
            }
        }

        return applicableLoanProducts;
    }

    @Override
    public LoanCreationGlimDto retrieveGlimSpecificDataForGroup(final CustomerBO customer) {

        final List<ValueListElement> loanPurposes = loanProductDao.findAllLoanPurposes();

        final List<ClientBO> activeClientsOfGroup = customerDao.findActiveClientsUnderGroup(customer);

        return new LoanCreationGlimDto(loanPurposes, activeClientsOfGroup);
    }
}
