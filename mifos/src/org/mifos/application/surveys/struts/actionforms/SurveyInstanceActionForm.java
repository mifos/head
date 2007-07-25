package org.mifos.application.surveys.struts.actionforms;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class SurveyInstanceActionForm extends BaseActionForm {
	
	private String surveyId;
	
	private String officerId;
	
	private String officerName;
	
	private String customerId;
	
	private String dateSurveyedDD;
	private String dateSurveyedMM;
	private String dateSurveyedYY;
	
	private String instanceStatus;
	
	private final Map<String, SurveyResponse> responses = new TreeMap<String, SurveyResponse>();
	
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
		if (officerId != null)
			return officerId;
		else {
			try {
				PersonnelPersistence personnelPersistence = new PersonnelPersistence();
				return Short.toString(personnelPersistence.getPersonnelByUserName(officerName).getPersonnelId());
			} catch (PersistenceException e) {
				return "-1";
			}
		}
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
	
	public void setResponse(String questionId, String value) throws ApplicationException {
		if (StringUtils.isNullOrEmpty(value)) {
			responses.put(questionId, null);
		} else {
			SurveysPersistence persistence = new SurveysPersistence();
			Question question = persistence.getQuestion(Integer.parseInt(questionId));
			SurveyResponse response = new SurveyResponse();
			response.setQuestion(question);
			response.setStringValue(value);
			
			responses.put(questionId, response);
		}
	}
	
	public String getResponse(String questionId) {
		SurveyResponse response = responses.get(questionId);
		return response == null ? null : response.toString();
	}
	
	public List<SurveyResponse> getResponseList() {
		List<SurveyResponse> list = new LinkedList<SurveyResponse>();
		list.addAll(responses.values());
		return list;
	}
	
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		instanceStatus = Integer.toString(InstanceStatus.COMPLETED.getValue());
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
			if (responses.get(fieldName) == null) {
				//errors.add(SurveysConstants.EMPTY_FIELD,
				//		new ActionMessage(SurveysConstants.EMPTY_FIELD, fieldName));
				instanceStatus = Integer.toString(InstanceStatus.INCOMPLETE.getValue());
				break;
			}
		}
	}

}