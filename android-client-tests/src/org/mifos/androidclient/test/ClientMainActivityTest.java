package org.mifos.androidclient.test;

import org.mifos.androiclient.utils.ConnectionHelper;

import android.test.ActivityInstrumentationTestCase2;

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
	
	
	
	public void testCentersListActivity() throws Exception {
		ConnectionHelper.changeServer(solo);
		ConnectionHelper.logIn(solo,0);
        solo.waitForActivity(Class.forName("org.mifos.androidclient.main.CustomersListActivity").getName());
        solo.assertCurrentActivity("CentersListActivity", Class.forName("org.mifos.androidclient.main.CentersListActivity"));
        ConnectionHelper.logOut(solo);
	}
}
