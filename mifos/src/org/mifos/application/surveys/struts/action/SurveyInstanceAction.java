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
import org.mifos.application.surveys.business.Survey;
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
import org.mifos.framework.struts.action.PersistenceAction;


public class SurveyInstanceAction extends PersistenceAction {
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveyInstanceAction");
		security.allow("create_entry", SecurityConstants.VIEW);
		return security;
	}
	
	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
//		return new SurveysBusinessService();
	}
	
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SurveyInstanceActionForm actionForm = (SurveyInstanceActionForm) form;
		SessionHolder holder = opener.open();
		SurveysPersistence persistence = new SurveysPersistence(holder);
		Survey survey = persistence.retrieveAllSurveys().get(0);

		return mapping.findForward(ActionForwards.create_success.toString());
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
