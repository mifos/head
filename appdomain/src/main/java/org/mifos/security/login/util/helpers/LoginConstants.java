/*
 * Copyright Grameen Foundation USA
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

package org.mifos.security.login.util.helpers;

public interface LoginConstants {

    String FIRSTLOGIN = "login_success_first";
    String REGULARLOGIN = "login_success_regular";
    String LOGINPAGE = "login_failure";
    String SETTINGSPAGE = "success_settings";
    String LOGIN = "login";
    String LOGOUT = "logout";
    // User Context for storing in session
    String USERCONTEXT = "UserContext";
    String ACTIVITYCONTEXT = "ActivityContext";
    String LOGINPASSCHANGE = "LoginChangePW";
    String SETTINGSPASSCHANE = "SettingsChangePW";
    String LOGOUTFORWARD = "/loginAction.do?method=logout";
    String LOGINPAGEURI = "/loginAction.do?method=load";
    Short FIRSTTIMEUSER = 0;
    Short PASSWORDCHANGEDFLAG = 1;
    Short MAXTRIES = 5;

    String LOGIN_PASSWORD_UPDATE_FAILURE = "login_update_failure";
    String SETTINGS_PASSWORD_UPDATE_FAILURE = "settings_update_failure";
    String LOGINACTION = "loginAction.do";

    String KEYUSERINACTIVE = "AbstractUserDetailsAuthenticationProvider.disabled";
    String KEYUSERLOCKED = "AbstractUserDetailsAuthenticationProvider.locked";
    String KEYINVALIDUSER = "AbstractUserDetailsAuthenticationProvider.badCredentials";

    String IllEGALSTATE = "errors.IllegalState";
    String SESSIONTIMEOUT = "errors.sessiontimeout";
    String INVALIDOLDPASSWORD = "errors.invalidoldpassword";
    String SAME_OLD_AND_NEW_PASSWORD = "errors.sameoldandnewpassword";
    String LOGOUTOUT = "errors.logoutout";
    String BATCH_JOB_RUNNING = "errors.batchjobrunning";
    String SHUTDOWN = "errors.shutdown";
    String METHODCALLED = "methodCalled";
    String LOGINACTIONFORM = "loginActionForm";

}
