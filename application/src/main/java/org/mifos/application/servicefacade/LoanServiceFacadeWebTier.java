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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.InstallmentDetailsDto;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanActivityDto;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.accounts.loan.struts.action.LoanInstallmentDetailsDto;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.LoanAccountDetailsDto;
import org.mifos.accounts.loan.util.helpers.LoanDisbursalDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.PrdOfferingDto;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.AccountingRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

/**
 * Implementation of {@link LoanServiceFacade} for web application usage.
 */
public class LoanServiceFacadeWebTier implements LoanServiceFacade {

    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final FundDao fundDao;
    private final LoanDao loanDao;

    public LoanServiceFacadeWebTier(final LoanProductDao loanProductDao, final CustomerDao customerDao,
            PersonnelDao personnelDao, FundDao fundDao, final LoanDao loanDao) {
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.fundDao = fundDao;
        this.loanDao = loanDao;
    }

    @Override
    public List<PrdOfferingDto> retrieveActiveLoanProductsApplicableForCustomer(final CustomerBO customer) {

        final List<LoanOfferingBO> applicableLoanProducts = new ArrayList<LoanOfferingBO>();

        final List<LoanOfferingBO> loanOfferings = loanProductDao
                .findActiveLoanProductsApplicableToCustomerLevel(customer.getCustomerLevel());

        final MeetingBO customerMeeting = customer.getCustomerMeetingValue();
        for (LoanOfferingBO loanProduct : loanOfferings) {
            if (MeetingBO.isMeetingMatched(customerMeeting, loanProduct.getLoanOfferingMeetingValue())) {
                applicableLoanProducts.add(loanProduct);
            }
        }

        List<PrdOfferingDto> applicationLoanProductDtos = new ArrayList<PrdOfferingDto>();
        for (LoanOfferingBO loanProduct : applicableLoanProducts) {
            applicationLoanProductDtos.add(loanProduct.toDto());
        }

        return applicationLoanProductDtos;
    }

    @Override
    public LoanCreationGlimDto retrieveGlimSpecificDataForGroup(final CustomerBO customer) {

        final List<ValueListElement> loanPurposes = loanProductDao.findAllLoanPurposes();

        final List<ClientBO> activeClientsOfGroup = customerDao.findActiveClientsUnderGroup(customer);

        return new LoanCreationGlimDto(loanPurposes, activeClientsOfGroup);
    }

    @Override
    public LoanCreationProductDetailsDto retrieveGetProductDetailsForLoanAccountCreation(final Integer customerId)
            throws ApplicationException {

        final CustomerBO customer = customerDao.findCustomerById(customerId);

        final CustomerDetailDto customerDetailDto = customer.toCustomerDetailDto();
        final Date nextMeetingDate = customer.getCustomerAccount().getNextMeetingDate();
        final String recurMonth = customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter()
                .toString();
        final boolean isGroup = customer.isGroup();
        final boolean isGlimEnabled = new ConfigurationPersistence().isGlimEnabled();

        final List<PrdOfferingDto> loanProductDtos = retrieveActiveLoanProductsApplicableForCustomer(customer);

        LoanCreationGlimDto loanCreationGlimDto = null;
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();

        if (isGroup && isGlimEnabled) {
            loanCreationGlimDto = retrieveGlimSpecificDataForGroup(customer);

            final List<ClientBO> activeClientsOfGroup = loanCreationGlimDto.getActiveClientsOfGroup();

            if (activeClientsOfGroup == null || activeClientsOfGroup.isEmpty()) {
                throw new ApplicationException(GroupConstants.IMPOSSIBLE_TO_CREATE_GROUP_LOAN);
            }

            for (ClientBO client : activeClientsOfGroup) {
                LoanAccountDetailsDto clientDetail = new LoanAccountDetailsDto();
                clientDetail.setClientId(client.getCustomerId().toString());
                clientDetail.setClientName(client.getDisplayName());
                clientDetails.add(clientDetail);
            }
        }

        return new LoanCreationProductDetailsDto(loanProductDtos, customerDetailDto, nextMeetingDate, recurMonth,
                isGroup, isGlimEnabled, loanCreationGlimDto, clientDetails);
    }

