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

package org.mifos.accounts.loan.business.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.calendar.WorkingDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.framework.business.service.Service;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

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
    HolidayDao holidayDao;

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

        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        List<FeeDto> defaultFees = new ArrayList<FeeDto>();
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
        if (!isPermissionAllowed(newState, userContext, officeId, loanOfficerId, true)) {
            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(Short newState, UserContext userContext, Short officeId, Short loanOfficerId,
            boolean saveFlag) {
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newState), officeId,
                        loanOfficerId));
    }


    public void validateDisbursementDateForNewLoan (short officeId, final DateTime disbursementDate)
                    throws ApplicationException {

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        validateDisbursementDateIsWorkingDay(disbursementDate, workingDays);

        HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(officeId);
        validateDisbursementDateIsNotInHoliday(disbursementDate, holidays);
    }

    public void validateDisbursementDateIsWorkingDay(DateTime disbursementDate, List<Days> workingDays) throws ApplicationException {
        if (WorkingDay.isNotWorkingDay(disbursementDate, workingDays)) {
            throw new ApplicationException(LoanExceptionConstants.DISBURSEMENTDATE_MUST_BE_A_WORKING_DAY);
        }
    }

    void validateDisbursementDateIsNotInHoliday (DateTime disbursementDate, List<Holiday> holidays) throws ApplicationException{
        for (Holiday holiday : holidays) {
            if (holiday.encloses(disbursementDate)) {
                throw new ApplicationException(LoanExceptionConstants.DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY);
            }
        }
    }
}
