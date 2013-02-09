package org.mifos.clientportfolio.loan.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.GroupLoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.loan.ui.helper.LoanControllerHelper;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGroupLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.screen.GroupLoanScheduleDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.service.BusinessRuleException;
import org.mifos.ui.core.controller.util.helpers.LoanCreationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Controller;

@SuppressWarnings("PMD")
@Controller
public class GroupLoanAccountController {

    private final LoanAccountServiceFacade loanAccountServiceFacade;
    
    private final GroupLoanAccountServiceFacade groupLoanAccountServiceFacade;
    
    private LoanControllerHelper loanControllerHelper;
    
    @Autowired
    public GroupLoanAccountController(LoanAccountServiceFacade loanAccountServiceFacade,
    		GroupLoanAccountServiceFacade groupLoanAccountServiceFacade,
    		LoanControllerHelper loanControllerHelper) {
        this.loanAccountServiceFacade = loanAccountServiceFacade;
        this.groupLoanAccountServiceFacade = groupLoanAccountServiceFacade;
        this.loanControllerHelper = loanControllerHelper;
    }
    
    public LoanCreationResultDto saveLoanApplicationForLater(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

        return submitGroupLoanApplication(applicationState.getPartialApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
    }
    
    public LoanCreationResultDto saveGroupLoanApplication(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

        return submitGroupLoanApplication(applicationState.getConfiguredApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
    }
    
    
    
    public LoanCreationResultDto submitGroupLoanApplication(Integer accountState, LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {
        
        LocalDate disbursementDate =  LoanCreationHelper.translateDisbursementDateToLocalDate(formBean);
        RecurringSchedule recurringSchedule =  LoanCreationHelper.determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees =  LoanCreationHelper.translateToAccountFeeDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees =  LoanCreationHelper.translateToAdditionalAccountFeeDtos(formBean);
        List<CreateAccountPenaltyDto> accountPenalties = LoanCreationHelper.translateToAccountPenaltyDtos(formBean);
        accountFees.addAll(additionalAccountFees);
        
        BigDecimal loanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());
        BigDecimal minAllowedLoanAmount = BigDecimal.valueOf(formBean.getMinAllowedAmount().doubleValue());
        BigDecimal maxAllowedLoanAmount = BigDecimal.valueOf(formBean.getMaxAllowedAmount().doubleValue());

        CreateLoanAccount loanAccountDetails = prepareLoanAccount(accountState, formBean, disbursementDate,
                recurringSchedule, accountFees, accountPenalties, loanAmount, minAllowedLoanAmount,
                maxAllowedLoanAmount);

        LoanCreationResultDto loanCreationResultDto = null;

        List<CreateLoanAccount> memberAccounts = createGroupLoanMembers(accountState, formBean, disbursementDate, recurringSchedule, additionalAccountFees,
                accountPenalties, loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount);
        BigDecimal totalLoanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());

        CreateGroupLoanAccount createGroupLoanAccount = new CreateGroupLoanAccount(memberAccounts, totalLoanAmount, loanAccountDetails);

        loanCreationResultDto = groupLoanAccountServiceFacade.createGroupLoan(createGroupLoanAccount, loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
   
        return loanCreationResultDto;
    }
    
    public LoanCreationResultDto openLoanWithBackdatedPayments(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean, MessageContext messageContext) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

            try {
            	return submitLoanWithBackdatedPaymentsApplication(applicationState.getPartialApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
            }
            catch (BusinessRuleException e) {
                MessageBuilder builder = new MessageBuilder()
                        .error()
                        .codes(Arrays.asList(e.getMessageKey()).toArray(
                                new String[1])).defaultText(e.getMessage())
                        .args(e.getMessageValues());

                messageContext.addMessage(builder.build());
                throw e;
            }
    }
    
    public GroupLoanScheduleDto retrieveLoanSchedule(int customerId, int productId, LoanAccountFormBean formBean,
            LoanScheduleFormBean loanScheduleFormBean, boolean resetRedoLoanAccountDetails) {
        
        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
        Integer accountState = applicationState.getConfiguredApplicationId();
        LocalDate disbursementDate =  LoanCreationHelper.translateDisbursementDateToLocalDate(formBean);
        RecurringSchedule recurringSchedule =  LoanCreationHelper.determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees =  LoanCreationHelper.translateToAccountFeeDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees =  LoanCreationHelper.translateToAdditionalAccountFeeDtos(formBean);
        List<CreateAccountPenaltyDto> accountPenalties = LoanCreationHelper.translateToAccountPenaltyDtos(formBean);
        accountFees.addAll(additionalAccountFees);
        
        BigDecimal loanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());
        BigDecimal minAllowedLoanAmount = BigDecimal.valueOf(formBean.getMinAllowedAmount().doubleValue());
        BigDecimal maxAllowedLoanAmount = BigDecimal.valueOf(formBean.getMaxAllowedAmount().doubleValue());

        CreateLoanAccount loanAccountDetails = prepareLoanAccount(accountState, formBean, disbursementDate,
                recurringSchedule, accountFees, accountPenalties, loanAmount, minAllowedLoanAmount,
                maxAllowedLoanAmount);

        List<CreateLoanAccount> memberAccounts = createGroupLoanMembers(accountState, formBean, disbursementDate, recurringSchedule, additionalAccountFees,
                accountPenalties, loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount);
        BigDecimal totalLoanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());

        CreateGroupLoanAccount createGroupLoanAccount = new CreateGroupLoanAccount(memberAccounts, totalLoanAmount, loanAccountDetails);
        
        GroupLoanScheduleDto groupLoanScheduleDto = groupLoanAccountServiceFacade.getGroupLoanScheduleDto(
                createGroupLoanAccount, memberAccounts);
        
        List<String> globalMemberIds = loanControllerHelper.getSelectedMemberGlobalIds(formBean);
        List<Integer> memberIds = loanControllerHelper.getSelectedMemberIdsFromFormBean(formBean);
        List<BigDecimal> memberAmounts = loanControllerHelper.getSelectedMemberAmounts(formBean);
        loanScheduleFormBean.setVariableInstallments(groupLoanScheduleDto.getGroupSchedule().getInstallments());
        
        for (int i = 0; i < memberIds.size(); ++i) {
            Integer memberId = memberIds.get(i);
            String memberIdString = memberId.toString();
            LoanScheduleFormBean memberScheduleBean = loanScheduleFormBean.getMemberSchedules().get(memberIdString);
            if (memberScheduleBean == null) {
                memberScheduleBean = new LoanScheduleFormBean();
            }
            memberScheduleBean.setGlobalCustomerId(globalMemberIds.get(i));
            loanScheduleFormBean.getMemberSchedules().put(memberIdString, memberScheduleBean);
            LoanScheduleDto memberLoanSchedule = groupLoanScheduleDto.getMemberSchedules().get(memberId);
            loanControllerHelper.populateFormBeanFromDto(memberId, productId, formBean,
                    memberScheduleBean, disbursementDate, memberLoanSchedule, resetRedoLoanAccountDetails);
            memberScheduleBean.setLoanPrincipal(memberAmounts.get(i));
            
        }
        loanScheduleFormBean.setActualPaymentAmounts(new ArrayList<Number>());

        return groupLoanScheduleDto;
    }
    
