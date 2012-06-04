package org.mifos.androiclient.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
	
	public static void  changeServer(Solo solo) throws IOException {
		String server = "http://10.0.2.2:"+ getPort() +"/mifos";
		
		solo.clearEditText(0);
		solo.enterText(0, server);
		solo.clickOnButton(0);
	}
	
	public static void logOut(Solo solo) {
        solo.waitForText("Log out");
        solo.clickOnMenuItem("Log out");
	}

	public static String getPort() throws IOException {
		File properties = new File("/system/port.properties");
		BufferedReader br = new BufferedReader(new FileReader(properties));
		return br.readLine();
	}
}
