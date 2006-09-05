package org.mifos.application.personnel.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelLevelEntity;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.struts.actionforms.PersonActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class PersonAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Personnel);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward chooseOffice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.chooseOffice_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonActionForm personActionForm = (PersonActionForm) form;
		SessionUtils
				.setAttribute(PersonnelConstants.OFFICE,
						((PersonnelBusinessService) getService())
								.getOffice(getShortValue(personActionForm
										.getOfficeId())), request);
		personActionForm.clear();
		loadMasterData(request, personActionForm);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		PersonActionForm personActionForm = (PersonActionForm) form;
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		personActionForm.setDateOfJoiningBranch(DateHelper
				.getCurrentDate(userContext.getPereferedLocale()));
		updateRoleLists(request, (PersonActionForm) form);

		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonActionForm personActionForm = (PersonActionForm) form;
		personActionForm.setPersonnelRoles(null);
		return mapping.findForward(ActionForwards.previous_success.toString());
	}
	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		PersonActionForm personActionForm = (PersonActionForm) form;
		PersonnelLevel level = PersonnelLevel
				.getPersonnelLevel(getShortValue(personActionForm.getLevel()));
		OfficeBO office = (OfficeBO) SessionUtils.getAttribute(
				PersonnelConstants.OFFICE, request);
		Integer title = getIntegerValue(personActionForm.getTitle());
		Short perefferedLocale = getLocaleId(getShortValue(personActionForm
				.getPreferredLocale()));
		Date dob = null;
		if (personActionForm.getDob() != null
				&& !personActionForm.getDob().equals(""))
			dob = DateHelper.getDate(personActionForm.getDob());
		Date dateOfJoiningMFI = null;

		if (personActionForm.getDob() != null
				&& !personActionForm.getDob().equals(""))
			dateOfJoiningMFI = DateHelper.getDate(personActionForm
					.getDateOfJoiningMFI());
		PersonnelBO personnelBO = new PersonnelBO(level, office, title,
				perefferedLocale, personActionForm.getUserPassword(),
				personActionForm.getLoginName(), personActionForm.getEmailId(),
				getRoles(request, personActionForm), personActionForm
						.getCustomFields(), personActionForm.getName(),
				personActionForm.getGovernmentIdNumber(), dob,
				getIntegerValue(personActionForm.getMaritalStatus()),
				getIntegerValue(personActionForm.getGender()),
				dateOfJoiningMFI, new Date(), personActionForm.getAddress(),
				userContext.getId());
		personnelBO.save();
		request.setAttribute("displayName", personnelBO.getDisplayName());
		request.setAttribute("globalPersonnelNum", personnelBO
				.getGlobalPersonnelNum());
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	private void updateRoleLists(HttpServletRequest request,
			PersonActionForm personActionForm) throws PageExpiredException {
		if (personActionForm.getPersonnelRoles() != null) {
			List<Role> selectList = new ArrayList<Role>();
			List<Role> masterList = (List<Role>) SessionUtils.getAttribute(
					PersonnelConstants.ROLEMASTERLIST, request);
			for (Role role : masterList) {
				for (String roleId : personActionForm.getPersonnelRoles()) {
					if (role.getId().intValue() == Integer.valueOf(roleId)
							.intValue()) {
						selectList.add(role);
					}
				}
			}
			SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
					selectList, request);
		} else {
			SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
					null, request);
		}
	}

	private void loadMasterData(HttpServletRequest request,
			PersonActionForm personActionForm) throws Exception {
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		MasterPersistence masterPersistence = new MasterPersistence();

		SessionUtils.setAttribute(PersonnelConstants.TITLE_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.PERSONNEL_TITLE, userContext
								.getLocaleId()), request);

		SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST,
				masterPersistence.retrieveMasterEntities(
						PersonnelLevelEntity.class, userContext.getLocaleId()),
				request);

		SessionUtils.setAttribute(PersonnelConstants.GENDER_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.GENDER, userContext.getLocaleId()),
				request);

		SessionUtils.setAttribute(PersonnelConstants.MARITAL_STATUS_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.MARITAL_STATUS, userContext
								.getLocaleId()), request);

		SessionUtils.setAttribute(PersonnelConstants.LANGUAGE_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.LANGUAGE, userContext.getLocaleId()),
				request);

		SessionUtils.setAttribute(PersonnelConstants.ROLES_LIST,
				((PersonnelBusinessService) getService()).getRoles(), request);

		SessionUtils.setAttribute(PersonnelConstants.ROLEMASTERLIST,
				((PersonnelBusinessService) getService()).getRoles(), request);

		List<CustomFieldDefinitionEntity> customFieldDefs = masterPersistence
				.retrieveCustomFieldsDefinition(EntityType.PERSONNEL);

		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request);
		loadCreateCustomFields(personActionForm, customFieldDefs, userContext);

	}

	private void loadCreateCustomFields(PersonActionForm actionForm,
			List<CustomFieldDefinitionEntity> customFieldDefs,
			UserContext userContext) throws SystemException,
			ApplicationException {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						DateHelper.getUserLocaleDate(userContext
								.getPereferedLocale(), fieldDef
								.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}


	private List<Role> getRoles(HttpServletRequest request,
			PersonActionForm personActionForm) throws PageExpiredException {
		List<Role> selectList = new ArrayList<Role>();
		List<Role> masterList = (List<Role>) SessionUtils.getAttribute(
				PersonnelConstants.ROLEMASTERLIST, request);
		for (Role role : masterList) {
			for (String roleId : personActionForm.getPersonnelRoles()) {
				if (role.getId().intValue() == Integer.valueOf(roleId)
						.intValue()) {
					selectList.add(role);
				}
			}
		}
		return selectList;
	}

	private Short getLocaleId(Short lookUpId) throws ServiceException {
		if (lookUpId != null)
			for (SupportedLocalesEntity locale : ((PersonnelBusinessService) getService())
					.getAllLocales()) {
				if (locale.getLanguage().getLookUpId().intValue() == lookUpId
						.intValue())
					return locale.getLocaleId();
				break;
			}
		return Short.valueOf("1");

	}
}
