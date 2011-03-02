/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.business.service.validators.InstallmentValidationContext;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.Localization;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.validations.Errors;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanServiceFacadeWebTier implements LoanServiceFacade {

    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final FundDao fundDao;
    private final LoanDao loanDao;
    private final InstallmentsValidator installmentsValidator;
    private final ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;
    private final LoanBusinessService loanBusinessService;
    private final HolidayServiceFacade holidayServiceFacade;
    private final LoanPrdBusinessService loanPrdBusinessService;

    @Autowired
    public LoanServiceFacadeWebTier(final LoanProductDao loanProductDao, final CustomerDao customerDao,
                                    PersonnelDao personnelDao, FundDao fundDao, final LoanDao loanDao,
                                    InstallmentsValidator installmentsValidator,
                                    ScheduleCalculatorAdaptor scheduleCalculatorAdaptor, LoanBusinessService loanBusinessService,
                                    HolidayServiceFacade holidayServiceFacade, LoanPrdBusinessService loanPrdBusinessService) {
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.fundDao = fundDao;
        this.loanDao = loanDao;
        this.installmentsValidator = installmentsValidator;
        this.scheduleCalculatorAdaptor = scheduleCalculatorAdaptor;
        this.loanBusinessService = loanBusinessService;
        this.holidayServiceFacade = holidayServiceFacade;
        this.loanPrdBusinessService = loanPrdBusinessService;
    }

    private CustomerBO loadCustomer(Integer customerId) {
        return customerDao.findCustomerById(customerId);
    }

    private MeetingBO createNewMeetingForRepaymentDay(DateTime disbursementDate,
            final LoanAccountActionForm loanAccountActionForm, final CustomerBO customer) throws NumberFormatException,
            MeetingException {

        MeetingBO newMeetingForRepaymentDay = null;
        Short recurrenceId = Short.valueOf(loanAccountActionForm.getRecurrenceId());

        final int minDaysInterval = new ConfigurationPersistence().getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final Date repaymentStartDate = disbursementDate.plusDays(minDaysInterval).toDate();

        if (RecurrenceType.WEEKLY.getValue().equals(recurrenceId)) {
            newMeetingForRepaymentDay = new MeetingBO(WeekDay.getWeekDay(Short.valueOf(loanAccountActionForm
                    .getWeekDay())), Short.valueOf(loanAccountActionForm.getRecurWeek()), repaymentStartDate,
                    MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
        } else if (RecurrenceType.MONTHLY.getValue().equals(recurrenceId)) {
            if (loanAccountActionForm.getMonthType().equals("1")) {
                newMeetingForRepaymentDay = new MeetingBO(Short.valueOf(loanAccountActionForm.getMonthDay()), Short
                        .valueOf(loanAccountActionForm.getDayRecurMonth()), repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
            } else {
                newMeetingForRepaymentDay = new MeetingBO(Short.valueOf(loanAccountActionForm.getMonthWeek()), Short
                        .valueOf(loanAccountActionForm.getRecurMonth()), repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace(),
                        Short.valueOf(loanAccountActionForm.getMonthRank()));
            }
        }
        return newMeetingForRepaymentDay;
    }

    @Override
	public Errors validateInputInstallments(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails,
                                            List<RepaymentScheduleInstallment> installments, Integer customerId) {
        Short officeId = loadCustomer(customerId).getOfficeId();
        InstallmentValidationContext context = new InstallmentValidationContext(disbursementDate, variableInstallmentDetails, holidayServiceFacade, officeId);
        return installmentsValidator.validateInputInstallments(installments, context);
    }

    @Override
    public Errors validateInstallmentSchedule(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO) {
        return installmentsValidator.validateInstallmentSchedule(installments, variableInstallmentDetailsBO);
    }

    @Override
    public Errors validateCashFlowForInstallments(LoanInstallmentsDto loanInstallmentsDto, 
            List<MonthlyCashFlowDto> monthlyCashFlows, Double repaymentCapacity, BigDecimal cashFlowTotalBalance) {

        Errors errors = new Errors();
        if (CollectionUtils.isNotEmpty(monthlyCashFlows)) {
            
            boolean lowerBound = DateUtils.firstLessOrEqualSecond(monthlyCashFlows.get(0).getMonthDate().toDate(), loanInstallmentsDto.getFirstInstallmentDueDate());
            boolean upperBound = DateUtils.firstLessOrEqualSecond(loanInstallmentsDto.getLastInstallmentDueDate(), monthlyCashFlows.get(monthlyCashFlows.size() - 1).getMonthDate().toDate());

            Locale locale = Localization.getInstance().getConfiguredLocale();
            SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", locale);

            if (!lowerBound) {
                errors.addError(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE, new String[]{df.format(loanInstallmentsDto.getFirstInstallmentDueDate())});
            }

            if (!upperBound) {
                errors.addError(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE, new String[]{df.format(loanInstallmentsDto.getLastInstallmentDueDate())});
            }
        }
        validateForRepaymentCapacity(loanInstallmentsDto.getTotalInstallmentAmount(), loanInstallmentsDto.getLoanAmount(), repaymentCapacity, errors, cashFlowTotalBalance);
        return errors;
    }
    
    private void validateForRepaymentCapacity(BigDecimal totalInstallmentAmount, BigDecimal loanAmount, Double repaymentCapacity, Errors errors, BigDecimal totalBalance) {
        if (repaymentCapacity == null || repaymentCapacity == 0) {
            return;
        }
        Double calculatedRepaymentCapacity = totalBalance.add(loanAmount).multiply(CashFlowConstants.HUNDRED).divide(totalInstallmentAmount, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (calculatedRepaymentCapacity < repaymentCapacity) {
            errors.addError(AccountConstants.REPAYMENT_CAPACITY_LESS_THAN_ALLOWED, new String[]{calculatedRepaymentCapacity.toString(), repaymentCapacity.toString()});
        }
    }

    @Override
    public OriginalScheduleInfoDto retrieveOriginalLoanSchedule(Integer accountId, Locale locale) throws PersistenceException {
        List<OriginalLoanScheduleEntity> loanScheduleEntities = loanBusinessService.retrieveOriginalLoanSchedule(accountId);
        ArrayList<RepaymentScheduleInstallment> repaymentScheduleInstallments = new ArrayList<RepaymentScheduleInstallment>();
        for (OriginalLoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
              repaymentScheduleInstallments.add(loanScheduleEntity.toDto(locale));
        }

        LoanBO loan = this.loanDao.findById(accountId);
        return new OriginalScheduleInfoDto(loan.getLoanAmount().toString(),loan.getDisbursementDate(),repaymentScheduleInstallments);
    }
}