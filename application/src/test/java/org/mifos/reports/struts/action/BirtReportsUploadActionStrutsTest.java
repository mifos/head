/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.reports.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.apache.struts.upload.FormFile;
import org.junit.Ignore;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.reports.business.MockFormFile;
import org.mifos.reports.business.ReportsBO;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.struts.actionforms.BirtReportsUploadActionForm;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.rolesandpermission.utils.ActivityTestUtil;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.security.AddActivity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;

@Ignore
public class BirtReportsUploadActionStrutsTest extends MifosMockStrutsTestCase {

    public BirtReportsUploadActionStrutsTest() throws Exception {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        ReportsBO report = (ReportsBO) request.getAttribute(Constants.BUSINESS_KEY);
       Assert.assertEquals("1", report.getReportId().toString());
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
        addRequestParameter("reportTitle", "editPreviewFailureWhenReportCategoryIdIsEmpty");
        addRequestParameter("reportCategoryId", "");
        addRequestParameter("isActive", "1");
        actionPerform();
        verifyForwardPath("/birtReportsUploadAction.do?method=validate");
    }

    public void testShouldEditPreviewFailureWhenIsActiveIsEmpty() {
        setRequestPathInfo("/birtReportsUploadAction.do");
        addRequestParameter("method", "editpreview");
        addRequestParameter("reportTitle", "editPreviewFailureWhenIsActiveIsEmpty");
        addRequestParameter("reportCategoryId", "1");
        addRequestParameter("isActive", "");
        actionPerform();
        verifyForwardPath("/birtReportsUploadAction.do?method=validate");
    }

    public void testUpgradePathNotRuined() throws Exception {
        // Retrieve initial activities information
        List<ActivityEntity> activities = new RolesPermissionsBusinessService().getActivities();
        int newActivityId = activities.get(activities.size() - 1).getId() + 1;

        // Upload a report creating an activity for the report
        FormFile file = new MockFormFile("testFilename.rptdesign");
        BirtReportsUploadActionForm actionForm = new BirtReportsUploadActionForm();
        setRequestPathInfo("/birtReportsUploadAction.do");
        addRequestParameter("method", "upload");
        actionForm.setFile(file);
        actionForm.setReportTitle("exsitTitle");
        actionForm.setReportCategoryId("1");
        actionForm.setIsActive("1");
        setActionForm(actionForm);
        actionPerform();
       Assert.assertEquals(0, getErrorSize());

        Assert.assertNotNull(request.getAttribute("report"));

        // Simulate an future activities upgrade
        AddActivity activity = null;
        try {
            activity = new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION, (short) newActivityId,
                    SecurityConstants.ORGANIZATION_MANAGEMENT, DatabaseVersionPersistence.ENGLISH_LOCALE, "no name");
            activity.upgrade(StaticHibernateUtil.getSessionTL().connection());

        } catch (Exception e) {
            StaticHibernateUtil.startTransaction();
            new RolesPermissionsPersistence().delete(request.getAttribute("report"));
            StaticHibernateUtil.commitTransaction();
            throw e;
        }

