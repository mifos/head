package org.mifos.application.reports.struts.action;

import java.util.List;

import org.apache.struts.upload.FormFile;
import org.mifos.application.reports.business.MockFormFile;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.struts.actionforms.BirtReportsUploadActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.AddActivity;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class BirtReportsUploadActionTest extends MifosMockStrutsTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/reports/struts-config.xml").getPath());
	}

	public void testGetBirtReportsUploadPage() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "getBirtReportsUploadPage");
		addRequestParameter("viewPath", "administerreports_path");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
	}

	public void testEdit() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "edit");
		addRequestParameter("reportId", "1");
		actionPerform();
		ReportsBO report = (ReportsBO) request
				.getAttribute(Constants.BUSINESS_KEY);
		assertEquals("1", report.getReportId().toString());
		verifyNoActionErrors();
		verifyForward(ActionForwards.edit_success.toString());
	}

	public void testShouldEditPreviewFailureWhenReportTitleIsEmpty() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "editpreview");
		addRequestParameter("reportTitle", "");
		addRequestParameter("reportCategoryId", "1");
		addRequestParameter("isActive", "1");
		actionPerform();
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}

	public void testShouldEditPreviewFailureWhenReportCategoryIdIsEmpty() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "editpreview");
		addRequestParameter("reportTitle", "existTitle");
		addRequestParameter("reportCategoryId", "");
		addRequestParameter("isActive", "1");
		actionPerform();
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}

	public void testShouldEditPreviewFailureWhenIsActiveIsEmpty() {
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "editpreview");
		addRequestParameter("reportTitle", "exsitTitle");
		addRequestParameter("reportCategoryId", "1");
		addRequestParameter("isActive", "");
		actionPerform();
		verifyForwardPath("/birtReportsUploadAction.do?method=validate");
	}

	/**
	 * !!Remove the 'x' in method name to allow test to run!!
	 * This is tests if the upgrade path for activities is not ruined by
	 * BirtReports permission changes. At this point the test fails because
	 * BirtReports does interfere with the upgrade path.
	 */
	public void testUpgradePathNotRuined() throws Exception {
		// Retrieve initial activities information
		List<ActivityEntity> activities = new RolesPermissionsBusinessService()
				.getActivities();
		int newActivityId = activities.get(activities.size() - 1).getId() + 1;

		// Upload a report creating an activity for the report
		FormFile file = new MockFormFile("testFilename");
		BirtReportsUploadActionForm actionForm = new BirtReportsUploadActionForm();
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "upload");
		actionForm.setFile(file);
		actionForm.setReportTitle("exsitTitle");
		actionForm.setReportCategoryId("1");
		actionForm.setIsActive("1");
		setActionForm(actionForm);
		actionPerform();
		assertEquals(0, getErrorSize());

		assertNotNull(request.getAttribute("activity"));
		assertNotNull(request.getAttribute("report"));

		// Simulate an future activities upgrade 
		AddActivity activity = null;
		try {
			activity = new AddActivity(
					DatabaseVersionPersistence.APPLICATION_VERSION,
					(short) newActivityId,
					SecurityConstants.ORGANIZATION_MANAGEMENT,
					DatabaseVersionPersistence.ENGLISH_LOCALE, "no name");
			activity.upgrade(HibernateUtil.getSessionTL().connection());

		}
		catch (Exception e) {
			((AddActivity) request.getAttribute("activity"))
					.downgrade(HibernateUtil.getSessionTL().connection());
			HibernateUtil.startTransaction();
			new RolesPermissionsPersistence().delete(request
					.getAttribute("report"));
			HibernateUtil.commitTransaction();
			throw e;
		}

		//Undo
		activity.downgrade(HibernateUtil.getSessionTL().connection());
		((AddActivity) request.getAttribute("activity"))
				.downgrade(HibernateUtil.getSessionTL().connection());
		new RolesPermissionsPersistence()
				.delete(request.getAttribute("report"));
	}

	public void testShouldCreateFailureWhenActivityIdOutOfRange()
			throws Exception {
		AddActivity activity = new AddActivity(
				DatabaseVersionPersistence.APPLICATION_VERSION,
				Short.MIN_VALUE, SecurityConstants.ORGANIZATION_MANAGEMENT,
				DatabaseVersionPersistence.ENGLISH_LOCALE,
				"report with a min activityId");
		activity.upgrade(HibernateUtil.getSessionTL().connection());

		FormFile file = new MockFormFile("testFilename");
		BirtReportsUploadActionForm actionForm = new BirtReportsUploadActionForm();
		setRequestPathInfo("/birtReportsUploadAction.do");
		addRequestParameter("method", "upload");
		actionForm.setFile(file);
		actionForm.setReportTitle("exsitTitle");
		actionForm.setReportCategoryId("1");
		actionForm.setIsActive("1");
		setActionForm(actionForm);
		actionPerform();

		verifyForward("preview_failure");
		String[] errors = { ReportsConstants.ERROR_NOMOREDYNAMICACTIVITYID };
		verifyActionErrors(errors);


		activity.downgrade(HibernateUtil.getSessionTL().connection());
	}

}
