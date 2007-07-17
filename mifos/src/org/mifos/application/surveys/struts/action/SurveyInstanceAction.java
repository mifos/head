package org.mifos.application.surveys.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Transaction;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.helpers.PovertyBand;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.DateComponentValidator;
import org.mifos.framework.formulaic.EnumValidator;
import org.mifos.framework.formulaic.ErrorType;
import org.mifos.framework.formulaic.IntValidator;
import org.mifos.framework.formulaic.IsInstanceValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.formulaic.ValidationError;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.actionforms.GenericActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.PPICalculator;
import org.mifos.framework.util.helpers.StringUtils;

public class SurveyInstanceAction extends BaseAction {


	private static Schema chooseSurveyValidator;
	private static Schema createEntryValidator;
	private static Schema previewValidator;
	private static Schema sessionValidator;

	private class SurveyValidator extends Schema {

		private Survey survey;
		
		private final String MULTI_SELECT_EMPTY_STRING = "0";
		
		public SurveyValidator(Survey survey) {
			this.survey = survey;
			DateComponentValidator dateSurveyedValidator = new DateComponentValidator();
			dateSurveyedValidator.setErrorPrefix("errors.dateSurveyed");
			setMapValidator("dateSurveyed", dateSurveyedValidator);
		}

		@Override
		public Map<String, Object> validate(Object objectData)
				throws ValidationError {
			Map<String, Object> data;
			data = super.validate(objectData);
			Map<String, Object> results = new HashMap<String, Object>();
			results.put("dateSurveyed", data.get("dateSurveyed"));

			Map fieldErrors = new HashMap<String, ValidationError>();
			for (SurveyQuestion question : survey.getQuestions()) {
				String formName = "response_"
						+ Integer.toString(question.getSurveyQuestionId());

				String formInput = null;
				if (question.getQuestion().getAnswerTypeAsEnum() == AnswerType.DATE) {
					String dayValue = (String) data.get(formName + "_DD");
					String monthValue = (String) data.get(formName + "_MM");
					String yearValue = (String) data.get(formName + "_YY");
					if (StringUtils.isNullAndEmptySafe(dayValue) &&
							StringUtils.isNullAndEmptySafe(dayValue) &&
							StringUtils.isNullAndEmptySafe(dayValue))
						formInput = dayValue + "/" + monthValue + "/" + yearValue;
				} else if (question.getQuestion().getAnswerTypeAsEnum() == AnswerType.MULTISELECT) {
					formInput = "";
					int size = question.getQuestion().getChoices().size();
					boolean filled = false;
					
					for (int i = 1; i <= size; i++) {
						String value = (String) data.get(formName + "." + i);
						formInput += "," + value;
					
						if (!value.equals(MULTI_SELECT_EMPTY_STRING))
							filled = true;
					}
					
					if (!filled)
						formInput = null;
				} else {
					formInput = (String) data.get(formName);
				}

				if (StringUtils.isNullOrEmpty(formInput)) {
					if (question.getMandatory() == 1) {
						int questionNum = survey.getQuestions().indexOf(question) + 1;
						ActionMessage newMessage = new ActionMessage(getKey(ErrorType.MISSING.toString()), Integer.toString(questionNum));
						fieldErrors.put(formName, new ValidationError(formInput, newMessage));
					}
				} else {
					try {
						question.getQuestion().validate(formInput);
						results.put(formName, formInput);
					} catch (ValidationError e) {
						// find the number of the question in the survey, so we can
						// give a helpful error message
						int questionNum = survey.getQuestions().indexOf(question) + 1;
						ActionMessage newMessage = new ActionMessage(e.getMsg(),
								Integer.toString(questionNum));
						fieldErrors.put(formName, new ValidationError(formInput, newMessage));
					}
				}
			}

			if (fieldErrors.size() > 0) {
				throw new SchemaValidationError(data, fieldErrors);
			}

			return results;
		}
	}

