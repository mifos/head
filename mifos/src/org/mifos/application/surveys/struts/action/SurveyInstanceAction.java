package org.mifos.application.surveys.struts.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyInstanceActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.EnumValidator;
import org.mifos.framework.formulaic.IntValidator;
import org.mifos.framework.formulaic.IsInstanceValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyInstanceAction extends PersistenceAction {
	
	private static Schema chooseSurveyValidator;
	
	private static Schema createEntryValidator;
	
	static {
		
		chooseSurveyValidator = new Schema();
		chooseSurveyValidator.setValidator("globalNum",
				new IsInstanceValidator(String.class));
		chooseSurveyValidator.setValidator("surveyType", new EnumValidator(
				SurveyType.class));
		
		createEntryValidator = new Schema();
		createEntryValidator.setValidator("value(surveyId)", new IntValidator());
		createEntryValidator.setValidator("value(globalNum)",
				new IsInstanceValidator(String.class));
		
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
		request.setAttribute(SurveysConstants.KEY_SURVEY, survey);
		String globalNum = (String) results.get("value(globalNum)");
		String displayName = getBusinessObjectName(survey.getAppliesToAsEnum(), globalNum);
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		return mapping.findForward(ActionForwards.create_entry_success.toString());
	}
	
	private String getBusinessObjectName(SurveyType surveyType, String globalNum) throws Exception {
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
		
		SurveysPersistence persistence = new SurveysPersistence(opener.open());
		SurveyType surveyType = SurveyType.fromString(request.getParameter("surveyType"));
		
		String displayName = getBusinessObjectName(surveyType, (String) results
				.get("globalNum"));
		request.setAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME,
				displayName);
		
		List<Survey> surveys = persistence.retrieveSurveysByType(surveyType);
		request.setAttribute(SurveysConstants.KEY_SURVEYS_LIST, surveys);
		return mapping.findForward(ActionForwards.choose_survey.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// do validation here
//		GenericActionForm actionForm = (GenericActionForm) form;
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	/*
	 * This page is the page where we actually create a new survey instance, after
	 * optionally filling in responses
	 */
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyInstanceActionForm actionForm = (SurveyInstanceActionForm) form;
		SurveysPersistence persistence = new SurveysPersistence();
		
		//int surveyId = Integer.parseInt(actionForm.getSurveyId());
		Survey survey = (Survey) request.getSession().getAttribute(SurveysConstants.KEY_SURVEY);

		InstanceStatus status = InstanceStatus.fromInt(Integer
				.parseInt(actionForm.getInstanceStatus()));
		int clientId = Integer.parseInt(actionForm.getCustomerId());
		short officerId = Short.parseShort(actionForm.getOfficerId());
		Date dateConducted = DateUtils.getDate(actionForm
				.getDateSurveyed());
		List<SurveyResponse> surveyResponses = actionForm.getResponseList();
		
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
		instance.setSurveyResponses(surveyResponses);
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
