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

package org.mifos.application.login.util.helpers;

public interface LoginConstants {

    public String FIRSTLOGIN = "login_success_first";
    public String REGULARLOGIN = "login_success_regular";
    public String LOGINPAGE = "login_failure";
    public String SETTINGSPAGE = "success_settings";
    public String LOGIN = "login";
    public String LOGOUT = "logout";
    // User Context for storing in session
    public String USERCONTEXT = "UserContext";
    public String ACTIVITYCONTEXT = "ActivityContext";
    public String LOGINPASSCHANGE = "LoginChangePW";
    public String SETTINGSPASSCHANE = "SettingsChangePW";
    public String LOGOUTFORWARD = "/loginAction.do?method=logout";
    public String LOGINPAGEURI = "/loginAction.do?method=load";
    public Short FIRSTTIMEUSER = 0;
    public Short PASSWORDCHANGEDFLAG = 1;
    public final Short MAXTRIES = 5;

    public static final String LOGIN_PASSWORD_UPDATE_FAILURE = "login_update_failure";
    public static final String SETTINGS_PASSWORD_UPDATE_FAILURE = "settings_update_failure";
    public static final String LOGINACTION = "loginAction.do";

    public static final String KEYUSERINACTIVE = "errors.inactiveuser";
    public static final String KEYUSERLOCKED = "errors.lockeduser";
    public static final String KEYINVALIDUSER = "errors.invaliduser";

    public static final String IllEGALSTATE = "errors.IllegalState";
    public static final String SESSIONTIMEOUT = "errors.sessiontimeout";
    public static final String INVALIDOLDPASSWORD = "errors.invalidoldpassword";
    public static final String SAME_OLD_AND_NEW_PASSWORD = "errors.sameoldandnewpassword";
    public static final String LOGOUTOUT = "errors.logoutout";
    public static final String BATCH_JOB_RUNNING = "errors.batchjobrunning";
    public String METHODCALLED = "methodCalled";
    public String LOGINACTIONFORM = "loginActionForm";

}
