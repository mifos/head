package org.mifos.androidclient.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.jayway.android.robotium.solo.Solo;

@SuppressWarnings("unchecked")
public class ClientMainActivityTest extends ActivityInstrumentationTestCase2 {

	private static final String LAUNCHER_ACTIVITY = "org.mifos.androidclient.main.ClientMainActivity";
	private static final String TARGET_PACKAGE_ID = "org.mifos.androidclient";
	private Solo solo;
	
	@SuppressWarnings("unchecked")
	public ClientMainActivityTest() throws ClassNotFoundException {
		super(TARGET_PACKAGE_ID, Class.forName(LAUNCHER_ACTIVITY));
	}
	
	@UiThreadTest
	@Override
	protected void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
	private static void logIn(Solo solo, int menuIndex) {
		String login = "mlo";
		String password = "m12345";
		solo.clickOnButton(menuIndex);
		solo.clickOnButton(menuIndex);
        
        solo.clickOnEditText(0);
        solo.enterText(0, login);
        solo.clickOnEditText(1);
        solo.enterText(1, password);
        solo.clickOnButton(0);
	}
	
	private static void logOut(Solo solo) {
        solo.waitForText("Log out");
        solo.clickOnMenuItem("Log out");
	}
	
	public void testCentersListActivity() throws Exception {
		logIn(solo,0);
        solo.waitForActivity(Class.forName("org.mifos.androidclient.main.CustomersListActivity").getName());
        solo.assertCurrentActivity("CentersListActivity", Class.forName("org.mifos.androidclient.main.CentersListActivity"));
        logOut(solo);
	}
}