	static {

		chooseSurveyValidator = new Schema();
		chooseSurveyValidator.setSimpleValidator("globalNum",
				new IsInstanceValidator(String.class));
		chooseSurveyValidator.setSimpleValidator("surveyType",
				new EnumValidator(SurveyType.class));

		createEntryValidator = new Schema();
		createEntryValidator.setSimpleValidator("value(surveyId)",
				new IntValidator());

		previewValidator = new Schema();
		previewValidator.setComplexValidator("dateSurveyed",
				new DateComponentValidator());

		sessionValidator = new Schema();
		sessionValidator.setSimpleValidator(SurveysConstants.KEY_SURVEY,
				new IsInstanceValidator(Survey.class));
		sessionValidator.setSimpleValidator(Constants.BUSINESS_KEY,
				new IsInstanceValidator(BusinessObject.class));

	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveyInstanceAction");
		security.allow("create_entry", SecurityConstants.VIEW);
		security.allow("create", SecurityConstants.VIEW);
		security.allow("choosesurvey", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		security.allow("edit", SecurityConstants.VIEW);
		security.allow("delete", SecurityConstants.VIEW);
		security.allow("clear", SecurityConstants.VIEW);
		return security;
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
		//		return new SurveysBusinessService();
	}
	
	public static String getGlobalNum(SurveyInstance instance) {
		SurveyType type = instance.getSurvey().getAppliesToAsEnum();
		if (type == SurveyType.ALL) {
			String globalNum = instance.getCustomer().getGlobalCustNum();
			if (globalNum == null) {
				globalNum = instance.getAccount().getGlobalAccountNum();
			}
			return globalNum;
		}
		else if (type == SurveyType.CLIENT || type == SurveyType.CENTER || type == SurveyType.GROUP) {
			return instance.getCustomer().getGlobalCustNum();
		}
		else {
			return instance.getAccount().getGlobalAccountNum();
		}
	}
	
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		GenericActionForm actionForm = (GenericActionForm) form;
		int instanceId = Integer.parseInt(actionForm.getValue("instanceId"));
		SurveyInstance instance = persistence.getInstance(instanceId);
		if (instance != null) { // if a valid instanceId was provided
			SurveyType type = SurveyType.fromString(actionForm.getValue("surveyType"));
			String redirectUrl = getRedirectUrl(type, getGlobalNum(instance));
			Transaction tx = persistence.getSession().beginTransaction();
			try {
				persistence.delete(instance);
				tx.commit();
			} catch (PersistenceException ex) {
				tx.rollback();
				throw ex;
			}
			response.sendRedirect(redirectUrl);
			return null;
		}
		else {
			response.sendRedirect("/adminAction.do?method=load");
			return null;
		}
	}
	

	// set the business key to the business object for the header link
	// set the survey instance
	// set the business object name, type and global id
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		GenericActionForm actionForm = (GenericActionForm) form;
		
		int instanceId = Integer.parseInt(actionForm.getValue("instanceId"));
		SurveyInstance instance = persistence.getInstance(instanceId);
		SurveyType surveyType = instance.getSurvey().getAppliesToAsEnum();
		List<SurveyResponse> responses = persistence.retrieveResponsesByInstance(instance);
		BusinessObject businessObject;
		String businessObjectName;
		String globalNum;
		
		// determining if an instance is associated with a client/group/center
		// or a loan/savings account could be awkward if the surveytype is ALL
		// this is kinda a hack... pass it in from the referring page... but 
		// probably better than trying to retrieve each from from the db
		if (surveyType == SurveyType.ALL) {
			surveyType = SurveyType.fromString(actionForm.getValue("surveyType"));
		}
		
		globalNum = getGlobalNum(instance);
		businessObjectName = getBusinessObjectName(surveyType, globalNum);
		
		businessObject = getBusinessObject(surveyType, globalNum);
		
