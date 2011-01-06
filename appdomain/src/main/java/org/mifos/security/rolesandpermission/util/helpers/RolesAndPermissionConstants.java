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

package org.mifos.security.rolesandpermission.util.helpers;

public interface RolesAndPermissionConstants {
    String ROLE = "Role";

    String ROLES = "Roles";

    String ROLENAME = "RoleName";
    String ROLEID = "roleId";
    String BUFF = "BUFF";
    String ROLEANDPERMISSIONDAO = "ManageRolesAndPermission";
    String KEYROLEDELETIONFAILED = "roleandpermission.error.roleDeleteFailed";
    String KEYROLETEMPLETELOADFAILED = "roleandpermission.error.loadTempleteFailed";
    String KEYROLEALREADYEXIST = "roleandpermission.error.roleNameExist";
    String KEYROLECREATIONFAILED = "roleandpermission.error.roleCreationFailed";
    String KEYROLEWITHNOACTIVITIES = "roleandpermission.error.noActivitySelected";
    String KEYROLEWITHNOACTIVITIESFORUPDATE = "roleandpermission.error.noActivitySelectedForUpdate";
    String KEYROLEUPDATIONFAILED = "roleandpermission.error.roleUpdateFailed";
    String KEYROLELOADINGFAILED = "roleandpermission.error.noRoleInSystem";
    String KEYROLENOTEXIST = "roleandpermission.errorroleNotExist";
    String ACTIVITYLIST = "ActivityList";
    String SELECTED_ACTIVITIES = "SelectedActivities";
    String ROLESUBOBJECT = "RoleSubObject";
    String KEYROLEASSIGNEDTOPERSONNEL = "roleandpermission.error.roleassigned";
    String KEYROLEDELETEDBYOTHERUSER = "roleandpermission.error.roledeleted";
    String KEYROLENAMENOTSPECIFIED = "roleandpermission.rolename.required";

    /* Set up in latest-data.sql */
    int ADMIN_ROLE = 1;

}
