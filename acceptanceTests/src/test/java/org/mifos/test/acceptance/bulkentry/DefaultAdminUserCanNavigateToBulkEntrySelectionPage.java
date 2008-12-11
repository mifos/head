/**
 * 
 */
package org.mifos.test.acceptance.bulkentry;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.BulkEntrySelectPage;
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
	
	public void defaultAdminNavigateToBulkEntrySelection() {
		loginAndNavigateToBulkEntrySelectPage("mifos", "testmifos")
			.verifyPage();
	}
	
	public void defaultAdminUserSelectsValidBulkEntryParameters() {
		loginAndNavigateToBulkEntrySelectPage("mifos", "testmifos")
			.submitForm("Office1","Bagonza Wilson", "Center1", "", "", "", "Cash", "", "", "", "")
			.verifyPage ("Office1","Bagonza Wilson", "Center1", "", "", "", "Cash", "", "", "", "");
	}
	
	private BulkEntrySelectPage loginAndNavigateToBulkEntrySelectPage(String userName, String password) {
		return appLauncher
		 .launchMifos()
		 .loginSuccessfulAs(userName, password)
		 .navigateToClientsAndAccountsUsingHeaderTab()
		 .navigateToEnterCollectionSheetDataUsingLeftMenu();
	}
}
