package org.mifos.application.surveys.struts.actionforms;

import org.mifos.application.surveys.SurveysConstants;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.application.surveys.helpers.InstanceStatus;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

public class SurveyInstanceActionForm extends BaseActionForm {
	
	private String surveyId;
	
	private String officerId;
	
	private String officerName;
	
	private String customerId;
	
	private String dateSurveyedDD;
	private String dateSurveyedMM;
	private String dateSurveyedYY;
	
	private String instanceStatus;
	
	private final Map<String, String> responses = new TreeMap<String, String>();

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public String getDateSurveyed() {
		if (StringUtils.isNullOrEmpty(getDateSurveyedDD()) ||
				StringUtils.isNullOrEmpty(getDateSurveyedMM()) ||
				StringUtils.isNullOrEmpty(getDateSurveyedYY()))
			return null;
		return getDateSurveyedDD() + "/" + getDateSurveyedMM() + "/" + getDateSurveyedYY();
	}

	public void setDateSurveyedYY(String dateConductedYY) {
		this.dateSurveyedYY = dateConductedYY;
	}

	public String getDateSurveyedYY() {
		return dateSurveyedYY;
	}

	public void setDateSurveyedMM(String dateConductedMM) {
		this.dateSurveyedMM = dateConductedMM;
	}

	public String getDateSurveyedMM() {
		return dateSurveyedMM;
	}

	public void setDateSurveyedDD(String dateConductedDD) {
		this.dateSurveyedDD = dateConductedDD;
	}

	public String getDateSurveyedDD() {
		return dateSurveyedDD;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOfficerId() {
		return officerId;
	}

	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public String getOfficerName() {
		return officerName;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	
	public void setResponse(String questionId, String value) {
		responses.put(questionId, value);
	}
	
	public String getResponse(String questionId) {
		return responses.get(questionId);
	}
	
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		Integer.toString(InstanceStatus.COMPLETED.getValue());
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals("preview")) {
			validateDate(errors);
			validateResponses(errors);
		}
		
		errors.add(super.validate(mapping, request));
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		
		return errors;
	}
	
	private void validateDate(ActionErrors errors) {
		if (StringUtils.isNullOrEmpty(getDateSurveyed())) {
			errors.add(SurveysConstants.MANDATORY_DATE_SURVEYED,
					new ActionMessage(SurveysConstants.MANDATORY_DATE_SURVEYED));
			Integer.toString(InstanceStatus.INCOMPLETE.getValue());
		} else if (!DateUtils.isValidDate(getDateSurveyed())) {
			errors.add(SurveysConstants.INVALID_DATE_SURVEYED,
					new ActionMessage(SurveysConstants.INVALID_DATE_SURVEYED));
		}
	}
	
	private void validateResponses(ActionErrors errors) {
		for (String fieldName : responses.keySet()) {
			if (StringUtils.isNullOrEmpty(responses.get(fieldName))) {
				//errors.add(SurveysConstants.EMPTY_FIELD,
				//		new ActionMessage(SurveysConstants.EMPTY_FIELD, fieldName));
				instanceStatus = Integer.toString(InstanceStatus.INCOMPLETE.getValue());
				break;
			}
		}
	}

}