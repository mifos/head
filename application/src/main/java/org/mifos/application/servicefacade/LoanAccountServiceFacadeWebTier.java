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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.AccountingRules;
import org.mifos.config.business.service.ConfigurationBusinessService;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.InstallmentDetailsDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.AccountFeesDto;
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
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoanAccountServiceFacadeWebTier implements LoanAccountServiceFacade {

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
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    public LoanAccountServiceFacadeWebTier(final LoanProductDao loanProductDao, final CustomerDao customerDao,
            PersonnelDao personnelDao, FundDao fundDao, final LoanDao loanDao,
            InstallmentsValidator installmentsValidator, ScheduleCalculatorAdaptor scheduleCalculatorAdaptor,
            LoanBusinessService loanBusinessService, HolidayServiceFacade holidayServiceFacade,
            LoanPrdBusinessService loanPrdBusinessService) {
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
    public void updateLoanAccountStatus(AccountUpdateStatus updateStatus) {
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

            CustomValueDto customValueDto = new MasterPersistence().getLookUpEntity(MasterConstants.COLLATERAL_TYPES,
                    userContext.getLocaleId());
            List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();

            // Business activities got in getPrdOfferings also but only for glim.
            List<ValueListElement> loanPurposes = new MasterDataService().retrieveMasterEntities(
                    MasterConstants.LOAN_PURPOSES, userContext.getLocaleId());

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
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
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
    public LoanCreationResultDto createLoan(LoanAccountMeetingDto loanAccountMeetingDto,
                                            LoanAccountInfoDto loanAccountInfo,
                                            List<LoanScheduledInstallmentDto> loanRepayments) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

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

        List<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        for (LoanScheduledInstallmentDto installment : loanRepayments) {
            RepaymentScheduleInstallment repaymentScheduleInstallment = RepaymentScheduleInstallment.createForScheduleCopy(installment.getInstallmentNumber(), installment.getPrincipal(), installment.getInterest(), installment.getDueDate(), userContext.getPreferredLocale(), loan.getCurrency());
            installments.add(repaymentScheduleInstallment);
        }

        loan.copyInstallmentSchedule(installments);

        try {
            StaticHibernateUtil.startTransaction();
            PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan
                    .getAccountState(), createdBy, loan));
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            loan.setGlobalAccountNum(loan.generateId(userContext.getBranchGlobalNum()));
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

            // FIXME - PUT BACK IN.
//            List<FeeDto> fees = loanActionForm.getFeesToApply();
            List<FeeDto> fees = new ArrayList<FeeDto>();
            List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

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
                    fund, fees, customFields, maxLoanAmount, minLoanAmount, maxNumOfInstallments,
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

    private boolean isPermissionAllowed(final Short newSate, final UserContext userContext, final Short officeId,
            final Short loanOfficerId) {
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId, loanOfficerId));
    }

    @Override
    public LoanCreationResultDto redoLoan(LoanAccountMeetingDto loanAccountMeetingDto, LoanAccountInfoDto loanAccountInfoDto,
            List<LoanPaymentDto> existingLoanPayments) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfoDto.getCustomerId());
        LoanBO loan = redoLoan(customer, loanAccountMeetingDto, loanAccountInfoDto, existingLoanPayments);

        StaticHibernateUtil.startTransaction();
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        try {
            loan.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(loan.getAccountState(), loan
                    .getAccountState(), createdBy, loan));
            this.loanDao.save(loan);
            StaticHibernateUtil.flushSession();

            loan.setGlobalAccountNum(loan.generateId(userContext.getBranchGlobalNum()));
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

    private LoanBO redoLoan(CustomerBO customer, LoanAccountMeetingDto loanAccountMeetingDto, LoanAccountInfoDto loanAccountActionForm, List<LoanPaymentDto> existingLoanPayments) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        try {
            boolean isRepaymentIndepOfMeetingEnabled = new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();

            MeetingBO newMeetingForRepaymentDay = null;
            if (isRepaymentIndepOfMeetingEnabled) {
                newMeetingForRepaymentDay = createNewMeetingForRepaymentDay(loanAccountActionForm.getDisbursementDate(), loanAccountMeetingDto, customer);
            }

            Short productId = loanAccountActionForm.getProductId();

            LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering(productId, userContext.getLocaleId());

            Money loanAmount = new Money(loanOffering.getCurrency(), loanAccountActionForm.getLoanAmount());
            Short numOfInstallments = loanAccountActionForm.getNumOfInstallments();
            boolean isInterestDeductedAtDisbursement = loanAccountActionForm.isInterestDeductedAtDisbursement();
            Double interest = loanAccountActionForm.getInterest();
            Short gracePeriod = loanAccountActionForm.getGracePeriod();
            // FIXME - put back in fees
//            List<FeeDto> fees = loanAccountActionForm.getFeesToApply();
            List<FeeDto> fees = new ArrayList<FeeDto>();
            Double maxLoanAmount = Double.valueOf(loanAccountActionForm.getMaxLoanAmount());
            Double minLoanAmount = Double.valueOf(loanAccountActionForm.getMinLoanAmount());
            Short maxNumOfInstallments = loanAccountActionForm.getMaxNumOfInstallments();
            Short minNumOfShortInstallments = loanAccountActionForm.getMinNumOfInstallments();
            String externalId = loanAccountActionForm.getExternalId();
            Integer selectedLoanPurpose = loanAccountActionForm.getSelectedLoanPurpose();
            String collateralNote = loanAccountActionForm.getCollateralNote();
            Integer selectedCollateralType = loanAccountActionForm.getSelectedCollateralType();

            AccountState accountState = null;
            Short accountStateValue = loanAccountActionForm.getAccountState();
            if (accountStateValue != null) {
                accountState = AccountState.fromShort(accountStateValue);
            } else {
                accountState = AccountState.LOAN_PARTIAL_APPLICATION;
            }

            FundBO fund = null;
            Short fundId = loanAccountActionForm.getFundId();
            if (fundId != null) {
                fund = this.fundDao.findById(fundId);
            }

            LoanBO redoLoan = LoanBO.redoLoan(userContext, loanOffering, customer, accountState, loanAmount,
                    numOfInstallments, loanAccountActionForm.getDisbursementDate().toDateMidnight().toDate(),
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
    public void checkIfProductsOfferingCanCoexist(Integer loanAccountId) {
        try {
            LoanBO loan = this.loanDao.findById(loanAccountId);
            new ProductMixValidator().checkIfProductsOfferingCanCoexist(loan);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public LoanDisbursalDto retrieveLoanDisbursalDetails(Integer loanAccountId) {

        LoanBO loan = this.loanDao.findById(loanAccountId);

        Date proposedDate = new DateTimeService().getCurrentJavaDateTime();
        if (AccountingRules.isBackDatedTxnAllowed()) {
            proposedDate = loan.getDisbursementDate();
        }

        return new LoanDisbursalDto(loan.getAccountId(), proposedDate, loan.getLoanAmount().toString(), loan.getAmountTobePaidAtdisburtail().toString());
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
    public void makeEarlyRepayment(String globalAccountNum, String earlyRepayAmountStr,
                                   String receiptNumber, java.sql.Date receiptDate,
                                   String paymentTypeId, Short userId, boolean waiveOffInterest) {

        try {
            LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
            if (waiveOffInterest && !loan.isInterestWaived()) {
                throw new BusinessRuleException(LoanConstants.WAIVER_INTEREST_NOT_CONFIGURED);
            }
            Money earlyRepayAmount = new Money(loan.getCurrency(), earlyRepayAmountStr);

            loan.makeEarlyRepayment(earlyRepayAmount, receiptNumber, receiptDate, paymentTypeId, userId, waiveOffInterest);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    public LoanInformationDto retrieveLoanInformation(String globalAccountNum) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
        String fundName = null;
        if (loan.getFund() != null) {
            fundName = loan.getFund().getFundName();
        }

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        boolean activeSurveys = surveysPersistence.isActiveSurveysForSurveyType(SurveyType.LOAN);
        List<SurveyDto> accountSurveys = loanDao.getAccountSurveyDto(loan.getAccountId());

        LoanSummaryDto loanSummary = new LoanSummaryDto(loan.getLoanSummary().getOriginalPrincipal().toString(), loan.getLoanSummary().getPrincipalPaid().toString(),
                                                        loan.getLoanSummary().getPrincipalDue().toString(), loan.getLoanSummary().getOriginalInterest().toString(),
                                                        loan.getLoanSummary().getInterestPaid().toString(), loan.getLoanSummary().getInterestDue().toString(),
                                                        loan.getLoanSummary().getOriginalFees().toString(), loan.getLoanSummary().getFeesPaid().toString(),
                                                        loan.getLoanSummary().getFeesDue().toString(), loan.getLoanSummary().getOriginalPenalty().toString(),
                                                        loan.getLoanSummary().getPenaltyPaid().toString(), loan.getLoanSummary().getPenaltyDue().toString(),
                                                        loan.getLoanSummary().getTotalLoanAmnt().toString(), loan.getLoanSummary().getTotalAmntPaid().toString(),
                                                        loan.getLoanSummary().getTotalAmntDue().toString());

        LoanPerformanceHistoryDto loanPerformanceHistory = new LoanPerformanceHistoryDto(loan.getPerformanceHistory().getNoOfPayments(),
                                                                                        loan.getPerformanceHistory().getTotalNoOfMissedPayments(),
                                                                                        loan.getPerformanceHistory().getDaysInArrears(),
                                                                                        loan.getPerformanceHistory().getLoanMaturityDate());

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
        Money repaymentAmount = loan.getEarlyRepayAmount();
        Money waivedRepaymentAmount = repaymentAmount.subtract(loan.waiverAmount());

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
}