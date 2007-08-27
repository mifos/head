package org.mifos.application.surveys.struts.action;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.NotNullEmptyValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.struts.actionforms.GenericActionForm;

public class QuestionsAction extends PersistenceAction {
	
	private static final int QUESTIONS_PER_PAGE = 20;
	private static Schema addQuestionValidator;
	
	static {
		addQuestionValidator = new Schema();
		addQuestionValidator.setSimpleValidator("value(shortName)",
				new NotNullEmptyValidator());
		addQuestionValidator.setSimpleValidator("value(questionText)",
				new NotNullEmptyValidator());
	}
	
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
		security.allow("get", SecurityConstants.VIEW);
		security.allow("edit_entry", SecurityConstants.VIEW);
		security.allow("update_entry", SecurityConstants.VIEW);
		security.allow("preview_entry", SecurityConstants.VIEW);
		return security;
	}

	public ActionForward viewQuestions(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		
		int length = surveysPersistence.getNumQuestions();
		String offsetParameter = request.getParameter(
				SurveysConstants.KEY_ITEM_OFFSET);
		int offset = offsetParameter != null ?
				Integer.parseInt(offsetParameter) : 1;
		List<Question> questionList =
			surveysPersistence.retrieveSomeQuestions(offset - 1,
					QUESTIONS_PER_PAGE);
		
		request.setAttribute(SurveysConstants.KEY_QUESTIONS_LIST, questionList);
		request.setAttribute("length", length);
		request.setAttribute(SurveysConstants.KEY_ITEM_OFFSET, offset);
		return mapping.findForward(ActionForwards.viewAll_success.toString());
	}
	
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		
		int questionId = Integer.parseInt(request.getParameter("questionId"));
		Question question = surveysPersistence.getQuestion(questionId);
		request.getSession().setAttribute(SurveysConstants.KEY_QUESTION,
				question);
		
		return mapping.findForward(ActionForwards.get_success.toString());
	}
	
	public ActionForward edit_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Question question = (Question) request.getSession().getAttribute(
				SurveysConstants.KEY_QUESTION);
		List<QuestionChoice> choices = question.getChoices();
		
		// Determine if question belongs to a PPISurvey
		if (question.getAnswerType() == AnswerType.CHOICE.getValue()
				&& choices.size() > 0 && choices.get(0) instanceof PPIChoice) {
			
			ActionMessages errors = new ActionMessages();
			errors.add(
					question.getShortName(),
					new ActionMessage("errors.readonly",
							question.getShortName()));
			saveErrors(request, errors);
			return mapping.findForward(ActionForwards.get_success.toString());
		}
		
		request.setAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES,
				choices);
		GenericActionForm actionForm = (GenericActionForm) form;
		actionForm.setValue("shortName", question.getShortName());
		actionForm.setValue("questionText", question.getQuestionText());
		actionForm.setValue("questionState", Integer.toString(
				question.getQuestionState()));
		
		return mapping.findForward(ActionForwards.edit_success.toString());
	}
	
	public ActionForward preview_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionMessages errors = new ActionMessages();
		try {
			addQuestionValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			errors.add(e.makeActionMessages());
			saveErrors(request, errors);
			return mapping.findForward(ActionForwards.edit_success.toString());
		}
		GenericActionForm actionForm = (GenericActionForm) form;
		request.setAttribute("shortName", actionForm.getValue("shortName"));
		request.setAttribute("questionText",
				actionForm.getValue("questionText"));
		request.setAttribute("status", actionForm.getValue("questionState"));
		
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward update_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		Question question = (Question) request.getSession().getAttribute(
				SurveysConstants.KEY_QUESTION);
		GenericActionForm actionForm = (GenericActionForm) form;
		question.setShortName(actionForm.getValue("shortName"));
		question.setQuestionText(actionForm.getValue("questionText"));
		question.setQuestionState(Integer.parseInt(actionForm.getValue(
				"questionState")));
		
		surveysPersistence.createOrUpdate(question);
		
		return mapping.findForward(ActionForwards.update_success.toString());
	}
	
	

	public ActionForward defineQuestions(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<String> choices = new LinkedList<String>();
		request.getSession().setAttribute(
				SurveysConstants.KEY_NEW_QUESTION_CHOICES, choices);
		LinkedList<Question> newQuestions = new LinkedList<Question>();
		request.getSession().setAttribute(
				SurveysConstants.KEY_NEW_QUESTIONS, newQuestions);
		
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward addChoice(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GenericActionForm actionForm = (GenericActionForm) form;
		LinkedList<String> choices = 
			(LinkedList<String>) request.getSession().getAttribute(
					SurveysConstants.KEY_NEW_QUESTION_CHOICES);
		choices.add(actionForm.getValue("choice"));
		actionForm.setValue("choice", "");
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward deleteChoice(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<String> choices = 
			(LinkedList<String>) request.getSession().getAttribute(
					SurveysConstants.KEY_NEW_QUESTION_CHOICES);
		int index = Integer.parseInt(request.getParameter("choiceNum"));
		choices.remove(index);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward deleteNewQuestion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LinkedList<Question> questions =
			(LinkedList<Question>) request.getSession().getAttribute(
					SurveysConstants.KEY_NEW_QUESTIONS);
		int index = Integer.parseInt(request.getParameter("newQuestionNum"));
		questions.remove(index);
		return mapping.findForward(ActionForwards.load_success.toString());
	}
	
	public ActionForward createQuestions(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Question> questions =
				(List<Question>) request.getSession().getAttribute(
						SurveysConstants.KEY_NEW_QUESTIONS);
		SurveysPersistence persistence = new SurveysPersistence();
		for (Question question : questions) {
			persistence.createOrUpdate(question);
		}
		List<Question> questionList = persistence.retrieveAllQuestions();
		request.setAttribute(SurveysConstants.KEY_QUESTIONS_LIST, questionList);
		request.setAttribute(SurveysConstants.KEY_ITEM_COUNT, questions.size());
		return mapping.findForward(ActionForwards.viewAll_success.toString());
	}
	
	public ActionForward addQuestion(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		GenericActionForm actionForm = (GenericActionForm) form;
		ActionMessages errors = new ActionMessages();
		try {
			addQuestionValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			errors.add(e.makeActionMessages());
		}
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		
		LinkedList<Question> newQuestions = 
			(LinkedList<Question>) request.getSession().getAttribute(
					SurveysConstants.KEY_NEW_QUESTIONS);
		LinkedList<String> questionNames = new LinkedList<String>();
		for (Question q : newQuestions) {
			questionNames.add(q.getShortName());
		}
		String shortName = actionForm.getValue("shortName");
		if (questionNames.contains(shortName))
			errors.add("shortName", new ActionMessage(
					SurveysConstants.NAME_EXISTS));
		else {
				List<Question> retrievedQuestions = 
				surveysPersistence.retrieveQuestionsByName(shortName);
			if (retrievedQuestions.size() > 0)
				errors.add("shortName", new ActionMessage(
						SurveysConstants.NAME_EXISTS));
		}
		if (errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(ActionForwards.load_success.toString());
		}
				
		AnswerType type = AnswerType.fromInt(Integer.parseInt(
				actionForm.getValue("answerType")));
		Question question = new Question(shortName,
				actionForm.getValue("questionText"), type);
		if (type == AnswerType.CHOICE || type == AnswerType.MULTISELECT) {
			List<QuestionChoice> choices = new LinkedList<QuestionChoice>();
			List<String> newQuestionChoices =
				(List<String>) request.getSession().getAttribute(
						SurveysConstants.KEY_NEW_QUESTION_CHOICES);
			for (String choiceText : newQuestionChoices) {
				choices.add(new QuestionChoice(choiceText));
			}
			question.setChoices(choices);
		}
		newQuestions.add(question);
		actionForm.clear();
		request.getSession().setAttribute(
				SurveysConstants.KEY_NEW_QUESTION_CHOICES,
				new LinkedList<Question>());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}
}