package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.mifos.clientportfolio.newloan.applicationservice.CreateGroupLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.dto.domain.GroupIndividualLoanDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.screen.GroupLoanMemberAdjustmentDto;
import org.mifos.dto.screen.GroupLoanScheduleDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

public interface GroupLoanAccountServiceFacade {

    LoanCreationResultDto createGroupLoan(CreateGroupLoanAccount createGroupLoanAccount,List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow);
    
    LoanCreationResultDto createBackdatedGroupLoan(CreateGroupLoanAccount createGroupLoanAccount, Map<Integer, List<LoanPaymentDto>> backdatedLoanPayments,
    		List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow);
    
    Integer getMemberClientId(String globalCustNum);
    
    int getNumberOfMemberAccounts(Integer accountId);
    
    List<Integer> getListOfMemberAccountIds(Integer parentAccountId);
    
    List<String> getListOfMemberGlobalAccountNumbers(Integer parentAccountId);
    
    List<GroupIndividualLoanDto> getMemberLoansAndDefaultPayments(Integer parentAccountId, BigDecimal amount);

    LoanInformationDto retrieveLoanInformation(String globalAccountNum);
    
    List<GroupLoanMemberAdjustmentDto> retrieveMemberAdjustmentDtos(Integer parentAccountId, Integer parentPaymentId, BigDecimal newAmount);
    
    GroupLoanScheduleDto getGroupLoanScheduleDto(CreateGroupLoanAccount createGroupLoanAccount, List<CreateLoanAccount> memberDetails);
}
