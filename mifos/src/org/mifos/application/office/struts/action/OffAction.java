package org.mifos.application.office.struts.action;

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
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.struts.actionforms.OffActionForm;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class OffAction extends BaseAction {

	@Override
	protected BusinessService getService() throws ServiceException {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Office);
	}

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OffActionForm actionForm = (OffActionForm) form;
		loadParents(request, actionForm);
		actionForm.clear();
		loadCreateCustomFields(actionForm, request);
		SessionUtils.setAttribute(OfficeConstants.OFFICELEVELLIST,
				((OfficeBusinessService) getService())
						.getConfiguredLevels(getUserContext(request)
								.getLocaleId()), request.getSession());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward loadParent(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loadParents(request, (OffActionForm) form);
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return mapping.findForward(ActionForwards.previous_success.toString());
	}

	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OffActionForm offActionForm = (OffActionForm) form;
		OfficeLevel level = OfficeLevel
				.getOfficeLevel(getShortValue(offActionForm.getOfficeLevel()));
		OfficeBO parentOffice = ((OfficeBusinessService) getService())
				.getOffice(getShortValue(offActionForm.getParentOfficeId()));

		OfficeBO officeBO = new OfficeBO(getUserContext(request), level,
				parentOffice, offActionForm.getCustomFields(), offActionForm
						.getOfficeName(), offActionForm.getShortName(),
				offActionForm.getAddress(), OperationMode.REMOTE_SERVER);
		HibernateUtil.closeandFlushSession();
		officeBO.save();
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, officeBO, request
				.getSession());
		return mapping.findForward(ActionForwards.create_success.toString());
	}

	private void loadParents(HttpServletRequest request, OffActionForm form)
			throws Exception {
		String officeLevel = (String) request.getParameter("officeLevel");
		if (!StringUtils.isNullOrEmpty(officeLevel)) {
			form.setOfficeLevel(officeLevel);
			OfficeLevel Level = OfficeLevel.getOfficeLevel(Short
					.valueOf(officeLevel));
			SessionUtils.setAttribute(OfficeConstants.PARENTS,
					((OfficeBusinessService) getService()).getActiveParents(
							Level, getUserContext(request).getLocaleId()),
					request.getSession());
		}
	}

	private void loadCreateCustomFields(OffActionForm actionForm,
			HttpServletRequest request) throws SystemException {
		loadCustomFieldDefinitions(request);
		// Set Default values for custom fields
		List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
				.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request
						.getSession());
		List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

		for (CustomFieldDefinitionEntity fieldDef : customFieldDefs) {
			if (StringUtils.isNullAndEmptySafe(fieldDef.getDefaultValue())
					&& fieldDef.getFieldType().equals(
							CustomFieldType.DATE.getValue())) {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						DateHelper.getUserLocaleDate(getUserContext(request)
								.getPereferedLocale(), fieldDef
								.getDefaultValue()), fieldDef.getFieldType()));
			} else {
				customFields.add(new CustomFieldView(fieldDef.getFieldId(),
						fieldDef.getDefaultValue(), fieldDef.getFieldType()));
			}
		}
		actionForm.setCustomFields(customFields);
	}

	private void loadCustomFieldDefinitions(HttpServletRequest request)
			throws SystemException {
		MasterDataService masterDataService = (MasterDataService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.MasterDataService);
		List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
				.retrieveCustomFieldsDefinition(EntityType.OFFICE);
		SessionUtils.setAttribute(CustomerConstants.CUSTOM_FIELDS_LIST,
				customFieldDefs, request.getSession());
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

}
