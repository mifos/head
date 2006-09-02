package org.mifos.application.personnel.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.struts.actionforms.PersonActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
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
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	private void loadMasterData(HttpServletRequest request,
			PersonActionForm personActionForm) throws Exception {
		UserContext userContext = (UserContext) SessionUtils.getAttribute(
				Constants.USER_CONTEXT_KEY, request.getSession());
		MasterPersistence masterPersistence = new MasterPersistence();
		SessionUtils.setAttribute(PersonnelConstants.TITLE_LIST,
				masterPersistence.getLookUpEntity(
						MasterConstants.PERSONNEL_TITLE, userContext
								.getLocaleId()), request);
		SessionUtils.setAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST,
				masterPersistence.getLookUpEntity(
						MasterConstants.PERSONNEL_LEVELS, userContext
								.getLocaleId()), request);

		SessionUtils.setAttribute(PersonnelConstants.GENDER_LIST,
				masterPersistence.getLookUpEntity(MasterConstants.GENDER,
						userContext.getLocaleId()), request);
		SessionUtils.setAttribute(PersonnelConstants.MARITAL_STATUS_LIST,
				masterPersistence.getLookUpEntity(
						MasterConstants.MARITAL_STATUS, userContext
								.getLocaleId()), request);

		SessionUtils.setAttribute(PersonnelConstants.LANGUAGE_LIST,
				masterPersistence.getLookUpEntity(MasterConstants.LANGUAGE,
						userContext.getLocaleId()), request);

		SessionUtils.setAttribute(PersonnelConstants.ROLES_LIST,
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

}
