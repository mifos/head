package org.mifos.application.surveys.struts.action;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.EnumValidator;
import org.mifos.framework.formulaic.IntValidator;
import org.mifos.framework.formulaic.IsInstanceValidator;
import org.mifos.framework.formulaic.DateComponentValidator;
import org.mifos.framework.formulaic.PersonnelValidator;
import org.mifos.framework.formulaic.NullValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.actionforms.GenericActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyInstanceAction extends BaseAction {
	
	private static Schema chooseSurveyValidator;
	private static Schema createEntryValidator;
	private static Schema previewValidator;
	private static Schema sessionValidator;
	
	static {
		
		chooseSurveyValidator = new Schema();
		chooseSurveyValidator.setSimpleValidator("globalNum",
				new IsInstanceValidator(String.class));
		chooseSurveyValidator.setSimpleValidator("surveyType", new EnumValidator(
				SurveyType.class));
		
		createEntryValidator = new Schema();
		createEntryValidator.setSimpleValidator("value(surveyId)", new IntValidator());
		
		previewValidator = new Schema();
		previewValidator.setComplexValidator("dateSurveyed", new DateComponentValidator());
		previewValidator.setSimpleValidator("value(officerName)", new NullValidator(new PersonnelValidator()));
		
		sessionValidator = new Schema();
		sessionValidator.setSimpleValidator(SurveysConstants.KEY_SURVEY, new IsInstanceValidator(Survey.class));
		sessionValidator.setSimpleValidator(Constants.BUSINESS_KEY, new IsInstanceValidator(BusinessObject.class));
		
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveyInstanceAction");
		security.allow("create_entry", SecurityConstants.VIEW);
		security.allow("create", SecurityConstants.VIEW);
		security.allow("choosesurvey", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		return security;
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
		//		return new SurveysBusinessService();
	}
	
	public static String getBusinessObjectName(BusinessObject businessObject) throws Exception {
		if (ClientBO.class.isInstance(businessObject)) {
			return ((ClientBO)businessObject).getDisplayName();		
		}
		else if (LoanBO.class.isInstance(businessObject)){
			LoanBO loanBO = (LoanBO) businessObject;
			return loanBO.getLoanOffering().getPrdOfferingName()
					+ "# " + loanBO.getGlobalAccountNum();
		}
		else {
			throw new NotImplementedException();
		}
	}
	
	/*
	* Use a combination of {@link #getBusinessObject(SurveyType surveyType, String globalNum)} and
	* {@link #getBusinessObjectName(BusinessObject customer)} instead of this method.
	*/
	public static String getBusinessObjectName(SurveyType surveyType, String globalNum) throws Exception {
		if (surveyType == SurveyType.CLIENT) {
			ClientBO client = (ClientBO) CustomerBusinessService.getInstance().findBySystemId(
					globalNum, CustomerLevel.CLIENT.getValue());
			return client.getDisplayName();		
		}
		else if (surveyType == SurveyType.LOAN){
			LoanBusinessService service = new LoanBusinessService();
			LoanBO loanBO = service.findBySystemId(globalNum);
			return loanBO.getLoanOffering().getPrdOfferingName()
					+ "# " + globalNum;
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
		}
		else if (surveyType == SurveyType.LOAN) {
			LoanBusinessService service = new LoanBusinessService();
			LoanBO loanBO = service.findBySystemId(globalNum);
			return loanBO;
		}
		else {
			throw new NotImplementedException();
		}
	}
	
	public ActionForward choosesurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		try {
			results = chooseSurveyValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			saveErrors(request, Schema.makeActionMessages(e));
			// errors should never happen here... user enters no data.  so what page should we show?
			// this is mostly for clean testing
			return mapping.findForward(ActionForwards.choose_survey.toString()); 
		}
		
		SurveysPersistence persistence = new SurveysPersistence();
		String globalNum = (String) results.get("globalNum");
		SurveyType surveyType = SurveyType.fromString(request.getParameter("surveyType"));
		
		BusinessObject businessObject = getBusinessObject(surveyType, globalNum);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, businessObject);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		List<Survey> surveys = persistence.retrieveSurveysByType(surveyType);
		request.setAttribute(SurveysConstants.KEY_SURVEYS_LIST, surveys);
		return mapping.findForward(ActionForwards.choose_survey.toString());
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
		
		BusinessObject businessObject = (BusinessObject)request.getSession().getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BusinessObject businessObject = (BusinessObject) request.getSession().getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		Map<String, Object> results = null;
		try {
			results = previewValidator.validate(request);
			sessionValidator.validate(request.getSession());
		} catch (SchemaValidationError e) {
			saveErrors(request, Schema.makeActionMessages(e));
			return mapping.findForward(ActionForwards.create_entry_success.toString());
		}
		
		request.setAttribute("dateSurveyed",
				DateUtils.makeDateAsSentFromBrowser(((Date)results.get("dateSurveyed"))));
		
		PersonnelBO officer = (PersonnelBO) results.get("value(officerName)");
		request.setAttribute("officerName", officer == null ? "" : officer.getDisplayName());
		request.getSession().setAttribute("officer", officer);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}
	
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BusinessObject businessObject = (BusinessObject)request.getSession().getAttribute(Constants.BUSINESS_KEY);
		String displayName = getBusinessObjectName(businessObject);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
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
		try {
			results = sessionValidator.validate(request.getSession());
		}
		catch (SchemaValidationError e) {
			return mapping.findForward(ActionForwards.create_entry_success.toString());
		}
		
		GenericActionForm actionForm = (GenericActionForm) form;
		SurveysPersistence persistence = new SurveysPersistence();
		
		Survey survey = (Survey) results.get(SurveysConstants.KEY_SURVEY);
		BusinessObject businessObject = (BusinessObject) results.get(Constants.BUSINESS_KEY);
		
		Date dateConducted = DateUtils.getDateAsSentFromBrowser(actionForm.getDateValue("dateSurveyed"));
		
		PersonnelBO officer = (PersonnelBO) request.getSession().getAttribute("officer");
		
		SurveyInstance instance = new SurveyInstance();
		
		
		
		instance.setSurvey(survey);
		instance.setDateConducted(dateConducted);
		instance.setOfficer(officer);
		if (CustomerBO.class.isInstance(businessObject)) {
			instance.setCustomer((CustomerBO)businessObject);
		} else { // Account
			instance.setAccount((AccountBO)businessObject);
		}
		
		InstanceStatus status = InstanceStatus.COMPLETED;
		List<SurveyResponse> surveyResponses = new ArrayList<SurveyResponse>();
		
		for (Map.Entry<String, String> answerSet : actionForm.getAll("response_").entrySet()) {
			SurveyResponse surveyResponse = new SurveyResponse();
			surveyResponse.setQuestion(survey.getQuestionById(Integer.parseInt(answerSet.getKey())));
			String value = answerSet.getValue();
			surveyResponse.setStringValue(value);
			surveyResponse.setInstance(instance);
			surveyResponses.add(surveyResponse);
			persistence.createOrUpdate(surveyResponse);
			
			if (value == null || value.trim().equals(""))
				status = InstanceStatus.INCOMPLETE;
		}
		
		instance.setSurveyResponses(surveyResponses);
		instance.setCompletedStatus(status);
		persistence.createOrUpdate(instance);
		
		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
