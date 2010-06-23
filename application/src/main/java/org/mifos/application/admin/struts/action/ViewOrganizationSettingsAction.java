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

package org.mifos.application.admin.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.admin.business.service.ViewOrganizationSettingsServiceFacadeWebTier;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Properties;

public class ViewOrganizationSettingsAction extends BaseAction {
    /** Name of request attribute where organization settings are stored. */
    public static final String ORGANIZATION_SETTINGS = "orgSettings";

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("viewOrganizationSettingsAction");
        security.allow("get", SecurityConstants.CAN_VIEW_SYSTEM_INFO);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade = new ViewOrganizationSettingsServiceFacadeWebTier();

        final HttpSession session = request.getSession();

        Properties orgSettings = viewOrganizationSettingsServiceFacade.getOrganizationSettings(session);
        request.setAttribute(ORGANIZATION_SETTINGS, orgSettings);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return null;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }
}
