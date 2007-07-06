package org.mifos.application.surveys.struts.action;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.EnumValidator;
import org.mifos.framework.formulaic.IntValidator;
import org.mifos.framework.formulaic.MaxLengthValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.actionforms.GenericActionForm;
import org.mifos.framework.util.helpers.Constants;

public class SurveysAction extends BaseAction {
	
	Schema previewValidator;
	Schema newVersionValidator;
	
	public SurveysAction() {
		super();
		previewValidator = new Schema();
		previewValidator.setSimpleValidator("value(name)", new MaxLengthValidator(30));
		previewValidator.setSimpleValidator("value(appliesTo)", new EnumValidator(SurveyType.class));
		previewValidator.setSimpleValidator("value(state)", new EnumValidator(SurveyState.class));
		
		newVersionValidator = new Schema();
		newVersionValidator.setSimpleValidator("value(surveyId)", new IntValidator());
	}

	
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
		security.allow("printVersion", SecurityConstants.VIEW);
		return security;
	}
	
	public ActionForward printVersion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		get(mapping, form, request, response);
		return mapping.findForward("print_success");
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
//		return new SurveysBusinessService();
	}
	
	public ActionForward mainpage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		
		// TODO: change this to aggregate the customers and accounts lists from the subtypes
		List<Survey> clientSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.CLIENT);
		List<Survey> centerSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.CENTER);
		List<Survey> groupSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.GROUP);
		List<Survey> loanSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.LOAN);
		List<Survey> savingsSurveys = surveysPersistence.retrieveSurveysByType(SurveyType.SAVINGS);
		
		request.setAttribute(SurveysConstants.KEY_CLIENT_SURVEYS_LIST, clientSurveys);
		request.setAttribute(SurveysConstants.KEY_CENTER_SURVEYS_LIST, centerSurveys);
		request.setAttribute(SurveysConstants.KEY_GROUP_SURVEYS_LIST, groupSurveys);
		request.setAttribute(SurveysConstants.KEY_LOAN_SURVEYS_LIST, loanSurveys);
		request.setAttribute(SurveysConstants.KEY_SAVINGS_SURVEYS_LIST, savingsSurveys);
		return mapping.findForward(ActionForwards.load_main_page
				.toString());
	}

	
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		GenericActionForm actionForm = (GenericActionForm) form;
		int surveyId = BaseActionForm.getIntegerValue(actionForm.getValue("surveyId"));
		Survey survey = surveysPersistence.getSurvey(surveyId);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, survey);
		request.getSession().setAttribute(SurveysConstants.KEY_ITEM_COUNT, survey.getQuestions().size());
		return mapping.findForward(ActionForwards.get_success.toString());
	}
	
	public List<Question> getRemainingQuestions(List<Question> surveyQuestions) 
		throws PersistenceException {
		List<Question> allQuestions = getQuestions();
		for (Question question : surveyQuestions)
			allQuestions.remove(question);
		return allQuestions;
	}
	
	public ActionForward create_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GenericActionForm actionForm = (GenericActionForm) form;
		actionForm.clear();
			List<Question> questionsList = getQuestions();
		request.getSession().setAttribute(SurveysConstants.KEY_QUESTIONS_LIST,
				questionsList);
		request.getSession().setAttribute(SurveysConstants.KEY_ADDED_QUESTIONS,
				new LinkedList<Question>());
		request.getSession().setAttribute(SurveysConstants.KEY_SURVEY_TYPES, SurveyType.values());
		request.getSession().setAttribute("disable", "false");
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	public ActionForward create_instance_entry(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) {
		
		return mapping.findForward(ActionForwards.create_instance_entry_success.toString());
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		try {
			results = previewValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			saveErrors(request, Schema.makeActionMessages(e));
			return mapping.findForward(ActionForwards.create_entry_success.toString());
		}
		
		GenericActionForm actionForm = (GenericActionForm) form;
		
		request.getSession().setAttribute(
				SurveysConstants.KEY_VALIDATED_VALUES, results);
		List<Question> addedQuestions = (List<Question>) request.getSession()
				.getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		request.getSession().setAttribute(SurveysConstants.KEY_ITEM_COUNT,
				addedQuestions.size());
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward add_new_question(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GenericActionForm actionForm = (GenericActionForm) form;
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		List<Question> questionsList = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_QUESTIONS_LIST);
		List<Question> addedQuestions = (List<Question>)request.getSession().getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		// TODO: insert code for an error message if the question id is invalid here
		try {
			int questionId = Integer.parseInt(actionForm.getValue("newQuestion"));
			Question newQuestion = surveysPersistence.getQuestion(questionId);
			if (newQuestion == null)
				throw new PersistenceException("Question does not exists");
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
		GenericActionForm actionForm = (GenericActionForm) form;
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		int questionNum = Integer.parseInt(actionForm.getValue("questionNum"));
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
		SurveysPersistence persistence = new SurveysPersistence();
		GenericActionForm actionForm = (GenericActionForm) form;
		SurveyState state = Enum.valueOf(SurveyState.class, actionForm.getValue("state"));
		SurveyType type = SurveyType.fromString(actionForm.getValue("appliesTo"));
		Survey newSurvey = new Survey(actionForm.getValue("name"),
				state, type);
		// we have to ensure that all new question choices are created during this
		// transaction, so that they're associated with the same hibernate request
		// that the rest of the survey and questions are
		List<Question> addedQuestions = (List<Question>)request.getSession().getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		for (Question question : addedQuestions) {
			LinkedList<QuestionChoice> choices = new LinkedList<QuestionChoice>();
			if (question.getAnswerTypeAsEnum() == AnswerType.CHOICE) {
				for (QuestionChoice choice : question.getChoices()) {
					choices.add(new QuestionChoice(choice.getChoiceText()));
				}
			}
			question.setChoices(choices);
			boolean mandatory = actionForm.getValue("mandatory_" + question.getQuestionId()) != null;
			newSurvey.addQuestion(question, mandatory);
		}
		persistence.createOrUpdate(newSurvey);
		request.setAttribute(SurveysConstants.KEY_NEW_SURVEY_ID, newSurvey.getSurveyId());
		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	private List<Question> getQuestions() throws PersistenceException {
			SurveysPersistence persistence = new SurveysPersistence();
			return persistence.retrieveQuestionsByState(QuestionState.ACTIVE);
	}
	
	public ActionForward newVersion(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GenericActionForm actionForm = (GenericActionForm) form;
		int surveyId = Integer.parseInt(request.getParameter("value(surveyId)"));
		SurveysPersistence surveysPersistence = new SurveysPersistence();
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

		actionForm.setValue("name", newName);
		actionForm.setValue("appliesTo", survey.getAppliesTo());
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
		request.getSession().setAttribute("edit", true);
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}

}
