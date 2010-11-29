/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.application.questionnaire.migration.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.persistence.Upgrade1290720085;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MigrateAction extends BaseAction {

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("migrateAction");
        security.allow("get", SecurityConstants.VIEW);
        security.allow("migrateSurveys", SecurityConstants.VIEW);
        security.allow("migrateAdditionalFields", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward migrateSurveys(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Upgrade1290720085 upgrade1290720085 = new Upgrade1290720085();
        ServletContext servletContext = request.getSession().getServletContext();
        upgrade1290720085.setUpgradeContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
        upgrade1290720085.migrateSurveys();
        return mapping.findForward(ActionForwards.migrate_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward migrateAdditionalFields(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Upgrade1290720085 upgrade1290720085 = new Upgrade1290720085();
        ServletContext servletContext = request.getSession().getServletContext();
        upgrade1290720085.setUpgradeContext(WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext));
        upgrade1290720085.migrateAdditionalFields();
        return mapping.findForward(ActionForwards.migrate_success.toString());
    }
}