    @Override
    public LoanCreationLoanDetailsDto retrieveLoanDetailsForLoanAccountCreation(UserContext userContext,
            Integer customerId, Short productId) throws ApplicationException {

        List<FeeDto> additionalFees = new ArrayList<FeeDto>();
        List<FeeDto> defaultFees = new ArrayList<FeeDto>();

        new LoanProductService(new LoanPrdBusinessService()).getDefaultAndAdditionalFees(productId, userContext, defaultFees, additionalFees);

        LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

        if (AccountingRules.isMultiCurrencyEnabled()) {
            defaultFees = getFilteredFeesByCurrency(defaultFees, loanOffering.getCurrency().getCurrencyId());
            additionalFees = getFilteredFeesByCurrency(additionalFees, loanOffering.getCurrency().getCurrencyId());
        }

        // setDateIntoForm
        CustomerBO customer = customerDao.findCustomerById(customerId);
        LoanAmountOption eligibleLoanAmount = loanOffering.eligibleLoanAmount(customer.getMaxLoanAmount(loanOffering),
                customer.getMaxLoanCycleForProduct(loanOffering));
        LoanOfferingInstallmentRange eligibleNoOfInstall = loanOffering.eligibleNoOfInstall(customer
                .getMaxLoanAmount(loanOffering), customer.getMaxLoanCycleForProduct(loanOffering));

        CustomValueDto customValueDto = new MasterPersistence().getLookUpEntity(MasterConstants.COLLATERAL_TYPES,
                userContext.getLocaleId());
        List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();

        // Business activities got in getPrdOfferings also but only for glim.
        List<ValueListElement> loanPurposes = new MasterDataService().retrieveMasterEntities(
                MasterConstants.LOAN_PURPOSES, userContext.getLocaleId());

        List<CustomFieldDefinitionEntity> customFieldDefs = new AccountBusinessService()
                .retrieveCustomFieldsDefinition(EntityType.LOAN);

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
            if (StringUtils.isNotBlank(fieldDef.getDefaultValue())
                    && fieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                customFields.add(new CustomFieldDto(fieldDef.getFieldId(), DateUtils.getUserLocaleDate(userContext
                        .getPreferredLocale(), fieldDef.getDefaultValue()), fieldDef.getFieldType()));
            } else {
                customFields.add(new CustomFieldDto(fieldDef.getFieldId(), fieldDef.getDefaultValue(), fieldDef
                        .getFieldType()));
            }
        }

        MeetingDetailsEntity loanOfferingMeetingDetail = loanOffering.getLoanOfferingMeeting().getMeeting()
                .getMeetingDetails();
        RecurrenceType loanProductRecurrenceType = loanOfferingMeetingDetail.getRecurrenceTypeEnum();

        List<FundBO> funds = getFunds(loanOffering);

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationBusinessService()
                .isRepaymentIndepOfMeetingEnabled();

