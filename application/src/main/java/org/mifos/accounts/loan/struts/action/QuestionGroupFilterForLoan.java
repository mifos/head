package org.mifos.accounts.loan.struts.action;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.QuestionGroupReference;
import org.mifos.application.questionnaire.struts.QuestionGroupFilter;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuestionGroupFilterForLoan implements QuestionGroupFilter {
    private LoanOfferingBO loanOfferingBO;

    public void setLoanOfferingBO(LoanOfferingBO loanOfferingBO) {
        this.loanOfferingBO = loanOfferingBO;
    }

    @Override
    public List<QuestionGroupDetail> doFilter(List<QuestionGroupDetail> questionGroupDetails) {
        if (questionGroupDetails == null || loanOfferingBO == null) return questionGroupDetails;
        List<QuestionGroupDetail> filteredQuestionGroupDetails = new ArrayList<QuestionGroupDetail>();
        Set<QuestionGroupReference> questionGroupReferences = loanOfferingBO.getQuestionGroups();
        if (questionGroupReferences != null) {
            for (QuestionGroupReference questionGroupReference : questionGroupReferences) {
                QuestionGroupDetail questionGroupDetail = findQuestionGroupById(questionGroupReference.getQuestionGroupId(), questionGroupDetails);
                if (questionGroupDetail != null) filteredQuestionGroupDetails.add(questionGroupDetail);
            }
        }
        return filteredQuestionGroupDetails;
    }

    private QuestionGroupDetail findQuestionGroupById(Integer questionGroupId, List<QuestionGroupDetail> questionGroupDetails) {
        QuestionGroupDetail result = null;
        for (QuestionGroupDetail questionGroupDetail : questionGroupDetails) {
            if (questionGroupId.equals(questionGroupDetail.getId())) {
                result = questionGroupDetail;
                break;
            }
        }
        return result;
    }
}
