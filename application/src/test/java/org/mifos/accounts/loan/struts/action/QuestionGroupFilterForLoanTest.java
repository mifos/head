package org.mifos.accounts.loan.struts.action;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.QuestionGroupReference;
import org.mifos.domain.builders.LoanProductBuilder;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

public class QuestionGroupFilterForLoanTest {

    @Test
    public void shouldDoFilterForNoLoanOfferBO() {
        QuestionGroupFilterForLoan questionGroupFilterForLoan = new QuestionGroupFilterForLoan();
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"),
                getQuestionGroupDetail(2, "QG2"), getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        List<QuestionGroupDetail> filteredQuestionGroupDetails = questionGroupFilterForLoan.doFilter(questionGroupDetails,null);
        assertThat(filteredQuestionGroupDetails, is(questionGroupDetails));
    }

    @Test
    public void shouldDoFilterForNoQuestionGroupDetails() {
        QuestionGroupFilterForLoan questionGroupFilterForLoan = new QuestionGroupFilterForLoan();
        LoanOfferingBO loanOfferingBO = new LoanProductBuilder().buildForUnitTests();
        loanOfferingBO.setQuestionGroups(getQustionGroups(2, 4));
        questionGroupFilterForLoan.setLoanOfferingBO(loanOfferingBO);
        List<QuestionGroupDetail> filteredQuestionGroupDetails = questionGroupFilterForLoan.doFilter(null,null);
        assertThat(filteredQuestionGroupDetails, is(nullValue()));
    }

    @Test
    public void shouldDoFilter() {
        QuestionGroupFilterForLoan questionGroupFilterForLoan = new QuestionGroupFilterForLoan();
        LoanOfferingBO loanOfferingBO = new LoanProductBuilder().buildForUnitTests();
        loanOfferingBO.setQuestionGroups(getQustionGroups(2, 4));
        questionGroupFilterForLoan.setLoanOfferingBO(loanOfferingBO);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"),
                getQuestionGroupDetail(2, "QG2"), getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        List<QuestionGroupDetail> filteredQuestionGroupDetails = questionGroupFilterForLoan.doFilter(questionGroupDetails,new Criteria<QuestionGroupDetail>() {
            @Override
            public QuestionGroupDetail filter(Integer questionGroupId, List<QuestionGroupDetail> questionGroupDetails) {
                QuestionGroupDetail result = null;
                for (QuestionGroupDetail questionGroupDetail : questionGroupDetails) {
                    if (questionGroupId.equals(questionGroupDetail.getId())) {
                        result = questionGroupDetail;
                        break;
                    }
                }
                return result;
            }
        });
        assertThat(filteredQuestionGroupDetails, is(notNullValue()));
        assertThat(filteredQuestionGroupDetails.size(), is(2));
        assertThat(filteredQuestionGroupDetails.get(0).getId(), is(2));
        assertThat(filteredQuestionGroupDetails.get(0).getTitle(), is("QG2"));
        assertThat(filteredQuestionGroupDetails.get(1).getId(), is(4));
        assertThat(filteredQuestionGroupDetails.get(1).getTitle(), is("QG4"));
    }

    @Test
    public void shouldDoFilterForNoLoanOfferingQuestionGroups() {
        QuestionGroupFilterForLoan questionGroupFilterForLoan = new QuestionGroupFilterForLoan();
        LoanOfferingBO loanOfferingBO = new LoanProductBuilder().buildForUnitTests();
        loanOfferingBO.setQuestionGroups(new HashSet<QuestionGroupReference>());
        questionGroupFilterForLoan.setLoanOfferingBO(loanOfferingBO);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"),
                getQuestionGroupDetail(2, "QG2"), getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        List<QuestionGroupDetail> filteredQuestionGroupDetails = questionGroupFilterForLoan.doFilter(questionGroupDetails,null);
        assertThat(filteredQuestionGroupDetails, is(notNullValue()));
        assertThat(filteredQuestionGroupDetails.size(), is(0));
        loanOfferingBO.setQuestionGroups(null);
        filteredQuestionGroupDetails = questionGroupFilterForLoan.doFilter(questionGroupDetails,null);
        assertThat(filteredQuestionGroupDetails, is(notNullValue()));
        assertThat(filteredQuestionGroupDetails.size(), is(0));
    }

    private Set<QuestionGroupReference> getQustionGroups(int... questionGroupIds) {
        Set<QuestionGroupReference> questionGroupReferences = new HashSet<QuestionGroupReference>();
        for (int questionGroupId : questionGroupIds) {
            questionGroupReferences.add(makeQuestionGroupRef(questionGroupId));
        }
        return questionGroupReferences;
    }

    private QuestionGroupReference makeQuestionGroupRef(int questionGroupId) {
        QuestionGroupReference questionGroupReference = new QuestionGroupReference();
        questionGroupReference.setQuestionGroupId(questionGroupId);
        return questionGroupReference;
    }

    private QuestionGroupDetail getQuestionGroupDetail(int id, String title) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setId(id);
        questionGroupDetail.setTitle(title);
        return questionGroupDetail;
    }
}
