package org.mifos.application.personnel.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelCustomFieldEntity;
import org.mifos.application.personnel.business.PersonnelDetailsEntity;
import org.mifos.application.personnel.business.PersonnelLevelEntity;
import org.mifos.application.personnel.business.PersonnelRoleEntity;
import org.mifos.application.personnel.business.PersonnelStatusEntity;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.struts.actionforms.PersonActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class PersonAction extends SearchAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Personnel);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	private PersonnelBusinessService getPersonnelBusinessService() throws ServiceException{
		return (PersonnelBusinessService)getService();
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
		OfficeBO office = ((PersonnelBusinessService) getService())
				.getOffice(getShortValue(personActionForm.getOfficeId()));
		SessionUtils.setAttribute(PersonnelConstants.OFFICE, office, request);
		personActionForm.clear();
		loadCreateMasterData(request, personActionForm);
		if (office.getOfficeLevel() != OfficeLevel.BRANCHOFFICE)
			updatePersonnelLevelList(request);
		personActionForm.setDateOfJoiningMFI(DateHelper
				.getCurrentDate(getUserLocale(request)));
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
				.getCurrentDate(userContext.getPreferredLocale()));
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

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext userContext = getUserContext(request);
		PersonActionForm personActionForm = (PersonActionForm) form;
		PersonnelLevel level = PersonnelLevel
				.fromInt(getShortValue(personActionForm.getLevel()));
		OfficeBO office = (OfficeBO) SessionUtils.getAttribute(
				PersonnelConstants.OFFICE, request);
		Integer title = getIntegerValue(personActionForm.getTitle());
		Short perefferedLocale =getPerefferedLocale(personActionForm,userContext);
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

	private Short getPerefferedLocale(PersonActionForm personActionForm,UserContext userContext)
	{
		if (StringUtils.isNullAndEmptySafe(personActionForm
				.getPreferredLocale()))
			return  getShortValue(personActionForm
				.getPreferredLocale());
		else
			return userContext.getMfiLocaleId();

	}
	@TransactionDemarcate(joinToken = true)
	public ActionForward manage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonActionForm actionform = (PersonActionForm) form;
		actionform.clear();
		PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		PersonnelBO personnelBO = ((PersonnelBusinessService) getService())
				.getPersonnel(personnel.getPersonnelId());
		personnel = null;
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnelBO, request);
		loadUpdateMasterData(request, actionform);
		setValuesInActionForm(actionform, request);
		return mapping.findForward(ActionForwards.manage_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previewManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonActionForm actionForm = (PersonActionForm) form;
		updateRoleLists(request, actionForm);
		return mapping.findForward(ActionForwards.previewManage_success
				.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previousManage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonActionForm personActionForm = (PersonActionForm) form;
		personActionForm.setPersonnelRoles(null);
		return mapping.findForward(ActionForwards.previousManage_success
				.toString());
	}

	@CloseSession
	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserContext userContext = getUserContext(request);
		PersonActionForm actionForm = (PersonActionForm) form;
		PersonnelLevel level = PersonnelLevel
				.fromInt(getShortValue(actionForm.getLevel()));
		PersonnelStatus personnelStatus = PersonnelStatus
				.getPersonnelStatus(getShortValue(actionForm.getStatus()));
		OfficeBusinessService officeService = (OfficeBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Office);
		OfficeBO office = officeService.getOffice(getShortValue(actionForm
				.getOfficeId()));
		Integer title = getIntegerValue(actionForm.getTitle());
		Short perefferedLocale = getPerefferedLocale(actionForm,userContext);

		PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);

		PersonnelBO personnelInit = ((PersonnelBusinessService) getService())
				.getPersonnel(Short.valueOf(actionForm.getPersonnelId()));
		checkVersionMismatch(personnel.getVersionNo(),personnelInit.getVersionNo());
		personnelInit.setVersionNo(personnel.getVersionNo());
		personnelInit.setUserContext(getUserContext(request));
		setInitialObjectForAuditLogging(personnelInit);

		personnelInit.update(personnelStatus, level, office, title,
				perefferedLocale, actionForm.getUserPassword(), actionForm
						.getEmailId(), getRoles(request, actionForm),
				actionForm.getCustomFields(), actionForm.getName(),
				getIntegerValue(actionForm.getMaritalStatus()),
				getIntegerValue(actionForm.getGender()), actionForm
						.getAddress(), userContext.getId());
		request.setAttribute("displayName", personnelInit.getDisplayName());
		request.setAttribute("globalPersonnelNum", personnelInit
				.getGlobalPersonnelNum());
		return mapping.findForward(ActionForwards.update_success.toString());
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
		String forward = null;
		PersonActionForm actionForm = (PersonActionForm) form;
		String fromPage = actionForm.getInput();
		if (fromPage.equals(PersonnelConstants.MANAGE_USER)
				|| fromPage.equals(PersonnelConstants.UNLOCK_USER)) {
			forward = ActionForwards.cancelEdit_success.toString();
		} else {
			forward = ActionForwards.cancel_success.toString();
		}
		return mapping.findForward(forward.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PersonActionForm personActionForm = (PersonActionForm) form;
		PersonnelBO personnel = ((PersonnelBusinessService) getService())
				.getPersonnelByGlobalPersonnelNum(personActionForm
						.getGlobalPersonnelNum());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		personnel.getStatus().setLocaleId(userContext.getLocaleId());
		loadCreateMasterData(request, personActionForm);
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward loadUnLockUser(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SessionUtils.setAttribute(PersonnelConstants.LOGIN_ATTEMPTS_COUNT,
				LoginConstants.MAXTRIES, request);
		return mapping.findForward(ActionForwards.loadUnLockUser_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward unLockUserAccount(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		personnel.unlockPersonnel(userContext.getId());
		return mapping.findForward(ActionForwards.unLockUserAccount_success
				.toString());
	}

	@Override
	@TransactionDemarcate(saveToken = true)
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward actionForward = null;
		UserContext userContext = getUserContext(request);
		PersonnelBusinessService personnelBusinessService=getPersonnelBusinessService();
		PersonnelBO personnel = personnelBusinessService.getPersonnel(userContext.getId());
		String searchString = ((PersonActionForm) form).getSearchString();
		addSeachValues(searchString,personnel.getOffice().getOfficeId().toString(),personnel.getOffice().getOfficeName(),request);
		searchString= StringUtils.normalizeSearchString(searchString);
		actionForward=super.search(mapping, form, request, response);
		SessionUtils.setQueryResultAttribute(Constants.SEARCH_RESULTS,
				new PersonnelPersistence().search(searchString, userContext.getId()), request);
		return actionForward;
	}
	@TransactionDemarcate(saveToken = true)
	public ActionForward loadSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		cleanUpOnLoadSearch(request);
		return mapping.findForward(ActionForwards.search_success.toString());
	}
	
	private void cleanUpOnLoadSearch(HttpServletRequest request) throws PageExpiredException {
		SessionUtils.setRemovableAttribute("TableCache",null,TableTagConstants.PATH,request.getSession());
		SessionUtils.removeAttribute(Constants.SEARCH_RESULTS,request);
	}

	private void loadMasterData(HttpServletRequest request,
			PersonActionForm personActionForm) throws Exception {
		UserContext userContext = getUserContext(request);
		MasterPersistence masterPersistence = new MasterPersistence();

		SessionUtils.setCollectionAttribute(PersonnelConstants.TITLE_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.PERSONNEL_TITLE, userContext
								.getLocaleId()), request);

		SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST,
				masterPersistence.retrieveMasterEntities(
						PersonnelLevelEntity.class, userContext.getLocaleId()),
				request);

		SessionUtils.setCollectionAttribute(PersonnelConstants.GENDER_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.GENDER, userContext.getLocaleId()),
				request);

		SessionUtils.setCollectionAttribute(PersonnelConstants.MARITAL_STATUS_LIST,
				masterPersistence.retrieveMasterEntities(
						MasterConstants.MARITAL_STATUS, userContext
								.getLocaleId()), request);
		loadLanguageList(request);
		SessionUtils.setCollectionAttribute(PersonnelConstants.ROLES_LIST,
				((PersonnelBusinessService) getService()).getRoles(), request);

		SessionUtils.setCollectionAttribute(PersonnelConstants.ROLEMASTERLIST,
				((PersonnelBusinessService) getService()).getRoles(), request);

		List<CustomFieldDefinitionEntity> customFieldDefs = masterPersistence
				.retrieveCustomFieldsDefinition(EntityType.PERSONNEL);
		SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request);
	}

	private void loadLanguageList(HttpServletRequest request)throws Exception{
		
		List <SupportedLocalesEntity> locales= getPersonnelBusinessService().getSupportedLocales();
		UserContext userContext = getUserContext(request);
		for (SupportedLocalesEntity entity : locales) {
			entity.getLanguage().setLocaleId(userContext.getLocaleId());
		}
		SessionUtils.setCollectionAttribute(PersonnelConstants.LANGUAGE_LIST,locales,request);
	}
	private void updatePersonnelLevelList(HttpServletRequest request)
			throws PageExpiredException {
		List<MasterDataEntity> levelList = (List<MasterDataEntity>) SessionUtils
				.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request);
		for (MasterDataEntity level : levelList) {
			if (level.getId().equals(PersonnelLevel.LOAN_OFFICER.getValue())) {
				levelList.remove(level);
				break;
			}
		}

	}

	private void loadCreateMasterData(HttpServletRequest request,
			PersonActionForm personActionForm) throws Exception {
		loadMasterData(request, personActionForm);
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		loadCreateCustomFields(personActionForm, customFieldDefs, getUserContext(request));
	}

	private void loadUpdateMasterData(HttpServletRequest request,
			PersonActionForm personActionForm) throws Exception {
		loadMasterData(request, personActionForm);
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		SessionUtils.setCollectionAttribute(PersonnelConstants.STATUS_LIST,
				getMasterEntities(PersonnelStatusEntity.class, getUserContext(
						request).getLocaleId()), request);
		OfficeBusinessService officeService = (OfficeBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Office);
		OfficeBO loggedInOffice = officeService.getOffice(userContext
				.getBranchId());
		SessionUtils.setCollectionAttribute(PersonnelConstants.OFFICE_LIST, officeService
				.getChildOffices(loggedInOffice.getSearchId()), request);
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
								.getPreferredLocale(), fieldDef
								.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}

	private void setValuesInActionForm(PersonActionForm actionForm,
			HttpServletRequest request) throws Exception {
		PersonnelBO personnel = (PersonnelBO) SessionUtils.getAttribute(
				Constants.BUSINESS_KEY, request);
		actionForm.setPersonnelId(personnel.getPersonnelId().toString());
		if (personnel.getOffice() != null)
			actionForm.setOfficeId(personnel.getOffice().getOfficeId()
					.toString());
		if (personnel.getTitle() != null)
			actionForm.setTitle(personnel.getTitle().toString());
		if (personnel.getLevel() != null)
			actionForm.setLevel(personnel.getLevelEnum().getValue().toString());
		if (personnel.getStatus() != null)
			actionForm.setStatus(personnel.getStatus().getId().toString());
		actionForm.setLoginName(personnel.getUserName());
		actionForm.setGlobalPersonnelNum(personnel.getGlobalPersonnelNum());
		actionForm.setCustomFields(createCustomFieldViews(personnel
				.getCustomFields(), request));

		if (personnel.getPersonnelDetails() != null) {
			PersonnelDetailsEntity personnelDetails = personnel
					.getPersonnelDetails();
			actionForm.setFirstName(personnelDetails.getName().getFirstName());
			actionForm
					.setMiddleName(personnelDetails.getName().getMiddleName());
			actionForm.setSecondLastName(personnelDetails.getName()
					.getSecondLastName());
			actionForm.setLastName(personnelDetails.getName().getLastName());
			if (personnelDetails.getGender() != null)
				actionForm.setGender(personnelDetails.getGender().toString());
			if (personnelDetails.getMaritalStatus() != null)
				actionForm.setMaritalStatus(personnelDetails.getMaritalStatus()
						.toString());
			actionForm.setAddress(personnelDetails.getAddress());
			if (personnelDetails.getDateOfJoiningMFI() != null) {
				actionForm.setDateOfJoiningMFI(DateHelper.getUserLocaleDate(
						getUserContext(request).getPreferredLocale(),
						personnelDetails.getDateOfJoiningMFI().toString()));
			}
			if (personnelDetails.getDob() != null) {
				actionForm.setDob(DateHelper.getUserLocaleDate(getUserContext(
						request).getPreferredLocale(), personnelDetails
						.getDob().toString()));
			}

		}
		actionForm.setEmailId(personnel.getEmailId());
		if (personnel.getPreferredLocale() != null)actionForm.setPreferredLocale(getStringValue(personnel.getPreferredLocale().getLocaleId()));
		List<RoleBO> selectList = new ArrayList<RoleBO>();
		for (PersonnelRoleEntity personnelRole : personnel.getPersonnelRoles()) {
			selectList.add(personnelRole.getRole());
		}
		SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
				selectList, request);
	}

	private List<CustomFieldView> createCustomFieldViews(
			Set<PersonnelCustomFieldEntity> customFieldEntities,
			HttpServletRequest request) throws ApplicationException {
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
		Locale locale = getUserContext(request).getPreferredLocale();
		for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
			for (PersonnelCustomFieldEntity customFieldEntity : customFieldEntities) {
				if (customFieldDef.getFieldId().equals(
						customFieldEntity.getFieldId())) {
					if (customFieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
						customFields.add(new CustomFieldView(customFieldEntity
								.getFieldId(), DateHelper.getUserLocaleDate(
								locale, customFieldEntity.getFieldValue()),
								customFieldDef.getFieldType()));
					} else {
						customFields
								.add(new CustomFieldView(customFieldEntity
										.getFieldId(), customFieldEntity
										.getFieldValue(), customFieldDef
										.getFieldType()));
					}
				}
			}
		}
		return customFields;
	}

	private List<RoleBO> getRoles(HttpServletRequest request,
			PersonActionForm personActionForm) throws PageExpiredException {
		boolean addFlag = false;
		List<RoleBO> selectList = new ArrayList<RoleBO>();
		List<RoleBO> masterList = (List<RoleBO>) SessionUtils.getAttribute(
				PersonnelConstants.ROLEMASTERLIST, request);
		if (personActionForm.getPersonnelRoles() != null)
			for (RoleBO role : masterList) {
				for (String roleId : personActionForm.getPersonnelRoles()) {
					if (roleId != null
							&& role.getId().intValue() == Integer.valueOf(
									roleId).intValue()) {
						selectList.add(role);
						addFlag = true;
					}
				}
			}
		if (addFlag)
			return selectList;
		else
			return null;
	}

	private void updateRoleLists(HttpServletRequest request,
			PersonActionForm personActionForm) throws PageExpiredException {

		boolean addFlag = false;
		List<RoleBO> selectList = new ArrayList<RoleBO>();
		if (personActionForm.getPersonnelRoles() != null) {
			List<RoleBO> masterList = (List<RoleBO>) SessionUtils.getAttribute(
					PersonnelConstants.ROLEMASTERLIST, request);
			for (RoleBO role : masterList) {
				for (String roleId : personActionForm.getPersonnelRoles()) {
					if (roleId != null
							&& role.getId().intValue() == Integer.valueOf(
									roleId).intValue()) {
						selectList.add(role);
						addFlag = true;
					}
				}
			}
		}
		if (addFlag)
			SessionUtils.setCollectionAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
					selectList, request);
		else
			SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_ROLES_LIST,
					null, request);
	}

	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPreferredLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}

}
