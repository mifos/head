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

package org.mifos.accounts.servicefacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.business.RatePenaltyBO;
import org.mifos.accounts.penalties.persistence.PenaltyDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AdjustedPaymentDto;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentDto;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.dto.screen.AccountTypeCustomerLevelDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Concrete implementation of service to manipulate accounts from the presentation layer.
 *
 */
public class WebTierAccountServiceFacade implements AccountServiceFacade {
    private AccountService accountService;
    private HibernateTransactionHelper transactionHelper;
    private AccountBusinessService accountBusinessService;
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;
    private LegacyAcceptedPaymentTypeDao acceptedPaymentTypePersistence;
    private LegacyPersonnelDao personnelPersistence;
    private LegacyAccountDao legacyAccountDao;
    private MonthClosingServiceFacade monthClosingServiceFacade;
    private ClientServiceFacade clientServiceFacade;
    private SavingsServiceFacade savingsServiceFacade;

    @Autowired
    private PersonnelDao personnelDao;
    
    @Autowired
    private LoanDao loanDao;
    
    @Autowired
    private FeeDao feeDao;
    
    @Autowired
    private PenaltyDao penaltyDao;

    @Autowired
    public WebTierAccountServiceFacade(AccountService accountService, HibernateTransactionHelper transactionHelper,
                                       AccountBusinessService accountBusinessService,
                                       ScheduleCalculatorAdaptor scheduleCalculatorAdaptor,
                                       LegacyAcceptedPaymentTypeDao acceptedPaymentTypePersistence,
                                       LegacyPersonnelDao personnelPersistence, LegacyAccountDao legacyAccountDao,
                                       MonthClosingServiceFacade monthClosingServiceFacade,
                                       ClientServiceFacade clientServiceFacade, SavingsServiceFacade savingsServiceFacade) {
        this.accountService = accountService;
        this.transactionHelper = transactionHelper;
        this.accountBusinessService = accountBusinessService;
        this.scheduleCalculatorAdaptor = scheduleCalculatorAdaptor;
        this.acceptedPaymentTypePersistence = acceptedPaymentTypePersistence;
        this.personnelPersistence = personnelPersistence;
        this.legacyAccountDao = legacyAccountDao;
        this.monthClosingServiceFacade = monthClosingServiceFacade;
        this.clientServiceFacade = clientServiceFacade;
        this.savingsServiceFacade = savingsServiceFacade;
    }

