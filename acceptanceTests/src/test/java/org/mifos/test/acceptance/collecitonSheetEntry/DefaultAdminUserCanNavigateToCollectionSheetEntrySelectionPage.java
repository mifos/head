package org.mifos.test.acceptance.collecitonSheetEntry;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"acceptance","ui"})
public class DefaultAdminUserCanNavigateToCollectionSheetEntrySelectionPage extends
		UiTestCaseBase {

	private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
	
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
	
	@Test(enabled=true)
	public void defaultAdminNavigateToBulkEntrySelection() throws DatabaseUnitException, SQLException, IOException {
		loadDataFromFile("acceptance_small_001_dbunit.xml");
		loginAndNavigateToBulkEntrySelectPage("mifos", "testmifos")
			.verifyPage();
	}
	
	@Test(enabled=true)
	public void testLoadData() throws DatabaseUnitException, SQLException, IOException {
		loadDataFromFile("acceptance_small_001_dbunit.xml");
		Assert.assertTrue(true);
	}
	
	private void loadDataFromFile(String filename) throws DatabaseUnitException, SQLException, IOException {
		Connection jdbcConnection = null;
        boolean enableColumnSensing = true;
        URL url = DbUnitResource.getInstance().getUrl(filename);
        if (url == null) {
        	throw new RuntimeException("Couldn't find file:" + filename);
        }
        IDataSet dataSet = new FlatXmlDataSet(url,false,enableColumnSensing);
		try {
			jdbcConnection = DataSourceUtils.getConnection(dataSource);
			IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
			DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
		}
		finally {
			if (jdbcConnection != null) {
				jdbcConnection.close();
			}
			DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
		}
	}

	@Test(enabled=true)
	public void defaultAdminUserSelectsValidBulkEntryParameters() throws DatabaseUnitException, SQLException, IOException {
		loadDataFromFile("acceptance_small_001_dbunit.xml");
		loginAndNavigateToBulkEntrySelectPage("mifos", "testmifos")
			.submitForm("Office1","Bagonza Wilson", "Center1", "", "", "", "Cash", "", "", "", "")
			.verifyPage ("Office1","Bagonza Wilson", "Center1", "", "", "", "Cash", "", "", "", "");
	}

	
	private CollectionSheetEntrySelectPage loginAndNavigateToBulkEntrySelectPage(String userName, String password) {
		return appLauncher
		 .launchMifos()
		 .loginSuccessfulAs(userName, password)
		 .navigateToClientsAndAccountsUsingHeaderTab()
		 .navigateToEnterCollectionSheetDataUsingLeftMenu();
	}
}
