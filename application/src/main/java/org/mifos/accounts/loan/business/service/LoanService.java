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

package org.mifos.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fund.business.FundBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.framework.business.service.Service;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.FormUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * LoanService encapsulates high level operations on loans including loan
 * creation and retrieval. No domain/business objects should be returned from
 * this class, only Data Transfer Objects (DTOs) or primitives.
 * 
 */
public class LoanService implements Service {
    private static final FundBO NO_FUND = null;

    LoanProductService loanProductService;
    LoanDao loanDao;

    public LoanProductService getLoanProductService() {
        return this.loanProductService;
    }

    public void setLoanProductService(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    public LoanDao getLoanDao() {
        return this.loanDao;
    }

    public void setLoanDao(LoanDao loanDao) {
        this.loanDao = loanDao;
    }

    public LoanService() {
        // for use with setter dependency injection
    }

    public LoanService(LoanProductService loanProductService, LoanDao loanDao) {
        this.loanProductService = loanProductService;
        this.loanDao = loanDao;
    }

    public LoanDto createLoan(UserContext userContext, Integer centerId, Short loanProductId, Integer clientId,
            AccountState accountState, String loanAmount, Short defaultNumberOfInstallments, Double maxLoanAmount,
            Double minLoanAmount, Short maxInstallments, Short minInstallments, Integer businessActivityId)
            throws ApplicationException {

        CustomerBO center = new CustomerBusinessService().getCustomer(centerId);
        checkPermissionForCreate(accountState.getValue(), userContext, null, center.getOffice().getOfficeId(), center
                .getPersonnel().getPersonnelId());
        LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(loanProductId);

        List<FeeView> additionalFees = new ArrayList<FeeView>();
        List<FeeView> defaultFees = new ArrayList<FeeView>();
        loanProductService.getDefaultAndAdditionalFees(loanProductId, userContext, defaultFees, additionalFees);

        CustomerBO client = new CustomerBusinessService().getCustomer(clientId);
        LoanBO loan = loanDao.createLoan(userContext, loanOffering, client, accountState, 
                new Money(loanOffering.getCurrency(), loanAmount),
                defaultNumberOfInstallments, center.getCustomerAccount().getNextMeetingDate(), loanOffering
                        .isIntDedDisbursement(), loanOffering.getDefInterestRate(), loanOffering
                        .getGracePeriodDuration(), NO_FUND, defaultFees, null, maxLoanAmount, minLoanAmount,
                maxInstallments, minInstallments, false);
        loan.setBusinessActivityId(businessActivityId);
        loan.save();

        return new LoanDto(loan);
    }

    protected void checkPermissionForCreate(Short newState, UserContext userContext, Short flagSelected,
            Short officeId, Short loanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(newState, userContext, officeId, loanOfficerId, true))
            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
    }

    private boolean isPermissionAllowed(Short newSate, UserContext userContext, Short officeId, Short loanOfficerId,
            boolean saveFlag) {
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId,
                        loanOfficerId));
    }

}
