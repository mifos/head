package org.mifos.application.servicefacade;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanActivityEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanPerformanceHistoryEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.LoanSummaryEntity;
import org.mifos.accounts.loan.business.MaxMinLoanAmount;
import org.mifos.accounts.loan.business.MaxMinNoOfInstall;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.persistence.PenaltyDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.InstallmentRange;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGroupLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.domain.CreationDetail;
import org.mifos.clientportfolio.newloan.domain.LoanAccountDetail;
import org.mifos.clientportfolio.newloan.domain.LoanProductOverridenDetail;
import org.mifos.clientportfolio.newloan.domain.LoanSchedule;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleConfiguration;
import org.mifos.clientportfolio.newloan.domain.service.LoanScheduleService;
import org.mifos.config.AccountingRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.GroupIndividualLoanDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.screen.AccountFeesDto;
import org.mifos.dto.screen.AccountPenaltiesDto;
import org.mifos.dto.screen.GroupLoanMemberAdjustmentDto;
import org.mifos.dto.screen.GroupLoanScheduleDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.LoanPerformanceHistoryDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.dto.screen.LoanSummaryDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class GroupLoanAccountServiceFacadeWebTier implements GroupLoanAccountServiceFacade {

    private final OfficeDao officeDao;
    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final LoanDao loanDao;
    private final FundDao fundDao;
    private final LoanProductDao loanProductDao;
    private final LoanScheduleService loanScheduleService;
    private final LoanBusinessService loanBusinessService;
    private HibernateTransactionHelper transactionHelper;
    private final ConfigurationPersistence configurationPersistence;

    @Autowired
    private FeeDao feeDao;
    
    @Autowired
    private PenaltyDao penaltyDao;

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Autowired
    private CashFlowService cashFlowService;

    @Autowired
    public GroupLoanAccountServiceFacadeWebTier(OfficeDao officeDao, CustomerDao customerDao,
            PersonnelDao personnelDao, LoanDao loanDao, FundDao fundDao, LoanProductDao loanProductDao, LoanBusinessService loanBusinessService,
            HibernateTransactionHelper transactionHelper, FeeDao feeDao, PenaltyDao penaltyDao,
            LegacyAccountDao legacyAccountDao, LegacyMasterDao legacyMasterDao,
            QuestionnaireServiceFacade questionnaireServiceFacade, CashFlowService cashFlowService, ConfigurationPersistence configurationPersistence,
            LoanScheduleService loanScheduleService) {
        this.officeDao = officeDao;
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.loanDao = loanDao;
        this.fundDao = fundDao;
        this.loanProductDao = loanProductDao;
        this.loanBusinessService = loanBusinessService;
        this.transactionHelper = transactionHelper;
        this.feeDao = feeDao;
        this.penaltyDao = penaltyDao;
        this.legacyMasterDao = legacyMasterDao;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
        this.cashFlowService = cashFlowService;
        this.configurationPersistence = configurationPersistence;
        this.loanScheduleService = loanScheduleService;
    }

    @Override
    public LoanCreationResultDto createGroupLoan(CreateGroupLoanAccount loanAccountInfo,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow) {
        return createGroupLoanAccount(loanAccountInfo, new HashMap<Integer, List<LoanPaymentDto>>(), questionGroups,
        		loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), loanAccountInfo.getMemberDetails(), false);
    }
    
    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }
    
    private LoanCreationResultDto createGroupLoanAccount(CreateGroupLoanAccount loanAccountInfo, Map<Integer, List<LoanPaymentDto>> backdatedLoanPayments,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow, List<DateTime> loanScheduleInstallmentDates,
            List<Number> totalInstallmentAmounts, List<CreateLoanAccount> memberDetails, boolean isBackdatedLoan) {

        DateTime creationDate = new DateTime();

        // 0. verify member details for GLIM group accounts
        for (CreateLoanAccount groupMemberAccount : memberDetails) {
            ClientBO member = this.customerDao.findClientById(groupMemberAccount.getCustomerId());
            if (creationDate.isBefore(new DateTime(member.getCreatedDate()))) {
                throw new BusinessRuleException("errors.cannotCreateLoan.because.clientsAreCreatedInFuture");
            }
        }

        // 1. assemble loan details
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        OfficeBO userOffice = this.officeDao.findOfficeById(user.getBranchId());
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfo.getGroupLoanAccountDetails().getCustomerId());
        if (customer.isGroup()) {
            customer = this.customerDao.findGroupBySystemId(customer.getGlobalCustNum());
        }

        // assemble
        LoanAccountDetail loanAccountDetail = assembleLoanAccountDetail(loanAccountInfo);

        List<AccountFeesEntity> accountFeeEntities = assembleAccountFees(loanAccountInfo.getGroupLoanAccountDetails().getAccountFees());
        List<AccountPenaltiesEntity> accountPenaltyEntities = assembleAccountPenalties(loanAccountInfo.getGroupLoanAccountDetails().getAccountPenalties());
        LoanProductOverridenDetail overridenDetail = new LoanProductOverridenDetail(loanAccountDetail.getLoanAmount(), loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(),
                loanAccountInfo.getGroupLoanAccountDetails().getInterestRate(), loanAccountInfo.getGroupLoanAccountDetails().getNumberOfInstallments(), loanAccountInfo.getGroupLoanAccountDetails().getGraceDuration(), accountFeeEntities, accountPenaltyEntities);

        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        boolean loanScheduleIndependentOfCustomerMeetingEnabled = loanAccountInfo.getGroupLoanAccountDetails().isRepaymentScheduleIndependentOfCustomerMeeting();
        LoanScheduleConfiguration configuration = new LoanScheduleConfiguration(loanScheduleIndependentOfCustomerMeetingEnabled, interestDays);

        MeetingBO repaymentDayMeeting = null;
        if (loanScheduleIndependentOfCustomerMeetingEnabled) {
            repaymentDayMeeting = this.createNewMeetingForRepaymentDay(loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), loanAccountInfo.getGroupLoanAccountDetails(), loanAccountDetail.getCustomer());
        } else {
            MeetingDto customerMeetingDto = customer.getCustomerMeetingValue().toDto();
            repaymentDayMeeting = new MeetingFactory().create(customerMeetingDto);
            
            Short recurAfter = loanAccountDetail.getLoanProduct().getLoanOfferingMeeting().getMeeting().getRecurAfter();
            repaymentDayMeeting.getMeetingDetails().setRecurAfter(recurAfter);
        }

        List<DateTime> loanScheduleDates = new ArrayList<DateTime>(loanScheduleInstallmentDates);

        LoanSchedule loanSchedule = assembleLoanSchedule(loanAccountDetail.getCustomer(), loanAccountDetail.getLoanProduct(), overridenDetail, configuration, repaymentDayMeeting, userOffice, loanScheduleDates, loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), totalInstallmentAmounts);

        // 2. create loan
        InstallmentRange installmentRange = new MaxMinNoOfInstall(loanAccountInfo.getGroupLoanAccountDetails().getMinAllowedNumberOfInstallments().shortValue(),
                loanAccountInfo.getGroupLoanAccountDetails().getMaxAllowedNumberOfInstallments().shortValue(), null);
        AmountRange amountRange = new MaxMinLoanAmount(loanAccountInfo.getGroupLoanAccountDetails().getMaxAllowedLoanAmount().doubleValue(), loanAccountInfo.getGroupLoanAccountDetails().getMinAllowedLoanAmount().doubleValue(), null);

        if (isBackdatedLoan) {
            creationDate = loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate().toDateMidnight().toDateTime();
        }
        CreationDetail creationDetail = new CreationDetail(creationDate, Integer.valueOf(user.getUserId()));
        LoanBO loan = LoanBO.openGroupLoanAccount(loanAccountDetail.getLoanProduct(), loanAccountDetail.getCustomer(), repaymentDayMeeting,
                loanSchedule, loanAccountDetail.getAccountState(), loanAccountDetail.getFund(), overridenDetail, configuration, installmentRange, amountRange,
                creationDetail, createdBy);
        loan.setBusinessActivityId(loanAccountInfo.getGroupLoanAccountDetails().getLoanPurposeId());
        loan.setExternalId(loanAccountInfo.getGroupLoanAccountDetails().getExternalId());
        loan.setCollateralNote(loanAccountInfo.getGroupLoanAccountDetails().getCollateralNotes());
        loan.setCollateralTypeId(loanAccountInfo.getGroupLoanAccountDetails().getCollateralTypeId());
        if (isBackdatedLoan) {
            loan.markAsCreatedWithBackdatedPayments();
        }
        //set up predefined loan account for importing loans
        if(loanAccountInfo.getGroupLoanAccountDetails().getPredefinedAccountNumber()!=null){
            loan.setGlobalAccountNum(loanAccountInfo.getGroupLoanAccountDetails().getPredefinedAccountNumber());
        }
        
        try {
            personnelDao.checkAccessPermission(userContext, loan.getOfficeId(), loan.getCustomer()
                    .getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
        }
        
        try {
            transactionHelper.startTransaction();
            this.loanDao.save(loan);
            transactionHelper.flushSession();
            //no predefined account number, generate one instead
            if(loanAccountInfo.getGroupLoanAccountDetails().getPredefinedAccountNumber()==null){
                try {
                    loan.setGlobalAccountNum(loan.generateId(userOffice.getGlobalOfficeNum()));
                } catch (AccountException e) {
                    throw new BusinessRuleException(e.getMessage());
                }
                this.loanDao.save(loan);
                transactionHelper.flushSession();
            }
            //set up status flag
            AccountStateFlagEntity flagEntity=null;
            if(loanAccountInfo.getGroupLoanAccountDetails().getFlagId()!=null){
                try {
                    flagEntity=legacyMasterDao.getPersistentObject(AccountStateFlagEntity.class, loanAccountInfo.getGroupLoanAccountDetails().getFlagId());
                    loan.setUserContext(userContext);
                    loan.setFlag(flagEntity);
                    loan.setClosedDate(new DateTimeService().getCurrentJavaDateTime());
                    loan.setUserContext(userContext);
                } catch (PersistenceException e) {
                    throw new BusinessRuleException(e.getMessage());
                }
                this.loanDao.save(loan);
                transactionHelper.flushSession();
            }

            // for GLIM loans only
            List<CreateLoanAccount> individualMembersOfGroupLoan = new ArrayList<CreateLoanAccount>();
            List<BigDecimal> radio = new ArrayList<BigDecimal>(loan.getNoOfInstallments());
            
            List<LoanProductOverridenDetail> memberOverridenDetails = new ArrayList<LoanProductOverridenDetail>();
            List<LoanSchedule> memberLoanSchedules = new ArrayList<LoanSchedule>();
            List<ClientBO> clients = new ArrayList<ClientBO>();
            
            for (CreateLoanAccount groupMemberAccount : memberDetails) {
                ClientBO member = this.customerDao.findClientById(groupMemberAccount.getCustomerId());
                Money loanAmount = new Money(loanAccountDetail.getLoanProduct().getCurrency(), groupMemberAccount.getLoanAmount());
                List<CreateAccountPenaltyDto> defaultAccountPenalties = new ArrayList<CreateAccountPenaltyDto>();
                
                radio.add(loanAmount.divide(loan.getLoanAmount()));
                
                int memberCount = memberDetails.size();
                for(CreateAccountPenaltyDto createAccountPenaltyDto : loanAccountInfo.getGroupLoanAccountDetails().getAccountPenalties()) {
                    Integer penaltyId = createAccountPenaltyDto.getPenaltyId();
                    String amount = createAccountPenaltyDto.getAmount();
                    PenaltyBO penaltyBO = this.penaltyDao.findPenaltyById(penaltyId.shortValue());
                    
                    if(penaltyBO instanceof AmountPenaltyBO) {
                        amount = String.valueOf(Double.valueOf(createAccountPenaltyDto.getAmount()) / memberCount);
                    }
                    
                    defaultAccountPenalties.add(new CreateAccountPenaltyDto(penaltyId, amount));
                }
                
                List<AccountFeesEntity> feeEntities = assembleAccountFees(groupMemberAccount.getAccountFees());
                List<AccountPenaltiesEntity> penaltyEntities = assembleAccountPenalties(defaultAccountPenalties);
                LoanProductOverridenDetail memberOverridenDetail = new LoanProductOverridenDetail(loanAmount, feeEntities, overridenDetail, penaltyEntities);

                LoanSchedule memberSchedule = assembleLoanSchedule(member, loanAccountDetail.getLoanProduct(), memberOverridenDetail, configuration, repaymentDayMeeting, userOffice, new ArrayList<DateTime>(), loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), new ArrayList<Number>());
                memberOverridenDetails.add(memberOverridenDetail);
                memberLoanSchedules.add(memberSchedule);
                clients.add(member);
                individualMembersOfGroupLoan.add(groupMemberAccount);
            }
            
            List<LoanBO> memberLoans = new ArrayList<LoanBO>(); //for original schedule persisting
            int index = 0;
            for (CreateLoanAccount groupMemberAccount : individualMembersOfGroupLoan) {

                
                LoanBO memberLoan = LoanBO.openGroupLoanForAccount(loan, loanAccountDetail.getLoanProduct(), clients.get(index), repaymentDayMeeting, memberLoanSchedules.get(index), memberOverridenDetails.get(index), configuration,
                        installmentRange, amountRange, creationDetail, createdBy, Boolean.TRUE);
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
                
                memberLoans.add(memberLoan);
                index ++;
            }
            
            
            // update loan schedule for Group Loan Account
            loanSchedule = this.loanScheduleService.generateGroupLoanSchedule(loanAccountDetail.getLoanProduct(), repaymentDayMeeting, loanSchedule, memberLoanSchedules, 
                    loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), overridenDetail, configuration, userOffice.getOfficeId(), loanAccountDetail.getCustomer(), accountFeeEntities);
            

            fixMemberAndParentInstallmentDetails(loan, memberLoans);

            for (LoanBO memberLoan : memberLoans) {
            	memberLoan.updateLoanSummary();
            	loanDao.save(memberLoan);
            }
            
            this.loanDao.save(loan);
            transactionHelper.flushSession();
            
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
                LocalDate approvalDate = loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate();
                loan.approve(createdBy, comment, approvalDate);

                for (LoanBO memberLoan : memberLoans) {
                    memberLoan.approve(createdBy, comment, approvalDate);
                }
                
                
                // 4. disburse loan
                String receiptNumber = null;
                Date receiptDate = null;
                PaymentTypeEntity paymentType = new PaymentTypeEntity(PaymentTypes.CASH.getValue());
                if (loanAccountInfo.getGroupLoanAccountDetails().getDisbursalPaymentTypeId() != null) {
                    paymentType = new PaymentTypeEntity(loanAccountInfo.getGroupLoanAccountDetails().getDisbursalPaymentTypeId());
                }
                Date paymentDate = loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate().toDateMidnight().toDate();
                AccountPaymentEntity disbursalPayment = new AccountPaymentEntity(loan, loan.getLoanAmount(),
                        receiptNumber, receiptDate, paymentType, paymentDate);
                disbursalPayment.setCreatedByUser(createdBy);

                this.loanBusinessService.persistOriginalSchedule(loan);
                for(LoanBO memberLoan : memberLoans) {
                    this.loanBusinessService.persistOriginalSchedule(memberLoan);
                }
                
                // refactoring of loan disbursal
                if (customer.isDisbursalPreventedDueToAnyExistingActiveLoansForTheSameProduct(loan.getLoanOffering())) {
                    throw new BusinessRuleException("errors.cannotDisburseLoan.because.otherLoansAreActive");
                }

                try {
                    loan.updateCustomer(customer);
                    new ProductMixValidator().checkIfProductsOfferingCanCoexist(loan);
                } catch (ServiceException e1) {
                    throw new AccountException(e1.getMessage());
                }

                loan.disburse(createdBy, disbursalPayment);
                for (LoanBO memberLoan : memberLoans) {
                    memberLoan.disburse(createdBy, disbursalPayment);
                }
                
                customer.updatePerformanceHistoryOnDisbursement(loan, loan.getLoanAmount());
                // end of refactoring of loan disbural
                this.loanDao.save(loan);
                transactionHelper.flushSession();
                
                // 5. apply each payment
                for (LoanBO memberLoan : memberLoans) {
                    Integer id = memberLoan.getCustomer().getCustomerId();
                    List<LoanPaymentDto> payments = backdatedLoanPayments.get(id);

                    for (LoanPaymentDto loanPayment : payments) {
                        Money amountPaidToDate = new Money(loan.getCurrency(), loanPayment.getAmount());
                        PaymentData paymentData = new PaymentData(amountPaidToDate, createdBy,
                                loanPayment.getPaymentTypeId(), loanPayment.getPaymentDate().toDateMidnight().toDate());
                        memberLoan.applyPayment(paymentData);
                        loan.applyPayment(paymentData);
                        this.loanDao.save(memberLoan);
                        this.loanDao.save(loan);
                    }
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
    
    // fix installment details in order to match sum of interest of member accounts to interest of parent account
    // update member fee amounts in order to match parent fee amount
    private void fixMemberAndParentInstallmentDetails(LoanBO loan, List<LoanBO> memberLoans) {

        Map<Integer, LoanScheduleEntity> parentScheduleEntities = loan.getLoanScheduleEntityMap();
        List<RepaymentScheduleInstallment> correctedInstallments = new ArrayList<RepaymentScheduleInstallment>();
        
        for (Integer installmentId : parentScheduleEntities.keySet()) {
        	
        	LoanScheduleEntity parentEntity = parentScheduleEntities.get(installmentId);
        	Map<Short, BigDecimal> feeAmountsForInstallment = new HashMap<Short, BigDecimal>();
        	
        	for (AccountFeesActionDetailEntity feesActionDetailEntity : parentEntity.getAccountFeesActionDetails()) {
        		feeAmountsForInstallment.put(feesActionDetailEntity.getFee().getFeeId(), feesActionDetailEntity.getFeeAmount().getAmount());
        	}
        	
        	RepaymentScheduleInstallment correctedInstallment = new RepaymentScheduleInstallment();
        	correctedInstallment.setInstallment(installmentId);
        	correctedInstallment.setDueDateValue(parentEntity.getActionDate());
        	
        	BigDecimal principal = BigDecimal.ZERO;
        	BigDecimal interest = BigDecimal.ZERO;
        	
        	for (LoanBO memberLoan : memberLoans) {
        		LoanScheduleEntity memberEntity = memberLoan.getLoanScheduleEntityMap().get(installmentId);
        		principal = principal.add(memberEntity.getPrincipal().getAmount());
        		interest = interest.add(memberEntity.getInterest().getAmount());

        		for (AccountFeesActionDetailEntity feesActionDetailEntity : memberEntity.getAccountFeesActionDetails()) {
        			
        			if (feesActionDetailEntity.getFee().getFeeType().equals(RateAmountFlag.RATE)) {
        				continue;
        			}
        			
        			BigDecimal currentAmount = feeAmountsForInstallment.get(feesActionDetailEntity.getFee().getFeeId());
        			currentAmount = currentAmount.subtract(feesActionDetailEntity.getFeeAmount().getAmount());
        			
        			if (currentAmount.compareTo(BigDecimal.ZERO) == -1) {
        				BigDecimal toUpdate = feesActionDetailEntity.getFeeAmount().getAmount().add(currentAmount);
        				feesActionDetailEntity.updateFeeAmount(toUpdate);
        				currentAmount = BigDecimal.ZERO;
        			}
        			
        			feeAmountsForInstallment.put(feesActionDetailEntity.getFee().getFeeId(), currentAmount);
        		}
        	}
        	
        	correctedInstallment.setPrincipal(new Money(parentEntity.getPrincipal().getCurrency(), principal));
        	correctedInstallment.setInterest(new Money(parentEntity.getInterest().getCurrency(), interest));
        	
        	correctedInstallments.add(correctedInstallment);
        }
        loan.updateInstallmentSchedule(correctedInstallments);
    }
    
    private LoanAccountDetail assembleLoanAccountDetail(CreateGroupLoanAccount loanAccountInfo) {

        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfo.getGroupLoanAccountDetails().getCustomerId());
        LoanOfferingBO loanProduct = this.loanProductDao.findById(loanAccountInfo.getGroupLoanAccountDetails().getProductId());

        Money loanAmount = new Money(loanProduct.getCurrency(), loanAccountInfo.getGroupLoanAccountDetails().getLoanAmount());
        AccountState accountStateType = AccountState.fromShort(loanAccountInfo.getGroupLoanAccountDetails().getAccountState().shortValue());
        FundBO fund = null;
        if (loanAccountInfo.getGroupLoanAccountDetails().getSourceOfFundId() != null) {
            fund = this.fundDao.findById(loanAccountInfo.getGroupLoanAccountDetails().getSourceOfFundId().shortValue());
        }

        return new LoanAccountDetail(customer, loanProduct, loanAmount, accountStateType, fund);
    }
    
    private MeetingBO createNewMeetingForRepaymentDay(LocalDate disbursementDate, RecurringSchedule recurringSchedule, CustomerBO customer) {
        MeetingBO newMeetingForRepaymentDay = null;

        final int minDaysInterval = configurationPersistence.getConfigurationValueInteger(
                MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY);

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
            } else {
                Short recurEvery = recurringSchedule.getEvery().shortValue();
                newMeetingForRepaymentDay = new MeetingBO(recurEvery, repaymentStartDate, MeetingType.LOAN_INSTALLMENT,
                        customer.getCustomerMeeting().getMeeting().getMeetingPlace());
            }
            return newMeetingForRepaymentDay;
        } catch (NumberFormatException nfe) {
            throw new MifosRuntimeException(nfe);
        } catch (MeetingException me) {
            throw new BusinessRuleException(me.getKey(), me);
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
    
    private List<AccountPenaltiesEntity> assembleAccountPenalties(List<CreateAccountPenaltyDto> defaultAccountPenalties) {
        List<AccountPenaltiesEntity> accountPenaltyEntities = new ArrayList<AccountPenaltiesEntity>();
        for (CreateAccountPenaltyDto defaultPenalty : defaultAccountPenalties) {
            PenaltyBO penalty = this.penaltyDao.findPenaltyById(defaultPenalty.getPenaltyId().shortValue());
            AccountPenaltiesEntity deafultAccountPenaltyEntity = new AccountPenaltiesEntity(null, penalty, Double.valueOf(defaultPenalty.getAmount()));
            accountPenaltyEntities.add(deafultAccountPenaltyEntity);
        }
        return accountPenaltyEntities;
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
    public Integer getMemberClientId(String globalCustNum) {
        return this.customerDao.findClientBySystemId(globalCustNum).getCustomerId();
    }
    
    
    @Override
    public int getNumberOfMemberAccounts(Integer accountId) {
        LoanBO loanAccount = loanDao.findById(accountId);
        return loanAccount.getMemberAccounts().size();
    }
    
    @Override
    public List<Integer> getListOfMemberAccountIds(Integer parentAccountId){
        List<Integer> ids = new ArrayList<Integer>();
        LoanBO loanAccount = loanDao.findById(parentAccountId);
        for(LoanBO memberAccount : loanAccount.getMemberAccounts()) {
            ids.add(memberAccount.getAccountId());
        }
        Collections.sort(ids);
        return ids;
    }
    
    public List<String> getListOfMemberGlobalAccountNumbers(Integer parentAccountId) {
        List<String> memberNumbers = new ArrayList<String>();
        LoanBO loanAccount = loanDao.findById(parentAccountId);
        for(LoanBO memberAccount : loanAccount.getMemberAccounts()) {
            memberNumbers.add(memberAccount.getGlobalAccountNum());
        }
        Collections.sort(memberNumbers);
        return memberNumbers;
    }

    @Override
    public List<GroupIndividualLoanDto> getMemberLoansAndDefaultPayments(Integer parentAccountId, BigDecimal amount) {
        LoanBO loanAccount = loanDao.findById(parentAccountId);
        List<GroupIndividualLoanDto> memberAccountDtos = new ArrayList<GroupIndividualLoanDto>();
        BigDecimal amountSpent = BigDecimal.ZERO;
        List<LoanBO> members = new ArrayList<LoanBO>(loanAccount.getMemberAccounts());
        Iterator<LoanBO> itr = members.iterator();
        List<BigDecimal> factors = new ArrayList<BigDecimal>();
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalFactor = BigDecimal.ZERO;
        amount = amount.setScale(AccountingRules.getDigitsAfterDecimal());
        
        while(itr.hasNext()) {
            LoanBO memberAccount = itr.next();
            BigDecimal factor = memberAccount.calcFactorOfEntireLoan(); 
            total = total.add(factor);
            if (memberAccount.isAccountActive() || memberAccount.isInState(AccountState.LOAN_PARTIAL_APPLICATION)
                    || memberAccount.isInState(AccountState.LOAN_PENDING_APPROVAL)
                    || memberAccount.isInState(AccountState.LOAN_APPROVED)) {
                factors.add(factor);   
                totalFactor = totalFactor.add(factor);
            } else {
                itr.remove();
            }
        }
        
        BigDecimal scale = totalFactor.divide(total, RoundingMode.HALF_EVEN);
        
        for (int i = 0; i < factors.size(); ++i) {
            BigDecimal rescaled = factors.get(i).multiply(scale);
            factors.set(i, rescaled);
        }
        
        for (int i = 0; i < members.size(); ++i) {
            LoanBO memberAccount = members.get(i);
                if(i < members.size() - 1) {
                    BigDecimal currentAmount = amount.divide(factors.get(i), RoundingMode.HALF_UP);
                    memberAccountDtos.add(new GroupIndividualLoanDto(memberAccount.getGlobalAccountNum(), currentAmount, memberAccount.getAccountId()));
                    amountSpent = amountSpent.add(currentAmount);
                } else {
                    //last element
                    memberAccountDtos.add(new GroupIndividualLoanDto(memberAccount.getGlobalAccountNum(), amount.subtract(amountSpent), memberAccount.getAccountId()));
                }
        }
        
        Collections.sort(memberAccountDtos);
        
        return memberAccountDtos;
    }
    
    @Override
    public List<GroupLoanMemberAdjustmentDto> retrieveMemberAdjustmentDtos(Integer parentAccountId, Integer parentPaymentId, BigDecimal newAmount) {
        LoanBO parentLoanAccount = loanDao.findById(parentAccountId);
        List<GroupLoanMemberAdjustmentDto> memberAdjustmentDtoList = new ArrayList<GroupLoanMemberAdjustmentDto>();  
        
        LoanBO memberAccount = null;
        AccountPaymentEntity memberPayment = null;
        BigDecimal currentAmount = null;
        BigDecimal amountSpent = BigDecimal.ZERO;
        
        List<LoanBO> members = new ArrayList<LoanBO>(parentLoanAccount.getMemberAccounts());

        Iterator<LoanBO> itr = members.iterator();
        while(itr.hasNext()) {
            memberAccount = itr.next();
            if(itr.hasNext()) {
                currentAmount = newAmount.divide(memberAccount.calcFactorOfEntireLoan(), RoundingMode.HALF_UP);
                memberPayment = memberAccount.findPaymentByParentPaymentId(parentPaymentId);
                if (memberPayment == null) {
                    continue;
                }
                memberAdjustmentDtoList.add(new GroupLoanMemberAdjustmentDto(memberPayment.getPaymentId(), memberAccount.getAccountId(),
                        memberPayment.getAmount().getAmount(), currentAmount, new LocalDate(memberPayment.getPaymentDate()),
                        memberAccount.getGlobalAccountNum(), memberAccount.getCustomer().getGlobalCustNum(),
                        memberAccount.getCustomer().getDisplayName()));
                
                amountSpent = amountSpent.add(currentAmount);
            } else {
                //last element
                memberPayment = memberAccount.findPaymentByParentPaymentId(parentPaymentId);
                memberAdjustmentDtoList.add(new GroupLoanMemberAdjustmentDto(memberPayment.getPaymentId(), memberAccount.getAccountId(),
                        memberPayment.getAmount().getAmount(), newAmount.subtract(amountSpent), new LocalDate(memberPayment.getPaymentDate()),
                        memberAccount.getGlobalAccountNum(), memberAccount.getCustomer().getGlobalCustNum(),
                        memberAccount.getCustomer().getDisplayName()));
            }
        }
        
        Collections.sort(memberAdjustmentDtoList);
        
        return memberAdjustmentDtoList;
    }

    @Override
    public LoanInformationDto retrieveLoanInformation(String globalAccountNum) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);

        try {
            personnelDao.checkAccessPermission(userContext, loan.getOfficeId(), loan.getCustomer().getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
        }

        String fundName = null;
        if (loan.getFund() != null) {
            fundName = loan.getFund().getFundName();
        }

        boolean activeSurveys = false;
        List<SurveyDto> accountSurveys = loanDao.getAccountSurveyDto(loan.getAccountId());

        LoanSummaryDto loanSummary = generateGroupLoanSummaryDto(new ArrayList<LoanBO>(loan.getMemberAccounts()));

        LoanPerformanceHistoryEntity performanceHistory = loan.getPerformanceHistory();
        LoanPerformanceHistoryDto loanPerformanceHistory = new LoanPerformanceHistoryDto(performanceHistory.getNoOfPayments(),
                                                                                        performanceHistory.getTotalNoOfMissedPayments(),
                                                                                        performanceHistory.getDaysInArrears(),
                                                                                        performanceHistory.getLoanMaturityDate());

        Set<AccountFeesDto> accountFeesDtos = new HashSet<AccountFeesDto>();
        if(!loan.getAccountFees().isEmpty()) {
            for (AccountFeesEntity accountFeesEntity: loan.getAccountFees()) {
                AccountFeesDto accountFeesDto = new AccountFeesDto(accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId(),
                        (accountFeesEntity.getFees().getFeeFrequency().getFeePayment() != null ?
                                accountFeesEntity.getFees().getFeeFrequency().getFeePayment().getId() : null),
                        accountFeesEntity.getFeeStatus(), accountFeesEntity.getFees().getFeeName(),
                        accountFeesEntity.getAccountFeeAmount().toString(),
                        getMeetingRecurrence(accountFeesEntity.getFees().getFeeFrequency()
                                .getFeeMeetingFrequency(), userContext),
                        accountFeesEntity.getFees().getFeeId());
                accountFeesDtos.add(accountFeesDto);
            }
        }
        
        Set<AccountPenaltiesDto> accountPenaltiesDtos = new HashSet<AccountPenaltiesDto>();
        if(!loan.getAccountPenalties().isEmpty()) {
            for (AccountPenaltiesEntity accountPenaltiesEntity: loan.getAccountPenalties()) {
                accountPenaltiesDtos.add(new AccountPenaltiesDto(accountPenaltiesEntity.getPenalty().getPenaltyFrequency().getId(),
                        accountPenaltiesEntity.getPenaltyStatus(),
                        accountPenaltiesEntity.getPenalty().getPenaltyName(),
                        accountPenaltiesEntity.getAccountPenaltyAmount().toString(),
                        accountPenaltiesEntity.getPenalty().getPenaltyFrequency().getName(),
                        accountPenaltiesEntity.getPenalty().getPenaltyId()));
            }
        }

        Set<String> accountFlagNames = getAccountStateFlagEntityNames(loan.getAccountFlags());
        Short accountStateId = loan.getAccountState().getId();
        String accountStateName = getAccountStateName(accountStateId);
        boolean disbursed = AccountState.isDisbursed(accountStateId);
        String gracePeriodTypeName = getGracePeriodTypeName(loan.getGracePeriodType().getId());
        Short interestType = loan.getInterestType().getId();
        String interestTypeName = getInterestTypeName(interestType);
        List<CustomerNoteDto> recentNoteDtos = new ArrayList<CustomerNoteDto>();
        List<AccountNotesEntity> recentNotes = loan.getRecentAccountNotes();
        for (AccountNotesEntity accountNotesEntity : recentNotes) {
            recentNoteDtos.add(new CustomerNoteDto(accountNotesEntity.getCommentDate(), accountNotesEntity.getComment(), accountNotesEntity.getPersonnelName()));
        }

        CustomValueDto customValueDto = legacyMasterDao.getLookUpEntity(MasterConstants.COLLATERAL_TYPES);
        List<CustomValueListElementDto> collateralTypes = customValueDto.getCustomValueListElements();
        String collateralTypeName = null;
        for (CustomValueListElementDto collateralType : collateralTypes) {
            if ( collateralType.getId() == loan.getCollateralTypeId() ){
                collateralTypeName = collateralType.getName();
                break;
            }
        }
        
        Boolean groupLoanWithMembers = AccountingRules.isGroupLoanWithMembers();
        return new LoanInformationDto(loan.getLoanOffering().getPrdOfferingName(), globalAccountNum, accountStateId,
                                     accountStateName, disbursed, accountFlagNames, loan.getDisbursementDate(), loan.isRedone(),
                                     loan.getBusinessActivityId(), loan.getAccountId(),gracePeriodTypeName, interestType, interestTypeName,
                                     loan.getCustomer().getCustomerId(), loan.getAccountType().getAccountTypeId(),
                                     loan.getOffice().getOfficeId(), loan.getPersonnel().getPersonnelId(), loan.getNextMeetingDate(),
                                     loan.getTotalAmountDue().toString(), loan.getTotalAmountInArrears().toString(), loanSummary,
                                     loan.getLoanActivityDetails().isEmpty()? false: true, loan.getInterestRate(),
                                     loan.isInterestDeductedAtDisbursement(),
                                     loan.getLoanOffering().getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurAfter(),
                                     loan.getLoanOffering().getLoanOfferingMeeting().getMeeting().getMeetingDetails().getRecurrenceType().getRecurrenceId(),
                                     loan.getLoanOffering().isPrinDueLastInst(), loan.getNoOfInstallments(),
                                     loan.getMaxMinNoOfInstall().getMinNoOfInstall(), loan.getMaxMinNoOfInstall().getMaxNoOfInstall(),
                                     loan.getGracePeriodDuration(), fundName, loan.getCollateralTypeId(), collateralTypeName, loan.getCollateralNote(),loan.getExternalId(),
                                     accountFeesDtos, loan.getCreatedDate(), loanPerformanceHistory,
                                     loan.getCustomer().isGroup(), getRecentActivityView(globalAccountNum), activeSurveys, accountSurveys,
                                     loan.getCustomer().getDisplayName(), loan.getCustomer().getGlobalCustNum(), loan.getOffice().getOfficeName(), recentNoteDtos,
                                     accountPenaltiesDtos, groupLoanWithMembers);
    }
    
    private LoanSummaryDto generateGroupLoanSummaryDto(ArrayList<LoanBO> memberAccounts) {
        List<LoanSummaryEntity> memberSummarys = new ArrayList<LoanSummaryEntity>();
        
        for (LoanBO member : memberAccounts) {
            memberSummarys.add(member.getLoanSummary());
        }
        
        Money originalPrincipal = new Money(Money.getDefaultCurrency(), 0.0), principalPaid = new Money(Money.getDefaultCurrency(), 0.0),
                principalDue = new Money(Money.getDefaultCurrency(), 0.0), originalInterest = new Money(Money.getDefaultCurrency(), 0.0), 
                interestPaid = new Money(Money.getDefaultCurrency(), 0.0), interestDue = new Money(Money.getDefaultCurrency(), 0.0),
                originalFees = new Money(Money.getDefaultCurrency(), 0.0), feesDue = new Money(Money.getDefaultCurrency(), 0.0), 
                originalPenalty = new Money(Money.getDefaultCurrency(), 0.0), feesPaid = new Money(Money.getDefaultCurrency(), 0.0),
                penaltyPaid = new Money(Money.getDefaultCurrency(), 0.0), penaltyDue = new Money(Money.getDefaultCurrency(), 0.0), 
                totalAmt = new Money(Money.getDefaultCurrency(), 0.0), totalAmtPaid = new Money(Money.getDefaultCurrency(), 0.0),
                totalAmtDue = new Money(Money.getDefaultCurrency(), 0.0);
        
        for (LoanSummaryEntity entity : memberSummarys) {
            originalPrincipal = originalPrincipal.add(entity.getOriginalPrincipal());
            principalPaid = principalPaid.add(entity.getPrincipalPaid());
            principalDue = principalDue.add(entity.getPrincipalDue());
            originalInterest = originalInterest.add(entity.getOriginalInterest());
            interestPaid = interestPaid.add(entity.getInterestPaid());
            interestDue = interestDue.add(entity.getInterestDue());
            originalFees = originalFees.add(entity.getOriginalFees());
            feesPaid = feesPaid.add(entity.getFeesPaid());
            feesDue = feesDue.add(entity.getFeesDue());
            originalPenalty = originalPenalty.add(entity.getOriginalPenalty());
            penaltyPaid = penaltyPaid.add(entity.getPenaltyPaid());
            penaltyDue = penaltyDue.add(entity.getPenaltyDue());
            totalAmt = totalAmt.add(entity.getTotalLoanAmnt());
            totalAmtPaid = totalAmtPaid.add(entity.getTotalAmntPaid());
            totalAmtDue = totalAmtDue.add(entity.getTotalAmntDue());
        }
        
        return new LoanSummaryDto(originalPrincipal.toString(), principalPaid.toString(), principalDue.toString(), originalInterest.toString(),
                interestPaid.toString(), interestDue.toString(), originalFees.toString(), feesPaid.toString(), feesDue.toString(), originalPenalty.toString(),
                penaltyPaid.toString(), penaltyDue.toString(), totalAmt.toString(), totalAmtPaid.toString(), totalAmtDue.toString());
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
    
    private String getMeetingRecurrence(MeetingBO meeting, UserContext userContext) {
        return meeting != null ? new MeetingHelper().getMessageWithFrequency(meeting, userContext) : null;
    }

	@Override
	public LoanCreationResultDto createBackdatedGroupLoan(CreateGroupLoanAccount createGroupLoanAccount, 
			Map<Integer, List<LoanPaymentDto>> backdatedLoanPayments, List<QuestionGroupDetail> questionGroups,
			LoanAccountCashFlow loanAccountCashFlow) {
		
        return createGroupLoanAccount(createGroupLoanAccount, backdatedLoanPayments, questionGroups,
        		loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), createGroupLoanAccount.getMemberDetails(), true);
	}

    @Override
    public GroupLoanScheduleDto getGroupLoanScheduleDto(CreateGroupLoanAccount loanAccountInfo, List<CreateLoanAccount> memberDetails) {

        DateTime creationDate = new DateTime();

        // 0. verify member details for GLIM group accounts
        for (CreateLoanAccount groupMemberAccount : memberDetails) {
            ClientBO member = this.customerDao.findClientById(groupMemberAccount.getCustomerId());
            if (creationDate.isBefore(new DateTime(member.getCreatedDate()))) {
                throw new BusinessRuleException("errors.cannotCreateLoan.because.clientsAreCreatedInFuture");
            }
        }

        // 1. assemble loan details
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        OfficeBO userOffice = this.officeDao.findOfficeById(user.getBranchId());
        PersonnelBO createdBy = this.personnelDao.findPersonnelById(userContext.getId());
        CustomerBO customer = this.customerDao.findCustomerById(loanAccountInfo.getGroupLoanAccountDetails().getCustomerId());
        if (customer.isGroup()) {
            customer = this.customerDao.findGroupBySystemId(customer.getGlobalCustNum());
        }

        // assemble
        LoanAccountDetail loanAccountDetail = assembleLoanAccountDetail(loanAccountInfo);

        List<AccountFeesEntity> accountFeeEntities = assembleAccountFees(loanAccountInfo.getGroupLoanAccountDetails().getAccountFees());
        List<AccountPenaltiesEntity> accountPenaltyEntities = assembleAccountPenalties(loanAccountInfo.getGroupLoanAccountDetails().getAccountPenalties());
        LoanProductOverridenDetail overridenDetail = new LoanProductOverridenDetail(loanAccountDetail.getLoanAmount(), loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(),
                loanAccountInfo.getGroupLoanAccountDetails().getInterestRate(), loanAccountInfo.getGroupLoanAccountDetails().getNumberOfInstallments(), loanAccountInfo.getGroupLoanAccountDetails().getGraceDuration(), accountFeeEntities, accountPenaltyEntities);

        Integer interestDays = Integer.valueOf(AccountingRules.getNumberOfInterestDays().intValue());
        boolean loanScheduleIndependentOfCustomerMeetingEnabled = loanAccountInfo.getGroupLoanAccountDetails().isRepaymentScheduleIndependentOfCustomerMeeting();
        LoanScheduleConfiguration configuration = new LoanScheduleConfiguration(loanScheduleIndependentOfCustomerMeetingEnabled, interestDays);

        MeetingBO repaymentDayMeeting = null;
        if (loanScheduleIndependentOfCustomerMeetingEnabled) {
            repaymentDayMeeting = this.createNewMeetingForRepaymentDay(loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), loanAccountInfo.getGroupLoanAccountDetails(), loanAccountDetail.getCustomer());
        } else {
            MeetingDto customerMeetingDto = customer.getCustomerMeetingValue().toDto();
            repaymentDayMeeting = new MeetingFactory().create(customerMeetingDto);
            
            Short recurAfter = loanAccountDetail.getLoanProduct().getLoanOfferingMeeting().getMeeting().getRecurAfter();
            repaymentDayMeeting.getMeetingDetails().setRecurAfter(recurAfter);
        }

        List<DateTime> loanScheduleDates = new ArrayList<DateTime>();

        LoanSchedule loanSchedule = assembleLoanSchedule(loanAccountDetail.getCustomer(), loanAccountDetail.getLoanProduct(),
                overridenDetail, configuration, repaymentDayMeeting, userOffice, loanScheduleDates,
                loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), new ArrayList<Number>());

        // 2. create loan
        InstallmentRange installmentRange = new MaxMinNoOfInstall(loanAccountInfo.getGroupLoanAccountDetails().getMinAllowedNumberOfInstallments().shortValue(),
                loanAccountInfo.getGroupLoanAccountDetails().getMaxAllowedNumberOfInstallments().shortValue(), null);
        AmountRange amountRange = new MaxMinLoanAmount(loanAccountInfo.getGroupLoanAccountDetails().getMaxAllowedLoanAmount().doubleValue(), loanAccountInfo.getGroupLoanAccountDetails().getMinAllowedLoanAmount().doubleValue(), null);

        creationDate = loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate().toDateMidnight().toDateTime();

        CreationDetail creationDetail = new CreationDetail(creationDate, Integer.valueOf(user.getUserId()));
        LoanBO loan = LoanBO.openGroupLoanAccount(loanAccountDetail.getLoanProduct(), loanAccountDetail.getCustomer(), repaymentDayMeeting,
                loanSchedule, loanAccountDetail.getAccountState(), loanAccountDetail.getFund(), overridenDetail, configuration, installmentRange, amountRange,
                creationDetail, createdBy);
        loan.setBusinessActivityId(loanAccountInfo.getGroupLoanAccountDetails().getLoanPurposeId());
        loan.setExternalId(loanAccountInfo.getGroupLoanAccountDetails().getExternalId());
        loan.setCollateralNote(loanAccountInfo.getGroupLoanAccountDetails().getCollateralNotes());
        loan.setCollateralTypeId(loanAccountInfo.getGroupLoanAccountDetails().getCollateralTypeId());

        loan.markAsCreatedWithBackdatedPayments();

        //set up predefined loan account for importing loans
        if(loanAccountInfo.getGroupLoanAccountDetails().getPredefinedAccountNumber()!=null){
            loan.setGlobalAccountNum(loanAccountInfo.getGroupLoanAccountDetails().getPredefinedAccountNumber());
        }
        

        // for GLIM loans only
        List<CreateLoanAccount> individualMembersOfGroupLoan = new ArrayList<CreateLoanAccount>();
        List<BigDecimal> radio = new ArrayList<BigDecimal>(loan.getNoOfInstallments());
        
        List<LoanProductOverridenDetail> memberOverridenDetails = new ArrayList<LoanProductOverridenDetail>();
        List<LoanSchedule> memberLoanSchedules = new ArrayList<LoanSchedule>();
        List<ClientBO> clients = new ArrayList<ClientBO>();
        
        for (CreateLoanAccount groupMemberAccount : memberDetails) {
            ClientBO member = this.customerDao.findClientById(groupMemberAccount.getCustomerId());
            Money loanAmount = new Money(loanAccountDetail.getLoanProduct().getCurrency(), groupMemberAccount.getLoanAmount());
            List<CreateAccountPenaltyDto> defaultAccountPenalties = new ArrayList<CreateAccountPenaltyDto>();
            
            radio.add(loanAmount.divide(loan.getLoanAmount()));
            
            int memberCount = memberDetails.size();
            for(CreateAccountPenaltyDto createAccountPenaltyDto : loanAccountInfo.getGroupLoanAccountDetails().getAccountPenalties()) {
                Integer penaltyId = createAccountPenaltyDto.getPenaltyId();
                String amount = createAccountPenaltyDto.getAmount();
                PenaltyBO penaltyBO = this.penaltyDao.findPenaltyById(penaltyId.shortValue());
                
                if(penaltyBO instanceof AmountPenaltyBO) {
                    amount = String.valueOf(Double.valueOf(createAccountPenaltyDto.getAmount()) / memberCount);
                }
                
                defaultAccountPenalties.add(new CreateAccountPenaltyDto(penaltyId, amount));
            }
            
            List<AccountFeesEntity> feeEntities = assembleAccountFees(groupMemberAccount.getAccountFees());
            List<AccountPenaltiesEntity> penaltyEntities = assembleAccountPenalties(defaultAccountPenalties);
            LoanProductOverridenDetail memberOverridenDetail = new LoanProductOverridenDetail(loanAmount, feeEntities, overridenDetail, penaltyEntities);

            LoanSchedule memberSchedule = assembleLoanSchedule(member, loanAccountDetail.getLoanProduct(), memberOverridenDetail, configuration, repaymentDayMeeting, userOffice, new ArrayList<DateTime>(), loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), new ArrayList<Number>());
            memberOverridenDetails.add(memberOverridenDetail);
            memberLoanSchedules.add(memberSchedule);
            clients.add(member);
            individualMembersOfGroupLoan.add(groupMemberAccount);
        }
        
        List<LoanBO> memberLoans = new ArrayList<LoanBO>(); //for original schedule persisting
        int index = 0;
        for (CreateLoanAccount groupMemberAccount : individualMembersOfGroupLoan) {

            LoanBO memberLoan = LoanBO.openGroupLoanForAccount(loan, loanAccountDetail.getLoanProduct(), clients.get(index), repaymentDayMeeting, memberLoanSchedules.get(index), memberOverridenDetails.get(index), configuration,
                    installmentRange, amountRange, creationDetail, createdBy, Boolean.TRUE);
            if (groupMemberAccount.getLoanPurposeId() > 0) {
                memberLoan.setBusinessActivityId(groupMemberAccount.getLoanPurposeId());
            }
                
            memberLoan.markAsCreatedWithBackdatedPayments();
            memberLoans.add(memberLoan);
            index ++;
        }
        
        
        // update loan schedule for Group Loan Account
        loanSchedule = this.loanScheduleService.generateGroupLoanSchedule(loanAccountDetail.getLoanProduct(), repaymentDayMeeting, loanSchedule, memberLoanSchedules, 
                loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(), overridenDetail, configuration, userOffice.getOfficeId(), loanAccountDetail.getCustomer(), accountFeeEntities);

        fixMemberAndParentInstallmentDetails(loan, memberLoans);
        

        // translate schedules to dto form
        Map<Integer, LoanScheduleEntity> parentScheduleEntities = loan.getLoanScheduleEntityMap();
        
        List<LoanCreationInstallmentDto> parentInstallments = new ArrayList<LoanCreationInstallmentDto>();
        Map<Integer, List<LoanCreationInstallmentDto>> memberInstallments = new HashMap<Integer, List<LoanCreationInstallmentDto>>();
        
        for (CreateLoanAccount member : memberDetails) {
            memberInstallments.put(member.getCustomerId(), new ArrayList<LoanCreationInstallmentDto>());
        }
        
        Short digitsAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        
        for (Integer installmentId : parentScheduleEntities.keySet()) {
            LoanScheduleEntity loanScheduleEntity = parentScheduleEntities.get(installmentId);
            parentInstallments.add(loanScheduleEntity.toLoanCreationInstallmentDto(digitsAfterDecimal));
            
            for (LoanBO memberLoan : memberLoans) {
                LoanScheduleEntity memberLoanScheduleEntity = memberLoan.getLoanScheduleEntityMap().get(installmentId);
                memberInstallments.get(memberLoan.getCustomer().getCustomerId()).add(
                        memberLoanScheduleEntity.toLoanCreationInstallmentDto(digitsAfterDecimal));
            }
        }
        
        GroupLoanScheduleDto groupScheduleDto = new GroupLoanScheduleDto(customer.getDisplayName(),
                Double.valueOf(loan.getLoanAmount().getAmountDoubleValue()),
                loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(),
                loan.getGraceType().getValue().intValue(), parentInstallments);
        groupScheduleDto.setMemberSchedules(new HashMap<Integer, LoanScheduleDto>());

        for (LoanBO memberLoan : memberLoans) {
            Integer id = memberLoan.getCustomer().getCustomerId();
            LoanScheduleDto memberScheduleDto = new LoanScheduleDto(memberLoan.getCustomer().getDisplayName(),
                    Double.valueOf(memberLoan.getLoanAmount().getAmountDoubleValue()),
                    loanAccountInfo.getGroupLoanAccountDetails().getDisbursementDate(),
                    loan.getGraceType().getValue().intValue(), memberInstallments.get(id));  
            groupScheduleDto.getMemberSchedules().put(id, memberScheduleDto);
        }
        
        return groupScheduleDto;
    }

}
