package org.mifos.androiclient.utils;

import com.jayway.android.robotium.solo.Solo;

public class ConnectionHelper {

	public static void logIn(Solo solo, int menuIndex) {
		String login = "loanofficer";
		String password = "testmifos";
		solo.clickOnButton(menuIndex);
        
        solo.clickOnEditText(0);
        solo.enterText(0, login);
        solo.clickOnEditText(1);
        solo.enterText(1, password);
        solo.clickOnButton(0);
	}
	
	public static void  changeServer(Solo solo) {
		String server = "http://10.0.2.2:8083/mifos";
		
		solo.clearEditText(0);
		solo.enterText(0, server);
		solo.clickOnButton(0);
	}
	
	public static void logOut(Solo solo) {
        solo.waitForText("Log out");
        solo.clickOnMenuItem("Log out");
	}
}
