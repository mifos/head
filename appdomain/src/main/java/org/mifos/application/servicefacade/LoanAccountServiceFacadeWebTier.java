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
import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;
import static org.mifos.framework.util.CollectionUtils.collect;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
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
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.MultipleLoanCreationDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
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
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.CreateLoanRequest;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.InstallmentDetailsDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OpeningBalanceLoanAccount;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.AccountFeesDto;
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
import org.mifos.dto.screen.LoanPerformanceHistoryDto;
import org.mifos.dto.screen.LoanScheduledInstallmentDto;
import org.mifos.dto.screen.LoanSummaryDto;
import org.mifos.dto.screen.MultipleLoanAccountDetailsDto;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.framework.util.helpers.Transformer;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;
import org.mifos.security.MifosUser;
import org.mifos.security.authorization.AuthorizationManager;
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
    private final HibernateTransactionHelper transactionHelper;

    @Autowired
    private FeeDao feeDao;

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    public LoanAccountServiceFacadeWebTier(OfficeDao officeDao, LoanProductDao loanProductDao, CustomerDao customerDao,
                                           PersonnelDao personnelDao, FundDao fundDao, LoanDao loanDao, HolidayDao holidayDao,
                                           AccountService accountService, ScheduleCalculatorAdaptor scheduleCalculatorAdaptor,
                                           LoanBusinessService loanBusinessService) {
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
        transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    }

    @Override
    public AccountStatusDto retrieveAccountStatuses(Long loanAccountId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        LoanBO loanAccount = this.loanDao.findById(loanAccountId.intValue());

        try {
            List<ListElement> loanStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeLoanStates();

            List<AccountStateEntity> statusList = AccountStateMachines.getInstance().getLoanStatusList(
                    loanAccount.getAccountState());
            for (AccountStateEntity accountState : statusList) {
                accountState.setLocaleId(userContext.getLocaleId());
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
        final Date nextMeetingDate = customer.getCustomerAccount().getNextMeetingDate();
        final String recurMonth = customer.getCustomerMeeting().getMeeting().getMeetingDetails().getRecurAfter()
                .toString();
        final boolean isGroup = customer.isGroup();
        final boolean isGlimEnabled = new ConfigurationPersistence().isGlimEnabled();

        final List<PrdOfferingDto> loanProductDtos = retrieveActiveLoanProductsApplicableForCustomer(customer);

        LoanCreationGlimDto loanCreationGlimDto = null;
        List<LoanAccountDetailsDto> clientDetails = new ArrayList<LoanAccountDetailsDto>();

        if (isGroup && isGlimEnabled) {
            final List<ValueListElement> loanPurposes = loanProductDao.findAllLoanPurposes();
            final List<ClientBO> activeClientsOfGroup = customerDao.findActiveClientsUnderGroup(customer);
            loanCreationGlimDto = new LoanCreationGlimDto(loanPurposes);

            if (activeClientsOfGroup == null || activeClientsOfGroup.isEmpty()) {
                throw new BusinessRuleException(GroupConstants.IMPOSSIBLE_TO_CREATE_GROUP_LOAN);
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

    private List<PrdOfferingDto> retrieveActiveLoanProductsApplicableForCustomer(final CustomerBO customer) {

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
    public LoanCreationLoanDetailsDto retrieveLoanDetailsForLoanAccountCreation(Integer customerId, Short productId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            List<FeeDto> additionalFees = new ArrayList<FeeDto>();
            List<FeeDto> defaultFees = new ArrayList<FeeDto>();

            new LoanProductService(new LoanPrdBusinessService()).getDefaultAndAdditionalFees(productId, userContext,
                    defaultFees, additionalFees);

            LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext
                    .getLocaleId());

            if (AccountingRules.isMultiCurrencyEnabled()) {
                defaultFees = getFilteredFeesByCurrency(defaultFees, loanOffering.getCurrency().getCurrencyId());
                additionalFees = getFilteredFeesByCurrency(additionalFees, loanOffering.getCurrency().getCurrencyId());
            }

            // setDateIntoForm
            CustomerBO customer = this.customerDao.findCustomerById(customerId);
            LoanAmountOption eligibleLoanAmount = loanOffering.eligibleLoanAmount(customer
                    .getMaxLoanAmount(loanOffering), customer.getMaxLoanCycleForProduct(loanOffering));
            LoanOfferingInstallmentRange eligibleNoOfInstall = loanOffering.eligibleNoOfInstall(customer
                    .getMaxLoanAmount(loanOffering), customer.getMaxLoanCycleForProduct(loanOffering));

            CustomValueDto customValueDto = legacyMasterDao.getLookUpEntity(MasterConstants.COLLATERAL_TYPES);
            List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();

            // Business activities got in getPrdOfferings also but only for glim.
            List<ValueListElement> loanPurposes = legacyMasterDao.findValueListElements(MasterConstants.LOAN_PURPOSES);

            MeetingDetailsEntity loanOfferingMeetingDetail = loanOffering.getLoanOfferingMeeting().getMeeting()
                    .getMeetingDetails();

            MeetingDto loanOfferingMeetingDto = loanOffering.getLoanOfferingMeetingValue().toDto();

            List<FundBO> funds = getFunds(loanOffering);

            boolean isRepaymentIndependentOfMeetingEnabled = new ConfigurationBusinessService()
                    .isRepaymentIndepOfMeetingEnabled();

            return new LoanCreationLoanDetailsDto(isRepaymentIndependentOfMeetingEnabled, loanOfferingMeetingDto,
                    customer.getCustomerMeetingValue().toDto(), loanPurposes);

        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (SystemException e) {
            throw new MifosRuntimeException(e);
        }
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
    public String createLoan(OpeningBalanceLoanAccount openingBalanceLoan) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());

        // assemble into domain entities
        LoanOfferingBO loanProduct = this.loanProductDao.findBySystemId(openingBalanceLoan.getLoanProductGlobalId());
        CustomerBO customer = this.customerDao.findCustomerBySystemId(openingBalanceLoan.getCustomerGlobalId());
        List<LoanScheduleEntity> scheduledLoanRepayments = new ArrayList<LoanScheduleEntity>();

        AccountState loanState = AccountState.fromShort(openingBalanceLoan.getAccountState());

        AccountState loanApprovedState =  AccountState.LOAN_APPROVED;

        Money loanAmountDisbursed = new Money(loanProduct.getCurrency(), openingBalanceLoan.getLoanAmountDisbursed());
        Money amountPaidToDate = new Money(loanProduct.getCurrency(), openingBalanceLoan.getAmountPaidToDate());
        Integer numberOfInstallments = openingBalanceLoan.getNumberOfInstallments();
        LocalDate firstInstallmentDate = openingBalanceLoan.getFirstInstallmentDate();
        LocalDate activationDate = openingBalanceLoan.getDisbursementDate();

        try {

            CalendarEvent calendarEvents = holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());

            List<InstallmentDate> dueInstallmentDates = new ArrayList<InstallmentDate>();

            if (openingBalanceLoan.getNumberOfInstallments() > 0) {

                ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(loanProduct.getLoanOfferingMeetingValue());
                ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(calendarEvents.getWorkingDays(), calendarEvents.getHolidays());

                List<Date> dueDates = new ArrayList<Date>();
                List<DateTime> installmentDates = dateGeneration.generateScheduledDates(numberOfInstallments, firstInstallmentDate.toDateMidnight().toDateTime(), scheduledEvent);
                for (DateTime installmentDate : installmentDates) {
                    dueDates.add(installmentDate.toDate());
                }

                Integer installmentId = 1;
                for (Date date : dueDates) {
                    dueInstallmentDates.add(new InstallmentDate(installmentId.shortValue(), date));
                    installmentId++;
                }
            }

            GraceType graceType = loanProduct.getGraceType();
            Integer gracePeriodDuration = Integer.valueOf(0);
            if (loanProduct.getGracePeriodDuration() != null) {
                gracePeriodDuration = loanProduct.getGracePeriodDuration().intValue();
            }
            MeetingBO loanMeeting = loanProduct.getLoanOfferingMeetingValue();

            // FIXME - should be provided on opening balance
            Double interestRate = Double.valueOf("10.0");
            Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
            InterestType interestType = loanProduct.getInterestType();
            Money loanInterest = getLoanInterest_v2(loanMeeting, graceType, gracePeriodDuration, loanAmountDisbursed, numberOfInstallments, interestRate, interestDays, interestType);

            if (loanProduct.isPrinDueLastInst()) {
                // Principal due on last installment has been cut, so throw an exception if we reach this code.
                throw new AccountException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
            }

            List<EMIInstallment> EMIInstallments = generateEMI_v2(loanInterest, loanMeeting, graceType, gracePeriodDuration, loanAmountDisbursed, numberOfInstallments, interestRate, interestDays, interestType);

            // create loanSchedules
            int installmentIndex = 0;
            for (InstallmentDate installmentDate : dueInstallmentDates) {
                EMIInstallment em = EMIInstallments.get(installmentIndex);
                LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(null, customer, installmentDate
                        .getInstallmentId(), new java.sql.Date(installmentDate.getInstallmentDueDate().getTime()),
                        PaymentStatus.UNPAID, em.getPrincipal(), em.getInterest());
                scheduledLoanRepayments.add(loanScheduleEntity);
                installmentIndex++;
            }

            applyRounding_v2(scheduledLoanRepayments, graceType, gracePeriodDuration, loanAmountDisbursed, numberOfInstallments, interestType);

        } catch (AccountException e1) {
            throw new BusinessRuleException(e1.getKey(), e1);
        }

        LoanBO loan = LoanBO.createOpeningBalanceLoan(userContext, loanProduct, customer, loanApprovedState,
                loanAmountDisbursed, openingBalanceLoan.getDisbursementDate(), numberOfInstallments,
                firstInstallmentDate, openingBalanceLoan.getCurrentInstallmentDate(),
                amountPaidToDate, openingBalanceLoan.getLoanCycle(), scheduledLoanRepayments);

        try {
            // create loan
            StaticHibernateUtil.startTransaction();
            PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan.getAccountState(), createdBy, loan));
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            loan.setGlobalAccountNum(loan.generateId(userOffice.getGlobalOfficeNum()));
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            // disburse loan
            String receiptNumber = null;
            Date receiptDate = null;
            PaymentTypeEntity paymentType = new PaymentTypeEntity(PaymentTypes.CASH.getValue());
            Date paymentDate = openingBalanceLoan.getDisbursementDate().toDateMidnight().toDate();
            AccountPaymentEntity disbursalPayment = new AccountPaymentEntity(loan, loanAmountDisbursed, receiptNumber, receiptDate, paymentType, paymentDate);
            disbursalPayment.setCreatedByUser(createdBy);
            loan.disburseLoan(disbursalPayment);
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            Short paymentId = PaymentTypes.CASH.getValue();
            PaymentData paymentData = new PaymentData(amountPaidToDate, createdBy, paymentId, firstInstallmentDate.toDateMidnight().toDate());
            // make payment for balance against first installment date.
            loan.applyPayment(paymentData);
            this.loanDao.save(loan);
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


        return loan.getGlobalAccountNum();
    }

    private void applyRounding_v2(List<? extends AccountActionDateEntity> installments, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, InterestType interestType) {

        RepaymentTotals totals = calculateInitialTotals_v2(installments, loanAmount);
        int installmentNum = 0;
        for (Iterator it = installments.iterator(); it.hasNext();) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) it.next();
            installmentNum++;
            if (it.hasNext()) { // handle all but the last installment
                if (isGraceInstallment_v2(installmentNum, graceType, gracePeriodDuration)) {
                    roundAndAdjustGraceInstallment_v2(currentInstallment);
                } else if (interestType.equals(InterestType.DECLINING_EPI.getValue())) {
                    roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(currentInstallment);
                } else {
                    roundAndAdjustButLastNonGraceInstallment_v2(currentInstallment);
                }
                updateRunningTotals_v2(totals, currentInstallment);
            } else {
                roundAndAdjustLastInstallment_v2(currentInstallment, totals);
            }
        }
    }

    /**
     * See JavaDoc comment for applyRounding_v2. TODO: handle fees
     */
    private void roundAndAdjustLastInstallment_v2(final LoanScheduleEntity lastInstallment, final RepaymentTotals totals) {

        roundInstallmentAccountFeesDue_v2(lastInstallment);
        Money installmentPayment = MoneyUtils.finalRound(totals.roundedPaymentsDue.subtract(totals.runningPayments));
        lastInstallment.setPrincipal(MoneyUtils.currencyRound(totals.getRoundedPrincipalDue().subtract(
                totals.runningPrincipal)));
        adjustLastInstallmentFees_v2(lastInstallment, totals);
        lastInstallment.setInterest(MoneyUtils.currencyRound(installmentPayment.subtract(
                lastInstallment.getPrincipalDue()).subtract(lastInstallment.getTotalFeeDueWithMiscFeeDue()).subtract(
                lastInstallment.getPenaltyDue())));
    }

    /**
     * adjust the first fee in the installment's set of fees
     */
    private void adjustLastInstallmentFees_v2(final LoanScheduleEntity lastInstallment, final RepaymentTotals totals) {
        Set<AccountFeesActionDetailEntity> feeDetails = lastInstallment.getAccountFeesActionDetails();
        if (!(feeDetails == null) && !feeDetails.isEmpty()) {
            Money lastInstallmentFeeSum = new Money(lastInstallment.getCurrency());
            for (AccountFeesActionDetailEntity e : feeDetails) {
                lastInstallmentFeeSum = lastInstallmentFeeSum.add(e.getFeeAmount());
            }
            for (Object element : feeDetails) {
                AccountFeesActionDetailEntity e = (AccountFeesActionDetailEntity) element;
                e.adjustFeeAmount(totals.roundedAccountFeesDue.subtract(totals.runningAccountFees).subtract(
                        lastInstallmentFeeSum));
                // just adjust the first fee
                return;
            }
        }
    }

    private void updateRunningTotals_v2(final RepaymentTotals totals, final LoanScheduleEntity currentInstallment) {

        totals.runningPayments = totals.runningPayments.add(currentInstallment.getTotalPaymentDue());

        totals.runningPrincipal = totals.runningPrincipal.add(currentInstallment.getPrincipalDue());
        totals.runningAccountFees = totals.runningAccountFees.add(currentInstallment.getTotalFeesDue());
        totals.runningMiscFees = totals.runningMiscFees.add(currentInstallment.getMiscFeeDue());
        totals.runningPenalties = totals.runningPenalties.add(currentInstallment.getPenaltyDue());
    }

    /**
     * See Javadoc comment for method applyRounding() for business rules for rounding and adjusting all installments but
     * the last. LoanScheduleEntity does not store the total payment due, directly, but it is the sum of principal,
     * interest, and non-miscellaneous fees.
     * <p>
     *
     * how to set rounded fee for installment?????? This is what I want to do: currentInstallment.setFee
     * (currencyRound_v2 (currentInstallment.getFee));
     *
     * Then I want to adjust principal, but need to extract the rounded fee, like this:
     * currentInstallment.setPrincipal(installmentRoundedTotalPayment .subtract (currentInstallment.getInterest()
     * .subtract (currentInstallment.getFee());
     */
    private void roundAndAdjustButLastNonGraceInstallment_v2(final LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);

        installment.setInterest(MoneyUtils.currencyRound(installment.getInterest()));
        // TODO: above comment applies to principal
        installment.setPrincipal(roundedTotalInstallmentPaymentDue.subtract(installment.getInterestDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()).add(
                installment.getPrincipalPaid()));
    }


    private void roundAndAdjustNonGraceInstallmentForDecliningEPI_v2(final LoanScheduleEntity installment) {
        Money roundedTotalInstallmentPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setPrincipal(MoneyUtils.currencyRound(installment.getPrincipal()));
        // TODO: above comment applies to principal
        installment.setInterest(roundedTotalInstallmentPaymentDue.subtract(installment.getPrincipalDue()).subtract(
                installment.getTotalFeeDueWithMiscFeeDue()).subtract(installment.getPenaltyDue()));
    }

    /**
     * For principal-only grace installments, adjust the interest to account for rounding discrepancies.
     */
    private void roundAndAdjustGraceInstallment_v2(final LoanScheduleEntity installment) {
        Money roundedInstallmentTotalPaymentDue = MoneyUtils.initialRound(installment.getTotalPaymentDue());
        roundInstallmentAccountFeesDue_v2(installment);
        installment.setInterest(roundedInstallmentTotalPaymentDue.subtract(installment.getTotalFeeDueWithMiscFeeDue())
                .subtract(installment.getPenaltyDue()));
    }

    private void roundInstallmentAccountFeesDue_v2(final LoanScheduleEntity installment) {

        for (AccountFeesActionDetailEntity e : installment.getAccountFeesActionDetails()) {
            e.roundFeeAmount(MoneyUtils.currencyRound(e.getFeeDue().add(e.getFeeAmountPaid())));
        }
    }

    /**
     * A grace-period installment can appear in the loan schedule only if the loan is setup with principal-only grace.
     */
    private boolean isGraceInstallment_v2(final int installmentNum, GraceType graceType, Integer gracePeriodDuration) {
        return graceType.equals(GraceType.PRINCIPALONLYGRACE) && installmentNum <= gracePeriodDuration;
    }

    private RepaymentTotals calculateInitialTotals_v2(final List<? extends AccountActionDateEntity> installmentsToBeRounded, Money loanAmount) {

        RepaymentTotals totals = new RepaymentTotals(loanAmount.getCurrency());

        Money exactTotalInterestDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalAccountFeesDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalMiscFeesDue = new Money(loanAmount.getCurrency(), "0");
        Money exactTotalMiscPenaltiesDue = new Money(loanAmount.getCurrency(), "0");

        // principal due = loan amount less any payments on principal
        Money exactTotalPrincipalDue = loanAmount;
        for (AccountActionDateEntity e : installmentsToBeRounded) {
            LoanScheduleEntity installment = (LoanScheduleEntity) e;
            exactTotalPrincipalDue = exactTotalPrincipalDue.subtract(installment.getPrincipalPaid());
        }

        for (Object element : installmentsToBeRounded) {
            LoanScheduleEntity currentInstallment = (LoanScheduleEntity) element;
            exactTotalInterestDue = exactTotalInterestDue.add(currentInstallment.getInterestDue());
            exactTotalAccountFeesDue = exactTotalAccountFeesDue.add(currentInstallment.getTotalFeesDue());
            exactTotalMiscFeesDue = exactTotalMiscFeesDue.add(currentInstallment.getMiscFeeDue());
            exactTotalMiscPenaltiesDue = exactTotalMiscPenaltiesDue.add(currentInstallment.getMiscPenaltyDue());
        }
        Money exactTotalPaymentsDue = exactTotalInterestDue.add(exactTotalAccountFeesDue).add(exactTotalMiscFeesDue)
                .add(exactTotalMiscPenaltiesDue).add(exactTotalPrincipalDue);

        totals.setRoundedPaymentsDue(MoneyUtils.finalRound(exactTotalPaymentsDue));
        totals.setRoundedAccountFeesDue(MoneyUtils.currencyRound(exactTotalAccountFeesDue));
        totals.setRoundedMiscFeesDue(MoneyUtils.currencyRound(exactTotalMiscFeesDue));
        totals.setRoundedMiscPenaltiesDue(MoneyUtils.currencyRound(exactTotalMiscPenaltiesDue));
        totals.setRoundedPrincipalDue(exactTotalPrincipalDue);

        // Adjust interest to account for rounding discrepancies
        totals.setRoundedInterestDue(totals.getRoundedPaymentsDue().subtract(totals.getRoundedAccountFeesDue())
                .subtract(totals.getRoundedMiscFeesDue()).subtract(totals.getRoundedPenaltiesDue()).subtract(
                        totals.getRoundedMiscPenaltiesDue()).subtract(totals.getRoundedPrincipalDue()));
        return totals;
    }

    private List<EMIInstallment> generateEMI_v2(Money loanInterest, MeetingBO loanMeeting, GraceType graceType,
            Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, Double interestRate,
            Integer interestDays, InterestType interestType) {


        switch (interestType) {
        case FLAT:
            return allFlatInstallments_v2(loanInterest, graceType, gracePeriodDuration, loanAmount, numberOfInstallments);
        case DECLINING:
        case DECLINING_PB:
            return allDecliningInstallments_v2(graceType, gracePeriodDuration, loanAmount, numberOfInstallments, loanMeeting, interestRate, interestDays);
        case DECLINING_EPI:
            return allDecliningEPIInstallments_v2(graceType, gracePeriodDuration, loanAmount, numberOfInstallments,
                    loanMeeting, interestRate, interestDays);
        default:
            try {
                throw new AccountException(AccountConstants.NOT_SUPPORTED_EMI_GENERATION);
            } catch (AccountException e) {
                throw new BusinessRuleException(e.getKey(), e);
            }
        }
    }

    private List<EMIInstallment> allDecliningEPIInstallments_v2(GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {

        List<EMIInstallment> emiInstallments;
        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, loanMeeting, interestRate, interestDays);
        } else {
            emiInstallments = generateDecliningEPIInstallmentsInterestOnly_v2(gracePeriodDuration, loanAmount, loanMeeting, interestRate, interestDays);
            emiInstallments.addAll(generateDecliningEPIInstallmentsAfterInterestOnlyGraceInstallments_v2(gracePeriodDuration, loanAmount, numberOfInstallments, loanMeeting, interestRate, interestDays));
        }
        return emiInstallments;
    }

    private List<EMIInstallment> generateDecliningEPIInstallmentsAfterInterestOnlyGraceInstallments_v2(Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {

        return generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, loanMeeting, interestRate, interestDays);
    }

    // same as Declining
    private List<EMIInstallment> generateDecliningEPIInstallmentsInterestOnly_v2(Integer gracePeriodDuration, Money loanAmount, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {

        return generateDecliningInstallmentsInterestOnly_v2(gracePeriodDuration, interestRate, interestDays, loanAmount, loanMeeting);
    }


    private List<EMIInstallment> generateDecliningEPIInstallmentsNoGrace_v2(final int numInstallments, Money loanAmount, MeetingBO loanMeeting, Double interestRate, Integer interestDays){

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numInstallments));
        double interestRateFractional = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);

        for (int i = 0; i < numInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            Money interestThisPeriod = principalBalance.multiply(interestRateFractional);
            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalPerPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }


    /**
     * Generate declining-interest installment variants based on the type of grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest, the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments. The first grace installments are
     * interest-only, and principal is paid off with the remaining installments.
     * </ul>
     */
    private List<EMIInstallment> allDecliningInstallments_v2(GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, MeetingBO loanMeeting, Double interestRate, Integer interestDays) {
        List<EMIInstallment> emiInstallments;

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningInstallmentsNoGrace_v2(numberOfInstallments, interestRate, interestDays, loanAmount, loanMeeting);
        } else {

            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.

            emiInstallments = generateDecliningInstallmentsInterestOnly_v2(gracePeriodDuration, interestRate, interestDays, loanAmount, loanMeeting);
            emiInstallments.addAll(generateDecliningInstallmentsAfterInterestOnlyGraceInstallments_v2(gracePeriodDuration, numberOfInstallments, interestRate, interestDays, loanAmount, loanMeeting));
        }
        return emiInstallments;
    }

    /**
     * Calculate the installments after grace period, in the case of principal-only grace type for a declining-interest
     * loan. Calculation is identical to the no-grace scenario except that the number of installments is reduced by the
     * grace period.
     */
    private List<EMIInstallment> generateDecliningInstallmentsAfterInterestOnlyGraceInstallments_v2(Integer gracePeriodDuration, Integer numberOfInstallments, Double interestRate, Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {

        return generateDecliningInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, interestRate, interestDays, loanAmount, loanMeeting);
    }


    /**
     * Generate interest-only payments for the duration of the grace period. Interest paid is on the outstanding
     * balance, which during the grace period is the entire principal amount.
     */
    private List<EMIInstallment> generateDecliningInstallmentsInterestOnly_v2(Integer gracePeriodDuration, Double interestRate, Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = MoneyUtils.zero(loanAmount.getCurrency());
        double interestRateFractional = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);
        for (int i = 0; i < gracePeriodDuration; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setInterest(loanAmount.multiply(interestRateFractional));
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }


    /**
     * Return the list if payment installments for declining interest method, for the number of installments specified.
     */
    private List<EMIInstallment> generateDecliningInstallmentsNoGrace_v2(final int numInstallments, Double interestRate, Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numInstallments, interestRate, interestDays, loanAmount, loanMeeting);

        // Now calculate the details of each installment. These are the exact
        // values, and have not been
        // adjusted for rounding and precision factors.

        Money principalBalance = loanAmount;

        double interestRateFractional = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);
        for (int i = 0; i < numInstallments; i++) {

            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());

            Money interestThisPeriod = principalBalance.multiply(interestRateFractional);
            Money principalThisPeriod = paymentPerPeriod.subtract(interestThisPeriod);

            installment.setInterest(interestThisPeriod);
            installment.setPrincipal(principalThisPeriod);
            principalBalance = principalBalance.subtract(principalThisPeriod);

            emiInstallments.add(installment);
        }

        return emiInstallments;
    }


    /**
     * Generate flat-interest installment variants based on the type of grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest, the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments. The first grace installments are
     * interest-only, and principal is paid off with the remaining installments. NOTE: Principal-only grace period
     * should be disable for release 1.1.
     * </ul>
     */
    private List<EMIInstallment> allFlatInstallments_v2(final Money loanInterest, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numOfInstallments) {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateFlatInstallmentsNoGrace_v2(loanInterest, loanAmount, numOfInstallments);
        } else {
            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.
            emiInstallments = generateFlatInstallmentsInterestOnly_v2(loanInterest, numOfInstallments, gracePeriodDuration);
            emiInstallments.addAll(generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(loanInterest, gracePeriodDuration, loanAmount, numOfInstallments));
        }
        return emiInstallments;
    }

    /**
     * Calculate the installments after grace period, in the case of principal-only grace type for a flat-interest loan.
     * Divide interest evenly among all installments, but divide principle evenly among installments after the grace
     * period.
     */
    private List<EMIInstallment> generateFlatInstallmentsAfterInterestOnlyGraceInstallments_v2(final Money loanInterest, Integer gracePeriodDuration, Money loanAmount, Integer numOfInstallments) {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalPerInstallment = loanAmount.divide(numOfInstallments - gracePeriodDuration);
        Money interestPerInstallment = loanInterest.divide(numOfInstallments);
        for (int i = gracePeriodDuration; i < numOfInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setPrincipal(principalPerInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest is divided evenly among all
     * installments, but only interest is paid during the grace period.
     */
    private List<EMIInstallment> generateFlatInstallmentsInterestOnly_v2(final Money loanInterest, Integer numOfInstallments, Integer gracePeriodDuration) {

        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money zero = MoneyUtils.zero(loanInterest.getCurrency());

        Money interestPerInstallment = loanInterest.divide(numOfInstallments);

        for (int i = 0; i < gracePeriodDuration; i++) {
            EMIInstallment installment = new EMIInstallment(loanInterest.getCurrency());
            installment.setInterest(interestPerInstallment);
            installment.setPrincipal(zero);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }


    /**
     * Divide principal and interest evenly among all installments, no grace period
     */
    private List<EMIInstallment> generateFlatInstallmentsNoGrace_v2(final Money loanInterest, Money loanAmount, Integer numOfInstallments) {
        List<EMIInstallment> emiInstallments = new ArrayList<EMIInstallment>();
        Money principalPerInstallment = loanAmount.divide(numOfInstallments);
        Money interestPerInstallment = loanInterest.divide(numOfInstallments);
        for (int i = 0; i < numOfInstallments; i++) {
            EMIInstallment installment = new EMIInstallment(loanAmount.getCurrency());
            installment.setPrincipal(principalPerInstallment);
            installment.setInterest(interestPerInstallment);
            emiInstallments.add(installment);
        }
        return emiInstallments;
    }


    private Money getLoanInterest_v2(MeetingBO loanMeeting, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, Integer numberOfInstallments, Double interestRate, Integer interestDays, InterestType interestType) throws AccountException {

        Double durationInYears = getTotalDurationInYears_v2(numberOfInstallments, loanMeeting, interestDays);

        Money interest = null;

        switch (interestType) {
        case FLAT:
            interest = loanAmount.multiply(interestRate).multiply(durationInYears).divide(new BigDecimal("100"));
            break;
        case DECLINING:
            interest = getDecliningInterestAmount_v2(loanMeeting, graceType, gracePeriodDuration, numberOfInstallments, loanAmount, interestRate, interestDays);
            break;
        case DECLINING_EPI:
            interest = getDecliningEPIAmount_v2(loanMeeting, graceType, gracePeriodDuration, numberOfInstallments, loanAmount, interestRate, interestDays);
            break;
        default:
            break;
        }

        return interest;
    }

    private Money getDecliningEPIAmount_v2(MeetingBO loanMeeting, GraceType graceType, Integer gracePeriodDuration, Integer numberOfInstallments, Money loanAmount, Double interestRate, Integer interestDays) {

        Money interest = new Money(loanAmount.getCurrency(), "0");
        if (graceType.equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningEPIAmountGrace_v2(loanMeeting, loanAmount, gracePeriodDuration, interestRate, interestDays);
            Money nonGraceInterestPayments = getDecliningEPIAmountNonGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, loanMeeting, interestRate, interestDays);
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        } else {
            interest = getDecliningEPIAmountNonGrace_v2(numberOfInstallments, loanAmount, loanMeeting, interestRate, interestDays);
        }
        return interest;
    }

    // the decliningEPI amount = sum of interests for all installments
    private Money getDecliningEPIAmountNonGrace_v2(final int numNonGraceInstallments, Money loanAmount, MeetingBO loanMeeting, Double productInterestRate, Integer interestDays) {
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numNonGraceInstallments));
        Double interestRate = getInterestFractionalRatePerInstallment_v2(loanMeeting, productInterestRate, interestDays);
        Money totalInterest = new Money(loanAmount.getCurrency(), "0");
        for (int i = 0; i < numNonGraceInstallments; i++) {
            Money interestThisPeriod = principalBalance.multiply(interestRate);
            totalInterest = totalInterest.add(interestThisPeriod);
            principalBalance = principalBalance.subtract(principalPerPeriod);
        }

        return totalInterest;
    }


    // the business rules for DecliningEPI for grace periods are the same as
    // Declining's
    private Money getDecliningEPIAmountGrace_v2(MeetingBO loanMeeting, Money loanAmount, Integer gracePeriodDuration, Double interestRate, Integer interestDays) {
        return getDecliningInterestAmountGrace_v2(loanMeeting, loanAmount, gracePeriodDuration, interestRate, interestDays);
    }

    /**
     * Compute the total interest due on a declining-interest loan. Interest during a principal-only grace period is
     * calculated differently from non-grace-periods.
     * <p>
     * The formula is as follows:
     * <p>
     * The total interest paid is I = Ig + In where Ig = interest paid during any principal-only grace periods In =
     * interest paid during regular payment periods In = A - P A = total amount paid across regular payment periods The
     * formula for computing A is A = p * n where A = total amount paid p = payment per installment n = number of
     * regular (non-grace) installments P = principal i = interest per period
     */
    private Money getDecliningInterestAmount_v2(MeetingBO loanMeeting, GraceType graceType, Integer gracePeriodDuration, Integer numOfInstallments, Money loanAmount, Double interestRate, Integer interestDays) {

        Money nonGraceInterestPayments = getDecliningInterestAmountNonGrace_v2(numOfInstallments - gracePeriodDuration, loanAmount, interestRate, interestDays, loanMeeting);
        Money interest = nonGraceInterestPayments;
        if (graceType.equals(GraceType.PRINCIPALONLYGRACE)) {
            Money graceInterestPayments = getDecliningInterestAmountGrace_v2(loanMeeting, loanAmount, gracePeriodDuration, interestRate, interestDays);
            interest = graceInterestPayments.add(nonGraceInterestPayments);
        }
        return interest;
    }

    private Money getDecliningInterestAmountGrace_v2(MeetingBO loanMeeting, Money loanAmount, Integer gracePeriodDuration, Double interestRate, Integer interestDays) {
        Double interest = getInterestFractionalRatePerInstallment_v2(loanMeeting, interestRate, interestDays);
        return loanAmount.multiply(interest).multiply(Double.valueOf(gracePeriodDuration.toString()));
    }

    private double getInterestFractionalRatePerInstallment_v2(MeetingBO loanMeeting, Double interestRate, Integer interestDays) {
        return interestRate / getDecliningInterestAnnualPeriods_v2(loanMeeting, interestDays) / 100;
    }

    /**
     * Corrects two defects:
     * <ul>
     * <li>period was being rounded to the closest integer because all of the factors involved in the calculation are
     * integers. First, convert the factors to double values.
     * <li>calculation uses the wrong formula for monthly installments. Whether fiscal year is 360 or 365, just consider
     * a month to be 1/12 of a year.
     */
    private double getDecliningInterestAnnualPeriods_v2(MeetingBO loanMeeting, Integer interestDays) {
        RecurrenceType meetingFrequency = loanMeeting.getMeetingDetails().getRecurrenceTypeEnum();

        short recurAfter = loanMeeting.getMeetingDetails().getRecurAfter();

        double period = 0;

        if (meetingFrequency.equals(RecurrenceType.WEEKLY)) {
            period = Double.valueOf(interestDays.toString()) / (7 * recurAfter);
        }
        /*
         * The use of monthly interest here does not distinguish between the 360 (with equal 30 day months) and the 365
         * day year cases. Should it?
         */
        else if (meetingFrequency.equals(RecurrenceType.MONTHLY)) {
            period = recurAfter * 12;
        }

        return period;
    }


    private Money getDecliningInterestAmountNonGrace_v2(final int numNonGraceInstallments, Money loanAmount, Double interestRate, Integer interestDays, MeetingBO loanMeeting) {
        Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numNonGraceInstallments, interestRate, interestDays, loanAmount, loanMeeting);
        Money totalPayments = paymentPerPeriod.multiply((double) numNonGraceInstallments);
        return totalPayments.subtract(loanAmount);
    }

    /*
     * Calculates equal payments per period for fixed payment, declining-interest loan type. Uses formula from
     * http://confluence.mifos.org :9090/display/Main/Declining+Balance+Example+Calcs The formula is copied here: EMI =
     * P * i / [1- (1+i)^-n] where p = principal (amount of loan) i = rate of interest per installment period as a
     * decimal (not percent) n = no. of installments
     *
     * Translated into program variables and method calls:
     *
     * paymentPerPeriod = interestFractionalRatePerPeriod * getLoanAmount() / ( 1 - (1 +
     * interestFractionalRatePerPeriod) ^ (-getNoOfInstallments()))
     *
     * NOTE: Use double here, not BigDecimal, to calculate the factor that getLoanAmount() is multiplied by. Since
     * calculations all involve small quantities, 64-bit precision is sufficient. It is is more accurate to use
     * floating-point, for quantities of small magnitude (say for very small interest rates)
     *
     * NOTE: These calculations do not take into account EPI or grace period adjustments.
     */
    private Money getPaymentPerPeriodForDecliningInterest_v2(final int numInstallments, Double interestRate,
            Integer interestDays, Money loanAmount, MeetingBO loanMeeting) {
        double factor = 0.0;
        if (interestRate == 0.0) {
            Money paymentPerPeriod = loanAmount.divide(numInstallments);
            return paymentPerPeriod;
        }

        Double interestFractionalRatePerInstallment = getInterestFractionalRatePerInstallment_v2(loanMeeting,
                interestRate, interestDays);

        factor = interestFractionalRatePerInstallment
                / (1.0 - Math.pow(1.0 + interestFractionalRatePerInstallment, -numInstallments));

        Money paymentPerPeriod = loanAmount.multiply(factor);
        return paymentPerPeriod;
    }

    private Double getTotalDurationInYears_v2(Integer numOfInstallments, MeetingBO loanMeeting, Integer interestDays) throws AccountException {
        int daysInWeek = 7;
        int daysInMonth = 30;
        int duration = numOfInstallments * loanMeeting.getMeetingDetails().getRecurAfter();

        Double durationInYears = Double.valueOf("0");
        RecurrenceType recurrenceType = loanMeeting.getMeetingDetails().getRecurrenceTypeEnum();
        switch (recurrenceType) {
        case MONTHLY:
            double totalMonthDays = duration * daysInMonth;
            durationInYears = totalMonthDays / AccountConstants.INTEREST_DAYS_360;
            break;
        case WEEKLY:

            if (interestDays != AccountConstants.INTEREST_DAYS_360 && interestDays != AccountConstants.INTEREST_DAYS_365) {
                throw new AccountException(AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
            }

            double totalWeekDays = duration * daysInWeek;
            durationInYears = totalWeekDays / interestDays;

            break;
        case DAILY:
            throw new AccountException(AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
        default:
            throw new AccountException(AccountConstants.NOT_SUPPORTED_DURATION_TYPE);
        }
        return durationInYears;
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

    private MeetingBO createNewMeetingForRepaymentDay(LocalDate disbursementDate,
            final LoanAccountMeetingDto loanAccountActionForm, final CustomerBO customer) {

        MeetingBO newMeetingForRepaymentDay = null;
        Short recurrenceId = Short.valueOf(loanAccountActionForm.getRecurrenceId());

        final int minDaysInterval = new ConfigurationPersistence().getConfigurationKeyValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY).getValue();

        final Date repaymentStartDate = disbursementDate.plusDays(minDaysInterval).toDateMidnight().toDateTime().toDate();
        try {
            if (RecurrenceType.WEEKLY.getValue().equals(recurrenceId)) {
                WeekDay weekDay = WeekDay.getWeekDay(Short.valueOf(loanAccountActionForm.getWeekDay()));
                Short recurEvery = Short.valueOf(loanAccountActionForm.getEveryWeek());
                newMeetingForRepaymentDay = new MeetingBO(weekDay, recurEvery, repaymentStartDate,
                        MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
            } else if (RecurrenceType.MONTHLY.getValue().equals(recurrenceId)) {
                if (loanAccountActionForm.getMonthType().equals("1")) {
                    Short dayOfMonth = Short.valueOf(loanAccountActionForm.getDayOfMonth());
                    Short dayRecurMonth = Short.valueOf(loanAccountActionForm.getDayRecurMonth());
                    newMeetingForRepaymentDay = new MeetingBO(dayOfMonth, dayRecurMonth, repaymentStartDate,
                            MeetingType.LOAN_INSTALLMENT, customer.getCustomerMeeting().getMeeting().getMeetingPlace());
                } else {
                    Short weekOfMonth = Short.valueOf(loanAccountActionForm.getWeekOfMonth());
                    Short everyMonth = Short.valueOf(loanAccountActionForm.getEveryMonth());
                    Short monthRank = Short.valueOf(loanAccountActionForm.getMonthRank());
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
            StaticHibernateUtil.commitTransaction();
        } catch (AccountException e) {
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
            throw new BusinessRuleException(e.getKey(), e);
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
            loandetails.setClientId(individualLoan.getCustomer().getCustomerId().toString());
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

        try {
            PaymentTypeDto paymentType = null;
            for (org.mifos.dto.domain.PaymentTypeDto paymentTypeDto : accountService.getLoanDisbursementTypes()) {
                if (paymentTypeDto.getValue() == paymentTypeId) {
                    paymentType = paymentTypeDto;
                }
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

            accountService.disburseLoans(loanDisbursements, userContext.getPreferredLocale());
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
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
                Double interestRate = loanProduct.getDefInterestRate();
                Short gracePeriodDuration = loanProduct.getGracePeriodDuration();

                checkPermissionForCreate(accountState.getValue(), userContext, userContext.getBranchId(), userContext.getId());

                List<FeeDto> additionalFees = new ArrayList<FeeDto>();
                List<FeeDto> defaultFees = new ArrayList<FeeDto>();

                new LoanProductService().getDefaultAndAdditionalFees(loanProductId, userContext, defaultFees, additionalFees);

                FundBO fund = null;
                List<FeeDto> feeDtos = new ArrayList<FeeDto>();
                List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
                boolean isRedone = false;

                // FIXME - keithw - tidy up constructor and use domain concepts rather than primitives, e.g. money v double, loanpurpose v integer.
                Double maxLoanAmount = Double.valueOf(loanDetail.getMaxLoanAmount());
                Double minLoanAmount = Double.valueOf(loanDetail.getMinLoanAmount());
                Short maxNoOfInstall  =loanDetail.getMaxNoOfInstall();
                Short minNoOfInstall = loanDetail.getMinNoOfInstall();
                LoanBO loan = new LoanBO(userContext, loanProduct, client, accountState, loanAmount, defaultNumOfInstallments,
                        disbursementDate, interestDeductedAtDisbursement, interestRate, gracePeriodDuration, fund, feeDtos,
                        customFields, isRedone, maxLoanAmount, minLoanAmount,
                        loanProduct.getMaxInterestRate(), loanProduct.getMinInterestRate(),
                        maxNoOfInstall, minNoOfInstall, isRepaymentIndepOfMeetingEnabled, null);
                loan.setBusinessActivityId(loanDetail.getLoanPurpose());
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
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId, loanOfficerId));
    }

    /**
     * A struct to hold totals that can be passed around during rounding computations.
     */
    private class RepaymentTotals {
        // rounded or adjusted totals prior to rounding installments
        Money roundedPaymentsDue;
        Money roundedInterestDue;
        Money roundedAccountFeesDue;
        Money roundedMiscFeesDue;
        Money roundedPenaltiesDue;
        Money roundedMiscPenaltiesDue;
        Money roundedPrincipalDue;

        // running totals as installments are rounded
        Money runningPayments = null;
        Money runningAccountFees = null;
        Money runningPrincipal = null;
        Money runningMiscFees = null;
        Money runningPenalties = null;

        public RepaymentTotals(MifosCurrency currency) {
            this.runningPayments = new Money(currency, "0");
            this.runningAccountFees = new Money(currency, "0");
            this.runningPrincipal = new Money(currency, "0");
            this.runningMiscFees = new Money(currency, "0");
            this.runningPenalties = new Money(currency, "0");
        }

        Money getRoundedPaymentsDue() {
            return roundedPaymentsDue;
        }

        void setRoundedPaymentsDue(final Money roundedPaymentsDue) {
            this.roundedPaymentsDue = roundedPaymentsDue;
        }

        void setRoundedInterestDue(final Money roundedInterestDue) {
            this.roundedInterestDue = roundedInterestDue;
        }

        Money getRoundedAccountFeesDue() {
            return roundedAccountFeesDue;
        }

        void setRoundedAccountFeesDue(final Money roundedAccountFeesDue) {
            this.roundedAccountFeesDue = roundedAccountFeesDue;
        }

        Money getRoundedMiscFeesDue() {
            return roundedMiscFeesDue;
        }

        void setRoundedMiscFeesDue(final Money roundedMiscFeesDue) {
            this.roundedMiscFeesDue = roundedMiscFeesDue;
        }

        Money getRoundedPenaltiesDue() {
            return roundedPenaltiesDue;
        }

        Money getRoundedMiscPenaltiesDue() {
            return roundedMiscPenaltiesDue;
        }

        void setRoundedMiscPenaltiesDue(final Money roundedMiscPenaltiesDue) {
            this.roundedMiscPenaltiesDue = roundedMiscPenaltiesDue;
        }

        Money getRoundedPrincipalDue() {
            return roundedPrincipalDue;
        }

        void setRoundedPrincipalDue(final Money roundedPrincipalDue) {
            this.roundedPrincipalDue = roundedPrincipalDue;
        }

        public Money getRoundedInterestDue() {
            return this.roundedInterestDue;
        }
    }
}