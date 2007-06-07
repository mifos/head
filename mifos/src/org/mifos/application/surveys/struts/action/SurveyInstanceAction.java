package org.mifos.application.surveys.struts.action;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyInstanceActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyInstanceAction extends BaseAction {

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveyInstanceAction");
		security.allow("create_entry", SecurityConstants.VIEW);
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

		// these 5 lines should be the only ones that need to be changed
		// to change implementation (if using new validation sys)
		InstanceStatus status = InstanceStatus.fromInt(Integer
				.parseInt(actionForm.getInstanceStatus()));
		int clientId = Integer.parseInt(actionForm.getCustomerId());
		short officerId = Short.parseShort(actionForm.getOfficerId());
		int surveyId = Integer.parseInt(actionForm.getSurveyId());
		Date dateConducted = DateUtils.getDateAsSentFromBrowser(actionForm
				.getDateConducted());

		Survey survey = persistence.getSurvey(surveyId);
		
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
