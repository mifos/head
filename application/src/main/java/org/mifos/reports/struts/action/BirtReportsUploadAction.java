/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.upload.FormFile;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.Localization;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.reports.business.ReportsBO;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.business.service.ReportsBusinessService;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.struts.actionforms.BirtReportsUploadActionForm;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.mifos.security.activity.ActivityGeneratorException;
import org.mifos.security.util.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirtReportsUploadAction extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(BirtReportsUploadAction.class);
    private ReportsBusinessService reportsBusinessService;

    public BirtReportsUploadAction() {
        reportsBusinessService = new ReportsBusinessService();
    }

    public ActionForward getBirtReportsUploadPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsAction:getBirtReportPage Method: ");
        StaticHibernateUtil.flushAndCloseSession();
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

    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
        ReportsPersistence rp = new ReportsPersistence();
        ReportsCategoryBO category = rp.getPersistentObject(ReportsCategoryBO.class, Short
                .valueOf(uploadForm.getReportCategoryId()));
        request.setAttribute("category", category);
        if (isReportAlreadyExist(request, uploadForm.getReportTitle(), category)) {
            return mapping.findForward(ActionForwards.preview_failure.toString());
        }
            return mapping.findForward(ActionForwards.preview_success.toString());
    }

    public ActionForward previous(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;

        ReportsPersistence rp = new ReportsPersistence();
        ReportsCategoryBO category = rp.getPersistentObject(ReportsCategoryBO.class, Short
                .valueOf(uploadForm.getReportCategoryId()));

        if (isReportAlreadyExist(request, uploadForm.getReportTitle(), category)) {
            return mapping.findForward(ActionForwards.preview_failure.toString());
        }

        short parentActivity = category.getActivityId();
        int newActivityId;
        String activityNameHead = "Can view ";
        try {
            newActivityId = legacyRolesPermissionsDao.calculateDynamicActivityId();
            legacyRolesPermissionsDao.createActivityForReports(parentActivity, activityNameHead + uploadForm.getReportTitle());
        } catch (ActivityGeneratorException ex) {
            ActionErrors errors = new ActionErrors();
            errors.add(ex.getKey(), new ActionMessage(ex.getKey()));
            request.setAttribute(Globals.ERROR_KEY, errors);
            return mapping.findForward(ActionForwards.preview_failure.toString());
        }

        FormFile formFile = uploadForm.getFile();
        uploadFile(formFile);

        ReportsBO reportBO = createOrUpdateReport(category, newActivityId, uploadForm.getReportTitle(), Short
                .valueOf(uploadForm.getIsActive()), formFile.getFileName(), uploadForm.getIsDW());

        allowActivityPermission(reportBO, newActivityId);
        request.setAttribute("report", reportBO);

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    private void allowActivityPermission(ReportsBO reportBO, int newActivityId) throws ApplicationException {
        ActivityMapper.getInstance().getActivityMap().put(
                "/reportsUserParamsAction-loadAddList-" + reportBO.getReportId(), (short) newActivityId);
    }

    private static String getServletRoot(ActionServlet servlet) {
        return servlet.getServletContext().getRealPath("/");
    }

    public static String getUploadStorageDirectory() {
        String uploadsDir = MifosConfigurationManager.getInstance().getString("GeneralConfig.UploadStorageDirectory",
                "$HOME/.mifos/uploads");
        if (File.separatorChar == '\\') { // windows platform
            uploadsDir = uploadsDir.replaceAll("/", "\\\\");
        }
        int id = uploadsDir.indexOf("$HOME");
        if (id != -1) {
            uploadsDir = uploadsDir.substring(0, id) + System.getProperty("user.home") + uploadsDir.substring(id+5);
        }
        return uploadsDir;
    }

    public static String getCustomReportStorageDirectory() {
        return getUploadStorageDirectory().endsWith(File.separator) ?
                getUploadStorageDirectory() + "report" : getUploadStorageDirectory() + File.separator + "report";
    }

    private void uploadFile(FormFile formFile) throws FileNotFoundException, IOException {
        File dir = new File(getCustomReportStorageDirectory());
        dir.mkdirs();
        File file = new File(dir, formFile.getFileName());
        InputStream is = formFile.getInputStream();
        OutputStream os;
        /*
         * for test purposes, if the real path does not exist (if we're
         * operating outside a deployed environment) the file is just written to
         * a ByteArrayOutputStream which is not actually stored. !! This does
         * not produce any sort of file that can be retirieved. !! it only
         * allows us to perform the upload action.
         */
        if (getServletRoot(getServlet()) != null) {
            os = new FileOutputStream(file);
        } else {
            os = new ByteArrayOutputStream();
        }
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = is.read(buffer, 0, 4096)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        is.close();
        formFile.destroy();
    }

    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    public ActionForward getViewReportPage(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsAction:getViewReportsPage Method: ");
        StaticHibernateUtil.flushAndCloseSession();
        request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
                new ReportsPersistence().getAllReportCategories());
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtReportsUploadActionForm birtReportsUploadActionForm = (BirtReportsUploadActionForm) form;
        ReportsBO report = new ReportsPersistence().getReport(Short.valueOf(request.getParameter("reportId")));
        request.setAttribute(Constants.BUSINESS_KEY, report);
        birtReportsUploadActionForm.setReportTitle(report.getReportName());
        birtReportsUploadActionForm.setReportCategoryId(report.getReportsCategoryBO().getReportCategoryId().toString());
        birtReportsUploadActionForm.setIsActive(report.getIsActive().toString());
        request.getSession().setAttribute(ReportsConstants.LISTOFREPORTS,
                new ReportsPersistence().getAllReportCategories());
        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    public ActionForward editpreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
        ReportsPersistence rp = new ReportsPersistence();
        ReportsCategoryBO category = rp.getPersistentObject(ReportsCategoryBO.class, Short
                .valueOf(uploadForm.getReportCategoryId()));
        request.setAttribute("category", category);
        ReportsBO report = new ReportsPersistence().getReport(Short.valueOf(uploadForm.getReportId()));
        if (isReportInfoNotEdit(request, uploadForm, report)) {
            return mapping.findForward(ActionForwards.editpreview_failure.toString());
        } else if (!isReportItsSelf(uploadForm, report)
                && isReportAlreadyExist(request, uploadForm.getReportTitle(), category)) {
            return mapping.findForward(ActionForwards.editpreview_failure.toString());
        }
        return mapping.findForward(ActionForwards.editpreview_success.toString());
    }

    private boolean isReportInfoNotEdit(HttpServletRequest request, BirtReportsUploadActionForm form, ReportsBO report) {
        if (isReportItsSelf(form, report)) {
            if (form.getIsActive().equals(report.getIsActive().toString())
                    && StringUtils.isBlank(form.getFile().getFileName())) {
                ActionErrors errors = new ActionErrors();
                errors.add(ReportsConstants.ERROR_REPORTINFONOTEDIT, new ActionMessage(
                        ReportsConstants.ERROR_REPORTINFONOTEDIT));
                request.setAttribute(Globals.ERROR_KEY, errors);
                return true;
            }
        }
        return false;
    }

    private boolean isReportAlreadyExist(HttpServletRequest request, String reportName, ReportsCategoryBO categoryBO)
            throws Exception {
        for (ReportsBO report : new ReportsPersistence().getAllReports()) {
            if (reportName.equals(report.getReportName())
                    && categoryBO.getReportCategoryId().equals(report.getReportsCategoryBO().getReportCategoryId())) {
                ActionErrors errors = new ActionErrors();
                errors.add(ReportsConstants.ERROR_REPORTALREADYEXIST, new ActionMessage(
                        ReportsConstants.ERROR_REPORTALREADYEXIST));
                request.setAttribute(Globals.ERROR_KEY, errors);
                return true;
            }
        }
        return false;
    }

    private boolean isReportItsSelf(BirtReportsUploadActionForm form, ReportsBO report) {
        if (form.getReportTitle().equals(report.getReportName())
                && form.getReportCategoryId().equals(report.getReportsCategoryBO().getReportCategoryId().toString())) {
            return true;
        }
        return false;
    }

    public ActionForward editprevious(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
        ReportsPersistence rp = new ReportsPersistence();
        ReportsCategoryBO category = rp.getPersistentObject(ReportsCategoryBO.class, Short
                .valueOf(uploadForm.getReportCategoryId()));
        request.setAttribute("category", category);
        return mapping.findForward(ActionForwards.editprevious_success.toString());
    }

    public ActionForward editThenUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtReportsUploadActionForm uploadForm = (BirtReportsUploadActionForm) form;
        ReportsPersistence rp = new ReportsPersistence();
        ReportsCategoryBO category = rp.getPersistentObject(ReportsCategoryBO.class, Short
                .valueOf(uploadForm.getReportCategoryId()));
        ReportsBO reportBO = rp.getReport(Short.valueOf(uploadForm.getReportId()));
        ReportsJasperMap reportJasperMap = reportBO.getReportsJasperMap();
        if (!isReportItsSelf(uploadForm, reportBO)
                && isReportAlreadyExist(request, uploadForm.getReportTitle(), category)) {
            return mapping.findForward(ActionForwards.editpreview_failure.toString());
        } else if (isReportActivityIdNull(request, reportBO)) {
            return mapping.findForward(ActionForwards.create_failure.toString());
        }

        reportBO.setReportName(uploadForm.getReportTitle());
        reportBO.setIsActive(Short.valueOf(uploadForm.getIsActive()));
        reportBO.setReportsCategoryBO(category);
        rp.createOrUpdate(reportBO);
        // kim
        String activityNameHead = "Can view ";
        rp.updateLookUpValue(reportBO.getActivityId(), activityNameHead + uploadForm.getReportTitle());
        legacyRolesPermissionsDao.reparentActivityUsingHibernate(reportBO.getActivityId(), category.getActivityId());
        legacyRolesPermissionsDao.changeActivityMessage(reportBO.getActivityId(), Localization.ENGLISH_LOCALE_ID,
                "Can view " + reportBO.getReportName());

        FormFile formFile = uploadForm.getFile();
        if (StringUtils.isEmpty(formFile.getFileName())) {
            formFile.destroy();
        } else {
            reportJasperMap.setReportJasper(formFile.getFileName());
            rp.createOrUpdate(reportJasperMap);
            uploadFile(formFile);
        }
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    private boolean isReportActivityIdNull(HttpServletRequest request, ReportsBO reportBO) {
        if (null == reportBO.getActivityId()) {
            ActionErrors errors = new ActionErrors();
            errors.add(ReportsConstants.ERROR_REPORTACTIVITYIDISNULL, new ActionMessage(
                    ReportsConstants.ERROR_REPORTACTIVITYIDISNULL));
            request.setAttribute(Globals.ERROR_KEY, errors);
            return true;
        }
        return false;
    }

    public ActionForward downloadBirtReport(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("reportsBO",
                new ReportsPersistence().getReport(Short.valueOf(request.getParameter("reportId"))));
        return mapping.findForward(ActionForwards.download_success.toString());
    }

    private ReportsBO createOrUpdateReport(ReportsCategoryBO category, int newActivityId, String reportTitle,
            Short isActive, String fileName, Boolean isDW) throws PersistenceException {
        ReportsBO reportBO = new ReportsBO();
        reportBO.setReportName(reportTitle);
        reportBO.setReportsCategoryBO(category);
        reportBO.setActivityId((short) newActivityId);
        reportBO.setIsActive(isActive);
        reportBO.setIsDW(isDW);

        ReportsJasperMap reportsJasperMap = reportBO.getReportsJasperMap();
        reportsJasperMap.setReportJasper(fileName);
        reportBO.setReportsJasperMap(reportsJasperMap);

        new ReportsPersistence().createOrUpdate(reportBO);
        return reportBO;
    }
}