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

package org.mifos.security.rolesandpermission.struts.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.struts.actionforms.RolesPermissionsActionForm;
import org.mifos.security.rolesandpermission.util.helpers.RoleTempleteBuilder;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class ActivityTag extends TagSupport {

    @Override
    public int doEndTag() throws JspException {
        UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USER_CONTEXT_KEY);
        RoleTempleteBuilder builder = new RoleTempleteBuilder();
        List<ActivityEntity> activities;
        try {
            activities = (List<ActivityEntity>) SessionUtils.getAttribute(RolesAndPermissionConstants.ACTIVITYLIST,
                    (HttpServletRequest) pageContext.getRequest());
            RoleBO role = (RoleBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, (HttpServletRequest) pageContext
                    .getRequest());
            RolesPermissionsActionForm rolesPermissionsActionForm = (RolesPermissionsActionForm) pageContext
                    .getSession().getAttribute("rolesPermissionsActionForm");
            if (rolesPermissionsActionForm != null && rolesPermissionsActionForm.getActivities().size() > 0) {
                Set activitySet = convertToIdSet(getActivities(activities, rolesPermissionsActionForm.getActivities()));
                builder.setCurrentActivites(activitySet);
            } else if (role != null) {
                Set activitySet = convertToIdSet(role.getActivities());
                builder.setCurrentActivites(activitySet);
            }
            SessionUtils.getAttribute(Constants.BUSINESS_KEY, (HttpServletRequest) pageContext.getRequest());
            StringBuilder sb = builder.getRolesTemplete(activities);
            pageContext.getOut().print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        } catch (PageExpiredException e1) {
            throw new JspException(e1);
        }
        return EVAL_PAGE;
    }

    static Set<Short> convertToIdSet(List<ActivityEntity> activityList) {
        Set<Short> activities = new HashSet<Short>();
        for (ActivityEntity activityEntity : activityList) {
            activities.add(activityEntity.getId());
        }
        return activities;
    }

    List<ActivityEntity> getActivities(List<ActivityEntity> activityList, Map<String, String> activities) {
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

}