    private LoanCreationResultDto submitLoanWithBackdatedPaymentsApplication(Integer accountState, LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LocalDate disbursementDate = LoanCreationHelper.translateDisbursementDateToLocalDate(formBean);
        RecurringSchedule recurringSchedule = LoanCreationHelper.determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees = LoanCreationHelper.translateToAccountFeeDtos(formBean);
        List<CreateAccountPenaltyDto> accountPenalties = LoanCreationHelper.translateToAccountPenaltyDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees = LoanCreationHelper.translateToAdditionalAccountFeeDtos(formBean);
        accountFees.addAll(additionalAccountFees);
        
        BigDecimal loanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());
        BigDecimal minAllowedLoanAmount = BigDecimal.valueOf(formBean.getMinAllowedAmount().doubleValue());
        BigDecimal maxAllowedLoanAmount = BigDecimal.valueOf(formBean.getMaxAllowedAmount().doubleValue());
        
        Map<Integer, List<LoanPaymentDto>> backdatedMemberLoanPayments = new HashMap<Integer, List<LoanPaymentDto>>();
        List<Integer> memberIds = loanControllerHelper.getSelectedMemberIdsFromFormBean(formBean);
        
        for (Integer memberId : memberIds) {
            String memberIdString = memberId.toString();
            List<Number> actualPaymentAmountDetails = loanScheduleFormBean.getMemberSchedules().get(memberIdString).getActualPaymentAmounts();
            List<LoanPaymentDto> backdatedLoanPayments = new ArrayList<LoanPaymentDto>();
            List<DateTime> actualPaymentDates = loanScheduleFormBean.getMemberSchedules().get(memberIdString).getActualPaymentDates(); 
            List<Short> actualPaymentTypes = loanScheduleFormBean.getMemberSchedules().get(memberIdString).getActualPaymentTypes();
            
            for (int i = 0; i < actualPaymentAmountDetails.size(); ++i) {
                Number actualPaymentAmount = actualPaymentAmountDetails.get(i);
                
                if (actualPaymentAmount.doubleValue() > 0) {
                    LocalDate transactionDate = new LocalDate(actualPaymentDates.get(i));
                    backdatedLoanPayments.add(new LoanPaymentDto(actualPaymentAmount.toString(), transactionDate, actualPaymentTypes.get(i), null));
                }
            }
            
            backdatedMemberLoanPayments.put(memberId, backdatedLoanPayments);
        }
        
