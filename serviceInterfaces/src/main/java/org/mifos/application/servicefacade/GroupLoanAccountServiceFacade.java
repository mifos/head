package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.clientportfolio.newloan.applicationservice.CreateGroupLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

public interface GroupLoanAccountServiceFacade {

    LoanCreationResultDto createGroupLoan(CreateGroupLoanAccount createGroupLoanAccount,List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow);
    
    Integer getMemberClientId(String globalCustNum);
}