    @Override
    public AccountPaymentDto getAccountPaymentInformation(Integer accountId, String paymentType, Short localeId, UserReferenceDto userReferenceDto, Date paymentDate) {
        try {
            AccountBO account = accountBusinessService.getAccount(accountId);
            CustomerDto customer = account.getCustomer().toCustomerDto();

            List<SavingsDetailDto> savingsInUse = clientServiceFacade.retrieveSavingsInUseForClient(customer.getCustomerId());
            List<SavingsDetailDto> accountsForTransfer = new ArrayList<SavingsDetailDto>();
            if (savingsInUse != null) {
                for (SavingsDetailDto savingsAccount : savingsInUse) {
                    if (savingsAccount.getAccountStateId().equals(AccountState.SAVINGS_ACTIVE.getValue())) {
                        accountsForTransfer.add(savingsAccount);
                    }
                }
            }

            if (isLoanPayment(paymentType)){
                scheduleCalculatorAdaptor.computeExtraInterest((LoanBO) account, paymentDate);
            }

            UserReferenceDto accountUser = userReferenceDto;
            if (account.getPersonnel() != null) {
                accountUser = new UserReferenceDto(account.getPersonnel().getPersonnelId());
            }

            List<ListItem<Short>> paymentTypeList = constructPaymentTypeList(paymentType, localeId);
            AccountTypeDto accountType = AccountTypeDto.getAccountType(account.getAccountType().getAccountTypeId());
            String totalPaymentDue = account.getTotalPaymentDue().toString();
            clearSessionAndRollback();

            return new AccountPaymentDto(accountType, account.getVersionNo(), paymentTypeList, totalPaymentDue,
                    accountUser, getLastPaymentDate(account), accountsForTransfer, customer);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    public Date retrieveLatPaymentDate(String globalAccountNum) {
        try {
            AccountBO account = accountBusinessService.findBySystemId(globalAccountNum);
            return getLastPaymentDate(account);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }
    
    private Date getLastPaymentDate(AccountBO account) {
        Date lastPaymentDate = new Date(0);
        AccountPaymentEntity lastPayment = account.findMostRecentNonzeroPaymentByPaymentDate();
        if(lastPayment != null){
            lastPaymentDate = lastPayment.getPaymentDate();
        }
        return lastPaymentDate;
    }

    // Exposed for testing
    void clearSessionAndRollback() {
        StaticHibernateUtil.getSessionTL().clear();
        transactionHelper.rollbackTransaction();
    }

    private boolean isLoanPayment(String paymentType) {
        return paymentType.equals(Constants.LOAN);
    }

    @Override
    public List<ListItem<Short>> constructPaymentTypeListForLoanRepayment(Short localeId) {
       try { 
            List<PaymentTypeEntity> paymentTypeList = acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(
                    localeId, TrxnTypes.loan_repayment.getValue());
            List<ListItem<Short>> listItems = new ArrayList<ListItem<Short>>();
            for (PaymentTypeEntity paymentTypeEntity : paymentTypeList) {
                listItems.add(new ListItem<Short>(paymentTypeEntity.getId(), paymentTypeEntity.getName()));
            }
            return listItems;
       } catch (PersistenceException e) {
           throw new MifosRuntimeException(e);
       }
    }
    
    private List<ListItem<Short>> constructPaymentTypeList(String paymentType, Short localeId) {

        try {
            List<PaymentTypeEntity> paymentTypeList = null;
            if (paymentType != null && !Constants.EMPTY_STRING.equals(paymentType.trim())) {
                if (isLoanPayment(paymentType)) {
                    paymentTypeList = acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(
                            localeId, TrxnTypes.loan_repayment.getValue());
                } else {
                    paymentTypeList = acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(
                            localeId, TrxnTypes.fee.getValue());
                }
            }

            List<ListItem<Short>> listItems = new ArrayList<ListItem<Short>>();
            for (PaymentTypeEntity paymentTypeEntity : paymentTypeList) {
                listItems.add(new ListItem<Short>(paymentTypeEntity.getId(), paymentTypeEntity.getName()));
            }
            return listItems;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public boolean isPaymentPermitted(Integer accountId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            AccountBO account = new AccountBusinessService().getAccount(accountId);

            CustomerLevel customerLevel = null;
            if (account.getType().equals(AccountTypes.CUSTOMER_ACCOUNT)) {
                customerLevel = account.getCustomer().getLevel();
            }

            Short personnelId = userContext.getId();
            if (account.getPersonnel() != null) {
                personnelId = account.getPersonnel().getPersonnelId();
            }

            return ActivityMapper.getInstance().isPaymentPermittedForAccounts(account.getType(), customerLevel,
                    userContext, account.getOffice().getOfficeId(), personnelId);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<ApplicableCharge> getApplicableFees(Integer accountId) {
        try {
            MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserContext userContext = toUserContext(user);

            AccountBO account = this.accountBusinessService.getAccount(accountId);
            
            try {
                personnelDao.checkAccessPermission(userContext, account.getOfficeId(), account.getCustomer().getLoanOfficerId());
            } catch (AccountException e) {
                throw new MifosRuntimeException(e.getMessage(), e);
            }
            
            return new AccountBusinessService().getAppllicableFees(accountId, userContext);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    @Override
    public void applyCharge(Integer accountId, Short chargeId, Double chargeAmount, boolean isPenaltyType) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            AccountBO account = new AccountBusinessService().getAccount(accountId);
            
            if (account instanceof LoanBO && !account.isGroupLoanAccount()) {
                List<LoanBO> individualLoans = this.loanDao.findIndividualLoans(account.getAccountId());

                if (individualLoans != null && individualLoans.size() > 0) {
                    for (LoanBO individual : individualLoans) {
                        individual.updateDetails(userContext);

                        if (isPenaltyType && !chargeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                            PenaltyBO penalty = this.penaltyDao.findPenaltyById(chargeId.intValue());
                            individual.addAccountPenalty(new AccountPenaltiesEntity(individual, penalty, chargeAmount));
                        } else {
                            FeeBO fee = this.feeDao.findById(chargeId);

                            if (fee instanceof RateFeeBO) {
                                individual.applyCharge(chargeId, chargeAmount);
                            } else {
                                Double radio = individual.getLoanAmount().getAmount().doubleValue()
                                        / ((LoanBO) account).getLoanAmount().getAmount().doubleValue();

                                individual.applyCharge(chargeId, chargeAmount * radio);
                            }
                        }
                    }
                }
            }
            
            account.updateDetails(userContext);

            CustomerLevel customerLevel = null;
            if (account.isCustomerAccount()) {
                customerLevel = account.getCustomer().getLevel();
            }
            if (account.getPersonnel() != null) {
                checkPermissionForApplyCharges(account.getType(), customerLevel, userContext,
                        account.getOffice().getOfficeId(), account.getPersonnel().getPersonnelId());
            } else {
                checkPermissionForApplyCharges(account.getType(), customerLevel, userContext,
                        account.getOffice().getOfficeId(), userContext.getId());
            }

            this.transactionHelper.startTransaction();
            
            if(isPenaltyType && account instanceof LoanBO) {
                PenaltyBO penalty = this.penaltyDao.findPenaltyById(chargeId.intValue());
                ((LoanBO) account).addAccountPenalty(new AccountPenaltiesEntity(account, penalty, chargeAmount));
            } else {
                account.applyCharge(chargeId, chargeAmount);
            }
            
            this.transactionHelper.commitTransaction();
        } catch (ServiceException e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private void checkPermissionForApplyCharges(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(accountTypes, customerLevel, userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isApplyChargesPermittedForAccounts(accountTypes, customerLevel,
                userContext, recordOfficeId, recordLoanOfficerId);
    }

    @Override
    public AccountTypeCustomerLevelDto getAccountTypeCustomerLevelDto(Integer accountId) {

        try {
            AccountBO account = new AccountBusinessService().getAccount(accountId);
            return new AccountTypeCustomerLevelDto(account.getType().getValue(), account.getCustomer().getCustomerLevel().getId());
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void makePayment(AccountPaymentParametersDto accountPaymentParametersDto) {
        this.accountService.makePayment(accountPaymentParametersDto);
    }

    @Override
    public void applyAdjustment(String globalAccountNum, String adjustmentNote, Short loggedInUser) {
        try {
            AccountBO account = accountBusinessService.findBySystemId(globalAccountNum);
            AccountPaymentEntity lastPayment = account.getLastPmntToBeAdjusted();
            applyHistoricalAdjustment(globalAccountNum, (lastPayment == null) ? null : lastPayment.getPaymentId(),
                    adjustmentNote, loggedInUser, null);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void checkPermissionForAdjustment(AccountBO accountBO) throws ServiceException {
        AccountPaymentEntity lastPmntToBeAdjusted = accountBO.getLastPmntToBeAdjusted();
        if (lastPmntToBeAdjusted == null) return;
        UserContext userContext = accountBO.getUserContext();
        Date lastPaymentDate = lastPmntToBeAdjusted.getPaymentDate();
        PersonnelBO personnel = accountBO.getPersonnel();
        Short personnelId = personnel != null? personnel.getPersonnelId() : userContext.getId();
        Short officeId = accountBO.getOfficeId();
        accountBusinessService.checkPermissionForAdjustment(AccountTypes.LOAN_ACCOUNT, null, userContext, officeId, personnelId);
        accountBusinessService.checkPermissionForAdjustmentOnBackDatedPayments(lastPaymentDate, userContext, officeId, personnelId);
    }

    UserContext getUserContext() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserContextFactory().create(user);
    }

    /**
     * adjustment for member account payment has to be handled along with parent account payments and other members payments.
     * This method prepares data and run applyHistoricalAdjustment so adjustment will be made for parent payment context.
     */
    @Override
    @PreAuthorize("isFullyAuthenticated()")
    public void applyMemberAccountHistoricalAdjustment(String memberGlobalAccountNum, Integer memberPaymentId, String adjustmentNote,
            Short personnelId, AdjustedPaymentDto adjustedPaymentDto) {
        
        try {
            LoanBO memberAccount = (LoanBO) accountBusinessService.findBySystemId(memberGlobalAccountNum);
            LoanBO parentAccount = memberAccount.getParentAccount();
            
            if (parentAccount == null){
                throw new AccountException(LoanExceptionConstants.NO_PARENT_ACCOUNT_EXCEPTION);
            }
            
            AccountPaymentEntity parentPaymentEntity = memberAccount.findParentPaymentByMemberPaymentId(memberPaymentId);
            List<AdjustedPaymentDto> membersAdjustedPaymentDtoList = new ArrayList<AdjustedPaymentDto>();
            
            for (AccountPaymentEntity memberPayment : parentPaymentEntity.getMemberPayments()){
                if (memberPayment.getAccount().getAccountId().equals(memberAccount.getAccountId())){
                    membersAdjustedPaymentDtoList.add(new AdjustedPaymentDto(adjustedPaymentDto.getAmount(), adjustedPaymentDto.getPaymentDate(),
                            adjustedPaymentDto.getPaymentType(), memberAccount.getAccountId()));
                } else {
                    membersAdjustedPaymentDtoList.add(new AdjustedPaymentDto(memberPayment.getAmount().getAmount().toString(), adjustedPaymentDto.getPaymentDate(),
                            adjustedPaymentDto.getPaymentType(), memberPayment.getAccount().getAccountId()));
                }
                
            }
            
            BigDecimal parentAmountSubstraction = memberAccount.findPaymentById(memberPaymentId).getAmount().getAmount().subtract((new BigDecimal(adjustedPaymentDto.getAmount())));
            String newParentAmount = parentPaymentEntity.getAmount().getAmount().subtract(parentAmountSubstraction).toString();
            AdjustedPaymentDto parentAdjustedPaymentDto = new AdjustedPaymentDto(newParentAmount, adjustedPaymentDto.getPaymentDate(), 
                    adjustedPaymentDto.getPaymentType(), parentAccount.getAccountId(), membersAdjustedPaymentDtoList);
            
            this.applyHistoricalAdjustment(parentAccount.getGlobalAccountNum(), parentPaymentEntity.getPaymentId(), adjustmentNote, personnelId, parentAdjustedPaymentDto);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        } catch (AccountException e) {
            throw new MifosRuntimeException(e);
        }
        
        
    }

    @Override
    public void applyHistoricalAdjustment(String globalAccountNum, Integer paymentId, String adjustmentNote,
            Short personnelId, AdjustedPaymentDto adjustedPaymentDto) {        
        try {            
            AccountBO accountBO = accountBusinessService.findBySystemId(globalAccountNum);
            accountBO.setUserContext(getUserContext());
            checkPermissionForAdjustment(accountBO);
            PersonnelBO personnelBO = personnelPersistence.findPersonnelById(personnelId);

            AccountPaymentEntity accountPaymentEntity = accountBO.findPaymentById(paymentId);
            if (accountPaymentEntity == null) {
                throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
            }
            monthClosingServiceFacade.validateTransactionDate(accountPaymentEntity.getPaymentDate());

            PaymentDto otherTransferPayment = accountPaymentEntity.getOtherTransferPaymentDto();

            transactionHelper.flushAndClearSession(); //flush to avoid proxy casting problems
            transactionHelper.startTransaction();

            Integer newSavingsPaymentId = null;
            if (otherTransferPayment != null) {
                SavingsAdjustmentDto savingsAdjustment = new SavingsAdjustmentDto(otherTransferPayment.getAccountId().longValue(),
                        (adjustedPaymentDto == null) ? 0 : Double.valueOf(adjustedPaymentDto.getAmount()), adjustmentNote, otherTransferPayment.getPaymentId(),
                                (adjustedPaymentDto == null) ? otherTransferPayment.getPaymentDate() : new LocalDate(adjustedPaymentDto.getPaymentDate()));
                PaymentDto newSavingsPayment = this.savingsServiceFacade.adjustTransaction(savingsAdjustment, true);
                newSavingsPaymentId = (newSavingsPayment == null) ? null : newSavingsPayment.getPaymentId();
            }

            //reload after flush & clear
            accountBO = accountBusinessService.findBySystemId(globalAccountNum);
            accountBO.setUserContext(getUserContext());

            AccountPaymentEntity adjustedPayment = null;
            Integer adjustedId;
            Stack<PaymentData> paymentsToBeReapplied = new Stack<PaymentData>();
            
            Map<Integer, Stack<PaymentData>> memberPaymentsToBeReappliedMap = new HashMap<Integer, Stack<PaymentData>>();
            if (accountBO.isGroupLoanAccount()){
                for (LoanBO memberAccount : ((LoanBO)accountBO).getMemberAccounts()){
                    Stack<PaymentData> memberPaymentsToBeReapplied = new Stack<PaymentData>();
                    memberPaymentsToBeReappliedMap.put(memberAccount.getAccountId(), memberPaymentsToBeReapplied);
                }
            }
            
            do {
                adjustedPayment = accountBO.getLastPmntToBeAdjusted();

                if (adjustedPayment == null) {
                    break;
                }

                adjustedId = adjustedPayment.getPaymentId();
                if (!accountPaymentEntity.getPaymentId().equals(adjustedId)) {
                    PersonnelBO paymentCreator = (adjustedPayment.getCreatedByUser() == null) ? personnelBO : adjustedPayment.getCreatedByUser();

                    PaymentData paymentData = accountBO.createPaymentData(adjustedPayment.getAmount(), adjustedPayment.getPaymentDate(), 
                            adjustedPayment.getReceiptNumber(), adjustedPayment.getReceiptDate(), adjustedPayment.getPaymentType().getId(), 
                            paymentCreator);
                    paymentData.setOtherTransferPayment(adjustedPayment.getOtherTransferPayment());

                    paymentsToBeReapplied.push(paymentData);
                    
                    // handling new Group Loan Members payments
                    for (AccountPaymentEntity memberAdjustedPayment : adjustedPayment.getMemberPayments()){
                        PaymentData memberPaymentData = memberAdjustedPayment.getAccount().createPaymentData(memberAdjustedPayment.getAmount(), adjustedPayment.getPaymentDate(), 
                                adjustedPayment.getReceiptNumber(), adjustedPayment.getReceiptDate(), adjustedPayment.getPaymentType().getId(), 
                                paymentCreator);
                        memberPaymentsToBeReappliedMap.get(memberAdjustedPayment.getAccount().getAccountId()).push(memberPaymentData);
                    }
                    
                }

                transactionHelper.flushAndClearSession();
                //reload after flush & clear
                accountBO = accountBusinessService.findBySystemId(globalAccountNum);
                accountBO.setUserContext(getUserContext());
                accountBO.adjustLastPayment(adjustmentNote, personnelBO);
                
                legacyAccountDao.createOrUpdate(accountBO);
                
                //adjust New Group Loan member payments
                if (accountBO.isGroupLoanAccount()){
                    for (LoanBO memberAccount : ((LoanBO)accountBO).getMemberAccounts()){
                        
                        AccountPaymentEntity memberPayment = memberAccount.getLastPmntToBeAdjusted();
                        if (memberPayment.getParentPaymentId() == null ||
                                !memberPayment.getParentPaymentId().getPaymentId().equals(accountPaymentEntity.getPaymentId())) {
                            continue;
                        }
                        
                        memberAccount.setUserContext(getUserContext());
                        memberAccount.adjustLastPayment(adjustmentNote, personnelBO);
                        legacyAccountDao.createOrUpdate(memberAccount);
                    }
                }
                transactionHelper.flushSession();
            } while (!accountPaymentEntity.getPaymentId().equals(adjustedId));

            if (adjustedPaymentDto != null) {
                //reapply adjusted payment
                PersonnelBO paymentCreator = (accountPaymentEntity.getCreatedByUser() == null) ? personnelBO : accountPaymentEntity.getCreatedByUser();
                Money amount = new Money(accountBO.getCurrency(), adjustedPaymentDto.getAmount());
                
                PaymentData paymentData = accountBO.createPaymentData(amount, adjustedPaymentDto.getPaymentDate(), 
                        accountPaymentEntity.getReceiptNumber(), accountPaymentEntity.getReceiptDate(), adjustedPaymentDto.getPaymentType(), 
                        paymentCreator);

                //new adjusted savings payment must be tied to this payment
                if (newSavingsPaymentId != null) {
                    AccountPaymentEntity newSvngPayment = legacyAccountDao.findPaymentById(newSavingsPaymentId);
                    paymentData.setOtherTransferPayment(newSvngPayment);
                }
                
                accountBO.applyPayment(paymentData);
                legacyAccountDao.createOrUpdate(accountBO);
                transactionHelper.flushSession();
                // handling new Group Loan Members payments
                if (accountBO.isGroupLoanAccount()){
                    for (AdjustedPaymentDto adjustedMemberPayment : adjustedPaymentDto.getMemberPayments()){
                        AccountBO memberAccount = ((LoanBO)accountBO).findMemberById(adjustedMemberPayment.getAccountId());
                        BigDecimal adjustedMemberPaymentAmount = BigDecimal.ZERO;
                        if (!StringUtils.isBlank(adjustedMemberPayment.getAmount())) {
                        	adjustedMemberPaymentAmount = new BigDecimal(adjustedMemberPayment.getAmount());
                        }
                        Money memberAmount = new Money(memberAccount.getCurrency(), adjustedMemberPaymentAmount.toString());
                        
                        PaymentData memberPaymentData = memberAccount.createPaymentData(memberAmount, adjustedPaymentDto.getPaymentDate(), 
                                accountPaymentEntity.getReceiptNumber(), accountPaymentEntity.getReceiptDate(), adjustedPaymentDto.getPaymentType(), 
                                paymentCreator);
                        memberPaymentData.setParentPayment(accountBO.getLastPmnt());
                        memberAccount.applyPayment(memberPaymentData);
                        legacyAccountDao.createOrUpdate(memberAccount);
                    }
                }
            }

            while (!paymentsToBeReapplied.isEmpty()) {
                PaymentData paymentData = paymentsToBeReapplied.pop();
                //avoid lazy loading exception
                if (paymentData.getOtherTransferPayment() != null) {
                    legacyAccountDao.updatePayment(paymentData.getOtherTransferPayment());
                }
                accountBO.applyPayment(paymentData);
                legacyAccountDao.createOrUpdate(accountBO);
                transactionHelper.flushSession();
                
                if (accountBO.isGroupLoanAccount()){
                    for (LoanBO memberAccount : ((LoanBO)accountBO).getMemberAccounts()){
                        PaymentData memberPaymentData = memberPaymentsToBeReappliedMap.get(memberAccount.getAccountId()).pop();
                        memberPaymentData.setParentPayment(accountBO.getLastPmnt());
                        memberAccount.applyPayment(memberPaymentData);
                        legacyAccountDao.createOrUpdate(memberAccount);
                    }
                }
            }

            transactionHelper.commitTransaction();
        } catch (ServiceException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (AccountException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (RuntimeException e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public void makePaymentFromSavingsAcc(AccountPaymentParametersDto accountPaymentParametersDto,
            String savingsAccGlobalNumber) {
        this.accountService.makePaymentFromSavings(accountPaymentParametersDto, savingsAccGlobalNumber);
    }

    @Override
    /**
     * do not use this method
     * used and implemented in Audi Plugin
     * @return null
     */
    public Integer getAccountTrxnById(Integer id) {
        return null;
    }
    
    @Override
    public void applyGroupCharge(Map<Integer, String> idsAndValues, Short chargeId, boolean isPenaltyType) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        TreeMap<Integer, String> idsAndValueAsTreeMap = new TreeMap<Integer, String>(idsAndValues);
        
        try {
            AccountBO parentAccount = ((LoanBO)legacyAccountDao.getAccount(new AccountBusinessService().getAccount(idsAndValueAsTreeMap.firstKey()).getAccountId())).getParentAccount();
            BigDecimal parentAmount = ((LoanBO) parentAccount).getLoanAmount().getAmount();
            BigDecimal membersAmount = BigDecimal.ZERO; 
            
            for (Map.Entry<Integer, String> entry: idsAndValues.entrySet()) {
                LoanBO individual = loanDao.findById(entry.getKey());
                Double chargeAmount = Double.valueOf(entry.getValue());
                if (chargeAmount.equals(0.0)) {
                	continue;
                }
                membersAmount = membersAmount.add(individual.getLoanAmount().getAmount());
                individual.updateDetails(userContext);

                if (isPenaltyType && !chargeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                    PenaltyBO penalty = this.penaltyDao.findPenaltyById(chargeId.intValue());
                    individual.addAccountPenalty(new AccountPenaltiesEntity(individual, penalty, chargeAmount));
                } else {
                    individual.applyCharge(chargeId, chargeAmount);
                }
            }
            
            boolean isRateCharge = false;
            
            if (!chargeId.equals(Short.valueOf(AccountConstants.MISC_FEES)) 
                    && !chargeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                
                if (isPenaltyType) {
                    PenaltyBO penalty = this.penaltyDao.findPenaltyById(chargeId.intValue());
                    if (penalty instanceof RatePenaltyBO) {
                        isRateCharge = true;
                    }
                } else {
                    FeeBO fee = feeDao.findById(chargeId);
                    if (fee.getFeeType().equals(RateAmountFlag.RATE)) {
                        isRateCharge = true;
                    }
                }
            }
            
            Double chargeAmount = null;
            
            if (!isRateCharge) {
                chargeAmount  = sumCharge(idsAndValues);    
            } else {
                chargeAmount = Double.valueOf(idsAndValueAsTreeMap.firstEntry().getValue());
                BigDecimal chargeAmountBig = new BigDecimal(chargeAmount);
                membersAmount = membersAmount.multiply(chargeAmountBig);
                int scale = Money.getInternalPrecision();
                chargeAmountBig = membersAmount.divide(parentAmount, scale, RoundingMode.HALF_EVEN);
                chargeAmount = chargeAmountBig.doubleValue();
            }
                   
            parentAccount.updateDetails(userContext);

            CustomerLevel customerLevel = null;
            if (parentAccount.isCustomerAccount()) {
                customerLevel = parentAccount.getCustomer().getLevel();
            }
            if (parentAccount.getPersonnel() != null) {
                checkPermissionForApplyCharges(parentAccount.getType(), customerLevel, userContext,
                        parentAccount.getOffice().getOfficeId(), parentAccount.getPersonnel().getPersonnelId());
            } else {
                checkPermissionForApplyCharges(parentAccount.getType(), customerLevel, userContext,
                        parentAccount.getOffice().getOfficeId(), userContext.getId());
            }
            
            this.transactionHelper.startTransaction();
            
            if(isPenaltyType && parentAccount instanceof LoanBO) {
                PenaltyBO penalty = this.penaltyDao.findPenaltyById(chargeId.intValue());
                ((LoanBO) parentAccount).addAccountPenalty(new AccountPenaltiesEntity(parentAccount, penalty, chargeAmount));
            } else {
                parentAccount.applyCharge(chargeId, chargeAmount);
            }
            
            this.transactionHelper.commitTransaction();
        } catch (ServiceException e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        }
        
    }

    private Double sumCharge(Map<Integer, String> idsAndValues) {
        Double sum = 0.0;
        for (Map.Entry<Integer, String> entry: idsAndValues.entrySet()) {
            sum += Double.valueOf(entry.getValue());
        }
        return sum;
    }

}