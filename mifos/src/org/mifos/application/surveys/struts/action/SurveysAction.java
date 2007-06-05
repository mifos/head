package org.mifos.application.surveys.struts.action;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.Constants;

public class SurveysAction extends PersistenceAction {

	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveysAction");
		security.allow("mainpage", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("create_entry", SecurityConstants.VIEW);
		security.allow("add_new_question", SecurityConstants.VIEW);
		security.allow("delete_new_question", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("create", SecurityConstants.VIEW);
		security.allow("newVersion", SecurityConstants.VIEW);
		security.allow("edit", SecurityConstants.VIEW);
		return security;
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
//		return new SurveysBusinessService();
	}
	
	public ActionForward mainpage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		List<Survey> customerSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.CUSTOMERS);
		List<Survey> accountsSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.ACCOUNTS);
		
		request.getSession().setAttribute(SurveysConstants.KEY_ACCOUNTS_SURVEYS_LIST, accountsSurveys);
		request.getSession().setAttribute(SurveysConstants.KEY_CUSTOMERS_SURVEYS_LIST, customerSurveys);
		return mapping.findForward(ActionForwards.load_main_page
				.toString());
	}

	
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		SurveyActionForm actionForm = (SurveyActionForm) form;
		int surveyId = actionForm.getSurveyIdValue();
		Survey survey = surveysPersistence.getSurvey(surveyId);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, survey);
		request.getSession().setAttribute(SurveysConstants.KEY_ITEM_COUNT, survey.getQuestions().size());
		holder.close();
		return mapping.findForward(ActionForwards.get_success.toString());
	}
	
	public ActionForward create_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	
			List<Question> questionsList = getQuestions();
		request.getSession().setAttribute(SurveysConstants.KEY_QUESTIONS_LIST,
				questionsList);
		request.getSession().setAttribute(SurveysConstants.KEY_ADDED_QUESTIONS,
				new LinkedList<Question>());
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyActionForm actionForm = (SurveyActionForm) form;
		request.getSession().setAttribute(
				SurveysConstants.KEY_SURVEY_ACTIONFORM, actionForm);
		List<Question> addedQuestions = (List<Question>) request.getSession()
				.getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		request.getSession().setAttribute(SurveysConstants.KEY_ITEM_COUNT,
				addedQuestions.size());
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward add_new_question(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyActionForm actionForm = (SurveyActionForm) form;
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		List<Question> questionsList = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_QUESTIONS_LIST);
		List<Question> addedQuestions = (List<Question>)request.getSession().getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		// TODO: insert code for an error message if the question id is invalid here
		try {
			int questionId = Integer.parseInt(actionForm.getNewQuestion());
			Question newQuestion = surveysPersistence.getQuestion(Integer.parseInt(actionForm.getNewQuestion()));
			if (questionsList.contains(newQuestion)) {
				addedQuestions.add(newQuestion);
				questionsList.remove(newQuestion);
			}
		
			return mapping.findForward(ActionForwards.create_entry_success.toString());
		}
		catch (NumberFormatException e) {
			return mapping.findForward(ActionForwards.create_entry_success.toString());
		}
	}
	
	public ActionForward delete_new_question(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyActionForm actionForm = (SurveyActionForm) form;
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		int questionNum = Integer.parseInt(actionForm.getQuestionNum());
		Question question = surveysPersistence.getQuestion(questionNum);
		List<Question> questionsList = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_QUESTIONS_LIST);
		List<Question> addedQuestions = (List<Question>)request.getSession().getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		if (addedQuestions.contains(question)) {
			addedQuestions.remove(question);
			questionsList.add(question);
		}
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
	
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionHolder holder = opener.open();
		SurveysPersistence persistence = new SurveysPersistence(holder);
		SurveyActionForm actionForm = (SurveyActionForm) form;
		SurveyType type = SurveyType.fromString(actionForm.getAppliesTo());
		Survey newSurvey = new Survey(actionForm.getName(),
				SurveyState.ACTIVE, type);
		List<Question> addedQuestions = (List<Question>)request.getSession().getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		for (Question question : addedQuestions) {
			//newSurvey.addQuestion(question, true);
		}
		persistence.createOrUpdate(newSurvey);
		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	private List<Question> getQuestions() throws PersistenceException {
			SessionHolder holder = opener.open();
			SurveysPersistence persistence = new SurveysPersistence(holder);
			return persistence.retrieveQuestionsByState(QuestionState.ACTIVE);
	}
	
	public ActionForward newVersion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyActionForm actionForm = (SurveyActionForm) form;
		int surveyId = Integer.parseInt(request.getParameter("surveyId"));
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		Survey survey = surveysPersistence.getSurvey(surveyId);
		String newName = survey.getName();
		Pattern pattern = Pattern.compile("(.*) v([0-9]+)");
		Matcher matcher = pattern.matcher(newName);
		if (matcher.matches()) {
			String base = matcher.group(1);
			int version = Integer.parseInt(matcher.group(2));
			newName = base + " v" + Integer.toString(version + 1);
		}
		else {
			newName = newName + " v2";
		}

		actionForm.setName(newName);
		actionForm.setAppliesTo(survey.getAppliesTo());
		List<Question> associatedQuestions = new LinkedList<Question>();
		List<Question> questionsList = getQuestions();
		for (SurveyQuestion question : survey.getQuestions()) {
			associatedQuestions.add(question.getQuestion());
			questionsList.remove(question.getQuestion());
		}
		request.getSession().setAttribute(SurveysConstants.KEY_QUESTIONS_LIST,
				questionsList);
		request.getSession().setAttribute(SurveysConstants.KEY_ADDED_QUESTIONS,
				associatedQuestions);
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}

}