        return new LoanCreationLoanDetailsDto(isRepaymentIndependentOfMeetingEnabled, additionalFees, defaultFees,
                loanOfferingMeetingDetail, loanProductRecurrenceType, customer.getCustomerMeetingValue()
                        .getMeetingDetails(), loanOffering, eligibleLoanAmount, eligibleNoOfInstall, collateralTypes,
                loanPurposes, customFieldDefs, customFields, funds);
    }

    private List<FundBO> getFunds(final LoanOfferingBO loanOffering) {
        List<FundBO> funds = new ArrayList<FundBO>();
        if (loanOffering.getLoanOfferingFunds() != null && loanOffering.getLoanOfferingFunds().size() > 0) {
            for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds()) {
                funds.add(loanOfferingFund.getFund());
            }
        }
        return funds;
    }

    private List<FeeDto> getFilteredFeesByCurrency(List<FeeDto> defaultFees, Short currencyId) {
        List<FeeDto> filteredFees = new ArrayList<FeeDto>();
        for (FeeDto feeDto : defaultFees) {
            if (feeDto.isValidForCurrency(currencyId)) {
                filteredFees.add(feeDto);
            }
        }
        return filteredFees;
    }

    @Override
    public LoanCreationLoanScheduleDetailsDto retrieveScheduleDetailsForLoanCreation(UserContext userContext,
            Integer customerId, DateTime disbursementDate, FundBO fund, LoanAccountActionForm loanActionForm)
            throws ApplicationException {

        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        LocalizationConverter localizationConverter = new LocalizationConverter();
        CustomerBO customer = customerDao.findCustomerById(customerId);

        new LoanService().validateDisbursementDateForNewLoan(customer.getOfficeId(), disbursementDate);

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence()
                .isRepaymentIndepOfMeetingEnabled();

        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndependentOfMeetingEnabled) {
            newMeetingForRepaymentDay = this
                    .createNewMeetingForRepaymentDay(disbursementDate, loanActionForm, customer);
        }

        LoanBO loan = assembleLoan(userContext, customer, disbursementDate, fund,
                isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay, loanActionForm);

        List<RepaymentScheduleInstallment> installments = loan.toRepaymentScheduleDto();

        if (isRepaymentIndependentOfMeetingEnabled) {
            Date firstRepaymentDate = installments.get(0).getDueDate();

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

        final boolean isGroup = customer.isGroup();
        final boolean isGlimApplicable = isGroup && configurationPersistence.isGlimEnabled();
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

        boolean isLoanPendingApprovalDefined = ProcessFlowRules.isLoanPendingApprovalStateEnabled();

        return new LoanCreationLoanScheduleDetailsDto(isGroup, isGlimApplicable, glimLoanAmount,
                isLoanPendingApprovalDefined, installments, new ArrayList<PaymentDataHtmlBean>());
    }

    @Override
    public LoanCreationLoanScheduleDetailsDto retrieveScheduleDetailsForRedoLoan(UserContext userContext,
            Integer customerId, DateTime disbursementDate, FundBO fund, LoanAccountActionForm loanActionForm)
            throws ApplicationException {

        ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
        LocalizationConverter localizationConverter = new LocalizationConverter();
        CustomerBO customer = customerDao.findCustomerById(customerId);

        new LoanService().validateDisbursementDateForNewLoan(customer.getOfficeId(), disbursementDate);

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence()
                .isRepaymentIndepOfMeetingEnabled();

        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndependentOfMeetingEnabled) {
            newMeetingForRepaymentDay = this
                    .createNewMeetingForRepaymentDay(disbursementDate, loanActionForm, customer);
        }

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

        LoanBO loan = LoanBO.redoLoan(userContext, loanOffering, customer, accountState, loanAmount, numOfInstallments,
                disbursementDate.toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod, fund, fees,
                customFields, maxLoanAmount, minLoanAmount, maxNumOfInstallments, minNumOfShortInstallments,
                isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay);
        loan.setExternalId(externalId);

        List<RepaymentScheduleInstallment> installments = loan.toRepaymentScheduleDto();

        if (isRepaymentIndependentOfMeetingEnabled) {
            Date firstRepaymentDate = installments.get(0).getDueDate();

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

        final boolean isGroup = customer.isGroup();
        final boolean isGlimApplicable = isGroup && configurationPersistence.isGlimEnabled();
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

        List<PaymentDataHtmlBean> paymentDataBeans = new ArrayList<PaymentDataHtmlBean>(installments.size());
        PersonnelBO personnel = this.personnelDao.findPersonnelById(userContext.getId());
        if (personnel == null) {
            throw new IllegalArgumentException("bad UserContext id");
        }

        for (RepaymentScheduleInstallment repaymentScheduleInstallment : installments) {
            paymentDataBeans.add(new PaymentDataHtmlBean(userContext.getPreferredLocale(), personnel,
                    repaymentScheduleInstallment));
        }

        boolean isLoanPendingApprovalDefined = ProcessFlowRules.isLoanPendingApprovalStateEnabled();

        return new LoanCreationLoanScheduleDetailsDto(isGroup, isGlimApplicable, glimLoanAmount,
                isLoanPendingApprovalDefined, installments, paymentDataBeans);
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
    public LoanCreationPreviewDto previewLoanCreationDetails(Integer customerId,
            List<LoanAccountDetailsDto> accountDetails, List<String> selectedClientIds,
            List<BusinessActivityEntity> businessActEntity) {

        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        final boolean isGroup = customer.isGroup();
        final boolean isGlimEnabled = new ConfigurationPersistence().isGlimEnabled();

        List<LoanAccountDetailsDto> loanAccountDetailsView = new ArrayList<LoanAccountDetailsDto>();

        for (String clientIdAsString : selectedClientIds) {
            if (StringUtils.isNotEmpty(clientIdAsString)) {

                LoanAccountDetailsDto tempLoanAccount = new LoanAccountDetailsDto();
                ClientBO client = (ClientBO) this.customerDao.findCustomerById(Integer.valueOf(clientIdAsString));

                LoanAccountDetailsDto account = null;
                for (LoanAccountDetailsDto tempAccount : accountDetails) {
                    if (tempAccount.getClientId().equals(clientIdAsString)) {
                        account = tempAccount;
                    }
                }
                tempLoanAccount.setClientId(client.getGlobalCustNum().toString());
                tempLoanAccount.setClientName(client.getDisplayName());
                tempLoanAccount.setLoanAmount((null != account.getLoanAmount()
                        && !EMPTY.equals(account.getLoanAmount().toString()) ? account.getLoanAmount() : "0.0"));

                String businessActName = null;
                for (ValueListElement busact : businessActEntity) {
                    if (busact.getId().toString().equals(account.getBusinessActivity())) {
                        businessActName = busact.getName();
                    }
                }
                tempLoanAccount.setBusinessActivity(account.getBusinessActivity());
                tempLoanAccount.setBusinessActivityName((StringUtils.isNotBlank(businessActName) ? businessActName
                        : "-").toString());
                tempLoanAccount.setGovermentId((StringUtils.isNotBlank(client.getGovernmentId()) ? client
                        .getGovernmentId() : "-").toString());

                loanAccountDetailsView.add(tempLoanAccount);
            }
        }

        return new LoanCreationPreviewDto(isGlimEnabled, isGroup, loanAccountDetailsView);
    }

    @Override
    public LoanCreationResultDto createLoan(UserContext userContext, Integer customerId, DateTime disbursementDate,
            FundBO fund, LoanAccountActionForm loanActionForm) throws ApplicationException {

        CustomerBO customer = this.customerDao.findCustomerById(customerId);

        final boolean isGlimApplicable = new ConfigurationPersistence().isGlimEnabled() && customer.isGroup();

        if (!isPermissionAllowed(loanActionForm.getState().getValue(), userContext, customer.getOffice().getOfficeId(),
                customer.getPersonnel().getPersonnelId())) {
            throw new ApplicationException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence()
                .isRepaymentIndepOfMeetingEnabled();

        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndependentOfMeetingEnabled) {
            newMeetingForRepaymentDay = this
                    .createNewMeetingForRepaymentDay(disbursementDate, loanActionForm, customer);
        }

        LoanBO loan = assembleLoan(userContext, customer, disbursementDate, fund,
                isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay, loanActionForm);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        try {
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan
                    .getAccountState(), createdBy, loan));
            new LoanPersistence().createOrUpdate(loan);
            StaticHibernateUtil.commitTransaction();

            loan.setGlobalAccountNum(loan.generateId(userContext.getBranchGlobalNum()));
            new LoanPersistence().createOrUpdate(loan);
            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

        return new LoanCreationResultDto(isGlimApplicable, loan.getAccountId(), loan.getGlobalAccountNum(), loan,
                customer);
    }

    @Override
    public LoanCreationResultDto redoLoan(UserContext userContext, Integer customerId, DateTime disbursementDate,
            LoanAccountActionForm loanAccountActionForm) throws ApplicationException {

        CustomerBO customer = customerDao.findCustomerById(customerId);
        LoanBO loan = redoLoan(customer, loanAccountActionForm, disbursementDate, userContext);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        try {
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan
                    .getAccountState(), createdBy, loan));
            new LoanPersistence().createOrUpdate(loan);
            StaticHibernateUtil.commitTransaction();

            loan.setGlobalAccountNum(loan.generateId(userContext.getBranchGlobalNum()));
            new LoanPersistence().createOrUpdate(loan);
            StaticHibernateUtil.commitTransaction();
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

        return new LoanCreationResultDto(new ConfigurationPersistence().isGlimEnabled(), loan.getAccountId(), loan
                .getGlobalAccountNum(), loan, customer);
    }

    private boolean isPermissionAllowed(final Short newSate, final UserContext userContext, final Short officeId,
            final Short loanOfficerId) {
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId,
                        loanOfficerId));
    }

    @Override
    public void checkIfProductsOfferingCanCoexist(Integer loanAccountId) throws ServiceException, PersistenceException,
            AccountException {
        LoanBO loan = this.loanDao.findById(loanAccountId);
        new ProductMixValidator().checkIfProductsOfferingCanCoexist(loan);
    }

    @Override
    public LoanDisbursalDto getLoanDisbursalDto(Integer loanAccountId) throws ServiceException {

        LoanBO loan = this.loanDao.findById(loanAccountId);

        Date proposedDate = new DateTimeService().getCurrentJavaDateTime();
        if (AccountingRules.isBackDatedTxnAllowed()) {
            proposedDate = loan.getDisbursementDate();
        }

        return new LoanDisbursalDto(loan.getAccountId(), proposedDate, loan.getLoanAmount(), loan
                .getAmountTobePaidAtdisburtail());
    }

    @Override
    public LoanBO previewLoanRedoDetails(Integer customerId, LoanAccountActionForm loanAccountActionForm,
            DateTime disbursementDate, UserContext userContext) throws ApplicationException {
        CustomerBO customer = customerDao.findCustomerById(customerId);
        return redoLoan(customer, loanAccountActionForm, disbursementDate, userContext);
    }

    private LoanBO redoLoan(CustomerBO customer, LoanAccountActionForm loanAccountActionForm,
            DateTime disbursementDate, UserContext userContext) throws ApplicationException {
        boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndepOfMeetingEnabled) {
            newMeetingForRepaymentDay = createNewMeetingForRepaymentDay(disbursementDate, loanAccountActionForm,
                    customer);
        }

        Short productId = loanAccountActionForm.getPrdOfferingIdValue();
        LoanOfferingBO loanOffering = new LoanPrdBusinessService()
                .getLoanOffering(productId, userContext.getLocaleId());

        Money loanAmount = new Money(loanOffering.getCurrency(), loanAccountActionForm.getLoanAmount());
        Short numOfInstallments = loanAccountActionForm.getNoOfInstallmentsValue();
        boolean isInterestDeductedAtDisbursement = loanAccountActionForm.isInterestDedAtDisbValue();
        Double interest = loanAccountActionForm.getInterestDoubleValue();
        Short gracePeriod = loanAccountActionForm.getGracePeriodDurationValue();
        List<FeeDto> fees = loanAccountActionForm.getFeesToApply();
        List<CustomFieldDto> customFields = loanAccountActionForm.getCustomFields();
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
                fund, fees, customFields, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                minNumOfShortInstallments, isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
        redoLoan.setExternalId(externalId);
        redoLoan.setBusinessActivityId(selectedLoanPurpose);
        redoLoan.setCollateralNote(collateralNote);
        redoLoan.setCollateralTypeId(selectedCollateralType);

        redoLoan.changeStatus(AccountState.LOAN_APPROVED, null, "Automatic Status Update (Redo Loan)");

        PersonnelBO user = personnelDao.findPersonnelById(userContext.getId());

        // We're assuming cash disbursal for this situation right now
        try {
            redoLoan.disburseLoan(user, PaymentTypes.CASH.getValue(), false);
        } catch (PersistenceException e1) {
            throw new MifosRuntimeException(e1);
        }

        List<PaymentDataHtmlBean> paymentDataBeans = loanAccountActionForm.getPaymentDataBeans();
        PaymentData payment;
        try {
            for (PaymentDataTemplate template : paymentDataBeans) {
                if (template.hasValidAmount() && template.getTransactionDate() != null) {
                    if (!customer.getCustomerMeeting().getMeeting().isValidMeetingDate(template.getTransactionDate(),
                            DateUtils.getLastDayOfNextYear())) {
                        throw new AccountException("errors.invalidTxndate");
                    }
                    payment = PaymentData.createPaymentData(template);
                    redoLoan.applyPayment(payment, false);
                }
            }
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        } catch (MeetingException e) {
            throw new ServiceException(e);
        }
        return redoLoan;
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

        return loan.isTrxnDateValid(trxnDate);
    }

    @Override
    public LoanBO retrieveLoanRepaymentSchedule(UserContext userContext, Integer loanId) {
        LoanBO loan = this.loanDao.findById(loanId);
        loan.updateDetails(userContext);
        return loan;
    }

    @Override
    public List<AccountStatusChangeHistoryEntity> retrieveLoanAccountStatusChangeHistory(UserContext userContext, String globalAccountNum) {

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        loan.updateDetails(userContext);
        return new ArrayList<AccountStatusChangeHistoryEntity>(loan.getAccountStatusChangeHistory());
    }

    public Money getTotalEarlyRepayAmount(String globalAccountNum) {

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        return loan.getTotalEarlyRepayAmount();
    }

    @Override
    public void makeEarlyRepayment(String globalAccountNum, String earlyRepayAmountStr, String receiptNumber,
            java.sql.Date receiptDate, String paymentTypeId, Short userId) throws AccountException {

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        Money earlyRepayAmount = new Money(loan.getCurrency(), earlyRepayAmountStr);
        loan.makeEarlyRepayment(earlyRepayAmount, receiptNumber, receiptDate, paymentTypeId, userId);
    }
}