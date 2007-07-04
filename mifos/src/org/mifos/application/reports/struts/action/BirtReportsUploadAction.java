package org.mifos.application.reports.struts.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsCategoryBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.business.service.ReportsBusinessService;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.reports.struts.actionforms.BirtReportsUploadActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.AddActivity;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;

public class BirtReportsUploadAction extends BaseAction {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	private ReportsBusinessService reportsBusinessService;
	public static final short ENGLISH_LOCALE = 1;

	public BirtReportsUploadAction() {
		reportsBusinessService = new ReportsBusinessService();
	}

	public static ActionSecurity getSecurity() {
		ActionSecurity security = new ActionSecurity("birtReportsUploadAction");
		security.allow("getBirtReportsUploadPage",
				SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("preview", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("previous", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("upload", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("getViewReportPage",
				SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("edit", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("editpreview", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security
				.allow("editprevious", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		security.allow("editThenUpload",
				SecurityConstants.UPLOAD_REPORT_TEMPLATE);
		return security;
	}

	public ActionForward getBirtReportsUploadPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsAction:getBirtReportPage Method: ");
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
		uploadForm.clear();
		request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
				new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@Override
	protected BusinessService getService() throws ServiceException {
		return reportsBusinessService;
	}

	public ActionForward preview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());
		request.setAttribute("category", category);
		return mapping.findForward(ActionForwards.preview_success.toString());
	}

	public ActionForward previous(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	// TODO: transaction
	public ActionForward upload(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());

		for (ReportsBO report : category.getReportsSet()) {
			boolean isTitleSame = report.getReportName().equals(
					uploadForm.getReportTitle());
			if (isTitleSame) {
				ActionErrors errors = new ActionErrors();
				errors.add(ReportsConstants.ERROR_TITLEALREADYEXIST,
						new ActionMessage(
								ReportsConstants.ERROR_TITLEALREADYEXIST));
				request.setAttribute(Globals.ERROR_KEY, errors);
				return mapping.findForward(ActionForwards.preview_failure
						.toString());
			}
		}


		addreportActivity(uploadForm);
		ReportsBO reportBO = createAReportBOInDB(uploadForm);
		createAReportsJasperMapInDB(uploadForm);
		uploadFile(uploadForm.getFile());

		int newActivityId = maxActivityId() + 1;
		allowActivityPermission(reportBO, newActivityId);

		return mapping.findForward(ActionForwards.create_success.toString());
	}


	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String method = (String) request.getAttribute("methodCalled");
		return mapping.findForward(method + "_failure");
	}

	public ActionForward getViewReportPage(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("In ReportsAction:getViewReportsPage Method: ");
		request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
				new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(ActionForwards.get_success.toString());
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BirtReportsUploadActionForm birtReportsUploadActionForm = (BirtReportsUploadActionForm) form;
		ReportsBO report = new ReportsPersistence().getReport(Short
				.valueOf(request.getParameter("reportId")));
		request.setAttribute(Constants.BUSINESS_KEY, report);
		birtReportsUploadActionForm.setReportTitle(report.getReportName());
		birtReportsUploadActionForm.setReportCategoryId(report
				.getReportsCategoryBO().getReportCategoryId().toString());
		birtReportsUploadActionForm
				.setIsActive(report.getIsActive().toString());
		request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
				new ReportsPersistence().getAllReportCategories());
		return mapping.findForward(ActionForwards.edit_success.toString());
	}

	public ActionForward editpreview(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());
		request.setAttribute("category", category);
		return mapping.findForward(ActionForwards.editpreview_success
				.toString());
	}

	public ActionForward editprevious(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());
		request.setAttribute("category", category);
		return mapping.findForward(ActionForwards.editprevious_success
				.toString());
	}

	public ActionForward editThenUpload(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;


		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());

		String filename = filename(uploadForm);

		for (ReportsBO report : category.getReportsSet()) {
			boolean isTitleSame = report.getReportName().equals(
					uploadForm.getReportTitle());
			boolean isCategorySame = report.getReportsCategoryBO()
					.getReportCategoryId().toString().equals(
							uploadForm.getReportCategoryId());
			boolean isStatusSame = report.getIsActive().toString().equals(
					uploadForm.getIsActive());
			boolean isFileSame = filename.equals(uploadForm.getFile()
					.getFileName());

			if (isTitleSame && isCategorySame && isStatusSame && isFileSame) {
				ActionErrors errors = new ActionErrors();
				errors.add(ReportsConstants.ERROR_NOCHANGE, new ActionMessage(
						ReportsConstants.ERROR_NOCHANGE));
				request.setAttribute(Globals.ERROR_KEY, errors);
				return mapping.findForward(ActionForwards.editpreview_failure
						.toString());
			}
		}


		updateAReportBOInDB(uploadForm);
		updateAReportsJasperMapInDB(uploadForm);
		uploadFile(uploadForm.getFile());

		return mapping.findForward(ActionForwards.create_success.toString());
	}

	private String filename(BirtReportsUploadActionForm uploadForm)
			throws PersistenceException {
		ReportsPersistence reportPersistence = new ReportsPersistence();
		String filename = null;
		List<ReportsJasperMap> reports = reportPersistence
				.findJasperOfReportId(Short.valueOf(uploadForm.getReportId()));
		if (reports.size() > 0) {
			ReportsJasperMap reportFile = reports.get(0);
			filename = reportFile.getReportJasper();
		}
		return filename;
	}

	private void updateAReportsJasperMapInDB(
			BirtReportsUploadActionForm uploadForm) throws PersistenceException {
		ReportsJasperMap reportJasperMap = new ReportsPersistence().getReport(
				Short.valueOf(uploadForm.getReportId())).getReportsJasperMap();

		reportJasperMap.setReportJasper(uploadForm.getFile().getFileName());
		new ReportsPersistence().createOrUpdate(reportJasperMap);
	}

	private void updateAReportBOInDB(BirtReportsUploadActionForm uploadForm)
			throws PersistenceException {

		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());
		ReportsBO reportBO = new ReportsPersistence().getReport(Short
				.valueOf(uploadForm.getReportId()));

		
		
		reportBO.setReportName(uploadForm.getReportTitle());
		reportBO.setReportsCategoryBO(category);
		reportBO.setIsActive(Short.valueOf(uploadForm.getIsActive()));
		new ReportsPersistence().createOrUpdate(reportBO);
	}

