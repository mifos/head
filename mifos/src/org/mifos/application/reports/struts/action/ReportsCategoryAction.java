/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.reports.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.struts.actionforms.ReportsCategoryActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.activity.ActivityGenerator;
import org.mifos.framework.security.activity.ActivityGeneratorException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.security.activity.DynamicLookUpValueCreationTypes;

public class ReportsCategoryAction extends BaseAction {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.REPORTSLOGGER);
	private ReportsBusinessService reportsBusinessService;

	public ReportsCategoryAction() {
		reportsBusinessService = (ReportsBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.ReportsService);
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return reportsBusinessService;
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("reportsCategoryAction");
		security.allow("loadDefineNewCategoryPage",
				SecurityConstants.DEFINE_REPORT_CATEGORY);
		security.allow("preview", SecurityConstants.DEFINE_REPORT_CATEGORY);
		security.allow("addNewCategory",
				SecurityConstants.DEFINE_REPORT_CATEGORY);
		security.allow("viewReportsCategory",
				SecurityConstants.VIEW_REPORT_CATEGORY);
		security.allow("confirmDeleteReportsCategory",
				SecurityConstants.DELETE_REPORT_CATEGORY);
		security.allow("edit", SecurityConstants.VIEW_REPORT_CATEGORY);
		security.allow("editPreview", SecurityConstants.VIEW_REPORT_CATEGORY);
		security.allow("deleteReportsCategory",
				SecurityConstants.DELETE_REPORT_CATEGORY);
		security
				.allow("editThenSubmit", SecurityConstants.VIEW_REPORT_CATEGORY);
		return security;
	}

	public ActionForward loadDefineNewCategoryPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger
				.debug("In ReportsCategoryAction:loadDefineNewCategoryPage Method: ");
		((ReportsCategoryActionForm) form).clear();
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:preview Method: ");
		ReportsCategoryActionForm defineCategoryForm = (ReportsCategoryActionForm) form;
		String categoryName = defineCategoryForm.getCategoryName();
		request.setAttribute("categoryName", categoryName);

		for (ReportsCategoryBO category : new ReportsPersistence()
				.getAllReportCategories()) {
			if (categoryName.equals(category.getReportCategoryName())) {
				ActionErrors errors = new ActionErrors();
				errors
						.add(
								ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST,
								new ActionMessage(
										ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST));
				request.setAttribute(Globals.ERROR_KEY, errors);
				return mapping.findForward(ActionForwards.preview_failure
						.toString());
			}
		}

		return mapping.findForward(ActionForwards.preview_success.toString());

	}

	public ActionForward addNewCategory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:addNewCategory Method: ");

		ReportsCategoryActionForm defineNewCategoryForm = (ReportsCategoryActionForm) form;
		String categoryName = defineNewCategoryForm.getCategoryName();
		ReportsCategoryBO reportsCategoryBO = new ReportsCategoryBO();

		int newActivityId;
		try {
			newActivityId = ActivityGenerator.calculateDynamicActivityId();
		}
		catch (ActivityGeneratorException agex) {

			ActionErrors errors = new ActionErrors();
			errors.add(agex.getKey(), new ActionMessage(agex.getKey()));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(ActionForwards.preview_failure
					.toString());
		}

		ActivityGenerator activityGenerator = new ActivityGenerator();
		Short parentActivityId = SecurityConstants.REPORTS_MANAGEMENT;
		activityGenerator.upgradeUsingHQL(DynamicLookUpValueCreationTypes.BirtReport, parentActivityId, categoryName);

		reportsCategoryBO.setActivityId((short) newActivityId);
		reportsCategoryBO.setReportCategoryName(categoryName);

		new ReportsPersistence().createOrUpdate(reportsCategoryBO);

		return mapping.findForward(ActionForwards.create_success.toString());
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	public ActionForward viewReportsCategory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsCategoryAction:viewReportsCategory Method: ");
		request.getSession().setAttribute(
				ReportsConstants.LISTOFREPORTCATEGORIES,
				new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	public ActionForward confirmDeleteReportsCategory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger
				.debug("In ReportsCategoryAction:confirmDeleteReportsCategory Method: ");
		ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
		ReportsCategoryBO reportsCategoryBO = new ReportsPersistence()
				.getReportCategoryByCategoryId(Short
						.valueOf(reportsCategoryActionForm.getCategoryId()));

		reportsCategoryActionForm.setCategoryName(reportsCategoryBO
				.getReportCategoryName());

		if (!isValidToDelete(request, reportsCategoryBO)) {
			return mapping
					.findForward(ActionForwards.confirm_delete.toString());
		}

		return mapping.findForward(ActionForwards.confirm_delete.toString());
	}

	private boolean isValidToDelete(HttpServletRequest request,
			ReportsCategoryBO reportsCategoryBO) {
		if (!reportsCategoryBO.getReportsSet().isEmpty()) {
			ActionErrors errors = new ActionErrors();
			errors
					.add(ReportsConstants.ERROR_CATEGORYHASREPORTS,
							new ActionMessage(
									ReportsConstants.ERROR_CATEGORYHASREPORTS));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return false;
		}
		return true;
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:edit Method: ");
		ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
		String reportCategoryId = request.getParameter("categoryId");
		ReportsCategoryBO reportCategory = new ReportsPersistence()
				.getReportCategoryByCategoryId(Short.valueOf(reportCategoryId));
		reportsCategoryActionForm.setCategoryName(reportCategory
				.getReportCategoryName());
		return mapping.findForward(ActionForwards.edit_success.toString());
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:editPreview Method: ");
		ReportsCategoryActionForm defineCategoryForm = (ReportsCategoryActionForm) form;
		String inputCategoryName = defineCategoryForm.getCategoryName();
		short reportCategoryId = defineCategoryForm.getCategoryId();
		ReportsCategoryBO reportCategory = new ReportsPersistence()
				.getReportCategoryByCategoryId(reportCategoryId);
		if (isReportCategoryNameNotEdit(request, inputCategoryName,
				reportCategory)) {
			return mapping.findForward(ActionForwards.editPreview_failure
					.toString());
		}
		else if (isReportCategoryNameAlreadyExist(request, inputCategoryName)) {
			return mapping.findForward(ActionForwards.editPreview_failure
					.toString());
		}

		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	private boolean isReportCategoryNameAlreadyExist(
			HttpServletRequest request, String inputCategoryName) {
		for (ReportsCategoryBO category : new ReportsPersistence()
				.getAllReportCategories()) {
			if (category.getReportCategoryName().equals(inputCategoryName)) {
				ActionErrors errors = new ActionErrors();
				errors
						.add(
								ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST,
								new ActionMessage(
										ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST));
				request.setAttribute(Globals.ERROR_KEY, errors);
				return true;
			}
		}
		return false;
	}

	private boolean isReportCategoryNameNotEdit(HttpServletRequest request,
			String inputCategoryName, ReportsCategoryBO reportCategory) {
		if (inputCategoryName.equals(reportCategory.getReportCategoryName())) {
			ActionErrors errors = new ActionErrors();
			errors.add(ReportsConstants.ERROR_CATEGORYNAMENOTEDIT,
					new ActionMessage(
							ReportsConstants.ERROR_CATEGORYNAMENOTEDIT));
			request.setAttribute(Globals.ERROR_KEY, errors);
			return true;
		}
		return false;
	}

	public ActionForward deleteReportsCategory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsCategoryAction:deleteReportsCategory Method: ");
		ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
		ReportsCategoryBO reportsCategoryBO = new ReportsPersistence()
				.getReportCategoryByCategoryId(Short
						.valueOf(reportsCategoryActionForm.getCategoryId()));

		if (!isValidToDelete(request, reportsCategoryBO)) {
			return mapping
					.findForward(ActionForwards.confirm_delete.toString());
		}
		new ReportsPersistence().delete(reportsCategoryBO);

		request.getSession().setAttribute(
				ReportsConstants.LISTOFREPORTCATEGORIES,
				new ReportsPersistence().getAllReportCategories());

		return mapping.findForward(ActionForwards.delete_success.toString());
	}


	public ActionForward editThenSubmit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:editThenSubmit Method: ");
		ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
		short reportCategoryId = reportsCategoryActionForm.getCategoryId();
		String inputCategoryName = reportsCategoryActionForm.getCategoryName();
		ReportsCategoryBO reportsCategoryBO = new ReportsPersistence()
				.getReportCategoryByCategoryId(reportCategoryId);
		if (isReportCategoryNameAlreadyExist(request, inputCategoryName)) {
			return mapping.findForward(ActionForwards.editPreview_failure
					.toString());
		}
		reportsCategoryBO.setReportCategoryName(inputCategoryName);
		ReportsPersistence rPersistence = new ReportsPersistence();
		rPersistence.createOrUpdate(reportsCategoryBO);
		// update cache
		Short activityId = reportsCategoryBO.getActivityId();
		rPersistence.updateLookUpValue(activityId, inputCategoryName);
		
		ActivityGenerator.changeActivityMessage(reportsCategoryBO
				.getActivityId(), DatabaseVersionPersistence.ENGLISH_LOCALE,
				reportsCategoryBO.getReportCategoryName());
		return mapping.findForward(ActionForwards.create_success.toString());
	}
}
