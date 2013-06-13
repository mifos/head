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
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.productsmix.business.service.ProductMixBusinessService;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.reports.admindocuments.business.AdminDocAccActionMixBO;
import org.mifos.reports.admindocuments.business.AdminDocAccStateMixBO;
import org.mifos.reports.admindocuments.business.AdminDocumentBO;
import org.mifos.reports.admindocuments.struts.actionforms.BirtAdminDocumentUploadActionForm;
import org.mifos.reports.admindocuments.util.helpers.AdminDocumentsContants;
import org.mifos.reports.business.service.ReportsBusinessService;

public class BirtAdminDocumentUploadAction extends BaseAction {

    private ReportsBusinessService reportsBusinessService;

    public BirtAdminDocumentUploadAction() {
        reportsBusinessService = new ReportsBusinessService();
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getBirtAdminDocumentUploadPage(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        uploadForm.clear();
        List<ProductTypeEntity> productTypes = getProductTypes();
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, productTypes, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    private List<ProductTypeEntity> getProductTypes() throws Exception {
        List<ProductTypeEntity> productTypeList = new ProductMixBusinessService().getProductTypes();
        return productTypeList;
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return reportsBusinessService;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadProductInstance(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        SessionUtils.removeAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.SELECTEDACCOUNTSTATUS, request);
        SessionUtils.removeAttribute(ProductDefinitionConstants.STATUS_LIST, request);
        if (StringUtils.isNotBlank(uploadForm.getAccountTypeId()) && Short
                .valueOf(uploadForm.getAccountTypeId()).shortValue() <= 2) {
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS,
                    new AccountBusinessService().retrieveAllActiveAccountStateList(AccountTypes.getAccountType(Short
                            .valueOf(uploadForm.getAccountTypeId()))), request);
        } else if (StringUtils.isNotBlank(uploadForm.getAccountTypeId())) {
            SessionUtils.setCollectionAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS,
                    getAvailableLoanTransactions((Short)SessionUtils.getAttribute("CURRENT_LOCALE_ID", request)), request);
        }
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    /**
     * This is hardcode settings for extended admin documents functionality.
     */
    private List<AccountActionEntity> getAvailableLoanTransactions(Short currentLocaleId) throws ServiceException{
        List<AccountActionEntity> loanAccountActionTypes = new ArrayList<AccountActionEntity>();
        loanAccountActionTypes.add(new AccountBusinessService().getAccountAction(AccountActionTypes.LOAN_REPAYMENT.getValue(), currentLocaleId ));
        loanAccountActionTypes.add(new AccountBusinessService().getAccountAction(AccountActionTypes.DISBURSAL.getValue(), currentLocaleId ));
        loanAccountActionTypes.add(new AccountBusinessService().getAccountAction(AccountActionTypes.CUSTOMER_ACCOUNT_REPAYMENT.getValue(), currentLocaleId ));
        return loanAccountActionTypes;
    }

    @TransactionDemarcate(joinToken = true)
    private void updateSelectedStatus(HttpServletRequest request, BirtAdminDocumentUploadActionForm uploadForm)
            throws PageExpiredException, ServiceException {
        List selectList = null;
        if (uploadForm.getStatusList() != null) {
            Short accountTypeId = Short.valueOf(uploadForm
                    .getAccountTypeId());
            if (accountTypeId.shortValue() <= 2){
                selectList = new ArrayList<AccountStateEntity>();
                List<AccountStateEntity> masterList = new AccountBusinessService()
                        .retrieveAllActiveAccountStateList(AccountTypes.getAccountType(accountTypeId));
                for (AccountStateEntity product : masterList) {
                    for (String productStatusId : uploadForm.getStatusList()) {
                        if (productStatusId != null
                                && product.getId().intValue() == Integer.valueOf(productStatusId).intValue()) {

                            selectList.add(product);

                        }
                    }
                }
            } else {
                Short currentLocaleId = (Short)SessionUtils.getAttribute("CURRENT_LOCALE_ID", request);
                selectList = new ArrayList<AccountActionEntity>();
                List<AccountActionEntity> masterList = getAvailableLoanTransactions(currentLocaleId);
                for (String accountActionId : uploadForm.getStatusList()){
                    AccountActionTypes accountActionType = AccountActionTypes.fromInt(Integer.valueOf(accountActionId));
                    AccountActionEntity accountAction = new AccountBusinessService().getAccountAction(accountActionType.getValue(), currentLocaleId);
                    if ( masterList.contains(accountAction) ){
                        selectList.add(accountAction);
                    }
                }
            }
        }
        SessionUtils.setCollectionAttribute("SelectedStatus", selectList, request);

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        if (uploadForm.getAccountTypeId().equals(ProductType.LOAN.getValue().toString())) {
            uploadForm.setAccountTypeName("LOAN");
        } else if (uploadForm.getAccountTypeId().equals(ProductType.SAVINGS.getValue().toString())){
            uploadForm.setAccountTypeName("SAVINGS");
        } else {
            uploadForm.setAccountTypeName("TRANSACTIONS PAYMENTS");
        }

        updateSelectedStatus(request, uploadForm);

        return mapping.findForward(ActionForwards.preview_success.toString());

    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward editpreview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        if (uploadForm.getAccountTypeId().equals(ProductType.LOAN.getValue().toString())) {
            uploadForm.setAccountTypeName("LOAN");
        } else if (uploadForm.getAccountTypeId().equals(ProductType.SAVINGS.getValue().toString())){
            uploadForm.setAccountTypeName("SAVINGS");
        } else {
            uploadForm.setAccountTypeName("TRANSACTIONS PAYMENTS");
        }

        updateSelectedStatus(request, uploadForm);

        return mapping.findForward("edit_preview_success");

    }

    @SuppressWarnings("unchecked")
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;

        FormFile formFile = uploadForm.getFile();
        uploadFile(formFile);
        AdminDocumentBO admindocBO = createOrUpdateAdminDocument(uploadForm.getAdminiDocumentTitle(), Short
                .valueOf("1"), formFile.getFileName());
        AdminDocAccStateMixBO admindocaccstatemixBO = new AdminDocAccStateMixBO();
        if (Short.valueOf(uploadForm.getAccountTypeId()).shortValue() <= 2){
            List<AccountStateEntity> masterList = (List<AccountStateEntity>) SessionUtils.getAttribute("SelectedStatus",
                    request);
            for (AccountStateEntity acc : masterList) {
                admindocaccstatemixBO = new AdminDocAccStateMixBO();
                admindocaccstatemixBO.setAccountStateID(acc);
                admindocaccstatemixBO.setAdminDocumentID(admindocBO);
                legacyAdminDocAccStateMixDao.createOrUpdate(admindocaccstatemixBO);

            }
        } else {
            List<AccountActionEntity> masterList = (List<AccountActionEntity>) SessionUtils.getAttribute("SelectedStatus",
                    request);
            for (AccountActionEntity accountActionEntity : masterList) {
                AdminDocAccActionMixBO adminDocAccActionMixBO = new AdminDocAccActionMixBO();
                adminDocAccActionMixBO.setAccountAction(accountActionEntity);
                adminDocAccActionMixBO.setAdminDocument(admindocBO);
                legacyAdminDocumentDao.createOrUpdate(adminDocAccActionMixBO);
            }
        }
        request.setAttribute("report", admindocBO);
        return getViewBirtAdminDocumentPage(mapping, form, request, response);

    }

