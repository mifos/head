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
package org.mifos.application.servicefacade;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MAX_RANGE_IS_NOT_MET;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_RANGE_IS_NOT_MET;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.InstallmentDetailsDto;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanActivityDto;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.AccountFeesDto;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.LoanInformationDto;
import org.mifos.accounts.loan.business.service.LoanPerformanceHistoryDto;
import org.mifos.accounts.loan.business.service.LoanScheduleGenerationDto;
import org.mifos.accounts.loan.business.service.LoanService;
import org.mifos.accounts.loan.business.service.LoanSummaryDto;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.business.service.validators.InstallmentValidationContext;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.LoanInstallmentDetailsDto;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.CashFlowDataDto;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.CashFlowDetail;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.validations.Errors;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implementation of {@link LoanServiceFacade} for web application usage.
 */
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

    @Override
    public LoanCreationLoanScheduleDetailsDto retrieveScheduleDetailsForRedoLoan(Integer customerId,
            DateTime disbursementDate, Short fundId, LoanAccountActionForm loanActionForm) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
            LocalizationConverter localizationConverter = new LocalizationConverter();
            CustomerBO customer = this.customerDao.findCustomerById(customerId);

            new LoanService().validateDisbursementDateForNewLoan(customer.getOfficeId(), disbursementDate);

            boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            MeetingBO newMeetingForRepaymentDay = null;
            if (isRepaymentIndependentOfMeetingEnabled) {
                newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(disbursementDate, loanActionForm, customer);
            }

            Short productId = loanActionForm.getPrdOfferingIdValue();
            LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

            Money loanAmount = new Money(loanOffering.getCurrency(), loanActionForm.getLoanAmount());
            Short numOfInstallments = loanActionForm.getNoOfInstallmentsValue();
            boolean isInterestDeductedAtDisbursement = loanActionForm.isInterestDedAtDisbValue();
            Double interest = loanActionForm.getInterestDoubleValue();
            Short gracePeriod = loanActionForm.getGracePeriodDurationValue();
            List<FeeDto> fees = loanActionForm.getFeesToApply();

            Double maxLoanAmount = loanActionForm.getMaxLoanAmountValue();
            Double minLoanAmount = loanActionForm.getMinLoanAmountValue();
            Short maxNumOfInstallments = loanActionForm.getMaxNoInstallmentsValue();
            Short minNumOfShortInstallments = loanActionForm.getMinNoInstallmentsValue();
            String externalId = loanActionForm.getExternalId();
            AccountState accountState = loanActionForm.getState();
            if (accountState == null) {
                accountState = AccountState.LOAN_PARTIAL_APPLICATION;
            }

            FundBO fund = null;
            if (fundId != null) {
                fund = this.fundDao.findById(fundId);
            }
            LoanBO loan = LoanBO.redoLoan(userContext, loanOffering, customer, accountState, loanAmount,
                    numOfInstallments, disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest,
                    gracePeriod, fund, fees, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                    minNumOfShortInstallments, isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay);
            loan.setExternalId(externalId);


            List<RepaymentScheduleInstallment> installments = loan.toRepaymentScheduleDto(userContext.getPreferredLocale());

            List<PaymentDataHtmlBean> paymentDataBeans = new ArrayList<PaymentDataHtmlBean>(installments.size());
            PersonnelBO personnel = this.personnelDao.findPersonnelById(userContext.getId());
            if (personnel == null) {
                throw new IllegalArgumentException("bad UserContext id");
            }

            for (RepaymentScheduleInstallment repaymentScheduleInstallment : installments) {
                paymentDataBeans.add(new PaymentDataHtmlBean(userContext.getPreferredLocale(), personnel,
                        repaymentScheduleInstallment));
            }

            final boolean isGlimApplicable = customer.isGroup() && configurationPersistence.isGlimEnabled();
            double glimLoanAmount = computeGLIMLoanAmount(loanActionForm, localizationConverter);
            boolean isLoanPendingApprovalDefined = ProcessFlowRules.isLoanPendingApprovalStateEnabled();

            if (isRepaymentIndependentOfMeetingEnabled) {
                Date firstRepaymentDate = installments.get(0).getDueDateValue();

                validateFirstRepaymentDate(disbursementDate, configurationPersistence, firstRepaymentDate);
            }

            return new LoanCreationLoanScheduleDetailsDto(customer.isGroup(), isGlimApplicable, glimLoanAmount,
                    isLoanPendingApprovalDefined, installments, paymentDataBeans);

        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }


    @Override
    public LoanCreationLoanScheduleDetailsDto retrieveScheduleDetailsForLoanCreation(Integer customerId, DateTime disbursementDate,
                                                                                    Short fundId, LoanAccountActionForm loanActionForm) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        try {
            ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
            LocalizationConverter localizationConverter = new LocalizationConverter();
            CustomerBO customer = loadCustomer(customerId);

            if (loanActionForm.isDefaultFeeRemoved()) {
                customerDao.checkPermissionForDefaultFeeRemovalFromLoan(userContext, customer);
            }

            new LoanService().validateDisbursementDateForNewLoan(customer.getOfficeId(), disbursementDate);

            boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            MeetingBO newMeetingForRepaymentDay = null;
            if (isRepaymentIndependentOfMeetingEnabled) {
                newMeetingForRepaymentDay = this
                        .createNewMeetingForRepaymentDay(disbursementDate, loanActionForm, customer);
            }

            FundBO fund = null;
            if (fundId != null) {
                fund = this.fundDao.findById(fundId);
            }
            LoanBO loan = assembleLoan(userContext, customer, disbursementDate, fund,
                    isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay, loanActionForm);
            List<RepaymentScheduleInstallment> installments = loanBusinessService.applyDailyInterestRatesWhereApplicable(
                    new LoanScheduleGenerationDto(disbursementDate.toDate(),
                            loan, loanActionForm.isVariableInstallmentsAllowed(), loanActionForm.getLoanAmountValue(),
                            loanActionForm.getInterestDoubleValue()), userContext.getPreferredLocale());

            if (isRepaymentIndependentOfMeetingEnabled) {
                Date firstRepaymentDate = installments.get(0).getDueDateValue();
                validateFirstRepaymentDate(disbursementDate, configurationPersistence, firstRepaymentDate);
            }

            double glimLoanAmount = computeGLIMLoanAmount(loanActionForm, localizationConverter);

            boolean isLoanPendingApprovalDefined = ProcessFlowRules.isLoanPendingApprovalStateEnabled();

            final boolean isGlimApplicable = customer.isGroup() && configurationPersistence.isGlimEnabled();
            return new LoanCreationLoanScheduleDetailsDto(customer.isGroup(), isGlimApplicable, glimLoanAmount,
                    isLoanPendingApprovalDefined, installments, new ArrayList<PaymentDataHtmlBean>());
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }


    private double computeGLIMLoanAmount(LoanAccountActionForm loanActionForm, LocalizationConverter localizationConverter) {
        double glimLoanAmount = Double.valueOf("0");
        List<LoanAccountDetailsDto> loanAccountDetails = loanActionForm.getClientDetails();
        List<String> clientNames = loanActionForm.getClients();
        for (LoanAccountDetailsDto loanAccount : loanAccountDetails) {
            if (clientNames.contains(loanAccount.getClientId())) {
                if (loanAccount.getLoanAmount() != null) {
                    glimLoanAmount = glimLoanAmount
                            + localizationConverter.getDoubleValueForCurrentLocale(loanAccount.getLoanAmount());
                }
            }
        }
        return glimLoanAmount;
    }

    private void validateFirstRepaymentDate(DateTime disbursementDate, ConfigurationPersistence configurationPersistence, Date firstRepaymentDate) throws AccountException {
        Integer minDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();
        Integer maxDaysInterval = configurationPersistence.getConfigurationKeyValueInteger(
                MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils.getDateWithoutTimeStamp(firstRepaymentDate),
                DateUtils.getDateWithoutTimeStamp(disbursementDate.toDate())) < minDaysInterval) {
            throw new AccountException(MIN_RANGE_IS_NOT_MET, new String[] { minDaysInterval.toString() });
        } else if (DateUtils.getNumberOfDaysBetweenTwoDates(DateUtils.getDateWithoutTimeStamp(firstRepaymentDate),
                DateUtils.getDateWithoutTimeStamp(disbursementDate.toDate())) > maxDaysInterval) {
            throw new AccountException(MAX_RANGE_IS_NOT_MET, new String[] { maxDaysInterval.toString() });
        }
    }

    private LoanBO assembleLoan(UserContext userContext, CustomerBO customer, DateTime disbursementDate, FundBO fund,
            boolean isRepaymentIndependentOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay,
            LoanAccountActionForm loanActionForm) throws ApplicationException {

        Short productId = loanActionForm.getPrdOfferingIdValue();
        LoanOfferingBO loanOffering = new LoanPrdBusinessService()
                .getLoanOffering(productId, userContext.getLocaleId());

        Money loanAmount = new Money(loanOffering.getCurrency(), loanActionForm.getLoanAmount());
        Short numOfInstallments = loanActionForm.getNoOfInstallmentsValue();
        boolean isInterestDeductedAtDisbursement = loanActionForm.isInterestDedAtDisbValue();
        Double interest = loanActionForm.getInterestDoubleValue();
        Short gracePeriod = loanActionForm.getGracePeriodDurationValue();
        List<FeeDto> fees = loanActionForm.getFeesToApply();
        List<CustomFieldDto> customFields = loanActionForm.getCustomFields();
        Double maxLoanAmount = loanActionForm.getMaxLoanAmountValue();
        Double minLoanAmount = loanActionForm.getMinLoanAmountValue();
        Short maxNumOfInstallments = loanActionForm.getMaxNoInstallmentsValue();
        Short minNumOfShortInstallments = loanActionForm.getMinNoInstallmentsValue();
        String externalId = loanActionForm.getExternalId();
        Integer selectedLoanPurpose = loanActionForm.getBusinessActivityIdValue();
        String collateralNote = loanActionForm.getCollateralNote();
        Integer selectedCollateralType = loanActionForm.getCollateralTypeIdValue();
        AccountState accountState = loanActionForm.getState();
        if (accountState == null) {
            accountState = AccountState.LOAN_PARTIAL_APPLICATION;
        }

        LoanBO loan = LoanBO.createLoan(userContext, loanOffering, customer, accountState, loanAmount,
                numOfInstallments, disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod,
                fund, fees, customFields, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                minNumOfShortInstallments, isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay);

        loan.setExternalId(externalId);
        loan.setBusinessActivityId(selectedLoanPurpose);
        loan.setCollateralNote(collateralNote);
        loan.setCollateralTypeId(selectedCollateralType);

        return loan;
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
    public LoanBO previewLoanRedoDetails(Integer customerId, LoanAccountActionForm loanAccountActionForm, DateTime disbursementDate) {
        CustomerBO customer = loadCustomer(customerId);
        return redoLoan(customer, loanAccountActionForm, disbursementDate);
    }

    private LoanBO redoLoan(CustomerBO customer, LoanAccountActionForm loanAccountActionForm, DateTime disbursementDate) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        try {
            boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            MeetingBO newMeetingForRepaymentDay = null;
            if (isRepaymentIndepOfMeetingEnabled) {
                newMeetingForRepaymentDay = createNewMeetingForRepaymentDay(disbursementDate, loanAccountActionForm, customer);
            }

            Short productId = loanAccountActionForm.getPrdOfferingIdValue();

            LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

            Money loanAmount = new Money(loanOffering.getCurrency(), loanAccountActionForm.getLoanAmount());
            Short numOfInstallments = loanAccountActionForm.getNoOfInstallmentsValue();
            boolean isInterestDeductedAtDisbursement = loanAccountActionForm.isInterestDedAtDisbValue();
            Double interest = loanAccountActionForm.getInterestDoubleValue();
            Short gracePeriod = loanAccountActionForm.getGracePeriodDurationValue();
            List<FeeDto> fees = loanAccountActionForm.getFeesToApply();

            Double maxLoanAmount = loanAccountActionForm.getMaxLoanAmountValue();
            Double minLoanAmount = loanAccountActionForm.getMinLoanAmountValue();
            Short maxNumOfInstallments = loanAccountActionForm.getMaxNoInstallmentsValue();
            Short minNumOfShortInstallments = loanAccountActionForm.getMinNoInstallmentsValue();
            String externalId = loanAccountActionForm.getExternalId();
            Integer selectedLoanPurpose = loanAccountActionForm.getBusinessActivityIdValue();
            String collateralNote = loanAccountActionForm.getCollateralNote();
            Integer selectedCollateralType = loanAccountActionForm.getCollateralTypeIdValue();
            AccountState accountState = loanAccountActionForm.getState();
            if (accountState == null) {
                accountState = AccountState.LOAN_PARTIAL_APPLICATION;
            }

            FundBO fund = null;
            Short fundId = loanAccountActionForm.getLoanOfferingFundValue();
            if (fundId != null) {
                fund = this.fundDao.findById(fundId);
            }

            LoanBO redoLoan = LoanBO.redoLoan(userContext, loanOffering, customer, accountState, loanAmount,
                    numOfInstallments, disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod,
                    fund, fees, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                    minNumOfShortInstallments, isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
            redoLoan.setExternalId(externalId);
            redoLoan.setBusinessActivityId(selectedLoanPurpose);
            redoLoan.setCollateralNote(collateralNote);
            redoLoan.setCollateralTypeId(selectedCollateralType);

            PersonnelBO user = personnelDao.findPersonnelById(userContext.getId());

            redoLoan.changeStatus(AccountState.LOAN_APPROVED, null, "Automatic Status Update (Redo Loan)", user);

            // We're assuming cash disbursal for this situation right now
            redoLoan.disburseLoan(user, PaymentTypes.CASH.getValue(), false);

            List<PaymentDataHtmlBean> paymentDataBeans = loanAccountActionForm.getPaymentDataBeans();
            PaymentData payment;

            for (PaymentDataTemplate template : paymentDataBeans) {
                if (template.hasValidAmount() && template.getTransactionDate() != null) {
                    if (!customer.getCustomerMeeting().getMeeting().isValidMeetingDate(template.getTransactionDate(),
                            DateUtils.getLastDayOfNextYear())) {
                        throw new BusinessRuleException("errors.invalidTxndate");
                    }
                    payment = PaymentData.createPaymentData(template);
                    redoLoan.applyPayment(payment);
                }
            }
            return redoLoan;
        } catch (InvalidDateException ide) {
                throw new BusinessRuleException(ide.getMessage());
        } catch (MeetingException e) {
                throw new MifosRuntimeException(e);
        } catch (ServiceException e2) {
            throw new MifosRuntimeException(e2);
        } catch (PersistenceException e1) {
            throw new MifosRuntimeException(e1);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public List<LoanActivityDto> retrieveAllLoanAccountActivities(String globalAccountNum) {

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        List<LoanActivityEntity> loanAccountActivityDetails = loan.getLoanActivityDetails();
        List<LoanActivityDto> loanActivityViewSet = new ArrayList<LoanActivityDto>();
        for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
            loanActivityViewSet.add(loanActivity.toDto());
        }

        return loanActivityViewSet;
    }

    @Override
    public LoanInstallmentDetailsDto retrieveInstallmentDetails(Integer accountId) {

        LoanBO loanBO = this.loanDao.findById(accountId);

        InstallmentDetailsDto viewUpcomingInstallmentDetails = getUpcomingInstallmentDetails(loanBO
                .getDetailsOfNextInstallment(), loanBO.getCurrency());
        InstallmentDetailsDto viewOverDueInstallmentDetails = getOverDueInstallmentDetails(loanBO
                .getDetailsOfInstallmentsInArrears(), loanBO.getCurrency());
        Money totalAmountDue = viewUpcomingInstallmentDetails.getSubTotal().add(
                viewOverDueInstallmentDetails.getSubTotal());

        return new LoanInstallmentDetailsDto(viewUpcomingInstallmentDetails, viewOverDueInstallmentDetails,
                totalAmountDue, loanBO.getNextMeetingDate());
    }

    private InstallmentDetailsDto getUpcomingInstallmentDetails(
            final AccountActionDateEntity upcomingAccountActionDate, final MifosCurrency currency) {
        if (upcomingAccountActionDate != null) {
            LoanScheduleEntity upcomingInstallment = (LoanScheduleEntity) upcomingAccountActionDate;
            return new InstallmentDetailsDto(upcomingInstallment.getPrincipalDue(), upcomingInstallment
                    .getInterestDue(), upcomingInstallment.getTotalFeeDueWithMiscFeeDue(), upcomingInstallment
                    .getPenaltyDue());
        }
        return new InstallmentDetailsDto(new Money(currency), new Money(currency), new Money(currency), new Money(
                currency));
    }

    private InstallmentDetailsDto getOverDueInstallmentDetails(
            final List<AccountActionDateEntity> overDueInstallmentList, final MifosCurrency currency) {
        Money principalDue = new Money(currency);
        Money interestDue = new Money(currency);
        Money feesDue = new Money(currency);
        Money penaltyDue = new Money(currency);
        for (AccountActionDateEntity accountActionDate : overDueInstallmentList) {
            LoanScheduleEntity installment = (LoanScheduleEntity) accountActionDate;
            principalDue = principalDue.add(installment.getPrincipalDue());
            interestDue = interestDue.add(installment.getInterestDue());
            feesDue = feesDue.add(installment.getTotalFeeDueWithMiscFeeDue());
            penaltyDue = penaltyDue.add(installment.getPenaltyDue());
        }
        return new InstallmentDetailsDto(principalDue, interestDue, feesDue, penaltyDue);
    }

    @Override
    public boolean isTrxnDateValid(Integer loanAccountId, Date trxnDate) throws ApplicationException {

        LoanBO loan = this.loanDao.findById(loanAccountId);

        Date meetingDate = new CustomerPersistence().getLastMeetingDateForCustomer(loan.getCustomer().getCustomerId());
        boolean repaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
        return loan.isTrxnDateValid(trxnDate, meetingDate, repaymentIndependentOfMeetingEnabled);
    }

    @Override
    public LoanBO retrieveLoanRepaymentSchedule(UserContext userContext, Integer loanId, Date asOfDate) {
        LoanBO loan = this.loanDao.findById(loanId);
        scheduleCalculatorAdaptor.computeExtraInterest(loan, asOfDate);
        loan.updateDetails(userContext);
        return loan;
    }

    @Override
    public List<AccountStatusChangeHistoryEntity> retrieveLoanAccountStatusChangeHistory(UserContext userContext,
            String globalAccountNum) {

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        loan.updateDetails(userContext);
        return new ArrayList<AccountStatusChangeHistoryEntity>(loan.getAccountStatusChangeHistory());
    }

    @Override
    public void makeEarlyRepayment(String globalAccountNum, String earlyRepayAmountStr, String receiptNumber,
                                   java.sql.Date receiptDate, String paymentTypeId, Short userId, boolean waiveOffInterest) throws AccountException {

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        if (waiveOffInterest && !loan.isInterestWaived()) {
            throw new AccountException(LoanConstants.WAIVER_INTEREST_NOT_CONFIGURED);
        }
        Money earlyRepayAmount = new Money(loan.getCurrency(), earlyRepayAmountStr);
        loan.makeEarlyRepayment(earlyRepayAmount, receiptNumber, receiptDate, paymentTypeId, userId, waiveOffInterest);
    }

    public LoanInformationDto getLoanInformationDto(String globalAccountNum, UserContext userContext) throws ServiceException {
        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        String fundName = null;
        if (loan.getFund() != null) {
            fundName = loan.getFund().getFundName();
        }

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        boolean activeSurveys = surveysPersistence.isActiveSurveysForSurveyType(SurveyType.LOAN);
        List<SurveyDto> accountSurveys = loanDao.getAccountSurveyDto(loan.getAccountId());

        LoanSummaryDto loanSummary = new LoanSummaryDto(loan.getLoanSummary().getOriginalPrincipal(), loan.getLoanSummary().getPrincipalPaid(),
                                                        loan.getLoanSummary().getPrincipalDue(), loan.getLoanSummary().getOriginalInterest(),
                                                        loan.getLoanSummary().getInterestPaid(), loan.getLoanSummary().getInterestDue(),
                                                        loan.getLoanSummary().getOriginalFees(), loan.getLoanSummary().getFeesPaid(),
                                                        loan.getLoanSummary().getFeesDue(), loan.getLoanSummary().getOriginalPenalty(),
                                                        loan.getLoanSummary().getPenaltyPaid(), loan.getLoanSummary().getPenaltyDue(),
                                                        loan.getLoanSummary().getTotalLoanAmnt(), loan.getLoanSummary().getTotalAmntPaid(),
                                                        loan.getLoanSummary().getTotalAmntDue());

        LoanPerformanceHistoryDto loanPerformanceHistory = new LoanPerformanceHistoryDto(loan.getPerformanceHistory().getNoOfPayments(),
                                                                                        loan.getPerformanceHistory().getTotalNoOfMissedPayments(),
                                                                                        loan.getPerformanceHistory().getDaysInArrears(),
                                                                                        loan.getPerformanceHistory().getLoanMaturityDate());

        Set<AccountFeesDto> accountFeesDtos = new HashSet<AccountFeesDto>();
        if(!loan.getAccountFees().isEmpty()) {
            for (AccountFeesEntity accountFeesEntity: loan.getAccountFees()) {
                AccountFeesDto accountFeesDto = new AccountFeesDto(accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId(),
                                                                  accountFeesEntity.getFeeStatus(), accountFeesEntity.getFees().getFeeName(),
                                                                  accountFeesEntity.getAccountFeeAmount(),
                                                                  getMeetingRecurrence(accountFeesEntity.getFees().getFeeFrequency()
                                                                          .getFeeMeetingFrequency(), userContext),
                                                                  accountFeesEntity.getFees().getFeeId());
                accountFeesDtos.add(accountFeesDto);
            }
        }

        Set<String> accountFlagNames = getAccountStateFlagEntityNames(loan.getAccountFlags());
        Short accountStateId = loan.getAccountState().getId();
        String accountStateName = getAccountStateName(accountStateId);
        String gracePeriodTypeName = getGracePeriodTypeName(loan.getGracePeriodType().getId());
        String interestTypeName = getInterestTypeName(loan.getInterestType().getId());

        return new LoanInformationDto(loan.getLoanOffering().getPrdOfferingName(), globalAccountNum, accountStateId,
                                     accountStateName, accountFlagNames, loan.getDisbursementDate(), loan.isRedone(),
                                     loan.getBusinessActivityId(), loan.getAccountId(),gracePeriodTypeName, interestTypeName,
                                     loan.getRecentAccountNotes(),loan.getCustomer().getCustomerId(), loan.getAccountType().getAccountTypeId(),
                                     loan.getOffice().getOfficeId(), loan.getPersonnel().getPersonnelId(), loan.getNextMeetingDate(),
                                     loan.getTotalAmountDue(), loan.getTotalAmountInArrears(), loanSummary,
                                     loan.getLoanActivityDetails().isEmpty()? false: true, loan.getInterestRate(),
                                     loan.isInterestDeductedAtDisbursement(),
                                     loan.getLoanOffering().getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurAfter(),
                                     loan.getLoanOffering().getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId(),
                                     loan.getLoanOffering().isPrinDueLastInst(), loan.getNoOfInstallments(),
                                     loan.getMaxMinNoOfInstall().getMinNoOfInstall(), loan.getMaxMinNoOfInstall().getMaxNoOfInstall(),
                                     loan.getGracePeriodDuration(), fundName, loan.getCollateralTypeId(), loan.getCollateralNote(),loan.getExternalId(),
                                     loan.getAccountCustomFields(), accountFeesDtos, loan.getCreatedDate(), loanPerformanceHistory,
                                     loan.getCustomer().isGroup(), getRecentActivityView(globalAccountNum), activeSurveys, accountSurveys,
                                     loan.getCustomer().getDisplayName(), loan.getCustomer().getGlobalCustNum(), loan.getOffice().getOfficeName());
    }

    private String getMeetingRecurrence(MeetingBO meeting, UserContext userContext) {
        return meeting != null ? new MeetingHelper().getMessageWithFrequency(meeting, userContext) : null;
    }

    private String getAccountStateName(Short id) {

        MasterPersistence masterPersistence = new MasterPersistence();
        AccountStateEntity accountStateEntity;
        try {
            accountStateEntity = (AccountStateEntity) masterPersistence.getPersistentObject(AccountStateEntity.class,
                    id);
            return accountStateEntity.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private String getGracePeriodTypeName(Short id) {

        MasterPersistence masterPersistence = new MasterPersistence();
        GracePeriodTypeEntity gracePeriodType;
        try {
            gracePeriodType = (GracePeriodTypeEntity) masterPersistence.getPersistentObject(GracePeriodTypeEntity.class,
                    id);
            return gracePeriodType.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private String getInterestTypeName(Short id) {

        MasterPersistence masterPersistence = new MasterPersistence();
        InterestTypesEntity interestType;
        try {
            interestType = (InterestTypesEntity) masterPersistence.getPersistentObject(InterestTypesEntity.class,
                    id);
            return interestType.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private String getAccountStateFlagEntityName(Short id) {
        MasterPersistence masterPersistence = new MasterPersistence();
        AccountStateFlagEntity accountStateFlagEntity;
        try {
            accountStateFlagEntity = (AccountStateFlagEntity) masterPersistence.getPersistentObject(AccountStateFlagEntity.class,
                    id);
            return accountStateFlagEntity.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private Set<String> getAccountStateFlagEntityNames(Set<AccountFlagMapping> accountFlagMappings) {
        Set<String> accountFlagNames = new HashSet<String>();
        if(!accountFlagMappings.isEmpty()) {
            for (AccountFlagMapping accountFlagMapping: accountFlagMappings) {
                String accountFlagName = getAccountStateFlagEntityName(accountFlagMapping.getFlag().getId());
                accountFlagNames.add(accountFlagName);
            }
        }
        return accountFlagNames;
    }

    private List<LoanActivityDto> getRecentActivityView(final String globalAccountNumber) throws ServiceException {
        LoanBO loanBO = loanDao.findByGlobalAccountNum(globalAccountNumber);
        List<LoanActivityEntity> loanAccountActivityDetails = loanBO.getLoanActivityDetails();
        List<LoanActivityDto> recentActivityView = new ArrayList<LoanActivityDto>();

        int count = 0;
        for (LoanActivityEntity loanActivity : loanAccountActivityDetails) {
            recentActivityView.add(getLoanActivityView(loanActivity));
            if (++count == 3) {
                break;
            }
        }
        return recentActivityView;
    }

    private LoanActivityDto getLoanActivityView(final LoanActivityEntity loanActivity) {
        LoanActivityDto loanActivityDto = new LoanActivityDto(loanActivity.getAccount().getCurrency());
        loanActivityDto.setId(loanActivity.getAccount().getAccountId());
        loanActivityDto.setActionDate(loanActivity.getTrxnCreatedDate());
        loanActivityDto.setActivity(loanActivity.getComments());
        loanActivityDto.setPrincipal(removeSign(loanActivity.getPrincipal()));
        loanActivityDto.setInterest(removeSign(loanActivity.getInterest()));
        loanActivityDto.setPenalty(removeSign(loanActivity.getPenalty()));
        loanActivityDto.setFees(removeSign(loanActivity.getFee()));
        loanActivityDto.setTotal(removeSign(loanActivity.getFee()).add(removeSign(loanActivity.getPenalty())).add(
                removeSign(loanActivity.getPrincipal())).add(removeSign(loanActivity.getInterest())));
        loanActivityDto.setTimeStamp(loanActivity.getTrxnCreatedDate());
        loanActivityDto.setRunningBalanceInterest(loanActivity.getInterestOutstanding());
        loanActivityDto.setRunningBalancePrinciple(loanActivity.getPrincipalOutstanding());
        loanActivityDto.setRunningBalanceFees(loanActivity.getFeeOutstanding());
        loanActivityDto.setRunningBalancePenalty(loanActivity.getPenaltyOutstanding());

        return loanActivityDto;
    }

    private Money removeSign(final Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }

        return amount;
    }

    @Override
    public List<LoanAccountDetailsDto> getLoanAccountDetailsViewList(LoanInformationDto loanInformationDto,
                                                                     List<BusinessActivityEntity> businessActEntity,
                                                                     ClientBusinessService clientBusinessService) throws ServiceException {
        List<LoanBO> individualLoans = loanBusinessService.findIndividualLoans(Integer.valueOf(
                loanInformationDto.getAccountId()).toString());

        List<LoanAccountDetailsDto> loanAccountDetailsViewList = new ArrayList<LoanAccountDetailsDto>();

        for (LoanBO individualLoan : individualLoans) {
            LoanAccountDetailsDto loandetails = new LoanAccountDetailsDto();
            loandetails.setClientId(individualLoan.getCustomer().getCustomerId().toString());
            loandetails.setClientName(individualLoan.getCustomer().getDisplayName());
            loandetails.setLoanAmount(null != individualLoan.getLoanAmount()
                    && !EMPTY.equals(individualLoan.getLoanAmount().toString()) ? individualLoan.getLoanAmount()
                    .toString() : "0.0");

            if (null != individualLoan.getBusinessActivityId()) {
                loandetails.setBusinessActivity(individualLoan.getBusinessActivityId().toString());
                for (ValueListElement busact : businessActEntity) {
                    if (busact.getId().toString().equals(individualLoan.getBusinessActivityId().toString())) {
                        loandetails.setBusinessActivityName(busact.getName());
                    }
                }
            }
            String governmentId = clientBusinessService.getClient(individualLoan.getCustomer().getCustomerId())
                    .getGovernmentId();
            loandetails.setGovermentId(StringUtils.isNotBlank(governmentId) ? governmentId : "-");
            loanAccountDetailsViewList.add(loandetails);
        }
        return loanAccountDetailsViewList;
    }

    @Override
    public RepayLoanDto getRepaymentDetails(String globalAccountNumber, Short localeId, AcceptedPaymentTypePersistence acceptedPaymentTypePersistence) throws PersistenceException {
        LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNumber);
        Money repaymentAmount = loan.getEarlyRepayAmount();
        Money waivedRepaymentAmount = repaymentAmount.subtract(loan.waiverAmount());
        return new RepayLoanDto(repaymentAmount, waivedRepaymentAmount,
                acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(localeId, TrxnTypes.loan_repayment.getValue()),
                loan.isInterestWaived());
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
    public Errors validateCashFlowForInstallmentsForWarnings(LoanAccountActionForm loanActionForm, Short localeId) throws ServiceException {
        Errors errors = new Errors();
        LoanOfferingBO loanOfferingBO = loanPrdBusinessService.getLoanOffering(loanActionForm.getPrdOfferingIdValue(), localeId);
        if (loanOfferingBO.shouldValidateCashFlowForInstallments()) {
            CashFlowDetail cashFlowDetail = loanOfferingBO.getCashFlowDetail();
            List<CashFlowDataDto> cashFlowDataDtos = loanActionForm.getCashflowDataDtos();
            if (CollectionUtils.isNotEmpty(cashFlowDataDtos)) {
                for (CashFlowDataDto cashflowDataDto : cashFlowDataDtos) {
                    validateCashFlow(errors, cashFlowDetail.getCashFlowThreshold(), cashflowDataDto);
                }
            }
        }
        return errors;
    }

    private void validateCashFlow(Errors errors, Double cashFlowThreshold, CashFlowDataDto cashflowDataDto) {
        String cashFlowAndInstallmentDiffPercent = cashflowDataDto.getDiffCumulativeCashflowAndInstallmentPercent();
        String monthYearAsString = cashflowDataDto.getMonthYearAsString();
        String cumulativeCashFlow = cashflowDataDto.getCumulativeCashFlow();
        if (StringUtils.isNotEmpty(cashFlowAndInstallmentDiffPercent) && Double.valueOf(cashFlowAndInstallmentDiffPercent) > cashFlowThreshold) {
            errors.addError(AccountConstants.BEYOND_CASHFLOW_THRESHOLD, new String[]{monthYearAsString, cashFlowThreshold.toString()});
        }
        if (StringUtils.isNotEmpty(cumulativeCashFlow)) {
            Double cumulativeCashFlowValue = Double.valueOf(cumulativeCashFlow);
            if (cumulativeCashFlowValue < 0) {
                errors.addError(AccountConstants.CUMULATIVE_CASHFLOW_NEGATIVE, new String[]{monthYearAsString});
            } else if (cumulativeCashFlowValue == 0) {
                errors.addError(AccountConstants.CUMULATIVE_CASHFLOW_ZERO, new String[]{monthYearAsString});
            }
        }
    }

    // TODO: Write unit tests for this method. Currently they only exist for repayment capacity related validations
    @Override
    public Errors validateCashFlowForInstallments(List<RepaymentScheduleInstallment> installments, CashFlowForm cashFlowForm, Double repaymentCapacity) {
        Errors errors = new Errors();
        if (cashFlowForm == null) {
            return errors;
        }
        List<MonthlyCashFlowForm> monthlyCashFlows = cashFlowForm.getMonthlyCashFlows();
        if (CollectionUtils.isNotEmpty(installments) && CollectionUtils.isNotEmpty(monthlyCashFlows)) {
            boolean lowerBound = DateUtils.firstLessOrEqualSecond(monthlyCashFlows.get(0).getDate(), installments.get(0).getDueDateValue());
            boolean upperBound = DateUtils.firstLessOrEqualSecond(installments.get(installments.size() - 1).getDueDateValue(), monthlyCashFlows.get(monthlyCashFlows.size() - 1).getDate());

            SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", installments.get(0).getLocale());

            if (!lowerBound) {
                errors.addError(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE, new String[]{df.format(installments.get(0).getDueDateValue())});
            }

            if (!upperBound) {
                errors.addError(AccountConstants.INSTALLMENT_BEYOND_CASHFLOW_DATE, new String[]{df.format(installments.get(installments.size() - 1).getDueDateValue())});
            }

        }
        validateForRepaymentCapacity(installments, cashFlowForm, repaymentCapacity, errors);
        return errors;
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

    private void validateForRepaymentCapacity(List<RepaymentScheduleInstallment> installments, CashFlowForm cashFlowForm, Double repaymentCapacity, Errors errors) {
        if (cashFlowForm == null || CollectionUtils.isEmpty(installments) || repaymentCapacity == null || repaymentCapacity == 0) {
            return;
        }
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        for (RepaymentScheduleInstallment installment : installments) {
            totalInstallmentAmount = totalInstallmentAmount.add(installment.getTotalValue().getAmount());
        }
        Double calculatedRepaymentCapacity = cashFlowForm.computeRepaymentCapacity(totalInstallmentAmount).doubleValue();
        if (calculatedRepaymentCapacity < repaymentCapacity) {
            errors.addError(AccountConstants.REPAYMENT_CAPACITY_LESS_THAN_ALLOWED, new String[]{calculatedRepaymentCapacity.toString(), repaymentCapacity.toString()});
        }
    }

}