        CreateLoanAccount loanAccountDetails = prepareLoanAccount(accountState, formBean, disbursementDate,
                recurringSchedule, accountFees, accountPenalties, loanAmount, minAllowedLoanAmount,
                maxAllowedLoanAmount);

        List<CreateLoanAccount> memberAccounts = createGroupLoanMembers(accountState, formBean, disbursementDate, recurringSchedule, additionalAccountFees,
                accountPenalties, loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount);
        BigDecimal totalLoanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());

        CreateGroupLoanAccount createGroupLoanAccount = new CreateGroupLoanAccount(memberAccounts, totalLoanAmount, loanAccountDetails);
        
        return groupLoanAccountServiceFacade.createBackdatedGroupLoan(createGroupLoanAccount, backdatedMemberLoanPayments, 
                loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
    }

    private CreateLoanAccount prepareLoanAccount(Integer accountState, LoanAccountFormBean formBean,
            LocalDate disbursementDate, RecurringSchedule recurringSchedule, List<CreateAccountFeeDto> accountFees,
            List<CreateAccountPenaltyDto> accountPenalties, BigDecimal loanAmount, BigDecimal minAllowedLoanAmount,
            BigDecimal maxAllowedLoanAmount) {
        CreateLoanAccount loanAccountDetails = new CreateLoanAccount(formBean.getCustomerId(),
                formBean.getProductId(), accountState, loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount,
                formBean.getInterestRate().doubleValue(), disbursementDate, null, formBean.getNumberOfInstallments().intValue(),
                formBean.getMinNumberOfInstallments().intValue(), formBean.getMaxNumberOfInstallments().intValue(),
                formBean.getGraceDuration().intValue(), formBean.getFundId(),
                formBean.getLoanPurposeId(), formBean.getCollateralTypeId(), formBean.getCollateralNotes(),
                formBean.getExternalId(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees, accountPenalties);
        return loanAccountDetails;
    }
    
    private CreateLoanAccount prepareGroupLoanAccountMember(Integer accountState, LoanAccountFormBean formBean,
            LocalDate disbursementDate, RecurringSchedule recurringSchedule, List<CreateAccountFeeDto> accountFees,
            List<CreateAccountPenaltyDto> accountPenalties, BigDecimal loanAmount, BigDecimal minAllowedLoanAmount,
            BigDecimal maxAllowedLoanAmount, int index) {
        Integer memberId = this.groupLoanAccountServiceFacade.getMemberClientId(formBean.getClientGlobalId()[index]);
        CreateLoanAccount loanAccountDetails = new CreateLoanAccount(memberId,
                formBean.getProductId(), accountState, BigDecimal.valueOf(formBean.getClientAmount()[index].doubleValue()), minAllowedLoanAmount, maxAllowedLoanAmount,
                formBean.getInterestRate().doubleValue(), disbursementDate, null, formBean.getNumberOfInstallments().intValue(),
                formBean.getMinNumberOfInstallments().intValue(), formBean.getMaxNumberOfInstallments().intValue(),
                formBean.getGraceDuration().intValue(), formBean.getFundId(),
                formBean.getClientLoanPurposeId()[index], formBean.getCollateralTypeId(), formBean.getCollateralNotes(),
                formBean.getExternalId(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees, accountPenalties);
        return loanAccountDetails;
    }

    private List<CreateLoanAccount> createGroupLoanMembers(Integer accountState, LoanAccountFormBean formBean,
            LocalDate disbursementDate, RecurringSchedule recurringSchedule, List<CreateAccountFeeDto> accountFees,
            List<CreateAccountPenaltyDto> accountPenalties, BigDecimal loanAmount, BigDecimal minAllowedLoanAmount,
            BigDecimal maxAllowedLoanAmount) {
        List<CreateLoanAccount> memberAccounts = new ArrayList<CreateLoanAccount>();
        
        int index = 0;
        for (Boolean clientSelected : formBean.getClientSelectForGroup()) {
            if (clientSelected != null && clientSelected.booleanValue()) {
                CreateLoanAccount memberAccount = prepareGroupLoanAccountMember(accountState, formBean, disbursementDate, recurringSchedule, accountFees, accountPenalties,
                        BigDecimal.valueOf(formBean.getClientAmount()[index].doubleValue()), minAllowedLoanAmount, maxAllowedLoanAmount, index);
                memberAccounts.add(memberAccount);
            }
            index++;
        }
        return memberAccounts;
    }
}
