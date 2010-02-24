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

package org.mifos.reports.admindocuments.struts.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.upload.FormFile;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.reports.admindocuments.business.AdminDocAccStateMixBO;
import org.mifos.reports.admindocuments.business.AdminDocumentBO;
import org.mifos.reports.admindocuments.persistence.AdminDocAccStateMixPersistence;
import org.mifos.reports.admindocuments.persistence.AdminDocumentPersistence;
import org.mifos.reports.admindocuments.struts.actionforms.BirtAdminDocumentUploadActionForm;
import org.mifos.reports.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;

import org.mifos.reports.business.service.ReportsBusinessService;

import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;

import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.config.ConfigurationManager;

public class BirtAdminDocumentUploadAction extends BaseAction {

    private ReportsBusinessService reportsBusinessService;

    public BirtAdminDocumentUploadAction() {
        reportsBusinessService = new ReportsBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("birtAdminDocumentUploadAction");
        security.allow("getBirtAdminDocumentUploadPage", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("preview", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("loadProductInstance", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);

        security.allow("getProductTypes", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("previous", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("upload", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("getViewBirtAdminDocumentPage", SecurityConstants.CAN_VIEW_ADMIN_DOCUMENTS);
        security.allow("edit", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("editpreview", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("editprevious", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("editThenUpload", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("downloadAdminDocument", SecurityConstants.DOWNLOAD_REPORT_TEMPLATE);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getBirtAdminDocumentUploadPage(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        uploadForm.clear();
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
                getProductTypes(getUserContext(request)), request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private List<ProductTypeEntity> getProductTypes(UserContext userContext) throws Exception {
        List<ProductTypeEntity> productTypeList = ((ProductMixBusinessService) ServiceFactory.getInstance()
                .getBusinessService(BusinessServiceName.PrdMix)).getProductTypes();
        for (ProductTypeEntity productTypeEntity : productTypeList)
            productTypeEntity.setUserContext(userContext);
        return productTypeList;
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return reportsBusinessService;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadProductInstance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        SessionUtils.removeAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.SELECTEDACCOUNTSTATUS, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.STATUS_LIST, request);
        if (StringUtils.isNotBlank(uploadForm.getAccountTypeId())) {
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS,
                    new AccountBusinessService().retrieveAllActiveAccountStateList(AccountTypes.getAccountType(Short
                            .valueOf(uploadForm.getAccountTypeId()))), request);
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    private void updateSelectedStatus(HttpServletRequest request, BirtAdminDocumentUploadActionForm uploadForm)
            throws PageExpiredException, ServiceException {
        List<AccountStateEntity> selectList = new ArrayList<AccountStateEntity>();
        if (uploadForm.getStatusList() != null) {
            List<AccountStateEntity> masterList = new AccountBusinessService()
                    .retrieveAllActiveAccountStateList(AccountTypes.getAccountType(Short.valueOf(uploadForm
                            .getAccountTypeId())));
            for (AccountStateEntity product : masterList) {
                for (String productStatusId : uploadForm.getStatusList()) {
                    if (productStatusId != null
                            && product.getId().intValue() == Integer.valueOf(productStatusId).intValue()) {

                        selectList.add(product);

                    }
                }
            }
            SessionUtils.setCollectionAttribute("SelectedStatus", selectList, request);

        }

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        if (uploadForm.getAccountTypeId().equals(ProductType.LOAN.getValue().toString()))
            uploadForm.setAccountTypeName("LOAN");
        else
            uploadForm.setAccountTypeName("SAVINGS");

        updateSelectedStatus(request, uploadForm);

        return mapping.findForward(ActionForwards.preview_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editpreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        if (uploadForm.getAccountTypeId().equals(ProductType.LOAN.getValue().toString()))
            uploadForm.setAccountTypeName("LOAN");
        else
            uploadForm.setAccountTypeName("SAVINGS");

        updateSelectedStatus(request, uploadForm);

        return mapping.findForward("edit_preview_success");

    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        List<AccountStateEntity> masterList = (List<AccountStateEntity>) SessionUtils.getAttribute("SelectedStatus",
                request);

        FormFile formFile = uploadForm.getFile();
        uploadFile(formFile);
        AdminDocumentBO admindocBO = createOrUpdateAdminDocument(uploadForm.getAdminiDocumentTitle(), Short
                .valueOf("1"), formFile.getFileName());
        AdminDocAccStateMixBO admindocaccstatemixBO = new AdminDocAccStateMixBO();
        for (AccountStateEntity acc : masterList) {
            admindocaccstatemixBO = new AdminDocAccStateMixBO();
            admindocaccstatemixBO.setAccountStateID(acc);
            admindocaccstatemixBO.setAdminDocumentID(admindocBO);
            new AdminDocAccStateMixPersistence().createOrUpdate(admindocaccstatemixBO);

        }
        request.setAttribute("report", admindocBO);
        return getViewBirtAdminDocumentPage(mapping, form, request, response);

    }
    
    private static String getServletRoot(ActionServlet servlet) {
        return servlet.getServletContext().getRealPath("/");
    }
    
    public static String getUploadStorageDirectory() {
        return ConfigurationManager.getInstance().getString("GeneralConfig.UploadStorageDirectory",
                "$HOME/.mifos/uploads").replaceFirst("\\$HOME", System.getProperty("user.home"));
    }

    public static String getAdminDocumentStorageDirectory() {
        return getUploadStorageDirectory().endsWith("/") ?
                getUploadStorageDirectory() + "adminReport" : getUploadStorageDirectory() + "/adminReport";
    }

    private void uploadFile(FormFile formFile) throws FileNotFoundException, IOException {
        File dir = new File(getAdminDocumentStorageDirectory());
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
        if (getServletRoot(getServlet()) != null)
            os = new FileOutputStream(file);
        else
            os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = is.read(buffer, 0, 4096)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        is.close();
        formFile.destroy();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    public ActionForward getViewBirtAdminDocumentPage(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(AdminDocumentsContants.LISTOFADMINISTRATIVEDOCUMENTS,
                new AdminDocumentPersistence().getAllAdminDocuments());
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        AdminDocumentBO businessKey = null;

        BirtAdminDocumentUploadActionForm birtReportsUploadActionForm = (BirtAdminDocumentUploadActionForm) form;
        List<AdminDocAccStateMixBO> admindoclist = new AdminDocAccStateMixPersistence().getMixByAdminDocuments(Short
                .valueOf(request.getParameter("admindocId")));
        if ((admindoclist != null) && (!admindoclist.isEmpty())) {
            SessionUtils.setAttribute("admindocId", admindoclist.get(0).getAdminDocumentID().getAdmindocId(), request);

            birtReportsUploadActionForm.setAdminiDocumentTitle(admindoclist.get(0).getAdminDocumentID()
                    .getAdminDocumentName());
            birtReportsUploadActionForm.setAccountTypeId(admindoclist.get(0).getAccountStateID().getPrdType()
                    .getProductTypeID().toString());
            businessKey = admindoclist.get(0).getAdminDocumentID();
        }

        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST,
                getProductTypes(getUserContext(request)), request);
        List<AccountStateEntity> selectedlist = new ArrayList<AccountStateEntity>();
        for (AdminDocAccStateMixBO admindoc : admindoclist) {
            selectedlist.add(admindoc.getAccountStateID());
        }

        List<AccountStateEntity> masterList = null;
        if (birtReportsUploadActionForm.getAccountTypeId() != null) {
            masterList = new AccountBusinessService().retrieveAllActiveAccountStateList(AccountTypes
                    .getAccountType(Short.valueOf(birtReportsUploadActionForm.getAccountTypeId())));
            masterList.removeAll(selectedlist);
        }

        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS, masterList, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SELECTEDACCOUNTSTATUS, selectedlist, request);

        request.setAttribute(Constants.BUSINESS_KEY, businessKey);

        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward editThenUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        List<AccountStateEntity> masterList = (List<AccountStateEntity>) SessionUtils.getAttribute("SelectedStatus",
                request);
        FormFile formFile = uploadForm.getFile();
        boolean newFile = false;

        if (StringUtils.isEmpty(formFile.getFileName())) {
            formFile.destroy();
        }
        else {
            uploadFile(formFile);
            newFile = true;
        }

        AdminDocumentBO admindoc = new AdminDocumentPersistence().getAdminDocumentById(Short.valueOf(SessionUtils
                .getAttribute("admindocId", request).toString()));
        admindoc.setAdminDocumentName(uploadForm.getAdminiDocumentTitle());
        admindoc.setIsActive(Short.valueOf("1"));
        if (newFile) {
            admindoc.setAdminDocumentIdentifier(formFile.getFileName());
        }
        new AdminDocumentPersistence().createOrUpdate(admindoc);
        List<AdminDocAccStateMixBO> admindoclist = new AdminDocAccStateMixPersistence().getMixByAdminDocuments(Short
                .valueOf(SessionUtils.getAttribute("admindocId", request).toString()));
        for (AdminDocAccStateMixBO temp : admindoclist) {
            new AdminDocAccStateMixPersistence().delete(temp);
        }

        AdminDocAccStateMixBO admindocaccstatemixBO = new AdminDocAccStateMixBO();
        for (AccountStateEntity acc : masterList) {
            admindocaccstatemixBO = new AdminDocAccStateMixBO();
            admindocaccstatemixBO.setAccountStateID(acc);
            admindocaccstatemixBO.setAdminDocumentID(admindoc);
            new AdminDocAccStateMixPersistence().createOrUpdate(admindocaccstatemixBO);

        }
        return getViewBirtAdminDocumentPage(mapping, form, request, response);
    }

    public ActionForward downloadAdminDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("reportsBO",
                new AdminDocumentPersistence().getAdminDocumentById(Short.valueOf(request.getParameter("admindocId"))));
        return mapping.findForward(ActionForwards.download_success.toString());
    }

    private AdminDocumentBO createOrUpdateAdminDocument(String admindocTitle, Short isActive, String fileName)
            throws PersistenceException {
        AdminDocumentBO admindocBO = new AdminDocumentBO();
        admindocBO.setAdminDocumentName(admindocTitle);
        admindocBO.setIsActive(isActive);
        admindocBO.setAdminDocumentIdentifier(fileName);
        new AdminDocumentPersistence().createOrUpdate(admindocBO);
        return admindocBO;
    }
}
