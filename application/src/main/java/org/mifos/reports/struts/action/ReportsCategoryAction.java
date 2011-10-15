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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.Localization;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.business.service.ReportsBusinessService;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.reports.struts.actionforms.ReportsCategoryActionForm;
import org.mifos.reports.util.helpers.ReportsConstants;
import org.mifos.security.activity.ActivityGeneratorException;
import org.mifos.security.util.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportsCategoryAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ReportsCategoryAction.class);

    private ReportsBusinessService reportsBusinessService;

    public ReportsCategoryAction() {
        reportsBusinessService = new ReportsBusinessService();
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return reportsBusinessService;
    }

    public ActionForward loadDefineNewCategoryPage(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:loadDefineNewCategoryPage Method: ");
        ((ReportsCategoryActionForm) form).clear();
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:preview Method: ");
        ReportsCategoryActionForm defineCategoryForm = (ReportsCategoryActionForm) form;
        String categoryName = defineCategoryForm.getCategoryName();
        request.setAttribute("categoryName", categoryName);

        for (ReportsCategoryBO category : new ReportsPersistence().getAllReportCategories()) {
            if (categoryName.equals(category.getReportCategoryName())) {
                ActionErrors errors = new ActionErrors();
                errors.add(ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST, new ActionMessage(
                        ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST));
                request.setAttribute(Globals.ERROR_KEY, errors);
                return mapping.findForward(ActionForwards.preview_failure.toString());
            }
        }

        return mapping.findForward(ActionForwards.preview_success.toString());

    }

    public ActionForward addNewCategory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:addNewCategory Method: ");

        ReportsCategoryActionForm defineNewCategoryForm = (ReportsCategoryActionForm) form;
        String categoryName = defineNewCategoryForm.getCategoryName();
        ReportsCategoryBO reportsCategoryBO = new ReportsCategoryBO();

        int newActivityId;
        try {
            newActivityId = legacyRolesPermissionsDao.calculateDynamicActivityId();
        } catch (ActivityGeneratorException agex) {

            ActionErrors errors = new ActionErrors();
            errors.add(agex.getKey(), new ActionMessage(agex.getKey()));
            request.setAttribute(Globals.ERROR_KEY, errors);
            return mapping.findForward(ActionForwards.preview_failure.toString());
        }

        Short parentActivityId = SecurityConstants.REPORTS_MANAGEMENT;
        legacyRolesPermissionsDao.createActivityForReports(parentActivityId, categoryName);

        reportsCategoryBO.setActivityId((short) newActivityId);
        reportsCategoryBO.setReportCategoryName(categoryName);

        new ReportsPersistence().createOrUpdate(reportsCategoryBO);

        return mapping.findForward(ActionForwards.create_success.toString());
    }

    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    public ActionForward viewReportsCategory(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:viewReportsCategory Method: ");
        request.getSession().setAttribute(ReportsConstants.LISTOFREPORTCATEGORIES,
                new ReportsPersistence().getAllReportCategories());
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    public ActionForward confirmDeleteReportsCategory(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) {
        logger.debug("In ReportsCategoryAction:confirmDeleteReportsCategory Method: ");
        ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
        ReportsCategoryBO reportsCategoryBO = new ReportsPersistence().getReportCategoryByCategoryId(Short
                .valueOf(reportsCategoryActionForm.getCategoryId()));

        reportsCategoryActionForm.setCategoryName(reportsCategoryBO.getReportCategoryName());

        if (!isValidToDelete(request, reportsCategoryBO)) {
            return mapping.findForward(ActionForwards.confirm_delete.toString());
        }

        return mapping.findForward(ActionForwards.confirm_delete.toString());
    }

    private boolean isValidToDelete(HttpServletRequest request, ReportsCategoryBO reportsCategoryBO) {
        if (!reportsCategoryBO.getReportsSet().isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add(ReportsConstants.ERROR_CATEGORYHASREPORTS, new ActionMessage(
                    ReportsConstants.ERROR_CATEGORYHASREPORTS));
            request.setAttribute(Globals.ERROR_KEY, errors);
            return false;
        }
        return true;
    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:edit Method: ");
        ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
        String reportCategoryId = request.getParameter("categoryId");
        ReportsCategoryBO reportCategory = new ReportsPersistence().getReportCategoryByCategoryId(Short
                .valueOf(reportCategoryId));
        reportsCategoryActionForm.setCategoryName(reportCategory.getReportCategoryName());
        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    public ActionForward editPreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:editPreview Method: ");
        ReportsCategoryActionForm defineCategoryForm = (ReportsCategoryActionForm) form;
        String inputCategoryName = defineCategoryForm.getCategoryName();
        short reportCategoryId = defineCategoryForm.getCategoryId();
        ReportsCategoryBO reportCategory = new ReportsPersistence().getReportCategoryByCategoryId(reportCategoryId);
        if (isReportCategoryNameNotEdit(request, inputCategoryName, reportCategory)) {
            return mapping.findForward(ActionForwards.editPreview_failure.toString());
        } else if (isReportCategoryNameAlreadyExist(request, inputCategoryName)) {
            return mapping.findForward(ActionForwards.editPreview_failure.toString());
        }

        return mapping.findForward(ActionForwards.editpreview_success.toString());
    }

    private boolean isReportCategoryNameAlreadyExist(HttpServletRequest request, String inputCategoryName) {
        for (ReportsCategoryBO category : new ReportsPersistence().getAllReportCategories()) {
            if (category.getReportCategoryName().equals(inputCategoryName)) {
                ActionErrors errors = new ActionErrors();
                errors.add(ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST, new ActionMessage(
                        ReportsConstants.ERROR_CATEGORYNAMEALREADYEXIST));
                request.setAttribute(Globals.ERROR_KEY, errors);
                return true;
            }
        }
        return false;
    }

    private boolean isReportCategoryNameNotEdit(HttpServletRequest request, String inputCategoryName,
            ReportsCategoryBO reportCategory) {
        if (inputCategoryName.equals(reportCategory.getReportCategoryName())) {
            ActionErrors errors = new ActionErrors();
            errors.add(ReportsConstants.ERROR_CATEGORYNAMENOTEDIT, new ActionMessage(
                    ReportsConstants.ERROR_CATEGORYNAMENOTEDIT));
            request.setAttribute(Globals.ERROR_KEY, errors);
            return true;
        }
        return false;
    }

    public ActionForward deleteReportsCategory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:deleteReportsCategory Method: ");
        ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
        ReportsCategoryBO reportsCategoryBO = new ReportsPersistence().getReportCategoryByCategoryId(Short
                .valueOf(reportsCategoryActionForm.getCategoryId()));

        if (!isValidToDelete(request, reportsCategoryBO)) {
            return mapping.findForward(ActionForwards.confirm_delete.toString());
        }
        new ReportsPersistence().delete(reportsCategoryBO);

        request.getSession().setAttribute(ReportsConstants.LISTOFREPORTCATEGORIES,
                new ReportsPersistence().getAllReportCategories());

        return mapping.findForward(ActionForwards.delete_success.toString());
    }

    public ActionForward editThenSubmit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("In ReportsCategoryAction:editThenSubmit Method: ");
        ReportsCategoryActionForm reportsCategoryActionForm = (ReportsCategoryActionForm) form;
        short reportCategoryId = reportsCategoryActionForm.getCategoryId();
        String inputCategoryName = reportsCategoryActionForm.getCategoryName();
        ReportsCategoryBO reportsCategoryBO = new ReportsPersistence().getReportCategoryByCategoryId(reportCategoryId);
        if (isReportCategoryNameAlreadyExist(request, inputCategoryName)) {
            return mapping.findForward(ActionForwards.editPreview_failure.toString());
        }
        reportsCategoryBO.setReportCategoryName(inputCategoryName);
        ReportsPersistence rPersistence = new ReportsPersistence();
        rPersistence.createOrUpdate(reportsCategoryBO);
        // update cache
        Short activityId = reportsCategoryBO.getActivityId();
        rPersistence.updateLookUpValue(activityId, inputCategoryName);

        legacyRolesPermissionsDao.changeActivityMessage(reportsCategoryBO.getActivityId(),
                Localization.ENGLISH_LOCALE_ID, reportsCategoryBO.getReportCategoryName());
        return mapping.findForward(ActionForwards.create_success.toString());
    }
}