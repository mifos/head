package org.mifos.application.surveys.struts.action;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.QuestionActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.PersistenceAction;

public class QuestionsAction extends PersistenceAction {
	
	
	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
		//             return new SurveysBusinessService();
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("questionsAction");
		security.allow("viewQuestions", SecurityConstants.VIEW);
		security.allow("defineQuestions", SecurityConstants.VIEW);
		security.allow("addChoice", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("deleteChoice", SecurityConstants.VIEW);
		security.allow("deleteNewQuestion", SecurityConstants.VIEW);
		security.allow("addQuestion", SecurityConstants.VIEW);
		security.allow("createQuestions", SecurityConstants.VIEW);
		return security;
	}

	public ActionForward viewQuestions(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		
		List<Question> questionList = surveysPersistence.retrieveAllQuestions();

		request.setAttribute(SurveysConstants.KEY_QUESTIONS_LIST, questionList);
		return mapping.findForward(ActionForwards.viewAll_success.toString());
	}

	public ActionForward defineQuestions(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<String> choices = new LinkedList<String>();
		request.getSession().setAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES, choices);
		LinkedList<Question> newQuestions = new LinkedList<Question>();
		request.getSession().setAttribute(SurveysConstants.KEY_NEW_QUESTIONS, newQuestions);
		LinkedList<AnswerType> answerTypes = new LinkedList<AnswerType>();
		for (AnswerType type : AnswerType.values()) {
			answerTypes.add(type);
		}
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward addChoice(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QuestionActionForm actionForm = (QuestionActionForm) form;
		LinkedList<String> choices = (LinkedList<String>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES);
		choices.add(actionForm.getChoice());
		actionForm.setChoice("");
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward deleteChoice(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<String> choices = (LinkedList<String>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES);
		int index = Integer.parseInt(request.getParameter("choiceNum"));
		choices.remove(index);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward deleteNewQuestion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<Question> questions = (LinkedList<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		int index = Integer.parseInt(request.getParameter("newQuestionNum"));
		questions.remove(index);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward createQuestions(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<Question> questions = (LinkedList<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		SessionHolder holder = opener.open();
		SurveysPersistence persistence = new SurveysPersistence(holder);
		for (Question question : questions) {
			persistence.createOrUpdate(question);
		}
		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	public ActionForward addQuestion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		QuestionActionForm actionForm = (QuestionActionForm) form;
		AnswerType type = AnswerType.fromInt(Integer.parseInt(actionForm.getAnswerType()));
		Question question = new Question(actionForm.getQuestionText(), type);
		if (type == AnswerType.CHOICE) {
			List<QuestionChoice> choices = new LinkedList<QuestionChoice>();
			for (String choiceText : (List<String>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES)) {
				choices.add(new QuestionChoice(choiceText));
			}
			question.setChoices(choices);
		}
		LinkedList<Question> newQuestions = (LinkedList<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		newQuestions.add(question);
		actionForm.clear();
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
}