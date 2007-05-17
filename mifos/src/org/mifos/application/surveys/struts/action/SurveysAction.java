package org.mifos.application.surveys.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.SessionOpener;
import org.mifos.framework.persistence.ThreadLocalOpener;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;

public class SurveysAction extends BaseAction {

	private SessionOpener opener;

	public SurveysAction() {
		this(new ThreadLocalOpener());
	}

	public SurveysAction(SessionOpener opener) {
		this.opener = opener;
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("surveysAction");
		security.allow("mainpage", SecurityConstants.VIEW);
		security.allow("load", SecurityConstants.VIEW);
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
		holder.close();
		return mapping.findForward(ActionForwards.load_main_page
				.toString());
	}
	
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionHolder holder = opener.open();
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		SurveyActionForm actionForm = (SurveyActionForm) form;
		int surveyId = actionForm.getSurveyIdValue();
		Survey survey = surveysPersistence.getSurvey(surveyId);
		request.getSession().setAttribute(Constants.BUSINESS_KEY, survey);
		holder.close();
		return mapping.findForward(ActionForwards.get_success.toString());
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

}
