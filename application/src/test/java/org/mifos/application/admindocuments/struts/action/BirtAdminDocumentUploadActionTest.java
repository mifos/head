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

package org.mifos.application.admindocuments.struts.action;

import org.junit.Ignore;
import org.mifos.application.admindocuments.business.AdminDocumentBO;
import org.mifos.application.admindocuments.persistence.AdminDocumentPersistence;
import org.mifos.application.admindocuments.struts.actionforms.BirtAdminDocumentUploadActionForm;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.reports.business.MockFormFile;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.application.reports.business.ReportsJasperMap;
import org.mifos.application.reports.persistence.ReportsPersistence;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;

@Ignore
public class BirtAdminDocumentUploadActionTest extends MifosMockStrutsTestCase {

    public BirtAdminDocumentUploadActionTest() throws SystemException, ApplicationException {
        super();
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    public void testGetBirtAdminDocumentUploadPage() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "getBirtAdminDocumentUploadPage");
        actionPerform();
        verifyNoActionErrors();
    }

    public void testShouldSubmitSucessWhenUploadNewAdminDocument() throws Exception {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");

        BirtAdminDocumentUploadActionForm form = new BirtAdminDocumentUploadActionForm();
        form
                .setAdminiDocumentTitle("testShouldSubmitSucessWhenUploadNewAdminDocumentWithAVeryLongNameThatExceedsOneHundredCharactersInLength");
        form.setIsActive("1");
        form.setFile(new MockFormFile("testFileName1.rptdesign"));
        setActionForm(form);

        addRequestParameter("method", "upload");
        actionPerform();

        AdminDocumentBO adminDocument = (AdminDocumentBO) request.getAttribute(Constants.BUSINESS_KEY);
        assertNotNull(adminDocument);
        ReportsPersistence rp = new ReportsPersistence();
        ReportsJasperMap jasper = (ReportsJasperMap) rp.getPersistentObject(ReportsJasperMap.class, adminDocument
                .getAdmindocId());
        assertNotNull(jasper);

        verifyNoActionErrors();
        verifyForward("create_success");

        removeReport(adminDocument.getAdmindocId());

    }

    private void removeReport(Short reportId) throws PersistenceException {

        AdminDocumentPersistence reportPersistence = new AdminDocumentPersistence();
        reportPersistence.getSession().clear();
        ReportsBO report = (ReportsBO) reportPersistence.getPersistentObject(AdminDocumentBO.class, reportId);

        RolesPermissionsPersistence permPersistence = new RolesPermissionsPersistence();
        ActivityEntity activityEntity = (ActivityEntity) permPersistence.getPersistentObject(ActivityEntity.class,
                report.getActivityId());
        reportPersistence.delete(report);

        LookUpValueEntity anLookUp = activityEntity.getActivityNameLookupValues();

        permPersistence.delete(activityEntity);
        permPersistence.delete(anLookUp);

        StaticHibernateUtil.commitTransaction();
    }

    public void testLoadProductInstance() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "loadProductInstance");
        actionPerform();
        verifyNoActionErrors();
    }

    public void testGetViewBirtAdminDocumentPage() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "loadProductInstance");
        actionPerform();
        verifyNoActionErrors();
    }

    public void testUpload() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "upload");
        actionPerform();
        verifyNoActionErrors();
    }

    public void testEdit() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "edit");
        addRequestParameter("admindocId", "1");
        actionPerform();
        AdminDocumentBO adminDocument = (AdminDocumentBO) request.getAttribute(Constants.BUSINESS_KEY);
        assertEquals("1", adminDocument.getAdmindocId().toString());
        verifyNoActionErrors();
        verifyForward(ActionForwards.edit_success.toString());

    }

    public void testEditThenUpload() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "editThenUpload");
        actionPerform();
        verifyNoActionErrors();
    }

    public void testDownloadAdminDocument() {
        setRequestPathInfo("/birtAdminDocumentUploadAction.do");
        addRequestParameter("method", "downloadAdminDocument");
        addRequestParameter("admindocId", "1");
        actionPerform();
        verifyNoActionErrors();
    }

}