		request.setAttribute(SurveysConstants.KEY_GLOBAL_NUM, globalNum);
		request.setAttribute(SurveysConstants.KEY_INSTANCE, instance);
		request.setAttribute(SurveysConstants.KEY_INSTANCE_RESPONSES, responses);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME, businessObjectName);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, businessObject);
		request.getSession().setAttribute(SurveysConstants.KEY_BUSINESS_TYPE, surveyType);
		request.setAttribute(SurveysConstants.KEY_REDIRECT_URL, getRedirectUrl(surveyType, globalNum));
		
		return mapping.findForward(ActionForwards.get_success
				.toString());
	}

	public ActionForward create_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		try {
			results = createEntryValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			// is this the right page to show?
			saveErrors(request, Schema.makeActionMessages(e));
			return mapping.findForward(ActionForwards.choose_survey.toString());
		}

		SurveysPersistence persistence = new SurveysPersistence();
		int surveyId = (Integer) results.get("value(surveyId)");
		Survey survey = persistence.getSurvey(surveyId);
		request.getSession().setAttribute(SurveysConstants.KEY_SURVEY, survey);

		BusinessObject businessObject = (BusinessObject) request.getSession()
				.getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(
				SurveysConstants.KEY_BUSINESS_OBJECT_NAME, displayName);
		if (PPISurvey.class.isInstance(survey))
			return mapping.findForward(ActionForwards.create_entry_success_ppi
					.toString());
		else
			return mapping.findForward(ActionForwards.create_entry_success
				.toString());
	}

	public static String getBusinessObjectName(BusinessObject businessObject)
			throws Exception {
		if (CustomerBO.class.isInstance(businessObject)) {
			return ((CustomerBO) businessObject).getDisplayName();
		}
		
		else if (LoanBO.class.isInstance(businessObject)) {
			LoanBO loanBO = (LoanBO) businessObject;
			return loanBO.getLoanOffering().getPrdOfferingName() + "# "
					+ loanBO.getGlobalAccountNum();
		}
		else {
			throw new NotImplementedException();
		}
	}

	/*
	 * Use a combination of {@link #getBusinessObject(SurveyType surveyType, String globalNum)} and
	 * {@link #getBusinessObjectName(BusinessObject customer)} instead of this method.
	 */
	public static String getBusinessObjectName(SurveyType surveyType,
			String globalNum) throws Exception {
		if (surveyType == SurveyType.CLIENT || surveyType == SurveyType.CENTER
				|| surveyType == SurveyType.GROUP) {
			CustomerBO customer = CustomerBusinessService.getInstance()
					.findBySystemId(globalNum);
			return customer.getDisplayName();
		}
		else if (surveyType == SurveyType.LOAN) {
			LoanBusinessService service = new LoanBusinessService();
			LoanBO loanBO = service.findBySystemId(globalNum);
			return loanBO.getLoanOffering().getPrdOfferingName() + "# "
					+ globalNum;
		}
		else {
			throw new NotImplementedException();
		}
	}

	public static BusinessObject getBusinessObject(SurveyType surveyType,
			String globalNum) throws Exception {
		if (surveyType == SurveyType.CLIENT) {
			ClientBO client = (ClientBO) CustomerBusinessService.getInstance()
					.findBySystemId(globalNum, CustomerLevel.CLIENT.getValue());
			return client;
		} else if (surveyType == SurveyType.CENTER) {
			CenterBO center = (CenterBO) CustomerBusinessService.getInstance()
			.findBySystemId(globalNum, CustomerLevel.CENTER.getValue());
			return center;
		} else if (surveyType == SurveyType.GROUP) {
			GroupBO group = (GroupBO) CustomerBusinessService.getInstance()
			.findBySystemId(globalNum, CustomerLevel.GROUP.getValue());
			return group;
		}
		else if (surveyType == SurveyType.LOAN) {
			LoanBusinessService service = new LoanBusinessService();
			LoanBO loanBO = service.findBySystemId(globalNum);
			return loanBO;
		}
		else if (surveyType == SurveyType.SAVINGS) {
			SavingsBusinessService service = new SavingsBusinessService();
			SavingsBO savingsBO = service.findBySystemId(globalNum);
			return savingsBO;
		}
		else {
			throw new NotImplementedException();
		}
	}

	public ActionForward choosesurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		UserContext userContext = getUserContext(request);
		
		Map<String, Object> results;
		GenericActionForm actionForm = (GenericActionForm) form;
		try {
			results = chooseSurveyValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			saveErrors(request, Schema.makeActionMessages(e));
			// errors should never happen here... user enters no data.  so what page should we show?
			// this is mostly for clean testing
			return mapping.findForward(ActionForwards.choose_survey.toString());
		}
		actionForm.setValue("officerName", userContext.getName());

		SurveysPersistence persistence = new SurveysPersistence();
		String globalNum = (String) results.get("globalNum");
		SurveyType surveyType = SurveyType.fromString(request
				.getParameter("surveyType"));
		request.getSession().setAttribute(SurveysConstants.KEY_BUSINESS_TYPE, surveyType);

		BusinessObject businessObject = getBusinessObject(surveyType, globalNum);
		request.getSession().setAttribute(Constants.BUSINESS_KEY,
				businessObject);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(
				SurveysConstants.KEY_BUSINESS_OBJECT_NAME, displayName);

		List<Survey> surveys = persistence.retrieveSurveysByType(surveyType);
		request.setAttribute(SurveysConstants.KEY_SURVEYS_LIST, surveys);
		return mapping.findForward(ActionForwards.choose_survey.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BusinessObject businessObject = (BusinessObject) request.getSession()
			.getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
			request.setAttribute(
		SurveysConstants.KEY_BUSINESS_OBJECT_NAME, displayName);
		Survey survey = (Survey) request.getSession().getAttribute(
				SurveysConstants.KEY_SURVEY);
		String ppi = "";
		if (PPISurvey.class.isInstance(survey))
			ppi = "_ppi";
		
		Map<String, Object> results = null;
		ActionMessages errors = new ActionMessages();
		GenericActionForm actionForm = (GenericActionForm) form;
		try {
			sessionValidator.validate(request.getSession());
			results = new SurveyValidator(survey).validate(actionForm);
		}
		catch (SchemaValidationError e) {
			errors.add(e.makeActionMessages());
		}
		
		// verify that a real officer username is provided
		String officerName = actionForm.getValue("officerName");
		PersonnelPersistence personnelPersistence = new PersonnelPersistence();
		PersonnelBO officer = personnelPersistence.getPersonnel(officerName);
		if (officer == null) {
			errors.add("value(officerName)", new ActionMessage(SurveysConstants.INVALID_OFFICER));
		}

		if (errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(ActionForwards.create_entry_success
					.toString() + ppi);
		}
		
		SurveyInstance instance = new SurveyInstance();

		instance.setSurvey(survey);
		Set<SurveyResponse> surveyResponses = new TreeSet<SurveyResponse>();
		String prefix = "response_";
		InstanceStatus status = InstanceStatus.COMPLETED;
		
		for (String key : results.keySet()) {
			Object value = results.get(key);
			if (value.equals("") || value == null) {
				status = InstanceStatus.INCOMPLETE;
			} else if (key.startsWith(prefix)){
				SurveyResponse surveyResponse = new SurveyResponse();
				surveyResponse.setSurveyQuestion(survey.getSurveyQuestionById(Integer
						.parseInt(key.substring(prefix.length()))));
				String stringValue = (String) results.get(key);
				surveyResponse.setStringValue(stringValue);
				surveyResponse.setInstance(instance);
				surveyResponse.setResponseId(surveyResponse.getQuestion().getQuestionId());
				surveyResponses.add(surveyResponse);
			}
		}
		
		instance.setSurveyResponses(surveyResponses);
		
		if (PPISurvey.class.isInstance(survey)) {
			request.setAttribute("povertyBand",
					PovertyBand.fromInt(PPICalculator.calculateScore(instance), (PPISurvey)survey));
		}
		
		actionForm.setValue("instanceStatus", Integer.toString(status
				.getValue()));
		
		request.setAttribute("surveyInstance", instance);
		request.setAttribute("dateSurveyed", actionForm
				.getDateValue("dateSurveyed"));
		request.setAttribute("officerName", actionForm.getValue("officerName"));
		
		
		return mapping.findForward(ActionForwards.preview_success.toString() + ppi);
	}
	
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		GenericActionForm actionForm = (GenericActionForm) form;
		
		actionForm.clear();
		actionForm.setValue("officerName", getUserContext(request).getName());
		BusinessObject businessObject = (BusinessObject)request.getSession().getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		Survey survey = (Survey) request.getSession().getAttribute(
				SurveysConstants.KEY_SURVEY);
		if (PPISurvey.class.isInstance(survey))
			return mapping.findForward(ActionForwards.create_entry_success_ppi.toString());
		else
			return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BusinessObject businessObject = (BusinessObject)request.getSession().getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		Survey survey = (Survey) request.getSession().getAttribute(
				SurveysConstants.KEY_SURVEY);
		if (PPISurvey.class.isInstance(survey))
			return mapping.findForward(ActionForwards.create_entry_success_ppi.toString());
		else
			return mapping.findForward(ActionForwards.create_entry_success.toString());
	}

	/*
	 * This page is the page where we actually create a new survey instance, after
	 * optionally filling in responses
	 */
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		Map<String, Object> formInputs;
		Survey survey;
		GenericActionForm actionForm;
		try {
			results = sessionValidator.validate(request.getSession());
			survey = (Survey) results.get(SurveysConstants.KEY_SURVEY);
			actionForm = (GenericActionForm) form;
			formInputs = new SurveyValidator(survey).validate(actionForm);
		}
		catch (SchemaValidationError e) {
			return mapping.findForward(ActionForwards.create_entry_success
					.toString());
		}

		SurveysPersistence persistence = new SurveysPersistence();
		PersonnelPersistence personnelPersistence = new org.mifos.application.personnel.persistence.PersonnelPersistence();

		BusinessObject businessObject = (BusinessObject) results
				.get(Constants.BUSINESS_KEY);

		// partially completed instances not supported yet
		InstanceStatus status = InstanceStatus.COMPLETED; 
		String officerName = actionForm.getValue("officerName");
		Date dateConducted = DateUtils.getDateAsSentFromBrowser(actionForm
				.getDateValue("dateSurveyed"));

		PersonnelBO officer = personnelPersistence.getPersonnel(officerName);

		SurveyInstance instance = new SurveyInstance();

		instance.setSurvey(survey);
		instance.setDateConducted(dateConducted);
		instance.setCompletedStatus(status);
		instance.setOfficer(officer);
		
		
		UserContext userContext = getUserContext(request);
		PersonnelBO currentUser = personnelPersistence.getPersonnel(userContext.getName());
		instance.setCreator(currentUser);
		
		if (CustomerBO.class.isInstance(businessObject)) {
			instance.setCustomer((CustomerBO) businessObject);
		}
		else { // Account
			instance.setAccount((AccountBO) businessObject);
		}

		List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
		List<String> responseKeys = new LinkedList<String>();
		String prefix = "response_";
		for (String key : formInputs.keySet()) {
			if (key.startsWith(prefix)) {
				responseKeys.add(key);
			}
		}
		Collections.sort(responseKeys);
		for (String key : responseKeys) {
			SurveyResponse surveyResponse = new SurveyResponse();
			surveyResponse.setSurveyQuestion(survey.getSurveyQuestionById(Integer
					.parseInt(key.substring(prefix.length()))));
			String stringValue = (String) formInputs.get(key);
			surveyResponse.setStringValue(stringValue);
			surveyResponse.setInstance(instance);
			surveyResponses.add(surveyResponse);
			persistence.createOrUpdate(surveyResponse);
		}
		
		persistence.createOrUpdate(instance);
		
		SurveyType businessType = (SurveyType) request.getSession()
				.getAttribute(SurveysConstants.KEY_BUSINESS_TYPE);
		String redirectUrl = getRedirectUrl(businessType, getGlobalNum(instance));
		response.sendRedirect(redirectUrl);
		return null;
	}
	
	public static String getRedirectUrl(SurveyType type, String globalNum) {
		if (type == SurveyType.CLIENT) {
			return "clientCustAction.do?method=get&globalCustNum=" + globalNum;
		}
		else if (type == SurveyType.GROUP) {
			return "groupCustAction.do?method=get&globalCustNum=" + globalNum;
		}
		
		else if (type == SurveyType.CENTER) {
			return "centerCustAction.do?method=get&globalCustNum=" + globalNum;
		}
		
		else if (type == SurveyType.LOAN) {
			return "loanAccountAction.do?method=get&globalCustNum=" + globalNum;
		}
		else {
			throw new NotImplementedException();
		}
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
