/**

 * CustomFieldsAction.java

 

 * Copyright (c) 2005-2007 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2007 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.configuration.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.configuration.struts.actionform.CustomFieldsActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.MessageLookup;
import org.mifos.framework.util.helpers.SessionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.mifos.application.configuration.util.helpers.CustomFieldsListBoxData;
import org.mifos.application.master.persistence.MasterPersistence;


public class CustomFieldsAction extends BaseAction {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("customFieldsAction");

		security.allow("load", SecurityConstants.VIEW);
		security.allow("viewCategory", SecurityConstants.VIEW);
		security.allow("update", SecurityConstants.VIEW);
		security.allow("cancel", SecurityConstants.VIEW);
		security.allow("loadDefineCustomFields", SecurityConstants.VIEW);
		security.allow("preview", SecurityConstants.VIEW);
		return security;
	}


	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
		actionForm.clear();

		logger.debug("Outside load method");
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	private void LoadDataTypes(Locale locale,
			CustomFieldsActionForm actionForm, HttpServletRequest request)
			throws Exception {
		List<CustomFieldsListBoxData> dataTypes = new ArrayList<CustomFieldsListBoxData>();
		CustomFieldsListBoxData data = null;
		for (CustomFieldType dataType : CustomFieldType.values()) {
			data = new CustomFieldsListBoxData();
			data.setName(MessageLookup.getInstance().lookup(dataType, locale));
			data.setId(dataType.getValue());
			dataTypes.add(data);
		}
		SessionUtils.setCollectionAttribute(
				ConfigurationConstants.ALL_DATA_TYPES, dataTypes, request);

	}

	private void LoadCategories(Short localeId,
			CustomFieldsActionForm actionForm, HttpServletRequest request)
			throws Exception {
		List<CustomFieldsListBoxData> allCategories = new ArrayList<CustomFieldsListBoxData>();
		CustomFieldsListBoxData data = null;
		MasterPersistence persistence = new MasterPersistence();
		for (CustomFieldCategory category : CustomFieldCategory.values()) {
			String entityName = category.mapToEntityType().name();
			CustomValueList valueList = persistence.getLookUpEntity(entityName,
					localeId);
			data = new CustomFieldsListBoxData();
			data.setName(valueList.getEntityLabel());
			data.setId(valueList.getEntityId());
			allCategories.add(data);
		}
		SessionUtils.setCollectionAttribute(
				ConfigurationConstants.ALL_CATEGORIES, allCategories, request);

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

	@TransactionDemarcate(saveToken = true)
	public ActionForward loadDefineCustomFields(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("Inside load define fields method");
		CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
		actionForm.clear();
		Locale locale = getUserLocale(request);
		LoadDataTypes(locale, actionForm, request);
		UserContext userContext = getUserContext(request);
		Short localeId = userContext.getLocaleId();
		LoadCategories(localeId, actionForm, request);

		logger.debug("Outside load define fields method");
		return mapping.findForward(ActionForwards.loadDefineCustomFields_success
				.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward viewCategory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside viewCategory method");
		CustomFieldsActionForm actionForm = (CustomFieldsActionForm) form;
		actionForm.clear();

		request.setAttribute("category", request.getParameter("category"));

		logger.debug("Outside viewCategory method");
		return mapping.findForward(ActionForwards.viewCategory_success
				.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("cancel method called");
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	@Override
	protected BusinessService getService() {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Configuration);
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside validate method");
		ActionForwards actionForward = ActionForwards.update_failure;

		String method = (String) request.getAttribute("method");
		if (method != null) {
			if (method.equals(Methods.load.toString())) {
				actionForward = ActionForwards.load_failure;
			}
			else if (method.equals(Methods.update.toString())) {
				actionForward = ActionForwards.update_failure;
			}
			else if (method.equals(Methods.preview.toString())) {
				actionForward = ActionForwards.preview_failure;
			}
		}

		logger.debug("outside validate method");
		return mapping.findForward(actionForward.toString());
	}

}