	private ReportsBO createAReportBOInDB(BirtReportsUploadActionForm uploadForm)
			throws ServiceException, PersistenceException {
		ReportsBO reportBO = new ReportsBO();
		int newActivityId = maxActivityId() + 1;
		ReportsCategoryBO category = getReportCategory(uploadForm
				.getReportCategoryId());
		

		reportBO.setReportName(uploadForm.getReportTitle());
		reportBO.setReportsCategoryBO(category);
		reportBO.setActivityId((short) newActivityId);
		reportBO.setIsActive(Short.valueOf(uploadForm.getIsActive()));
		new ReportsPersistence().createOrUpdate(reportBO);
		return reportBO;
	}

	private void createAReportsJasperMapInDB(
			BirtReportsUploadActionForm uploadForm) throws ServiceException,
			PersistenceException {
		ReportsJasperMap reportJasperMap;
		reportJasperMap = new ReportsJasperMap();
		FormFile formFile = uploadForm.getFile();

		reportJasperMap.setReportJasper(formFile.getFileName());
		new ReportsPersistence().createOrUpdate(reportJasperMap);
	}


	private void addreportActivity(BirtReportsUploadActionForm uploadForm)
			throws ServiceException, IOException, SQLException {
		int newActivityId = maxActivityId() + 1;
		Connection conn = new ReportsPersistence().getConnection();

		new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION,
				newActivityId, getReportCategory(
						uploadForm.getReportCategoryId()).getActivityId(),
				ENGLISH_LOCALE, "Can view " + uploadForm.getReportTitle())
				.upgrade(conn);

	}

	private int maxActivityId() throws ServiceException {
		int activityId = 0;
		for (ActivityEntity activity : new RolesPermissionsBusinessService()
				.getActivities()) {
			if (activity.getId().intValue() > activityId)
				activityId = activity.getId();
		}
		return activityId;
	}

	private void allowActivityPermission(ReportsBO reportBO, int newActivityId)
			throws ApplicationException {
		ActivityMapper.getInstance().getActivityMap().put(
				"/reportsUserParamsAction-loadAddList-"
						+ reportBO.getReportId(), (short) newActivityId);

		AuthorizationManager.getInstance().init();
	}

	private void uploadFile(FormFile formFile) throws FileNotFoundException,
			IOException {
		File dir = new File(getServlet().getServletContext().getRealPath("/")
				+ "report");
		File file = new File(dir, formFile.getFileName());
		InputStream is = formFile.getInputStream();
		FileOutputStream os = new FileOutputStream(file);
		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		while ((bytesRead = is.read(buffer, 0, 4096)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		is.close();
		formFile.destroy();
	}


	private ReportsCategoryBO getReportCategory(Short reportCategoryId) {
		List<ReportsCategoryBO> categories = new ReportsPersistence()
				.getAllReportCategories();
		for (ReportsCategoryBO reportsCategory : categories) {
			if (reportsCategory.getReportCategoryId().equals(reportCategoryId)) {
				return reportsCategory;
			}
		}
		return null;
	}

	private ReportsCategoryBO getReportCategory(String reportCategoryId) {
		return getReportCategory(Short.valueOf(reportCategoryId));
	}
}
