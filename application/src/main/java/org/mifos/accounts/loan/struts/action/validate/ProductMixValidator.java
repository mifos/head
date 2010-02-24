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

package org.mifos.accounts.loan.struts.action.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.customers.business.CustomerBO;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

public class ProductMixValidator {

    public ProductMixValidator() {
        this(new LoanBusinessService(), new AccountBusinessService(), new ConfigurationBusinessService(),
                new ProductMixBusinessService());
    }

    ProductMixValidator(ConfigurationBusinessService configService, ProductMixBusinessService productMixBusinessService) {
        this(new LoanBusinessService(), new AccountBusinessService(), configService, productMixBusinessService);
    }

    private ProductMixValidator(LoanBusinessService loanBusinessService, AccountBusinessService accountBusinessService,
            ConfigurationBusinessService configService, ProductMixBusinessService productMixBusinessService) {
        this.loanBusinessService = loanBusinessService;
        this.accountBusinessService = accountBusinessService;
        this.configService = configService;
        this.productMixBusinessService = productMixBusinessService;
    }

    public void checkIfProductsOfferingCanCoexist(LoanBO loan) throws PersistenceException, AccountException,
            ServiceException {
        CustomerBO customer = loan.getCustomer();
        List<LoanBO> loanList = (loanBusinessService.getLoanAccountsActiveInGoodBadStanding(customer.getCustomerId()));
        loanList.addAll(populateLoanListForCustomer(loan, customer));
        validateProductMix(loan, loanList);
    }

    private List<LoanBO> populateLoanListForCustomer(LoanBO loan, CustomerBO customer) throws ServiceException {
        List<LoanBO> loanList = new ArrayList<LoanBO>();
        if (customer.isGroup()) {
            loanList = loanBusinessService.getActiveLoansForAllClientsAssociatedWithGroupLoan(loan);
        } else if (customer.isClient() && customer.getParentCustomer() != null) {
            List<LoanBO> groupLoans = loanBusinessService.getLoanAccountsActiveInGoodBadStanding(customer
                    .getParentCustomer().getCustomerId());
            loanList = getLoansToCheckAgainstProductMix(customer, groupLoans);
        }
        return loanList;
    }

    List<LoanBO> getLoansToCheckAgainstProductMix(final CustomerBO customer, List<LoanBO> groupLoans)
            throws ServiceException {
        List<LoanBO> activeLoansWhereClientIsAMember = new ArrayList<LoanBO>();

        for (LoanBO loanBO : groupLoans) {
            if (configService.isGlimEnabled()) {
                if (isCustomerACoSigningClient(customer, loanBO)) {
                    activeLoansWhereClientIsAMember.add(loanBO);
                }
            } else {
                activeLoansWhereClientIsAMember.addAll(groupLoans);
            }
        }
        return activeLoansWhereClientIsAMember;
    }

    private List<CustomerBO> getCosigningClientsForLoan(LoanBO loanBO) throws ServiceException {
        return accountBusinessService.getCoSigningClientsForGlim(loanBO.getAccountId());
    }

    boolean isCustomerACoSigningClient(final CustomerBO customer, LoanBO loan) throws ServiceException {
        return CollectionUtils.find(getCosigningClientsForLoan(loan), new Predicate() {
            public boolean evaluate(Object arg0) {
                return customer.getCustomerId().equals(((CustomerBO) arg0).getCustomerId());
            }
        }) != null;
    }

    void validateProductMix(LoanBO newloan, List<LoanBO> loanList) throws PersistenceException, AccountException,
            ServiceException {
        if (null != loanList) {
            for (LoanBO loan : loanList) {
                if (!productMixBusinessService.canProductsCoExist(newloan.getLoanOffering(), loan.getLoanOffering())) {
                    handleConflict(newloan, loan);
                }
            }
        }
    }

    void handleConflict(LoanBO newloan, LoanBO loan) throws AccountException {
        List<String> params = new ArrayList(Arrays.asList(loan.getLoanOffering().getPrdOfferingName(), newloan
                .getLoanOffering().getPrdOfferingName()));
        if (areCustomerLevelsDifferent(newloan, loan)) {
            params.add(loan.getCustomer().getDisplayName());
            throw new AccountException(LoanExceptionConstants.LOANS_CANNOT_COEXIST_ACROSS_CUSTOMER_LEVELS, params
                    .toArray());
        } else {
            throw new AccountException(LoanExceptionConstants.LOANS_CANNOT_COEXIST, params.toArray());
        }
    }

    private boolean areCustomerLevelsDifferent(LoanBO newloan, LoanBO loan) {
        return newloan.getCustomer().getLevel() != loan.getCustomer().getLevel();
    }

    private LoanBusinessService loanBusinessService;
    private ConfigurationBusinessService configService;
    private AccountBusinessService accountBusinessService;
    private ProductMixBusinessService productMixBusinessService;
}
