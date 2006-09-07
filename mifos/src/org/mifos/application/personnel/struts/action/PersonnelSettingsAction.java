package org.mifos.application.personnel.struts.action;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.struts.actionforms.PersonnelSettingsActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class PersonnelSettingsAction extends BaseAction {
	@Override
	protected BusinessService getService() {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Personnel);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonnelBO personnel = getPersonnel(getStringValue(getUserContext(
				request).getId()));
		setBusinessKey(request, personnel);
		setDetailsData(request, personnel);
		loadMasterData(request);
		setPersonnelAge(request, (Date) personnel.getPersonnelDetails()
				.getDob());
		setFormAttributes((PersonnelSettingsActionForm) form, personnel);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.editPersonalInfo_success
				.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		setUpdatedDetailsData(request,
				getPersonnel(getStringValue(getUserContext(request).getId())),
				(PersonnelSettingsActionForm) form);
		return mapping
				.findForward(PersonnelConstants.PREVIEW_PERSONAL_INFO_SUCCESS);
	}

	@CloseSession
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonnelSettingsActionForm personnelSettingsActionForm = (PersonnelSettingsActionForm) form;
		PersonnelBO personnel = (PersonnelBO) getPersonnel(getStringValue(getUserContext(
				request).getId()));
		personnel.update(personnelSettingsActionForm.getEmailId(),
				personnelSettingsActionForm.getName(),
				personnelSettingsActionForm.getMaritalStatusValue(),
				personnelSettingsActionForm.getGenderValue(),
				personnelSettingsActionForm.getAddress(),
				personnelSettingsActionForm.getPreferredLocaleValue());
		return mapping.findForward(PersonnelConstants.UPDATE_SETTINGS_SUCCESS);
	}

	public ActionForward loadChangePassword(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.loadChangePassword_success
				.toString());
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		if (method.equalsIgnoreCase(Methods.preview.toString())) {
			return mapping.findForward(ActionForwards.editPersonalInfo_failure
					.toString());
		}
		if (method.equalsIgnoreCase(Methods.update.toString())) {
			return mapping
					.findForward(ActionForwards.previewPersonalInfo_success
							.toString());
		}
		return null;
	}

	private PersonnelBO getPersonnel(String personnelId) throws Exception {
		return (((PersonnelBusinessService) getService())
				.getPersonnel(getShortValue(personnelId)));
	}

	private void setPersonnelAge(HttpServletRequest request, Date date) {
		request.getSession().removeAttribute(PersonnelConstants.PERSONNEL_AGE);
		int age = DateHelper.DateDiffInYears(date);
		if (age < 0) {
			age = 0;
		}
		SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_AGE, age,
				request.getSession());
	}

	private String getNameForBusinessActivityEntity(Integer entityId,
			Short localeId) throws Exception {
		if (entityId != null) {
			return ((MasterDataService) ServiceFactory.getInstance()
					.getBusinessService(BusinessServiceName.MasterDataService))
					.retrieveMasterEntities(entityId, localeId);
		}
		return "";
	}

	private void setFormAttributes(PersonnelSettingsActionForm form,
			PersonnelBO personnelBO) {
		form.setFirstName(personnelBO.getPersonnelDetails().getName()
				.getFirstName());
		form.setMiddleName(personnelBO.getPersonnelDetails().getName()
				.getMiddleName());
		form.setSecondLastName(personnelBO.getPersonnelDetails().getName()
				.getSecondLastName());
		form.setLastName(personnelBO.getPersonnelDetails().getName()
				.getLastName());
		form.setGender(getStringValue(personnelBO.getPersonnelDetails()
				.getGender()));
		form.setUserName(personnelBO.getUserName());
		form.setGovernmentIdNumber(personnelBO.getPersonnelDetails()
				.getGovernmentIdNumber());
		form.setAddress(personnelBO.getPersonnelDetails().getAddress());
		form.setDob(personnelBO.getPersonnelDetails().getDob().toString());
		form.setPreferredLocale(getStringValue(personnelBO.getPreferredLocale()
				.getLocaleId()));
		form.setMaritalStatus(getStringValue(personnelBO.getPersonnelDetails()
				.getMaritalStatus()));
	}

	private void loadMasterData(HttpServletRequest request) throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		SessionUtils.setAttribute(PersonnelConstants.GENDER_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.GENDER, getUserContext(request)
								.getLocaleId()), request.getSession());

		SessionUtils.setAttribute(PersonnelConstants.MARITAL_STATUS_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.MARITAL_STATUS, getUserContext(request)
								.getLocaleId()), request.getSession());

		SessionUtils.setAttribute(PersonnelConstants.LANGUAGE_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.LANGUAGE, getUserContext(request)
								.getLocaleId()), request.getSession());
	}

	private void setBusinessKey(HttpServletRequest request,
			PersonnelBO personnel) {
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request
				.getSession());
	}

	private void setDetailsData(HttpServletRequest request,
			PersonnelBO personnel) throws Exception {
		SessionUtils.setAttribute(PersonnelConstants.GENDER,
				getNameForBusinessActivityEntity(personnel
						.getPersonnelDetails().getGender(),
						(getUserContext(request)).getLocaleId()), request
						.getSession());
		SessionUtils.setAttribute(PersonnelConstants.MARITALSTATUS,
				getNameForBusinessActivityEntity(personnel
						.getPersonnelDetails().getMaritalStatus(),
						(getUserContext(request)).getLocaleId()), request
						.getSession());
	}

	private void setUpdatedDetailsData(HttpServletRequest request,
			PersonnelBO personnel, PersonnelSettingsActionForm form)
			throws Exception {
		SessionUtils.setAttribute(PersonnelConstants.GENDER,
				getNameForBusinessActivityEntity(form.getGenderValue(),
						(getUserContext(request)).getLocaleId()), request
						.getSession());
		SessionUtils.setAttribute(PersonnelConstants.MARITALSTATUS,
				getNameForBusinessActivityEntity(form.getMaritalStatusValue(),
						(getUserContext(request)).getLocaleId()), request
						.getSession());
	}

}
