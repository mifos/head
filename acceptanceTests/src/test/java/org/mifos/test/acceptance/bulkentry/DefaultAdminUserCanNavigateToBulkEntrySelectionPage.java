/**
 * 
 */
package org.mifos.test.acceptance.bulkentry;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"userLoginStory","acceptance","ui"})
public class DefaultAdminUserCanNavigateToBulkEntrySelectionPage extends
		UiTestCaseBase {

	private AppLauncher appLauncher;
	
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
	@BeforeMethod
	public void setUp() throws Exception {
		super.setUp();
		appLauncher = new AppLauncher(selenium);
	}

	@AfterMethod
	public void logOut() {
		(new MifosPage(selenium)).logout();
	}
	
	public void navigateToBulkEntrySelection() {
		appLauncher
			.launchMifos()
			.loginSuccessfulAs("mifos", "testmifos")
			.navigateToClientsAndAccountsUsingHeaderTab()
			.navigateToEnterCollectionSheetDataUsingLeftMenu()
			.verifyPage();
	}
}