        // Undo
        ReportsBO report = (ReportsBO) request.getAttribute("report");
        removeReport(report.getReportId());
    }

    public void testShouldCreateFailureWhenActivityIdOutOfRange() throws Exception {
        ActivityEntity activityEntity = ActivityTestUtil.insertActivityForTest(Short.MIN_VALUE);

        FormFile file = new MockFormFile("testFilename");
        BirtReportsUploadActionForm actionForm = new BirtReportsUploadActionForm();
        setRequestPathInfo("/birtReportsUploadAction.do");
        addRequestParameter("method", "upload");
        actionForm.setFile(file);
        actionForm.setReportTitle("existingTitle");
        actionForm.setReportCategoryId("1");
        actionForm.setIsActive("1");
        setActionForm(actionForm);
        actionPerform();

        verifyForward("preview_failure");
        String[] errors = { ReportsConstants.ERROR_NOMOREDYNAMICACTIVITYID };
        verifyActionErrors(errors);

        ActivityTestUtil.deleteActivityForTest(activityEntity);
    }

    public void testShouldPreviewSuccessWithReportTemplate() throws Exception {
        setRequestPathInfo("/birtReportsUploadAction.do");

        BirtReportsUploadActionForm form = new BirtReportsUploadActionForm();
        form.setFile(new MockFormFile("testFileName1.rptdesign"));
        form.setIsActive("1");
        form.setReportCategoryId("1");
        form.setReportTitle("testReportTitle1");
        setActionForm(form);

        addRequestParameter("method", "preview");
        actionPerform();

        verifyNoActionErrors();
        verifyForward("preview_success");
    }

    public void testShouldPreviewFailureWithOutReportTemplate() throws Exception {
        setRequestPathInfo("/birtReportsUploadAction.do");

        BirtReportsUploadActionForm form = new BirtReportsUploadActionForm();
        form.setIsActive("1");
        form.setReportCategoryId("1");
        form.setReportTitle("testReportTitle2");
        setActionForm(form);

        addRequestParameter("method", "preview");
        actionPerform();

        String[] errors = { ReportsConstants.ERROR_FILE };
        verifyActionErrors(errors);
    }

    public void testShouldSubmitSucessWhenUploadNewReport() throws Exception {

        setRequestPathInfo("/birtReportsUploadAction.do");

        BirtReportsUploadActionForm form = new BirtReportsUploadActionForm();
        form.setReportTitle("testShouldSubmitSucessWhenUploadNewReport");
        form.setReportCategoryId("1");
        form.setIsActive("1");
        form.setFile(new MockFormFile("testFileName1.rptdesign"));
        setActionForm(form);

        addRequestParameter("method", "upload");
        actionPerform();

        ReportsBO report = (ReportsBO) request.getAttribute("report");
        Assert.assertNotNull(report);
        ReportsPersistence rp = new ReportsPersistence();
        ReportsJasperMap jasper = (ReportsJasperMap) rp.getPersistentObject(ReportsJasperMap.class, report
                .getReportId());
        Assert.assertNotNull(jasper);

        verifyNoActionErrors();
        verifyForward("create_success");

        removeReport(report.getReportId());

    }

    public void testShouldSubmitSuccessAfterEdit() throws Exception {
        setRequestPathInfo("/birtReportsUploadAction.do");

        ReportsPersistence persistence = new ReportsPersistence();
        ReportsBO report = new ReportsBO();
        report.setReportName("testShouldSubmitSuccessAfterEdit");
        report.setReportsCategoryBO((ReportsCategoryBO) persistence.getPersistentObject(ReportsCategoryBO.class,
                (short) 1));
        report.setIsActive((short) 1);
        short newActivityId = (short) (new BirtReportsUploadAction()).insertActivity((short) 1, "test"
                + "testShouldSubmitSuccessAfterEdit");
        report.setActivityId(newActivityId);

        ReportsJasperMap reportJasperMap = report.getReportsJasperMap();
        reportJasperMap.setReportJasper("testFileName_EDIT.rptdesign");
        report.setReportsJasperMap(reportJasperMap);
        persistence.createOrUpdate(report);

        BirtReportsUploadActionForm editForm = new BirtReportsUploadActionForm();
        editForm.setReportId(report.getReportId().toString());
        editForm.setReportTitle("newTestShouldSubmitSuccessAfterEdit");
        editForm.setReportCategoryId("2");
        editForm.setIsActive("0");
        editForm.setFile(new MockFormFile("newTestShouldSubmitSuccessAfterEdit.rptdesign"));
        setActionForm(editForm);
        addRequestParameter("method", "editThenUpload");

        actionPerform();

        ReportsBO newReport = persistence.getReport(report.getReportId());
        ReportsJasperMap jasper = (ReportsJasperMap) persistence.getPersistentObject(ReportsJasperMap.class, report
                .getReportId());

       Assert.assertEquals("newTestShouldSubmitSuccessAfterEdit", newReport.getReportName());
       Assert.assertEquals(2, newReport.getReportsCategoryBO().getReportCategoryId().shortValue());
       Assert.assertEquals(0, newReport.getIsActive().shortValue());
       Assert.assertEquals("newTestShouldSubmitSuccessAfterEdit.rptdesign", newReport.getReportsJasperMap().getReportJasper());
       Assert.assertEquals("newTestShouldSubmitSuccessAfterEdit.rptdesign", jasper.getReportJasper());

        removeReport(newReport.getReportId());

    }

    private void removeReport(Short reportId) throws PersistenceException {

        ReportsPersistence reportPersistence = new ReportsPersistence();
        reportPersistence.getSession().clear();
        ReportsBO report = (ReportsBO) reportPersistence.getPersistentObject(ReportsBO.class, reportId);

        RolesPermissionsPersistence permPersistence = new RolesPermissionsPersistence();
        ActivityEntity activityEntity = (ActivityEntity) permPersistence.getPersistentObject(ActivityEntity.class,
                report.getActivityId());
        reportPersistence.delete(report);

        LookUpValueEntity anLookUp = activityEntity.getActivityNameLookupValues();

        permPersistence.delete(activityEntity);
        permPersistence.delete(anLookUp);

        StaticHibernateUtil.commitTransaction();
    }

}
