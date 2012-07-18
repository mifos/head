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

package org.mifos.security.rolesandpermission.struts.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.dto.domain.ActivityRestrictionDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ServletUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.struts.actionforms.RolesPermissionsActionForm;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.UserContext;

public class RolesPermissionsAction extends BaseAction {

    private final RolesPermissionServiceFacade rolesPermissionServiceFacade = ApplicationContextProvider.getBean(RolesPermissionServiceFacade.class);
    @Override
    protected BusinessService getService() throws ServiceException {
        return ServiceFactory.getInstance().getBusinessService(BusinessServiceName.RolesPermissions);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward viewRoles(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        doCleanUp((RolesPermissionsActionForm) form);
        List<ListElement> roles = rolesPermissionServiceFacade.retrieveAllRoles();
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
        rolesPermissionServiceFacade.createRole(userContext.getId(), rolesPermissionsActionForm.getName(),
                getActivityIds(getActivities(activities,rolesPermissionsActionForm.getActivities())), rolesPermissionsActionForm.getActivityRestrictionDtoToPersistList());
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward manage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) form;
        SessionUtils.setCollectionAttribute(RolesAndPermissionConstants.ACTIVITYLIST,
                ((RolesPermissionsBusinessService) getService()).getActivities(), request);
        Short roleId = Short.valueOf(rolesPermissionsActionForm
                .getId());
        RoleBO role = ((RolesPermissionsBusinessService) getService()).getRole(roleId);
        List<ActivityRestrictionDto> activityRestrictionDtoList = rolesPermissionServiceFacade.getRoleActivitiesRestrictions(roleId);
        Map<Short, ActivityRestrictionDto> activityRestrictionDtoMap = new HashMap<Short, ActivityRestrictionDto>();
        for (ActivityRestrictionDto activityRestrictionDto : activityRestrictionDtoList){
            activityRestrictionDtoMap.put(activityRestrictionDto.getActivityRestrictionTypeId(), activityRestrictionDto);
        }
        rolesPermissionsActionForm.setActivityRestrictionDtoMap(activityRestrictionDtoMap);
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
        List<ActivityEntity> activities = (List<ActivityEntity>) SessionUtils.getAttribute(
                RolesAndPermissionConstants.ACTIVITYLIST, request);
        rolesPermissionServiceFacade.updateRole(Short.parseShort(rolesPermissionsActionForm.getId()), userContext.getId(),
                rolesPermissionsActionForm.getName(), getActivityIds(getActivities(activities, rolesPermissionsActionForm.getActivities())),
                rolesPermissionsActionForm.getActivityRestrictionDtoToPersistList());
        // MIFOS-3530: update all currently logged users
        for (String loggedUser : getLoggedUsers(request)) {
            this.authenticationAuthorizationServiceFacade.reloadUserDetailsForSecurityContext(loggedUser);
        }
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    private List<String> getLoggedUsers(HttpServletRequest request) {
        List<String> loggedUsers = new ArrayList<String>();
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
                .getName());
        Collection<HttpSession> sessions = shutdownManager.getActiveSessions();
        PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
        for (HttpSession session : sessions) {
            UserContext userContextFromSession = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);
            if (userContextFromSession == null) {
                continue;
            }
            PersonnelBO personnel;
            try {
                personnel = personnelBusinessService.getPersonnel(userContextFromSession.getId());
            } catch (ServiceException e) {
                continue;
            }
            loggedUsers.add(personnel.getUserName());

        }
        return loggedUsers;
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
        rolesPermissionServiceFacade.deleteRole(role.getVersionNo(), role.getId());
        role = null;
        return mapping.findForward(ActionForwards.delete_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }
    
    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        ActionForwards actionForward = ActionForwards.viewRoles_success;
        String method = (String) request.getAttribute("methodCalled");
        if (method != null) {
            if (Methods.update.toString().equals(method)) {
                actionForward = ActionForwards.manage_success;
            } else if (Methods.create.toString().equals(method)){
            	actionForward = ActionForwards.load_success;
            } else {
                actionForward = ActionForwards.valueOf(method+"_success");
            }
        }
        return mapping.findForward(actionForward.toString());
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
            if (ids.contains(activityEntity.getId())) {
                newActivityList.add(activityEntity);
            }
        }
        return newActivityList;
    }

    private List<Short> getActivityIds(List<ActivityEntity> activityList) {
        List<Short> activityIds = new ArrayList<Short>();
        for (ActivityEntity activityEntity : activityList) {
            activityIds.add(activityEntity.getId());
        }
        return activityIds;
    }

    private void doCleanUp(RolesPermissionsActionForm form) {
        form.getActivities().clear();
        form.resetActivityRestriction();
        form.setName(null);
        form.setId(null);
    }

}
