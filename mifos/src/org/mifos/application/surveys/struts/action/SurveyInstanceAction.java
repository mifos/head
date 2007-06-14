package org.mifos.application.surveys.struts.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.struts.action.ClientCustAction;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyInstanceActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.BaseValidator;
import org.mifos.framework.formulaic.ConstantValidator;
import org.mifos.framework.formulaic.IntValidator;
import org.mifos.framework.formulaic.IsInstanceValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.formulaic.SwitchValidator;
import org.mifos.framework.formulaic.ValidationError;
import org.mifos.framework.formulaic.Validator;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyInstanceAction extends PersistenceAction {
	
	private static SwitchValidator chooseSurveyValidator;
	
	static {
		chooseSurveyValidator = new SwitchValidator("surveyType");
		Schema chooseSurveyClientValidator = new Schema();
		chooseSurveyClientValidator.setValidator("globalCustNum",
				new IsInstanceValidator(String.class));
		chooseSurveyValidator.addCase("client", chooseSurveyClientValidator);
		
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveyInstanceAction");
		security.allow("dummy_create", SecurityConstants.VIEW);
		security.allow("create_entry", SecurityConstants.VIEW);
		security.allow("choosesurvey", SecurityConstants.VIEW);
		return security;
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
		//		return new SurveysBusinessService();
	}
	
	public ActionForward dummy_create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		Survey survey = persistence.getSurvey(2);
		request.getSession().setAttribute(SurveysConstants.KEY_SURVEY, survey);
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	public ActionForward choosesurvey(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		try {
			results = chooseSurveyValidator.validate(request);
		}
		catch (SchemaValidationError e) {
			saveErrors(request, Schema.makeActionErrors(e));
			// errors should never happen here... user enters no data.  so what page should we show?
			// this is mostly for clean testing
			return mapping.findForward(ActionForwards.choose_survey.toString()); 
		}
		
		SurveyInstanceActionForm actionForm = (SurveyInstanceActionForm) form;
		SurveysPersistence persistence = new SurveysPersistence(opener.open());
		SurveyType surveyType = SurveyType.fromString(request.getParameter("surveyType"));
		
		if (surveyType == SurveyType.CLIENT) {
			String globalCustNum = request.getParameter("globalCustNum");
			ClientBO client = (ClientBO) CustomerBusinessService.getInstance().findBySystemId(
					globalCustNum, CustomerLevel.CLIENT.getValue());
			request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME, client.getDisplayName());		
		}
		else {
			throw new NotImplementedException();
		}
		
		List<Survey> surveys = persistence.retrieveSurveysByType(surveyType);
		request.setAttribute(SurveysConstants.KEY_SURVEYS_LIST, surveys);
		return mapping.findForward(ActionForwards.choose_survey.toString());
	}

	public ActionForward create_entry(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyInstanceActionForm actionForm = (SurveyInstanceActionForm) form;
		SurveysPersistence persistence = new SurveysPersistence();
		List<Question> questionsList = persistence.retrieveQuestionsByState(QuestionState.ACTIVE);
		request.getSession().setAttribute(SurveysConstants.KEY_QUESTIONS_LIST,
				questionsList);
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyInstanceActionForm actionForm = (SurveyInstanceActionForm) form;
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyInstanceActionForm actionForm = (SurveyInstanceActionForm) form;
		SurveysPersistence persistence = new SurveysPersistence();
		
		int surveyId = Integer.parseInt(actionForm.getSurveyId());
		Survey survey = persistence.getSurvey(surveyId);

		InstanceStatus status = InstanceStatus.fromInt(Integer
				.parseInt(actionForm.getInstanceStatus()));
		int clientId = Integer.parseInt(actionForm.getCustomerId());
		short officerId = Short.parseShort(actionForm.getOfficerId());
		Date dateConducted = DateUtils.getDateAsSentFromBrowser(actionForm
				.getDateConducted());
		
		ClientBO client = (ClientBO) persistence.getPersistentObject(
				ClientBO.class, clientId);
		PersonnelBO officer = (PersonnelBO) persistence.getPersistentObject(
				PersonnelBO.class, officerId);

		SurveyInstance instance = new SurveyInstance();
		instance.setSurvey(survey);
		instance.setDateConducted(dateConducted);
		instance.setCompletedStatus(status);
		instance.setClient(client);
		instance.setOfficer(officer);
		persistence.createOrUpdate(instance);
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
