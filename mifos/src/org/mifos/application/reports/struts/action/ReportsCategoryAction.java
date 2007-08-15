package org.mifos.application.reports.struts.action;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.struts.actionforms.ReportsCategoryActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.AddActivity;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;

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
		return mapping.findForward(ActionForwards.preview_success.toString());

	}

	public ActionForward addNewCategory(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:addNewCategory Method: ");

		ReportsCategoryActionForm defineNewCategoryForm = (ReportsCategoryActionForm) form;
		String categoryName = defineNewCategoryForm.getCategoryName();
		ReportsCategoryBO reportsCategoryBO = new ReportsCategoryBO();


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

		int activityId = 0;
		for (ActivityEntity activity : new RolesPermissionsBusinessService()
				.getActivities()) {
			if (activity.getId().intValue() > activityId)
				activityId = activity.getId();
		}
		activityId = activityId + 1;

		Connection conn = new ReportsPersistence().getConnection();
		new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION,
				(short) activityId, SecurityConstants.REPORTS_MANAGEMENT,
				DatabaseVersionPersistence.ENGLISH_LOCALE, categoryName)
				.upgrade(conn);

		reportsCategoryBO.setActivityId((short) activityId);
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
		String categoryName = request.getParameter("categoryName");
		for (ReportsCategoryBO category : new ReportsPersistence()
				.getAllReportCategories()) {
			if (category.getReportCategoryName().equals(categoryName)) {
				if (!category.getReportsSet().isEmpty()) {
					ActionErrors errors = new ActionErrors();
					errors.add(ReportsConstants.ERROR_CATEGORYHASREPORTS,
							new ActionMessage(
									ReportsConstants.ERROR_CATEGORYHASREPORTS));
					request.setAttribute(Globals.ERROR_KEY, errors);

				}
				break;
			}
		}

		return mapping.findForward(ActionForwards.confirm_delete.toString());
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:edit Method: ");
		ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
		String reportCategoryId = request.getParameter("categoryId");
		ReportsCategoryBO reportCategory = new ReportsPersistence().getReportCategoryByCategoryId(Short.valueOf(reportCategoryId));
		reportsCategoryActionForm.setCategoryName(reportCategory.getReportCategoryName());
		return mapping.findForward(ActionForwards.edit_success.toString());
	}

	public ActionForward editPreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("In ReportsCategoryAction:editPreview Method: ");
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	public ActionForward deleteReportsCategory(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsCategoryAction:deleteReportsCategory Method: ");
		String categoryName = request.getParameter("categoryName");
		ReportsCategoryBO reportsCategoryBO = new ReportsCategoryBO();
		for (ReportsCategoryBO category : new ReportsPersistence()
				.getAllReportCategories()) {
			if (category.getReportCategoryName().equals(categoryName)) {
				if (!category.getReportsSet().isEmpty()) {
					ActionErrors errors = new ActionErrors();
					errors.add(ReportsConstants.ERROR_CATEGORYHASREPORTS,
							new ActionMessage(
									ReportsConstants.ERROR_CATEGORYHASREPORTS));
					request.setAttribute(Globals.ERROR_KEY, errors);
					return mapping.findForward(ActionForwards.confirm_delete
							.toString());
				}
				else {
					reportsCategoryBO = category;
					break;
				}
			}
		}
		
		Connection conn = new ReportsPersistence().getConnection();
		new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION,
				reportsCategoryBO.getActivityId(),
				SecurityConstants.REPORTS_MANAGEMENT,
				DatabaseVersionPersistence.ENGLISH_LOCALE, categoryName)
				.downgrade(conn);
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
		ReportsCategoryBO reportsCategoryBO = new ReportsPersistence().getReportCategoryByCategoryId(reportCategoryId);
		reportsCategoryBO.setReportCategoryName(reportsCategoryActionForm.getCategoryName());
		new ReportsPersistence().createOrUpdate(reportsCategoryBO);
		return mapping.findForward(ActionForwards.create_success.toString());
	}
}
