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

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.framework.util.CollectionUtils.collect;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.fund.servicefacade.FundCodeDto;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.MaxMinLoanAmount;
import org.mifos.accounts.loan.business.MaxMinNoOfInstall;
import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.validators.InstallmentValidationContext;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.MultipleLoanCreationDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.CashFlowDetail;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.InstallmentRange;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountSearchResultsDto;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.clientportfolio.loan.service.CreateLoanSchedule;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGlimLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.clientportfolio.newloan.applicationservice.VariableInstallmentWithFeeValidationResult;
import org.mifos.clientportfolio.newloan.domain.CreationDetail;
import org.mifos.clientportfolio.newloan.domain.GroupMemberLoanDetail;
import org.mifos.clientportfolio.newloan.domain.LoanAccountDetail;
import org.mifos.clientportfolio.newloan.domain.LoanDisbursementDateFactory;
import org.mifos.clientportfolio.newloan.domain.LoanDisbursementDateFinder;
import org.mifos.clientportfolio.newloan.domain.LoanDisbursementDateValidator;
import org.mifos.clientportfolio.newloan.domain.LoanDisbursmentDateFactoryImpl;
import org.mifos.clientportfolio.newloan.domain.LoanProductOverridenDetail;
import org.mifos.clientportfolio.newloan.domain.LoanSchedule;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleConfiguration;
import org.mifos.clientportfolio.newloan.domain.LoanService;
import org.mifos.clientportfolio.newloan.domain.service.LoanScheduleService;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.Localization;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.ApplicationConfigurationDto;
import org.mifos.dto.domain.CashFlowDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.CreateLoanRequest;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.InstallmentDetailsDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.ProductDetailsDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.AccountFeesDto;
import org.mifos.dto.screen.CashFlowDataDto;
import org.mifos.dto.screen.ChangeAccountStatusDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.LoanAccountDetailDto;
import org.mifos.dto.screen.LoanAccountInfoDto;
import org.mifos.dto.screen.LoanAccountMeetingDto;
import org.mifos.dto.screen.LoanCreationGlimDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationPreviewDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanDisbursalDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.dto.screen.LoanPerformanceHistoryDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.dto.screen.LoanScheduledInstallmentDto;
import org.mifos.dto.screen.LoanSummaryDto;
import org.mifos.dto.screen.MultipleLoanAccountDetailsDto;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.Transformer;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.util.CollectionUtils;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.mifos.security.MifosUser;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoanAccountServiceFacadeWebTier implements LoanAccountServiceFacade {

    private final OfficeDao officeDao;
    private final LoanProductDao loanProductDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final FundDao fundDao;
    private final LoanDao loanDao;
    private final HolidayDao holidayDao;
    private final AccountService accountService;
    private final ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;
    private final LoanBusinessService loanBusinessService;
    private final LoanService loanService;
    private final LoanScheduleService loanScheduleService;
    private final HibernateTransactionHelper transactionHelper;

    @Autowired
    private FeeDao feeDao;

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Autowired
    private LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    @Autowired
    private CashFlowService cashFlowService;
    private final InstallmentsValidator installmentsValidator;
    private final HolidayServiceFacade holidayServiceFacade;

    @Autowired
    public LoanAccountServiceFacadeWebTier(OfficeDao officeDao, LoanProductDao loanProductDao, CustomerDao customerDao,
                                           PersonnelDao personnelDao, FundDao fundDao, LoanDao loanDao, HolidayDao holidayDao,
                                           AccountService accountService, ScheduleCalculatorAdaptor scheduleCalculatorAdaptor,
                                           LoanBusinessService loanBusinessService, LoanService loanService, LoanScheduleService loanScheduleService,
                                           InstallmentsValidator installmentsValidator, HolidayServiceFacade holidayServiceFacade) {
        this.officeDao = officeDao;
        this.loanProductDao = loanProductDao;
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.fundDao = fundDao;
        this.loanDao = loanDao;
        this.holidayDao = holidayDao;
        this.accountService = accountService;
        this.scheduleCalculatorAdaptor = scheduleCalculatorAdaptor;
        this.loanBusinessService = loanBusinessService;
        this.loanService = loanService;
        this.loanScheduleService = loanScheduleService;
        this.installmentsValidator = installmentsValidator;
        this.holidayServiceFacade = holidayServiceFacade;
        transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    }

    @Override
    public AccountStatusDto retrieveAccountStatuses(Long loanAccountId) {

        LoanBO loanAccount = this.loanDao.findById(loanAccountId.intValue());

        try {
            List<ListElement> loanStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeLoanStates();

            List<AccountStateEntity> statusList = AccountStateMachines.getInstance().getLoanStatusList(
                    loanAccount.getAccountState());
            for (AccountStateEntity accountState : statusList) {
                loanStatesList.add(new ListElement(accountState.getId().intValue(), accountState.getName()));
            }

            return new AccountStatusDto(loanStatesList);
        } catch (StatesInitializationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    @Override
    public String updateLoanAccountStatus(AccountUpdateStatus updateStatus) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

        LoanBO loanAccount = this.loanDao.findById(updateStatus.getSavingsId().intValue());
        loanAccount.updateDetails(userContext);
        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(loanAccount);
            AccountState newStatus = AccountState.fromShort(updateStatus.getNewStatusId());

            loanAccount.changeStatus(newStatus, updateStatus.getFlagId(), updateStatus.getComment(), loggedInUser);
            this.loanDao.save(loanAccount);
            this.transactionHelper.commitTransaction();
            return loanAccount.getGlobalAccountNum();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public LoanAccountDetailDto retrieveLoanAccountNotes(Long loanAccountId) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        LoanBO loanAccount = this.loanDao.findById(loanAccountId.intValue());
        loanAccount.updateDetails(userContext);
        return loanAccount.toDto();
    }

    @Override
    public void addNote(CreateAccountNote accountNote) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        PersonnelBO createdBy = this.personnelDao.findPersonnelById(accountNote.getCreatedById().shortValue());
        LoanBO loanAccount = this.loanDao.findById(accountNote.getAccountId());

        AccountNotesEntity accountNotes = new AccountNotesEntity(new java.sql.Date(accountNote.getCommentDate()
                .toDateMidnight().toDate().getTime()), accountNote.getComment(), createdBy, loanAccount);

        try {
            this.transactionHelper.startTransaction();

            loanAccount.updateDetails(userContext);
            loanAccount.addAccountNotes(accountNotes);
            this.loanDao.save(loanAccount);
            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public LoanCreationProductDetailsDto retrieveGetProductDetailsForLoanAccountCreation(final Integer customerId) {

        final CustomerBO customer = this.customerDao.findCustomerById(customerId);

        final CustomerDetailDto customerDetailDto = customer.toCustomerDetailDto();

        // FIXME - keithw - below code is not needed when jsp/struts is removed as this is worked out for enter loan account details step.
        final Date nextMeetingDate = customer.getCustomerAccount().getNextMeetingDate();
        final String recurMonth = customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter().toString();
        final boolean isGroup = customer.isGroup();
        final boolean isGlimEnabled = new ConfigurationPersistence().isGlimEnabled();
        final boolean isLsimEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

        List<PrdOfferingDto> loanProductDtos = retrieveActiveLoanProductsApplicableForCustomer(customer, isLsimEnabled);

        LoanCreationGlimDto loanCreationGlimDto = null;
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();

        Errors errors = new Errors();
        if (isGroup && isGlimEnabled) {
            final List<ValueListElement> loanPurposes = loanProductDao.findAllLoanPurposes();
            final List<ClientBO> activeClientsOfGroup = customerDao.findActiveClientsUnderGroup(customer);
            loanCreationGlimDto = new LoanCreationGlimDto(loanPurposes);

            if (activeClientsOfGroup == null || activeClientsOfGroup.size() < 2) {
                String defaultMessage = "Group loan is not allowed as there must be at least two active clients within the group when Group loan with individual monitoring (GLIM) is enabled";
                ErrorEntry errorEntry  = new ErrorEntry("createLoanAccount.glim.invalid.less.than.two.active.clients.in.group", "activeClients", defaultMessage);
                errors.addErrors(Arrays.asList(errorEntry));
                loanProductDtos = new ArrayList<PrdOfferingDto>();
            } else {

                for (ClientBO client : activeClientsOfGroup) {
                    LoanAccountDetailsDto clientDetail = new LoanAccountDetailsDto();
                    clientDetail.setClientId(client.getCustomerId().toString());
                    clientDetail.setClientName(client.getDisplayName());
                    clientDetails.add(clientDetail);
                }
            }
        }

        return new LoanCreationProductDetailsDto(loanProductDtos, customerDetailDto, nextMeetingDate, recurMonth,
                isGroup, isGlimEnabled, loanCreationGlimDto, clientDetails, errors);
    }

    private List<PrdOfferingDto> retrieveActiveLoanProductsApplicableForCustomer(final CustomerBO customer, boolean lsimEnabled) {

        List<PrdOfferingDto> applicationLoanProductDtos = new ArrayList<PrdOfferingDto>();
        final List<LoanOfferingBO> applicableLoanProducts = new ArrayList<LoanOfferingBO>();

        final List<LoanOfferingBO> loanOfferings = loanProductDao
                .findActiveLoanProductsApplicableToCustomerLevel(customer.getCustomerLevel());

        if (!lsimEnabled) {
            final MeetingBO customerMeeting = customer.getCustomerMeetingValue();
            for (LoanOfferingBO loanProduct : loanOfferings) {
                if (loanProduct.getLoanOfferingMeetingValue().hasSameRecurrenceAs(customerMeeting)
                        && customerMeeting.recursOnMultipleOf(loanProduct.getLoanOfferingMeetingValue())) {
                    applicableLoanProducts.add(loanProduct);
                }
            }
            
            for (LoanOfferingBO loanProduct : applicableLoanProducts) {
                applicationLoanProductDtos.add(loanProduct.toDto());
            }
        } else {
            for (LoanOfferingBO loanProduct : loanOfferings) {
                applicationLoanProductDtos.add(loanProduct.toDto());
            }
        }

        return applicationLoanProductDtos;
    }

    @Override
    public LoanCreationLoanDetailsDto retrieveLoanDetailsForLoanAccountCreation(Integer customerId, Short productId, boolean isLoanWithBackdatedPayments) {

        try {
            List<org.mifos.dto.domain.FeeDto> additionalFees = new ArrayList<org.mifos.dto.domain.FeeDto>();
            List<org.mifos.dto.domain.FeeDto> defaultFees = new ArrayList<org.mifos.dto.domain.FeeDto>();

            LoanOfferingBO loanProduct = this.loanProductDao.findById(productId.intValue());

            MeetingBO loanProductMeeting = loanProduct.getLoanOfferingMeetingValue();
            MeetingDto loanOfferingMeetingDto = loanProductMeeting.toDto();

            List<FeeBO> fees = this.feeDao.getAllAppllicableFeeForLoanCreation();

            for (FeeBO fee : fees) {
                if (!fee.isPeriodic() || (MeetingBO.isMeetingMatched(fee.getFeeFrequency().getFeeMeetingFrequency(), loanProductMeeting))) {

                    org.mifos.dto.domain.FeeDto feeDto = fee.toDto();

                    FeeFrequencyType feeFrequencyType = FeeFrequencyType.getFeeFrequencyType(fee.getFeeFrequency().getFeeFrequencyType().getId());

                    FeeFrequencyTypeEntity feeFrequencyEntity = this.loanProductDao.retrieveFeeFrequencyType(feeFrequencyType);
                    String feeFrequencyTypeName = MessageLookup.getInstance().lookup(feeFrequencyEntity.getLookUpValue());
                    feeDto.setFeeFrequencyType(feeFrequencyTypeName);

                    if (feeDto.getFeeFrequency().isOneTime()) {
                        FeePayment feePayment = FeePayment.getFeePayment(fee.getFeeFrequency().getFeePayment().getId());
                        FeePaymentEntity feePaymentEntity = this.loanProductDao.retrieveFeePaymentType(feePayment);
                        String feePaymentName = MessageLookup.getInstance().lookup(feePaymentEntity.getLookUpValue());
                        feeDto.getFeeFrequency().setPayment(feePaymentName);
                    }

                    if (loanProduct.isFeePresent(fee)) {
                        defaultFees.add(feeDto);
                    } else {
                        additionalFees.add(feeDto);
                    }
                }
            }

            if (AccountingRules.isMultiCurrencyEnabled()) {
                defaultFees = getFilteredFeesByCurrency(defaultFees, loanProduct.getCurrency().getCurrencyId());
                additionalFees = getFilteredFeesByCurrency(additionalFees, loanProduct.getCurrency().getCurrencyId());
            }

            Map<String, String> defaultFeeOptions = new LinkedHashMap<String, String>();
            for (org.mifos.dto.domain.FeeDto feeDto : defaultFees) {
                defaultFeeOptions.put(feeDto.getId(), feeDto.getName());
            }

            Map<String, String> additionalFeeOptions = new LinkedHashMap<String, String>();
            for (org.mifos.dto.domain.FeeDto feeDto : additionalFees) {
                additionalFeeOptions.put(feeDto.getId(), feeDto.getName());
            }

            CustomerBO customer = this.customerDao.findCustomerById(customerId);
            boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationBusinessService().isRepaymentIndepOfMeetingEnabled();

            LoanDisbursementDateFactory loanDisbursementDateFactory = new LoanDisbursmentDateFactoryImpl();
            LoanDisbursementDateFinder loanDisbursementDateFinder = loanDisbursementDateFactory.create(customer, loanProduct, isRepaymentIndependentOfMeetingEnabled, isLoanWithBackdatedPayments);
            LocalDate nextPossibleDisbursementDate = loanDisbursementDateFinder.findClosestMatchingDateFromAndInclusiveOf(new LocalDate());

            LoanAmountOption eligibleLoanAmount = loanProduct.eligibleLoanAmount(customer.getMaxLoanAmount(loanProduct), customer.getMaxLoanCycleForProduct(loanProduct));
            LoanOfferingInstallmentRange eligibleNoOfInstall = loanProduct.eligibleNoOfInstall(customer.getMaxLoanAmount(loanProduct), customer.getMaxLoanCycleForProduct(loanProduct));

            Double defaultInterestRate = loanProduct.getDefInterestRate();
            Double maxInterestRate = loanProduct.getMaxInterestRate();
            Double minInterestRate = loanProduct.getMinInterestRate();

            LinkedHashMap<String, String> collateralOptions = new LinkedHashMap<String, String>();
            LinkedHashMap<String, String> purposeOfLoanOptions = new LinkedHashMap<String, String>();

            CustomValueDto customValueDto = legacyMasterDao.getLookUpEntity(MasterConstants.COLLATERAL_TYPES);
            List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();
            for (CustomValueListElementDto element : collateralTypes) {
                collateralOptions.put(element.getId().toString(), element.getName());
            }

            // Business activities got in getPrdOfferings also but only for glim.
            List<ValueListElement> loanPurposes = legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES);
            for (ValueListElement element : loanPurposes) {
                purposeOfLoanOptions.put(element.getId().toString(), element.getName());
            }

            List<FundDto> fundDtos = new ArrayList<FundDto>();
            List<FundBO> funds = getFunds(loanProduct);
            for (FundBO fund : funds) {
                FundDto fundDto = new FundDto();
                fundDto.setId(Short.toString(fund.getFundId()));
                fundDto.setCode(translateFundCodeToDto(fund.getFundCode()));
                fundDto.setName(fund.getFundName());

                fundDtos.add(fundDto);
            }

            ProductDetailsDto productDto = loanProduct.toDetailsDto();
            CustomerDetailDto customerDetailDto = customer.toCustomerDetailDto();

            Integer gracePeriodInInstallments = loanProduct.getGracePeriodDuration().intValue();

            final List<PrdOfferingDto> loanProductDtos = retrieveActiveLoanProductsApplicableForCustomer(customer, isRepaymentIndependentOfMeetingEnabled);

            InterestType interestType = InterestType.fromInt(loanProduct.getInterestTypes().getId().intValue());
            InterestTypesEntity productInterestType = this.loanProductDao.findInterestType(interestType);
            String interestTypeName = MessageLookup.getInstance().lookup(productInterestType.getLookUpValue());

            LinkedHashMap<String, String> daysOfTheWeekOptions = new LinkedHashMap<String, String>();
            List<WeekDay> workingDays = new FiscalCalendarRules().getWorkingDays();
            for (WeekDay workDay : workingDays) {
                String weekdayName = MessageLookup.getInstance().lookup(workDay.getPropertiesKey());
                workDay.setWeekdayName(weekdayName);
                daysOfTheWeekOptions.put(workDay.getValue().toString(), weekdayName);
            }

            LinkedHashMap<String, String> weeksOfTheMonthOptions = new LinkedHashMap<String, String>();
            for(RankOfDay weekOfMonth : RankOfDay.values()) {
                String weekOfMonthName = MessageLookup.getInstance().lookup(weekOfMonth.getPropertiesKey());
                weeksOfTheMonthOptions.put(weekOfMonth.getValue().toString(), weekOfMonthName);
            }

            boolean variableInstallmentsAllowed = loanProduct.isVariableInstallmentsAllowed();
            Integer minGapInDays = Integer.valueOf(0);
            Integer maxGapInDays = Integer.valueOf(0);
            BigDecimal minInstallmentAmount = BigDecimal.ZERO;
            if (variableInstallmentsAllowed) {
                VariableInstallmentDetailsBO variableInstallmentsDetails = loanProduct.getVariableInstallmentDetails();
                minGapInDays = variableInstallmentsDetails.getMinGapInDays();
                maxGapInDays = variableInstallmentsDetails.getMaxGapInDays();
                minInstallmentAmount = variableInstallmentsDetails.getMinInstallmentAmount().getAmount();
            }

            boolean compareCashflowEnabled = loanProduct.isCashFlowCheckEnabled();

            // GLIM specific
            final boolean isGroup = customer.isGroup();
            final boolean isGlimEnabled = new ConfigurationPersistence().isGlimEnabled();

            List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();

            if (isGroup && isGlimEnabled) {
                final List<ClientBO> activeClientsOfGroup = customerDao.findActiveClientsUnderGroup(customer);

                if (activeClientsOfGroup == null || activeClientsOfGroup.isEmpty()) {
                    throw new BusinessRuleException(GroupConstants.IMPOSSIBLE_TO_CREATE_GROUP_LOAN);
                }

                for (ClientBO client : activeClientsOfGroup) {
                    LoanAccountDetailsDto clientDetail = new LoanAccountDetailsDto();
                    clientDetail.setClientId(client.getGlobalCustNum());
                    clientDetail.setClientName(client.getDisplayName());
                    clientDetails.add(clientDetail);
                }
            }
            // end of GLIM specific

            int digitsAfterDecimalForInterest = AccountingRules.getDigitsAfterDecimalForInterest().intValue();
            int digitsBeforeDecimalForInterest = AccountingRules.getDigitsBeforeDecimalForInterest().intValue();
            int digitsAfterDecimalForMonetaryAmounts = AccountingRules.getDigitsAfterDecimal().intValue();
            int digitsBeforeDecimalForMonetaryAmounts = AccountingRules.getDigitsBeforeDecimal().intValue();
            
            ApplicationConfigurationDto appConfig = new ApplicationConfigurationDto(digitsAfterDecimalForInterest, digitsBeforeDecimalForInterest, digitsAfterDecimalForMonetaryAmounts, digitsBeforeDecimalForMonetaryAmounts);

            return new LoanCreationLoanDetailsDto(isRepaymentIndependentOfMeetingEnabled, loanOfferingMeetingDto,
                    customer.getCustomerMeetingValue().toDto(), loanPurposes, productDto, gracePeriodInInstallments, customerDetailDto, loanProductDtos,
                    interestTypeName, fundDtos, collateralOptions, purposeOfLoanOptions,
                    defaultFeeOptions, additionalFeeOptions, defaultFees, additionalFees,
                    BigDecimal.valueOf(eligibleLoanAmount.getDefaultLoanAmount()),
                    BigDecimal.valueOf(eligibleLoanAmount.getMaxLoanAmount()), BigDecimal.valueOf(eligibleLoanAmount.getMinLoanAmount()), defaultInterestRate, maxInterestRate, minInterestRate,
                    eligibleNoOfInstall.getDefaultNoOfInstall().intValue(), eligibleNoOfInstall.getMaxNoOfInstall().intValue(), eligibleNoOfInstall.getMinNoOfInstall().intValue(), nextPossibleDisbursementDate,
                    daysOfTheWeekOptions, weeksOfTheMonthOptions, variableInstallmentsAllowed, minGapInDays, maxGapInDays, minInstallmentAmount, compareCashflowEnabled,
                    isGlimEnabled, isGroup, clientDetails, appConfig);

        } catch (SystemException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private FundCodeDto translateFundCodeToDto(FundCodeEntity fundCode) {
        FundCodeDto fundCodeDto = new FundCodeDto();
        fundCodeDto.setId(Short.toString(fundCode.getFundCodeId()));
        fundCodeDto.setValue(fundCode.getFundCodeValue());
        return fundCodeDto;
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

    private List<org.mifos.dto.domain.FeeDto> getFilteredFeesByCurrency(List<org.mifos.dto.domain.FeeDto> defaultFees, Short currencyId) {
        List<org.mifos.dto.domain.FeeDto> filteredFees = new ArrayList<org.mifos.dto.domain.FeeDto>();
        for (org.mifos.dto.domain.FeeDto feeDto : defaultFees) {
            if (feeDto.isValidForCurrency(currencyId.intValue())) {
                filteredFees.add(feeDto);
            }
        }
        return filteredFees;
    }

    @Override
    public LoanCreationPreviewDto previewLoanCreationDetails(Integer customerId, List<LoanAccountDetailsDto> accountDetails, List<String> selectedClientIds) {

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

                tempLoanAccount.setBusinessActivity(account.getBusinessActivity());
                tempLoanAccount.setGovermentId((StringUtils.isNotBlank(client.getGovernmentId()) ? client.getGovernmentId() : "-").toString());

                loanAccountDetailsView.add(tempLoanAccount);
            }
        }

        return new LoanCreationPreviewDto(isGlimEnabled, isGroup, loanAccountDetailsView);
    }

    @Override
    public LoanScheduleDto createLoanSchedule(CreateLoanSchedule createLoanSchedule) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        // assemble into domain entities
        LoanOfferingBO loanProduct = this.loanProductDao.findById(createLoanSchedule.getProductId());
        CustomerBO customer = this.customerDao.findCustomerById(createLoanSchedule.getCustomerId());

        Money loanAmountDisbursed = new Money(loanProduct.getCurrency(), createLoanSchedule.getLoanAmount());

        List<AccountFeesEntity> accountFeeEntities = assembleAccountFees(createLoanSchedule.getAccountFeeEntities());
        LoanProductOverridenDetail overridenDetail = new LoanProductOverridenDetail(loanAmountDisbursed, createLoanSchedule.getDisbursementDate(),
                createLoanSchedule.getInterestRate(), createLoanSchedule.getNumberOfInstallments(), createLoanSchedule.getGraceDuration(), accountFeeEntities);

        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        boolean loanScheduleIndependentOfCustomerMeetingEnabled = createLoanSchedule.isRepaymentIndependentOfCustomerMeetingSchedule();

        MeetingBO loanMeeting = customer.getCustomerMeetingValue();
        if (loanScheduleIndependentOfCustomerMeetingEnabled) {
            loanMeeting = this.createNewMeetingForRepaymentDay(createLoanSchedule.getDisbursementDate(), createLoanSchedule, customer);
            
            if (loanProduct.isVariableInstallmentsAllowed()) {
                loanMeeting.setMeetingStartDate(createLoanSchedule.getDisbursementDate().toDateMidnight().toDate());
            }
        }
        LoanScheduleConfiguration configuration = new LoanScheduleConfiguration(loanScheduleIndependentOfCustomerMeetingEnabled, interestDays);

        LoanSchedule loanSchedule = this.loanScheduleService.generate(loanProduct, customer, loanMeeting, overridenDetail, configuration, userContext.getBranchId(), accountFeeEntities, createLoanSchedule.getDisbursementDate());

        // translate to DTO form
        List<LoanCreationInstallmentDto> installments = new ArrayList<LoanCreationInstallmentDto>();
        Short digitsAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        for (LoanScheduleEntity loanScheduleEntity : loanSchedule.getRoundedLoanSchedules()) {
            Integer installmentNumber = loanScheduleEntity.getInstallmentId().intValue();
            LocalDate dueDate = new LocalDate(loanScheduleEntity.getActionDate());
            String principal = loanScheduleEntity.getPrincipal().toString(digitsAfterDecimal);
            String interest = loanScheduleEntity.getInterest().toString(digitsAfterDecimal);
            String fees = loanScheduleEntity.getTotalFees().toString(digitsAfterDecimal);
            String penalty = "0.0";
            String total = loanScheduleEntity.getPrincipal().add(loanScheduleEntity.getInterest()).add(loanScheduleEntity.getTotalFees()).toString(digitsAfterDecimal);
            LoanCreationInstallmentDto installment = new LoanCreationInstallmentDto(installmentNumber, dueDate,
                    Double.valueOf(principal), Double.valueOf(interest), Double.valueOf(fees), Double.valueOf(penalty),
                    Double.valueOf(total));
            installments.add(installment);
        }

        return new LoanScheduleDto(customer.getDisplayName(), Double.valueOf(createLoanSchedule.getLoanAmount().doubleValue()), 
                createLoanSchedule.getDisbursementDate(), loanProduct.getGraceType().getValue().intValue(), installments);
    }

    private LoanAccountDetail assembleLoanAccountDetail(CreateLoanAccount loanAccountInfo) {

        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfo.getCustomerId());
        LoanOfferingBO loanProduct = this.loanProductDao.findById(loanAccountInfo.getProductId());

        Money loanAmount = new Money(loanProduct.getCurrency(), loanAccountInfo.getLoanAmount());
        AccountState accountStateType = AccountState.fromShort(loanAccountInfo.getAccountState().shortValue());
        FundBO fund = null;
        if (loanAccountInfo.getSourceOfFundId() != null) {
            fund = this.fundDao.findById(loanAccountInfo.getSourceOfFundId().shortValue());
        }

        return new LoanAccountDetail(customer, loanProduct, loanAmount, accountStateType, fund);
    }

    private LoanSchedule assembleLoanSchedule(CustomerBO customer, LoanOfferingBO loanProduct,
            LoanProductOverridenDetail overridenDetail, LoanScheduleConfiguration configuration,
            MeetingBO repaymentDayMeeting, OfficeBO userOffice, List<DateTime> loanScheduleDates, LocalDate disbursementDate, 
            List<Number> totalInstallmentAmounts) {

        LoanSchedule loanSchedule = null;
        if (loanScheduleDates.isEmpty() && totalInstallmentAmounts.isEmpty()) {
            loanSchedule = this.loanScheduleService.generate(loanProduct, customer, repaymentDayMeeting, overridenDetail, configuration, userOffice.getOfficeId(), overridenDetail.getAccountFeeEntities(), disbursementDate);
        } else {
            loanSchedule = this.loanScheduleService.generate(loanProduct, customer, repaymentDayMeeting, overridenDetail, configuration, overridenDetail.getAccountFeeEntities(), disbursementDate, loanScheduleDates, totalInstallmentAmounts);
        }

        return loanSchedule;
    }
    
    @Override
    public LoanCreationResultDto redoLoan(LoanAccountMeetingDto loanAccountMeetingDto, LoanAccountInfoDto loanAccountInfoDto,
                                          List<LoanPaymentDto> existingLoanPayments, List<LoanScheduledInstallmentDto> installmentDtos) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());

        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfoDto.getCustomerId());
        LoanBO loan = getLoanBOForRedo(customer, loanAccountMeetingDto, loanAccountInfoDto, existingLoanPayments, installmentDtos);

        StaticHibernateUtil.startTransaction();
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        try {
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan
                    .getAccountState(), createdBy, loan));
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            loan.setGlobalAccountNum(loan.generateId(userOffice.getGlobalOfficeNum()));
            this.loanDao.save(loan);
            loanBusinessService.persistOriginalSchedule(loan);
            StaticHibernateUtil.commitTransaction();
        } catch (AccountException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

        return new LoanCreationResultDto(new ConfigurationPersistence().isGlimEnabled() && customer.isGroup(), loan.getAccountId(), loan.getGlobalAccountNum());
    }

    private LoanBO getLoanBOForRedo(CustomerBO customer, LoanAccountMeetingDto loanAccountMeetingDto,
                                   LoanAccountInfoDto loanAccountInfoDto, List<LoanPaymentDto> existingLoanPayments,
                                   List<LoanScheduledInstallmentDto> installmentDtos) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        try {
            boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            MeetingBO newMeetingForRepaymentDay = null;
            if (isRepaymentIndepOfMeetingEnabled) {
                newMeetingForRepaymentDay = createNewMeetingForRepaymentDay(loanAccountInfoDto.getDisbursementDate(), loanAccountMeetingDto, customer);
            }

            Short productId = loanAccountInfoDto.getProductId();

            LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

            Money loanAmount = new Money(loanOffering.getCurrency(), loanAccountInfoDto.getLoanAmount());
            Short numOfInstallments = loanAccountInfoDto.getNumOfInstallments();
            boolean isInterestDeductedAtDisbursement = loanAccountInfoDto.isInterestDeductedAtDisbursement();
            Double interest = loanAccountInfoDto.getInterest();
            Short gracePeriod = loanAccountInfoDto.getGracePeriod();

            List<AccountFeesEntity> fees = new ArrayList<AccountFeesEntity>();
            List<CreateAccountFeeDto> accouontFees = loanAccountInfoDto.getFees();
            for (CreateAccountFeeDto accountFee : accouontFees) {
                FeeBO feeEntity = feeDao.findById(accountFee.getFeeId().shortValue());
                Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(accountFee.getAmount());
                fees.add(new AccountFeesEntity(null, feeEntity, feeAmount));
            }

            Double maxLoanAmount = Double.valueOf(loanAccountInfoDto.getMaxLoanAmount());
            Double minLoanAmount = Double.valueOf(loanAccountInfoDto.getMinLoanAmount());
            Short maxNumOfInstallments = loanAccountInfoDto.getMaxNumOfInstallments();
            Short minNumOfShortInstallments = loanAccountInfoDto.getMinNumOfInstallments();
            String externalId = loanAccountInfoDto.getExternalId();
            Integer selectedLoanPurpose = loanAccountInfoDto.getSelectedLoanPurpose();
            String collateralNote = loanAccountInfoDto.getCollateralNote();
            Integer selectedCollateralType = loanAccountInfoDto.getSelectedCollateralType();

            AccountState accountState = null;
            Short accountStateValue = loanAccountInfoDto.getAccountState();
            if (accountStateValue != null) {
                accountState = AccountState.fromShort(accountStateValue);
            } else {
                accountState = AccountState.LOAN_PARTIAL_APPLICATION;
            }

            FundBO fund = null;
            Short fundId = loanAccountInfoDto.getFundId();
            if (fundId != null) {
                fund = this.fundDao.findById(fundId);
            }

            LoanBO redoLoan = LoanBO.redoLoan(userContext, loanOffering, customer, accountState, loanAmount,
                    numOfInstallments, loanAccountInfoDto.getDisbursementDate().toDateMidnight().toDate(),
                    isInterestDeductedAtDisbursement, interest, gracePeriod,
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

            copyInstallmentSchedule(installmentDtos, userContext, redoLoan);

            for (LoanPaymentDto payment : existingLoanPayments) {
                if (StringUtils.isNotBlank(payment.getAmount()) && payment.getPaymentDate() != null) {
                    if (!customer.getCustomerMeeting().getMeeting().isValidMeetingDate(payment.getPaymentDate().toDateMidnight().toDate(),
                            DateUtils.getLastDayOfNextYear())) {
                        throw new BusinessRuleException("errors.invalidTxndate");
                    }
                    Money totalAmount = new Money(loanOffering.getCurrency(), payment.getAmount());
                    PersonnelBO personnel  = this.personnelDao.findPersonnelById(payment.getPaidByUserId());
                    Short paymentId = payment.getPaymentTypeId().shortValue();
                    Date transactionDate = payment.getPaymentDate().toDateMidnight().toDate();
                    PaymentData paymentData = new PaymentData(totalAmount, personnel, paymentId, transactionDate);
                    redoLoan.applyPayment(paymentData);
                }
            }
            return redoLoan;
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
    public LoanCreationResultDto createLoan(CreateLoanAccount loanAccountInfo, List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow) {

        return createLoanAccount(loanAccountInfo, new ArrayList<LoanPaymentDto>(), questionGroups, loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), new ArrayList<GroupMemberAccountDto>(), false);
    }
    
    @Override
    public LoanCreationResultDto createLoan(CreateLoanAccount loanAccountInfo,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow, 
            List<DateTime> loanScheduleInstallmentDates, List<Number> totalInstallmentAmounts) {

        return createLoanAccount(loanAccountInfo, new ArrayList<LoanPaymentDto>(), questionGroups, loanAccountCashFlow, loanScheduleInstallmentDates, totalInstallmentAmounts, new ArrayList<GroupMemberAccountDto>(), false);
    }
    
    @Override
    public LoanCreationResultDto createGroupLoanWithIndividualMonitoring(CreateGlimLoanAccount glimLoanAccount,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow) {

        return createLoanAccount(glimLoanAccount.getGroupLoanAccountDetails(), new ArrayList<LoanPaymentDto>(), questionGroups, loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), glimLoanAccount.getMemberDetails(), false);
    }
    
    @Override
    public LoanCreationResultDto createBackdatedGroupLoanWithIndividualMonitoring(
            CreateGlimLoanAccount glimLoanAccount, List<LoanPaymentDto> backdatedLoanPayments,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow) {
        
        CreateLoanAccount loanAccountInfo = glimLoanAccount.getGroupLoanAccountDetails();
        
        return createLoanAccount(loanAccountInfo, backdatedLoanPayments, questionGroups, 
                loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), glimLoanAccount.getMemberDetails(), true);
    }
    
    @Override
    public LoanCreationResultDto createBackdatedLoan(CreateLoanAccount loanAccountInfo,
            List<LoanPaymentDto> backdatedLoanPayments, List<QuestionGroupDetail> questionGroups,
            LoanAccountCashFlow loanAccountCashFlow, List<DateTime> loanScheduleInstallmentDates,
            List<Number> installmentPrincipalAmounts) {
        return createLoanAccount(loanAccountInfo, backdatedLoanPayments, questionGroups, 
                loanAccountCashFlow, loanScheduleInstallmentDates, installmentPrincipalAmounts, new ArrayList<GroupMemberAccountDto>(), true);
    }
    
    @Override
    public LoanCreationResultDto createBackdatedLoan(CreateLoanAccount loanAccountInfo,
            List<LoanPaymentDto> backdatedLoanPayments, List<QuestionGroupDetail> questionGroups,
            LoanAccountCashFlow loanAccountCashFlow) {
        return createLoanAccount(loanAccountInfo, backdatedLoanPayments, questionGroups, 
                loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), new ArrayList<GroupMemberAccountDto>(), true);
    }
    
    private LoanCreationResultDto createLoanAccount(CreateLoanAccount loanAccountInfo, List<LoanPaymentDto> backdatedLoanPayments,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow, List<DateTime> loanScheduleInstallmentDates,
            List<Number> totalInstallmentAmounts, List<GroupMemberAccountDto> memberDetails, boolean isBackdatedLoan) {
        
        // 1. assemble loan details
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        OfficeBO userOffice = this.officeDao.findOfficeById(user.getBranchId());
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfo.getCustomerId());
        if (customer.isGroup()) {
            customer = this.customerDao.findGroupBySystemId(customer.getGlobalCustNum());
        }

        // assemble
        LoanAccountDetail loanAccountDetail = assembleLoanAccountDetail(loanAccountInfo);

        List<AccountFeesEntity> accountFeeEntities = assembleAccountFees(loanAccountInfo.getAccountFees());
        LoanProductOverridenDetail overridenDetail = new LoanProductOverridenDetail(loanAccountDetail.getLoanAmount(), loanAccountInfo.getDisbursementDate(),
                loanAccountInfo.getInterestRate(), loanAccountInfo.getNumberOfInstallments(), loanAccountInfo.getGraceDuration(), accountFeeEntities);

        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        boolean loanScheduleIndependentOfCustomerMeetingEnabled = loanAccountInfo.isRepaymentScheduleIndependentOfCustomerMeeting();
        LoanScheduleConfiguration configuration = new LoanScheduleConfiguration(loanScheduleIndependentOfCustomerMeetingEnabled, interestDays);

        MeetingBO repaymentDayMeeting = loanAccountDetail.getCustomer().getCustomerMeetingValue();
        if (loanScheduleIndependentOfCustomerMeetingEnabled) {
            repaymentDayMeeting = this.createNewMeetingForRepaymentDay(loanAccountInfo.getDisbursementDate(), loanAccountInfo, loanAccountDetail.getCustomer());
        }

        List<DateTime> loanScheduleDates = new ArrayList<DateTime>(loanScheduleInstallmentDates);

        LoanSchedule loanSchedule = assembleLoanSchedule(loanAccountDetail.getCustomer(), loanAccountDetail.getLoanProduct(), overridenDetail, configuration, repaymentDayMeeting, userOffice, loanScheduleDates, loanAccountInfo.getDisbursementDate(), totalInstallmentAmounts);

        // 2. create loan
        InstallmentRange installmentRange = new MaxMinNoOfInstall(loanAccountInfo.getMinAllowedNumberOfInstallments().shortValue(),
                loanAccountInfo.getMaxAllowedNumberOfInstallments().shortValue(), null);
        AmountRange amountRange = new MaxMinLoanAmount(loanAccountInfo.getMaxAllowedLoanAmount().doubleValue(), loanAccountInfo.getMinAllowedLoanAmount().doubleValue(), null);

        DateTime creationDate = new DateTime();
        if (isBackdatedLoan) {
            creationDate = loanAccountInfo.getDisbursementDate().toDateMidnight().toDateTime();
        }
        CreationDetail creationDetail = new CreationDetail(creationDate, Integer.valueOf(user.getUserId()));
        LoanBO loan = LoanBO.openStandardLoanAccount(loanAccountDetail.getLoanProduct(), loanAccountDetail.getCustomer(), repaymentDayMeeting,
                loanSchedule, loanAccountDetail.getAccountState(), loanAccountDetail.getFund(), overridenDetail, configuration, installmentRange, amountRange,
                creationDetail, createdBy);
        loan.setBusinessActivityId(loanAccountInfo.getLoanPurposeId());
        loan.setExternalId(loanAccountInfo.getExternalId());
        loan.setCollateralNote(loanAccountInfo.getCollateralNotes());
        loan.setCollateralTypeId(loanAccountInfo.getCollateralTypeId());

        if (isBackdatedLoan) {
            loan.markAsCreatedWithBackdatedPayments();
        }
        
        try {
            transactionHelper.startTransaction();
            this.loanDao.save(loan);
            transactionHelper.flushSession();
            try {
                loan.setGlobalAccountNum(loan.generateId(userOffice.getGlobalOfficeNum()));
            } catch (AccountException e) {
                throw new BusinessRuleException(e.getMessage());
            }
            this.loanDao.save(loan);
            transactionHelper.flushSession();
            
            // for GLIM loans only
            List<GroupMemberLoanDetail> individualMembersOfGroupLoan = new ArrayList<GroupMemberLoanDetail>();
            for (GroupMemberAccountDto groupMemberAccount : memberDetails) {
                ClientBO member = this.customerDao.findClientBySystemId(groupMemberAccount.getGlobalId());
                Money loanAmount = new Money(loanAccountDetail.getLoanProduct().getCurrency(), groupMemberAccount.getLoanAmount());
                LoanProductOverridenDetail memberOverridenDetail = new LoanProductOverridenDetail(loanAmount, new ArrayList<AccountFeesEntity>(), overridenDetail);

                LoanSchedule memberSchedule = assembleLoanSchedule(member, loanAccountDetail.getLoanProduct(), memberOverridenDetail, configuration, repaymentDayMeeting, userOffice, new ArrayList<DateTime>(), loanAccountInfo.getDisbursementDate(), new ArrayList<Number>());

                GroupMemberLoanDetail groupMemberLoanDetail = new GroupMemberLoanDetail(member, memberOverridenDetail, memberSchedule, groupMemberAccount.getLoanPurposeId());
                individualMembersOfGroupLoan.add(groupMemberLoanDetail);
            }
            
            for (GroupMemberLoanDetail groupMemberAccount : individualMembersOfGroupLoan) {

                LoanBO memberLoan = LoanBO.openGroupMemberLoanAccount(loan, loanAccountDetail.getLoanProduct(), groupMemberAccount.getMember(), repaymentDayMeeting, groupMemberAccount.getMemberSchedule(), groupMemberAccount.getMemberOverridenDetail(), configuration,
                        installmentRange, amountRange, creationDetail, createdBy);
                if (groupMemberAccount.getLoanPurposeId() > 0) {
                    memberLoan.setBusinessActivityId(groupMemberAccount.getLoanPurposeId());
                }
                if (!backdatedLoanPayments.isEmpty()) {
                    memberLoan.markAsCreatedWithBackdatedPayments();
                }                
                this.loanDao.save(memberLoan);
                transactionHelper.flushSession();
                try {
                    memberLoan.setGlobalAccountNum(memberLoan.generateId(userOffice.getGlobalOfficeNum()));
                } catch (AccountException e) {
                    throw new BusinessRuleException(e.getMessage());
                }
                this.loanDao.save(memberLoan);
                transactionHelper.flushSession();
            }

            // save question groups
            if (!questionGroups.isEmpty()) {
                Integer eventSourceId = questionnaireServiceFacade.getEventSourceId("Create", "Loan");
                QuestionGroupDetails questionGroupDetails = new QuestionGroupDetails(
                        Integer.valueOf(user.getUserId()).shortValue(), loan.getAccountId(), eventSourceId, questionGroups);
                questionnaireServiceFacade.saveResponses(questionGroupDetails);
                transactionHelper.flushSession();
            }

            if (loanAccountCashFlow != null && !loanAccountCashFlow.getMonthlyCashFlow().isEmpty()) {

                List<MonthlyCashFlowDetail> monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
                for (MonthlyCashFlowDto monthlyCashFlow : loanAccountCashFlow.getMonthlyCashFlow()) {
                    MonthlyCashFlowDetail monthlyCashFlowDetail = new MonthlyCashFlowDetail(monthlyCashFlow.getMonthDate(),
                            monthlyCashFlow.getRevenue(), monthlyCashFlow.getExpenses(), monthlyCashFlow.getNotes());

                    monthlyCashFlowDetails.add(monthlyCashFlowDetail);
                }

                org.mifos.platform.cashflow.service.CashFlowDetail cashFlowDetail = new org.mifos.platform.cashflow.service.CashFlowDetail(monthlyCashFlowDetails);
                cashFlowDetail.setTotalCapital(loanAccountCashFlow.getTotalCapital());
                cashFlowDetail.setTotalLiability(loanAccountCashFlow.getTotalLiability());
                cashFlowService.save(cashFlowDetail);
                transactionHelper.flushSession();
            }

            if (isBackdatedLoan) {
                // 3. auto approve loan
                String comment = "Automatic Status Update (Redo Loan)";
                LocalDate approvalDate = loanAccountInfo.getDisbursementDate();
                loan.approve(createdBy, comment, approvalDate);

                // 4. disburse loan
                String receiptNumber = null;
                Date receiptDate = null;
                PaymentTypeEntity paymentType = new PaymentTypeEntity(PaymentTypes.CASH.getValue());
                Date paymentDate = loanAccountInfo.getDisbursementDate().toDateMidnight().toDate();
                AccountPaymentEntity disbursalPayment = new AccountPaymentEntity(loan, loan.getLoanAmount(),
                        receiptNumber, receiptDate, paymentType, paymentDate);
                disbursalPayment.setCreatedByUser(createdBy);

                // refactoring of loan disbursal
                if (customer.isDisbursalPreventedDueToAnyExistingActiveLoansForTheSameProduct(loan.getLoanOffering())) {
                    throw new AccountException("errors.cannotDisburseLoan.because.otherLoansAreActive");
                }

                try {
                    loan.updateCustomer(customer);
                    new ProductMixValidator().checkIfProductsOfferingCanCoexist(loan);
                } catch (ServiceException e1) {
                    throw new AccountException(e1.getMessage());
                }

                loan.disburse(createdBy, disbursalPayment);

                customer.updatePerformanceHistoryOnDisbursement(loan, loan.getLoanAmount());
                // end of refactoring of loan disbural

                this.loanDao.save(loan);
                transactionHelper.flushSession();

                // 5. apply each payment
                Short paymentId = PaymentTypes.CASH.getValue();
                for (LoanPaymentDto loanPayment : backdatedLoanPayments) {
                    Money amountPaidToDate = new Money(loan.getCurrency(), loanPayment.getAmount());
                    PaymentData paymentData = new PaymentData(amountPaidToDate, createdBy, paymentId, loanPayment
                            .getPaymentDate().toDateMidnight().toDate());
                    loan.applyPayment(paymentData);
                    this.loanDao.save(loan);
                }
            }
            transactionHelper.commitTransaction();

            return new LoanCreationResultDto(false, loan.getAccountId(), loan.getGlobalAccountNum());
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }
    
    private List<AccountFeesEntity> assembleAccountFees(List<CreateAccountFeeDto> defaultAccountFees) {
        List<AccountFeesEntity> accountFeeEntities = new ArrayList<AccountFeesEntity>();
        for (CreateAccountFeeDto defaultFee : defaultAccountFees) {
            FeeBO fee = this.feeDao.findById(defaultFee.getFeeId().shortValue());
            AccountFeesEntity deafultAccountFeeEntity = new AccountFeesEntity(null, fee, Double.valueOf(defaultFee.getAmount()));
            accountFeeEntities.add(deafultAccountFeeEntity);
        }
        return accountFeeEntities;
    }
    
    @Override
    public LoanCreationResultDto createLoan(LoanAccountMeetingDto loanAccountMeetingDto,
                                            LoanAccountInfoDto loanAccountInfo,
                                            List<LoanScheduledInstallmentDto> loanRepayments) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());

        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfo.getCustomerId());
        boolean isGlimApplicable = new ConfigurationPersistence().isGlimEnabled() && customer.isGroup();

        if (!isPermissionAllowed(loanAccountInfo.getAccountState(), userContext, customer.getOffice().getOfficeId(),
                customer.getPersonnel().getPersonnelId())) {
            throw new BusinessRuleException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }

        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

        MeetingBO newMeetingForRepaymentDay = null;
        if (isRepaymentIndependentOfMeetingEnabled) {
            newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(loanAccountInfo.getDisbursementDate(), loanAccountMeetingDto, customer);
        }

        FundBO fund = null;
        if (loanAccountInfo.getFundId() != null) {
            fund = this.fundDao.findById(loanAccountInfo.getFundId());
        }

        LoanBO loan = assembleLoan(userContext, customer, loanAccountInfo.getDisbursementDate(), fund,
                                   isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay, loanAccountInfo);

        copyInstallmentSchedule(loanRepayments, userContext, loan);

        try {
            StaticHibernateUtil.startTransaction();
            PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan
                    .getAccountState(), createdBy, loan));
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            loan.setGlobalAccountNum(loan.generateId(userOffice.getGlobalOfficeNum()));
            this.loanDao.save(loan);
            StaticHibernateUtil.commitTransaction();
        } catch (AccountException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

        return new LoanCreationResultDto(isGlimApplicable, loan.getAccountId(), loan.getGlobalAccountNum());
    }

    private void copyInstallmentSchedule(List<LoanScheduledInstallmentDto> loanRepayments, UserContext userContext, LoanBO loan) {
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        for (LoanScheduledInstallmentDto installment : loanRepayments) {
            RepaymentScheduleInstallment repaymentScheduleInstallment = RepaymentScheduleInstallment.createForScheduleCopy(
                                                                        installment.getInstallmentNumber(),
                                                                        installment.getPrincipal(), installment.getInterest(),
                                                                        installment.getDueDate(), userContext.getPreferredLocale(),
                                                                        loan.getCurrency());
            installments.add(repaymentScheduleInstallment);
        }

        loan.updateInstallmentSchedule(installments);
    }

    private MeetingBO createNewMeetingForRepaymentDay(LocalDate disbursementDate, RecurringSchedule recurringSchedule, CustomerBO customer) {
        MeetingBO newMeetingForRepaymentDay = null;

        final int minDaysInterval = new ConfigurationPersistence().getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final Date repaymentStartDate = disbursementDate.plusDays(minDaysInterval).toDateMidnight().toDateTime().toDate();
        try {
            if (recurringSchedule.isWeekly()) {
                WeekDay weekDay = WeekDay.getWeekDay(recurringSchedule.getDay().shortValue());
                Short recurEvery = recurringSchedule.getEvery().shortValue();
                newMeetingForRepaymentDay = new MeetingBO(weekDay, recurEvery, repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
            } else if (recurringSchedule.isMonthly()) {
                if (recurringSchedule.isMonthlyOnDayOfMonth()) {
                    Short dayOfMonth = recurringSchedule.getDay().shortValue();
                    Short dayRecurMonth = recurringSchedule.getEvery().shortValue();
                    newMeetingForRepaymentDay = new MeetingBO(dayOfMonth, dayRecurMonth, repaymentStartDate,
                            MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
                } else {
                    Short weekOfMonth = recurringSchedule.getDay().shortValue();
                    Short monthRank = recurringSchedule.getWeek().shortValue();
                    Short everyMonth = recurringSchedule.getEvery().shortValue();
                    
                    newMeetingForRepaymentDay = new MeetingBO(weekOfMonth, everyMonth , repaymentStartDate,
                            MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace(),
                            monthRank);
                }
            }
            return newMeetingForRepaymentDay;
        } catch (NumberFormatException nfe) {
            throw new MifosRuntimeException(nfe);
        } catch (MeetingException me) {
            throw new BusinessRuleException(me.getKey(), me);
        }

    }
    
    private MeetingBO createNewMeetingForRepaymentDay(LocalDate disbursementDate,
            final LoanAccountMeetingDto meetingDto, final CustomerBO customer) {

        MeetingBO newMeetingForRepaymentDay = null;
        Short recurrenceId = Short.valueOf(meetingDto.getRecurrenceId());

        final int minDaysInterval = new ConfigurationPersistence().getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final Date repaymentStartDate = disbursementDate.plusDays(minDaysInterval).toDateMidnight().toDateTime().toDate();
        try {
            if (RecurrenceType.WEEKLY.getValue().equals(recurrenceId)) {
                WeekDay weekDay = WeekDay.getWeekDay(Short.valueOf(meetingDto.getWeekDay()));
                Short recurEvery = Short.valueOf(meetingDto.getEveryWeek());
                newMeetingForRepaymentDay = new MeetingBO(weekDay, recurEvery, repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
            } else if (RecurrenceType.MONTHLY.getValue().equals(recurrenceId)) {
                if (meetingDto.getMonthType().equals("1")) {
                    Short dayOfMonth = Short.valueOf(meetingDto.getDayOfMonth());
                    Short dayRecurMonth = Short.valueOf(meetingDto.getDayRecurMonth());
                    newMeetingForRepaymentDay = new MeetingBO(dayOfMonth, dayRecurMonth, repaymentStartDate,
                            MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
                } else {
                    Short weekOfMonth = Short.valueOf(meetingDto.getWeekOfMonth());
                    Short everyMonth = Short.valueOf(meetingDto.getEveryMonth());
                    Short monthRank = Short.valueOf(meetingDto.getMonthRank());
                    newMeetingForRepaymentDay = new MeetingBO(weekOfMonth, everyMonth , repaymentStartDate,
                            MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace(),
                            monthRank);
                }
            }
            return newMeetingForRepaymentDay;
        } catch (NumberFormatException nfe) {
            throw new MifosRuntimeException(nfe);
        } catch (MeetingException me) {
            throw new BusinessRuleException(me.getKey(), me);
        }
    }

    private LoanBO assembleLoan(UserContext userContext, CustomerBO customer, LocalDate disbursementDate, FundBO fund,
            boolean isRepaymentIndependentOfMeetingEnabled, MeetingBO newMeetingForRepaymentDay,
            LoanAccountInfoDto loanActionForm) {

        try {
            Short productId = loanActionForm.getProductId();
            LoanOfferingBO loanOffering = this.loanProductDao.findById(productId.intValue());

            Money loanAmount = new Money(loanOffering.getCurrency(), loanActionForm.getLoanAmount());
            Short numOfInstallments = loanActionForm.getNumOfInstallments();
            boolean isInterestDeductedAtDisbursement = loanActionForm.isInterestDeductedAtDisbursement();
            Double interest = loanActionForm.getInterest();
            Short gracePeriod = loanActionForm.getGracePeriod();

            List<AccountFeesEntity> fees = new ArrayList<AccountFeesEntity>();
            List<CreateAccountFeeDto> accouontFees = loanActionForm.getFees();
            for (CreateAccountFeeDto accountFee : accouontFees) {
                FeeBO feeEntity = feeDao.findById(accountFee.getFeeId().shortValue());
                Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(accountFee.getAmount());
                fees.add(new AccountFeesEntity(null, feeEntity, feeAmount));
            }

            Double maxLoanAmount = Double.valueOf(loanActionForm.getMaxLoanAmount());
            Double minLoanAmount = Double.valueOf(loanActionForm.getMinLoanAmount());
            Short maxNumOfInstallments = loanActionForm.getMaxNumOfInstallments();
            Short minNumOfShortInstallments = loanActionForm.getMinNumOfInstallments();
            String externalId = loanActionForm.getExternalId();
            Integer selectedLoanPurpose = loanActionForm.getSelectedLoanPurpose();
            String collateralNote = loanActionForm.getCollateralNote();
            Integer selectedCollateralType = loanActionForm.getSelectedCollateralType();
            Short accountState = loanActionForm.getAccountState();

            AccountState accountStateType = null;
            if (accountState != null) {
                accountStateType = AccountState.fromShort(accountState);
            }
            if (accountStateType == null) {
                accountStateType = AccountState.LOAN_PARTIAL_APPLICATION;
            }

            LoanBO loan = LoanBO.createLoan(userContext, loanOffering, customer, accountStateType, loanAmount,
                    numOfInstallments, disbursementDate.toDateMidnight().toDate(), isInterestDeductedAtDisbursement, interest, gracePeriod,
                    fund, fees, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
                    minNumOfShortInstallments, isRepaymentIndependentOfMeetingEnabled, newMeetingForRepaymentDay);

            loan.setExternalId(externalId);
            loan.setBusinessActivityId(selectedLoanPurpose);
            loan.setCollateralNote(collateralNote);
            loan.setCollateralTypeId(selectedCollateralType);

            return loan;
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }
    
    @Override
    public LoanDisbursalDto retrieveLoanDisbursalDetails(Integer loanAccountId) {

        try {
            LoanBO loan = this.loanDao.findById(loanAccountId);
            new ProductMixValidator().checkIfProductsOfferingCanCoexist(loan);

            Date proposedDate = new DateTimeService().getCurrentJavaDateTime();
            boolean backDatedTransactionsAllowed = AccountingRules.isBackDatedTxnAllowed();
            if (backDatedTransactionsAllowed) {
                proposedDate = loan.getDisbursementDate();
            }

            Short currencyId = Short.valueOf("0");
            boolean multiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
            if (multiCurrencyEnabled) {
                currencyId = loan.getCurrency().getCurrencyId();
            }

            boolean repaymentIndependentOfMeetingSchedule = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            return new LoanDisbursalDto(loan.getAccountId(), proposedDate, loan.getLoanAmount().toString(), loan.getAmountTobePaidAtdisburtail().toString(),
                    backDatedTransactionsAllowed, repaymentIndependentOfMeetingSchedule, multiCurrencyEnabled, currencyId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e.getValues());
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
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

        InstallmentDetailsDto viewUpcomingInstallmentDetails = getUpcomingInstallmentDetails(loanBO.getDetailsOfNextInstallment(), loanBO.getCurrency());
        InstallmentDetailsDto viewOverDueInstallmentDetails = getOverDueInstallmentDetails(loanBO.getDetailsOfInstallmentsInArrears(), loanBO.getCurrency());

        Money upcomingInstallmentSubTotal = new Money(loanBO.getCurrency(), viewUpcomingInstallmentDetails.getSubTotal());
        Money overdueInstallmentSubTotal = new Money(loanBO.getCurrency(), viewOverDueInstallmentDetails.getSubTotal());
        Money totalAmountDue = upcomingInstallmentSubTotal.add(overdueInstallmentSubTotal);

        return new LoanInstallmentDetailsDto(viewUpcomingInstallmentDetails, viewOverDueInstallmentDetails,
                totalAmountDue.toString(), loanBO.getNextMeetingDate());
    }

    private InstallmentDetailsDto getUpcomingInstallmentDetails(
            final AccountActionDateEntity upcomingAccountActionDate, final MifosCurrency currency) {
        if (upcomingAccountActionDate != null) {
            LoanScheduleEntity upcomingInstallment = (LoanScheduleEntity) upcomingAccountActionDate;
            Money subTotal = upcomingInstallment.getPrincipalDue().add(upcomingInstallment.getInterestDue()).add(upcomingInstallment.getTotalFeesDueWithMiscFee()).add(upcomingInstallment.getPenaltyDue());
            return new InstallmentDetailsDto(upcomingInstallment.getPrincipalDue().toString(), upcomingInstallment
                    .getInterestDue().toString(), upcomingInstallment.getTotalFeeDueWithMiscFeeDue().toString(), upcomingInstallment
                    .getPenaltyDue().toString(), subTotal.toString());
        }
        String zero = new Money(currency).toString();
        return new InstallmentDetailsDto(zero, zero, zero, zero, zero);
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
        Money subTotal = principalDue.add(interestDue).add(feesDue).add(penaltyDue);
        return new InstallmentDetailsDto(principalDue.toString(), interestDue.toString(), feesDue.toString(), penaltyDue.toString(), subTotal.toString());
    }

    @Override
    public boolean isTrxnDateValid(Integer loanAccountId, Date trxnDate) {

        try {
            LoanBO loan = this.loanDao.findById(loanAccountId);

            Date meetingDate = new CustomerPersistence().getLastMeetingDateForCustomer(loan.getCustomer().getCustomerId());
            boolean repaymentIndependentOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
            return loan.isTrxnDateValid(trxnDate, meetingDate, repaymentIndependentOfMeetingEnabled);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void makeEarlyRepayment(RepayLoanInfoDto repayLoanInfoDto) {

        try {
            LoanBO loan = this.loanDao.findByGlobalAccountNum(repayLoanInfoDto.getGlobalAccountNum());
            if (repayLoanInfoDto.isWaiveInterest() && !loan.isInterestWaived()) {
                throw new BusinessRuleException(LoanConstants.WAIVER_INTEREST_NOT_CONFIGURED);
            }
            Money earlyRepayAmount = new Money(loan.getCurrency(), repayLoanInfoDto.getEarlyRepayAmount());
            loanBusinessService.computeExtraInterest(loan, repayLoanInfoDto.getDateOfPayment());
            BigDecimal interestDueForCurrentInstallment =
                    interestDueForNextInstallment(repayLoanInfoDto.getTotalRepaymentAmount(),
                    repayLoanInfoDto.getWaivedAmount(),loan,repayLoanInfoDto.isWaiveInterest());
            loan.makeEarlyRepayment(earlyRepayAmount, repayLoanInfoDto.getReceiptNumber(),
                    repayLoanInfoDto.getReceiptDate(), repayLoanInfoDto.getPaymentTypeId(), repayLoanInfoDto.getId(),
                    repayLoanInfoDto.isWaiveInterest(), new Money(loan.getCurrency(), interestDueForCurrentInstallment));
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    BigDecimal interestDueForNextInstallment(BigDecimal totalRepaymentAmount, BigDecimal waivedAmount,
                                             LoanBO loan, boolean waiveInterest) {
        BigDecimal result = BigDecimal.ZERO;
        if (!waiveInterest) {
            if (loan.isDecliningBalanceInterestRecalculation()) {
                result = totalRepaymentAmount.subtract(waivedAmount);
            } else {
                AccountActionDateEntity nextInstallment = loan.getDetailsOfNextInstallment();
                if (nextInstallment != null) {
                    LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) nextInstallment;
                    result = loanScheduleEntity.getInterestDue().getAmount();
                }
            }
        }
        return result;
    }

    @Override
    public LoanInformationDto retrieveLoanInformation(String globalAccountNum) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        String fundName = null;
        if (loan.getFund() != null) {
            fundName = loan.getFund().getFundName();
        }

//        boolean activeSurveys = surveysPersistence.isActiveSurveysForSurveyType(SurveyType.LOAN);
        boolean activeSurveys = false;
        List<SurveyDto> accountSurveys = loanDao.getAccountSurveyDto(loan.getAccountId());

        LoanSummaryDto loanSummary = new LoanSummaryDto(loan.getLoanSummary().getOriginalPrincipal().toString(), loan.getLoanSummary().getPrincipalPaid().toString(),
                                                        loan.getLoanSummary().getPrincipalDue().toString(), loan.getLoanSummary().getOriginalInterest().toString(),
                                                        loan.getLoanSummary().getInterestPaid().toString(), loan.getLoanSummary().getInterestDue().toString(),
                                                        loan.getLoanSummary().getOriginalFees().toString(), loan.getLoanSummary().getFeesPaid().toString(),
                                                        loan.getLoanSummary().getFeesDue().toString(), loan.getLoanSummary().getOriginalPenalty().toString(),
                                                        loan.getLoanSummary().getPenaltyPaid().toString(), loan.getLoanSummary().getPenaltyDue().toString(),
                                                        loan.getLoanSummary().getTotalLoanAmnt().toString(), loan.getLoanSummary().getTotalAmntPaid().toString(),
                                                        loan.getLoanSummary().getTotalAmntDue().toString());

        LoanPerformanceHistoryEntity performanceHistory = loan.getPerformanceHistory();
        LoanPerformanceHistoryDto loanPerformanceHistory = new LoanPerformanceHistoryDto(performanceHistory.getNoOfPayments(),
                                                                                        performanceHistory.getTotalNoOfMissedPayments(),
                                                                                        performanceHistory.getDaysInArrears(),
                                                                                        performanceHistory.getLoanMaturityDate());

        Set<AccountFeesDto> accountFeesDtos = new HashSet<AccountFeesDto>();
        if(!loan.getAccountFees().isEmpty()) {
            for (AccountFeesEntity accountFeesEntity: loan.getAccountFees()) {
                AccountFeesDto accountFeesDto = new AccountFeesDto(accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId(),
                                                                  accountFeesEntity.getFeeStatus(), accountFeesEntity.getFees().getFeeName(),
                                                                  accountFeesEntity.getAccountFeeAmount().toString(),
                                                                  getMeetingRecurrence(accountFeesEntity.getFees().getFeeFrequency()
                                                                          .getFeeMeetingFrequency(), userContext),
                                                                  accountFeesEntity.getFees().getFeeId());
                accountFeesDtos.add(accountFeesDto);
            }
        }

        Set<String> accountFlagNames = getAccountStateFlagEntityNames(loan.getAccountFlags());
        Short accountStateId = loan.getAccountState().getId();
        String accountStateName = getAccountStateName(accountStateId);
        boolean disbursed = AccountState.isDisbursed(accountStateId);
        String gracePeriodTypeName = getGracePeriodTypeName(loan.getGracePeriodType().getId());
        String interestTypeName = getInterestTypeName(loan.getInterestType().getId());

        return new LoanInformationDto(loan.getLoanOffering().getPrdOfferingName(), globalAccountNum, accountStateId,
                                     accountStateName, disbursed, accountFlagNames, loan.getDisbursementDate(), loan.isRedone(),
                                     loan.getBusinessActivityId(), loan.getAccountId(),gracePeriodTypeName, interestTypeName,
                                     loan.getCustomer().getCustomerId(), loan.getAccountType().getAccountTypeId(),
                                     loan.getOffice().getOfficeId(), loan.getPersonnel().getPersonnelId(), loan.getNextMeetingDate(),
                                     loan.getTotalAmountDue().toString(), loan.getTotalAmountInArrears().toString(), loanSummary,
                                     loan.getLoanActivityDetails().isEmpty()? false: true, loan.getInterestRate(),
                                     loan.isInterestDeductedAtDisbursement(),
                                     loan.getLoanOffering().getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurAfter(),
                                     loan.getLoanOffering().getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId(),
                                     loan.getLoanOffering().isPrinDueLastInst(), loan.getNoOfInstallments(),
                                     loan.getMaxMinNoOfInstall().getMinNoOfInstall(), loan.getMaxMinNoOfInstall().getMaxNoOfInstall(),
                                     loan.getGracePeriodDuration(), fundName, loan.getCollateralTypeId(), loan.getCollateralNote(),loan.getExternalId(),
                                     accountFeesDtos, loan.getCreatedDate(), loanPerformanceHistory,
                                     loan.getCustomer().isGroup(), getRecentActivityView(globalAccountNum), activeSurveys, accountSurveys,
                                     loan.getCustomer().getDisplayName(), loan.getCustomer().getGlobalCustNum(), loan.getOffice().getOfficeName());
    }

    private String getMeetingRecurrence(MeetingBO meeting, UserContext userContext) {
        return meeting != null ? new MeetingHelper().getMessageWithFrequency(meeting, userContext) : null;
    }

    private String getAccountStateName(Short id) {
        AccountStateEntity accountStateEntity;
        try {
            accountStateEntity = legacyMasterDao.getPersistentObject(AccountStateEntity.class,
                    id);
            return accountStateEntity.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private String getGracePeriodTypeName(Short id) {
        GracePeriodTypeEntity gracePeriodType;
        try {
            gracePeriodType = legacyMasterDao.getPersistentObject(GracePeriodTypeEntity.class,
                    id);
            return gracePeriodType.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private String getInterestTypeName(Short id) {
        InterestTypesEntity interestType;
        try {
            interestType = legacyMasterDao.getPersistentObject(InterestTypesEntity.class,
                    id);
            return interestType.getLookUpValue().getLookUpName();
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

    private String getAccountStateFlagEntityName(Short id) {
        AccountStateFlagEntity accountStateFlagEntity;
        try {
            accountStateFlagEntity = legacyMasterDao.getPersistentObject(AccountStateFlagEntity.class,
                    id);
            return accountStateFlagEntity.getLookUpValue().getLookUpName();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e.toString());
        }
    }

    private List<LoanActivityDto> getRecentActivityView(final String globalAccountNumber) {
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
        LoanActivityDto loanActivityDto = new LoanActivityDto();
        loanActivityDto.setId(loanActivity.getAccount().getAccountId());
        loanActivityDto.setActionDate(loanActivity.getTrxnCreatedDate());
        loanActivityDto.setActivity(loanActivity.getComments());
        loanActivityDto.setPrincipal(removeSign(loanActivity.getPrincipal()).toString());
        loanActivityDto.setInterest(removeSign(loanActivity.getInterest()).toString());
        loanActivityDto.setPenalty(removeSign(loanActivity.getPenalty()).toString());
        loanActivityDto.setFees(removeSign(loanActivity.getFee()).toString());
        Money total = removeSign(loanActivity.getFee()).add(removeSign(loanActivity.getPenalty())).add(
                removeSign(loanActivity.getPrincipal())).add(removeSign(loanActivity.getInterest()));
        loanActivityDto.setTotal(total.toString());
        loanActivityDto.setTotalValue(total.getAmount().doubleValue());
        loanActivityDto.setTimeStamp(loanActivity.getTrxnCreatedDate());
        loanActivityDto.setRunningBalanceInterest(loanActivity.getInterestOutstanding().toString());
        loanActivityDto.setRunningBalancePrinciple(loanActivity.getPrincipalOutstanding().toString());
        loanActivityDto.setRunningBalanceFees(loanActivity.getFeeOutstanding().toString());
        loanActivityDto.setRunningBalancePenalty(loanActivity.getPenaltyOutstanding().toString());

        loanActivityDto.setRunningBalancePrincipleWithInterestAndFees(loanActivity.getPrincipalOutstanding().add(loanActivity.getInterestOutstanding()).add(loanActivity.getFeeOutstanding()).toString());

        return loanActivityDto;
    }

    private Money removeSign(final Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }

        return amount;
    }

    @Override
    public RepayLoanDto retrieveLoanRepaymentDetails(String globalAccountNumber) {
        LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNumber);
        Money repaymentAmount;
        Money waiverAmount;
        if (loan.isDecliningBalanceInterestRecalculation()) {
            RepaymentResultsHolder repaymentResultsHolder = scheduleCalculatorAdaptor.computeRepaymentAmount(loan, DateUtils.getCurrentDateWithoutTimeStamp());
            repaymentAmount = new Money(loan.getCurrency(), repaymentResultsHolder.getTotalRepaymentAmount());
            waiverAmount = new Money(loan.getCurrency(), repaymentResultsHolder.getWaiverAmount());
        } else {
            repaymentAmount = loan.getEarlyRepayAmount();
            waiverAmount = loan.waiverAmount();
        }
        Money waivedRepaymentAmount = repaymentAmount.subtract(waiverAmount);
        return new RepayLoanDto(repaymentAmount.toString(), waivedRepaymentAmount.toString(), loan.isInterestWaived());
    }

    @Override
    public List<LoanAccountDetailsDto> retrieveLoanAccountDetails(LoanInformationDto loanInformationDto) {

        List<LoanBO> individualLoans = this.loanDao.findIndividualLoans(loanInformationDto.getAccountId());
        List<ValueListElement> allLoanPurposes = this.loanProductDao.findAllLoanPurposes();

        List<LoanAccountDetailsDto> loanAccountDetailsViewList = new ArrayList<LoanAccountDetailsDto>();

        for (LoanBO individualLoan : individualLoans) {
            LoanAccountDetailsDto loandetails = new LoanAccountDetailsDto();
            loandetails.setClientId(individualLoan.getCustomer().getGlobalCustNum());
            loandetails.setClientName(individualLoan.getCustomer().getDisplayName());
            loandetails.setLoanAmount(null != individualLoan.getLoanAmount()
                    && !EMPTY.equals(individualLoan.getLoanAmount().toString()) ? individualLoan.getLoanAmount()
                    .toString() : "0.0");

            if (null != individualLoan.getBusinessActivityId()) {
                loandetails.setBusinessActivity(individualLoan.getBusinessActivityId().toString());
                for (ValueListElement busact : allLoanPurposes) {
                    if (busact.getId().toString().equals(individualLoan.getBusinessActivityId().toString())) {
                        loandetails.setBusinessActivityName(busact.getName());
                    }
                }
            }
            ClientBO client = this.customerDao.findClientBySystemId(individualLoan.getCustomer().getGlobalCustNum());
            String governmentId = client.getGovernmentId();
            loandetails.setGovermentId(StringUtils.isNotBlank(governmentId) ? governmentId : "-");
            loanAccountDetailsViewList.add(loandetails);
        }
        return loanAccountDetailsViewList;
    }

    @Override
    public void disburseLoan(AccountPaymentParametersDto loanDisbursement, Short paymentTypeId) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        PaymentTypeDto paymentType = null;
        try {
            for (org.mifos.dto.domain.PaymentTypeDto paymentTypeDto : accountService.getLoanDisbursementTypes()) {
                if (paymentTypeDto.getValue() == paymentTypeId) {
                    paymentType = paymentTypeDto;
                }
            }
        } catch (Exception e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }

        if (paymentType == null) {
            throw new MifosRuntimeException("Expected loan PaymentTypeDto not found for id: " + paymentTypeId);
        }

        loanDisbursement.setPaymentType(paymentType);

        Date trxnDate = DateUtils.getDateWithoutTimeStamp(loanDisbursement.getPaymentDate().toDateMidnight().toDate());
        if (!isTrxnDateValid(Integer.valueOf(loanDisbursement.getAccountId()), trxnDate)) {
            throw new BusinessRuleException("errors.invalidTxndate");
        }

        List<AccountPaymentParametersDto> loanDisbursements = new ArrayList<AccountPaymentParametersDto>();
        loanDisbursements.add(loanDisbursement);

        try {
            accountService.disburseLoans(loanDisbursements, userContext.getPreferredLocale());
        } catch (Exception e) {
            throw new MifosRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ChangeAccountStatusDto retrieveAllActiveBranchesAndLoanOfficerDetails() {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        List<PersonnelDto> loanOfficers = new ArrayList<PersonnelDto>();
        List<OfficeDetailsDto> activeBranches = this.officeDao.findActiveBranches(userContext.getBranchId());
        if (onlyOneActiveBranchExists(activeBranches)) {
            OfficeDetailsDto singleOffice = activeBranches.get(0);
            CenterCreation officeDetails = new CenterCreation(singleOffice.getOfficeId(), userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
            loanOfficers = this.personnelDao.findActiveLoanOfficersForOffice(officeDetails);
        }

        boolean loanPendingApprovalStateEnabled = ProcessFlowRules.isLoanPendingApprovalStateEnabled();
        Short accountState = AccountState.LOAN_PARTIAL_APPLICATION.getValue();
        if (loanPendingApprovalStateEnabled) {
            accountState = AccountState.LOAN_PENDING_APPROVAL.getValue();
        }

        boolean centerHierarchyExists = ClientRules.getCenterHierarchyExists();

        return new ChangeAccountStatusDto(activeBranches, loanOfficers, loanPendingApprovalStateEnabled, accountState, centerHierarchyExists);
    }

    private boolean onlyOneActiveBranchExists(List<OfficeDetailsDto> activeBranches) {
        return activeBranches.size() == 1;
    }

    @Override
    public ChangeAccountStatusDto retrieveLoanOfficerDetailsForBranch(Short officeId) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        CenterCreation officeDetails = new CenterCreation(officeId, userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
        List<PersonnelDto> loanOfficers = this.personnelDao.findActiveLoanOfficersForOffice(officeDetails);

        boolean loanPendingApprovalStateEnabled = ProcessFlowRules.isLoanPendingApprovalStateEnabled();
        Short accountState = AccountState.LOAN_PARTIAL_APPLICATION.getValue();
        if (loanPendingApprovalStateEnabled) {
            accountState = AccountState.LOAN_PENDING_APPROVAL.getValue();
        }
        boolean centerHierarchyExists = ClientRules.getCenterHierarchyExists();

        return new ChangeAccountStatusDto(new ArrayList<OfficeDetailsDto>(), loanOfficers, loanPendingApprovalStateEnabled, accountState, centerHierarchyExists);
    }

    @Override
    public List<String> updateSeveralLoanAccountStatuses(List<AccountUpdateStatus> accountsForUpdate) {

        List<String> updatedAccountNumbers = new ArrayList<String>();
        for (AccountUpdateStatus accountUpdate : accountsForUpdate) {
            String accountNumber = updateLoanAccountStatus(accountUpdate);
            updatedAccountNumbers.add(accountNumber);
        }

        return updatedAccountNumbers;
    }

    @Override
    public List<LoanActivityDto> retrieveLoanPaymentsForReversal(String globalAccountNum) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        userContext.setOfficeLevelId(userOffice.getOfficeLevel().getValue());

        LoanBO loan = null;
        LoanBO searchedLoan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        if (searchedLoan != null && isAccountUnderUserScope(searchedLoan, userContext)) {
            loan = searchedLoan;
        }

        if (loan == null) {
            throw new BusinessRuleException(LoanConstants.NOSEARCHRESULTS);
        }

        if (!loan.isAccountActive()) {
            throw new BusinessRuleException(LoanConstants.NOSEARCHRESULTS);
        }

        return getApplicablePayments(loan);
    }

    private boolean isAccountUnderUserScope(LoanBO loan, UserContext userContext) {
        if (userContext.getLevelId().equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
            return (loan.getPersonnel().getPersonnelId().equals(userContext.getId()));
        }

        if (userContext.getOfficeLevelId().equals(OfficeLevel.BRANCHOFFICE.getValue())) {
            return (loan.getOffice().getOfficeId().equals(userContext.getBranchId()));
        }

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        return (userOffice.isParent(loan.getOffice()));
    }

    private List<LoanActivityDto> getApplicablePayments(LoanBO loan) {

        List<LoanActivityDto> payments = new ArrayList<LoanActivityDto>();
        List<AccountPaymentEntity> accountPayments = loan.getAccountPayments();
        int i = accountPayments.size() - 1;
        if (accountPayments.size() > 0) {
            for (AccountPaymentEntity accountPayment : accountPayments) {
                if (accountPayment.getAmount().isGreaterThanZero()) {
                    Money amount = new Money(accountPayment.getAmount().getCurrency());
                    if (i == 0) {
                        for (AccountTrxnEntity accountTrxn : accountPayment.getAccountTrxns()) {
                            short accountActionTypeId = accountTrxn.getAccountActionEntity().getId().shortValue();
                            boolean isLoanRepayment = accountActionTypeId == AccountActionTypes.LOAN_REPAYMENT
                                    .getValue();
                            boolean isFeeRepayment = accountActionTypeId == AccountActionTypes.FEE_REPAYMENT.getValue();
                            if (isLoanRepayment || isFeeRepayment) {
                                amount = amount.add(accountTrxn.getAmount());
                            }
                        }
                    } else {
                        amount = accountPayment.getAmount();
                    }
                    if (amount.isGreaterThanZero()) {
                        LoanActivityDto loanActivityDto = new LoanActivityDto();
                        loanActivityDto.setActionDate(accountPayment.getPaymentDate());
                        loanActivityDto.setTotal(amount.toString());
                        loanActivityDto.setTotalValue(amount.getAmount().doubleValue());
                        payments.add(0, loanActivityDto);
                    }
                }
                i--;
            }
        }
        return payments;
    }

    @Override
    public void reverseLoanDisbursal(String globalAccountNum, String note) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        PersonnelBO personnel = this.personnelDao.findPersonnelById(userContext.getId());
        loan.updateDetails(userContext);

        try {
            this.transactionHelper.startTransaction();
            loan.reverseLoanDisbursal(personnel, note);
            this.loanDao.save(loan);
            this.transactionHelper.commitTransaction();
        } catch (BusinessRuleException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getMessageKey(), e);
        } catch (AccountException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public List<CustomerDto> retrieveActiveGroupingAtTopOfCustomerHierarchyForLoanOfficer(Short loanOfficerId, Short officeId) {

        CustomerLevel customerLevel = CustomerLevel.CENTER;
        if (!ClientRules.getCenterHierarchyExists()) {
            customerLevel = CustomerLevel.GROUP;
        }

        return this.customerDao.findTopOfHierarchyCustomersUnderLoanOfficer(customerLevel, loanOfficerId, officeId);
    }

    @Override
    public MultipleLoanAccountDetailsDto retrieveMultipleLoanAccountDetails(String searchId, Short branchId, Integer productId) {

        List<ClientBO> clients = this.customerDao.findActiveClientsUnderParent(searchId, branchId);
        if (clients.isEmpty()) {
            throw new BusinessRuleException(LoanConstants.NOSEARCHRESULTS);
        }

        LoanOfferingBO loanOffering = this.loanProductDao.findById(productId);

        // FIXME - Refactor MultipleLoanCreationDto into proper Dto
        List<MultipleLoanCreationDto> multipleLoanDetails = buildClientViewHelper(loanOffering, clients);

        List<ValueListElement> allLoanPruposes = this.loanProductDao.findAllLoanPurposes();
        boolean loanPendingApprovalStateEnabled = ProcessFlowRules.isLoanPendingApprovalStateEnabled();

        return new MultipleLoanAccountDetailsDto(allLoanPruposes, loanPendingApprovalStateEnabled);
    }

    private List<MultipleLoanCreationDto> buildClientViewHelper(final LoanOfferingBO loanOffering,
            List<ClientBO> clients) {
        return collect(clients,
                new Transformer<ClientBO, MultipleLoanCreationDto>() {
                    @Override
                    public MultipleLoanCreationDto transform(ClientBO client) {
                        return new MultipleLoanCreationDto(client, loanOffering.eligibleLoanAmount(client
                                .getMaxLoanAmount(loanOffering), client.getMaxLoanCycleForProduct(loanOffering)),
                                loanOffering.eligibleNoOfInstall(client.getMaxLoanAmount(loanOffering), client
                                        .getMaxLoanCycleForProduct(loanOffering)), loanOffering.getCurrency());
                    }
                });
    }

    @Override
    public List<String> createMultipleLoans(List<CreateLoanRequest> multipleLoans) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        userContext.setBranchGlobalNum(userOffice.getGlobalOfficeNum());

        List<String> createdLoanAccountNumbers = new ArrayList<String>();
        for (CreateLoanRequest loanDetail : multipleLoans) {

            try {
                CustomerBO center = this.customerDao.findCustomerById(loanDetail.getCenterId());

                Short loanProductId = loanDetail.getLoanProductId();
                LoanOfferingBO loanProduct = this.loanProductDao.findById(loanProductId.intValue());
                CustomerBO client = this.customerDao.findCustomerById(loanDetail.getClientId());

                AccountState accountState = AccountState.fromShort(loanDetail.getAccountStateId());
                Money loanAmount = new Money(loanProduct.getCurrency(), loanDetail.getLoanAmount());
                Short defaultNumOfInstallments = loanDetail.getDefaultNoOfInstall();
                Date disbursementDate = center.getCustomerAccount().getNextMeetingDate();
                boolean interestDeductedAtDisbursement = loanProduct.isIntDedDisbursement();
                boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationBusinessService().isRepaymentIndepOfMeetingEnabled();

                MeetingBO newMeetingForRepaymentDay = null;
                if (isRepaymentIndepOfMeetingEnabled) {
                    MeetingBO meeting = center.getCustomerAccount().getMeetingForAccount();
                    LoanAccountMeetingDto loanAccountMeetingDto = null;

                    if (meeting.isWeekly()) {
                        loanAccountMeetingDto = new LoanAccountMeetingDto(RecurrenceType.WEEKLY.getValue().toString(),
                                meeting.getMeetingDetails().getWeekDay().getValue().toString(),
                                meeting.getMeetingDetails().getRecurAfter().toString(),
                                null, null, null, null, null, null);
                    } else if (meeting.isMonthly()) {
                        if (meeting.isMonthlyOnDate()) {
                            loanAccountMeetingDto = new LoanAccountMeetingDto(RecurrenceType.MONTHLY.getValue().toString(),
                                    null, null, "1",
                                    meeting.getMeetingDetails().getDayNumber().toString(),
                                    meeting.getMeetingDetails().getRecurAfter().toString(),
                                    null, null, null);
                        } else {
                            loanAccountMeetingDto = new LoanAccountMeetingDto(RecurrenceType.MONTHLY.getValue().toString(),
                                    null, null, "2", null, null,
                                    meeting.getMeetingDetails().getWeekDay().getValue().toString(),
                                    meeting.getMeetingDetails().getRecurAfter().toString(),
                                    meeting.getMeetingDetails().getWeekRank().getValue().toString());
                        }
                    }

                    newMeetingForRepaymentDay = this.createNewMeetingForRepaymentDay(new LocalDate(disbursementDate), loanAccountMeetingDto, client);
                }

                Double interestRate = loanProduct.getDefInterestRate();
                Short gracePeriodDuration = loanProduct.getGracePeriodDuration();

                checkPermissionForCreate(accountState.getValue(), userContext, userContext.getBranchId(), userContext.getId());

                List<FeeDto> additionalFees = new ArrayList<FeeDto>();
                List<FeeDto> defaultFees = new ArrayList<FeeDto>();

                new LoanProductService().getDefaultAndAdditionalFees(loanProductId, userContext, defaultFees, additionalFees);

                FundBO fund = null;
                List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
                boolean isRedone = false;

                // FIXME - keithw - tidy up constructor and use domain concepts rather than primitives, e.g. money v double, loanpurpose v integer.
                Double maxLoanAmount = Double.valueOf(loanDetail.getMaxLoanAmount());
                Double minLoanAmount = Double.valueOf(loanDetail.getMinLoanAmount());
                Short maxNoOfInstall  =loanDetail.getMaxNoOfInstall();
                Short minNoOfInstall = loanDetail.getMinNoOfInstall();
                LoanBO loan = new LoanBO(userContext, loanProduct, client, accountState, loanAmount, defaultNumOfInstallments,
                        disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, defaultFees,
                        customFields, isRedone, maxLoanAmount, minLoanAmount,
                        loanProduct.getMaxInterestRate(), loanProduct.getMinInterestRate(),
                        maxNoOfInstall, minNoOfInstall, isRepaymentIndepOfMeetingEnabled, newMeetingForRepaymentDay);
                loan.setBusinessActivityId(loanDetail.getLoanPurpose());

                PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
                AccountStateEntity newAccountState = new AccountStateEntity(accountState);
                AccountStatusChangeHistoryEntity statusChange = new AccountStatusChangeHistoryEntity(null, newAccountState, loggedInUser, loan);

                this.transactionHelper.startTransaction();
                loan.addAccountStatusChangeHistory(statusChange);
                this.loanDao.save(loan);
                this.transactionHelper.flushSession();
                String globalAccountNum = loan.generateId(userContext.getBranchGlobalNum());
                loan.setGlobalAccountNum(globalAccountNum);
                this.loanDao.save(loan);
                this.transactionHelper.commitTransaction();

                createdLoanAccountNumbers.add(loan.getGlobalAccountNum());
            } catch (ServiceException e) {
                this.transactionHelper.rollbackTransaction();
                throw new MifosRuntimeException(e);
            } catch (PersistenceException e) {
                this.transactionHelper.rollbackTransaction();
                throw new MifosRuntimeException(e);
            } catch (AccountException e) {
                this.transactionHelper.rollbackTransaction();
                throw new BusinessRuleException(e.getKey(), e);
            }
        }

        return createdLoanAccountNumbers;
    }

    private void checkPermissionForCreate(Short newState, UserContext userContext, Short officeId, Short loanOfficerId) {
        if (!isPermissionAllowed(newState, userContext, officeId, loanOfficerId)) {
            throw new BusinessRuleException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(final Short newSate, final UserContext userContext, final Short officeId,
            final Short loanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId, loanOfficerId));
    }

    @Override
    public List<CustomerSearchResultDto> retrieveCustomersThatQualifyForLoans(CustomerSearchDto customerSearchDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            List<CustomerSearchResultDto> pagedDetails = new ArrayList<CustomerSearchResultDto>();

            QueryResult customerForSavings = new CustomerPersistence().searchGroupClient(customerSearchDto.getSearchTerm(), userContext.getId());

            int position = (customerSearchDto.getPage()-1) * customerSearchDto.getPageSize();
            List<AccountSearchResultsDto> pagedResults = customerForSavings.get(position, customerSearchDto.getPageSize());
            int i=1;
            for (AccountSearchResultsDto customerBO : pagedResults) {
                  CustomerSearchResultDto customer = new CustomerSearchResultDto();
                  customer.setCustomerId(customerBO.getClientId());
                  customer.setBranchName(customerBO.getOfficeName());
                  customer.setGlobalId(customerBO.getGlobelNo());
                  customer.setSearchIndex(i);

                  customer.setCenterName(StringUtils.defaultIfEmpty(customerBO.getCenterName(), "--"));
                  customer.setGroupName(StringUtils.defaultIfEmpty(customerBO.getGroupName(), "--"));
                  customer.setClientName(customerBO.getClientName());

                  pagedDetails.add(customer);
                  i++;
            }
            return pagedDetails;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (HibernateSearchException e) {
            throw new MifosRuntimeException(e);
        } catch (ConfigurationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public Errors validateLoanDisbursementDate(LocalDate loanDisbursementDate, Integer customerId, Integer productId) {
        
        Errors errors = new Errors();
        
        if (loanDisbursementDate.isBefore(new LocalDate())) {
            String[] args = {""};
            errors.addError("dibursementdate.cannot.be.before.todays.date", args);
        }
        
        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        LocalDate customerActivationDate = new LocalDate(customer.getCustomerActivationDate());
        if (loanDisbursementDate.isBefore(customerActivationDate)) {
            String[] args = {customerActivationDate.toString("dd-MMM-yyyy")};
            errors.addError("dibursementdate.before.customer.activation.date", args);
        }
        
        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId);
        LocalDate productStartDate = new LocalDate(loanProduct.getStartDate());
        if (loanDisbursementDate.isBefore(productStartDate)) {
            String[] args = {productStartDate.toString("dd-MMM-yyyy")};
            errors.addError("dibursementdate.before.product.startDate", args);
        }
        
        try {
            this.holidayServiceFacade.validateDisbursementDateForNewLoan(customer.getOfficeId(), loanDisbursementDate.toDateMidnight().toDateTime());
        } catch (BusinessRuleException e) {
            String[] args = {""};
            errors.addError("dibursementdate.falls.on.holiday", args);
        }
        
        boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationBusinessService().isRepaymentIndepOfMeetingEnabled();
        
        LoanDisbursementDateFactory loanDisbursementDateFactory = new LoanDisbursmentDateFactoryImpl();
        LoanDisbursementDateValidator loanDisbursementDateFinder = loanDisbursementDateFactory.create(customer, loanProduct, isRepaymentIndependentOfMeetingEnabled, false);
        
        boolean isValid = loanDisbursementDateFinder.isDisbursementDateValidInRelationToSchedule(loanDisbursementDate);
        if (!isValid) {
            String[] args = {""};
            errors.addError("dibursementdate.invalid.in.relation.to.meeting.schedule", args);
        }
        
        return errors;
    }
    
    @Override
    public Errors validateLoanWithBackdatedPaymentsDisbursementDate(LocalDate loanDisbursementDate, Integer customerId, Integer productId) {
        Errors errors = new Errors();
        
        if (!loanDisbursementDate.isBefore(new LocalDate())) {
            String[] args = {""};
            errors.addError("dibursementdate.before.todays.date", args);
        }
        
        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        LocalDate customerActivationDate = new LocalDate(customer.getCustomerActivationDate());
        if (loanDisbursementDate.isBefore(customerActivationDate)) {
            String[] args = {customerActivationDate.toString("dd-MMM-yyyy")};
            errors.addError("dibursementdate.before.customer.activation.date", args);
        }
        
        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId);
        LocalDate productStartDate = new LocalDate(loanProduct.getStartDate());
        if (loanDisbursementDate.isBefore(productStartDate)) {
            String[] args = {productStartDate.toString("dd-MMM-yyyy")};
            errors.addError("dibursementdate.before.product.startDate", args);
        }
        
        try {
            this.holidayServiceFacade.validateDisbursementDateForNewLoan(customer.getOfficeId(), loanDisbursementDate.toDateMidnight().toDateTime());
        } catch (BusinessRuleException e) {
            String[] args = {""};
            errors.addError("dibursementdate.falls.on.holiday", args);
        }
        
        return errors;
    }

    @Override
    public LoanApplicationStateDto retrieveLoanApplicationState() {

        AccountState applicationState = AccountState.LOAN_APPROVED;
        boolean loanPendingApprovalEnabled = ProcessFlowRules.isLoanPendingApprovalStateEnabled();
        if(loanPendingApprovalEnabled) {
            applicationState = AccountState.LOAN_PENDING_APPROVAL;
        }

        return new LoanApplicationStateDto(AccountState.LOAN_PARTIAL_APPLICATION.getValue().intValue(), applicationState.getValue().intValue());
    }

    @Override
    public List<QuestionGroupDetail> retrieveApplicableQuestionGroups(Integer productId) {

        List<QuestionGroupDetail> questionGroupDetails = new ArrayList<QuestionGroupDetail>();
        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId);
        if (!loanProduct.getQuestionGroups().isEmpty()) {
            List<QuestionGroupDetail> allQuestionGroupDetails = questionnaireServiceFacade.getQuestionGroups("Create", "Loan");
            for (QuestionGroupDetail questionGroupDetail : allQuestionGroupDetails) {
                if (questionGroupDetail.isActive()) {
                    questionGroupDetails.add(questionGroupDetail);
                }
            }
        }

        return questionGroupDetails;
    }

    @Override
    public CashFlowDto retrieveCashFlowSettings(DateTime firstInstallment, DateTime lastInstallment, Integer productId, BigDecimal loanAmount) {
        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId);
        return new CashFlowDto(firstInstallment, lastInstallment, loanProduct.shouldCaptureCapitalAndLiabilityInformation(), loanAmount, loanProduct.getIndebtednessRatio(), loanProduct.getRepaymentCapacity(), Localization.getInstance().getConfiguredLocale());
    }

    @Override
    public List<CashFlowDataDto> retrieveCashFlowSummary(List<MonthlyCashFlowDto> monthlyCashFlow, LoanScheduleDto loanScheduleDto) {

        Locale locale = Localization.getInstance().getConfiguredLocale();

        List<CashFlowDataDto> cashflowdetails = new ArrayList<CashFlowDataDto>();

        for (MonthlyCashFlowDto monthlyCashflowform : monthlyCashFlow) {

            CashFlowDataDto cashflowDataDto = new CashFlowDataDto();
            cashflowDataDto.setMonth(monthlyCashflowform.getMonthDate().monthOfYear().getAsText(locale));
            cashflowDataDto.setYear(String.valueOf(monthlyCashflowform.getMonthDate().getYear()));

            cashflowDataDto.setCumulativeCashFlow(String.valueOf(monthlyCashflowform.getCumulativeCashFlow().setScale(
                    AccountingRules.getDigitsAfterDecimal(), BigDecimal.ROUND_HALF_UP)));
            cashflowDataDto.setMonthYear(monthlyCashflowform.getMonthDate().toDate());
            cashflowDataDto.setNotes(monthlyCashflowform.getNotes());

            String cumulativeCashflowAndInstallment = computeDiffBetweenCumulativeAndInstallment(monthlyCashflowform.getMonthDate(), monthlyCashflowform.getCumulativeCashFlow(), loanScheduleDto);
            cashflowDataDto.setDiffCumulativeCashflowAndInstallment(cumulativeCashflowAndInstallment);
            String cashflowAndInstallmentPercent = computeDiffBetweenCumulativeAndInstallmentPercent(monthlyCashflowform.getMonthDate(), monthlyCashflowform.getCumulativeCashFlow(), loanScheduleDto);
            cashflowDataDto.setDiffCumulativeCashflowAndInstallmentPercent(cashflowAndInstallmentPercent);

            cashflowdetails.add(cashflowDataDto);
        }

        return cashflowdetails;
    }

    private String computeDiffBetweenCumulativeAndInstallment(DateTime dateOfCashFlow, BigDecimal cashflow, LoanScheduleDto loanScheduleDto) {
        BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow, loanScheduleDto);
        return String.valueOf(cashflow.subtract(totalInstallmentForMonth).setScale(
                AccountingRules.getDigitsAfterDecimal(), RoundingMode.HALF_UP));
    }

    private BigDecimal cumulativeTotalForMonth(DateTime dateOfCashFlow, LoanScheduleDto loanScheduleDto) {
        BigDecimal value = new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
        for (LoanCreationInstallmentDto repaymentScheduleInstallment : loanScheduleDto.getInstallments()) {

            DateTime dueDate = new DateTime(repaymentScheduleInstallment.getDueDate());

            if (dueDate.getMonthOfYear() == dateOfCashFlow.getMonthOfYear() && (dueDate.getYear() == dateOfCashFlow.getYear())) {
                value = value.add(BigDecimal.valueOf(repaymentScheduleInstallment.getTotal()));
            }
        }
        return value;
    }

    private String computeDiffBetweenCumulativeAndInstallmentPercent(DateTime dateOfCashFlow, BigDecimal cashflow, LoanScheduleDto loanScheduleDto) {
        BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow, loanScheduleDto);
        String value;
        if (cashflow.doubleValue() != 0) {
            value = String.valueOf(totalInstallmentForMonth.multiply(BigDecimal.valueOf(100)).divide(cashflow,
                    AccountingRules.getDigitsAfterDecimal(), RoundingMode.HALF_UP));
        } else {
            value = "Infinity";
        }
        return value;
    }

    @Override
    public Errors validateCashFlowForInstallmentsForWarnings(List<CashFlowDataDto> cashFlowDataDtos, Integer productId) {
        Errors errors = new Errors();
        LoanOfferingBO loanOfferingBO = this.loanProductDao.findById(productId);
        if (loanOfferingBO.shouldValidateCashFlowForInstallments()) {
            CashFlowDetail cashFlowDetail = loanOfferingBO.getCashFlowDetail();
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
    public boolean isCompareWithCashFlowEnabledOnProduct(Integer productId) {
        LoanOfferingBO loanProduct = this.loanProductDao.findById(productId);
        return loanProduct.isCashFlowCheckEnabled();
    }

    @Override
    public Errors validateInputInstallments(Date disbursementDate, Integer minGapInDays, Integer maxGapInDays,
            BigDecimal minInstallmentAmount, List<LoanCreationInstallmentDto> dtoInstallments, Integer customerId) {
        Short officeId = customerDao.findCustomerById(customerId).getOfficeId();
        VariableInstallmentDetailsBO variableInstallmentDetails = new VariableInstallmentDetailsBO();
        variableInstallmentDetails.setMinGapInDays(minGapInDays);
        variableInstallmentDetails.setMaxGapInDays(maxGapInDays);
        InstallmentValidationContext context = new InstallmentValidationContext(disbursementDate, variableInstallmentDetails, minInstallmentAmount, holidayServiceFacade, officeId);

        MifosCurrency currency = Money.getDefaultCurrency();
        Locale locale = Localization.getInstance().getConfiguredLocale();
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();

        for (LoanCreationInstallmentDto dto : dtoInstallments) {
            Money principal = new Money(currency, dto.getPrincipal());
            Money interest = new Money(currency, dto.getInterest());
            Money fees = new Money(currency, dto.getFees());
            Money miscFees = new Money(currency);
            Money miscPenalty = new Money(currency);
            RepaymentScheduleInstallment installment = new RepaymentScheduleInstallment(dto.getInstallmentNumber(),
                    dto.getDueDate(), principal, interest, fees, miscFees, miscPenalty, locale);
            installment.setTotalAndTotalValue(new Money(currency, dto.getTotal()));

            installments.add(installment);
        }

        return installmentsValidator.validateInputInstallments(installments, context);
    }

    @Override
    public Errors validateInstallmentSchedule(List<LoanCreationInstallmentDto> dtoInstallments, BigDecimal minInstallmentAmount) {

        MifosCurrency currency = Money.getDefaultCurrency();
        Locale locale = Localization.getInstance().getConfiguredLocale();
        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();

        for (LoanCreationInstallmentDto dto : dtoInstallments) {
            Money principal = new Money(currency, dto.getPrincipal());
            Money interest = new Money(currency, dto.getInterest());
            Money fees = new Money(currency, dto.getFees());
            Money miscFees = new Money(currency);
            Money miscPenalty = new Money(currency);
            RepaymentScheduleInstallment installment = new RepaymentScheduleInstallment(dto.getInstallmentNumber(),
                    dto.getDueDate(), principal, interest, fees, miscFees, miscPenalty, locale);
            installment.setTotalAndTotalValue(new Money(currency, dto.getTotal()));

            installments.add(installment);
        }

        return installmentsValidator.validateInstallmentSchedule(installments, minInstallmentAmount);
    }

    @Override
    public VariableInstallmentWithFeeValidationResult validateFeeCanBeAppliedToVariableInstallmentLoan(Long feeId) {

        try {
            org.mifos.dto.domain.FeeDto fee = this.feeDao.findDtoById(feeId.shortValue());
            boolean feeCanBeAppliedToVariableInstallmentLoan = true;
            String feeName = fee.getName();
            if (fee.isPeriodic()) {
                feeCanBeAppliedToVariableInstallmentLoan = false;
            } else if (fee.isRateBasedFee()) {
                FeeFormula formula = FeeFormula.getFeeFormula(fee.getFeeFormula().getId());
                switch (formula) {
                case AMOUNT_AND_INTEREST:
                    feeCanBeAppliedToVariableInstallmentLoan = false;
                    break;
                case INTEREST:
                    feeCanBeAppliedToVariableInstallmentLoan = false;
                    break;
                default:
                    break;
                }
            }
            return new VariableInstallmentWithFeeValidationResult(feeCanBeAppliedToVariableInstallmentLoan, feeName);
        } catch (PropertyNotFoundException e) {
            throw new MifosRuntimeException(e);
        }
    }
}