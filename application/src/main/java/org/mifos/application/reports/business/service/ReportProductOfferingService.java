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

package org.mifos.application.reports.business.service;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.SavingsPrdBusinessService;
import org.mifos.framework.business.service.ConfigService;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.core.io.Resource;

public class ReportProductOfferingService extends ConfigService {

    private static final String LOAN_PRODUCT_IDS = "loan.product.ids";
    private static final String SAVINGS_PRODUCT_IDS = "savings.product.ids";

    private final LoanPrdBusinessService loanProductBusinessService;
    private final SavingsPrdBusinessService savingsProductBusinessService;

    public ReportProductOfferingService(LoanPrdBusinessService loanProductBusinessService,
            SavingsPrdBusinessService savingsProductBusinessService, Resource productOfferingConfig) {
        super(productOfferingConfig);
        this.loanProductBusinessService = loanProductBusinessService;
        this.savingsProductBusinessService = savingsProductBusinessService;
    }

    protected Short getLoanProduct1() throws ServiceException {
        return Short.valueOf(getPropertyValues(LOAN_PRODUCT_IDS).get(0));
    }

    protected Short getLoanProduct2() throws ServiceException {
        return Short.valueOf(getPropertyValues(LOAN_PRODUCT_IDS).get(1));
    }

    protected Short getSavingsProduct1() throws ServiceException {
        return Short.valueOf(getPropertyValues(SAVINGS_PRODUCT_IDS).get(0));
    }

    protected Short getSavingsProduct2() throws ServiceException {
        return Short.valueOf(getPropertyValues(SAVINGS_PRODUCT_IDS).get(1));
    }

    LoanOfferingBO getLoanOffering1() throws ServiceException {
        return loanProductBusinessService.getLoanOffering(getLoanProduct1());
    }

    LoanOfferingBO getLoanOffering2() throws ServiceException {
        return loanProductBusinessService.getLoanOffering(getLoanProduct2());
    }

    SavingsOfferingBO getSavingsOffering1() throws ServiceException {
        return savingsProductBusinessService.getSavingsProduct(getSavingsProduct1());
    }

    SavingsOfferingBO getSavingsOffering2() throws ServiceException {
        return savingsProductBusinessService.getSavingsProduct(getSavingsProduct2());
    }

    public boolean displaySignatureColumn(Integer columnNumber) throws ServiceException {
        return isPropertyPresent(ReportConfigServiceConstants.DISPLAY_SIGNATURE_COLUMN + "." + columnNumber)
                && Boolean.valueOf(getProperty(ReportConfigServiceConstants.DISPLAY_SIGNATURE_COLUMN + "."
                        + columnNumber));
    }
}
