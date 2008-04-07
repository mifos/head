package org.mifos.application.ppi.struts.action;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.ppi.helpers.XmlPPISurveyParser;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.formulaic.EnumValidator;
import org.mifos.framework.formulaic.IntValidator;
import org.mifos.framework.formulaic.Schema;
import org.mifos.framework.formulaic.SchemaValidationError;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.struts.actionforms.GenericActionForm;
import org.mifos.framework.util.helpers.PPICalculator;

public class PPIAction extends PersistenceAction {
	
	private Schema validator;
	
	public PPIAction() {
		super();
		validator = new Schema();
		validator.setSimpleValidator("country",
				new EnumValidator(Country.class));
		validator.setSimpleValidator("state",
				new EnumValidator(SurveyState.class));
		validator.setSimpleValidator("veryPoorMin", new IntValidator());
		validator.setSimpleValidator("veryPoorMax", new IntValidator());
		validator.setSimpleValidator("poorMin", new IntValidator());
		validator.setSimpleValidator("poorMax", new IntValidator());
		validator.setSimpleValidator("atRiskMin", new IntValidator());
		validator.setSimpleValidator("atRiskMax", new IntValidator());
		validator.setSimpleValidator("nonPoorMin", new IntValidator());
		validator.setSimpleValidator("nonPoorMax", new IntValidator());
	}
	
	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("ppiAction");
		security.allow("configure", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("get", SecurityConstants.VIEW);
		return security;
	}
	
	public ActionForward configure(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PPIPersistence ppiPersistence = new PPIPersistence();
		PPISurvey activeSurvey = ppiPersistence.retrieveActivePPISurvey();
		
		if (activeSurvey == null) {
			activeSurvey = new PPISurvey();
			activeSurvey.populateDefaultValues();
		}
		
		GenericActionForm actionForm = (GenericActionForm) form;
		
		actionForm.setValue("veryPoorMin", activeSurvey.getVeryPoorMin());
		actionForm.setValue("veryPoorMax", activeSurvey.getVeryPoorMax());
		actionForm.setValue("poorMin", activeSurvey.getPoorMin());
		actionForm.setValue("poorMax", activeSurvey.getPoorMax());
		actionForm.setValue("atRiskMin", activeSurvey.getAtRiskMin());
		actionForm.setValue("atRiskMax", activeSurvey.getAtRiskMax());
		actionForm.setValue("nonPoorMin", activeSurvey.getNonPoorMin());
		actionForm.setValue("nonPoorMax", activeSurvey.getNonPoorMax());
		
		request.setAttribute("countries", Arrays.asList(Country.values()));
		
		return mapping.findForward("configure");
	}
	
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		ActionMessages errors = new ActionMessages();
		try {
			results = validator.validate((GenericActionForm)form);
		} catch (SchemaValidationError e) {
			saveErrors(request, e.makeActionMessages());
			request.setAttribute("countries", Arrays.asList(Country.values()));
			return mapping.findForward("configure");
		}
		
		PPISurvey survey = new PPISurvey();
		
		survey.setAppliesTo(SurveyType.CLIENT);
		
		survey.setCountry((Country)results.get("country"));
		survey.setState((SurveyState)results.get("state"));
		survey.setVeryPoorMin((Integer)results.get("veryPoorMin"));
		survey.setVeryPoorMax((Integer)results.get("veryPoorMax"));
		survey.setPoorMin((Integer)results.get("poorMin"));
		survey.setPoorMax((Integer)results.get("poorMax"));
		survey.setAtRiskMin((Integer)results.get("atRiskMin"));
		survey.setAtRiskMax((Integer)results.get("atRiskMax"));
		survey.setNonPoorMin((Integer)results.get("nonPoorMin"));
		survey.setNonPoorMax((Integer)results.get("nonPoorMax"));
		
		if (!PPICalculator.scoreLimitsAreValid(survey))
			errors.add("limits", new ActionMessage("errors.ppi.invalidlimits"));
		if (errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward("configure");
		}
		
		request.setAttribute("results", results);
		
		return mapping.findForward(ActionForwards.preview_success
				.toString());
	}
	
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> results;
		try {
			results = validator.validate((GenericActionForm)form);
		} catch (SchemaValidationError e) {
			saveErrors(request, e.makeActionMessages());
			return mapping.findForward("configure");
		}
		
		try {
		PPIPersistence ppiPersistence = new PPIPersistence();
		
		PPISurvey ppiSurvey = ppiPersistence.retrievePPISurveyByCountry(
				(Country)results.get("country"));
		
		if (((SurveyState)results.get("state")).equals(SurveyState.ACTIVE)) {
			List<PPISurvey> allPPISurveys = ppiPersistence.retrieveAllPPISurveys();
			for (PPISurvey curSurvey : allPPISurveys)
				curSurvey.setState(SurveyState.INACTIVE);
		}
		
		ppiSurvey = ppiSurvey == null ?
				new PPISurvey((Country)results.get("country")) : ppiSurvey;
		
		XmlPPISurveyParser xmlParser = new XmlPPISurveyParser();
        // TODO: parseInto method parses values from the xml document and overwrites whatever is stored in the database.
        ppiSurvey = xmlParser.parseInto(
				"org/mifos/framework/util/resources/ppi/PPISurvey" + 
				results.get("country") + ".xml", ppiSurvey);

        // TODO: Now it appears that PPI surveys, overwrite applies to no matter what.  This needs to be fixed.
        ppiSurvey.setAppliesTo(SurveyType.CLIENT);
		ppiSurvey.setState((SurveyState)results.get("state"));

        // TODO: Now we seem to be going back to the database for values...????
        ppiSurvey.setVeryPoorMin((Integer)results.get("veryPoorMin"));
		ppiSurvey.setVeryPoorMax((Integer)results.get("veryPoorMax"));
		ppiSurvey.setPoorMin((Integer)results.get("poorMin"));
		ppiSurvey.setPoorMax((Integer)results.get("poorMax"));
		ppiSurvey.setAtRiskMin((Integer)results.get("atRiskMin"));
		ppiSurvey.setAtRiskMax((Integer)results.get("atRiskMax"));
		ppiSurvey.setNonPoorMin((Integer)results.get("nonPoorMin"));
		ppiSurvey.setNonPoorMax((Integer)results.get("nonPoorMax"));
		
		ppiPersistence.createOrUpdate(ppiSurvey);
		
		if (ppiSurvey.getState() == 1) {
			List<PPISurvey> allSurveys = ppiPersistence.retrieveAllPPISurveys();
			for (PPISurvey currentSurvey : allSurveys) {
				if (!currentSurvey.equals(ppiSurvey));
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return mapping.findForward(ActionForwards.update_success.toString());
	}
	
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		PPIPersistence persistence = new PPIPersistence();
		
		PPISurvey survey = persistence.retrieveActivePPISurvey();
		
		if (survey == null) 
			return mapping.findForward(ActionForwards.get_success.toString());
		else {
			request.setAttribute(SurveysConstants.KEY_SURVEY, survey);
			return mapping.findForward(ActionForwards.get_success.toString());
		}
	}
	
	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}


}
