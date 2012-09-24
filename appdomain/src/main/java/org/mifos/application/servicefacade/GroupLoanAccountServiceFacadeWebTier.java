package org.mifos.application.servicefacade;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.MaxMinLoanAmount;
import org.mifos.accounts.loan.business.MaxMinNoOfInstall;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.action.validate.ProductMixValidator;
import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.persistence.PenaltyDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.InstallmentRange;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGroupLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.domain.CreationDetail;
import org.mifos.clientportfolio.newloan.domain.GroupMemberLoanDetail;
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
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.screen.LoanCreationResultDto;
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
        return createGroupLoanAccount(loanAccountInfo, new ArrayList<LoanPaymentDto>(), questionGroups, loanAccountCashFlow, new ArrayList<DateTime>(), new ArrayList<Number>(), loanAccountInfo.getMemberDetails(), false);
    }
    
    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }
    
    private LoanCreationResultDto createGroupLoanAccount(CreateGroupLoanAccount loanAccountInfo, List<LoanPaymentDto> backdatedLoanPayments,
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
        LoanBO loan = LoanBO.openStandardLoanAccount(loanAccountDetail.getLoanProduct(), loanAccountDetail.getCustomer(), repaymentDayMeeting,
                loanSchedule, loanAccountDetail.getAccountState(), loanAccountDetail.getFund(), overridenDetail, configuration, installmentRange, amountRange,
                creationDetail, createdBy);
        loan.setBusinessActivityId(loanAccountInfo.getGroupLoanAccountDetails().getLoanPurposeId());
        loan.setExternalId(loanAccountInfo.getGroupLoanAccountDetails().getExternalId());
        loan.setCollateralNote(loanAccountInfo.getGroupLoanAccountDetails().getCollateralNotes());
        loan.setCollateralTypeId(loanAccountInfo.getGroupLoanAccountDetails().getCollateralTypeId());
        loan.setGroupLoan(Boolean.TRUE);
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
                List<CreateAccountFeeDto> defaultAccountFees = new ArrayList<CreateAccountFeeDto>();
                List<CreateAccountPenaltyDto> defaultAccountPenalties = new ArrayList<CreateAccountPenaltyDto>();
                
                radio.add(loanAmount.divide(loan.getLoanAmount()));
                
                for(CreateAccountFeeDto createAccountFeeDto : loanAccountInfo.getGroupLoanAccountDetails().getAccountFees()) {
                    Integer feeId = createAccountFeeDto.getFeeId();
                    String amount = createAccountFeeDto.getAmount();
                    FeeBO feeBO = this.feeDao.findById(feeId.shortValue());
                    
                    if(feeBO instanceof AmountFeeBO) {
                        amount = String.valueOf(Double.valueOf(createAccountFeeDto.getAmount()) * (loanAmount.divide(loanAccountInfo.getGroupLoanAccountDetails().getLoanAmount()).getAmount().doubleValue()));
                    }
                    
                    defaultAccountFees.add(new CreateAccountFeeDto(feeId, amount));
                }
                
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
                
                List<AccountFeesEntity> feeEntities = assembleAccountFees(defaultAccountFees);
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

                customer.updatePerformanceHistoryOnDisbursement(loan, loan.getLoanAmount());
                // end of refactoring of loan disbural
                this.loanDao.save(loan);
                transactionHelper.flushSession();

                // 5. apply each payment
                for (LoanPaymentDto loanPayment : backdatedLoanPayments) {
                    Money amountPaidToDate = new Money(loan.getCurrency(), loanPayment.getAmount());
                    PaymentData paymentData = new PaymentData(amountPaidToDate, createdBy, loanPayment.getPaymentTypeId(), loanPayment
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

}
