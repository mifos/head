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

package org.mifos.application.rolesandpermission.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.struts.actionforms.RolesPermissionsActionForm;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class RolesPermissionsAction extends BaseAction {

    @Override
    protected BusinessService getService() throws ServiceException {
        return ServiceFactory.getInstance().getBusinessService(BusinessServiceName.RolesPermissions);
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("rolesPermission");
        security.allow("viewRoles", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.ROLES_CREATE_ROLES);
        security.allow("create", SecurityConstants.ROLES_CREATE_ROLES);
        security.allow("manage", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.ROLES_EDIT_ROLES);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.ROLES_DELETE_ROLES);
        security.allow("delete", SecurityConstants.ROLES_DELETE_ROLES);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewRoles(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        doCleanUp((RolesPermissionsActionForm) form);
        List<RoleBO> roles = ((RolesPermissionsBusinessService) getService()).getRoles();
        SessionUtils.setCollectionAttribute(RolesAndPermissionConstants.ROLES, roles, request);
        return mapping.findForward(ActionForwards.viewRoles_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        doCleanUp((RolesPermissionsActionForm) form);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, null, request);
        SessionUtils.setCollectionAttribute(RolesAndPermissionConstants.ACTIVITYLIST,
                ((RolesPermissionsBusinessService) getService()).getActivities(), request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        List<ActivityEntity> activities = (List<ActivityEntity>) SessionUtils.getAttribute(
                RolesAndPermissionConstants.ACTIVITYLIST, request);
        RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) form;
        RoleBO roleBO = new RoleBO(userContext, rolesPermissionsActionForm.getName(), getActivities(activities,
                rolesPermissionsActionForm.getActivities()));
        roleBO.save();
        AuthorizationManager.getInstance().addRole(roleBO);
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) form;
        SessionUtils.setCollectionAttribute(RolesAndPermissionConstants.ACTIVITYLIST,
                ((RolesPermissionsBusinessService) getService()).getActivities(), request);
        RoleBO role = ((RolesPermissionsBusinessService) getService()).getRole(Short.valueOf(rolesPermissionsActionForm
                .getId()));
        rolesPermissionsActionForm.setName(role.getName());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, role, request);
        return mapping.findForward(ActionForwards.manage_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) form;
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request
                .getSession());
        RoleBO roleBO = (RoleBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        List<ActivityEntity> activities = (List<ActivityEntity>) SessionUtils.getAttribute(
                RolesAndPermissionConstants.ACTIVITYLIST, request);
        roleBO.update(userContext.getId(), rolesPermissionsActionForm.getName(), getActivities(activities,
                rolesPermissionsActionForm.getActivities()));
        AuthorizationManager.getInstance().updateRole(roleBO);
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) form;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, ((RolesPermissionsBusinessService) getService())
                .getRole(Short.valueOf(rolesPermissionsActionForm.getId())), request);
        return mapping.findForward(ActionForwards.preview_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RoleBO role = (RoleBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        RoleBO roleBO = ((RolesPermissionsBusinessService) getService()).getRole(role.getId());
        roleBO.setVersionNo(role.getVersionNo());
        roleBO.delete();
        AuthorizationManager.getInstance().deleteRole(roleBO);
        role = null;
        return mapping.findForward(ActionForwards.delete_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    private List<ActivityEntity> getActivities(List<ActivityEntity> activityList, Map<String, String> activities) {
        List<ActivityEntity> newActivityList = new ArrayList<ActivityEntity>();
        List<Short> ids = new ArrayList<Short>();
        Set<String> keys = activities.keySet();
        for (String string : keys) {
            /*
             * We need to collect the id's of all the checked activities when we
             * created the ui. We have given unique name to leaf activities and
             * "chekbox" to non leaf activities .Now we are trying to get the
             * id's of checked leafs only
             */
            if (!activities.get(string).equalsIgnoreCase("checkbox") && !activities.get(string).equalsIgnoreCase("")) {
                Short activityId = Short.parseShort(activities.get(string));
                ids.add(activityId);
            }
        }
        for (ActivityEntity activityEntity : activityList) {
            if (ids.contains(activityEntity.getId()))
                newActivityList.add(activityEntity);
        }
        return newActivityList;
    }

    private void doCleanUp(RolesPermissionsActionForm form) {
        form.getActivities().clear();
        form.setName(null);
    }

}
