/**
 * 
 */
package org.mifos.application.login.util.helpers;

public interface LoginConstants {
	
	public String FIRSTLOGIN="login_success_first";
	public String REGULARLOGIN="login_success_regular";
	public String LOGINPAGE="login_failure";
	public String SETTINGSPAGE="success_settings";
	public String LOGIN = "login";
	public String LOGOUT = "logout";
	//User Context for storing in session
	public String USERCONTEXT="UserContext";
	public String ACTIVITYCONTEXT="ActivityContext";
	public String LOGINPASSCHANGE="LoginChangePW";
	public String SETTINGSPASSCHANE="SettingsChangePW";
	public String LOGOUTFORWARD="/loginAction.do?method=logout";
	public String LOGINPAGEURI="/loginAction.do?method=load";
	public Short FIRSTTIMEUSER=0;
	public Short PASSWORDCHANGEDFLAG=1;
	public final Short MAXTRIES = 5;
	
	public static final String LOGIN_PASSWORD_UPDATE_FAILURE="login_update_failure";
	public static final String SETTINGS_PASSWORD_UPDATE_FAILURE="settings_update_failure";
	public static final String  LOGINACTION="loginAction.do";
	
	public static final String  KEYUSERINACTIVE="errors.inactiveuser";	
	public static final String  KEYUSERLOCKED="errors.lockeduser";	
	public static final String  KEYINVALIDUSER="errors.invaliduser";	
	
	public static final String IllEGALSTATE ="errors.IllegalState";	
	public static final String SESSIONTIMEOUT="errors.sessiontimeout";
	public static final String INVALIDOLDPASSWORD="errors.invalidoldpassword";
	public static final String LOGOUTOUT="errors.logoutout";
	public static final String CRON_JOB_RUNNING="errors.cronjobrunning";
	public String METHODCALLED="methodCalled";
	public String LOGINACTIONFORM="loginActionForm";
	
}

