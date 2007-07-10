package org.mifos.application.ppi.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.formulaic.EnumValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.ppi.helpers.Country;

public class PPISurveyAction extends BaseAction {
	
	Schema previewValidator;
	Schema newVersionValidator;
	
	public PPISurveyAction() {
		super();
		previewValidator = new Schema();
		previewValidator.setSimpleValidator("value(country)", new EnumValidator(Country.class));
		previewValidator.setSimpleValidator("value(state)", new EnumValidator(SurveyState.class));
	}
	
	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
	}
	
	public ActionForward configure(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			PPIPersistence ppiPersistence = new PPIPersistence();
		
			PPISurvey activeSurvey = ppiPersistence.retrieveActivePPISurvey();
			
			if (activeSurvey == null) {
				activeSurvey = new PPISurvey();
			}
			
			request.getSession().setAttribute("ppiSurvey", activeSurvey);
		
		return mapping.findForward("configure");
	}
	
	public ActionForward configure_preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			
			
		return mapping.findForward("preview_success");
	}

}
