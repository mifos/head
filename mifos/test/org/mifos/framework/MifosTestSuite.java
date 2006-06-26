package org.mifos.framework;

import junit.framework.TestSuite;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;

public class MifosTestSuite extends TestSuite {

	static {
		try {
			MifosLogManager.configure(FilePaths.LOGFILE);
			HibernateStartUp
					.initialize("conf/HibernateTest.properties");
			FinancialInitializer.initialize();
			AuthorizationManager.getInstance().init();
			HierarchyManager.getInstance().init();
			MifosConfiguration.getInstance().init();
		} catch (Exception e) {
			throw new Error("Failed to start up",e);

		}
	}

	public MifosTestSuite() {
		super();

	}

}
