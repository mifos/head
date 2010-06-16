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

package org.mifos.accounts.productdefinition.business.service;

import java.util.List;

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.service.Service;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

/**
 * LoanProductService is a service layer encapsulation of methods related to
 * Loan Products (still called LoanOfferingBO). Unlike the existing
 * LoanPrdBusinessService class which returns business objects,
 * LoanProductService is intended not to expose business objects in any of its
 * return values. It can return primitives and Data Transfer Objects (DTOs). It
 * appears that existing "View" classes (such as FeeDto) serve as a kind of
 * DTO.
 */
public class LoanProductService implements Service {
    private LoanPrdBusinessService loanProductBusinessService;

    public LoanPrdBusinessService getLoanProductBusinessService() {
        return this.loanProductBusinessService;
    }

    public void setLoanProductBusinessService(LoanPrdBusinessService loanProductBusinessService) {
        this.loanProductBusinessService = loanProductBusinessService;
    }

    public LoanProductService() {
        // null constructor to use with setter injection.
    }

    public LoanProductService(LoanPrdBusinessService loanPrdBusinessService) {
        this.loanProductBusinessService = loanPrdBusinessService;
    }

    /*
     * For a given loan product, return the default fees associated with the
     * loan product and any additional fees that could be applied to it.
     *
     * @param loanProductId the loan product id
     *
     * @param userContext the user context to use when constructing FeeViews
     *
     * @param defaultFees the default fees list to populate
     *
     * @param additionalFees the additional fees list to populate
     *
     * @return the default and additional fees
     *
     * @throws ServiceException the service exception
     */
    public void getDefaultAndAdditionalFees(Short loanProductId, UserContext userContext, List<FeeDto> defaultFees,
            List<FeeDto> additionalFees) throws ServiceException, PersistenceException {
        LoanOfferingBO loanOffering = loanProductBusinessService.getLoanOffering(loanProductId);
        List<FeeBO> fees = new FeePersistence().getAllAppllicableFeeForLoanCreation();
        for (FeeBO fee : fees) {
            if (!fee.isPeriodic()
                    || (MeetingBO.isMeetingMatched(fee.getFeeFrequency().getFeeMeetingFrequency(), loanOffering
                            .getLoanOfferingMeeting().getMeeting()))) {
                FeeDto feeDto = new FeeDto(userContext, fee);
                if (loanOffering.isFeePresent(fee)) {
                    defaultFees.add(feeDto);
                } else {
                    additionalFees.add(feeDto);
                }
            }
        }
    }
}
