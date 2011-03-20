package org.mifos.application.questionnaire.struts;

import org.mifos.accounts.loan.struts.action.Criteria;

import java.util.List;

public interface QuestionGroupFilter {
    <T> List<T> doFilter(List<T> t, Criteria<T> criteria);
}
