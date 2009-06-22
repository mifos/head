package org.mifos.application.productsmix;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.productsmix.business.ProductMixBOIntegrationTest;
import org.mifos.application.productsmix.business.service.ProductMixBusinessServiceIntegrationTest;
import org.mifos.application.productsmix.persistence.ProductMixPersistenceIntegrationTest;
import org.mifos.application.productsmix.struts.action.ProductMixActionTest;

public class ProductMixTestSuite extends TestSuite{
	public ProductMixTestSuite() {
		super();
	}

	public static void main(String[] args) {
		try {
			Test testSuite = suite();
			TestRunner.run(testSuite);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new ProductMixTestSuite();
		testSuite.addTestSuite(ProductMixBOIntegrationTest.class);
		testSuite.addTestSuite(ProductMixBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(ProductMixPersistenceIntegrationTest.class);
		testSuite.addTestSuite(ProductMixActionTest.class);

		return testSuite;
	}
}
