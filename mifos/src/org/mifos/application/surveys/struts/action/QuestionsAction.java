package org.mifos.application.surveys.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;

public class QuestionsAction extends BaseAction {

	SurveysPersistence surveysPersistence;
	
	public QuestionsAction() {
		this(new SurveysPersistence());
	}
	
	public QuestionsAction(SurveysPersistence persistence) {
		surveysPersistence = persistence;
	}
	
	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
		//             return new SurveysBusinessService();
	}

	public ActionForward viewQuestions(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<Question> questionList = surveysPersistence.retrieveAllQuestions();

		request.getSession().setAttribute("questionList", questionList);
		//request.getSession().setAttribute("types", AnswerType.values());
		return mapping.findForward(ActionForwards.viewAll_success.toString());
	}

	public ActionForward defineQuestions(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