    private static String getServletRoot(ActionServlet servlet) {
        return servlet.getServletContext().getRealPath("/");
    }

    private void uploadFile(FormFile formFile) throws FileNotFoundException, IOException {
    	
        File dir = new File(viewOrganizationSettingsServiceFacade.getAdminDocumentStorageDirectory());
        dir.mkdirs();
        File file = new File(dir, formFile.getFileName());
        InputStream is = formFile.getInputStream();
        OutputStream os;
        /*
         * for test purposes, if the real path does not exist (if we're operating outside a deployed environment) the
         * file is just written to a ByteArrayOutputStream which is not actually stored. !! This does not produce any
         * sort of file that can be retirieved. !! it only allows us to perform the upload action.
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

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }

    public ActionForward getViewBirtAdminDocumentPage(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(AdminDocumentsContants.LISTOFADMINISTRATIVEDOCUMENTS,
                legacyAdminDocumentDao.getAllAdminDocuments());
        ((BirtAdminDocumentUploadActionForm) form).clear();
        return mapping.findForward(ActionForwards.get_success.toString());
    }
    
    public ActionForward getFileNotFoundPage(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
            HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response) {
        return mapping.findForward(ActionForwards.download_failure.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        BirtAdminDocumentUploadActionForm birtReportsUploadActionForm = (BirtAdminDocumentUploadActionForm) form;

        List masterList = null;
        List selectedlist = null;

        AdminDocumentBO adminDocumentBO = this.legacyAdminDocumentDao.getAdminDocumentById(Short
                .valueOf(request.getParameter("admindocId")));
        List<AdminDocAccStateMixBO> admindoclist = legacyAdminDocAccStateMixDao.getMixByAdminDocuments(Short
                .valueOf(request.getParameter("admindocId")));
        if (admindoclist != null && !admindoclist.isEmpty()){
            SessionUtils.setAttribute("admindocId", adminDocumentBO.getAdmindocId(), request);

            birtReportsUploadActionForm.setAdminiDocumentTitle(adminDocumentBO.getAdminDocumentName());
            birtReportsUploadActionForm.setAccountTypeId(admindoclist.get(0).getAccountStateID().getPrdType()
                    .getProductTypeID().toString());
            birtReportsUploadActionForm.setIsActive(adminDocumentBO.getIsActive().toString());

            selectedlist = new ArrayList<AccountStateEntity>();
            for (AdminDocAccStateMixBO admindoc : admindoclist) {
                selectedlist.add(admindoc.getAccountStateID());
            }
            if (birtReportsUploadActionForm.getAccountTypeId() != null) {
                Short accountTypeId = Short.valueOf(birtReportsUploadActionForm.getAccountTypeId());
                masterList = new AccountBusinessService().retrieveAllActiveAccountStateList(AccountTypes
                        .getAccountType(accountTypeId));
                masterList.removeAll(selectedlist);
            }
        } else if (adminDocumentBO != null){
            List<AdminDocAccActionMixBO> adminDocAccActionMixList = legacyAdminDocAccStateMixDao.getAccActionMixByAdminDocument(Short
                    .valueOf(request.getParameter("admindocId")));

            SessionUtils.setAttribute("admindocId", adminDocumentBO.getAdmindocId(), request);

            birtReportsUploadActionForm.setAdminiDocumentTitle(adminDocumentBO.getAdminDocumentName());
            birtReportsUploadActionForm.setAccountTypeId("3");
            birtReportsUploadActionForm.setIsActive(adminDocumentBO.getIsActive().toString());
            
            masterList = getAvailableLoanTransactions((Short)SessionUtils.getAttribute("CURRENT_LOCALE_ID", request));
            if ((adminDocAccActionMixList != null) && (!adminDocAccActionMixList.isEmpty())) {
                selectedlist = new ArrayList<AccountActionEntity>();
                for (AdminDocAccActionMixBO admindoc : adminDocAccActionMixList) {
                    selectedlist.add(admindoc.getAccountAction());
                }

                masterList.removeAll(selectedlist);
            }
        }

        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.AVAILABLEACCOUNTSTATUS, masterList, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.SELECTEDACCOUNTSTATUS, selectedlist, request);
        SessionUtils.setCollectionAttribute(ProductDefinitionConstants.PRODUCTTYPELIST, getProductTypes(), request);

        request.setAttribute(Constants.BUSINESS_KEY, adminDocumentBO);
        birtReportsUploadActionForm.setStatusList(null);

        return mapping.findForward(ActionForwards.edit_success.toString());
    }

    @SuppressWarnings("unchecked")
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward editThenUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BirtAdminDocumentUploadActionForm uploadForm = (BirtAdminDocumentUploadActionForm) form;
        FormFile formFile = uploadForm.getFile();
        boolean newFile = false;

        if (StringUtils.isEmpty(formFile.getFileName())) {
            formFile.destroy();
        } else {
            uploadFile(formFile);
            newFile = true;
        }

        AdminDocumentBO admindoc = legacyAdminDocumentDao.getAdminDocumentById(Short.valueOf(SessionUtils
                .getAttribute("admindocId", request).toString()));
        admindoc.setAdminDocumentName(uploadForm.getAdminiDocumentTitle());
        admindoc.setIsActive(Short.valueOf(uploadForm.getIsActive()));
        if (newFile) {
            admindoc.setAdminDocumentIdentifier(formFile.getFileName());
        }
        legacyAdminDocumentDao.createOrUpdate(admindoc);

        if (Short.valueOf(uploadForm.getAccountTypeId()).shortValue() <= 2){
            List<AccountStateEntity> masterList = (List<AccountStateEntity>) SessionUtils.getAttribute("SelectedStatus",
                    request);
            List<AdminDocAccStateMixBO> admindoclist = legacyAdminDocAccStateMixDao.getMixByAdminDocuments(Short
                    .valueOf(SessionUtils.getAttribute("admindocId", request).toString()));
            for (AdminDocAccStateMixBO temp : admindoclist) {
                legacyAdminDocAccStateMixDao.delete(temp);
            }

            AdminDocAccStateMixBO admindocaccstatemixBO = new AdminDocAccStateMixBO();
            for (AccountStateEntity acc : masterList) {
                admindocaccstatemixBO = new AdminDocAccStateMixBO();
                admindocaccstatemixBO.setAccountStateID(acc);
                admindocaccstatemixBO.setAdminDocumentID(admindoc);
                legacyAdminDocAccStateMixDao.createOrUpdate(admindocaccstatemixBO);

            }
        } else {
            List<AccountActionEntity> masterList = (List<AccountActionEntity>) SessionUtils.getAttribute("SelectedStatus",
                    request);
            List<AdminDocAccActionMixBO> admindoclist = legacyAdminDocAccStateMixDao.getAccActionMixByAdminDocument(Short
                    .valueOf(SessionUtils.getAttribute("admindocId", request).toString()));
            for (AdminDocAccActionMixBO temp : admindoclist) {
                legacyAdminDocAccStateMixDao.delete(temp);
            }
            
            AdminDocAccActionMixBO adminDocAccActionMixBO = new AdminDocAccActionMixBO();
            for (AccountActionEntity acc : masterList) {
                adminDocAccActionMixBO = new AdminDocAccActionMixBO();
                adminDocAccActionMixBO.setAccountAction(acc);
                adminDocAccActionMixBO.setAdminDocument(admindoc);
                legacyAdminDocAccStateMixDao.createOrUpdate(adminDocAccActionMixBO);

            }
        }
        return getViewBirtAdminDocumentPage(mapping, form, request, response);
    }

    public ActionForward downloadAdminDocument(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("reportsBO",
                legacyAdminDocumentDao.getAdminDocumentById(Short.valueOf(request.getParameter("admindocId"))));
        return mapping.findForward(ActionForwards.download_success.toString());
    }

    private AdminDocumentBO createOrUpdateAdminDocument(String admindocTitle, Short isActive, String fileName)
            throws PersistenceException {
        AdminDocumentBO admindocBO = new AdminDocumentBO();
        admindocBO.setAdminDocumentName(admindocTitle);
        admindocBO.setIsActive(isActive);
        admindocBO.setAdminDocumentIdentifier(fileName);
        legacyAdminDocumentDao.createOrUpdate(admindocBO);
        return admindocBO;
    }
}