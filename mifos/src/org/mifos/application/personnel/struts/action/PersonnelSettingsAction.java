package org.mifos.application.personnel.struts.action;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.business.SupportedLocalesEntity;
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
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

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

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonnelBO personnel = getPersonnel(getStringValue(getUserContext(
				request).getId()));
		SessionUtils.removeAttribute(Constants.BUSINESS_KEY,request);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);
		setDetailsData(request, getUserContext(request).getLocaleId(),
				personnel.getPersonnelDetails().getGender(), personnel
						.getPersonnelDetails().getMaritalStatus(),Integer.valueOf(personnel.getPreferredLocale().getLocaleId().intValue()));
		loadMasterData(request, getUserContext(request).getLocaleId());
		setPersonnelAge(request, (Date) personnel.getPersonnelDetails()
				.getDob());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
		setFormAttributes((PersonnelSettingsActionForm) form, personnel);
		return mapping.findForward(ActionForwards.manage_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonnelSettingsActionForm personnelactionForm = (PersonnelSettingsActionForm) form;
		Integer prefeeredLocaleId = null;
		if(personnelactionForm.getPreferredLocaleValue() != null)
			prefeeredLocaleId = Integer.valueOf(personnelactionForm.getPreferredLocaleValue().intValue());
		setDetailsData(request,getUserContext(request).getLocaleId(),personnelactionForm.getGenderValue(),personnelactionForm.getMaritalStatusValue(),prefeeredLocaleId);
		return mapping.findForward(ActionForwards.preview_success
				.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success
				.toString());
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonnelSettingsActionForm personnelSettingsActionForm = (PersonnelSettingsActionForm) form;
		PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
		personnel.update(personnelSettingsActionForm.getEmailId(),
				personnelSettingsActionForm.getName(),
				personnelSettingsActionForm.getMaritalStatusValue(),
				personnelSettingsActionForm.getGenderValue(),
				personnelSettingsActionForm.getAddress(),
				getLocaleId(personnelSettingsActionForm
						.getPreferredLocaleValue()),getUserContext(request).getId());
		return mapping.findForward(ActionForwards.updateSettings_success.toString());
	}
	
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}
	
	@TransactionDemarcate(joinToken = true)
	public ActionForward loadChangePassword(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return mapping.findForward(ActionForwards.loadChangePassword_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
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

	private void setPersonnelAge(HttpServletRequest request, Date date)
			throws PageExpiredException {
		SessionUtils.removeAttribute(PersonnelConstants.PERSONNEL_AGE, request);
		int age = DateHelper.DateDiffInYears(date);
		if (age < 0) {
			age = 0;
		}
		SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_AGE, age,
				request);
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
			PersonnelBO personnelBO) throws Exception {
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
		form.setEmailId(personnelBO.getEmailId());
		form.setGovernmentIdNumber(personnelBO.getPersonnelDetails()
				.getGovernmentIdNumber());
		form.setAddress(personnelBO.getPersonnelDetails().getAddress());
		form.setDob(personnelBO.getPersonnelDetails().getDob().toString());
		form.setPreferredLocale(getStringValue(personnelBO.getPreferredLocale().getLocaleId()));
		form.setMaritalStatus(getStringValue(personnelBO.getPersonnelDetails()
				.getMaritalStatus()));
	}

	private void loadMasterData(HttpServletRequest request, Short localeId)
			throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.GENDER, localeId), request);

		SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.MARITAL_STATUS, localeId), request);

		SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.LANGUAGE, localeId), request);
	}

	private void setDetailsData(HttpServletRequest request, Short localeId,
			Integer gender, Integer maritalStatus, Integer preferredLocale) throws Exception {
		SessionUtils.setAttribute(PersonnelConstants.GENDER,
				getNameForBusinessActivityEntity(gender, localeId), request);
		SessionUtils.setAttribute(PersonnelConstants.MARITALSTATUS,
				getNameForBusinessActivityEntity(maritalStatus, localeId),
				request);
		SessionUtils.setAttribute(MasterConstants.LANGUAGE,
				getNameForBusinessActivityEntity(preferredLocale, localeId), request);
	}

	private Short getLocaleId(Short lookUpId) throws ServiceException {
		if (lookUpId != null)
			for (SupportedLocalesEntity locale : ((PersonnelBusinessService) getService())
					.getAllLocales()) {
				if (locale.getLanguage().getLookUpValue().getLookUpId().intValue() == lookUpId
						.intValue())
					return locale.getLocaleId();
				break;
			}
		return Short.valueOf("1");
	}
}
