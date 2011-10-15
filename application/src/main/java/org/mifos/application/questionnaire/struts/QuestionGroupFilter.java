package org.mifos.application.questionnaire.struts;

import java.util.List;

import org.mifos.accounts.loan.struts.action.Criteria;

public interface QuestionGroupFilter {
    <T> List<T> doFilter(List<T> t, Criteria<T> criteria);
}
