/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.office.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.business.service.OfficeHierarchyBusinessService;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.struts.actionforms.OffHierarchyActionForm;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class OffHierarchyAction extends BaseAction {

    public OffHierarchyAction() {
        super();
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return new OfficeHierarchyBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<OfficeLevelEntity> officeLevels = new OfficeHierarchyBusinessService().getOfficeLevels(getUserContext(
                request).getLocaleId());
        setConfiguredDataInForm((OffHierarchyActionForm) form, officeLevels);
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICE_LEVELS, officeLevels, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        updateConfiguredData((OffHierarchyActionForm) form, (List<OfficeLevelEntity>) SessionUtils.getAttribute(
                OfficeConstants.OFFICE_LEVELS, request));
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    private void setConfiguredDataInForm(OffHierarchyActionForm officeHierarchyActionForm,
            List<OfficeLevelEntity> officeLevels) throws PropertyNotFoundException {
        officeHierarchyActionForm.setHeadOffice(getStringValue(true));
        officeHierarchyActionForm.setBranchOffice(getStringValue(true));
        for (OfficeLevelEntity officeLevelEntity : officeLevels) {
            if (officeLevelEntity.getLevel().equals(OfficeLevel.REGIONALOFFICE)) {
                officeHierarchyActionForm.setRegionalOffice(getStringValue(officeLevelEntity.isConfigured()));
            }
            if (officeLevelEntity.getLevel().equals(OfficeLevel.SUBREGIONALOFFICE)) {
                officeHierarchyActionForm.setSubRegionalOffice(getStringValue(officeLevelEntity.isConfigured()));
            }
            if (officeLevelEntity.getLevel().equals(OfficeLevel.AREAOFFICE)) {
                officeHierarchyActionForm.setAreaOffice(getStringValue(officeLevelEntity.isConfigured()));
            }

        }
    }

    private void updateConfiguredData(OffHierarchyActionForm officeHierarchyActionForm,
            List<OfficeLevelEntity> officeLevels) throws PropertyNotFoundException, OfficeException {
        for (OfficeLevelEntity officeLevelEntity : officeLevels) {
            if (officeLevelEntity.getLevel().equals(OfficeLevel.REGIONALOFFICE)) {
                officeLevelEntity.update(officeHierarchyActionForm.getRegionalOfficeValue());
            }
            if (officeLevelEntity.getLevel().equals(OfficeLevel.SUBREGIONALOFFICE)) {
                officeLevelEntity.update(officeHierarchyActionForm.getSubRegionalOfficeValue());
            }
            if (officeLevelEntity.getLevel().equals(OfficeLevel.AREAOFFICE)) {
                officeLevelEntity.update(officeHierarchyActionForm.getAreaOfficeValue());
            }

        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        return mapping.findForward(method + "_failure");
    }
}
