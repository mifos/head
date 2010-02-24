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

package org.mifos.security.rolesandpermission.util.helpers;

public interface RolesAndPermissionConstants {
    public final static String ROLE = "Role";

    public final static String ROLES = "Roles";

    public final static String ROLENAME = "RoleName";
    public final static String ROLEID = "roleId";
    public final static String BUFF = "BUFF";
    public final static String ROLEANDPERMISSIONDAO = "ManageRolesAndPermission";
    public final static String KEYROLEDELETIONFAILED = "roleandpermission.error.roleDeleteFailed";
    public final static String KEYROLETEMPLETELOADFAILED = "roleandpermission.error.loadTempleteFailed";
    public final static String KEYROLEALREADYEXIST = "roleandpermission.error.roleNameExist";
    public final static String KEYROLECREATIONFAILED = "roleandpermission.error.roleCreationFailed";
    public final static String KEYROLEWITHNOACTIVITIES = "roleandpermission.error.noActivitySelected";
    public final static String KEYROLEWITHNOACTIVITIESFORUPDATE = "roleandpermission.error.noActivitySelectedForUpdate";
    public final static String KEYROLEUPDATIONFAILED = "roleandpermission.error.roleUpdateFailed";
    public final static String KEYROLELOADINGFAILED = "roleandpermission.error.noRoleInSystem";
    public final static String KEYROLENOTEXIST = "roleandpermission.errorroleNotExist";
    public final static String ACTIVITYLIST = "ActivityList";
    public final static String SELECTED_ACTIVITIES = "SelectedActivities";
    public final static String ROLESUBOBJECT = "RoleSubObject";
    public final static String KEYROLEASSIGNEDTOPERSONNEL = "roleandpermission.error.roleassigned";
    public final static String KEYROLEDELETEDBYOTHERUSER = "roleandpermission.error.roledeleted";
    public final static String KEYROLENAMENOTSPECIFIED = "roleandpermission.rolename.required";

    /* Set up in latest-data.sql */
    public static final int ADMIN_ROLE = 1;

}
