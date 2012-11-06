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

package org.mifos.accounts.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.AccountingRules;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class AccountBusinessService implements BusinessService {

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") UserContext userContext) {
        return null;
    }

    public AccountBO findBySystemId(String accountGlobalNum) throws ServiceException {
        try {
            return getlegacyAccountDao().findBySystemId(accountGlobalNum);
        } catch (PersistenceException e) {
            throw new ServiceException(AccountExceptionConstants.FINDBYGLOBALACCNTEXCEPTION, e,
                    new Object[] { accountGlobalNum });
        }
    }

    public AccountTypes getTypeBySystemId(String accountGlobalNum) throws ServiceException {
        AccountBO accountBO = findBySystemId(accountGlobalNum);
        if (accountBO != null) {
            return accountBO.getType();
        }
        return null;
    }

    public AccountBO getAccount(Integer accountId) throws ServiceException {
        try {
            return getlegacyAccountDao().getAccount(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public AccountActionEntity getAccountAction(Short actionType, Short localeId) throws ServiceException {
        AccountActionEntity accountAction = null;
        try {
            accountAction = ApplicationContextProvider.getBean(LegacyMasterDao.class).getPersistentObject(
                    AccountActionEntity.class, actionType);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
        return accountAction;
    }

    public List<AccountStateEntity> retrieveAllAccountStateList(AccountTypes accountTypes) throws ServiceException {
        try {
            return getlegacyAccountDao().retrieveAllAccountStateList(accountTypes.getValue());
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<AccountStateEntity> retrieveAllActiveAccountStateList(AccountTypes accountTypes)
            throws ServiceException {
        try {
            return getlegacyAccountDao().retrieveAllActiveAccountStateList(accountTypes.getValue());
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<AccountCheckListBO> getStatusChecklist(Short accountStatusId, Short accountTypeId)
            throws ServiceException {
        try {
            return getlegacyAccountDao().getStatusChecklist(accountStatusId, accountTypeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<ApplicableCharge> getAppllicableFees(Integer accountId, UserContext userContext)
            throws ServiceException {
        List<ApplicableCharge> applicableChargeList = null;
        try {
            AccountBO account = getlegacyAccountDao().getAccount(accountId);
            FeeCategory categoryType = getCategoryType(account.getCustomer());

            if (account.getType() == AccountTypes.LOAN_ACCOUNT || account.getType() == AccountTypes.GROUP_LOAN_ACCOUNT) {

                applicableChargeList = getLoanApplicableCharges(getlegacyAccountDao().getAllApplicableFees(
                        accountId, FeeCategory.LOAN), userContext, (LoanBO) account);


            } else if (account.getType() == AccountTypes.CUSTOMER_ACCOUNT) {
                if (account.getCustomer().getCustomerMeeting() == null) {
                    throw new ServiceException(AccountExceptionConstants.APPLY_CAHRGE_NO_CUSTOMER_MEETING_EXCEPTION);
                }
                applicableChargeList = getCustomerApplicableCharges(getlegacyAccountDao().getAllApplicableFees(
                        accountId, categoryType), userContext, ((CustomerAccountBO) account).getCustomer()
                        .getCustomerMeeting().getMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId());
            }
            addMiscFeeAndPenalty(applicableChargeList);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
        return applicableChargeList;
    }



    private FeeCategory getCategoryType(CustomerBO customer) {
        if (customer.getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue())) {
            return FeeCategory.CLIENT;
        } else if (customer.getCustomerLevel().getId().equals(CustomerLevel.GROUP.getValue())) {
            return FeeCategory.GROUP;
        } else if (customer.getCustomerLevel().getId().equals(CustomerLevel.CENTER.getValue())) {
            return FeeCategory.CENTER;
        }
        return null;
    }

    private List<ApplicableCharge> getCustomerApplicableCharges(List<FeeBO> feeList, UserContext userContext,
            Short accountMeetingRecurrance) {
        List<ApplicableCharge> applicableChargeList = new ArrayList<ApplicableCharge>();
        if (feeList != null && !feeList.isEmpty()) {
            filterBasedOnRecurranceType(feeList, accountMeetingRecurrance);
            populaleApplicableCharge(applicableChargeList, feeList, userContext);
        }
        return applicableChargeList;
    }

    private List<ApplicableCharge> getLoanApplicableCharges(List<FeeBO> feeList, UserContext userContext, LoanBO loanBO) {
        List<ApplicableCharge> applicableChargeList = new ArrayList<ApplicableCharge>();
        if (feeList != null && !feeList.isEmpty()) {
            Short accountMeetingRecurrance = loanBO.getCustomer().getCustomerMeeting().getMeeting().getMeetingDetails()
                    .getRecurrenceType().getRecurrenceId();
            filterBasedOnRecurranceType(feeList, accountMeetingRecurrance);
            filterDisbursementFee(feeList, loanBO);
            filterTimeOfFirstRepaymentFee(feeList, loanBO);
            if(AccountingRules.isMultiCurrencyEnabled()){
                filterBasedOnCurrencyOfLoan(feeList, loanBO);
            }

            filterForVariableInstallmentLoanType(feeList,loanBO);

            populaleApplicableCharge(applicableChargeList, feeList, userContext);
        }
        return applicableChargeList;
    }

    private void filterForVariableInstallmentLoanType(List<FeeBO> feeList, LoanBO loanBO) {
        if (loanBO.isOfType(AccountTypes.LOAN_ACCOUNT)) {
            if (loanBO.getLoanOffering().isVariableInstallmentsAllowed()) {
                for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
                    FeeBO fee = iter.next();

                    if (fee.isPeriodic()) {
                        iter.remove();
                    } else if (fee.getFeeType().equals(RateAmountFlag.RATE)) {
                        FeeFormula feeFormula = ((RateFeeBO) fee).getFeeFormula().getFeeFormula();
                        if (feeFormula != null) {
                            if (feeFormula.equals(FeeFormula.AMOUNT_AND_INTEREST)
                                    || (feeFormula.equals(FeeFormula.INTEREST))) {
                                iter.remove();
                            }
                        }
                    }

                }
            }
        }
    }

    private void filterBasedOnCurrencyOfLoan(List<FeeBO> feeList, LoanBO loanBO) {
        // remove fees where the currency of fee doesn't match the currency of loan.
        for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
            FeeBO fee = iter.next();
            if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
                if (!((AmountFeeBO) fee).getFeeAmount().getCurrency().equals(loanBO.getCurrency())) {
                    iter.remove();
                }
            }
        }
    }

    private void populaleApplicableCharge(List<ApplicableCharge> applicableChargeList, List<FeeBO> feeList,
            UserContext userContext) {
        for (FeeBO fee : feeList) {
            ApplicableCharge applicableCharge = new ApplicableCharge();
            applicableCharge.setFeeId(fee.getFeeId().toString());
            applicableCharge.setFeeName(fee.getFeeName());
            applicableCharge.setIsPenaltyType(false);
            if (fee.getFeeType().getValue().equals(RateAmountFlag.RATE.getValue())) {
                applicableCharge.setAmountOrRate(new LocalizationConverter().getDoubleStringForInterest(((RateFeeBO) fee).getRate()));
                applicableCharge.setFormula(((RateFeeBO) fee).getFeeFormula().getFormulaStringThatHasName());
                applicableCharge.setIsRateType(true);
            } else {
                applicableCharge.setAmountOrRate(((AmountFeeBO) fee).getFeeAmount().toString());
                applicableCharge.setIsRateType(false);
            }
            MeetingBO meeting = fee.getFeeFrequency().getFeeMeetingFrequency();
            if (meeting != null) {
                applicableCharge
                        .setPeriodicity(new MeetingHelper().getDetailMessageWithFrequency(meeting, userContext));
            } else {
                applicableCharge.setPaymentType(fee.getFeeFrequency().getFeePayment().getName());
            }
            applicableChargeList.add(applicableCharge);
        }

    }

    private void filterBasedOnRecurranceType(List<FeeBO> feeList, Short accountMeetingRecurrance) {
        for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
            FeeBO fee = iter.next();
            if (fee.getFeeFrequency().getFeeFrequencyType().getId().equals(FeeFrequencyType.PERIODIC.getValue())) {
                Short feeRecurrance = fee.getFeeFrequency().getFeeMeetingFrequency().getMeetingDetails()
                        .getRecurrenceTypeEnum().getValue();
                if (!feeRecurrance.equals(accountMeetingRecurrance)) {
                    iter.remove();
                }
            }
        }
    }

    private void filterDisbursementFee(List<FeeBO> feeList, AccountBO account) {
        for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
            FeeBO fee = iter.next();
            FeePaymentEntity feePaymentEntity = fee.getFeeFrequency().getFeePayment();
            if (feePaymentEntity != null) {
                Short paymentType = feePaymentEntity.getId();
                if (paymentType.equals(FeePayment.TIME_OF_DISBURSEMENT.getValue())) {
                    AccountState accountState = account.getState();
                    if (accountState == AccountState.LOAN_PARTIAL_APPLICATION
                            || accountState == AccountState.LOAN_PENDING_APPROVAL
                            || accountState == AccountState.LOAN_APPROVED
                            || accountState == AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER) {
                        continue;
                    } else {
                        iter.remove();
                    }
                }
            }
        }
    }

    private void filterTimeOfFirstRepaymentFee(List<FeeBO> feeList, LoanBO loanBO) {
        for (Iterator<FeeBO> iter = feeList.iterator(); iter.hasNext();) {
            FeeBO fee = iter.next();
            FeePaymentEntity feePaymentEntity = fee.getFeeFrequency().getFeePayment();
            if (feePaymentEntity != null) {
                Short paymentType = feePaymentEntity.getId();
                if (paymentType.equals(FeePayment.TIME_OF_FIRSTLOANREPAYMENT.getValue())
                        && loanBO.isCurrentDateGreaterThanFirstInstallment()) {
                    iter.remove();
                }
            }

        }
    }

    private void addMiscFeeAndPenalty(List<ApplicableCharge> applicableChargeList) {
        ApplicableCharge applicableCharge = new ApplicableCharge();
        applicableCharge.setFeeId(AccountConstants.MISC_FEES);
        applicableCharge.setFeeName("Misc Fees");
        applicableCharge.setIsRateType(false);
        applicableCharge.setIsPenaltyType(false);
        applicableChargeList.add(applicableCharge);
        applicableCharge = new ApplicableCharge();
        applicableCharge.setFeeId(AccountConstants.MISC_PENALTY);
        applicableCharge.setFeeName("Misc Penalty");
        applicableCharge.setIsRateType(false);
        applicableCharge.setIsPenaltyType(false);
        applicableChargeList.add(applicableCharge);
    }

    public String getStatusName(AccountState accountState, AccountTypes accountType) {
        return AccountStateMachines.getInstance().getAccountStatusName(accountState, accountType);
    }

    public String getFlagName(AccountStateFlag accountStateFlag, AccountTypes accountType) {
        return AccountStateMachines.getInstance().getAccountFlagName(accountStateFlag, accountType);
    }

    public List<AccountStateEntity> getStatusList(AccountStateEntity accountStateEntity, AccountTypes accountType,
            Short localeId) {
        List<AccountStateEntity> statusList = AccountStateMachines.getInstance().getStatusList(accountStateEntity, accountType);
        return statusList;
    }

    public void checkPermissionForStatusChange(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) throws ServiceException {
        if (!isPermissionAllowedForStatusChange(newState, userContext, flagSelected, recordOfficeId,
                recordLoanOfficerId)) {
            throw new ServiceException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowedForStatusChange(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return getActivityMapper().isStateChangePermittedForAccount(newState.shortValue(),
                null != flagSelected ? flagSelected.shortValue() : 0, userContext, recordOfficeId, recordLoanOfficerId);
    }

    public void checkPermissionForAdjustment(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws ServiceException {
        if (!isPermissionAllowedForAdjustment(accountTypes, customerLevel, userContext, recordOfficeId,
                recordLoanOfficerId)) {
            throw new ServiceException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowedForAdjustment(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return getActivityMapper().isAdjustmentPermittedForAccounts(accountTypes, customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    ActivityMapper getActivityMapper() {
        return ActivityMapper.getInstance();
    }

    public void checkPermissionForWaiveDue(WaiveEnum waiveEnum, AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowedForWaiveDue(waiveEnum, accountTypes, customerLevel, userContext, recordOfficeId,
                recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowedForWaiveDue(WaiveEnum waiveEnum, AccountTypes accountTypes,
            CustomerLevel customerLevel, UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return getActivityMapper().isWaiveDuePermittedForCustomers(waiveEnum, accountTypes, customerLevel,
                userContext, recordOfficeId, recordLoanOfficerId);
    }

    public void checkPermissionForRemoveFees(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowedForRemoveFees(accountTypes, customerLevel, userContext, recordOfficeId,
                recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowedForRemoveFees(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return getActivityMapper().isRemoveFeesPermittedForAccounts(accountTypes, customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    private LegacyAccountDao getlegacyAccountDao() {
        return ApplicationContextProvider.getBean(LegacyAccountDao.class);
    }

    public List<CustomerBO> getCoSigningClientsForGlim(Integer accountId) throws ServiceException {
        try {
            return getlegacyAccountDao().getCoSigningClientsForGlim(accountId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public void checkPermissionForAdjustmentOnBackDatedPayments(Date lastPaymentDate, UserContext userContext,
                                                                Short recordOfficeId, Short recordLoanOfficer) throws ServiceException{
        if (!getActivityMapper().isAdjustmentPermittedForBackDatedPayments(lastPaymentDate, userContext, recordOfficeId, recordLoanOfficer)) {
            throw new ServiceException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    public Object checkPermissionForRemovePenalties(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return getActivityMapper().isRemovePenaltiesPermittedForAccounts(accountTypes, customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }
}
