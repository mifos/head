package org.mifos.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.QuestionGroupReference;
import org.mifos.application.questionnaire.struts.QuestionGroupFilter;

public class QuestionGroupFilterForLoan implements QuestionGroupFilter{
    private LoanOfferingBO loanOfferingBO;

    public void setLoanOfferingBO(LoanOfferingBO loanOfferingBO) {
        this.loanOfferingBO = loanOfferingBO;
    }

    @Override
    public <T> List<T> doFilter(List<T> t, Criteria<T> criteria) {
        if (t == null || loanOfferingBO == null) return t;
        List<T> filteredResult = new ArrayList<T>();
        Set<QuestionGroupReference> questionGroupReferences = loanOfferingBO.getQuestionGroups();
        if (questionGroupReferences != null) {
            for (QuestionGroupReference questionGroupReference : questionGroupReferences) {
                T result = criteria.filter(questionGroupReference.getQuestionGroupId(), t);
                if (result != null) filteredResult.add(result);
            }
        }
        return filteredResult;
    }
}
